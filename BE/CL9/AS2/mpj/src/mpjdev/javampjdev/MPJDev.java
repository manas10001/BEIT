/*
 The MIT License
 
 Copyright (c) 2005 - 2010
 1. Distributed Systems Group, University of Portsmouth (2005)
 2. Aamir Shafi (2005 - 2010)
 3. Bryan Carpenter (2005 - 2010)
 4. Mark Baker (2005 - 2010)
 
 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:
 
 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : MPJDev.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Mon Nov 1 12:22:15 BST 2004
 * Revision     : $Revision: 1.12 $
 * Updated      : $Date: 2005/12/05 11:58:20 $
 *
 */

package mpjdev.javampjdev;

import xdev.Device;
import xdev.ProcessID;
import org.apache.log4j.Logger ;
import mpjdev.*;

public class MPJDev {
    
    public static Comm WORLD = null;
    
    //TODO: I had to make it public because its being used from outside the package
    // in mpjdev.Request
    public static Device dev = null;
    
    static Logger logger = Logger.getLogger( "mpj" );
    
    public static Device init(String args[]) throws MPJDevException {
        
        if (args.length < 3) {
            
            throw new MPJDevException("Usage: " +
                                      "java MPJDev <myrank> <conf_file> <device_name>"
                                      +"conf_file can be, ../conf/xdev.conf <Local>"
                                      +"OR http://holly.dsg.port.ac.uk:15000/xdev.conf <Remote>");
            
        }
        
        /* we want multiple threads to see the same object */
      	synchronized (MPJDev.class) {
      	   if (dev == null) {
        	//dev = Device.newInstance(args[2]);
        	String device = args[2];

        	if (device.equals("niodev")) {
          		dev = new xdev.niodev.NIODevice();
        	} else if (device.equals("mxdev")) {
          		dev = new xdev.mxdev.MXDevice();
        	} else if (device.equals("smpdev")) {
          		dev = new xdev.smpdev.SMPDevice();
        	} else if (device.equals("hybdev")) {
            		dev = new xdev.hybdev.HYBDevice();
        	}else {
          throw new MPJDevException("No matching device found for <"
               + dev + ">");
        	}

      }
    }
        if (dev == null) {
            System.out.println("Specified device: " + args[2]);
            System.out.println("Available devices, niodev, smpdev, mxdev, hybdev");
            System.out.println("Error, cant execute, correct the device first");
            return dev ;
        }
        
        ProcessID[] ids = dev.init(args);
        ProcessID myID = dev.id();
        int myRank = -1;
        for(int i=0 ; i<ids.length ; i++) {
            if(myID.uuid().equals(ids[i].uuid())) {
                myRank = i ;
                break;
            }
        }
        WORLD = new Comm(dev, new Group(ids, myID, myRank ));
        return dev ;
    }
    
    /**
     * Gets the overhead incurred by send methods. It should be called
     * after calling #init(String[] args) method
     * @return int An integer specifying the overhead incurred by send methods
     */
    public static int getSendOverhead() {
        if(dev == null) {
            throw new MPJDevException("MPJDev should call init before getting "+
                                      "getSendOverhead()" ) ;
        }
        return dev.getSendOverhead() ;
    }
    
    /**
     * Gets the overhead incurred by recv methods. It should be called
     * after calling #init(String[] args) method
     * @return int Number of bytes overhead incurred by recv methods
     */
    public static int getRecvOverhead() {
        if(dev == null) {
            throw new MPJDevException("MPJDev should call init before getting "+
                                      "getRecvOverhead()" ) ;	    
        }
        return dev.getRecvOverhead() ;	  
    }
    
    public static void finish() throws MPJDevException {
        try { 
            dev.finish();
        }catch( xdev.XDevException xde) {
            throw new MPJDevException ( xde); 	    
        }
        
        dev = null;
    }
    
}
