import mpi.*;

public class Mpi_Advance{

    public static void main(String args[]){
        MPI.Init(args);
		int size = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank();
    }
}