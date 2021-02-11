/* This file generated automatically from template MultistridedPackerType.java.in. */
  /*
  The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

  Permission is hereby granted, free of charge, to any person obtaining
  a copy of this software and associated documentation files (the
  "Software"), to deal in the Software without restriction, including
  without limitation the rights to use, copy, modify, merge, publish,
  distribute, sublicense, and/or sell copies of the Software, and to
  permit persons to whom the Software is furnished to do so, subject to
  the following conditions:

  The above copyright notice and this permission notice shall be included
  in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  */
/*
 * File         : MultistridedPackerFloat.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.7 $
 * Updated      : $Date: 2005/07/29 14:03:09 $
 */

  package mpi;

  import mpjbuf.*;

  public class MultistridedPackerFloat extends MultistridedPacker {


      public MultistridedPackerFloat(int rank, int [] indexes,
                                      int extent, int size) {
        super(rank, indexes, extent, size);
      }

      public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		      int offset) throws MPIException {
        try {
          mpjbuf.strGather((float []) buf, offset, 
			  rank, 0, rank, indexes) ;
	}
	catch(Exception e) {
          throw new MPIException(e);		
	}

      }

      public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		      int offset) throws MPIException {
        try {
          mpjbuf.strScatter((float []) buf, offset, 
			  rank, 0, rank, indexes) ;
	}
	catch(Exception e) {
          throw new MPIException(e);		
	}
      }

      // In following methods, tempting to try to temporarily modify the
      // existing `indexes' array, to avoid temporary allocations.
      // But if `indexes' is not immutable we lose the ability to safely
      // share the datatype object across threads.

      public void unpackPartial(mpjbuf.Buffer mpjbuf, int length,
                                Object buf, int offset) throws MPIException {

          int [] cIndexes = new int [2 * rank] ;
          for(int i = 0 ; i < 2 * rank ; i++)
              cIndexes [i] = indexes [i] ;

          int cubeRank = rank - 1;
          int cubeSize = size ;

          while(length > 0) {
              cubeSize /= indexes [cubeRank] ;
                      // size of next smallest dimension cube.
              int numCubes = length / cubeSize ;    // Number of whole cubes.
              if(numCubes > 0) {
                  cIndexes [cubeRank] = numCubes ;
                  int blockSize = numCubes * cubeSize ;
		  
		  try {
                    mpjbuf.strScatter((float []) buf, offset,
                                    cubeRank + 1, 0, rank, cIndexes) ;
		  }
		  catch(Exception e) {
                    throw new MPIException(e);  			  
		  }

                  // Unpack block of cubes.
                  offset += blockSize ;
                  length -= blockSize ;
                  // If cubeRank = 0, then
                  //    cubeSize = 1, blockSize = numCubes = length
                  //         => This assignment puts length to zero.
              }
              cubeRank-- ;
          }
      }

      public void pack(mpjbuf.Buffer mpjbuf, Object buf, int offset,
                       int count) throws MPIException {
	      
          if(count == 1) {
            try {
              mpjbuf.strGather((float []) buf, offset,
                               rank, 0, rank, indexes) ;
            }
	    catch(Exception e) {
              throw new MPIException(e);		    
	    }
          }
          else {
              int cRank = rank + 1 ;
              int [] cIndexes = new int [2 * cRank] ;
              for(int i = 0 ; i < rank ; i++)
                  cIndexes [i] = indexes [i] ;
              cIndexes [rank] = count ;
              for(int i = 0 ; i < rank ; i++)
                  cIndexes [cRank + i] = indexes [rank + i] ;
              cIndexes [cRank + rank] = extent ;
	      
	      try {
                mpjbuf.strGather((float []) buf, offset,
                               cRank, 0, cRank, cIndexes) ;
	      }
	      catch(Exception e) {
                throw new MPIException(e);		    
	      }
          }
      }

      public void unpack(mpjbuf.Buffer mpjbuf, Object buf, int offset,
                         int count) throws MPIException {
	  
          if(count == 1) {

            try {
              mpjbuf.strScatter((float []) buf, offset,
                                rank, 0, rank, indexes) ;
            }
	    catch(Exception e) {
              throw new MPIException(e);		    
	    }
          }
          else {
              int cRank = rank + 1 ;
              int [] cIndexes = new int [2 * cRank] ;
              for(int i = 0 ; i < rank ; i++)
                  cIndexes [i] = indexes [i] ;
              cIndexes [rank] = count ;
              for(int i = 0 ; i < rank ; i++)
                  cIndexes [cRank + i] = indexes [rank + i] ;
              cIndexes [cRank + rank] = extent ;
	      
              try {
                mpjbuf.strScatter((float []) buf, offset,
                                cRank, 0, cRank, cIndexes) ;
              }catch(Exception e) {
                throw new MPIException(e);		    
              }
          }
      }


  }

