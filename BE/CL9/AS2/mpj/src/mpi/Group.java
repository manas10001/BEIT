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
 * File         : Group.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.14 $
 * Updated      : $Date: 2014/03/11 13:24:14 $
 */

package mpi;

import mpjdev.*;
import java.util.UUID;
import xdev.ProcessID;

public class Group implements Freeable {

  static final int NULL = 2;

  // made public because of mpjdev.MPJDev.java
  // where Constants.GROUP_EMPTY is initialized
  public static final int EMPTY = 3;
  // TODO remove public
  public int code = -1;

  public mpjdev.Group mpjdevGroup = null;

  public Group(mpjdev.Group mpjdevGroup) {
    if (mpjdevGroup == null) {
      if (Constants.isNative == true) {
	this.mpjdevGroup = new mpjdev.natmpjdev.Group(Group.EMPTY);
      } else {
	this.mpjdevGroup = new mpjdev.javampjdev.Group(new ProcessID[0], null,
	    -1);
      }
    } else {
      this.mpjdevGroup = mpjdevGroup;
    }
  }

  Group(int code) {
    this.code = code;
  }

  /**
   * This method frees this group object. Though automatic garbage collector
   * will take care of it, but we mark this object for gc, by declaring it null
   * implement at the end.
   */
  public void free() {
    // this.group = null;
    // this = null;
  }

  /**
   * Size of group.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>number of processors in the group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_SIZE</tt>.
   */
  public int Size() throws MPIException {
    return (this.mpjdevGroup != null ? this.mpjdevGroup.size() : MPI.UNDEFINED);
  }

  /**
   * Rank of this process in group.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>rank of the calling process in the group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_RANK</tt>.
   * 
   * Result value is <tt>MPI.UNDEFINED</tt> if this process is not a member of
   * the group.
   */
  public int Rank() throws MPIException {
    return this.mpjdevGroup.rank();
  }

  /**
   * Translate ranks within one group to ranks within another.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group1   </tt></td>
   * <td>a group
   * </tr>
   * <tr>
   * <td><tt> ranks1   </tt></td>
   * <td>array of valid ranks in <tt>group1</tt>
   * </tr>
   * <tr>
   * <td><tt> group2   </tt></td>
   * <td>another group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>array of corresponding ranks in <tt>group2</tt>
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_TRANSLATE_RANKS</tt>.
   * <p>
   * Result elements are <tt>MPI.UNDEFINED</tt> where no correspondence exists.
   */
  public static int[] Translate_ranks(Group group1, int[] ranks1, Group group2)
      throws MPIException {
    return mpjdev.Group.transRanks(group1.mpjdevGroup, ranks1,
	group2.mpjdevGroup);
  }

  /**
   * Compare two groups.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group1   </tt></td>
   * <td>first group
   * </tr>
   * <tr>
   * <td><tt> group2   </tt></td>
   * <td>second group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>result
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_COMPARE</tt>.
   * <p>
   * <tt>MPI.IDENT</tt> results if the group members and group order are exactly
   * the same in both groups. <tt>MPI.SIMILAR</tt> results if the group members
   * are the same but the order is different. <tt>MPI.UNEQUAL</tt> results
   * otherwise.
   */
  public static int Compare(Group group1, Group group2) throws MPIException {

    // System.out.println("group1 code = "+ group1.code+" group2 code = "+
    // group2.code);

    if (group1 == null || group1 == null) {
      throw new MPIException(" Group.Compare does not accept null " + "groups");
    } else if (group1.code == EMPTY && group2.code == EMPTY) {
      throw new MPIException("Group.Compare cannot compare empty groups");
    } else if (group1.code == EMPTY) {
      return MPI.UNEQUAL;
    } else if (group2.code == EMPTY) {
      return MPI.UNEQUAL;
    } else if (group1.code == NULL && group2.code == NULL) {
      throw new MPIException("Group.Compare cannot compare null groups");
    } else if (group1.code == NULL) {
      return MPI.UNEQUAL;
    } else if (group2.code == NULL) {
      return MPI.UNEQUAL;
    } else
      return mpjdev.Group.compare(group1.mpjdevGroup, group2.mpjdevGroup);
  }

  /**
   * Set union of two groups.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group1   </tt></td>
   * <td>first group
   * </tr>
   * <tr>
   * <td><tt> group2   </tt></td>
   * <td>second group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>union group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_UNION</tt>.
   */

  public static Group Union(Group group1, Group group2) throws MPIException {

    if (group1 == null || group2 == null) {
      throw new MPIException("Group.Union does not accept null groups as"
	  + "arguments");
    }

    else if (group1.code == NULL && group2.code == NULL) {
      return MPI.GROUP_NULL;
    }

    else if (group2.code == NULL) {
      return group1;
    }

    else if (group1.code == NULL) {
      return group2;
    }

    else if (group1.code == EMPTY && group2.code == EMPTY) {
      return MPI.GROUP_EMPTY;
    }

    else if (group2.code == EMPTY) {
      return group1;
    }

    else if (group1.code == EMPTY) {
      return group2;
    }

    return new Group(mpjdev.Group.union(group1.mpjdevGroup, group2.mpjdevGroup));
  }

  /**
   * Set intersection of two groups.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group1   </tt></td>
   * <td>first group
   * </tr>
   * <tr>
   * <td><tt> group2   </tt></td>
   * <td>second group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>intersection group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_INTERSECTION</tt>.
   */
  public static Group Intersection(Group group1, Group group2)
      throws MPIException {
    if (group1 == null || group2 == null) {
      throw new MPIException("Group.Union does not accept null groups as"
	  + "arguments");
    }

    else if (group1.code == NULL || group2.code == NULL) {
      return MPI.GROUP_NULL;
    }

    else if (group1.code == EMPTY || group2.code == EMPTY) {
      return MPI.GROUP_EMPTY;
    }

    return new Group(mpjdev.Group.intersection(group1.mpjdevGroup,
	group2.mpjdevGroup));
  }

  /**
   * Result contains all elements of the first group that are not in the second
   * group.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group1   </tt></td>
   * <td>first group
   * </tr>
   * <tr>
   * <td><tt> group2   </tt></td>
   * <td>second group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>difference group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_DIFFERENCE</tt>.
   */
  public static Group Difference(Group group1, Group group2)
      throws MPIException {
    if (group1 == null || group2 == null) {
      throw new MPIException("Group.Union does not accept null groups as"
	  + "arguments");
    }

    if (group1.code == NULL) {
      return MPI.GROUP_NULL;
    }

    if (group2.code == NULL) {
      return group1;
    }

    if (group1.code == EMPTY) {
      return MPI.GROUP_EMPTY;
    }

    if (group2.code == EMPTY) {
      return group1;
    }

    return new Group(mpjdev.Group.difference(group1.mpjdevGroup,
	group2.mpjdevGroup));
  }

  /**
   * Create a subset group including specified processes.
   * <p>
   * <table>
   * <tr>
   * <td><tt> ranks    </tt></td>
   * <td>ranks from this group to appear in new group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_INCL</tt>.
   */
  public Group Incl(int[] ranks) throws MPIException {
    if (ranks == null) {
      throw new MPIException("Group.Incl does not accept a null integer "
	  + " array ");
    }

    if (ranks.length == 0) {
      return MPI.GROUP_EMPTY;
    }

    return new Group(mpjdevGroup.incl(ranks));
  }

  /**
   * Create a subset group excluding specified processes.
   * <p>
   * <table>
   * <tr>
   * <td><tt> ranks    </tt></td>
   * <td>ranks from this group <em>not</em> to appear in new group
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_EXCL</tt>.
   */
  public Group Excl(int[] ranks) throws MPIException {
    if (ranks == null) {
      throw new MPIException("Group.Excl does not accept a null integer "
	  + " array ");
    }

    if (ranks.length == 0) {
      return this;
    }

    return new Group(mpjdevGroup.excl(ranks));
  }

  /**
   * Create a subset group including processes specified by strided intervals of
   * ranks.
   * <p>
   * <table>
   * <tr>
   * <td><tt> ranges   </tt></td>
   * <td>array of integer triplets
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_RANGE_INCL</tt>.
   * <p>
   * The triplets are of the form (first rank, last rank, stride) indicating
   * ranks in this group to be included in the new group. The size of the first
   * dimension of <tt>ranges</tt> is the number of triplets. The size of the
   * second dimension is 3.
   */
  public Group Range_incl(int[][] ranges) throws MPIException {
    if (ranges == null) {
      throw new MPIException("Group.Range_incl does not accept null integer "
	  + "array as argument");
    }
    return new Group(mpjdevGroup.rangeIncl(ranges));
  }

  /**
   * Create a subset group excluding processes specified by strided intervals of
   * ranks.
   * <p>
   * <table>
   * <tr>
   * <td><tt> ranges   </tt></td>
   * <td>array of integer triplets
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_RANGE_EXCL</tt>.
   * <p>
   * Triplet array is defined as for <tt>Range_incl</tt>, the ranges indicating
   * ranks in this group to be excluded from the new group.
   */
  public Group Range_excl(int[][] ranges) throws MPIException {
    if (ranges == null) {
      throw new MPIException("Group.Range_incl does not accept null integer "
	  + "array as argument");
    }
    return new Group(mpjdevGroup.rangeExcl(ranges));
  }

  /**
   * Destructor.
   * <p>
   * Java binding of the MPI operation <tt>MPI_GROUP_FREE</tt>.
   */

  public void finalize() throws MPIException {
  }
}
