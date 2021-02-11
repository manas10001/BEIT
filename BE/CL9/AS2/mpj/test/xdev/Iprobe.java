package xdev;

import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;
import mpjbuf.*;
import mpjdev.*;
import xdev.mxdev.*;

public class Iprobe {

    public Iprobe(String args[]) throws Exception {
	/*
	 * args[0]=0
	 * args[1]=http://148.197.150.3:15000/mpjdev.conf
	 * args[2]=mxdev
	 */
        int DATA_SIZE = 2; 
        Object[] source = new Object[5];
        //xdev.Device device = xdev.Device.newInstance("mxdev");	    
        xdev.Device device = MPJDev.init(new String[]{"","","mxdev"});
	ProcessID[] ids = device.init(args);
        int sendOverhead = device.getSendOverhead();
        int recvOverhead = device.getRecvOverhead();
 	    
	int intArray [] = new int[DATA_SIZE];       		
	int intReadArray [] = new int[DATA_SIZE];
		
	for(int i =0;i < intArray.length;i++) {
	    intArray[i] = i+1;
 	    intReadArray[i] = 3;
	} 

        Vector vector1 = null;
	Vector vector = new Vector();

        for (int i = 0; i < 10; i++) {
          vector.add(i + "");
        }

        Object[] objArray = new Object[5];
	source[2] = null;
	source[3] = null;
	source[4] = null;

	if( Integer.parseInt(args[0]) == 0 ) {
	   source[0] = vector;
	   source[1] = vector;
           RawBuffer rawBuffer = BufferFactory.create(100);
           mpjbuf.Buffer sbuf = new mpjbuf.Buffer (rawBuffer, 
                                     sendOverhead,
                                     sendOverhead+100) ;
	   
	   // simple case ..
	   sbuf.putSectionHeader( Type.INT) ;
	   sbuf.write(intArray, 0, intArray.length) ;
	   sbuf.commit();
	   try {Thread.currentThread().sleep(1000); }catch(Exception e){}
	   device.send(sbuf, ids[1], 10, 10);
	   sbuf.clear();

	   // Java object case ..
           sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
	   try {Thread.currentThread().sleep(1000); }catch(Exception e){}
           device.send(sbuf, ids[1], 11, 11);
	   sbuf.clear();
	   
	   // ANY_TAG case ..
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
	   try {Thread.currentThread().sleep(1000); }catch(Exception e){}
           device.send(sbuf, ids[1], 13, 13);
	   //request.iwait();
	   sbuf.clear();  
	   
	   // ANY_SRC case 
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
	   try {Thread.currentThread().sleep(1000); }catch(Exception e){}
           sbuf.commit();
           device.send(sbuf, ids[1], 12, 12);
	   //request.iwait();
	   sbuf.clear();  

	   // ANY_TAG and ANY_SRC case ...
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
	   try {Thread.currentThread().sleep(1000); }catch(Exception e){}
           device.send(sbuf, ids[1], 14, 14);
	   //request.iwait();
	   sbuf.clear();  

           //.. .. 

           BufferFactory.destroy(rawBuffer);
		   
	} else if ( Integer.parseInt(args[0]) == 1) {
	   source[0] = null;
	   source[1] = null;
           RawBuffer rawBuffer = BufferFactory.create(100);
           mpjbuf.Buffer rbuf = new mpjbuf.Buffer (rawBuffer, recvOverhead,
                                                    recvOverhead+100) ;
           mpjdev.Status status = null; //new mpjdev.Status();
	   Type sectionHeader = null;
	   int sectionSize ; 
	   
	   do { 
             status = device.iprobe(ids[0],10,10); 
	   } while(status == null); 

	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   //simple case ..
           status = device.recv(rbuf, ids[0],  10, 10);
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   rbuf.commit();
	   sectionHeader = rbuf.getSectionHeader();
	   sectionSize = rbuf.getSectionSize();
	   rbuf.read( intReadArray,0,intArray.length);
	   rbuf.clear();
	   
	   if(Arrays.equals(intArray,intReadArray)) {
               System.out.println("\n#################"+
	       	       	          "\n <<<<PASSED>>>> "+
	           		  "\n################");
	   } else {
	       System.out.println("\n#################"+
	  		          "\n <<<<FAILED>>>> "+
				  "\n################");			
	   }

	   //Java object case ..
	   do { 
             status = device.iprobe(ids[0],11,11); 
	   } while(status == null); 

	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
           status = device.recv(rbuf, ids[0],11,11);
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
           rbuf.commit();
           rbuf.getSectionHeader();
           sectionSize = rbuf.getSectionSize (); 

           rbuf.read(source, 0, 1);

           vector1 = (java.util.Vector) source[0];

           if (vector1.equals(vector)) {
             System.out.println("Object case passed");
           } else {
             System.out.println("Object case failed");
	   }

	   //.. .. 
	   do { 
             status = device.iprobe(ids[0],Device.ANY_TAG,13); 
	   } while(status == null); 

	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status3 = new mpjdev.Status() ; 
           status3 = device.recv(rbuf, ids[0], Device.ANY_TAG, 
			   13);
	   //mpjdev.Status ret_status3 = request.iwait();
	   System.out.println("status.srcID "+status3.srcID);
	   System.out.println("status.tag "+status3.tag);
           rbuf.commit();
           rbuf.getSectionHeader();
           sectionSize = rbuf.getSectionSize (); 
           rbuf.read(source, 0, 1);

           vector1 = (java.util.Vector) source[0];

           if (vector1.equals(vector)) {
             System.out.println("**************************");		   
             System.out.println("Object case passed ANY_TAG ");
             System.out.println("**************************");		   
           } else {
             System.out.println("Object case failed");
	   }
	   
	   do { 
             status = device.iprobe(Device.ANY_SRC,12,12); 
	   } while(status == null); 

	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status2 = new mpjdev.Status() ; 
           status2 = device.recv(rbuf, Device.ANY_SRC,  
			   12, 12);
	   //mpjdev.Status ret_status2 = request.iwait();
	   System.out.println("status.srcID "+status2.srcID);
	   System.out.println("status.tag "+status2.tag);
           rbuf.commit();
           rbuf.getSectionHeader();
           sectionSize = rbuf.getSectionSize (); 
           rbuf.read(source, 0, 1);

           vector1 = (java.util.Vector) source[0];

           if (vector1.equals(vector)) {
             System.out.println("**************************");		   
             System.out.println("Object case passed ANY_SRC ");
             System.out.println("**************************");		   
           } else {
             System.out.println("Object case failed");
	   }

	   do { 
             status = device.iprobe(Device.ANY_SRC,Device.ANY_TAG,14); 
	   } while(status == null); 

	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status4 = new mpjdev.Status() ; 
           status4 = device.recv(rbuf, Device.ANY_SRC,
			   Device.ANY_TAG, 
			   14);
	   //mpjdev.Status ret_status4 = request.iwait();
	   System.out.println("status.srcID "+status4.srcID);
	   System.out.println("status.tag "+status4.tag);
           rbuf.commit();
           rbuf.getSectionHeader();
           sectionSize = rbuf.getSectionSize (); 
           rbuf.read(source, 0, 1);

           vector1 = (java.util.Vector) source[0];

           if (vector1.equals(vector)) {
             System.out.println("**************************");		   
             System.out.println("Object case passed ANY_TAG && ANY_SRC ");
             System.out.println("**************************");		   
           } else {
             System.out.println("Object case failed");
	   }

          // .. .. 

           BufferFactory.destroy(rawBuffer);
	}

	device.finish();

    }
    
    public static void main(String args[]) throws Exception{
    	Iprobe test = new Iprobe(args);
    }

}
