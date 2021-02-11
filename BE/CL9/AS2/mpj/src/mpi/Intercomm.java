/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

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
 * File         : Intercomm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.10 $
 * Updated      : $Date: 2005/07/29 14:03:09 $
 */

package mpi;

import mpjdev.*;

public class Intercomm
    extends Comm {

  Comm peerComm = null; //only need Create_intercommm method ..so its OK :)
  Comm local_comm;
  int local_leader, remote_leader;

  //TODO: get this approved
  // native Intercomm
  mpjdev.natmpjdev.Intercomm nativeIntercomm = null;


  //this constructor may be used to create intercommunicators.
  Intercomm(mpjdev.Comm mpjdevComm, mpjdev.Group lGrp, mpjdev.Group rGrp,
            mpi.Comm peerComm, int local_leader, int remote_leader,
            mpi.Comm local_comm) {
    this.local_leader = local_leader;
    this.remote_leader = remote_leader;
    this.peerComm = peerComm;
    this.mpjdevComm = mpjdevComm;
    this.group = new Group(rGrp);
    this.localgroup = new Group(lGrp);
    this.intercomm = true;
    this.local_comm = local_comm;
    //you ll have to agree on send_context and recv_context
    if(Constants.isNative)
    nativeIntercomm = new mpjdev.natmpjdev.Intercomm(
							   (mpjdev.natmpjdev.Comm) this.mpjdevComm); 
  }

  /**
   * Size of remote group.
   * <p>
   * <table>
   * <tr><td><em> returns: </em></td><td> number of process in remote group
   *                                      of this communicator </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_REMOTE_SIZE</tt>.
   */
  public int Remote_size() throws MPIException {
    return group.Size();
  }

  /**
   * Return the remote group.
   * <p>
   * <table>
   * <tr><td><em> returns: </em></td><td> remote group of this
   *                                      communicator </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_REMOTE_GROUP</tt>.
   */
  public Group Remote_group() throws MPIException  {
    return group;
  }

  /**
   * Create an inter-communicator.
   * <p>
   * <table>
   * <tr><td><tt> high     </tt></td><td> true if the local group has higher
   *                                      ranks in combined group </tr>
   * <tr><td><em> returns: </em></td><td> new intra-communicator </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_INTERCOMM_MERGE</tt>.
   */
  public Intracomm Merge(boolean high) throws MPIException {
    //currently we ignore the boolean argument. According to the MPI specs,
    //if all processes in either of the two groups provide an argument equal
    //to 'high' and all the processes in the other group provide an argument
    //'low', then the group with 'high' arg is the first argument to thie
    //union method. Else, it doesn't matter. It is a collective call.
    //Well i am ignoring low/high stuff at the moment. i am just combining
    //them.
    //System.out.println(" ****************** "+
//		       "Intercomm.Merge only works if one group of processes "+
//		       "specify high as 'true', and others specify high as "+
//		       "'false'. The arbitrary case is not handled at the "+
//		       "moment ***************** ");

    try {


		if(Constants.isNative){
			
			mpjdev.Group mpjdevGroup = nativeIntercomm.Merge(high);
			mpi.Group ngrp = new mpi.Group(mpjdevGroup);
			return MPI.COMM_WORLD.Create(ngrp); //should work
		}


      Group ngrp = null;

      if(high){
		//  System.out.println("Merge for high, localgroup.size = "+localgroup.Size()+" group = "+group.Size());
        ngrp = Group.Union(group, localgroup);
        }
      else{
		//  System.out.println("Merge for NOT high , localgroup.size = "+localgroup.Size()+" group = "+group.Size());
	ngrp = Group.Union(localgroup, group);
	
	}
	//System.out.println("going to create in merge\n");
      return MPI.COMM_WORLD.Create(ngrp); //ooooo! that's very dogdy.

    }catch(Exception e) {
      throw new MPIException(e);	    
    }
  }

  /**
   * where would peer communicator come from ...and also localleader,
   * and remoteleader ....
   */
  public Object clone() throws MPIException {
    return peerComm.Create_intercomm(local_comm, local_leader,
                                     remote_leader, 110); 
    //this 110 is abit ODD ...its OK to be "abit" ODD ;-)
  }

}

