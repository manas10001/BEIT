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

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : Datatype.java
 * Author       : Sang Lim, Sung-Hoon Ko, Xinying Li, Bryan Carpenter
 *                Aamir Shafi
 * Created      : Thu Apr  9 12:22:15 BST 1998
 * Revision     : $Revision: 1.15 $
 * Updated      : $Date: 2014/03/11 13:12:07 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer;

public abstract class Datatype implements Freeable {

  mpjbuf.Type bufferType = mpjbuf.Type.UNDEFINED;

  /*
   * Make these a superset of `mpjdev.Buffer' type enumeration. create an
   * enumberation here. Tried to create an ENUM, but if i declare it in
   * MPI.java, and if its name is also MPI (aamir)
   */

  final static int UNDEFINED = -1;
  final static int NULL = 0;
  final static int BYTE = 1;
  final static int CHAR = 2;
  final static int SHORT = 3;
  final static int BOOLEAN = 4;
  final static int INT = 5;
  final static int LONG = 6;
  final static int FLOAT = 7;
  final static int DOUBLE = 8;
  final static int PACKED = 9;
  final static int LB = 10;
  final static int UB = 11;
  final static int OBJECT = 12;

  final static int SHORT2 = 3;
  final static int INT2 = 5;
  final static int LONG2 = 6;
  final static int FLOAT2 = 7;
  final static int DOUBLE2 = 8;

  int numEls;
  public int baseType = UNDEFINED;
  int size;
  int byteSize;
  int lb, ub, extent;
  boolean ubSet, lbSet;
  // Flags set if MPI.UB, MPI.LB respectively appears as a component type.
  Packer packer;

  Packer getPacker() {

    if (packer == null)
      setPacker();

    return packer;
  }

  // added for native MPI support
  public int getType() {
    return this.baseType;
  }

  // added for native MPI support
  public int getByteSize() {
    return this.byteSize;
  }

  abstract void setPacker();

  /*
   * offset will be 'MPI.SEND_OVERHEAD' size will be 'packedSize(count) +
   * MPI.SEND_OVERHEAD '
   */
  abstract mpjbuf.Buffer createWriteBuffer(int count) throws MPIException;

  /*
   * messageSize will be 'packedSize(count)+MPI.BSEND_OVERHEAD' offset will be
   * set in this method to 'MPI.BSEND_OVERHEAD '
   */
  abstract mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer,
      int messageSize);

  /*
   * size is 'packedSize(count)+MPI.RECV_OVERHEAD' ; offset is
   * 'MPI.RECV_OVERHEAD'
   */
  abstract mpjbuf.Buffer createReadBuffer(int count);

  abstract int packedSize(int count);

  /**
   * Returns the extent of a datatype - the difference between upper and lower
   * bound.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>datatype extent
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_EXTENT</tt>.
   */

  public int Extent() throws MPIException {
    return extent;
  }

  /**
   * Returns the total size of a datatype - the number of buffer elements it
   * represents.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>datatype size
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_SIZE</tt>.
   */

  public int Size() throws MPIException {
    return size;
  }

  /**
   * Find the lower bound of a datatype - the least value in its displacement
   * sequence.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>displacement of lower bound from origin
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_LB</tt>.
   */

  public int Lb() throws MPIException {
    return lb;
  }

  /**
   * Find the upper bound of a datatype - the greatest value in its displacement
   * sequence.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>displacement of upper bound from origin
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_UB</tt>.
   */

  public int Ub() throws MPIException {
    return ub;
  }

  /**
   * Commit a derived datatype. Java binding of the MPI operation
   * <tt>MPI_TYPE_COMMIT</tt>.
   */

  public void Commit() throws MPIException {
    /*
     * if(... something ...) commit() ;
     */
  }

  public void finalize() throws MPIException {
    /*
     * synchronized(MPI.class) { MPI.freeList.addFirst(this) ; }
     */
  }

  /**
   * Construct new datatype representing replication of old datatype into
   * contiguous locations.
   * <p>
   * <table>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>replication count
   * </tr>
   * <tr>
   * <td><tt> oldtype  </tt></td>
   * <td>old datatype
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_CONTIGUOUS</tt>.
   * <p>
   * The base type of the new datatype is the same as the base type of
   * <tt>oldtype</tt>.
   */

  public static Datatype Contiguous(int count, Datatype oldtype)
      throws MPIException {
    return new Contiguous(count, oldtype);
  }

  /**
   * Construct new datatype representing replication of old datatype into
   * locations that consist of equally spaced blocks.
   * <p>
   * <table>
   * <tr>
   * <td><tt> count       </tt></td>
   * <td>number of blocks
   * </tr>
   * <tr>
   * <td><tt> blocklength </tt></td>
   * <td>number of elements in each block
   * </tr>
   * <tr>
   * <td><tt> stride      </tt></td>
   * <td>number of elements between start of each block
   * </tr>
   * <tr>
   * <td><tt> oldtype     </tt></td>
   * <td>old datatype
   * </tr>
   * <tr>
   * <td><em> returns:    </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_VECTOR</tt>.
   * <p>
   * The base type of the new datatype is the same as the base type of
   * <tt>oldtype</tt>.
   */

  public static Datatype Vector(int count, int blocklength, int stride,
      Datatype oldtype) throws MPIException {

    return new Vector(count, blocklength, stride, oldtype, true);
  }

  /**
   * Identical to <tt>vector</tt> except that the stride is expressed directly
   * in terms of the buffer index, rather than the units of the old type.
   * <p>
   * <table>
   * <tr>
   * <td><tt> count       </tt></td>
   * <td>number of blocks
   * </tr>
   * <tr>
   * <td><tt> blocklength </tt></td>
   * <td>number of elements in each block
   * </tr>
   * <tr>
   * <td><tt> stride      </tt></td>
   * <td>number of elements between start of each block
   * </tr>
   * <tr>
   * <td><tt> oldtype     </tt></td>
   * <td>old datatype
   * </tr>
   * <tr>
   * <td><em> returns:    </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_HVECTOR</tt>.
   * <p>
   * <em>Unlike other language bindings</em>, the value of <tt>stride</tt> is
   * <em>not</em> measured in bytes.
   */

  public static Datatype Hvector(int count, int blocklength, int stride,
      Datatype oldtype) throws MPIException {

    return new Vector(count, blocklength, stride, oldtype, false);
  }

  /**
   * Construct new datatype representing replication of old datatype into a
   * sequence of blocks where each block can contain a different number of
   * copies and have a different displacement.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_blocklengths  </tt></td>
   * <td>number of elements per block
   * </tr>
   * <tr>
   * <td><tt> array_of_displacements </tt></td>
   * <td>displacement of each block in units of old type
   * </tr>
   * <tr>
   * <td><tt> oldtype                </tt></td>
   * <td>old datatype
   * </tr>
   * <tr>
   * <td><em> returns:               </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_INDEXED</tt>.
   * <p>
   * The number of blocks is taken to be size of the
   * <tt>array_of_blocklengths</tt> argument. The second argument,
   * <tt>array_of_displacements</tt>, should be the same size. The base type of
   * the new datatype is the same as the base type of <tt>oldtype</tt>.
   */

  public static Datatype Indexed(int[] array_of_blocklengths,
      int[] array_of_displacements, Datatype oldtype) throws MPIException {

    return new Indexed(array_of_blocklengths, array_of_displacements, oldtype,
	true);
  }

  /**
   * Identical to <tt>indexed</tt> except that the displacements are expressed
   * directly in terms of the buffer index, rather than the units of the old
   * type.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_blocklengths  </tt></td>
   * <td>number of elements per block
   * </tr>
   * <tr>
   * <td><tt> array_of_displacements </tt></td>
   * <td>displacement in buffer for each block
   * </tr>
   * <tr>
   * <td><tt> oldtype                </tt></td>
   * <td>old datatype
   * </tr>
   * <tr>
   * <td><em> returns:               </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_HINDEXED</tt>.
   * <p>
   * <em>Unlike other language bindings</em>, the values in
   * <tt>array_of_displacements</tt> are <em>not</em> measured in bytes.
   */

  public static Datatype Hindexed(int[] array_of_blocklengths,
      int[] array_of_displacements, Datatype oldtype) throws MPIException {
    return new Indexed(array_of_blocklengths, array_of_displacements, oldtype,
	false);
  }

  /**
   * The most general type constructor.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_blocklengths  </tt></td>
   * <td>number of elements per block
   * </tr>
   * <tr>
   * <td><tt> array_of_displacements </tt></td>
   * <td>displacement in buffer for each block
   * </tr>
   * <tr>
   * <td><tt> array_of_types         </tt></td>
   * <td>type of elements in each block
   * </tr>
   * <tr>
   * <td><em> returns:               </em></td>
   * <td>new datatype
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TYPE_STRUCT</tt>.
   * <p>
   * The number of blocks is taken to be size of the
   * <tt>array_of_blocklengths</tt> argument. The second and third arguments,
   * <tt>array_of_displacements</tt>, and <tt>array_of_types</tt>, should be the
   * same size. <em>Unlike other language bindings</em>, the values in
   * <tt>array_of_displacements</tt> are <em>not</em> measured in bytes. All
   * elements of <tt>array_of_types</tt> with definite base types
   * <em>must have the <em>same</em> base type</em>: this will be the base type
   * of new datatype.
   */

  public static Datatype Struct(int[] array_of_blocklengths,
      int[] array_of_displacements, Datatype[] array_of_types)
      throws MPIException {
    return new Struct(array_of_blocklengths, array_of_displacements,
	array_of_types);
  }

  public void free() {
  }

}
