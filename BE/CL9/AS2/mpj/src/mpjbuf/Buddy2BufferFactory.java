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
 * File         : Buddy2BufferFactory.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.6 $
 * Updated      : $Date: 2005/09/11 00:14:35 $
 *    
 */
package mpjbuf ;

import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import org.apache.log4j.Logger ; 

public class Buddy2BufferFactory extends BufferFactory { 
  //static final boolean DEBUG = false;	

  private ArrayList<Buddy2Region> regionLists = 
	  new ArrayList<Buddy2Region>();
  static Logger logger = Logger.getLogger ( "mpj");
	  

  public synchronized void init() {
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("creating region of size "+
		      BufferConstants.DEFAULT_REGION_SIZE );
    }
    Buddy2Region region = new Buddy2Region (
		    BufferConstants.DEFAULT_REGION_SIZE);
    regionLists.add( region );
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Filling regionLists"+regionLists );
      logger.debug("init completes ");
    }

  }

  public synchronized void finalixe() {

    mpjbuf.Buddy2Buffer current = null ;
	  
    for(Buddy2Region reg : regionLists) {

      int numLevels = reg.freeLists.size() ;

      for(int l=0 ; l<numLevels ; l++) {

        Buddy2FreeList fList = reg.freeLists.get(l);   	      

	if(!fList.isEmpty()) {
		
          current = (mpjbuf.Buddy2Buffer) fList.head ;		
	  
	  do {	
	    current.free() ;
            current = (mpjbuf.Buddy2Buffer) current.prev; 
	  } while(current != (mpjbuf.Buddy2Buffer) fList.head);

	}

	fList = null ;

      }

      reg.buffer = null ;

      reg = null; 

    }

    regionLists = null ;
  }

/**
  * Return a free buffer of level greater than or equal to `level'.
  */
  mpjbuf.Buddy2Buffer findInAllRegions (int level) {
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--findInAllRegions--");	  
    }
    mpjbuf.Buddy2Buffer freeBuffer = null ;
    foundIt: 	
    for(Buddy2Region reg : regionLists) { 

      int numLevels = reg.freeLists.size() ;
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" reg<"+reg+"> has "+numLevels+" levels");
      }
	
      if(level < numLevels) {
        for (int l = level ; l < numLevels ; l++) {
		
          if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" searching for a buffer at level "+l);
	  }
		
          Buddy2FreeList freeList = reg.freeLists.get(l) ;

          if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" got list "+freeList);
	  }
	    
          if(!freeList.isEmpty()) { 

            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug(" got list "+freeList);
	    }

	    if(freeList.head == null) {
    	      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                logger.debug(" freeList.head is null ");
	      }
	      continue;	
	    }
	    else {
              if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                logger.debug(" freeList.head is not null ");
	      }
	      mpjbuf.Buddy2Buffer current = 
		      (mpjbuf.Buddy2Buffer) freeList.head;      
              if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                logger.debug(" head "+current);
                logger.debug(" current "+current);
	        logger.debug(" tail " + (mpjbuf.Buddy2Buffer) freeList.tail );
	      }
	      
	      do {	
	        if(current.free) {
                  if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                    logger.debug(" current is free ");
		  }
                  freeBuffer = current;
		  break foundIt; 
		} else {
                  if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                    logger.debug(" current is not free ");
		  }
		}
		current = (mpjbuf.Buddy2Buffer) current.prev; 
    		if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                  logger.debug(" next current "+current);
		}
	      } while(current != (mpjbuf.Buddy2Buffer) freeList.head);

	    }//end else.
	  }//end if.
	  else {
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug(" list is empty ");
	    }
	  }
	}//end for.
      }//end if. 
    }//end for iterating the regions ..

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--findInAllRegions ends--");	  
    }
    return freeBuffer ;
  }

  mpjbuf.Buddy2Buffer find(Buddy2Region inRegion, int level ) {
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--find--");	  
    }
    mpjbuf.Buddy2Buffer freeBuffer = null ;

    int numLevels = inRegion.freeLists.size() ;

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("inRegion <"+inRegion+"> has "+numLevels+" levels");
    }
	
    if(level < numLevels) {
     foundIt: 	
     for (int l = level ; l < numLevels ; l++) {
       if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
         logger.debug(" searching for a buffer at level "+l);
       }
       Buddy2FreeList freeList = inRegion.freeLists.get(l) ;
       
       if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
         logger.debug(" got list "+freeList);
       }
	    
       if(!freeList.isEmpty()) { 

         if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
           logger.debug(" got list "+freeList);
	 }
         if(freeList.head == null) {
           if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
             logger.debug(" freeList.head is null ");
	   }
	   continue;		    
	 }
	 else {
           if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
             logger.debug(" freeList.head is not null ");
	   }
	   mpjbuf.Buddy2Buffer current = 
		   (mpjbuf.Buddy2Buffer) freeList.head;      
           if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
             logger.debug(" current "+current);
	   }
		
           do {
	     if(current.free) {
               if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                 logger.debug(" current is free ");
	       }
               freeBuffer = current;
	       break foundIt; 
	     } else {
               if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
                 logger.debug(" current is not free ");
	       }
	     }
	     current = (mpjbuf.Buddy2Buffer) current.prev; 
             if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
               logger.debug(" next current "+(mpjbuf.Buddy2Buffer) current);
	     }
	   } while(current != 
			   (mpjbuf.Buddy2Buffer) freeList.tail); 
	 }//end else.
       }//end if.
       else {
         if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
           logger.debug(" list is empty ");
	 }
       }
     }//end for.
    }//end if. 

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--find ends--");	  
    }
    return freeBuffer ;
  }

  public synchronized mpjbuf.RawBuffer createBuffer (int size) {

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--createBuffer--"+size);	  
    }
    if(size == 0) {

      //do something weird.		 
      return null; 
      
    } else {

      int numChunks = (size+BufferConstants.CHUNK_SIZE-1)
	      /BufferConstants.CHUNK_SIZE;
      int level = CustomMath.widthInBits( numChunks-1 ); 
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" Required level "+level);
      }
      
      mpjbuf.Buddy2Buffer freeNode = findInAllRegions(level); 
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("findInAllRegions returned freeNode "+freeNode );
      }
      
      if(freeNode == null) {
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("creating newRegion");      
	}
        int newRegionSize = -1;	 

        if(size > BufferConstants.DEFAULT_REGION_SIZE) {
          for(int h=2 ; ; h=h*2) {
            if( size < BufferConstants.DEFAULT_REGION_SIZE * h) {
              newRegionSize = BufferConstants.DEFAULT_REGION_SIZE * h ;    
	      break;
	    }
	  }
	  
        } else {
 	  newRegionSize = BufferConstants.DEFAULT_REGION_SIZE;	  
	}

	Buddy2Region reg = new Buddy2Region( newRegionSize ); 
	regionLists.add( reg );
	
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("Filling regionLists"+regionLists );
	  logger.debug(" we just finished creating a new region ...");
	}

	freeNode = find(reg, level);
      }

      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" freeNode "+freeNode );
        logger.debug("the level of freeNode is "+freeNode.level );
      }
      
      mpjbuf.Buddy2Buffer output = block(freeNode ,freeNode.region , level) ;
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("block returned "+output);
        logger.debug("at level "+output.level);
        logger.debug("output.realOffset "+output.realOffset );
        logger.debug("output.free (false?) "+output.free );
        logger.debug("--createBuffer ends --");	  
      }
      
      return output; 
	      
    }
  }
    
  /**
   * return a buffer of level 'level' from freeBuffer and return 
   * remainder of freeBuffer (if any) to the freeList 
   */
  private mpjbuf.Buddy2Buffer block(mpjbuf.Buddy2Buffer freeBuffer,  
		  Buddy2Region region, int level) {
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--block--");	  
    }
    
    int l = freeBuffer.level;
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" level of the buffer found is <"+l+">");	 
    }
    
    mpjbuf.Buddy2Buffer buddy = null; 

    /* luckily we found a freeBuffer that is at the level we wanted */
    if(level == l) {
	    
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("luckily the level we were looking for");	      
      }
      
      freeBuffer.free = false; 
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("--block ends--");	  
      }
      
      return freeBuffer;
    } else {
	    
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("removing the buffer at level <"+l+">");
      }

      region.freeLists.get(l).remove(freeBuffer); 
      
      for( l=l-1 ; l>=level ; l-- ) {
	int childCap = freeBuffer.capacity/2; 
	
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("childCap "+childCap);
	  logger.debug("realOffset "+freeBuffer.realOffset);
	}
	
        region.buffer.position( freeBuffer.realOffset );
	region.buffer.limit( freeBuffer.realOffset +childCap);
	freeBuffer = new mpjbuf.Buddy2Buffer(region.buffer.slice(), 
			0 , childCap, l, region ,
			freeBuffer.realOffset );
        region.buffer.position( freeBuffer.realOffset +childCap );
        region.buffer.limit( freeBuffer.realOffset +(2*childCap) );
	
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("buddy real offset "+ 
			  (freeBuffer.realOffset +childCap) );
	  logger.debug("buddy limit "+ (freeBuffer.realOffset+2*childCap) );
	}

	buddy = new mpjbuf.Buddy2Buffer (region.buffer.slice(), 
			0 , childCap, l, region ,
			(freeBuffer.realOffset +childCap) );
        region.buffer.clear(); //dodgy ...
        region.freeLists.get(l).add(buddy); 

	if(l == level) {
	  if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug("Adding freeBuf to freeLists at level <"+l+">");
	  }
	  
          region.freeLists.get(l).add(freeBuffer); 
	}

	if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" Adding buddy to freeLists at level <"+l+">");
	}
	
      }
	
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" setting free as false ... ");
      }
      
      freeBuffer.free = false;
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("--block ends--");	  
      }
      
      return freeBuffer; 
    }
  }
    
  private mpjbuf.Buddy2Buffer calculateBuddy(
		  mpjbuf.Buddy2Buffer buffer) {
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--calculateBuddy--" + buffer.region );
      logger.debug("calculating buddy at level "+buffer.level);
    }
    
    Buddy2FreeList fList = buffer.region.freeLists.get(buffer.level);
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("fList.size "+fList.size );
      logger.debug("buffer.realOffset "+buffer.realOffset);
      logger.debug("buffer.region.regionSize "+ buffer.region.regionSize );
      logger.debug("blockSize"+(BufferConstants.CHUNK_SIZE << buffer.level));
    }

    int buddyOffset = buffer.realOffset ^ 
	    (BufferConstants.CHUNK_SIZE << buffer.level) ; 
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("buddyRealOffset (offset looking for) "+buddyOffset ); 
      logger.debug("tihs buffers realOffset "+buffer.realOffset ); 
    }
    
    mpjbuf.Buddy2Buffer current= (mpjbuf.Buddy2Buffer) fList.head ;
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("current "+current);
    }
    
    mpjbuf.Buddy2Buffer buddy = null; 
    
    do {
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("current.realOffset "+current.realOffset );
      }
      
      if(current.realOffset == buddyOffset) {
	if(current.free) {      
          if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" found buddy and its free "+current);
	  }
	  
	  buddy = current;
	  break;
	} else {
          if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" found buddy but its not free ");
	  }
	  
          //this means we've found the buddy, but its not free ..so there's
	  //not much we can do except set buffer.free = true and exit ..
	  break;
	}
      } else {
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" no buddy in this iteratoin at least ");
	}

      }
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("current(1) "+current);
      }
      
      current = (mpjbuf.Buddy2Buffer) current.prev; 
      
      if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("current "+current);
        logger.debug("fList.head "+ (mpjbuf.Buddy2Buffer) fList.head );
      }
      
    } while(current != (mpjbuf.Buddy2Buffer) fList.head ); 

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--calculateBuddy ends--");
    }
    
    return buddy; 
  }

  public synchronized void destroyBuffer (mpjbuf.RawBuffer buffer ) { 
    mpjbuf.Buddy2Buffer buf = (mpjbuf.Buddy2Buffer) buffer ;	  
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--destroyBuffer--" + buf);
    }
    
    mpjbuf.Buddy2Buffer buddy = calculateBuddy( buf); 
    
    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("called calculateBuddy"+buf+"the firstTime "+buddy);
    }
    
    if(buddy == null) {
      buf.free = true;
      try {
	((NIOBuffer)buf).clear() ;
      }catch(Exception e) {
	e.printStackTrace() ;      
      }
      return; 
    }

    int numLevels = buf.region.freeLists.size();
    int level = buf.level ; 
    Buddy2FreeList fList, topLevelList ; 
    
    while (buddy != null) {
      fList = buf.region.freeLists.get(level);

      if(buf.region.freeLists.size() == (level+1) ) {
        topLevelList = new Buddy2FreeList();
	buf.region.freeLists.add(topLevelList);
      } else {
        topLevelList = buf.region.freeLists.get(level+1);
      }
	      
      int parentOffset = -1; 

      if(buddy.realOffset > buf.realOffset ) {
       parentOffset = buf.realOffset ; 	
      } else {
       parentOffset = buddy.realOffset ; 	
      } 

      int parentbufoffset = 0 ; 

      try {
        ((NIOBuffer)buf).clear() ;
        buf.free = false ;
        ((NIOBuffer)buddy).clear() ;
        buddy.free = false ;
      }catch(Exception e) {
        e.printStackTrace() ;	
      }
 
      fList.remove(buddy); //we know buddy is free ...
      fList.remove(buf); 
      buf.region.buffer.position( parentOffset);
      buf.region.buffer.limit(parentOffset+(buf.capacity*2) );
      buf = new mpjbuf.Buddy2Buffer (buf.region.buffer.slice() , 
		      parentbufoffset, buf.capacity*2, (level+1) , 
		      buf.region, parentOffset );
      topLevelList.add(buf);
      buf.region.buffer.clear() ;
      buddy = calculateBuddy(buf); 
      level++; 
      
    } 

    if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--destroyBuffer ends --");
    }

  }
  
}
