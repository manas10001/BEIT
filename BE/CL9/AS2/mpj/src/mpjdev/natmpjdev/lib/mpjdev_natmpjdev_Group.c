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
 

 /*
 * File         : mpjdev_natmpjdev_Group.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

#include "mpjdev_natmpjdev_Group.h"
#include "mpi.h"
#include "mpjdev_natmpjdev_shared.h"
#include <inttypes.h>
#include <stdlib.h> // for malloc
jfieldID GrouphandleID;

/* class mpjdev_natmpjdev_Group */

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    init
 * Signature: ()V
 *
 * This function is called once in the static region of mpjdev.Group class
 * it only gets the field ID for handle in the global jfieldID GrouphandleID
 * so that we don't have to
 * do a lookup each time its used.
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Group_init
(JNIEnv *env, jclass thisClass) {

  GrouphandleID = (*env)->GetFieldID(env, thisClass, "handleOfGroup", "J");
}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    GetGroup
 * Signature: (I)V
 *
 * This function set the group to either the default group i.e. the group of
 * universal COMM_WORLD (typeOfWorld == 2) or it sets to MPI_GROUP_EMPTY
 * (typeOfWorld == 3)
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Group_GetGroup
(JNIEnv *env, jobject thisObject, jint typeOfWorld) {

  MPI_Group group;
  switch (typeOfWorld) {
    case 2:
    MPI_Comm_group(MPI_COMM_WORLD, &group);
    (*env)->SetLongField(env, thisObject, GrouphandleID, (jlong)group);
    break;
    case 3:
    (*env)->SetLongField(env, thisObject, GrouphandleID, (jlong)MPI_GROUP_EMPTY);
    break;
    default:
    break;
  }

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeFree
 * Signature: ()V
 *
 * Free the group and set the handle to MPI_GROUP_NULL
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Group_nativeFree
(JNIEnv *env, jobject thisObject) {

  MPI_Group group = (MPI_Group)((*env)->GetLongField(env, thisObject,
          GrouphandleID));
  MPI_Group_free(&group);

  (*env)->SetLongField(env, thisObject, GrouphandleID, (jlong)MPI_GROUP_NULL);
}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeSize
 * Signature: ()I
 *
 * Size of the group
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Group_nativeSize(JNIEnv *env,
    jobject thisObject) {
  int size;

  MPI_Group group = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));
  int errCode = MPI_Group_size(group, &size);

  if (errCode != MPI_SUCCESS)
    return -1;
  else
    return size;
}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeRank
 * Signature: ()I
 *
 * Rank of this process with in the group
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Group_nativeRank(JNIEnv *env,
    jobject thisObject) {

  int rank;

  MPI_Group group = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));
  int errCode = MPI_Group_rank(group, &rank);

  if (errCode == MPI_ERR_GROUP || errCode == MPI_ERR_ARG) {
    printf("native Group Rank returned with error !\n");
    fflush (stdout);
    rank = -1;

  }

  if (rank < 0)
    return -1;

  return rank;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeIncl
 * Signature: ([I)J
 *
 * Incl for group
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeIncl(JNIEnv *env,
    jobject thisObject, jintArray ranks) {

  int n;
  jint *rks;
  jboolean isCopy = JNI_TRUE;
  MPI_Group newGroup;

  n = (*env)->GetArrayLength(env, ranks);
  rks = (*env)->GetIntArrayElements(env, ranks, &isCopy);

  MPI_Group existingGroup = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));

  MPI_Group_incl(existingGroup, n, (int*) rks, &newGroup);

  (*env)->ReleaseIntArrayElements(env, ranks, rks, 0);

  return (jlong) newGroup;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeExcl
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeExcl(JNIEnv *env,
    jobject thisObject, jintArray ranks) {

  int n;
  jint *rks;
  jboolean isCopy = JNI_TRUE;
  MPI_Group newGroup;

  n = (*env)->GetArrayLength(env, ranks);
  rks = (*env)->GetIntArrayElements(env, ranks, &isCopy);

  MPI_Group existingGroup = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));

  int errCode = MPI_Group_excl(existingGroup, n, (int*) rks, &newGroup);

  (*env)->ReleaseIntArrayElements(env, ranks, rks, 0);

  if (errCode != MPI_SUCCESS) {
    printf("Nativ: Group - nativeExcl Failed errCode = %d\n", errCode);
    return 0;
  } else
    return (jlong) newGroup;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeRangeIncl
 * Signature: (I[I)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeRangeIncl(JNIEnv *env,
    jobject thisObject, jint n, jintArray oneDimRanges) {

  int oneDimRangesLen;
  jint *nativeOneDimRanges;
  jboolean isCopy = JNI_TRUE;
  MPI_Group newGroup;

  oneDimRangesLen = (*env)->GetArrayLength(env, oneDimRanges);
  nativeOneDimRanges = (*env)->GetIntArrayElements(env, oneDimRanges, &isCopy);

  MPI_Group existingGroup = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));

  int errCode = MPI_Group_range_incl(existingGroup, n,
      (int (*)[3]) nativeOneDimRanges, &newGroup);

  (*env)->ReleaseIntArrayElements(env, oneDimRanges, nativeOneDimRanges, 0);

  if (errCode != MPI_SUCCESS) {
    printf("Nativ: Group - nativeRangeIncl Failed errCode = %d\n", errCode);
    return 0;
  } else
    return (jlong) newGroup;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeRangeExcl
 * Signature: (I[I)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeRangeExcl(JNIEnv *env,
    jobject thisObject, jint n, jintArray oneDimRanges) {

  int oneDimRangesLen;
  jint *nativeOneDimRanges;
  jboolean isCopy = JNI_TRUE;
  MPI_Group newGroup;

  oneDimRangesLen = (*env)->GetArrayLength(env, oneDimRanges);
  nativeOneDimRanges = (*env)->GetIntArrayElements(env, oneDimRanges, &isCopy);

  MPI_Group existingGroup = (MPI_Group)(
      (*env)->GetLongField(env, thisObject, GrouphandleID));

  int errCode = MPI_Group_range_excl(existingGroup, n,
      (int (*)[3]) nativeOneDimRanges, &newGroup);

  (*env)->ReleaseIntArrayElements(env, oneDimRanges, nativeOneDimRanges, 0);

  if (errCode != MPI_SUCCESS) {
    printf("Nativ: Group - nativeRangeExcl Failed errCode = %d\n", errCode);
    return 0;
  } else
    return (jlong) newGroup;

}
/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativetransRanks
 * Signature: (JJ[I)[I
 */
JNIEXPORT jintArray JNICALL Java_mpjdev_natmpjdev_Group_nativetransRanks(
    JNIEnv *env, jclass thisClass, jlong group1_, jlong group2_,
    jintArray ranks) {

  int errCode;
  int n;
  jintArray result;
  jint *rks, *rks2;
  jboolean isCopy = JNI_TRUE;
  MPI_Group group1 = (MPI_Group) (intptr_t) group1_;
  MPI_Group group2 = (MPI_Group) (intptr_t) group2_;

  n = (*env)->GetArrayLength(env, ranks);
  rks = (*env)->GetIntArrayElements(env, ranks, &isCopy);
  rks2 = malloc(sizeof(int) * n);
  result = (*env)->NewIntArray(env, n);

  if (result == NULL) {
    return NULL; /* out of memory error thrown */
  }

  errCode = MPI_Group_translate_ranks(group1, n, (int*) rks, group2,
      (int*) rks2);

  if (errCode != MPI_SUCCESS) {
    printf("Nativ: Group - nativetransRanks Failed errCode = %d\n", errCode);
    free(rks2);
    (*env)->ReleaseIntArrayElements(env, ranks, rks, 0);
    return NULL;
  }

  (*env)->ReleaseIntArrayElements(env, ranks, rks, 0);

  // move from rks2 to the java jintArray result
  (*env)->SetIntArrayRegion(env, result, 0, n, rks2);

  free(rks2);

  return (jintArray) result;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeCompare
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Group_nativeCompare(JNIEnv *env,
    jobject thisObject, jlong _group1, jlong _group2) {

  int result;
  int errCode;

  MPI_Group group1 = (MPI_Group) (intptr_t) _group1;
  MPI_Group group2 = (MPI_Group) (intptr_t) _group2;

  errCode = MPI_Group_compare(group1, group2, &result);

  // here return the result as MPJ Express macros
  // as per MPJ Express 0.38
  // NO_RANK = -1, IDENT = 0, CONGRUENT = 3, SIMILAR = 1, UNEQUAL = 2;
  // UNDEFINED = -1; 

  if (errCode == MPI_SUCCESS) {

    if (result == MPI_IDENT) {
      return IDENT;
    } else if (result == MPI_SIMILAR) {
      return SIMILAR;
    } else if (result == MPI_UNEQUAL) {
      return UNEQUAL;
    }
  } else if (errCode == MPI_ERR_GROUP || errCode == MPI_ERR_ARG) {
    //error !
    printf("nativeCompare error = %d \n", errCode);
    return -1; // UNDEFINED
  }
}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeUnion
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeUnion(JNIEnv *env,
    jobject thisObject, jlong group1_, jlong group2_) {

  int result;
  int errCode;

  MPI_Group group1 = (MPI_Group) (intptr_t) group1_;
  MPI_Group group2 = (MPI_Group) (intptr_t) group2_;
  MPI_Group newGroup;
  errCode = MPI_Group_union(group1, group2, &newGroup);

  if (errCode != MPI_SUCCESS) {
    printf("nativeUnion not  MPI_SUCCESS\n");
    return -1;
  }

  return (jlong) newGroup;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeIntersection
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeIntersection(
    JNIEnv *env, jobject thisObject, jlong group1_, jlong group2_) {

  int result;
  int errCode;

  MPI_Group group1 = (MPI_Group) (intptr_t) group1_;
  MPI_Group group2 = (MPI_Group) (intptr_t) group2_;
  MPI_Group newGroup;
  errCode = MPI_Group_intersection(group1, group2, &newGroup);

  if (errCode != MPI_SUCCESS) {
    printf("nativeIntersection not MPI_SUCCESS\n");
    return -1;
  }

  return (jlong) newGroup;

}

/*
 * Class:     mpjdev_natmpjdev_Group
 * Method:    nativeDifference
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Group_nativeDifference(
    JNIEnv *env, jobject thisObject, jlong group1_, jlong group2_) {

  int result;
  int errCode;

  MPI_Group group1 = (MPI_Group) (intptr_t) group1_;
  MPI_Group group2 = (MPI_Group) (intptr_t) group2_;
  MPI_Group newGroup;
  errCode = MPI_Group_difference(group1, group2, &newGroup);

  if (errCode != MPI_SUCCESS) {
    printf("nativeDifference not  MPI_SUCCESS\n");
    return -1;
  }

  return (jlong) newGroup;
}

