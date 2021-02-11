package mpi.signals;

import java.util.*;
import mpi.*;
import java.io.*;

/**
 * Test program to emulate Pool of tasks....
 * need to get back to this soon.
 */

public class SendTest_Conv{
    
    /**
     * Serializes the given object and returns it as a byte aray
     */
    public static byte[] objToByte(Object obje){

	System.out.println("objToByte called");
	try{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    System.out.println("before writeObject in objToByte");
	    oos.writeObject(obje);
	    System.out.println("before flush in objToByte");
	    oos.flush(); // just to be sure;
	    byte[] objectBytes = baos.toByteArray();
	    System.out.println("objToByte Successful");
	    baos.close();
	    oos.close();
	    return objectBytes;
	}catch(Exception e){
	    e.printStackTrace();
	}
	System.err.println("error in SendTest_conv.objToByte()");
	return null;
    }

    /**
     * Deserializes a byte array into an object
     */
    public static Object byteToObject(byte[] bin){
	
	try{
	    ByteArrayInputStream bais = new ByteArrayInputStream(bin);
	    ObjectInputStream ois = new ObjectInputStream(bais);
	    Object obj_in = ois.readObject();
	    ois.close();
	    bais.close();
	    System.out.println("byteToObj Successful");
	    return obj_in;
	}catch(Exception e){
	    e.printStackTrace();
	}
	
	System.err.println("error in SendTest_conv.byteToObj()");
	return null;
    }

    /**
     *  Custom implementation of blocking send for objects.
     */
    public static void Send(Object obj,int dest,int flag){
	byte[] b = objToByte(obj);
	int[] len = {b.length};
	try{
	    MPI.COMM_WORLD.Send(len,0,1,MPI.INT,dest,flag);
	    MPI.COMM_WORLD.Send(b,0,len[0],MPI.BYTE,dest,flag);	
	}catch(Exception mpie){
	    mpie.printStackTrace();
	}
    }

    /**
     *  Custom implementation of blocking Recv for objects
     */
    public static Object Recv(int src,int flag){
	try{
	    int[] len = { 0 }; 
	    MPI.COMM_WORLD.Recv(len,0,1,MPI.INT,src,flag);
	    System.out.println("expecting b_arr of len : "+ len[0]);
	    byte[] br = new byte[len[0]];
	    MPI.COMM_WORLD.Recv(br,0,len[0],MPI.BYTE,src,flag);
	    return SendTest_Conv.byteToObject(br);
	}catch(Exception mpie){
	    mpie.printStackTrace();
	}
	System.err.println("Error in Recv");
	return null;
    }

    /**
     * Custom implementation of blocking Send for Objects
     */
    public static void Send(Object[] obj_arr,int offset,int no_items,int dest,int flag){
	for(int i=offset;i<offset+no_items;i++)
	    Send(obj_arr[i],dest,flag);
    }

    /**
     * Custom implementation of blocking Recv for Objects
     */
    public static void Recv(Object[] obj_arr,int offset,int no_items,int src,int flag){
	for(int i=offset;i<offset+no_items;i++)
	    obj_arr[i]= Recv(src,flag);
    }


    public static void main(String[] args) throws MPIException{
	
	MPI.Init(args);
	
	int my_pe = MPI.COMM_WORLD.Rank(); 	
	int npes = MPI.COMM_WORLD.Size();	
						
	if(my_pe == 0){
	    String proc_name = MPI.Get_processor_name();

	    //Job[] j = new Job[2];
	    Task[] t = new TestTask[2];
	    //MyLinkList[] t = new MyLinkList[2];

	    for(int i=0;i<2;i++){
		t[i] = new TestTask(proc_name);
		//t[i] = new MyLinkList(1,proc_name);
		System.out.println("Created Task["+i+"] : \n "+t[i]);
	    }

	    for(int i=1;i<npes;i++){
		System.out.println("Sending Task["+(i-1)+"]: \n "+ t[i-1]);
		
		//MPI.COMM_WORLD.Send(t,i-1,1,MPI.OBJECT,i,0);

		SendTest_Conv.Send(t[i-1],i,0);

		/*
		  byte[] b = SendTest_Conv.objToByte(t[i-1]);
		  int[] len = {b.length};
		  MPI.COMM_WORLD.Send(len,0,1,MPI.INT,i,0);
		  MPI.COMM_WORLD.Send(b,0,len[0],MPI.BYTE,i,0);
		*/
	    }

	    //recv loop
	    
	    Task[] t2 = new TestTask[2];
	    //MyLinkList[] t2 = new MyLinkList[2];

	    for(int i=1;i<npes;i++){
		System.out.println("waiting to hear from proc "+ (i-1));
		
		//MPI.COMM_WORLD.Recv(t2,i-1,1,MPI.OBJECT,i,0);
		
		t2[i-1]= (TestTask)SendTest_Conv.Recv(i,0);

		/*
		  int[] len = { 0 }; 
		  MPI.COMM_WORLD.Recv(len,0,1,MPI.INT,i,0);
		  System.out.println("expecting b_arry of len : "+ len[0] +" on "+my_pe);
		  byte[] b = new byte[len[0]];
		  MPI.COMM_WORLD.Recv(b,0,len[0],MPI.BYTE,i,0);
		  t2[i-1] = (TestTask)SendTest_Conv.byteToObject(b);
		*/
	    }
	    
	    System.out.println("Recieved Items: ");
	    for(int i=1;i<npes;i++)
		System.out.println(t2[i-1]);
	    
	    System.out.println("Exiting....");
	    
	}else{
	    // recieve task object , 
	    // call the run method , 
	    // return the result object
	    
	    Object[] inbuf= new Object[1];
	    //Job[] jin = new Job[1];
	    
	    System.out.println("before Recv on proc no "+my_pe);
	    
	    //Task t = null;

	    //recieving the task
	    try{

		//Status stat = MPI.COMM_WORLD.Recv(inbuf,0,1,MPI.OBJECT,0,0);
		//if(stat.Test_cancelled())
		//    System.out.println("Communication was cancelled");

		inbuf[0]= SendTest_Conv.Recv(0,0);

		/*
		  int[] len = { 0 }; 
		  MPI.COMM_WORLD.Recv(len,0,1,MPI.INT,0,0);
		  System.out.println("expecting b_arry of len : "+ len[0] +" on "+my_pe);
		  byte[] b = new byte[len[0]];
		  MPI.COMM_WORLD.Recv(b,0,len[0],MPI.BYTE,0,0);
		  inbuf[0] = SendTest_Conv.byteToObject(b);
		*/
		//System.out.println(my_pe+" : " +stat.Get_count(MPI.OBJECT)); 
		
		System.out.println("Recieved : " + (TestTask)inbuf[0] +" on " +my_pe);
		
	    }catch(Exception e){
		System.err.println(e);
	    }
	    
	    String s = " "+ my_pe;
	    
	    ((TestTask)inbuf[0]).setAck(new String(s));
	    //System.out.println( (MyLinkList)inbuf[0]);

	    //MPI.COMM_WORLD.Send(inbuf,0,1,MPI.OBJECT,0,0);

	    SendTest_Conv.Send(inbuf[0],0,0);

	    /*
	      byte[] b = SendTest_Conv.objToByte(inbuf[0]);
	      int[] len = {b.length};
	      MPI.COMM_WORLD.Send(len,0,1,MPI.INT,0,0);
	      MPI.COMM_WORLD.Send(b,0,len[0],MPI.BYTE,0,0);
	    */
	}

	MPI.Finalize();
    }
}








