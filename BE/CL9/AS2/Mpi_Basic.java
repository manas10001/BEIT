import mpi.*;

public class Mpi_Basic{
	public static void main(String arg[]){
		MPI.Init(arg);
		int size = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank();


		System.out.println("Process: " + rank);

		MPI.Finalize();
	}

}
