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
 * File         : mpjdev_natmpjdev_NativeSendRequest.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

/* mpjdev_natmpjdev_NativeSendRequest */

#include "mpi.h"
#include "mpjdev_natmpjdev_NativeSendRequest.h"
#include "mpjdev_natmpjdev_shared.h"

//jfieldID mpjdev_natmpjdev_NativeRequest_reqhandleID;
jfieldID reqhandleID;

jfieldID dbufreqhandleID;
jfieldID dbuflenID;
/*
 * Class:     mpjdev_natmpjdev_NativeSendRequest
 * Method:    initNativeSendRequest
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_NativeSendRequest_initNativeSendRequest
(JNIEnv *env, jclass thisClass) {

  // mpjdev_natmpjdev_NativeRequest_reqhandleID = (*env)->GetFieldID(env, thisClass,
  //                                                             "handle", "J");

  dbufreqhandleID = (*env)->GetFieldID(env, thisClass, "dbufHandle", "J");

  dbuflenID = (*env)->GetFieldID(env, thisClass, "dbuflen", "I");

  //this can go in JNI_onLOAD
  jclass CL_mpjdev_natmpjdev_NativeRequest = (*env)->FindClass(env,
      "mpjdev/natmpjdev/NativeRequest");

  reqhandleID = (*env)->GetFieldID(env, CL_mpjdev_natmpjdev_NativeRequest, "handle",
      "J");

}

/*
 * Class:     mpjdev_natmpjdev_NativeSendRequest
 * Method:    Wait
 * Signature: (Lmpjdev/Status;)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL Java_mpjdev_natmpjdev_NativeSendRequest_Wait(
    JNIEnv *env, jobject thisObject, jobject stat) {
  int elements;

  MPI_Request request = (MPI_Request)(
      (*env)->GetLongField(env, thisObject, reqhandleID));

  MPI_Status mpi_status;

  MPI_Wait(&request, &mpi_status);

  (*env)->SetIntField(env, stat, mpjdev_Status_sourceID, mpi_status.MPI_SOURCE);
  (*env)->SetIntField(env, stat, mpjdev_Status_tagID, mpi_status.MPI_TAG);

  int dbuf_len = -1;

  dbuf_len = ((*env)->GetIntField(env, thisObject, dbuflenID));
  if (dbuf_len > 0) {

    MPI_Request request_dyn = (MPI_Request)(
        (*env)->GetLongField(env, thisObject, dbufreqhandleID));
    MPI_Status mpi_status_dyn;

    MPI_Wait(&request_dyn, &mpi_status_dyn);

  }

  return stat;
}

/*
 * Class:     mpjdev_natmpjdev_NativeSendRequest
 * Method:    Test
 * Signature: (Lmpjdev/Status;)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL Java_mpjdev_natmpjdev_NativeSendRequest_Test(
    JNIEnv *env, jobject thisObject, jobject stat) {

  int flag;

  MPI_Request request = (MPI_Request)(
      (*env)->GetLongField(env, thisObject, reqhandleID));

  MPI_Status mpi_status;

  MPI_Test(&request, &flag, &mpi_status);

  if (flag) {
    int elements;
    //TODO get count or what?
    (*env)->SetIntField(env, stat, mpjdev_Status_sourceID,
        mpi_status.MPI_SOURCE);
    (*env)->SetIntField(env, stat, mpjdev_Status_tagID, mpi_status.MPI_TAG);

    //?	MPI_Get_count(&mpi_status, MPI_BYTE, &elements);
    // (*env)->SetIntField(env, stat, mpjdev_Status_numEls, elements);

    // for dynamic buffer
    int dbuf_len = -1;

    dbuf_len = ((*env)->GetIntField(env, thisObject, dbuflenID));
    if (dbuf_len > 0) {

      MPI_Request request_dyn = (MPI_Request)(
          (*env)->GetLongField(env, thisObject, dbufreqhandleID));
      MPI_Status mpi_status_dyn;

      //doing wait -- not ideal ??? 
      MPI_Wait(&request_dyn, &mpi_status_dyn);
    }

    return stat;

  } else
    return NULL;
}

