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

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : Minloc.java
 * Author       : Sang Lim, Sung-Hoon Ko, Xinying Li, Bryan Carpenter
 *                (contributions from MAEDA Atusi), Aamir Shafi
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.8 $
 * Updated      : $Date: 2005/07/29 14:03:09 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

package mpi;


// Minloc and Maxloc
public class Minloc extends User_function{
  public void Call(Object invec, int inoffset, Object outvec, int outoffset,
                   int count, Datatype datatype){
    if(datatype == MPI.SHORT2) {
      short [] in_array = (short[])invec;
      short [] out_array = (short[])outvec;

      int indisp  = inoffset ;
      int outdisp = outoffset ;
      for (int i = 0; i < count; i++, indisp += 2, outdisp += 2){
        
        short inval  = in_array  [indisp] ;
        short outval = out_array [outdisp] ;

        if(inval < outval) {
          out_array [outdisp    ] = inval ;
          out_array [outdisp + 1] = in_array [indisp + 1] ;
        }
        else if(inval == outval) {
          short inloc = in_array [indisp + 1] ;

          if(inloc < out_array [outdisp + 1])
            out_array [outdisp + 1] = inloc ;
        }
      }
    }
    else if(datatype == MPI.INT2) {
      int [] in_array = (int[])invec;
      int [] out_array = (int[])outvec;

      int indisp  = inoffset ;
      int outdisp = outoffset ;
      for (int i = 0; i < count; i++, indisp += 2, outdisp += 2){
        
        int inval  = in_array  [indisp] ;
        int outval = out_array [outdisp] ;

        if(inval < outval) {
          out_array [outdisp    ] = inval ;
          out_array [outdisp + 1] = in_array [indisp + 1] ;
        }
        else if(inval == outval) {
          int inloc = in_array [indisp + 1] ;

          if(inloc < out_array [outdisp + 1])
            out_array [outdisp + 1] = inloc ;
        }
      }
    }
    else if(datatype == MPI.LONG2) {
      long [] in_array = (long[])invec;
      long [] out_array = (long[])outvec;

      int indisp  = inoffset ;
      int outdisp = outoffset ;
      for (int i = 0; i < count; i++, indisp += 2, outdisp += 2){
        
        long inval  = in_array  [indisp] ;
        long outval = out_array [outdisp] ;

        if(inval < outval) {
          out_array [outdisp    ] = inval ;
          out_array [outdisp + 1] = in_array [indisp + 1] ;
        }
        else if(inval == outval) {
          long inloc = in_array [indisp + 1] ;

          if(inloc < out_array [outdisp + 1])
            out_array [outdisp + 1] = inloc ;
        }
      }
    }
    else if(datatype == MPI.FLOAT2) {
      float [] in_array = (float[])invec;
      float [] out_array = (float[])outvec;

      int indisp  = inoffset ;
      int outdisp = outoffset ;
      for (int i = 0; i < count; i++, indisp += 2, outdisp += 2){
        
        float inval  = in_array  [indisp] ;
        float outval = out_array [outdisp] ;

        if(inval < outval) {
          out_array [outdisp    ] = inval ;
          out_array [outdisp + 1] = in_array [indisp + 1] ;
        }
        else if(inval == outval) {
          float inloc = in_array [indisp + 1] ;

          if(inloc < out_array [outdisp + 1])
            out_array [outdisp + 1] = inloc ;
        }
      }
    }
    else if(datatype == MPI.DOUBLE2) {
      double [] in_array = (double[])invec;
      double [] out_array = (double[])outvec;

      int indisp  = inoffset ;
      int outdisp = outoffset ;
      for (int i = 0; i < count; i++, indisp += 2, outdisp += 2){
        
        double inval  = in_array  [indisp] ;
        double outval = out_array [outdisp] ;

        if(inval < outval) {
          out_array [outdisp    ] = inval ;
          out_array [outdisp + 1] = in_array [indisp + 1] ;
        }
        else if(inval == outval) {
          double inloc = in_array [indisp + 1] ;

          if(inloc < out_array [outdisp + 1])
            out_array [outdisp + 1] = inloc ;
        }
      }
    }
    else {
      System.out.println("MPI.MINLOC: invalid datatype") ;
      try {
        MPI.COMM_WORLD.Abort(1);
      }
      catch(MPIException e) {}
    }
  }
}

// Things to do:
//
//   Check if `Maxloc'/`Minloc' should work with derived types.

