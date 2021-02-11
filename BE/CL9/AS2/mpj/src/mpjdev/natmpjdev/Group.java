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
 * File         : natmpjdev.Group.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev.natmpjdev;

import mpjdev.*;

public class Group extends mpjdev.Group {

  /*
   * TODO also have to implement there abstract methods
   * 
   * public abstract Group rangeIncl(int[][] ranges); public abstract Group
   * rangeExcl(int[][] ranges);
   */

  static {
    init();
  }

  protected long handleOfGroup; // native MPI handle of this group

  public long getHandle() {

    return handleOfGroup;
  }

  private static native void init(); // This is called in the static block

  public Group(int Type) {
    GetGroup(Type); // Type is 3 or 2 // GetGroup() is a native method
		    // type 2 = COMM_WORLD this is mostly the case
  }

  /*
   * This function set the group to the group pointed by the _handle
   */
  public Group(long _handle) {
    handleOfGroup = _handle;

  }

  /*
   * This function sets the group to either the default group i.e. the group of
   * universal COMM_WORLD (typeOfWorld == 2) or it sets to MPI_GROUP_EMPTY
   * (typeOfWorld == 3)
   */
  private native void GetGroup(int Type);

  public void free() {

    nativeFree();
  }

  public int size() {

    return nativeSize();
  }

  public int rank() {

    return nativeRank();
  }

  public Group incl(int[] ranks) {
    return new Group(nativeIncl(ranks));

  }

  public Group excl(int[] ranks) {
    return new Group(nativeExcl(ranks));

  }

  public Group rangeIncl(int[][] ranges) {

    if (ranges == null)
      return null;

    // convert the 2D ranges into 1D and pass it to the nativeRangeIncl

    // as we know that the dimension is 3 so ranges[len][3]
    // here find len with ranges.length
    int len = ranges.length;
    int oneDimRanges[] = new int[len * 3];

    for (int i = 0; i < len; i++) {
      oneDimRanges[(i * 3) + 0] = ranges[i][0];
      oneDimRanges[(i * 3) + 1] = ranges[i][1];
      oneDimRanges[(i * 3) + 2] = ranges[i][2];
    }

    return new Group(nativeRangeIncl(len, oneDimRanges));

  }

  public Group rangeExcl(int[][] ranges) {

    if (ranges == null)
      return null;

    // convert the 2D ranges into 1D and pass it to the nativeRangeIncl

    // as we know that the dimension is 3 so ranges[len][3]
    // here find len with ranges.length
    int len = ranges.length;
    int oneDimRanges[] = new int[len * 3];

    for (int i = 0; i < len; i++) {
      oneDimRanges[(i * 3) + 0] = ranges[i][0];
      oneDimRanges[(i * 3) + 1] = ranges[i][1];
      oneDimRanges[(i * 3) + 2] = ranges[i][2];
    }

    return new Group(nativeRangeExcl(len, oneDimRanges));
  }

  public void finalize() throws MPJDevException {
    // MPI_GROUP_FREE functionality .. as per mpiJava 1.2 specs
  }

  public static int[] transRanksNativ(mpjdev.natmpjdev.Group group1,
      int[] ranks1, mpjdev.natmpjdev.Group group2) {

    return nativetransRanks(group1.getHandle(), group2.getHandle(), ranks1);
  }

  /*
   * Methods below are static in mpjdev.Group where they act as wrappers to
   * functions in javampjdev.Group and natmpjdev.Group. So have to provide
   * implementation
   */
  // Static functions
  public static int compareNativ(mpjdev.natmpjdev.Group group1,
      mpjdev.natmpjdev.Group group2) {

    int result = nativeCompare(group1.getHandle(), group2.getHandle());

    // No need to explicitly compare the result with
    // IDENT, SIMILAR or UNEQUAL as the native code takes care of it
    // But make sure to keep these macros the same in
    // mpjdev.Group and /mpjdev/nativ/lib/mpjdev_nativ_Group.c
    // in case there values change in the future

    return result;
  }

  public static Group unionNativ(mpjdev.natmpjdev.Group group1,
      mpjdev.natmpjdev.Group group2) {

    long unionGroupHandle = nativeUnion(group1.getHandle(), group2.getHandle());

    return new Group(unionGroupHandle);
  }

  public static Group intersectionNativ(mpjdev.natmpjdev.Group group1,
      mpjdev.natmpjdev.Group group2) {

    long intersectionGroupHandle = nativeIntersection(group1.getHandle(),
	group2.getHandle());

    return new Group(intersectionGroupHandle);

  }

  public static Group differenceNativ(mpjdev.natmpjdev.Group group1,
      mpjdev.natmpjdev.Group group2) {

    long differenceGroupHandle = nativeDifference(group1.getHandle(),
	group2.getHandle());

    return new Group(differenceGroupHandle);
  }

  // TODO: native rangeIncl() rangeExcl() to be implemented
  private native void nativeFree();

  private native int nativeSize();

  private native int nativeRank();

  private native long nativeIncl(int[] ranks);

  private native long nativeExcl(int[] ranks);

  private native long nativeRangeIncl(int n, int[] ranges);

  private native long nativeRangeExcl(int n, int[] ranges);

  private static native int[] nativetransRanks(long handleGroup1,
      long handleGroup2, int[] ranks1);

  private static native int nativeCompare(long handleGroup1, long handleGroup2);

  private static native long nativeUnion(long handleGroup1, long handleGroup2);

  private static native long nativeIntersection(long handleGroup1,
      long handleGroup2);

  private static native long nativeDifference(long handleGroup1,
      long handleGroup2);
}
