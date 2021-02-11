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

public class getcount {
  static public void main(String[] args) throws Exception {
    try {
      getcount c = new getcount(args);
    }
    catch (Exception e) {
    }
  }

  public getcount() {
  }

  public getcount(String[] args) throws Exception {
    int me, count;
    int datacout = 50;
    byte dataBYTE[] = new byte[5];
    char dataCHAR[] = new char[5];
    int dataINT[] = new int[datacout];
    float dataFLOAT[] = new float[5];
    double dataDOUBLE[] = new double[5];
    short dataSHORT[] = new short[5];
    long dataLONG[] = new long[5];
    Status status;
    Status st2;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 0) {
      // MPI.COMM_WORLD.Send(dataBYTE,0,5,MPI.BYTE,1,1);
      // MPI.COMM_WORLD.Send(dataCHAR,0,5,MPI.CHAR,1,1);

      for (int i = 0; i < datacout; i++) {
	dataINT[i] = i;
      }
      MPI.COMM_WORLD.Send(dataINT, 0, 50, MPI.INT, 1, 1);
      /*
       * MPI.COMM_WORLD.Send(dataFLOAT,0,5,MPI.FLOAT,1,1);
       * 
       * MPI.COMM_WORLD.Send(dataDOUBLE,0,5,MPI.DOUBLE,1,1);
       * 
       * MPI.COMM_WORLD.Send(dataSHORT,0,5,MPI.SHORT,1,1);
       * 
       * MPI.COMM_WORLD.Send(dataLONG,0,5,MPI.LONG,1,1);
       */
    } else if (me == 1) {
      /*
       * status = MPI.COMM_WORLD.Recv(dataBYTE,0,5,MPI.BYTE,0,1); count =
       * status.Get_count(MPI.BYTE); if(count != 5) System.out.println
       * ("ERROR(1) in Get_count(MPI.BYTE), count = "+count+", should be 5");
       */
      /*
       * status = MPI.COMM_WORLD.Recv(dataCHAR,0,5,MPI.CHAR,0,1); count =
       * status.Get_count(MPI.CHAR); if(count != 5) System.out.println
       * ("ERROR(2) in Get_count(MPI.CHAR), count = "+count+", should be 5");
       */

      status = MPI.COMM_WORLD.Recv(dataINT, 0, 50, MPI.INT, 0, 1);
      for (int i = 0; i < datacout; i++) {
	System.out.print(" " + dataINT[i]);
      }
      System.out.println(" ");
      count = status.Get_count(MPI.INT);
      if (count != 50)
	System.out.println("ERROR(3) in Get_count(MPI.INT), count = " + count
	    + ", should be 5");
      /*
       * status = MPI.COMM_WORLD.Recv(dataFLOAT,0,5,MPI.FLOAT,0,1); count =
       * status.Get_count(MPI.FLOAT); if(count != 5) System.out.println
       * ("ERROR(4) in Get_count(MPI.FLOAT), count = "+count+", should be 5");
       * 
       * 
       * status = MPI.COMM_WORLD.Recv(dataDOUBLE,0,5,MPI.DOUBLE,0,1); count =
       * status.Get_count(MPI.DOUBLE); if(count != 5) System.out.println
       * ("ERROR(5) in Get_count(MPI.DOUBLE), count = "+count+", should be 5");
       * 
       * 
       * status = MPI.COMM_WORLD.Recv(dataSHORT,0,5,MPI.SHORT,0,1); count =
       * status.Get_count(MPI.SHORT); if(count != 5) System.out.println
       * ("ERROR(6) in Get_count(MPI.SHORT), count = "+count+", should be 5");
       * 
       * 
       * status = MPI.COMM_WORLD.Recv(dataLONG,0,5,MPI.LONG,0,1); count =
       * status.Get_count(MPI.LONG); if(count != 5) System.out.println
       * ("ERROR(7) in Get_count(MPI.LONG), count = "+count+", should be 5");
       */
    }

    if (me == 1)
      System.out.println("Get_count TEST COMPLETE");

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
