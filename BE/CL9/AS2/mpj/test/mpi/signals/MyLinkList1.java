package mpi.signals;

import java.io.*;

public class MyLinkList1 implements Serializable{

    public MyLinkList1 next = null ;
    public String label = null ;
    public int[] bintern = {42};
    //public String[] s = {"parthee","sunil"};
    //public String s = "parthee";

    public MyLinkList1(){
	
    }

    public MyLinkList1(String s){
	this.label = s ;
    }

    //creates a link list of size n
    public MyLinkList1(int n,String s ){
	this.label = s;
	MyLinkList1 curr = this;
	
	for(int i=0;i<n;i++){
	    String so = "Name"+i;
	    curr.next = new MyLinkList1(so);
	    curr = curr.next ;
	}

    }

    public String toString(){
	return this.label+"->"+this.next;
    }

    public static void main(String[] args){
	int listsize = 3;
	if(args.length > 0 ) 
	    listsize = Integer.parseInt(args[0]);

	String watev = "siva";
	MyLinkList1 mll = new MyLinkList1(listsize,watev);
	System.out.println(mll);

	//ripping into bytes 
	byte[] b = SendTest_Conv.objToByte(mll);
	
	System.out.println("Converted into byte array of size :"+b.length);
	
	Object o = SendTest_Conv.byteToObject(b);
	
	System.out.println("Before Class cast");
	
	MyLinkList1 mll2 = (MyLinkList1)o;
	
	System.out.println(mll2+"\n---------------------------");
	
	Job[] j = new Job[2];
	    
	for(int i=0;i<2;i++) {
	    j[i] = new Job(new TestTask(watev),new TestResult());
    
	    b = SendTest_Conv.objToByte(j[i]);
	    
	    System.out.println("After serializing Job");
	    
	    System.out.println("b.length = "+b.length);
	    
	    o = SendTest_Conv.byteToObject(b);

	    System.out.println("Recreated : \n "+(Job)o);
	}
    }
    
}







