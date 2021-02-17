import mpi.*;
import java.util.*;
public class Assignment2 {
    public static void main(String[] args) {
        
        //Initializing the MPI Environment
        MPI.Init(args);

        int size = MPI.COMM_WORLD.Size();
        int rank = MPI.COMM_WORLD.Rank();

        //the rank of root process will always be 0
        int root = 0;

        //creating array which will consist the data to be sent using mpi
        int send[] = new int[size];

        Scanner s = null;

        //the root process will consist of full array which we want to send;
        if(rank == root)
        {
            System.out.println("Enter the number to be sent: ");
            send[0] = 1;
            send[1] = 2;
            send[2] = 3;
            send[3] = 4;
            send[4] = 5;
        }
        int receive[] = new int[1];

        //scatter operation shares the data of root to all other processes of communicator
        MPI.COMM_WORLD.Scatter(send,0,1,MPI.INT,receive,0,1,MPI.INT,root);

        //printing which process has what data
        System.out.println("Process ["+rank+"] has data: "+receive[0]);
        //squaring the numbers
        receive[0] = receive[0] * receive[0];

        //gather operation is inverse of the scatter, takes the data from where they recieved and sends back to root process
        MPI.COMM_WORLD.Gather(receive,0,1,MPI.INT,send,0,1,MPI.INT,root);

        //displaying the data contained by root process after gather operation
        if(rank == root)
        {
            System.out.println("The root ["+rank+"] process with squared numbers: ");
            for(int i: send)
            {
                System.out.print(i+"\t");
            }
        }
    }
}
