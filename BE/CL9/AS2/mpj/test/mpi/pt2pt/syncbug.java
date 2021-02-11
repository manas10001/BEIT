import mpi.*;

class syncbug {
  static public void main(String[] args) throws Exception {

    int me, tasks, i;
    int SIZE = 4;
    int[] s = new int[SIZE];
    int[] r = new int[SIZE];
    int tag = 87;
    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();
    Request req[] = new Request[SIZE];

    if (me == 0) {

      for (i = 0; i < SIZE; i++) {
	System.out.println("process " + me + "sending to 1");
	req[i] = MPI.COMM_WORLD.Issend(s, 0, SIZE, MPI.INT, 1, tag + i);
	System.out.println("process " + me + "sent to 1");
      }

      for (i = 0; i < SIZE; i++) {
	System.out.println("process " + me + "receving 1");
	MPI.COMM_WORLD.Recv(r, 0, SIZE, MPI.INT, 1, tag + i);
	System.out.println("process " + me + "received 1");
      }

      for (i = 0; i < SIZE; i++) {
	req[i].Wait();
      }

    } else {

      for (i = 0; i < SIZE; i++) {
	System.out.println("process " + me + "sending 0");
	req[i] = MPI.COMM_WORLD.Issend(s, 0, SIZE, MPI.INT, 0, tag + i);
	System.out.println("process " + me + " sent 0");
      }

      for (i = 0; i < SIZE; i++) {
	System.out.println("process " + me + "receiving 0");
	MPI.COMM_WORLD.Recv(s, 0, SIZE, MPI.INT, 0, tag + i);
	System.out.println("process " + me + "received 0");
      }

      for (i = 0; i < SIZE; i++) {
	req[i].Wait();
      }

    }
    System.out.println("process " + me + " finished");
    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
