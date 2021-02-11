/* This file generated automatically from template ProdType.java.in. */
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
 * File         : ProdInt.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.7 $
 * Updated      : $Date: 2005/08/01 22:31:40 $
 */

  package mpi;

  import mpjbuf.*;

  public class ProdInt extends Prod {
    int [] arr = null;
    ProdInt() {
    }

    void perform (Object buf1, int offset, int count) throws MPIException {
      int[] arr1 = (int[]) buf1;

      for (int i = 0; i < count; i++) {
        arr[i] = (int) (arr1[i] * arr[i]);
      }
    }

    void createInitialBuffer(Object buf, int offset, int count) 
	    throws MPIException {
      int[] tempArray = (int[]) buf;
      arr = new int[tempArray.length];
      System.arraycopy(buf, offset, arr, offset, count) ;
    }

    void getResultant(Object buf, int offset, int count ) throws MPIException {
      int[] tempArray = (int[]) buf;
      System.arraycopy(arr, offset, tempArray, offset, count);
    }
  }

