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
 * File         : Group.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Oct 15 12:22:15 BST 2004
 * Revision     : $Revision: 1.13 $
 * Updated      : $Date: 2005/11/27 19:40:12 $
 *
 */

package mpjdev.javampjdev;

import java.util.UUID;
import xdev.ProcessID;
import mpjdev.*;

public class Group extends mpjdev.Group {
    
    //public ProcessID[] ids = null;
    
    //ProcessID myID = null;
    //int rank, size;
    
    //indexes of this array are ranks, remember this
    
    public Group(ProcessID[] ids, ProcessID myID, int rank) {
        this.ids = ids;
        this.size = ids.length;
        
        if (myID != null) {
            this.rank = rank; // myID.rank();
            //this rank is index of myID on ids array ...
            this.myID = myID;
            if(!(ids[rank].uuid().equals(myID.uuid()))) {
                System.out.println("The group should contain itself "+
                                   "Error in mpjdev.Group constructor ");
            }
        }
        else
            this.rank = NO_RANK;
    }
    
    /**
     * This method frees this group object. Though automatic garbage collector
     * will take care of it, but we mark this object for gc, by declaring it
     * null ...
     * implement at the end.
     */
    public void free() {
        //this.group = null;
        //this = null;
    }
    
    /**
     * This method returns the size of the group. It is a count of the number
     * of process encapsulated by this group object.
     * @return int The number of processes in this group
     */
    public int size() {
        return this.size;
    }
    
    /**
     * This method returns the rank of the group. It is the rank (id) of the calling thread/process in this group.
     * @return int The rank of the callling process in this group.
     */
    public int rank() {
        return this.rank;
    }
    
    /**
     * ranks1 should be a valid argument in Translate_ranks
     * ranks2.length = ranks1.length
     */
    
    public static int[] transRanks(mpjdev.Group group1, int[] ranks1,
                                   mpjdev.Group group2) {
        int[] ranks2 = new int[ranks1.length];
        int i, j, k;
        
        for (k = 0; k < ranks2.length; k++) {
            ranks2[k] = UNDEFINED ;
        }
        
        ProcessID[] ids1 = group1.ids;
        int size1 = group1.size;
        
        ProcessID[] ids2 = group2.ids;
        int size2 = group2.size;
        UUID temp1 = null, temp2 = null;
        
        for (i = 0; i < ranks1.length; i++) {
            temp1 = ids1[i].uuid();
            
            for (j = 0; j < size2; j++) {
                temp2 = ids2[j].uuid();
                
                if (temp1.equals(temp2)) {
                    ranks2[i] = j;
                    //ranks2[i] = ids2[j].rank();
                    break;
                }
            }
            
        }
        return ranks2;
    }
    
    /**
     */
    public static int compare(mpjdev.Group group1, mpjdev.Group group2) {
        /* Null will be replaced by GROUP_EMPTY */
        if (group1 == null && group2 == null) {
            System.out.println("compare why are you comparing two null groups :( ?");
            return -1;
        }
        else if (group1 == null && group2 != null) {
            System.out.println("compare group1 is null" + group1);
            return -1;
        }
        else if (group1 != null && group2 == null) {
            System.out.println("compare group2 is null" + group2);
            return -1;
        }
        else {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            int size1 = group1.size;
            int rank1 = group1.rank;
            
            ProcessID[] ids2 = group2.ids;
            ProcessID myID2 = group2.myID;
            int size2 = group2.size;
            int rank2 = group2.rank;
            
            /* debugging loop ...
             System.out.println("size1 "+size1);
             System.out.println("size2 "+size2);
             for(int g=0 ; g<size1; g++) {
             System.out.print("ids1["+g+"]="+ids1[g]);
             System.out.print("ids2["+g+"]="+ids2[g]);
             
             }*/
            
            ProcessID[] tempids = new ProcessID[size1], ids = null;
            ProcessID myID = null;
            int rank = 0;
            int size = 0;
            int i, j;
            UUID temp1 = null, temp2 = null;
            
            if (size1 != size2) {
                return UNEQUAL;
            }
            
            boolean ident = true;
            
            for (i = 0; i < size1; i++) {
                temp1 = ids1[i].uuid();
                temp2 = ids2[i].uuid();
                
                if (temp1.equals(temp2)) {
                }
                else {
                    ident = false;
                    break;
                }
            }
            
            if (ident) {
                return IDENT;
            }
            
            boolean sim = true;
            
            for (i = 0; i < size1; i++) {
                temp1 = ids1[i].uuid();
                for (j = 0; j < size2; j++) {
                    temp2 = ids2[j].uuid();
                    if (temp1.equals(temp2)) {
                        break;
                    }
                    if (j == size2) {
                        sim = false;
                    }
                }
            }
            
            if (sim) {
                return SIMILAR;
            }
            else {
                return UNEQUAL;
            }
            
        }
    }
    
    //TODO: why not return mpjdev.Group
    public static Group union(mpjdev.Group group1, mpjdev.Group group2) throws MPJDevException {
        
        /* Null will be replaced by GROUP_EMPTY */
        if (group1 == null && group2 == null) {
            return null;
        }
        else if (group1 == null && group2 != null) {
            ProcessID[] ids2 = group2.ids;
            ProcessID myID2 = group2.myID;
            int size2 = group2.size;
            int rank2 = group2.rank;
            ProcessID[] ids = new ProcessID[size2];
            ProcessID myID = null;
            int rank = NO_RANK, size = 0;
            int i;
            
            for (i = 0; i < size2; i++) {
                ids[i] = ids2[i] ; //new ProcessID(ids2[i].uuid(), ids2[i].rank());
                if (myID2 != null && ids[i].uuid().equals(myID2.uuid())) {
                    rank = rank2;
                    myID = ids[i]; //new ProcessID(ids[i].uuid(), rank);
                    //myID.rank(rank);
                }
            }
            
            size = size2;
            return new Group(ids, myID, rank);
        }
        else if (group1 != null && group2 == null) {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            int size1 = group1.size;
            int rank1 = group1.rank;
            ProcessID[] ids = new ProcessID[size1];
            ProcessID myID = null;
            int rank = NO_RANK, size = 0;
            int i;
            
            for (i = 0; i < size1; i++) {
                ids[i] = ids1[i]; //new ProcessID(ids1[i].uuid(), ids1[i].rank());
                rank = i;
                if (myID1 != null && ids[i].uuid().equals(myID1.uuid())) {
                    myID = ids[i]; //new ProcessID(ids[i].uuid(), rank);
                    //myID.rank(rank);
                }
            }
            
            size = size1;
            return new Group(ids, myID,rank);
        }
        else {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            int size1 = group1.size;
            int rank1 = group1.rank;
            
            ProcessID[] ids2 = group2.ids;
            ProcessID myID2 = group2.myID;
            int size2 = group2.size;
            int rank2 = group2.rank;
            
            ProcessID[] tempids = new ProcessID[size1 + size2], ids = null;
            ProcessID myID = null;
            int rank = NO_RANK;
            int size = 0;
            int i, j;
            
            for (i = 0; i < size1; i++) {
                tempids[i] = ids1[i];
                //new ProcessID(ids1[i].uuid(), ids1[i].rank());
                
                if (myID1 != null && tempids[i].uuid().equals(myID1.uuid())) {
                    myID = tempids[i]; //new ProcessID(tempids[i].uuid(), i);
                    //myID.rank(i);
                    rank = i;
                }
            }
            
            size = size1;
            i = 0;
            UUID uuid2 = null;
            
            for (j = 0; j < size2; j++) {
                uuid2 = ids2[j].uuid();
                //check if it is already contained
                boolean present = false;
                //System.out.println("\n uuid2 ==>"+uuid2);
                for (i = 0; i < size; i++) {
                    //System.out.println("tempids["+i+"]="+tempids[i]);
                    //System.out.println("uids1["+i+"].uuid() "+tempids[i].uuid());
                    
                    if (tempids[i].uuid().equals(uuid2)) {
                        present = true;
                        break;
                    }
                    
                }
                //its not in there, so add it
                if (!present) {
                    tempids[size] = ids2[j];
                    //new ProcessID(ids2[j].uuid(), size);
                    //tempids[size].rank(size);
                    
                    if (myID2 != null && tempids[size].uuid().equals(myID2.uuid())) {
                        myID = tempids[size]; //new ProcessID(tempids[size].uuid(), size);
                        //myID.rank(size);
                        rank = size ;
                    }
                    size++;
                }
                present = false;
            }
            ids = new ProcessID[size];
            for (j = 0; j < size; j++)
                ids[j] = tempids[j];
            
            return new Group(ids, myID, rank);
        }
    }
    
    /**
     *
     */
    
    public static mpjdev.Group intersection(mpjdev.Group group1, mpjdev.Group group2) {
        
        /* Null will be replaced by GROUP_EMPTY */
        if (group1 == null || group2 == null) {
            return null;
        }
        else {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            int size1 = group1.size;
            int rank1 = group1.rank;
            
            ProcessID[] ids2 = group2.ids;
            ProcessID myID2 = group2.myID;
            int size2 = group2.size;
            int rank2 = group2.rank;
            
            ProcessID[] tempids = new ProcessID[size1 + size2], ids = null;
            ProcessID myID = null;
            int rank = NO_RANK;
            int size = 0;
            int i, j;
            UUID temp1, temp2;
            
            for (i = 0; i < size1; i++) {
                temp1 = ids1[i].uuid();
                for (j = 0; j < size2; j++) {
                    temp2 = ids2[j].uuid();
                    
                    if (temp1.equals(temp2)) {
                        //rank = size;
                        tempids[size] = ids1[i]; //new ProcessID(temp1, rank);
                        //tempids[size].rank(rank);
                        
                        if ( (myID2 != null && tempids[size].uuid().equals(myID2.uuid())) ||
                            (myID1 != null && tempids[size].uuid().equals(myID1.uuid()))) {
                            rank = size;
                            myID = tempids[size];//new ProcessID(tempids[size].uuid(), rank);
                            //myID.rank(rank);
                        }
                        
                        size++;
                        break;
                    }
                }
            }
            ids = new ProcessID[size];
            
            for (j = 0; j < size; j++)
                ids[j] = tempids[j];
            
            return new Group(ids, myID, rank);
        }
    }
    
    /**
     * Processes in group1, which are not in group2 make another grp.
     */
    public static Group difference(mpjdev.Group group1, mpjdev.Group group2) throws
    MPJDevException {
        
        /* Null will be replaced by GROUP_EMPTY */
        if (group1 == null && group2 == null) {
            return null;
        }
        else if (group1 == null && group2 != null) {
            return null;
        }
        else if (group1 != null && group2 == null) {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            return new Group(ids1, myID1,group1.rank);
        }
        else {
            ProcessID[] ids1 = group1.ids;
            ProcessID myID1 = group1.myID;
            int size1 = group1.size;
            int rank1 = group1.rank;
            
            ProcessID[] ids2 = group2.ids;
            ProcessID myID2 = group2.myID;
            int size2 = group2.size;
            int rank2 = group2.rank;
            
            ProcessID[] tempids = new ProcessID[size1], ids = null;
            ProcessID myID = null;
            int rank = NO_RANK;
            int size = 0;
            int i, j;
            UUID temp1 = null, temp2 = null;
            
            for (i = 0; i < size1; i++) {
                temp1 = ids1[i].uuid();
                boolean present = false, sid = false;
                
                for (j = 0; j < size2; j++) {
                    temp2 = ids2[j].uuid();
                    if (temp1.equals(temp2)) {
                        present = true;
                        break;
                    }
                }
                
                if (!present) {
                    tempids[size] = ids1[i]; //new ProcessID(temp1, size);
                    //ids1[i].rank(rank);
                    
                    if ( (rank1 != NO_RANK && temp1.equals(myID1.uuid()))) { //||
                        //( rank2 != NO_RANK && temp2.equals(myID2.uuid()) ) ) {
                        //System.out.println("\nfixing my id to " + size + "where, rank1=" +
                        //                   rank1 + ",rank2" + rank2 + "\n");
                        myID = tempids[size]; //new ProcessID(tempids[size].uuid(), size);
                        //myID.rank(size);
                        rank = size;
                    }
                    
                    size++;
                }
                present = false;
            }
            
            ids = new ProcessID[size];
            
            for (j = 0; j < size; j++)
                ids[j] = tempids[j];
            
            return new Group(ids, myID, rank);
        }
    }
    
    /**
     * This method returns a new group object including all the ranks specified in the argument array.
     * Each item of the argument array should be a valid rank in the calling group. The total number of
     * items in argument array should not be more than the size of the existing group. This method is a local
     * operation.
     * @param ranks Integer array specifying the ranks of the processes that will be part of the new group
     * @return Group The group object of the new process or null if the calling process is not in the new group.
     */
    public Group incl(int[] ranks) throws MPJDevException {
        StringBuffer buffer = new StringBuffer();
        /*
         buffer.append("rank "+rank);
         for(int g=0 ; g<ranks.length ; g++) {
         buffer.append("ranks["+g+"]="+ranks[g]);
         buffer.append("ids["+ranks[g]+"]="+ids[ranks[g]].uuid());
         }
         if(rank == 0)
         System.out.println("\n\n mpjdev incl.(before) "+buffer.toString());
         */
        int i, j;
        int rank = NO_RANK;
        if (ranks.length > this.size) {
            throw new MPJDevException("Error in Group Incl method: length of "+
                                      "array "+ranks.length+" is greater than size <"+
                                      this.size + ">" );
        }
        
        for (i = 0; i < ranks.length; i++) {
            if (ranks[i] > (this.size - 1) || ranks[i] < 0) {
                throw new MPJDevException("Error in Group Incl method: arg["+i+"] "+
                                          "of array is bounded by [0-"+(this.size-1)+"]" );
            }
        }
        
        ProcessID[] newIds = new ProcessID[ranks.length];
        ProcessID myID = null;
        
        for (j = 0; j < ranks.length; j++) {
            newIds[j] = ids[ranks[j]]; //new ProcessID(ids[ranks[j]].uuid(), j);
            //newIds[j].rank(j);
            if (ranks[j] == this.rank) {
                myID = newIds[j]; //new ProcessID(newIds[j].uuid(), j);
                rank = j;
                //myID.rank(j);
            }
        }
        /*
         StringBuffer nbuffer = new StringBuffer();
         nbuffer.append("rank "+rank);
         for(int g=0 ; g<newIds.length ; g++) {
         nbuffer.append("newIds["+g+"]="+newIds[g].uuid() );
         nbuffer.append("newIds["+g+"]="+newIds[g].rank() );
         }
         if(rank == 0)
         System.out.println("\n\n mpjdev incl. (after)"+nbuffer.toString());
         */
        return new Group(newIds, myID, rank);
    }
    
    /**
     * This method returns a new group object excluding all the ranks specified in the argument array.
     * Each item of the argument array should be a valid rank in the calling group. The total number of
     * items in argument array should not be more than the size of the existing group. This method is a local
     * operation.
     * @param ranks Integer array specifying the ranks of the processes that will not be part of the new group
     * @return Group The group object of the new process or null if the calling process is in argument array.
     */
    public Group excl(int[] ranks) throws MPJDevException {
        int i, j;
        if (ranks.length > this.size) {
            throw new MPJDevException("Error in Group Excl method: length of "+
                                      "array "+ranks.length+" is greater than size <"+
                                      this.size + ">" );
        }
        
        for (i = 0; i < ranks.length; i++) {
            if (ranks[i] > (this.size - 1) || ranks[i] < 0) {
                throw new MPJDevException("Error in Group Excl method: arg["+i+"] "+
                                          "of array is bounded by [0-"+(this.size-1)+"]" );
            }
        }
        
        ProcessID[] newIds = new ProcessID[this.size - ranks.length];
        ProcessID myID = null;
        int rank = NO_RANK;
        int nrank = 0;
        boolean present = false;
        
        for (j = 0; j < this.size; j++) {
            for (i = 0; i < ranks.length; i++) {
                //if (ids[j].rank() == ranks[i])
                if (j == ranks[i]) {
                    present = true;
                }
            }
            
            if (!present) {
                newIds[nrank] = ids[j]; //new ProcessID(ids[j].uuid(), nrank);
                //newIds[nrank].rank(nrank);
                //if (ids[j].rank() == this.rank) { 
                if (j == this.rank) { 
                    myID = newIds[nrank]; //new ProcessID(newIds[nrank].uuid(), nrank);
                    rank = nrank;
                    //myID.rank(nrank);
                }
                nrank++;
            }
            
            present = false;
        }
        
        return new Group(newIds, myID, rank);
    }
    
    /**
     */
    public Group rangeIncl(int[][] ranges) throws MPJDevException {
        int len = ranges.length, fRank, lRank, str, i, j, k, np = 0, ranks;
        
        for (i = 0; i < len; i++) {
            fRank = ranges[i][0];
            lRank = ranges[i][1];
            str = ranges[i][2];
            
            if (str != 0) {
                if ( str > 0 && fRank > lRank) {
                    throw new MPJDevException( "Error in rangeIncl: stride <"+str+">"+
                                              "is greater than zero and firstRank <"+fRank+
                                              "> is greater than lastRank <"+lRank+ ">" );
                }
                
                if (str < 0 && fRank < lRank) {
                    throw new MPJDevException( "Error in rangeIncl: stride <"+str+">"+
                                              "is less than zero and firstRank <"+fRank+
                                              "> is less than lastRank <"+lRank+ ">" );
                }
                
                if ( (ranks = ( ( (lRank - fRank) / str) + 1)) > 0) {
                    np += ranks;
                }
            }
            else {
                throw new MPJDevException( "Error in rangeIncl: stride <"+str+">"+
                                          "is zero"); 
            }
        }
        
        if (np == 0) {
            throw new MPJDevException( "Error in rangeIncl: number of processes <"
                                      +np+"> to be included in new group is zero"); 
        }
        
        int[] npp = new int[np];
        
        //for(int l=0 ; l<npp.length ; l++) npp[l] =-1;
        
        k = 0;
        
        for (i = 0; i < len; i++) {
            fRank = ranges[i][0];
            lRank = ranges[i][1];
            str = ranges[i][2];
            
            if (str != 0) {
                //Does this start with fRank ?
                for (j = fRank; j * str <= lRank * str; j += str) {
                    if (j < this.size() && j >= 0) {
                        npp[k] = j;
                        k++;
                    }
                }
            }
            else {
                throw new MPJDevException( "Error in rangeIncl: stride <"+str+">"+
                                          "is zero"); 
            }
        }
        
        return incl(npp);
    }
    
    /**
     * implemented, not sure what it does and thus not tested at the moment.
     */
    public Group rangeExcl(int[][] ranges) throws MPJDevException {
        int len = ranges.length, fRank, lRank, str, i, j, k, np = 0, ranks;
        
        for (i = 0; i < len; i++) {
            fRank = ranges[i][0];
            lRank = ranges[i][1];
            str = ranges[i][2];
            
            if (str != 0) {
                
                if ( str > 0 && fRank > lRank) {
                    throw new MPJDevException( "Error in rangeExcl: stride <"+str+">"+
                                              "is greater than zero and firstRank <"+fRank+
                                              "> is greater than lastRank <"+lRank+ ">" );
                }
                
                if (str < 0 && fRank < lRank) {
                    throw new MPJDevException( "Error in rangeExcl: stride <"+str+">"+
                                              "is less than zero and firstRank <"+fRank+
                                              "> is less than lastRank <"+lRank+ ">" );
                }
                
                if ( (ranks = ( ( (lRank - fRank) / str) + 1)) > 0) {
                    np += ranks;
                }
            }
            else {
                throw new MPJDevException( "Error in rangeExcl: stride <"+str+">"+
                                          "is zero"); 
            }
        }
        
        if (np == 0) {
            throw new MPJDevException( "Error in rangeExcl: number of processes <"
                                      +np+"> to be included in new group is zero"); 
        }
        
        k = 0;
        int[] npp = new int[np];
        
        for (i = 0; i < len; i++) {
            fRank = ranges[i][0];
            lRank = ranges[i][1];
            str = ranges[i][2];
            
            if (str != 0) {
                //does this start with fRank ?
                for (j = fRank; j * str <= lRank * str; j += str) {
                    if (j < this.size() && j >= 0) {
                        npp[k] = j;
                        k++;
                    }
                }
            }
            else {
                throw new MPJDevException( "Error in rangeExcl: stride <"+str+">"+
                                          "is zero"); 
            }
        }
        
        return excl(npp);
    }
    
    //implement at the end.
    public void finalize() throws MPJDevException {
    }
}

