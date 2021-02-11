package xdev;

import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;
import mpjbuf.*;
import mpjdev.*;
import xdev.mxdev.*;

public class Probe {

    public Probe(String args[]) throws Exception {
	/*
	 * args[0]=0
	 * args[1]=http://148.197.150.3:15000/mpjdev.conf
	 * args[2]=mxdev
	 */
        int DATA_SIZE = 2; 
        Object[] source = new Object[100];
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

        Object[] objArray = new Object[100];
	source[2] = null;
	source[3] = null;
	source[4] = null;

	if( Integer.parseInt(args[0]) == 0 ) {
           for(int j=0 ; j<100 ; j++) { 		
	     source[j] = vector;
	   }
           RawBuffer rawBuffer = BufferFactory.create(100);
           mpjbuf.Buffer sbuf = new mpjbuf.Buffer (rawBuffer, 
                                     sendOverhead,
                                     sendOverhead+100) ;
	   mpjdev.Request request ; 

	   sbuf.putSectionHeader( Type.INT) ;
	   sbuf.write(intArray, 0, intArray.length) ;
	   sbuf.commit();

	   //calling sleep ...
           try {  Thread.currentThread().sleep(1000); } catch(Exception e) { } 
	   
	   request = device.isend(sbuf, ids[1], 10, 10);
	   request.iwait();
	   sbuf.clear();
	   //Basic datatype case ..
	   
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 100);
           sbuf.commit();
           try {  Thread.currentThread().sleep(1000); } catch(Exception e) { } 
           request = device.isend(sbuf, ids[1], 11, 11);
	   request.iwait();
	   sbuf.clear();
	   // Java object case 

	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
           try {  Thread.currentThread().sleep(1000); } catch(Exception e) { } 
           request = device.isend(sbuf, ids[1], 13, 13);
	   request.iwait();
	   sbuf.clear();  
	   // ANY_TAG case ..
	   
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
           try {  Thread.currentThread().sleep(1000); } catch(Exception e) { } 
           request = device.isend(sbuf, ids[1], 12, 12);
	   request.iwait();
	   sbuf.clear();  

	   // ANY_SRC case 
	   sbuf.putSectionHeader(Type.OBJECT);
           sbuf.write(source, 0, 1);
           sbuf.commit();
           try {  Thread.currentThread().sleep(1000); } catch(Exception e) { } 
           request = device.isend(sbuf, ids[1], 14, 14);
	   request.iwait();
	   sbuf.clear();  
	   // ANY_TAG and ANY_SRC case ...
	   
	   BufferFactory.destroy(rawBuffer);
		   
	} else if ( Integer.parseInt(args[0]) == 1) {
	   source[0] = null;
	   source[1] = null;
           RawBuffer rawBuffer = BufferFactory.create(100);
           mpjbuf.Buffer rbuf = new mpjbuf.Buffer (rawBuffer, recvOverhead,
                                                    recvOverhead+100) ;
           mpjdev.Status status = new mpjdev.Status();
	   mpjdev.Request request ;
	   Type sectionHeader ;
	   int sectionSize ; 
            
	   // this will block until a message has been received ...
	   //System.out.println(" calling probe "); 
	   status = device.probe( ids[0],10,10); 
	   //System.out.println(" called probe "); 
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);

           request = device.irecv(rbuf, ids[0], 
                                    10, 10, status);	
	   mpjdev.Status ret_status = request.iwait();
	   System.out.println("status.srcID "+ret_status.srcID);
	   System.out.println("status.tag "+ret_status.tag);
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
	   
	   status = device.probe( ids[0],11,11); 
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status1 = new mpjdev.Status();
           request = device.irecv(rbuf, ids[0],11,11,status1);
	   mpjdev.Status ret_status1 = request.iwait();
	   System.out.println("status.srcID "+ret_status1.srcID);
	   System.out.println("status.tag "+ret_status1.tag);
           rbuf.commit();
           rbuf.getSectionHeader();
           sectionSize = rbuf.getSectionSize (); 
           //System.out.println("sectionSize "+sectionSize);
           rbuf.read(source, 0, 100);

           vector1 = (java.util.Vector) source[0];

           if (vector1.equals(vector)) {
             System.out.println("**************************");		   
             System.out.println("Object case passed");
             System.out.println("**************************");		   
           } else {
             System.out.println("Object case failed");
	   }

	   status = device.probe(ids[0], Device.ANY_TAG, 13); 
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status3 = new mpjdev.Status() ; 
           request = device.irecv(rbuf, ids[0], Device.ANY_TAG, 
			   13, status3);
	   mpjdev.Status ret_status3 = request.iwait();
	   System.out.println("status.srcID "+ret_status3.srcID);
	   System.out.println("status.tag "+ret_status3.tag);
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
	   
	   status = device.probe(Device.ANY_SRC, 12, 12); 
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status2 = new mpjdev.Status() ; 
           request = device.irecv(rbuf, Device.ANY_SRC,  
			   12, 12, status2);
	   mpjdev.Status ret_status2 = request.iwait();
	   System.out.println("status.srcID "+ret_status2.srcID);
	   System.out.println("status.tag "+ret_status2.tag);
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

	   status = device.probe(Device.ANY_SRC, Device.ANY_TAG, 14); 
	   System.out.println("status.srcID "+status.srcID);
	   System.out.println("status.tag "+status.tag);
	   mpjdev.Status status4 = new mpjdev.Status() ; 
           request = device.irecv(rbuf, Device.ANY_SRC,
			   Device.ANY_TAG, 
			   14,status4);
	   mpjdev.Status ret_status4 = request.iwait();
	   System.out.println("status.srcID "+ret_status4.srcID);
	   System.out.println("status.tag "+ret_status4.tag);
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

           BufferFactory.destroy(rawBuffer);
	}
	device.finish();

    }
    
    public static void main(String args[]) throws Exception{
    	Probe test = new Probe(args);
    }

}
