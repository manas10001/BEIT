import mpi.*;
import java.util.Scanner;
import java.io.*;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class Mpi_Advance {
    public static void main(String[] args) {

        MPI.Init(args);
        File file;
        Scanner sc = null;

        int root = 0;
        int data_size = 0;
        int chunk = 0;
        double data_ar[];

        int size = MPI.COMM_WORLD.Size();
        int rank = MPI.COMM_WORLD.Rank();

        double collect[] = new double[size];

        try {
            file = new File("./input.txt");
            sc = new Scanner(file);
            //1st line has size of data in int
            data_size = sc.nextInt();
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(0);
        }

        data_ar = new double[data_size];

        //amount of data to be divided in processrs
        chunk = data_size / size;

        if (rank == root) {
            try {
                int counter = 0;

                //read data in array
                while (sc.hasNextDouble() && counter < data_size) {
                    data_ar[counter++] = sc.nextDouble();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        double recieve_buffer[] = new double[chunk];

        //for time calculation using nanoTime method
        long starttime = System.nanoTime();

        //Scatter(senddata,start,end,datatype,receivingtargetdata,start,end,datatype,source process)
        MPI.COMM_WORLD.Scatter(data_ar,0,chunk,MPI.DOUBLE,recieve_buffer,0,chunk,MPI.DOUBLE,root);

        BigDecimal sum = new BigDecimal(0);;

        for(int i=0;i<chunk;i++){

            sum = sum.add(BigDecimal.valueOf(recieve_buffer[i]));
            // System.out.println("Adding "+recieve_buffer[i] + "to sum sum=" + sum+" by:"+rank );
            // double sqrt = Math.sqrt(recieve_buffer[i]);  
        }

        double gather[] = new double[1];
        gather[0] = sum.doubleValue();


        //Gather(receivingtargetdata,start,end,datatype,send,start,end,datatype,source process)
        MPI.COMM_WORLD.Gather(gather,0,1,MPI.DOUBLE,collect,0,1,MPI.DOUBLE,root);

        if(rank == root)
        {

            

            System.out.println("Sum of array: ");
            BigDecimal fsum = new BigDecimal(0);
            for(int i = 0; i< size; i++){
                fsum = fsum.add(BigDecimal.valueOf(collect[i]));
                // System.out.print(collect[i]+"\t");
                // printWriter.println(i);
            }
            System.out.println(fsum);
            long elapsedtime = System.nanoTime() - starttime;
            System.out.println("Time taken: "+(elapsedtime/1000000)+"ms");
        }
        MPI.Finalize();
    }
}
