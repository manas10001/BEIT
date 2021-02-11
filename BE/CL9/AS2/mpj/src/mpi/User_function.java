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
 * File         : User_function.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.7 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 */

package mpi;

public abstract class User_function {
  /**
   * User-defined function for a new <tt>Op</tt>.
   * <p>
   * <table>
   * <tr><td><tt> invec       </tt></td><td> array of values to combine with
   *                                         <tt>inoutvec</tt> elements </tr>
   * <tr><td><tt> inoffset    </tt></td><td> initial offset in
   *                                         <tt>invec<tt> </tr>
   * <tr><td><tt> inoutvec    </tt></td><td> in-out array of accumulator
   *                                         locations </tr>
   * <tr><td><tt> inoutoffset </tt></td><td> initial offset in
   *                                         <tt>inoutvec<tt> </tr>
   * <tr><td><tt> count       </tt></td><td> number of items in arrays </tr>
   * <tr><td><tt> datatype    </tt></td><td> type of each item </tr>
   * </table>
   * <p>
   * Java equivalent of the MPI <tt>USER_FUNCTION</tt>.
   */
  public abstract void Call(Object invec, int inoffset,
                   Object inoutvec, int inoutoffset,
                   int count, Datatype datatype) throws MPIException ;
}

