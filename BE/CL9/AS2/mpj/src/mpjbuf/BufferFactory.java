/*
The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
/*
 * File         : BufferFactory.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.13 $
 * Updated      : $Date: 2005/08/12 19:12:25 $
 *    
 */
package mpjbuf ; 
import mpi.MPIException ;

public abstract class BufferFactory {
	
    static BufferFactory factory ;	

    static {
      init("Buddy1") ;  	    
    }

    /**
     * Initialize BufferFactory.
     * @param str The current options are 'Buddy1' and 'Buddy2' 
     */ 
    public static void init (String str) {

      if(str.equals("Buddy1")) {
        if(factory == null) {	      
          factory = new Buddy1BufferFactory() ;  	      
	  factory.init() ;
	}
	else {
          System.out.println(" attempt to initialize bufferFactory again" + 
			  " with strategy represented by string <"+str+">"); 
	}
      } 
      else if(str.equals("Buddy2")) {
        if(factory == null) {	      
          factory = new Buddy2BufferFactory() ;  	      
	  factory.init() ;
	}
	else {
          System.out.println(" attempt to initialize bufferFactory again" + 
			  " with strategy represented by string <"+str+">"); 
	}
      } 
      else {
        throw new MPIException (" No matching buffering strategy for <"+
			str+">");
      }

    }

    /**
     * Create a buffer 
     * @param size Size of the buffer in bytes. The user need to care 
     * about headers and all that kind of stuff ..
     */ 
    public static mpjbuf.RawBuffer create(int size)  {
      if(factory != null) { 	    
        return factory.createBuffer( size ); 
      }
      else {
        throw new MPIException("BufferFactory has not been initialized") ;     
      }
    }
    
    /**
     * Destroy the buffer and add it back to factory 
     * @param buffer RawBuffer object.
     */ 
    public static void destroy(mpjbuf.RawBuffer buffer) {
      if(factory != null) { 	    
        factory.destroyBuffer( buffer ); 
      } else {
        throw new MPIException("BufferFactory has not been initialized") ;     
      }
    }

    public static boolean initialized() {
      if(factory != null) {
        return true; 	      
      }
      else {
        return false; 	      
      }
    }
    
    /**
     * Finalize factory ...not important to call ..but if you do that's 
     * great. This is re-entrant so xdev/mpjdev/mpi packages can call it 
     * without any detrimental effect.
     */ 
    public static void shut () {
      if(factory != null) { 	    
        factory.finalixe(); 
      }
      factory = null ;
    }

    public abstract void init() ;
    public abstract void finalixe() ; 
    public abstract void destroyBuffer(mpjbuf.RawBuffer buffer) ; 
    public abstract mpjbuf.RawBuffer createBuffer(int size) ; 

}
