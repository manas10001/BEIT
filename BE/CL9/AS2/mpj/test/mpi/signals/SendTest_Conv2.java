package mpi.signals;

import java.util.*;
import mpi.*;
import java.io.*;

// Test case contributed by Sivakumar Venkata Pabolu.

/**
 * Test program to emulate Pool of tasks....
 * need to get back to this soon.
 */
public class SendTest_Conv2{

    public static void bugTestRank0(String proc_name,int npes,int flag){

	Object[] t = new Object[2];

	/*
	  if(flag == 0)
	  MyLinkList[] t = new MyLinkList[2];
	  else
	  MyLinkList1[] t = new MyLinkList1[2];
	*/
	
	for(int i=0;i<2;i++){
	    
	    if(flag == 0)
		t[i] = new MyLinkList(1000,proc_name);   // Modified, dbc.
		//t[i] = new MyLinkList(20,proc_name);
	    else
		t[i] = new MyLinkList1(1000,proc_name);   // Modified, dbc.
		//t[i] = new MyLinkList1(20,proc_name);
	    
	    System.out.println("Created Task["+i+"] : \n "+t[i]);
	}
	
	for(int i=1;i<npes;i++){
	    System.out.println("Sending Task["+(i-1)+"]: \n "+ t[i-1]);

	    SendTest_Conv.Send(t[i-1],i,0);
	    
	}
	
	Object[] t2 = new Object[2];

	/*
	  if(flag ==0)
	  MyLinkList[] t2 = new MyLinkList[2];
	  else
	  MyLinkList1[] t2 = new MyLinkList1[2];
	*/

	for(int i=1;i<npes;i++){
	    System.out.println("waiting to hear from proc "+ (i-1));
	    
	    if(flag == 0){
		t2[i-1]= (MyLinkList)SendTest_Conv.Recv(i,0);		
		System.out.println("Recieved : "+ (MyLinkList) t2[i-1]);
	    }else{ 
		t2[i-1]= (MyLinkList1)SendTest_Conv.Recv(i,0);	
	        System.out.println("Recieved : "+ (MyLinkList1) t2[i-1]);
	    }
	}
	
	System.out.println("Recieved Items: ");
	
	
	for(int i=1;i<npes;i++)
	    System.out.println(t2[i-1]);
	
	System.out.println("Exiting....");
	
    }
    
    public static void bugTestRank12(int flag,int my_pe){
	
	Object[] inbuf= new Object[1];
        
	System.out.println("before Recv on proc no "+my_pe);
	
	try{
	    
	    inbuf[0]= SendTest_Conv.Recv(0,0);
	    
	    if(flag==0)
		System.out.println("Recieved : " + (MyLinkList)inbuf[0] +" on " +my_pe);
	    else
		System.out.println("Recieved : " + (MyLinkList1)inbuf[0] +" on " +my_pe);
	}catch(Exception e){
	    System.err.println(e);
	}
	
	SendTest_Conv.Send(inbuf[0],0,0);
    }
    
    public static void main(String[] args) throws MPIException{
    }

    public SendTest_Conv2() {
    }

    public SendTest_Conv2(String[] args) throws Exception {	    
	
	MPI.Init(args);
	
	int my_pe = MPI.COMM_WORLD.Rank(); 	
	int npes = MPI.COMM_WORLD.Size();	

	if(npes != 2) {
	  if(my_pe == 0) 	
            System.out.println("signals->SendTest_Conv2 runs with 2 tasks");

	  MPI.COMM_WORLD.Barrier();
	  MPI.Finalize();
	  return;	  
	}
	
	if(my_pe==0){
	    //String proc_name = MPI.Get_processor_name();
	    String proc_name = "mpiJava" ;   // Modified, dbc
	    SendTest_Conv2.bugTestRank0(proc_name,npes,0);
	}else{
	    SendTest_Conv2.bugTestRank12(0,my_pe);
	}

	MPI.COMM_WORLD.Barrier();
	if(my_pe==0){
	    System.out.println("############################");
	    System.out.println("Starting next test");
	    System.out.println("############################");
	    //String proc_name = MPI.Get_processor_name();
	    String proc_name = "mpiJava" ;   // Modified, dbc
	    SendTest_Conv2.bugTestRank0(proc_name,npes,1);
	}else{
	    SendTest_Conv2.bugTestRank12(1,my_pe);
	}

	/*
	if(my_pe == 0){
	    String proc_name = MPI.Get_processor_name();

	    //Job[] j = new Job[2];
	    //Task[] t = new TestTask[2];
	    MyLinkList[] t = new MyLinkList[2];

	    for(int i=0;i<2;i++){
		//j[i] = new Job(new TestTask(proc_name),new TestResult());
		//t[i] = new TestTask(proc_name);
		t[i] = new MyLinkList(20,proc_name);
		//System.out.println("Created job["+i+"] : \n "+j[i]);
		System.out.println("Created Task["+i+"] : \n "+t[i]);
	    }

	    for(int i=1;i<npes;i++){
		//System.out.println("Sending Job["+(i-1)+"]: \n "+ j[i-1]);
		System.out.println("Sending Task["+(i-1)+"]: \n "+ t[i-1]);
		
		//MPI.COMM_WORLD.Send(t,i-1,1,MPI.OBJECT,i,0);
		
		//SendTest_Conv.Send(j[i-1],i,0);
		SendTest_Conv.Send(t[i-1],i,0);

	    }

	    //recv loop
	    
	    //Job[] j2 = new Job[2];
	    //Task[] t2 = new TestTask[2];
	    MyLinkList[] t2 = new MyLinkList[2];

	    for(int i=1;i<npes;i++){
		System.out.println("waiting to hear from proc "+ (i-1));
		
		//MPI.COMM_WORLD.Recv(t2,i-1,1,MPI.OBJECT,i,0);
		
		//j2[i-1]= (Job)SendTest_Conv.Recv(i,0);
		//t2[i-1]= (TestTask)SendTest_Conv.Recv(i,0);
		t2[i-1]= (MyLinkList)SendTest_Conv.Recv(i,0);
	    }
	    
	    System.out.println("Recieved Items: ");
	    for(int i=1;i<npes;i++)
		//System.out.println(j2[i-1]);
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

		//System.out.println(my_pe+" : " +stat.Get_count(MPI.OBJECT)); 
		
		//System.out.println("Recieved : " + (Job)inbuf[0] +" on " +my_pe);
		//System.out.println("Recieved : " + (TestTask)inbuf[0] +" on " +my_pe);
		System.out.println("Recieved : " + (MyLinkList)inbuf[0] +" on " +my_pe);
		
	    }catch(Exception e){
		System.err.println(e);
	    }
	    
	    String s = " "+ my_pe;
	    
	    //((TestTask)inbuf[0]).setAck(new String(s));
	    //((TestTask)inbuf[0]).setAck(new String(s));
	    //System.out.println( (MyLinkList)inbuf[0]);

	    //MPI.COMM_WORLD.Send(inbuf,0,1,MPI.OBJECT,0,0);

	    SendTest_Conv.Send(inbuf[0],0,0);

	}
	*/

	MPI.Finalize();
    }
}








