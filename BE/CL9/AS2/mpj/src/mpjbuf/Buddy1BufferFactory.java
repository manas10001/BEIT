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
 * File         : Buddy1BufferFactory.java
 * Author       : Bryan Carpenter, Aamir Shafi 
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.13 $
 * Updated      : $Date: 2006/04/25 19:26:14 $
 *    
 */
package mpjbuf ; 

import java.util.ArrayList ;
import java.nio.ByteBuffer ;
import java.io.FileOutputStream ;
import java.io.PrintStream ;
import java.io.File ;
import org.apache.log4j.Logger ;

public class Buddy1BufferFactory extends BufferFactory {
    static Logger logger = Logger.getLogger ( "mpj" );

    //static final boolean DEBUG = false;

    private ArrayList<Buddy1FreeList> freeLists = 
	    new ArrayList<Buddy1FreeList>() ;

    public synchronized void init(){
        //initialize the factory here ..	     
	//create buffer creates the first region anyway ...!
    }

    public synchronized mpjbuf.RawBuffer createBuffer(int size) {

        if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
 	  logger.debug("\n\n createBuffer "+size);

        if(size == 0) {
            //do something weird.
            return null;   // *** (But less weird than this !!!) ***
        } else {
            //+1 is the extra bit required by clever buddy ...   
            int numChunks = (size + 1 + BufferConstants.CHUNK_SIZE - 1) / 
		    BufferConstants.CHUNK_SIZE;
            int level = CustomMath.widthInBits(numChunks - 1);
            if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
	      logger.debug("required level "+level );
	    
            Buddy1RegionFreeList list ;
            if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
	      logger.debug("finding the right level ");
            find : {

                int numLevels = freeLists.size() ;
		
                if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
		  logger.debug("freeList.size() "+freeLists.size()) ;

                for (int l = level ; l < numLevels ; l++) {

                    list = freeLists.get(l).getFront() ;
                    if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
		      logger.debug("list @level <"+l+">="+list);
                    if(list != null) break find ;
                }

                // If we got here, we need to allocate a new region.
                // Current policy is:
                //
                //   newNumLevels =
                //       max(INIT_NUM_LEVELS, numLevels + 1, level + 1)

                int newNumLevels = BufferConstants.INIT_NUM_LEVELS ;
                if(numLevels + 1 > newNumLevels)
                    newNumLevels = numLevels + 1;
                if(level + 1 > newNumLevels)
                    newNumLevels = level + 1;

                if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
	          logger.debug("allocating a new region ");
                list = initializeRegion(newNumLevels) ;
            }

            if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
	      logger.debug("calling allocate ...");
            return allocate(level, list) ;
        }
    }


    public synchronized void destroyBuffer(mpjbuf.RawBuffer buffer) {
        free((Buddy1Buffer) buffer) ;
    }


    /**
     * Allocate a block of specified level from a non-empty free list
     * with the same or higher level.
     *
     * If list level is higher than required, excess memory is returned
     * to the appropriate lower free lists.
     *
     * @param level the level of the required block
     * @param non-empty list
     * @return allocate buffer
     */
    private Buddy1Buffer allocate(int level, Buddy1RegionFreeList list) {
        if(mpi.MPI.DEBUG && logger.isDebugEnabled())     
          logger.debug("--allocate--");
        int position = list.front ;
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug(" level :"+level );
	  logger.debug(" list :"+list);
	}

        list.removeFront() ;

        while(list.level > level) {
            list = list.down ;
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug("position "+position) ;
	      logger.debug("list.blockSize "+list.blockSize );
	      logger.debug("Adding buddy @ "+ (position ^ list.blockSize)) ;
	    }
	    list.memory.position(position ^ list.blockSize) ;
	    list.memory.put( (byte) list.level ); 
            list.add(position ^ list.blockSize) ;  // Add buddy to list
        }

	int cap = BufferConstants.CHUNK_SIZE << level ; 
	list.memory.position( position) ; 
	list.memory.put( BufferConstants.ALLOCATED ); 

	list.memory.position( position+1  ); 
	list.memory.limit ( cap+position);
	ByteBuffer slicedBuffer = list.memory.slice() ; 
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("slicedBuffer "+slicedBuffer );
	  logger.debug(" cap "+cap);
	  logger.debug("list "+list);
	  logger.debug("list.memory "+list.memory );
	  logger.debug("position "+position );
	}
	//.debug("(malloc) position "+ position ); 
        return new Buddy1Buffer(slicedBuffer , cap , list , position ) ;
    }

    /**
     * Free a buffer.
     *
     * Recursively aggregate block with free buddies where possible,
     * returning aggregated block free list of appropriate level.
     *
     * @param buffer the buffer to free.
     */
    private void free(Buddy1Buffer buffer) {
	if(buffer.list.front == buffer.position) {
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) 
	      logger.debug("This can't happen, but returning ..."); 
	    return;
	}
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("\n--free--");
	  logger.debug("buffer.list.front "+buffer.list.front) ;
	}
        Buddy1RegionFreeList list = buffer.list ;
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" list.level "+list.level);
	}
        int position = buffer.position ;
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" position "+ position ) ;
	  //.debug("(free) position "+ position ); 
	}

        ByteBuffer memory = buffer.list.memory ;
	memory.clear() ;
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" memory (for region) "+ memory ) ;
	}

        while(list.up != null) {  // Not top-level list of region

            int buddy = position ^ list.blockSize ;
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug(" buddy "+ buddy ) ;
	    }
	    memory.position( buddy ) ;
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug(" memory "+ memory ) ;
	    }
	    
	    if(memory.get() != list.level ) {
                if(mpi.MPI.DEBUG && logger.isDebugEnabled())
		  logger.debug(" buddy is not FREE ");    
	        break ;
	    }
	    
            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	      logger.debug(" buddy is free ...removing it ..");
	    }
	    
            list.remove(buddy) ;

            if(position > buddy) {
                position = buddy ;
            }

            list = list.up ;
        }

            if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug(" adding the buffer at pos <"+position);
	logger.debug("size of htis buffer is "+list.blockSize );
	    }

	// problematic bits .. .. .. 
        list.add(position) ; 
	memory.position( position );
        memory.put( (byte) list.level );
	
    }

    public synchronized void finalixe() {
	    //do something! 
    }

    //int numberOfRegions = 0 ; 

    /**
     * Initialize a new region.
     *
     * New memory is allocated, and free list nodes are initialized as
     * required.
     *
     * @param numLevels the number of levels in the new region.
     * @return reference to top-level local free list, initially containing
     * all memory associated with region.
     */
    private Buddy1RegionFreeList initializeRegion(int numLevels) {
	
	//System.out.println(" regions="+(++numberOfRegions) ) ;   

        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("--initializeRegion--") ;
	}
	
        int size = BufferConstants.CHUNK_SIZE << (numLevels - 1) ;
	
        if(mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("size  "+size ) ;
	}
	
        ByteBuffer memory = ByteBuffer.allocateDirect ( size );    

        // Create local free lists for all levels.

        Buddy1RegionFreeList [] lists = new Buddy1RegionFreeList [numLevels] ;

        int oldNumLevels = freeLists.size() ;
        int blockSize = BufferConstants.CHUNK_SIZE ;

        for(int l = 0 ; l < numLevels ; l++) {

            Buddy1FreeList freeList ;

            // Extend global free list table as necessary.

            if(l < oldNumLevels)
                freeList = freeLists.get(l) ;
            else {
                freeList = new Buddy1FreeList() ;
                freeLists.add(freeList) ;
            }

            lists [l] = new Buddy1RegionFreeList(l, blockSize, 
			    memory, freeList) ;
            blockSize = blockSize << 1 ;
        }

        // Initialize `down' and `up' pointers connecting local free lists,
        // for use in `allocate()' and `free()' methods.

        for(int l = 0 ; l < numLevels ; l++) {

            Buddy1RegionFreeList list = lists [l] ;

            if(l > 0)
                list.down = lists [l - 1] ;

            if(l + 1 < numLevels)
                list.up   = lists [l + 1] ;
        }

        // Add whole of memory to top level local free list.

        Buddy1RegionFreeList top = lists [numLevels - 1] ;

        top.add(0) ;

        return top ;
    }
}
