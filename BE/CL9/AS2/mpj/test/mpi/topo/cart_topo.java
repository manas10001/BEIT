package mpi.topo;

import mpi.*;

public class cart_topo {
  public static void main(String[] args) throws Exception {
    try {
      cart_topo c = new cart_topo(args);
    }
    catch (Exception e) {
    }
  }

  public cart_topo() {
  }

  public cart_topo(String[] args) throws Exception {

    MPI.Init(args);
    Group group = MPI.COMM_WORLD.Group();
    int processes = group.Size();
    int rank = group.Rank();

    if (processes < 8) {
      if (rank == 0)
	System.out.println("topo->cart_topo: MUST RUN WITH 8 tasks");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    /*
     * checking two dimensional cartesian topology int dims2[] = new int[2];
     * dims2[0] = 2; dims2[1] = 2;
     * 
     * boolean periods2[] = new boolean[2]; periods2[0] = false; periods2[1] =
     * false; Cartcomm comm = MPI.COMM_WORLD.Create_cart(dims2,periods2,false);
     * 
     * int[] c = new int[2];
     * 
     * for(int i=0 ; i<2 ; i++) { for(int j=0 ; j<2 ; j++) { c[0]= i; c[1]= j;
     * 
     * if(rank == 0) {
     * System.out.println("rank =<"+comm.Rank(c)+">,i=<"+c[0]+">=j<"+c[1]); }
     * 
     * } }
     * 
     * checking three dimensional cartesian topology
     */
    int dims3[] = new int[3];
    dims3[0] = 2;
    dims3[1] = 2;
    dims3[2] = 2;

    boolean periods3[] = new boolean[3];
    periods3[0] = false;
    periods3[1] = false;
    periods3[2] = false;

    Cartcomm comm2 = MPI.COMM_WORLD.Create_cart(dims3, periods3, false);
    int[] c1 = new int[3];
    if (rank == 0)
      System.out.println("\ncoords to ranks (3D)\n");
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
	for (int k = 0; k < 2; k++) {

	  c1[0] = i;
	  c1[1] = j;
	  c1[2] = k;

	  if (rank == 0) {
	    System.out.println("rank =<" + comm2.Rank(c1) + ">,i=<" + c1[0]
		+ ">=j<" + c1[1] + ">,k=<" + c1[2]);
	  }
	}
      }
    }
    /*
     * if(rank == 0) System.out.println("\nranks to coords (3D)\n");
     * 
     * for(int i=0 ; i<comm2.Size() ; i++) { int[] ct = new int[3]; ct =
     * comm2.Coords(i);
     * 
     * if(rank == 0) { System.out.println("rank =<"+i+">,i=<"+ct[0]+
     * ">=j<"+ct[1]+">,k=<"+ct[2]); } }
     */

    // try { Thread.currentThread().sleep(10000); }catch(Exception e){}
    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }// end main

}
