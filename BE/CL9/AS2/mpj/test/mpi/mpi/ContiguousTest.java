//package mpi;
/*
 * Compile :- javac -classpath $HOME/aamirmpj/aamir-niompj/mpjdev/src:$MPJ_HOME/src/:$HOME/install/junit3.8.1/junit.jar ContiguousTest.java 
 * Execute :- java -classpath $HOME/aamirmpj/aamir-niompj/mpjdev/src:$MPJ_HOME/src/:$HOME/install/junit3.8.1/junit.jar mpi.ContiguousTest  */

import java.util.Arrays;
import mpjbuf.Buffer;
import junit.framework.*;

/**
 * This example application, is to check and demonstrat the functonality of
 * contiguous datatypes. I'm writing an MPJ program by coverting a C program by
 * Forest Hoffman at following URL
 * http://www.linux-mag.com/2003-04/extreme_01.html
 * 
 */
// public class ContiguousTest {
public class ContiguousTest extends TestCase {

  public static int NUM_PARAMS = 12;

  // public static void main(String args []) throws Exception {
  // new ContiguousTest().testSimplePackUnpack() ;
  // }

  public ContiguousTest(String testName) {
    super(testName);
  }

  public static Test suite() {

    TestSuite suite = new TestSuite();

    suite.addTest(new ContiguousTest("simple add") {
      public void runTest() {
	try {
	  testSimplePackUnpack();

	}
	catch (Exception e) {
	  e.printStackTrace();
	  fail("exception throw while calling testSimplePackUnpack method: "
	      + e.getMessage());
	}
      }
    });
    return suite;
  }

  public void testSimplePackUnpack() throws Exception {

    // MPI.Init(args);
    int i, rank, size;

    double[] params = new double[NUM_PARAMS];
    double[] rparams = new double[NUM_PARAMS];

    // rank = MPI.COMM_WORLD.Rank();
    // size = MPI.COMM_WORLD.Size();
    Datatype parameterType;

    /* only the first process reads the parameters */
    // if(rank == 0) {
    for (i = 0; i < NUM_PARAMS; i++) {
      params[i] = (double) (i * i);
    }
    // }

    parameterType = Datatype.Contiguous(NUM_PARAMS, MPI.DOUBLE);
    parameterType.Commit(); // this is doing nothing at the moment.

    mpjbuf.Buffer buf = parameterType.createWriteBuffer(1);
    mpi.Packer packer = parameterType.getPacker();

    packer.pack(buf, params, 0, 1);

    buf.commit();

    // Check it got packed OK.

    int numEls;
    numEls = buf.getSectionHeader(Buffer.DOUBLE);

    double[] packed = new double[NUM_PARAMS];
    buf.read(packed, 0, numEls);

    /*
     * for(i=0 ; i<NUM_PARAMS ; i++) { System.out.println("" + packed [i]); }
     */

    for (i = 0; i < NUM_PARAMS; i++) {
      Assert.assertTrue(packed[i] == (double) (i * i));
    }
    // Assert.assertTrue(false); // debug

    //
    // //MPI.COMM_WORLD.Send(params,0,1,parameterType,1, 10);
    // //if (rank == 1) {
    //
    // //MPI.COMM_WORLD.Recv(rparams,0,1,parameterType,0,10);
    // System.out.println("recv completed");
    //
    // for(int j=0 ; j<rparams.length ; j++) {
    // //System.out.print("rparams["+j+"]="+rparams[j]+"\t");
    // }
    //
    // /* work out what would be test ?
    // if (Arrays.equals(source,dest)) {
    // System.out.println("PASS");
    // }else {
    // System.out.println("\n****");
    // System.out.println("FAIL");
    // System.out.println("****");
    // }*/
    // //}else if(rank == 0) {
    // //MPI.COMM_WORLD.Send(params,0,1,parameterType,1, 10);
    // //System.out.println("Send Completed"); }
    // try { Thread.currentThread().sleep(1000); }catch(Exception e){}
    // //MPI.COMM_WORLD.Barrier();
    // //MPI.Finalize();
  }

  public static void main(String args[]) {
    junit.textui.TestRunner.run(suite());
  }
}
