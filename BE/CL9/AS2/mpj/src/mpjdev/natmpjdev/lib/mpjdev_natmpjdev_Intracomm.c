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
 * File         : mpjdev_natmpjdev_Intracomm.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

#include "mpjdev_natmpjdev_Intracomm.h"
#include <inttypes.h>
#include "mpjdev_natmpjdev_shared.h"
#include "mpi.h"
#include <stdlib.h>
#include <string.h>

jfieldID FID_mpjbuf_Buffer_staticBuffer;
jfieldID FID_mpjbuf_Buffer_dynamicBuffer;
jfieldID FID_mpjbuf_NIOBuffer_buffer;

jclass CL_mpjbuf_NIOBuffer;
jclass CL_mpjbuf_Type;
jclass CL_mpjbuf_Buffer;
jfieldID FID_mpjbuf_Buffer_capacity;
jfieldID FID_mpjbuf_Buffer_size;

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_init
(JNIEnv * env, jclass thisClass)
{
//-----------------------

  CL_mpjbuf_Buffer = (*env)->NewGlobalRef(env,
      (*env)->FindClass(env,"mpjbuf/Buffer"));

  CL_mpjbuf_NIOBuffer = (*env)->NewGlobalRef(env,
      (*env)->FindClass(env,"mpjbuf/NIOBuffer"));
  CL_mpjbuf_Type = (*env)->FindClass(env,"mpjbuf/Type");
  FID_mpjbuf_Buffer_capacity =
  (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"capacity","I");

  FID_mpjbuf_Buffer_size =
  (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"size","I");
  FID_mpjbuf_Buffer_staticBuffer =
  (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"staticBuffer","Lmpjbuf/RawBuffer;");
//TODO: no need ! of dynamicBuffer
  FID_mpjbuf_Buffer_dynamicBuffer =
  (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"dynamicBuffer","[B");

  FID_mpjbuf_NIOBuffer_buffer =
  (*env)->GetFieldID(env,CL_mpjbuf_NIOBuffer,"buffer","Ljava/nio/ByteBuffer;");

//-----------------------

}
/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeCompare
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeCompare(
    JNIEnv *env, jclass thisClass, jlong _comm1, jlong _comm2) {

  int result;
  int errCode;

  MPI_Comm comm1 = (MPI_Comm) (intptr_t) _comm1;
  MPI_Comm comm2 = (MPI_Comm) (intptr_t) _comm2;

  errCode = MPI_Comm_compare(comm1, comm2, &result);
  // here return the result as MPJ Express macros
  // as per MPJ Express 0.38
  // NO_RANK = -1, IDENT = 0, CONGRUENT = 3, SIMILAR = 1, UNEQUAL = 2;
  // UNDEFINED = -1; 

  if (errCode == MPI_SUCCESS) {
    //	printf("nativeCompare result = %d \n",result);
    if (result == MPI_IDENT) {
      return IDENT;
    } else if (result == MPI_CONGRUENT) {
      return CONGRUENT;
    } else if (result == MPI_SIMILAR) {
      return SIMILAR;
    } else if (result == MPI_UNEQUAL) {
      return UNEQUAL;
    }
  } else if (errCode == MPI_ERR_GROUP || errCode == MPI_ERR_ARG) {
    //error !
    return -1; // UNDEFINED
  }

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeBarrier
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeBarrier
(JNIEnv *env, jobject thisClass, jlong comm) {

  MPI_Barrier((MPI_Comm) (intptr_t) comm);

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeBcast
 * Signature: (JLmpjbuf/Buffer;II)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeBcast
(JNIEnv *env, jobject thisClass, jlong comm, jobject buf, jint count, jint root) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;

  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)buf);

  MPI_Bcast(buffer_address,count,MPI_BYTE,root,mpi_comm);

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeGather
 * Signature: (JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;IIZ)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeGather
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jint send_counter, jobject recv_buf, jint recv_count, jint root, jboolean isRoot) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;

  //- For SendBuffer -
  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */

  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)send_buf);

  // - For Recv Buffer -
  /*Declarations for ByteBuffer */

  char *r_buffer_address=NULL;

  /* Get the  ByteBuffer Related things.. */

  if(isRoot) {
    r_buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)recv_buf);

  }

  MPI_Gather(buffer_address,send_counter,MPI_BYTE,r_buffer_address,
      recv_count,MPI_BYTE,root,mpi_comm);

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeGatherv
 * Signature: (JLmpjbuf/Buffer;ILmpjbuf/Buffer;[I[II)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeGatherv
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf, jint send_count,
    jobject recv_buf, jintArray recv_counts, jintArray displs, jint root) {

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeScatter
 * Signature: (JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;II)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeScatter
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jint send_count, jobject recv_buf, jint recv_count, jint root) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;
  int rank = -1;
  MPI_Comm_rank(mpi_comm, &rank);
  //- For SendBuffer -
  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */
  if(rank == root)
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)send_buf);

  // - For Recv Buffer -
  /*Declarations for ByteBuffer */

  char *r_buffer_address=NULL;

  /* Get the  ByteBuffer Related things.. */

  r_buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)recv_buf);

  MPI_Scatter(buffer_address,send_count,MPI_BYTE,r_buffer_address,
      recv_count,MPI_BYTE,root,mpi_comm);
  /*
   * MPI_Scatter(void* send_data, int send_count, MPI_Datatype send_datatype, 
   * void* recv_data, int recv_count, MPI_Datatype recv_datatype, int root, 
   * MPI_Comm communicator)
   */

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeScatterv
 * Signature: (JLmpjbuf/Buffer;[I[ILmpjbuf/Buffer;II)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeScatterv
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jintArray send_counts, jintArray displs, jobject recv_buf,
    jint recv_count, jint root) {

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeAllgather
 * Signature: (JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;I)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeAllgather
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jint send_count, jobject recv_buf, jint recv_count) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;

  //- For SendBuffer -
  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */
  //if(rank == root)
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)send_buf);

  // - For Recv Buffer -
  /*Declarations for ByteBuffer */

  char *r_buffer_address=NULL;

  /* Get the  ByteBuffer Related things.. */

  r_buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)recv_buf);

  MPI_Allgather(buffer_address,send_count,MPI_BYTE,r_buffer_address,
      recv_count,MPI_BYTE,mpi_comm);
  /*
   * MPI_Allgather(void* send_data, int send_count, MPI_Datatype send_datatype,
   *  void* recv_data, int recv_count, MPI_Datatype recv_datatype, 
   * MPI_Comm communicator)
   */

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeAllgatherv
 * Signature: (JLmpjbuf/Buffer;ILmpjbuf/Buffer;[I[I)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeAllgatherv
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jint send_count, jobject recv_buf, jintArray recv_counts, jintArray displs) {

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    Alltoall
 * Signature: (JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;I)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeAlltoall
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jint send_count, jobject recv_buf, jint recv_count) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;

  //- For SendBuffer -
  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */

  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)send_buf);

  // - For Recv Buffer -
  /*Declarations for ByteBuffer */

  char *r_buffer_address=NULL;

  /* Get the  ByteBuffer Related things.. */

  r_buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)recv_buf);

  MPI_Alltoall(buffer_address,send_count,MPI_BYTE,r_buffer_address,
      recv_count,MPI_BYTE,mpi_comm);
  /*
   * int MPI_Alltoall(void *sendbuf, int sendcount, MPI_Datatype sendtype, 
   void *recvbuf, int recvcount, MPI_Datatype recvtype, 
   MPI_Comm comm)
   */
}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    Alltoallv
 * Signature: (JLjava/nio/ByteBuffer;[I[ILjava/nio/ByteBuffer;[I[I)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeAlltoallv
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jintArray send_counts, jintArray send_displ, jobject recv_buf,
    jintArray recv_counts, jintArray recv_displ) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;
  jboolean isCopy = JNI_TRUE;

  //- For SendBuffer -
  /*Declarations for staticBuffer */

  char * buffer_address = NULL;

  /* Get the static Buffer Related things.. */

  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)send_buf);

  // - For Recv Buffer -
  /*Declarations for ByteBuffer */

  char *r_buffer_address=NULL;

  /* Get the  ByteBuffer Related things.. */

  r_buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)recv_buf);

  //get send count 
  jint *native_send_count = (*env)->GetIntArrayElements(env, send_counts, &isCopy);
  //get send_displ
  jint *native_send_displ = (*env)->GetIntArrayElements(env, send_displ, &isCopy);
  //get recv_count
  jint *native_recv_count = (*env)->GetIntArrayElements(env, recv_counts, &isCopy);
  //get recv_displ
  jint*native_recv_displ = (*env)->GetIntArrayElements(env, recv_displ, &isCopy);

  MPI_Alltoallv(buffer_address,(int*)native_send_count, (int*)native_send_displ,
      MPI_BYTE, r_buffer_address, (int*)native_recv_count,
      (int*)native_recv_displ, MPI_BYTE, mpi_comm);

  //release
  (*env)->ReleaseIntArrayElements(env,send_counts,native_send_count,JNI_ABORT);
  (*env)->ReleaseIntArrayElements(env,send_displ,native_send_displ,JNI_ABORT);
  (*env)->ReleaseIntArrayElements(env,recv_counts,native_recv_count,JNI_ABORT);
  (*env)->ReleaseIntArrayElements(env,recv_displ,native_recv_displ,JNI_ABORT);

  /*
   * int MPI_Alltoallv(const void *sendbuf, const int *sendcounts,
   const int *sdispls, MPI_Datatype sendtype, void *recvbuf,
   const int *recvcounts, const int *rdispls, MPI_Datatype recvtype,
   MPI_Comm comm)
   */

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeReduce
 * Signature: (JLjava/lang/Object;Ljava/nio/ByteBuffer;IIII)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeReduce
(JNIEnv *env, jobject thisObject, jlong comm, jobject send_buf,
    jobject recv_buf, jint count, jint type_, jint op_, jint root) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;
  int rank;
  int eerCode = MPI_Comm_rank(mpi_comm, &rank);

  int n;

  void *native_send_array = NULL;
  void *native_result = NULL;
  jboolean isCopy = JNI_TRUE;
  MPI_Op op;
  MPI_Datatype type;
  int size_of_type = -1; //UNDEFINED 

  //set op -- TODO define a macro for this setting 
  if(op_ == MPJ_MAX_CODE) {
    op = MPI_MAX;
  } else if(op_ == MPJ_MIN_CODE) {
    op = MPI_MIN;
  } else if(op_ == MPJ_SUM_CODE) {
    op = MPI_SUM;
  } else if(op_ == MPJ_PROD_CODE) {
    op = MPI_PROD;
  } else if(op_ == MPJ_LAND_CODE) {
    op = MPI_LAND;
  } else if(op_ == MPJ_BAND_CODE) {
    op = MPI_BAND;
  } else if(op_ == MPJ_LOR_CODE) {
    op = MPI_LOR;
  } else if(op_ == MPJ_BOR_CODE) {
    op = MPI_BOR;
  } else if(op_ == MPJ_LXOR_CODE) {
    op = MPI_LXOR;
  } else if(op_ == MPJ_BXOR_CODE) {
    op = MPI_BXOR;
  } else if(op_ == MPJ_MAXLOC_CODE) {
    op = MPI_MAXLOC;
  } else if(op_ == MPJ_MINLOC_CODE) {
    op = MPI_MINLOC;
  }

  //define a macro for this #TODO
  if(type_ == MPJ_INT) {
    type = MPI_INT;
    jintArray send_array = (jintArray) send_buf;

    native_send_array = (*env)->GetIntArrayElements(env, send_array,
        &isCopy);

    if(rank == root) //only for root
    //TODO naive case may be its 2 not 1 for MAXLOC ?	
    native_result = malloc(sizeof(int) * count);
  } else if(type_ == MPJ_FLOAT) {

    type = MPI_FLOAT;
    jfloatArray send_array = (jfloatArray) send_buf;

    native_send_array = (*env)->GetFloatArrayElements(env, send_array,
        &isCopy);

    if(rank == root)	 //only for root
    //TODO naive case may be its 2 not 1 for MAXLOC ?	
    native_result = malloc(sizeof(float) * count);

  } else if(type_ == MPJ_DOUBLE) {

    type = MPI_DOUBLE;
    jdoubleArray send_array = (jdoubleArray) send_buf;

    native_send_array = (*env)->GetDoubleArrayElements(env, send_array,
        &isCopy);

    if(rank == root)	 //only for root
    //TODO naive case may be its 2 not 1 for MAXLOC ?	
    native_result = malloc(sizeof(double) * count);

  } else if(type_ == MPJ_SHORT) {

    type = MPI_SHORT;
    jshortArray send_array = (jshortArray) send_buf;

    native_send_array = (*env)->GetShortArrayElements(env, send_array,
        &isCopy);

    if(rank == root)	 //only for root
    //TODO naive case may be its 2 not 1 for MAXLOC ?	
    native_result = malloc(sizeof(short) * count);

  } else if(type_ == MPJ_LONG) {

    type = MPI_LONG;
    jlongArray send_array = (jlongArray) send_buf;

    native_send_array = (*env)->GetLongArrayElements(env, send_array,
        &isCopy);

    if(rank == root)	 //only for root
    //TODO naive case may be its 2 not 1 for MAXLOC ?	
    native_result = malloc(sizeof(long) * count);

  }

  // call native MPI_Reduce (.. );  
  MPI_Reduce(native_send_array, native_result, count, type, op,root,
      mpi_comm);

  //define a macro for this #TODO
  if(type_ == MPJ_INT) {
    jintArray send_array = (jintArray) send_buf;
    (*env)->ReleaseIntArrayElements(env,send_array,native_send_array,0);

    if(rank == root) {

      jclass bbclass = (*env)->FindClass(env, "java/nio/ByteBuffer" );
      jmethodID putMethod = (*env)->GetMethodID(env, bbclass, "putInt", "(II)Ljava/nio/ByteBuffer;");
      int i = 0;
      int val, index;
      size_of_type = 4; //because int is 32-bit
      for(i=0; i<count; i++) {
        val = ((int*)native_result)[i];
        //index = i*size_of_type;
        index = i*4;//hard coding this

        (*env)->CallObjectMethod(env, recv_buf, putMethod, index, val );

      }
    }
  }if(type_ == MPJ_FLOAT) {
    jfloatArray send_array = (jfloatArray) send_buf;
    (*env)->ReleaseFloatArrayElements(env,send_array,native_send_array,0);

    if(rank == root) {

      jclass bbclass = (*env)->FindClass(env, "java/nio/ByteBuffer" );
      jmethodID putMethod = (*env)->GetMethodID(env, bbclass, "putFloat", "(IF)Ljava/nio/ByteBuffer;");
      int i = 0;
      float val;
      int index;
      size_of_type = 4; //because float is 32-bit
      for(i=0; i<count; i++) {
        val = ((float*)native_result)[i];
        //index = i*size_of_type;
        index = i*4;//hard coding this

        (*env)->CallObjectMethod(env, recv_buf, putMethod, index, val );

      }
    }
  }if(type_ == MPJ_DOUBLE) {
    jdoubleArray send_array = (jdoubleArray) send_buf;
    (*env)->ReleaseDoubleArrayElements(env,send_array,native_send_array,0);

    if(rank == root) {

      jclass bbclass = (*env)->FindClass(env, "java/nio/ByteBuffer" );
      jmethodID putMethod = (*env)->GetMethodID(env, bbclass, "putDouble", "(ID)Ljava/nio/ByteBuffer;");
      int i = 0;
      double val;
      int index;
      size_of_type = 8; //because float is 64-bit
      for(i=0; i<count; i++) {
        val = ((double*)native_result)[i];
        //index = i*size_of_type;
        index = i*8;//hard coding this

        (*env)->CallObjectMethod(env, recv_buf, putMethod, index, val );

      }
    }
  }if(type_ == MPJ_SHORT) {
    jshortArray send_array = (jshortArray) send_buf;
    (*env)->ReleaseShortArrayElements(env,send_array,native_send_array,0);

    if(rank == root) {

      jclass bbclass = (*env)->FindClass(env, "java/nio/ByteBuffer" );
      jmethodID putMethod = (*env)->GetMethodID(env, bbclass, "putShort", "(IS)Ljava/nio/ByteBuffer;");
      int i = 0;
      short val;
      int index;
      size_of_type = 2; //because short is 16-bit
      for(i=0; i<count; i++) {
        val = ((short*)native_result)[i];
        //index = i*size_of_type;
        index = i*2;//hard coding this

        (*env)->CallObjectMethod(env, recv_buf, putMethod, index, val );

      }
    }
  }if(type_ == MPJ_LONG) {
    jlongArray send_array = (jlongArray) send_buf;
    (*env)->ReleaseLongArrayElements(env,send_array,native_send_array,0);

    if(rank == root) {

      jclass bbclass = (*env)->FindClass(env, "java/nio/ByteBuffer" );
      jmethodID putMethod = (*env)->GetMethodID(env, bbclass, "putLong", "(IJ)Ljava/nio/ByteBuffer;");
      int i = 0;
      long val;
      int index;
      size_of_type = 8; //because long is 64-bit
      for(i=0; i<count; i++) {
        val = ((long*)native_result)[i];
        //index = i*size_of_type;
        index = i*8;//hard coding this

        (*env)->CallObjectMethod(env, recv_buf, putMethod, index, val );

      }
    }
  }

  free(native_result);
}
//TODO: use thisObject instead of thisClass be consistent
/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeAllreduce
 * Signature: (JLmpjbuf/Buffer;Lmpjbuf/Buffer;ILmpi/Op;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeAllreduce
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jobject recv_buf, jint count, jobject op) {

}

/*
 * Class:     mpjdev_natmpjdev_Intracomm
 * Method:    nativeReduce_scatter
 * Signature: (JLmpjbuf/Buffer;Lmpjbuf/Buffer;[ILmpi/Op;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Intracomm_nativeReduce_1scatter
(JNIEnv *env, jobject thisClass, jlong comm, jobject send_buf,
    jobject recv_buf, jintArray recv_counts, jobject op) {

}

