package mpi.env;

import mpi.*;
import java.io.*;

public class InputTester {

  public static void main(String argv[]) {
  }

  public InputTester(String argv[]) throws Exception {
    MPI.Init(argv);
    int id = MPI.COMM_WORLD.Rank();
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String input;

    System.out.println("My id is: " + id);

    // get Input on process 0
    if (id == 0) {
      System.out.println("Enter a number in [ 0, 1000 ] : ");
      try {
	input = in.readLine();
	System.out.println("Got input. Value is: " + input);
      }
      catch (IOException e) {
	System.err.println("Exception while reading User Input");
	e.printStackTrace();
      }
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }

}
