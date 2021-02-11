package mpi.pt2pt;

/****************************************************************************

 MESSAGE PASSING INTERFACE TEST CASE SUITE

 Copyright IBM Corp. 1995

 IBM Corp. hereby grants a non-exclusive license to use, copy, modify, and
 distribute this software for any purpose and without fee provided that the
 above copyright notice and the following paragraphs appear in all copies.

 IBM Corp. makes no representation that the test cases comprising this
 suite are correct or are an accurate representation of any standard.

 In no event shall IBM be liable to any party for direct, indirect, special
 incidental, or consequential damage arising out of the use of this software
 even if IBM Corp. has been advised of the possibility of such damage.

 IBM CORP. SPECIFICALLY DISCLAIMS ANY WARRANTIES INCLUDING, BUT NOT LIMITED
 TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS AND IBM
 CORP. HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 ****************************************************************************

 These test cases reflect an interpretation of the MPI Standard.  They are
 are, in most cases, unit tests of specific MPI behaviors.  If a user of any
 test case from this set believes that the MPI Standard requires behavior
 different than that implied by the test case we would appreciate feedback.

 Comments may be sent to:
 Richard Treumann
 treumann@kgn.ibm.com

 ****************************************************************************

 MPI-Java version :
 Sung-Hoon Ko(shko@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 03/22/98

 ****************************************************************************
 */

import mpi.*;

public class probe {
  static public void main(String[] args) throws Exception {
    try {
      probe c = new probe(args);
    }
    catch (Exception e) {
    }
  }

  public probe() {
  }

  public probe(String[] args) throws Exception {
    int me, i, cnt, src, tag, tasks;
    int data[] = new int[1];
    Intracomm comm;
    Status status;

    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.Rank();
    tasks = comm.Size();

    // probe for specific source, tag
    if (me > 0) {
      data[0] = me;
      comm.Send(data, 0, 1, MPI.INT, 0, me);
    } else {

      for (i = 1; i < tasks; i++) {
	status = comm.Probe(i, i);

	src = status.source;
	if (src != i)
	  System.out.println("ERROR in MPI_Probe(1): src = " + src
	      + ", should be " + i);

	tag = status.tag;
	if (tag != i)
	  System.out.println("ERROR in MPI_Probe(1): tag = " + tag
	      + ", should be " + i);

	cnt = status.Get_count(MPI.INT);
	if (cnt != 1) {
	  System.out.println("ERROR in MPI_Probe(1): cnt = " + cnt
	      + ", should be 1");
	  System.exit(0);
	}

	status = comm.Recv(data, 0, cnt, MPI.INT, src, tag);
	if (data[0] != i)
	  System.out.println("ERROR in MPI_Recv(1), data = " + data[0]
	      + ", should be " + i);
      }
    }

    /*
     * // probe for specific source, tag = MPI_ANY_TAG if(me > 0) { data[0] =
     * me; comm.Send(data,0,1,MPI.INT,0,me); } else { for(i=1;i<tasks;i++) {
     * status = comm.Probe(i,MPI.ANY_TAG);
     * 
     * 
     * src = status.source; if(src != i) System.out.println
     * ("ERROR in MPI_Probe(2): src = "+src+", should be "+i);
     * 
     * 
     * tag =status.tag; if(tag != i) System.out.println
     * ("ERROR in MPI_Probe(2): tag = "+tag+", should be "+i);
     * 
     * 
     * cnt = status.Get_count(MPI.INT); if(cnt != 1) System.out.println
     * ("ERROR in MPI_Probe(2): cnt = "+cnt+", should be 1");
     * 
     * status = comm.Recv(data,0,cnt,MPI.INT,src,tag); if(data[0] != i)
     * System.out.println
     * ("ERROR in MPI_Recv(2), data = "+data[0]+", should be "+i); } }
     * 
     * // probe for specific tag, source = MPI_ANY_SOURCE if(me > 0) { data[0] =
     * me; comm.Send(data,0,1,MPI.INT,0,me); } else { for(i=1;i<tasks;i++) {
     * status = comm.Probe(MPI.ANY_SOURCE,i); src = status.source; if(src != i)
     * System.out.println
     * ("ERROR in MPI_Probe(3): src = "+src+", should be "+i);
     * 
     * 
     * tag =status.tag; if(tag != i) System.out.println
     * ("ERROR in MPI_Probe(3): tag = "+tag+", should be "+i);
     * 
     * 
     * cnt = status.Get_count(MPI.INT); if(cnt != 1) System.out.println
     * ("ERROR in MPI_Probe(3): cnt = "+cnt+", should be 1");
     * 
     * 
     * 
     * status = comm.Recv(data,0,cnt,MPI.INT,src,tag); if(data[0] != i)
     * System.out.println
     * ("ERROR in MPI_Recv(3), data = "+data[0]+", should be "+i); } //end for
     * }//end else
     * 
     * 
     * // probe for source = MPI_ANY_SOURCE, tag = MPI_ANY_TAG if(me > 0) {
     * data[0] = me; comm.Send(data,0,1,MPI.INT,0,me); } else {
     * for(i=1;i<tasks;i++) { status = comm.Probe(MPI.ANY_SOURCE,MPI.ANY_TAG);
     * 
     * src = status.source; tag =status.tag; if(src != tag) System.out.println
     * ("ERROR in MPI_Probe(4): tag = "+tag+", should be "+src);
     * 
     * 
     * cnt = status.Get_count(MPI.INT); if(cnt != 1) System.out.println
     * ("ERROR in MPI_Probe(4): cnt = "+cnt+", should be 1");
     * 
     * status = comm.Recv(data,0,cnt,MPI.INT,src,tag); if(data[0] != src)
     * System.out.println
     * ("ERROR in MPI_Recv(4), data = "+data+", should be "+src); }
     * 
     * }
     */
    comm.Barrier();
    if (me == 1)
      System.out.println("Probe TEST COMPLETE");
    MPI.Finalize();
  }
}
