package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;
import java.nio.ByteBuffer ;

public class Bsend_test2 {
	
  public Bsend_test2() {
  }
  
  public Bsend_test2(String args[]) throws Exception{
		
      MPI.Init(args);

      int me = MPI.COMM_WORLD.Rank();

      int intArray [] = new int[100];     
      float floatArray [] = new float[100];  
      double doubleArray [] = new double[100];
      long longArray [] = new long[100];		
      boolean booleanArray [] = new boolean[100];
      short shortArray [] = new short[100];
      char charArray [] = new char[100];
      byte byteArray [] = new byte[100];
      int intReadArray [] = new int[100];
      float floatReadArray [] = new float[100];
      double doubleReadArray [] = new double[100];
      long longReadArray [] = new long[100];
      boolean booleanReadArray [] = new boolean[100];
      short shortReadArray [] = new short[100];
      char charReadArray [] = new char[100];
      byte byteReadArray [] = new byte[100];
      
      for(int i =0;i < intArray.length;i++) {
        intArray[i] = i+1;
	floatArray[i] = i+11;
	doubleArray[i] = i+11.11;
	longArray[i] = i+11;
	booleanArray[i] = true;
	shortArray[i] = 1;
	charArray[i] = 's';
	byteArray[i] = 's';
	intReadArray[i] = 3;
	floatReadArray[i] = i+19;
	doubleReadArray[i] = i+99.11;
	longReadArray[i] = i+9;
	shortReadArray[i] = 2;
	booleanReadArray[i] = false;
	charReadArray[i] = 'x';
	byteReadArray[i] = 'x';
      }    	
      
      if(MPI.COMM_WORLD.Rank() == 0) {			
        ByteBuffer buf = ByteBuffer.allocateDirect ( 10000); 
        MPI.Buffer_attach(buf);
        MPI.COMM_WORLD.Bsend(intArray,0,100,MPI.INT,1,999);
	MPI.COMM_WORLD.Bsend(byteArray,0,100,MPI.BYTE,1,998);
	MPI.COMM_WORLD.Bsend(charArray,0,100,MPI.CHAR,1,997);
	MPI.COMM_WORLD.Bsend(doubleArray,0,100,MPI.DOUBLE,1,996);
	MPI.COMM_WORLD.Bsend(longArray,0,100,MPI.LONG,1,995);
	MPI.COMM_WORLD.Bsend(booleanArray,0,100,MPI.BOOLEAN,1,994);
	MPI.COMM_WORLD.Bsend(shortArray,0,100,MPI.SHORT,1,993);
	MPI.COMM_WORLD.Bsend(floatArray,0,100,MPI.FLOAT,1,992);
	//System.out.println("Send Completed \n\n");
        MPI.Buffer_detach(); 
      } else if (MPI.COMM_WORLD.Rank() == 1) {	
        //try { Thread.currentThread().sleep(1000); }catch(Exception e){}
	MPI.COMM_WORLD.Recv(intReadArray,0,100,MPI.INT,0,999);
	MPI.COMM_WORLD.Recv(byteReadArray,0,100,MPI.BYTE,0,998);
	MPI.COMM_WORLD.Recv(charReadArray,0,100,MPI.CHAR,0,997);        	
	MPI.COMM_WORLD.Recv(doubleReadArray,0,100,MPI.DOUBLE,0,996);        	
	MPI.COMM_WORLD.Recv(longReadArray,0,100,MPI.LONG,0,995);        	
	MPI.COMM_WORLD.Recv(booleanReadArray,0,100,MPI.BOOLEAN,0,994);        	
	MPI.COMM_WORLD.Recv(shortReadArray,0,100,MPI.SHORT,0,993);        	
	MPI.COMM_WORLD.Recv(floatReadArray,0,100,MPI.FLOAT,0,992);

        if(Arrays.equals(intArray,intReadArray) && 
 	   Arrays.equals(floatArray,floatReadArray) &&
	   Arrays.equals(doubleArray,doubleReadArray) && 
	   Arrays.equals(longArray,longReadArray) &&
	   Arrays.equals(shortArray,shortReadArray) && 
	   Arrays.equals(charArray,charReadArray) &&
	   Arrays.equals(byteArray,byteReadArray) && 
	   Arrays.equals(booleanArray,booleanReadArray)) {
		
	   System.out.println("\n#################"+
			      "\n <<<<PASSED>>>> "+
			      "\n################");
	        
	} else {
	   System.out.println("\n#################"+
	 		      "\n <<<<FAILED>>>> "+
			      "\n################");			
	}
      }
      
      
      
      if(MPI.COMM_WORLD.Rank() == 1) {
        System.out.println("Bsend_test2 TEST Completed");
      }
      MPI.COMM_WORLD.Barrier();      
      MPI.Finalize();
  
  }
    
    public static void main(String args[]) throws Exception{
    	Bsend_test2 test = new Bsend_test2(args);
    }
}
