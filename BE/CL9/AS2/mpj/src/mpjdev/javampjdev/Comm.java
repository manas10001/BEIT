/*
 The MIT License
 
 Copyright (c) 2005 - 2008
 1. Distributed Systems Group, University of Portsmouth (2005)
 2. Aamir Shafi (2005 - 2008)
 3. Bryan Carpenter (2005 - 2008)
 4. Mark Baker (2005 - 2008)
 
 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:
 
 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : Comm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 BST 2004
 * Revision     : $Revision: 1.17 $
 * Updated      : $Date: 2005/12/21 13:18:17 $
 *
 */

package mpjdev.javampjdev;

import xdev.Device;
import mpjbuf.Buffer;
import mpjbuf.Type ;
import mpjbuf.NIOBuffer ;
import mpjbuf.BufferFactory ;
import xdev.ProcessID;
import java.util.UUID;
import java.util.Random;

import mpi.MPI;
import mpjdev.*;

public class Comm extends mpjdev.Comm {
    
    Device device = null;
    /**
     public int sendctxt = 0;
     public int recvctxt = 0;
     int collctxt = 0;
     public Group group = null;
     public Group localgroup = null;
     */
    
    int calContextTag    = (34*1000)+20 ;
    int mpjdevBarrierTag = (34*1000)+21 ;
    int splitTag         = (34*1000)+22 ;
    int bCount           = 1000;
    static int staticContext = 2;
    
    /**
     * constructor used for MPJDev.WORLD intracomm.
     * how will coll. operations use different contexts ?
     */
    Comm(xdev.Device device, mpjdev.Group group) throws MPJDevException {
        this.device = device;
        this.group = group;
        int highestContext = -1;
        int nextContext = 0;
        
        synchronized(Comm.class) {
            nextContext = ++staticContext ;
        }
        
        try {
            highestContext = calculateContext(10, false, nextContext);
        }
        catch (Exception e) {
            throw new MPJDevException ( e );
        }
        
        sendctxt = highestContext;
        recvctxt = highestContext;
        collctxt = highestContext + (2 * highestContext);
        //collctxt = highestContext + (2 * highestContext);
    }
    
    /**
     * This method creates intercommunicator. Actually, the code may
     * suggest that this is a very complex function, but the truth is,
     * i don't have broadcast, reduce like functions at mpjdev level, which
     * makes this method look difficult ..the implementation. Anyway,
     *
     * There is a local leader, and a remote leader. There's a concept of
     * peer comm for creating intercommunicator. The peer comm is actually
     * the communicator, that allows the two leaders to communicate
     * their groups information with each other. The local leader,
     * sends the ranks of all of its members (these ranks are w.r.t peer comm)
     * to remote leader. The local leader then receives the ranks of all
     * processes from remote-leader. These ranks are used by local leader
     * to find out ProcessID, and thus create a remotegroup object that would
     * represent the remote group. This remote group is required because in
     * all communications using intercomm, the user will use the ranks
     * of processes in remote groups. So once localleader, and remote leader
     * have exchanged information, and created group objects representing
     * remote groups, they need to broadcast this 'set of ranks' to all
     * processes within their intracommunicator. Once this info. is broadcasted
     * , then all processes create group objects. Once this group creation
     * stufff is done, now its time to agree on contexts ...
     *
     * Both the intracommunictors (of which localleader, and remoteleader are
     * members) broadcast in order to calculate context, and once each group
     * has agreed on the contexts, localleaders and remoteleaders share
     * this information. If this context is not same, then intercomm is created,
     * if they are same, then i do some plus/minuses to agree on something
     * that is unique ..you may see this in the code ....i don't know how
     * to explain it in words.
     */
    
    //if (peergroup.ids[localleader].uuid().equals(
    //    peergroup.myID.uuid())) {
    Comm(xdev.Device device, mpjdev.Group localgroup, mpjdev.Group peergroup,
         int localleader, int remoteleader, int tag,
         int peercontext, int context) throws MPJDevException {
        
        mpjdev.Request req = null; // added by me
        int value = 5 ;
        int me = peergroup.rank();
        //System.out.println("--- intercomm constructor ---"+me );
        //System.out.println(" peercontext (comm_world) "+peercontext);
        //System.out.println(" context (local_comm)"+context);
        this.device = device;
        this.localgroup = localgroup;
        int sendOverhead = MPJDev.getSendOverhead() ;
        int recvOverhead = MPJDev.getRecvOverhead() ;
        int capacity = sendOverhead + 23;
        
        Buffer sbuf = new Buffer(BufferFactory.create(capacity),
                                 sendOverhead ,capacity  );
        Buffer rbuf = new Buffer(BufferFactory.create(capacity),
                                 
                                 recvOverhead , 16+recvOverhead );
        int[] intdata = new int[1];
        
        if (localgroup.ids[localleader].uuid().equals(
                                                      localgroup.myID.uuid())) {
            
            //System.out.println("local leader ..."+me);
            intdata[0] = localgroup.size();
            //System.out.println(me +"sending size of localgrp to remoteleader "
            //                   +intdata[0]);
            try {
                sbuf.putSectionHeader(Type.INT);
                sbuf.write(intdata, 0, 1);
                sbuf.commit();
               // System.out.println("sending ..") ;
                req = device.isend(sbuf, peergroup.ids[remoteleader],
                                   tag, peercontext); // added by me
            } catch(Exception e ) {
                throw new MPJDevException ( e );
            }
            
            mpjdev.Request []localgroupreq = new mpjdev.Request[localgroup.size()] ;
            mpjbuf.Buffer wBuffer [] = new mpjbuf.Buffer[localgroup.size()] ;
            
            
            for (int i = 0; i < localgroup.size(); i++) {
                
                int prank = 0;
                
                for (int j = 0; j < peergroup.size(); j++) {
                    
                    if (localgroup.ids[i].uuid().equals(
                                                        peergroup.ids[j].uuid())) {
                        prank = j; //peergroup.ids[j].rank();
                    }
                }
                
                //System.out.println("sending the rank(WORLD) of process"+me
                //	+" to remote leader"+prank);
                intdata[0] = prank;
                
                try {
                    wBuffer[i] = new mpjbuf.Buffer(
                                                   BufferFactory.create(capacity), sendOverhead, capacity);
                    
                    wBuffer[i].putSectionHeader(Type.INT);
                    wBuffer[i].write(intdata, 0, 1);
                    wBuffer[i].commit();
                    // System.out.println("sending ..") ;
                    localgroupreq[i]= device.isend(wBuffer[i],
                                                   peergroup.ids[remoteleader],
                                                   tag, peercontext); // added by me
                }
                catch(Exception e ){
                    throw new MPJDevException( e );
                }
            }
            
            
            //System.out.println("now the local leader is receiving ..."+me );
            device.recv(rbuf, peergroup.ids[remoteleader],
                        tag, peercontext);
            
            //System.out.println(me + "now the local leader has received ..."
            //           +peercontext );
            
            try {
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(intdata, 0, 1); // commented by me
                rbuf.clear();
            }
            catch(Exception e ) {
                System.out.println(me +"has failed ..") ;
                throw new MPJDevException( e );
            }
            
            req.iwait(); //Kamran: Completing isend of localleader to remote
            //leader
            
            
            try {
                sbuf.clear() ;
            } catch(Exception e ) {
                throw new MPJDevException( e );
            }
            
            int rgroupsize = intdata[0];
            ProcessID[] rids = new ProcessID[rgroupsize];
            int[] rranks = new int[rgroupsize];
            //System.out.println("local leader size "+rgroupsize);
            
            for (int i = 0; i < rgroupsize; i++) {
                try {
                    device.recv(rbuf, peergroup.ids[remoteleader],
                                tag, peercontext);
                    rbuf.commit();
                    rbuf.getSectionHeader();
                    rbuf.getSectionSize() ;
                    rbuf.read(intdata, 0, 1);
                    rbuf.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
                rranks[i] = intdata[0];
                rids[i] = new ProcessID(peergroup.ids[rranks[i]].uuid()); //, -1);
                
            }
            
            for (int i = 0; i < localgroup.size(); i++) {
                localgroupreq[i].iwait() ;
                try {
                    wBuffer[i].clear() ;
                } catch(Exception e ) {
                    throw new MPJDevException( e );
                }
                BufferFactory.destroy(wBuffer[i].getStaticBuffer()) ;
            }
            
            // done by Aamir //
            
            this.group = peergroup.incl(rranks); //check ?
            
            for (int i = 0; i < localgroup.size(); i++) {
                
                /* dont send recv to myself */
                if (localgroup.ids[localleader].uuid().equals(
                                                              localgroup.ids[i].uuid())) {
                    continue;
                }
                
                intdata[0] = rgroupsize;
                try {
                    sbuf.putSectionHeader(Type.INT);
                    sbuf.write(intdata, 0, 1);
                    sbuf.commit();
                    //System.out.println(me +"localleader sending group size to process"+i);
                    device.send(sbuf, localgroup.ids[i],
                                tag, context);
                    sbuf.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
                
                for (int j = 0; j < rgroupsize; j++) {
                    
                    intdata[0] = rranks[j];
                    try {
                        sbuf.putSectionHeader(Type.INT);
                        sbuf.write(intdata, 0, 1);
                        sbuf.commit();
                        device.send(sbuf, localgroup.ids[i],
                                    tag, context);
                        sbuf.clear();
                    }
                    catch(Exception e) {
                        throw new MPJDevException( e );
                    }
                }
            }
        }
        
        else {
            
            //System.out.println("not localleader"+ me);
            try {
                device.recv(rbuf, localgroup.ids[localleader],
                            tag, context);
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(intdata, 0, 1);
                rbuf.clear();
            }
            catch(Exception e) {
                throw new MPJDevException ( e );
            }
            int rgroupsize = intdata[0];
            //System.out.println(me +"received group size from local leader "
            //	 	      +rgroupsize);
            ProcessID[] rids = new ProcessID[rgroupsize];
            int[] rranks = new int[rgroupsize];
            
            for (int i = 0; i < rgroupsize; i++) {
                
                //device.recv(rbuf, peergroup.ids[localleader],
                try {
                    device.recv(rbuf, localgroup.ids[localleader],
                                tag, context);
                    rbuf.commit();
                    rbuf.getSectionHeader();
                    rbuf.getSectionSize() ;
                    rbuf.read(intdata, 0, 1);
                    rbuf.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
                rranks[i] = intdata[0];
                //System.out.println("received world rank of process"+i
                //		+"which is,"+rranks[i]);
                rids[i] = new ProcessID(peergroup.ids[rranks[i]].uuid()); //, -1);
                
            }
            
            this.group = peergroup.incl(rranks); //check?
            //System.out.println("built non-local leader which is, "+group);
        }
        
        //System.out.println(" calling calculateContext"+me);
        int nextContext = 0 ;
        synchronized(Comm.class) {
            nextContext = (staticContext+2) ;
        }
        int hcontext = calculateContext(context, true, nextContext);
        //System.out.println(" called calculateContext"+me);
        
        //if (peergroup.ids[localleader].uuid().equals(
        //    peergroup.myID.uuid()))
        if (localgroup.ids[localleader].uuid().equals(
                                                      localgroup.myID.uuid())) {
            
            intdata[0] = hcontext;
            
            try {
                sbuf.putSectionHeader(Type.INT);
                sbuf.write(intdata, 0, 1);
                sbuf.commit();
                req = device.isend(sbuf, peergroup.ids[remoteleader],
                                   tag, peercontext);
                
                
                device.recv(rbuf, peergroup.ids[remoteleader],
                            tag, peercontext);
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(intdata, 0, 1);
                rbuf.clear();
                
                req.iwait() ;
                sbuf.clear();
            }
            catch(Exception e) {
                throw new MPJDevException( e );
            }
            
            int rhcontext = intdata[0];
            
            if (hcontext == rhcontext) {
                // if both group's highest context is same, then we need some sort
                // of conflict resolution approahc. The way current i am doing it is
                // by passing remoteleaders rank to each other. These are ranks in
                // some communicator like WORLD ..and cannot be equal ...
                int peersRemoteLeader = 0 ;
                try {
                    sbuf.putSectionHeader(Type.INT);
                    intdata[0] = remoteleader ;
                    sbuf.write(intdata, 0, 1);
                    sbuf.commit();
                    req = device.isend(sbuf, peergroup.ids[remoteleader],
                                       tag, peercontext);
                    
                    
                    device.recv(rbuf, peergroup.ids[remoteleader],
                                tag, peercontext);
                    rbuf.commit();
                    rbuf.getSectionHeader();
                    rbuf.getSectionSize() ;
                    rbuf.read(intdata, 0, 1);
                    rbuf.clear();
                    
                    req.iwait() ;
                    sbuf.clear();
                    peersRemoteLeader = intdata[0] ;
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
                
                /* get on with it ...doesnt matter */
                //sendctxt = hcontext;
                //recvctxt = rhcontext;
                
                if (peersRemoteLeader > remoteleader) {
                    //System.out.println(" localleader > remoteleader ");
                    sendctxt = hcontext   ; //+1; // localleader;
                    recvctxt = hcontext -1; // localleader;
                }
                else {
                    //System.out.println(" remoteleader > localleader ");
                    sendctxt = hcontext-1 ; //-1 ;//localleader; //remoteleader;
                    recvctxt = hcontext   ; //1 localleader; //remoteleader;
                }
                
            }
            else {
                sendctxt = hcontext;
                recvctxt = rhcontext;
            }
            
            //System.out.println("context selected (leader)"+sendctxt);
            //System.out.println("context selected (leader)"+recvctxt);
            
            for (int i = 0; i < localgroup.size(); i++) {
                /* dont send recv to myself */
                //if (peergroup.ids[localleader].uuid().equals(
                //			localgroup.ids[i].uuid()))
                if (localgroup.ids[localleader].uuid().equals(
                                                              localgroup.ids[i].uuid())) {
                    continue;
                }
                intdata[0] = sendctxt;
                try {
                    sbuf.putSectionHeader(Type.INT);
                    sbuf.write(intdata, 0, 1);
                    sbuf.commit();
                    //System.out.println("localleader sending contexts to process "+i);
                    device.send(sbuf, localgroup.ids[i],
                                tag, context);
                    sbuf.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
                intdata[0] = recvctxt;
                try {
                    sbuf.putSectionHeader(Type.INT);
                    sbuf.write(intdata, 0, 1);
                    sbuf.commit();
                    //System.out.println("localleader sending contexts to process "+i);
                    device.send(sbuf, localgroup.ids[i],
                                tag, context);
                    sbuf.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException ( e) ;
                }
                
            }
            
        }
        else {
            
            //device.recv(rbuf, peergroup.ids[localleader],
            try {
                device.recv(rbuf, localgroup.ids[localleader],
                            tag, context);
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(intdata, 0, 1);
                
                rbuf.clear();
                sendctxt = intdata[0];
                
                //device.recv(rbuf, peergroup.ids[localleader],
                device.recv(rbuf, localgroup.ids[localleader],
                            tag, context);
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(intdata, 0, 1);
                rbuf.clear();
            }
            catch(Exception e) {
                throw new MPJDevException ( e );
            }
            
            recvctxt = intdata[0];
            
            //System.out.println("context selected (non-leader)"+sendctxt);
            //System.out.println("context selected (non-leader)"+recvctxt);
        }
        
        collctxt = -1; // intercomms dont need one.
        //System.out.println(" finished "+me);
        //System.out.println(" sendctxt "+sendctxt);
        //System.out.println(" recvctxt "+recvctxt);
        BufferFactory.destroy(rbuf.getStaticBuffer()) ;
        BufferFactory.destroy(sbuf.getStaticBuffer()) ;
        
        //for(i =0 ; i<localgroup.size() ;i++) {
        //  BufferFactory.destroy(wBuffer[i].getStaticBuffer());
        //}
        
    }
    
    /**
     * constructor used for making intracomm, other than MPJDev.WORLD
     */
    Comm(xdev.Device device, mpjdev.Group group, int context) {
        this.device = device;
        this.group = group;
        int highestContext = -1;
        int nextContext = 0 ;
        
        synchronized(Comm.class) {
            nextContext = ++staticContext ;
        }
        
        try {
            highestContext = calculateContext(context, false, nextContext);
        }
        catch (Exception e) {
            throw new MPJDevException (e);
        }
        
        sendctxt = highestContext;
        collctxt = highestContext + (2 * highestContext);
        //collctxt = highestContext + (2 * highestContext);
        recvctxt = highestContext;
    }
    
    /**
     * constructor used for making intercomm
     * group is the remote group ...this will be used for determining ranks
     * for the send and recv processes (becayuse this is intercomm)
     * localgroup is localgroup ..and this is used for nothing (it seems)
     * the contexts are named as sendcontext, and recvcontext ...and are
     * used as such ....
     * Do i still need this method?
     */
    Comm(xdev.Device device, mpjdev.Group group, mpjdev.Group localgroup, int context) {
        this.device = device;
        this.group = group;
        //this.localgroup = localgroup;
        //calculate context ...
    }
    

    /*
     * This private method calculates the context of communications for this
     * intracommunicator. This method will calculate context for intracomm
     * and calculating intercomm can be tricky. and i don't know at the
     * moment how that will be done.
     */
    private int calculateContext(int context, boolean isIComm,
                                 int nextContext)
    throws MPJDevException {
        
        int myRank = -1, mySize = -1;
        ProcessID[] ids = null;
        
        if (isIComm) {
            myRank = this.localgroup.rank();
            mySize = this.localgroup.size();
            ids = this.localgroup.ids;
        }
        else {
            myRank = this.group.rank();
            mySize = this.group.size();
            ids = this.group.ids;
        }
        
        //System.out.println("rank<"+myRank+"> starting to send");
        int[] contextArray = new int[1];
        contextArray[0] = nextContext ; //(new Random()).nextInt(1024);
        int i;
        mpjdev.Request[] req = new mpjdev.Request[mySize];
        int sendOverhead = MPJDev.getSendOverhead () ;
        int recvOverhead = MPJDev.getRecvOverhead () ;
        int cap = sendOverhead+23; //FIXME: What's this magic number?
        
        mpjbuf.Buffer wBuffer [] = new mpjbuf.Buffer[mySize] ;
        
        for (i = 0; i < mySize; i++) {
            if (i == myRank) {
                continue;
            }
            //System.out.println("process <"+group.rank()+"> sending to <"+i );
            try {
                wBuffer[i] = new mpjbuf.Buffer(
                                               BufferFactory.create(cap), sendOverhead, cap);
                wBuffer[i].putSectionHeader(mpjbuf.Type.INT);
                wBuffer[i].write(contextArray, 0, 1);
                wBuffer[i].commit();
                req[i] = device.isend(wBuffer[i], ids[i], (calContextTag+i), context);
            }
            catch(Exception e) {
                throw new MPJDevException( e );
            }
            //System.out.println("process <"+group.rank()+"> sent to "+i );
        }
        
        mpjbuf.Buffer rBuffer = new mpjbuf.Buffer(
                                                  BufferFactory.create(recvOverhead+16),
                                                  recvOverhead , recvOverhead+16);
        //FIXME: What's this magic number?
        int highestContext = contextArray[0];
        
        for (i = 0; i < mySize; i++) {
            if (i == myRank) {
                continue;
            }
            
            try {
                //System.out.println("rank<"+myRank+"> recving from"+i);
                device.recv(rBuffer, ids[i], (calContextTag+myRank), context);
                //System.out.println("rank<"+myRank+"> recved from"+i);
                rBuffer.commit();
                Type type = rBuffer.getSectionHeader();
                rBuffer.getSectionSize() ;
                rBuffer.read(contextArray, 0, 1);
                rBuffer.clear();
            }
            catch (Exception e) {
                throw new MPJDevException ( e );
            }
            
            if (contextArray[0] > highestContext) {
                highestContext = contextArray[0];
            }
            
        }
        
        //System.out.println("rank<"+myRank+"> recv completed ");
        //System.out.println("rank<"+myRank+"> is last loop");
        
        for (i = 0; i < mySize; i++) {
            if (i == myRank) {
                continue;
            }
            req[i].iwait();
            try {
                wBuffer[i].clear();
            } catch(Exception e) {
                e.printStackTrace() ;
            }
        }
        
        for(i =0 ; i<mySize ;i++) {
            if (i == myRank)
                continue;
            BufferFactory.destroy(wBuffer[i].getStaticBuffer());
        }
        
        BufferFactory.destroy(rBuffer.getStaticBuffer());
        return highestContext;
    }
    
    /**
     * this method will create intracommunicators. One way is to
     * use create(grp), or get ids from grp at MPJ level and
     * give the argument. contexts will be managed in this device and
     * intercomms will be created by create(grp1,grp2) or whatever
     * it may require.
     */
    public mpjdev.javampjdev.Comm create(int[] ids) throws MPJDevException {
        mpjdev.Group tmpgrp = this.group.incl(ids);
        // will be replaced by MPI.GROUP_EMPTY
        return ( (tmpgrp != null) ? (create(tmpgrp)) : (null));
    }
    
    /**
     * this method is used to create intra-communicators,
     * not inter-communicators.
     */
    public mpjdev.javampjdev.Comm create(mpjdev.Group ngroup) throws MPJDevException {
        
        if (ngroup.rank() == -1)
            return null; //this is basically COMM_GROUP_EMPTY ...
        
        //System.out.println("ngroup.size() "+ngroup.size);
        //System.out.println("ngroup.rank() "+ngroup.rank);
        //System.out.println(" collctxt "+collctxt);
        return new mpjdev.javampjdev.Comm(this.device, ngroup, collctxt);
    }
    
    public mpjdev.javampjdev.Comm create(mpjdev.Comm localcomm, mpjdev.Group peergroup,
                                   int localleader, int remoteleader,
                                   int tag) throws MPJDevException {
        return new mpjdev.javampjdev.Comm(this.device, localcomm.group, peergroup, localleader,
                                    remoteleader, tag, sendctxt, localcomm.collctxt);
    }
    
    public Comm clone() {
        return null;
    }
    
    public mpjdev.Status probe(int src, int tag) throws MPJDevException {
        
        if (src < 0 && src != -2) {
            throw new MPJDevException(
                                      "In Comm.iprobe(), requested negative message destination: "
                                      + src);
        }
        else if (src >= this.size() && src != -2) {
            throw new MPJDevException("In Comm.iprobe(), requested source " + src +
                                      " does not exist in communicator of size " +
                                      this.size());
        }
        
        ProcessID srcID = null;
        
        if (src == MPI.ANY_SOURCE) {
            srcID = xdev.Device.ANY_SRC;
        }
        else {
            srcID = group.ids[src];
        }
        
        mpjdev.Status status = device.probe(srcID, tag, recvctxt);
        /* This loop is trying to find the matching receive */
        for (int j = 0; j < group.ids.length; j++) {
            if (group.ids[j].uuid().equals(status.srcID)) {
                status.source = j;
                break;
            }
        }
        return status;
    }
    
    public mpjdev.Status iprobe(int src, int tag) throws MPJDevException {
        
        if (src < 0 && src != -2) {
            throw new MPJDevException(
                                      "In Comm.iprobe(), requested negative message destination: "
                                      + src);
        }
        else if (src >= this.size() && src != -2) {
            throw new MPJDevException("In Comm.iprobe(), requested source " + src +
                                      " does not exist in communicator of size " +
                                      this.size());
        }
        
        ProcessID srcID = null;
        
        if (src == MPI.ANY_SOURCE) {
            srcID = xdev.Device.ANY_SRC;
        }
        else {
            srcID = group.ids[src];
        }
        mpjdev.Status status = device.iprobe(srcID, tag, recvctxt);
        
        if(status != null) {
            /* This loop is trying to find the matching receive */
            for (int j = 0; j < group.ids.length; j++) {
                if (group.ids[j].uuid().equals(status.srcID)) {
                    status.source = j;
                    break;
                }
            }
        }
        return status;
    }
    
    public mpjdev.Request irecv(mpjbuf.Buffer buf, int src, int tag,
                                mpjdev.Status status, boolean pt2pt)
    throws MPJDevException {
        
        if (buf == null) {
            throw new MPJDevException("In Comm.irecv(), buffer is null.");
        }
        
        if (src < 0 && src != -2) {
            throw new MPJDevException(
                                      "In Comm.irecv(), requested negative message destination: "
                                      + src);
        }
        else if (src >= this.size() && src != -2) {
            throw new MPJDevException("In Comm.irecv(), requested source " + src +
                                      " does not exist in communicator of size " +
                                      this.size());
        }
        
        int context = 0;
        
        if (pt2pt) {
            context = recvctxt;
        }
        else {
            context = collctxt;
        }
        
        ProcessID srcID = null;
        
        if (src == MPI.ANY_SOURCE) {
            srcID = xdev.Device.ANY_SRC;
        }
        else {
            srcID = group.ids[src];
        }
        mpjdev.Request request = device.irecv(buf, srcID, tag, context, status);
        
        request.addCompletionHandler(new mpjdev.CompletionHandler() {
            public void handleCompletion(mpjdev.Status status) {
                /* This loop is trying to find the matching receive */
                for (int j = 0; j < group.ids.length; j++) {
                    if (group.ids[j].uuid().equals(status.srcID)) {
                        status.source = j;
                        break;
                    }
                }
            }
        });
        
        return request;
    }
    
    public mpjdev.Status recv(mpjbuf.Buffer buf, int src, int tag, boolean pt2pt)
    throws MPJDevException {
        
        if (buf == null) {
            throw new MPJDevException("In Comm.irecv(), buffer is null.");
        }
        
        if (src < 0 && src != -2) {
            throw new MPJDevException(
                                      "In Comm.irecv(), requested negative message destination: "
                                      + src);
        }
        
        else if (src >= this.size() && src != -2) {
            throw new MPJDevException("In Comm.irecv(), requested source " + src +
                                      " does not exist in communicator of size " +
                                      this.size());
        }
        
        int context = 0;
        mpjdev.Status status = null;
        
        if (pt2pt) {
            context = recvctxt;
        }
        else {
            context = collctxt;
        }
        
        ProcessID srcID = null;
        
        if (src == MPI.ANY_SOURCE) {
            srcID = xdev.Device.ANY_SRC;
        }
        else {
            srcID = group.ids[src];
        }
        
        status = device.recv(buf, srcID, tag, context);
        
        /* This loop is trying to find the matching receive */
        for (int j = 0; j < group.ids.length; j++) {
            if (group.ids[j].uuid().equals(status.srcID)) {
                status.source = j;
                break;
            }
        }
        
        return status;
    }
    
    public void barrier() throws MPJDevException {
        
        if (this.localgroup != null) {
            System.out.println("mpjdev_barrier cannot be called for intercomm");
            return;
        }
        
        bCount++;
        int size = this.group.size();
        int rank = this.group.rank();
        int stuff[] = new int[1];
        int x = 1;
        int y = (int) Math.pow(2d, Math.floor(Math.log(size) / Math.log(2)));
        int sendOverhead = MPJDev.getSendOverhead() ;
        int cap = sendOverhead + 23;
        mpjbuf.Buffer wBuffer = new mpjbuf.Buffer(BufferFactory.create(cap),
                                                  sendOverhead, cap);
        mpjbuf.Buffer rBuffer = new mpjbuf.Buffer(BufferFactory.create(16),
                                                  0, 16);
        try {
            wBuffer.putSectionHeader(mpjbuf.Type.INT);
            wBuffer.write(stuff, 0, 1);
            wBuffer.commit();
        }catch(Exception e) {
            throw new MPJDevException( e );
        }
        
        if (rank >= y) {
            //send(rank-y,(-176+rank-y+bCount));
            try {
                send(wBuffer, rank-y, ( ( mpjdevBarrierTag+rank-y) * bCount),
                     false);
                recv(rBuffer, rank-y, ( ( mpjdevBarrierTag+rank) * bCount), false);
                rBuffer.clear();
            }
            catch(Exception e) {
                throw new MPJDevException ( e );
            }
        }
        else {
            if ( (size - y) > rank) {
                try {
                    recv(rBuffer, rank + y, ((mpjdevBarrierTag+rank) * bCount), false);
                    rBuffer.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e );
                }
            }
            
            int round = -1;
            int peer = 0;
            
            do {
                round = round + 1;
                peer = rank ^ (int) Math.pow(2d, round);
                try {
                    send(wBuffer, peer, ( (mpjdevBarrierTag+peer) * bCount), false);
                    recv(rBuffer, peer, ( (mpjdevBarrierTag+rank) * bCount), false);
                    rBuffer.clear();
                }
                catch(Exception e) {
                    throw new MPJDevException( e ); 		
                }
            }
            while (round != ( (int) (Math.log(y) / Math.log(2)) - 1));
            
            if ( (size - y) > rank) {
                //Send(stuff,0,1,MPI.INT,rank+y,(-176+rank+y+bCount));
                try { 
                    send(wBuffer, rank + y, 
                         ((mpjdevBarrierTag+rank+y) * bCount), false);
                }
                catch(Exception e) {
                    throw new MPJDevException( e );		
                }
            }
        }
        
        if (bCount == Integer.MAX_VALUE - 1) {
            bCount = 1000;
        }
        
        BufferFactory.destroy(wBuffer.getStaticBuffer()) ;
        BufferFactory.destroy(rBuffer.getStaticBuffer()) ;
        
    }
    
    public mpjdev.Request isend(mpjbuf.Buffer buf, int dest, int tag,
                                boolean pt2pt) throws MPJDevException {
        
        if (buf == null) {
            throw new MPJDevException("In Comm.isend(), buffer is null.");
        }
        
        if (dest < 0) {
            throw new MPJDevException(
                                      "In Comm.isend(), requested negative message destination: " + dest);
        }
        else if (dest >= size()) {
            throw new MPJDevException("In Comm.isend(), requested destination "
                                      + dest +
                                      " does not exist in communicator of size " + size());
        }
        
        int context = 0;
        
        if (pt2pt) {
            context = sendctxt;
        }
        else {
            context = collctxt;
        }
        //is status important here?  .. .. .. 
        return device.isend(buf, group.ids[dest], tag, context);
        
    }
    
    public void send(mpjbuf.Buffer buf, int dest, int tag, boolean pt2pt) 
    throws MPJDevException {
        
        if (buf == null) {
            throw new MPJDevException("In Comm.isend(), buffer is null.");
        }
        
        if (dest < 0) {
            throw new MPJDevException(
                                      "In Comm.isend(), requested negative message destination: " + dest);
        }
        else if (dest >= size()) {
            throw new MPJDevException("In Comm.isend(), requested destination "
                                      + dest +
                                      " does not exist in communicator of size " + size());
        }
        
        int context = 0;
        //System.out.println(" sendctxt (send) mpjdev "+sendctxt);
        //System.out.println(" recvctxt (send) mpjdev "+recvctxt);
        
        if (pt2pt) {
            context = sendctxt;
        }
        else {
            context = collctxt;
        } 
        
        device.send(buf, group.ids[dest], tag, context);
    }
    
    public mpjdev.Request issend(mpjbuf.Buffer buf, int dest, int tag,
                                 boolean pt2pt) throws MPJDevException {
        if (buf == null) {
            throw new MPJDevException("In Comm.isend(), buffer is null.");
        }
        
        if (dest < 0) {
            throw new MPJDevException(
                                      "In Comm.isend(), requested negative message destination: " + dest);
        }
        else if (dest >= size()) {
            throw new MPJDevException("In Comm.isend(), requested destination "
                                      + dest +
                                      " does not exist in communicator of size " + size());
        }
        
        int context = 0;
        
        if (pt2pt) {
            context = sendctxt;
        }
        else {
            context = collctxt;
        }
        // again is status important here ..we can define a completion 
        // handler here if it is ..
        return device.issend(buf, group.ids[dest], tag, context);
    }
    
    public void ssend(mpjbuf.Buffer buf, int dest, int tag, boolean pt2pt) 
    throws MPJDevException {
        if (buf == null) {
            throw new MPJDevException("In Comm.isend(), buffer is null.");
        }
        
        if (dest < 0) {
            throw new MPJDevException(
                                      "In Comm.isend(), requested negative message destination: " + dest);
        }
        else if (dest >= size()) {
            throw new MPJDevException("In Comm.isend(), requested destination "
                                      + dest +
                                      " does not exist in communicator of size " + size());
        }
        
        int context = 0;
        
        if (pt2pt) {
            context = sendctxt;
        }
        else {
            context = collctxt;
        }
        
        device.ssend(buf, group.ids[dest], tag, context);
    }
    
    public int size() {
        return this.group.size();
    }
    
    public int id() {
        //System.out.println(" this.group.rank() <"+this.group.rank()+">");
        return this.group.rank();
    }
    
    public void free() throws MPJDevException {
        //cleaning up resources ...
    }
    
    
    /*
     all processes with same color would be form one sub-group ...
     all processes send their color and key to all other processes ...
     all processes receive color and key from all other processes ...
     now classify how many colors you've ...
     assign ranks depending on keys ...lower the key, lower the new rank ..
     but if the keys are same ..still its not a problem ..its my
     responsibility to assigning new ascending ranks to each process ...
     */
    public Comm split(int color, int key) throws MPJDevException {
        
        int[][] b = new int[group.size()][3];
        int len = 0;
        int a[] = new int[2];
        a[0] = color;
        a[1] = key;
        b[len][0] = color;
        b[len][1] = key;
        b[len][2] = group.rank();
        len++;
        
        int size = group.size();
        int rank = group.rank();
        int tag = splitTag ;
        int sOverhead = MPJDev.getSendOverhead() ;
        int cap = sOverhead + 23; 
        
        Buffer buf = new Buffer(BufferFactory.create(cap), sOverhead, cap);
        
        try {
            buf.putSectionHeader(Type.INT); 
            buf.write(a, 0, 2);
            buf.commit();
        }
        catch(Exception e) {
            throw new MPJDevException( e );
        }
        
        mpjdev.Request[] reqs = new mpjdev.Request[size];
        
        /* send to all processes */
        for (int i = 0; i < size; i++) {
            if (i == rank)continue;
            reqs[i] = isend(buf, i, rank + tag + i, false);
        }
        
        try {
            buf.clear();
        }
        catch(Exception e) {
            throw new MPJDevException( e ); 	    
        }
        
        Buffer rbuf = new Buffer(BufferFactory.create(16), 0, 16);
        /* now receive from all other processes */
        for (int i = 0; i < size; i++) {
            if (i == rank)continue;
            //System.out.print("p<"+rank+"> receving from <"+i+">");
            try { 
                recv(rbuf, i, tag + i + rank, false);
                rbuf.commit();
                rbuf.getSectionHeader();
                rbuf.getSectionSize() ;
                rbuf.read(a, 0, 2);
                rbuf.clear();
            }
            catch(Exception e) {
                throw new MPJDevException( e );	      
            }
            
            if (a[0] == color) {
                b[len][0] = a[0];
                b[len][1] = a[1];
                b[len][2] = i;
                len++;
            }
        }
        
        /* complete send operation */
        for (int i = 0; i < size; i++) {
            if (i == rank)continue;
            reqs[i].iwait();
        }
        
        int keys[] = new int[len];
        for (int i = 0; i < len; i++) {
            keys[i] = b[i][1];
        }
        
        java.util.Arrays.sort(keys);
        int nids[] = new int[len];
        
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (keys[i] == b[j][1])
                    nids[i] = b[j][2];
            }
        }
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("rank " + rank);
        for (int i = 0; i < nids.length; i++) {
            buffer.append("nids[" + i + "]=" + nids[i]);
        }
        
        //if(rank == 0)
        //System.out.println("\n "+buffer.toString());
        //	System.exit(0);
        BufferFactory.destroy(buf.getStaticBuffer()) ;
        BufferFactory.destroy(rbuf.getStaticBuffer()) ;
        return create(nids);
    }
    
}
