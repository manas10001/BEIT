/*
 The MIT License

 Copyright (c) 2013 - 2014
   1. SEECS, National University of Sciences and Technology, Pakistan (2013 - 2014)
   2. Bibrak Qamar  (2013 - 2014)

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
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev;

import mpi.*;
import xdev.ProcessID;

public abstract class Group {

  // these two are needed in javampjdev
  // but some test cases for mpjdev use ids[] so for the moment keeping them
  // here
  public ProcessID[] ids = null;
  public ProcessID myID = null;

  public int rank, size;

  public final static int NO_RANK = -1;
  public static final int IDENT = 0, CONGRUENT = 3, SIMILAR = 1, UNEQUAL = 2;
  protected static final int UNDEFINED = -1;

  /*
   * TODO: Methods below are supposed to be static, they will be defined
   * directly in repective class but we provide wrappers here
   * 
   * 
   * //public static int[] transRanks(Group group1, int[]ranks1, Group group2);
   * //public static int compare(Group group1, Group group2); //public static
   * Group union(Group group1, Group group2); //public static Group
   * intersection(Group group1, Group group2); //public static Group
   * difference(Group group1, Group group2);
   */

  public static int[] transRanks(mpjdev.Group group1, int[] ranks1,
      mpjdev.Group group2) {
    if (Constants.isNative) {
      return mpjdev.natmpjdev.Group.transRanksNativ(
	  (mpjdev.natmpjdev.Group) group1, ranks1,
	  (mpjdev.natmpjdev.Group) group2);

    } else {
      return mpjdev.javampjdev.Group.transRanks(group1, ranks1, group2);
    }

  }

  public static int compare(mpjdev.Group group1, mpjdev.Group group2) {

    if (Constants.isNative) {
      return mpjdev.natmpjdev.Group.compareNativ(
	  (mpjdev.natmpjdev.Group) group1, (mpjdev.natmpjdev.Group) group2);

    } else {

      return mpjdev.javampjdev.Group.compare(group1, group2);

    }
  }

  public static Group union(mpjdev.Group group1, mpjdev.Group group2) {
    if (Constants.isNative) {

      return mpjdev.natmpjdev.Group.unionNativ((mpjdev.natmpjdev.Group) group1,
	  (mpjdev.natmpjdev.Group) group2);
    } else {
      return mpjdev.javampjdev.Group.union(group1, group2);
    }

  }

  public static Group intersection(mpjdev.Group group1, mpjdev.Group group2) {
    if (Constants.isNative) {

      return mpjdev.natmpjdev.Group.intersectionNativ(
	  (mpjdev.natmpjdev.Group) group1, (mpjdev.natmpjdev.Group) group2);
    } else {
      return mpjdev.javampjdev.Group.intersection(group1, group2);
    }
  }

  public static Group difference(mpjdev.Group group1, mpjdev.Group group2) {
    if (Constants.isNative) {
      return mpjdev.natmpjdev.Group.differenceNativ(
	  (mpjdev.natmpjdev.Group) group1, (mpjdev.natmpjdev.Group) group2);
    } else {
      return mpjdev.javampjdev.Group.difference(group1, group2);
    }

  }

  public abstract void free();

  /**
   * This method returns the size of the group. It is a count of the number of
   * process encapsulated by this group object.
   * 
   * @return int The number of processes in this group
   */
  public abstract int size();

  /**
   * This method returns the rank of the group. It is the rank (id) of the
   * calling thread/process in this group.
   * 
   * @return int The rank of the callling process in this group.
   */
  public abstract int rank();

  /**
   * This method returns a new group object including all the ranks specified in
   * the argument array. Each item of the argument array should be a valid rank
   * in the calling group. The total number of items in argument array should
   * not be more than the size of the existing group. This method is a local
   * operation.
   * 
   * @param ranks
   *          Integer array specifying the ranks of the processes that will be
   *          part of the new group
   * @return Group The group object of the new process or null if the calling
   *         process is not in the new group.
   */
  public abstract Group incl(int[] ranks);

  /**
   * This method returns a new group object excluding all the ranks specified in
   * the argument array. Each item of the argument array should be a valid rank
   * in the calling group. The total number of items in argument array should
   * not be more than the size of the existing group. This method is a local
   * operation.
   * 
   * @param ranks
   *          Integer array specifying the ranks of the processes that will not
   *          be part of the new group
   * @return Group The group object of the new process or null if the calling
   *         process is in argument array.
   */
  public abstract Group excl(int[] ranks);

  /**
   */
  public abstract Group rangeIncl(int[][] ranges);

  /**
   * implemented, not sure what it does and thus not tested at the moment.
   */
  public abstract Group rangeExcl(int[][] ranges);

  
  public abstract void finalize();
}
