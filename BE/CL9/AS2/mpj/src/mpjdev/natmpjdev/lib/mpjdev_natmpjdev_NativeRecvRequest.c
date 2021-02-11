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
 * File         : mpjdev_natmpjdev_NativeRecvRequest.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

/* class mpjdev_natmpjdev_NativeRecvRequest */
#include "mpi.h"
#include "mpjdev_natmpjdev_NativeRecvRequest.h"
#include "mpjdev_natmpjdev_shared.h"

//jfieldID mpjdev_natmpjdev_NativeRequest_reqhandleID;

jfieldID mpjdev_natmpjdev_NativeRequest_comHandle;
jfieldID mpjdev_natmpjdev_NativeRequest_mpjbuf;
jfieldID reqhandleID;

jclass CL_mpjdev_natmpjdev_NativeRequest;

/*
 * Class:     mpjdev_natmpjdev_NativeRecvRequest
 * Method:    initNativeRecvRequest
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_NativeRecvRequest_initNativeRecvRequest
(JNIEnv *env, jclass thisClass) {

  //mpjdev_natmpjdev_NativeRequest_reqhandleID = (*env)->GetFieldID(env, 
  //											thisClass, "handle", "J");

  mpjdev_natmpjdev_NativeRequest_comHandle = (*env)->GetFieldID(env,
      thisClass, "commHandle", "J");

  mpjdev_natmpjdev_NativeRequest_mpjbuf = (*env)->GetFieldID(env, thisClass,
      "bufferHandle", "Lmpjbuf/Buffer;");

  CL_mpjdev_natmpjdev_NativeRequest = (*env)->FindClass(env, "mpjdev/natmpjdev/NativeRequest");

  reqhandleID = (*env)->GetFieldID(env, CL_mpjdev_natmpjdev_NativeRequest, "handle",
      "J");

}

/*
 * Class:     mpjdev_natmpjdev_NativeRecvRequest
 * Method:    Wait
 * Signature: (Lmpjdev/Status;)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL Java_mpjdev_natmpjdev_NativeRecvRequest_Wait(
    JNIEnv *env, jobject thisObject, jobject stat) {
  // printf("native recv iwait: called\n");
  int elements;

  MPI_Request request = (MPI_Request)(
      (*env)->GetLongField(env, thisObject, reqhandleID));

  MPI_Status mpi_status;

  MPI_Wait(&request, &mpi_status);

  MPI_Get_count(&mpi_status, MPI_BYTE, &elements);

  (*env)->SetIntField(env, stat, mpjdev_Status_sourceID, mpi_status.MPI_SOURCE);
  (*env)->SetIntField(env, stat, mpjdev_Status_tagID, mpi_status.MPI_TAG);

  //seting the number of elements as size of the buffer and in the iwait() method
  // we pass it to setSize() method of buffer 
  // bufferHandle.setSize(status.numEls);
  // later numEls is again initialized with getSectionSize() of the buffer
  (*env)->SetIntField(env, stat, mpjdev_Status_numEls, elements);

  // I am also setting this because we need it in mpi/Status Get_count
  // method : TODO remove magic numbers
  (*env)->SetIntField(env, stat, mpjdev_Status_countInBytes,
      (jint)(elements - 8 - 8)); // 8 is the size of section header
  // and 8 recv overhead

  //check if dynamic buffer?
  jobject mpjbuf;
  jobject staticBuffer;
  jbyteArray directBuffer;
  char *buffer_address = NULL;

  /* Get the static Buffer Related things.. */
  mpjbuf = (*env)->GetObjectField(env, thisObject,
      mpjdev_natmpjdev_NativeRequest_mpjbuf);
  staticBuffer = (*env)->GetObjectField(env, mpjbuf,
      FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (*env)->GetObjectField(env, staticBuffer,
      FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *) (*env)->GetDirectBufferAddress(env,
      (jobject) directBuffer);

  char encoding = 0;
  int dbuf_len = -1;

  encoding = buffer_address[0];
  if (encoding == 1) {

    dbuf_len = (((int) (unsigned char) buffer_address[4]) << 24)
        | (((int) (unsigned char) buffer_address[5]) << 16)
        | (((int) (unsigned char) buffer_address[6]) << 8)
        | (((int) (unsigned char) buffer_address[7]));

    /* Declarations of Dynamic Buffer */
    jboolean isCopy = JNI_TRUE;
    jbyteArray darr;
    jbyte * dBuffer;

    MPI_Status mpi_status_dyn;

    MPI_Comm mpi_comm = (MPI_Comm)(*env)->GetLongField(env, thisObject,
        mpjdev_natmpjdev_NativeRequest_comHandle);

    darr = (*env)->NewByteArray(env, dbuf_len);
    // perhaps no need for this - use malloc instead
    dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);
    // magic number ??
    MPI_Recv(dBuffer, dbuf_len, MPI_BYTE, mpi_status.MPI_SOURCE,
        mpi_status.MPI_TAG + 10001, mpi_comm, &mpi_status_dyn);

    (*env)->SetByteArrayRegion(env, darr, 0, dbuf_len, dBuffer);

    jmethodID setdbuf = (*env)->GetMethodID(env, CL_mpjbuf_Buffer,
        "setDynamicBuffer", "([B)V");
    (*env)->CallVoidMethod(env, mpjbuf, setdbuf, darr);

  }

  return stat;

}

/*
 * Class:     mpjdev_natmpjdev_NativeRecvRequest
 * Method:    Test
 * Signature: (Lmpjdev/Status;)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL Java_mpjdev_natmpjdev_NativeRecvRequest_Test(
    JNIEnv *env, jobject thisObject, jobject stat) {

  /*
   * Conventionaly our itest() 
   * provide iprobe like functionality. 
   * 
   * So we have 3 options:
   * 1: change our mpi.Request Test() and don't call Wait() then
   * we have to change implementations of itest() in all the devices
   * 
   * 2: Put if-else in mpi.Request Test() to distinguish between native
   * and pure. This is messy. And provide the real MPI_Test() functionality
   * here.
   * 
   * 3: Provide iprobe functionality here and don't change anything in
   * the upper layers. This requires introducing source and tag in
   * NativeRecvRequest
   * 
   * We are opting for option 2 right now.
   * 
   */

  int flag;

  MPI_Request request = (MPI_Request)(
      (*env)->GetLongField(env, thisObject, reqhandleID));

  MPI_Status mpi_status;

  MPI_Test(&request, &flag, &mpi_status);

  if (flag) {

    int elements;

    MPI_Get_count(&mpi_status, MPI_BYTE, &elements);

    (*env)->SetIntField(env, stat, mpjdev_Status_sourceID,
        mpi_status.MPI_SOURCE);
    (*env)->SetIntField(env, stat, mpjdev_Status_tagID, mpi_status.MPI_TAG);

    //seting the number of elements as size of the buffer and in the iwait() method
    // we pass it to setSize() method of buffer 
    // bufferHandle.setSize(status.numEls);
    // later numEls is again initialized with getSectionSize() of the buffer
    (*env)->SetIntField(env, stat, mpjdev_Status_numEls, elements);

    // I am also setting this because we need it in mpi/Status Get_count
    // method : TODO remove magic numbers
    (*env)->SetIntField(env, stat, mpjdev_Status_countInBytes,
        (jint)(elements - 8 - 8)); // 8 is the size of section header
    // and 8 recv overhead

    //check if dynamic buffer?
    jobject mpjbuf;
    jobject staticBuffer;
    jbyteArray directBuffer;
    char *buffer_address = NULL;

    /* Get the static Buffer Related things.. */
    mpjbuf = (*env)->GetObjectField(env, thisObject,
        mpjdev_natmpjdev_NativeRequest_mpjbuf);
    staticBuffer = (*env)->GetObjectField(env, mpjbuf,
        FID_mpjbuf_Buffer_staticBuffer);
    directBuffer = (*env)->GetObjectField(env, staticBuffer,
        FID_mpjbuf_NIOBuffer_buffer);
    buffer_address = (char *) (*env)->GetDirectBufferAddress(env,
        (jobject) directBuffer);

    char encoding = 0;
    int dbuf_len = -1;

    encoding = buffer_address[0];
    if (encoding == 1) {

      dbuf_len = (((int) (unsigned char) buffer_address[4]) << 24)
          | (((int) (unsigned char) buffer_address[5]) << 16)
          | (((int) (unsigned char) buffer_address[6]) << 8)
          | (((int) (unsigned char) buffer_address[7]));

      /* Declarations of Dynamic Buffer */
      jboolean isCopy = JNI_TRUE;
      jbyteArray darr;
      jbyte * dBuffer;

      MPI_Status mpi_status_dyn;

      MPI_Comm mpi_comm = (MPI_Comm)(*env)->GetLongField(env, thisObject,
          mpjdev_natmpjdev_NativeRequest_comHandle);

      darr = (*env)->NewByteArray(env, dbuf_len);
      // perhaps no need for this - use malloc instead
      dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);

      // This is wrong here Why blocking? we need to set the NativeSendRequest
      // wait to not call wait on dynamic buffer, else deadlock arises
      MPI_Recv(dBuffer, dbuf_len, MPI_BYTE, mpi_status.MPI_SOURCE,
          mpi_status.MPI_TAG + 10001, mpi_comm, &mpi_status_dyn);

      (*env)->SetByteArrayRegion(env, darr, 0, dbuf_len, dBuffer);

      jmethodID setdbuf = (*env)->GetMethodID(env, CL_mpjbuf_Buffer,
          "setDynamicBuffer", "([B)V");
      (*env)->CallVoidMethod(env, mpjbuf, setdbuf, darr);

    }

    return stat;

  } else
    return NULL;

}

