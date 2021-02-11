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
 * File         : mpjdev_natmpjdev_Comm.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

#include "mpjdev_natmpjdev_Comm.h"
#include "mpi.h"
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
#include "mpjdev_natmpjdev_shared.h"
//for ltdl
#ifdef __unix__
#include <dlfcn.h>
#elif __linux__
#include <dlfcn.h>
#endif

jfieldID mpjdev_natmpjdev_Comm_CommhandleID;

static JavaVM *jvm;
static int mpiTagUB;
static void *mpilibhandle=NULL;

jclass CL_mpjbuf_NIOBuffer;
jclass CL_mpjbuf_Buffer;
jclass CL_mpjdev_Status;
// used in send/recv
jfieldID FID_mpjbuf_Buffer_staticBuffer;
jfieldID FID_mpjbuf_Buffer_dynamicBuffer;
jfieldID FID_mpjbuf_Buffer_capacity;
jfieldID FID_mpjbuf_Buffer_size;
jfieldID FID_mpjbuf_NIOBuffer_buffer;

jfieldID mpjdev_Status_sourceID;
jfieldID mpjdev_Status_tagID;
//jfieldID mpjdev_Status_stathandleID;
jfieldID mpjdev_Status_indexID;
jfieldID mpjdev_Status_numEls;
jfieldID mpjdev_Status_countInBytes;

/* JNI caching
 * JNI_OnLoad()
 *
 */

jint JNI_OnLoad(JavaVM *vm, void *reserved) {

  JNIEnv *env;
  jvm = vm; //the static

  if (JNI_OK != (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6)) {
    exit(1);
  }

  //caching classes and flieds

  CL_mpjbuf_Buffer = (*env)->NewGlobalRef(env,
      (*env)->FindClass(env, "mpjbuf/Buffer"));
  CL_mpjbuf_NIOBuffer = (*env)->NewGlobalRef(env,
      (*env)->FindClass(env, "mpjbuf/NIOBuffer"));

  FID_mpjbuf_Buffer_size = (*env)->GetFieldID(env, CL_mpjbuf_Buffer, "size",
      "I");

  CL_mpjdev_Status = (*env)->NewGlobalRef(env,
      (*env)->FindClass(env, "mpjdev/Status"));

  mpjdev_Status_sourceID = (*env)->GetFieldID(env, CL_mpjdev_Status, "source",
      "I");

  mpjdev_Status_tagID = (*env)->GetFieldID(env, CL_mpjdev_Status, "tag", "I");

  mpjdev_Status_indexID = (*env)->GetFieldID(env, CL_mpjdev_Status, "index",
      "I");

  mpjdev_Status_numEls = (*env)->GetFieldID(env, CL_mpjdev_Status, "numEls",
      "I");

  mpjdev_Status_countInBytes = (*env)->GetFieldID(env, CL_mpjdev_Status,
      "countInBytes", "I");

  // x = (*env)->GetFieldID(env,CL_mpjdev_Status,"numEls","I");

  FID_mpjbuf_Buffer_capacity = (*env)->GetFieldID(env, CL_mpjbuf_Buffer,
      "capacity", "I");

  FID_mpjbuf_Buffer_staticBuffer = (*env)->GetFieldID(env, CL_mpjbuf_Buffer,
      "staticBuffer", "Lmpjbuf/RawBuffer;");

  FID_mpjbuf_Buffer_dynamicBuffer = (*env)->GetFieldID(env, CL_mpjbuf_Buffer,
      "dynamicBuffer", "[B");

  FID_mpjbuf_NIOBuffer_buffer = (*env)->GetFieldID(env, CL_mpjbuf_NIOBuffer,
      "buffer", "Ljava/nio/ByteBuffer;");

  if (FID_mpjbuf_Buffer_staticBuffer && FID_mpjbuf_Buffer_size
      && FID_mpjbuf_NIOBuffer_buffer && FID_mpjbuf_Buffer_capacity) {

    return JNI_VERSION_1_6;

  } else {
    fprintf(stderr, "\n Fatal error getting FIDs");
    exit(3);
  }

}
/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    loadGlobalLibraries
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpjdev_natmpjdev_Comm_loadGlobalLibraries
 (JNIEnv *env, jclass thisObject) {
    //This will make sure the library is loaded
    // specially in the case of Open MPI
    #ifdef OMPI_MPI_H 
    if (NULL == (mpilibhandle = dlopen("libmpi.so",
                                       RTLD_NOW | RTLD_GLOBAL))) {
        return JNI_FALSE;
    }
    #endif 
    return JNI_TRUE;
}
/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    getTagUB
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Comm_getTagUB(JNIEnv *env,
    jclass thisClass) {

  MPI_Aint  * maxTag;
  //void * maxTag;
  int error, flag;
  
  error = MPI_Comm_get_attr(MPI_COMM_WORLD, MPI_TAG_UB, &maxTag, &flag);
  
  if(error != MPI_SUCCESS || !flag ){
  printf( " ERROR IN native mpitagub\n");
  }
 
  return *maxTag;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    getWorld
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_getWorld(JNIEnv *env,
    jclass thisClass) {
  return (jlong) MPI_COMM_WORLD;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    dup
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_dup(JNIEnv *env,
    jobject thisObject, jlong comm) {

  MPI_Comm newComm;
  int errCode = MPI_Comm_dup((MPI_Comm) (intptr_t) comm, &newComm);

  return (long) newComm;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    rank
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Comm_rank(JNIEnv *env,
    jobject thisObject, jlong comm) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;
  int rank;
  int eerCode = MPI_Comm_rank(mpi_comm, &rank);

  return rank;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    size
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_mpjdev_natmpjdev_Comm_size(JNIEnv *env,
    jobject thisObject, jlong comm) {

  MPI_Comm mpi_comm = (MPI_Comm) (intptr_t) comm;
  int size;
  int eerCode = MPI_Comm_size(mpi_comm, &size);

  return size;

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    create
 * Signature: (J[I)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_create(JNIEnv *env,
    jobject thisObject, jlong comm, jintArray ids) {

  MPI_Comm newComm;
  MPI_Group existingGroup, newGroup;

  int size;
  jint *pids;
  int errCode;

  MPI_Comm existingComm = (MPI_Comm) (intptr_t) comm;
  MPI_Comm_group(existingComm, &existingGroup);

  size = (*env)->GetArrayLength(env, ids);
  pids = (*env)->GetIntArrayElements(env, ids, 0); // 0 or JNI_COPY?
  errCode = MPI_Group_incl(existingGroup, size, (int*) pids, &newGroup);

  (*env)->ReleaseIntArrayElements(env, ids, pids, JNI_ABORT);

  errCode = MPI_Comm_create(existingComm, newGroup, &newComm);

  if (newComm == MPI_COMM_NULL) {
    //	printf("Nativ: Comm - is NULL\n");
    return 0;
  } else
    return (jlong) newComm;

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeSplit
 * Signature: (JII)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_nativeSplit(JNIEnv *env,
    jobject thisObject, jlong comm, jint color, jint key) {

  int errCode;
  MPI_Comm newComm;
  MPI_Comm existingComm = (MPI_Comm) (intptr_t) comm;

  errCode = MPI_Comm_split(comm, color, key, &newComm);

  if (newComm == MPI_COMM_NULL) {
    printf("Nativ: Comm  Split- newComm is returning NULL\n");
    return 0;
  } else
    return (jlong) newComm;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeCreate
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_nativeCreate(JNIEnv *env,
    jobject thisObject, jlong group) {

  int errCode;
  MPI_Comm newComm;

  MPI_Group existingGroup = (MPI_Group) (intptr_t) group;
  MPI_Comm existingComm = (MPI_Comm)(*env)->GetLongField(env, thisObject,
      mpjdev_natmpjdev_Comm_CommhandleID);

  errCode = MPI_Comm_create(existingComm, existingGroup, &newComm);

  if (newComm == MPI_COMM_NULL) {
    //   printf("Nativ: Comm - nativeCreate is returning NULL\n");
    return 0;
  } else
    return (jlong) newComm;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeCreateIntercomm
 * Signature: (JJIII)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_nativeCreateIntercomm(
    JNIEnv *env, jobject thisObject, jlong peerComm_, jlong localComm_,
    jint localleader, jint remoteleader, jint tag) {

  int errCode;
  MPI_Comm peerComm = (MPI_Comm) (intptr_t) peerComm_;
  MPI_Comm localComm = (MPI_Comm) (intptr_t) localComm_;
  MPI_Comm newIntercomm;

  errCode = MPI_Intercomm_create(localComm, localleader, peerComm, remoteleader,
      tag, &newIntercomm);

  if (errCode == MPI_SUCCESS) {
    //printf("Nativ: Comm - nativeIntercommCreate MPI_SUCCESS \n");	
  } else {
    printf("Nativ: Comm - nativeIntercommCreate returned not MPI_SUCCESS\n");
  }

  return (jlong) newIntercomm;
}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_free
(JNIEnv *env, jobject thisObject, jlong comm) {

  MPI_Comm existingComm = (MPI_Comm) (intptr_t) comm;
  int errCode = MPI_Comm_free(&existingComm);

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeInit
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeInit
(JNIEnv *env, jclass thisClass, jobjectArray args) {
  //the logic of this function is almost same as of mpi-Java

  jclass string = (*env)->FindClass(env, "java/lang/String");
  int len = (*env)->GetArrayLength(env, args);
  char ** sargs = (char**) calloc(len+1, sizeof(char*));
  int errCode;
  int rank, len_of_proc_name;
  char name[MPI_MAX_PROCESSOR_NAME];
  jobject newArgs;

  int i;
  int flag;

  //parse the args array element wise and populate the sargs
  for (i=0; i<len; i++) {
    int slen;

    jstring jc = (jstring)(*env)->GetObjectArrayElement(env, args, i);
    slen = strlen((*env)->GetStringUTFChars(env, jc, 0)) + 1; //why plus one?
    sargs[i] = (char*) calloc(slen, sizeof(char));

    #ifdef WIN32
    strcpy_s(sargs[i], slen, (*env)->GetStringUTFChars(env, jc, 0));
    #elif _WIN64
    strcpy_s(sargs[i], slen, (*env)->GetStringUTFChars(env, jc, 0));	
    #elif _WIN32
    strcpy_s(sargs[i], slen, (*env)->GetStringUTFChars(env, jc, 0));
    #elif __unix__
    strcpy(sargs[i], (*env)->GetStringUTFChars(env, jc, 0));
    #elif __linux__
    strcpy(sargs[i], (*env)->GetStringUTFChars(env, jc, 0));
    #endif
  }

  errCode = MPI_Init(&len, &sargs);

  //cache the handle
  mpjdev_natmpjdev_Comm_CommhandleID = (*env)->GetFieldID(env, thisClass,
      "handle", "J");

  MPI_Comm_get_attr(MPI_COMM_WORLD, MPI_TAG_UB, &mpiTagUB, &flag);

  /*
   * Print the name of the computer node where this rank is executed
   * */
  MPI_Comm_rank(MPI_COMM_WORLD,&rank); 
  MPI_Get_processor_name(name, &len_of_proc_name);
  printf("Starting process <%d> on on <%s>\n", rank, name);
  fflush(stdout); 
  
  /* we are not returning the new arguments (the modified ones from MPI_Init()??
   * but mpi-Java is returning. I am just providing that logic of returning the
   * new arguments in this comment section, perhaps for future use....
   
   jobject newArgs = (*env)->NewObjectArray(env, len, string, NULL);
   
   for(i=0, i<len, i++){
   jstring jc = (*env)->NewStringUTF(env, sargs[i]);
   (*env)->SetObjectArrayElement(env, newArgs, i, jc);
   }
   
   return newArgs;
   */

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeFinish
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeFinish
(JNIEnv *env, jclass thisClass) {

  int errCode = MPI_Finalize();

  if (errCode != MPI_SUCCESS) {
    printf("Error in MPI_Finalize()\n");
    fflush(stdout);
  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    group
 * Signature: ()J
 *
 * This function returns the default group of the CommhandleID
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Comm_group(JNIEnv *env,
    jobject thisObject) {

  MPI_Group group;

  MPI_Comm existingComm = (MPI_Comm)(*env)->GetLongField(env, thisObject,
      mpjdev_natmpjdev_Comm_CommhandleID);
  MPI_Comm_group(existingComm, &group);

  return (jlong) group;

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    GetComm
 * Signature: (I)V
 *
 * This function sets the Comm.
 * 0 = COMM_NULL
 * 1 = COMM_SELF
 * 2 = COMM_WORLD is mostly the case
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_GetComm
(JNIEnv *env, jobject thisObject, jint type) {

  switch (type) {
    case 0:
    (*env)->SetLongField(env, thisObject, mpjdev_natmpjdev_Comm_CommhandleID,
        (jlong)MPI_COMM_NULL);
    break;
    case 1:
    (*env)->SetLongField(env, thisObject, mpjdev_natmpjdev_Comm_CommhandleID,
        (jlong)MPI_COMM_SELF);
    break;
    case 2:
    (*env)->SetLongField(env, thisObject, mpjdev_natmpjdev_Comm_CommhandleID,
        (jlong)MPI_COMM_WORLD);
    break;
    default:
    break;
  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeSend
 * Signature: (JLmpjbuf/Buffer;IIII)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeSend
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint dest, jint tag,
    jint sbufLen, jint dbufLen) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;

  /*Declarations for staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;
  char * buffer_address = NULL;

  /* Declarations for dynamicBuffer */

  jboolean isCopy = JNI_TRUE;
  jbyteArray dynamicBuffer;
  jbyte * dBuffer;

  /* Get the static Buffer Related things.. */

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (*env)->GetObjectField(env,staticBuffer,FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)directBuffer);
  jlong capacity = (*env)->GetDirectBufferCapacity(env,(jobject) directBuffer);

  //.. write the first eight bytes ..
  //   _________________________  	  
  //   | E | X | X | X | DSIZE |
  //   -------------------------
  if(dbufLen > 0) {

    char encoding = 1;
    buffer_address[0] = encoding;
    buffer_address[4] = (((unsigned int) dbufLen) >> 24) & 0xFF;
    buffer_address[5] = (((unsigned int) dbufLen) >> 16) & 0xFF;
    buffer_address[6] = (((unsigned int) dbufLen) >> 8) & 0xFF;
    buffer_address[7] = ((unsigned int) dbufLen) & 0xFF;

  } else {
    char encoding = 0;
    buffer_address[0] = encoding;
  }
  //TODO:  in case of dynamic buffer Optimize this by doing isend here and imediately a send 
  // and later a wait() on this isend for staticbuffer
  MPI_Send(buffer_address,sbufLen,MPI_BYTE,dest,tag,mpi_comm);

  if(dbufLen > 0) {
    dynamicBuffer = (jbyteArray)(*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_dynamicBuffer);

    dBuffer = (*env)->GetByteArrayElements(env,dynamicBuffer,&isCopy);
    //TODO: do something about this magic number
    MPI_Send(dBuffer,dbufLen,MPI_BYTE,dest,tag+10001,mpi_comm);
    (*env)->ReleaseByteArrayElements(env,dynamicBuffer,dBuffer,0);
  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeRecv
 * Signature: (JLmpjbuf/Buffer;IIILmpjdev/Status;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeRecv
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint size, jint src,
    jint tag, jobject status) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;

  jboolean isCopy = JNI_TRUE;

  /* Declarations of staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;
  MPI_Status mpi_status, mpi_status_dyn;
  int elements;
  char * buffer_address = NULL;

  /* Declarations of Dynamic Buffer */

  jbyteArray darr;
  jbyte * dBuffer;
  int dbuf_len = 0;

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (jbyteArray)(*env)->GetObjectField(env,staticBuffer,FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
      (jobject)directBuffer);

  int src_ = src;
  int tag_ = tag;

  if(src == -2)
  src_ = MPI_ANY_SOURCE;
  if(tag == -2)
  tag_ = MPI_ANY_TAG;

  // size = sizeInBytes of StaticBuffer
  MPI_Recv(buffer_address,size,MPI_BYTE,src_,tag_,comm,&mpi_status);

  char encoding = 0;
  int x;

  encoding = buffer_address[0];
  if(encoding == 1) {
    x = (((int)(unsigned char) buffer_address[4]) << 24) |
    (((int)(unsigned char) buffer_address[5]) << 16) |
    (((int)(unsigned char) buffer_address[6]) << 8) |
    (((int)(unsigned char) buffer_address[7]) );

    dbuf_len = x;
  }
  // Following 2 lines are important!

  MPI_Get_count(&mpi_status, MPI_BYTE, &elements);

  // setting this from nativ/Comm.c in recv() from sectionsize
  (*env)->SetIntField(env,buf,FID_mpjbuf_Buffer_size,elements);

  // I am also setting this because we need it in mpi/Status Get_count
  // method: TODO remove magic numbers
  (*env)->SetIntField(env, status, mpjdev_Status_countInBytes,
      (jint) (elements - 8-8));// 8 is the size of section header
  // and 8 recv overhead

  (*env)->SetIntField(env, status, mpjdev_Status_sourceID,
      mpi_status.MPI_SOURCE);

  (*env)->SetIntField(env, status, mpjdev_Status_tagID,
      mpi_status.MPI_TAG);

  //recv dynamic buf
  if(dbuf_len > 0) {

    darr = (*env)->NewByteArray (env, dbuf_len);
    // perhaps no need for this - use malloc instead
    dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);

    MPI_Recv(dBuffer,dbuf_len,MPI_BYTE,src_,tag_+10001,comm,&mpi_status_dyn);

    (*env)->SetByteArrayRegion(env,darr,0,dbuf_len,dBuffer);

    jmethodID setdbuf = (*env)->GetMethodID(env, CL_mpjbuf_Buffer,
        "setDynamicBuffer", "([B)V");
    (*env)->CallVoidMethod(env, buf, setdbuf, darr);

  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeSsend
 * Signature: (JLmpjbuf/Buffer;IIII)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeSsend
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint dest, jint tag,
    jint sbufLen, jint dbufLen) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;

  /*Declarations for staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;
  char * buffer_address = NULL;

  /* Declarations for dynamicBuffer */

  jboolean isCopy = JNI_TRUE;
  jbyteArray dynamicBuffer;
  jbyte * dBuffer;

  /* Get the static Buffer Related things.. */

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (*env)->GetObjectField(env,staticBuffer,FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)directBuffer);
  jlong capacity = (*env)->GetDirectBufferCapacity(env,(jobject) directBuffer);

  if(dbufLen > 0) {

    char encoding = 1;
    buffer_address[0] = encoding;
    buffer_address[4] = (((unsigned int) dbufLen) >> 24) & 0xFF;
    buffer_address[5] = (((unsigned int) dbufLen) >> 16) & 0xFF;
    buffer_address[6] = (((unsigned int) dbufLen) >> 8) & 0xFF;
    buffer_address[7] = ((unsigned int) dbufLen) & 0xFF;

  } else {
    char encoding = 0;
    buffer_address[0] = encoding;
  }
  MPI_Ssend(buffer_address,sbufLen,MPI_BYTE,dest,tag,mpi_comm);

  if(dbufLen > 0) {
    dynamicBuffer = (jbyteArray)(*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_dynamicBuffer);

    dBuffer = (*env)->GetByteArrayElements(env,dynamicBuffer,&isCopy);
    //TODO: do something about this magic number
    MPI_Ssend(dBuffer,dbufLen,MPI_BYTE,dest,tag+10001,mpi_comm);
    (*env)->ReleaseByteArrayElements(env,dynamicBuffer,dBuffer,0);
  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeIssend
 * Signature: (JLmpjbuf/Buffer;IIIILmpjdev/natmpjdev/NativeSendRequest;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeIssend
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint dest,
    jint tag, jint sbufLen, jint dbufLen, jobject request) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;
  MPI_Request mpi_request;

  /*
   * Acquiring the handles on request (the java NativeSendRequest)
   * These can go in the JNI_OnLoad() 
   */

  jclass native_send_req_class = (*env)->GetObjectClass(env, request);
  jfieldID bufferhandleID =
  (*env)->GetFieldID(env, native_send_req_class, "bufferHandle",
      "Lmpjbuf/Buffer;");
  jfieldID dbufreqhandleID =
  (*env)->GetFieldID(env, native_send_req_class, "dbufHandle", "J");

  jfieldID dbuflenID =
  (*env)->GetFieldID(env, native_send_req_class, "dbuflen", "I");

  jfieldID reqhandleID = (*env)->GetFieldID(env,native_send_req_class, "handle","J");

  /*Declarations for staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;
  char * buffer_address = NULL;

  /* Declarations for dynamicBuffer */

  jboolean isCopy = JNI_TRUE;
  jbyteArray dynamicBuffer;
  jbyte * dBuffer;

  /* Get the static Buffer Related things.. */

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (*env)->GetObjectField(env,staticBuffer,FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)directBuffer);

  if(dbufLen > 0) {

    char encoding = 1;
    buffer_address[0] = encoding;
    buffer_address[4] = (((unsigned int) dbufLen) >> 24) & 0xFF;
    buffer_address[5] = (((unsigned int) dbufLen) >> 16) & 0xFF;
    buffer_address[6] = (((unsigned int) dbufLen) >> 8) & 0xFF;
    buffer_address[7] = ((unsigned int) dbufLen) & 0xFF;

  } else {
    char encoding = 0;
    buffer_address[0] = encoding;
  }

  MPI_Issend(buffer_address,sbufLen,MPI_BYTE,dest,tag,mpi_comm, &mpi_request);

  /*
   * Set the handles of mpi_request to the request (the java one)
   */
  (*env)->SetLongField(env, request, reqhandleID, (jlong)mpi_request);

  (*env)->SetObjectField(env, request, bufferhandleID, buf);

  if(dbufLen > 0) {

    dynamicBuffer = (jbyteArray)(*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_dynamicBuffer);

    MPI_Request mpi_request_dyn;
    dBuffer = (*env)->GetByteArrayElements(env,dynamicBuffer,&isCopy);
    MPI_Issend(dBuffer,dbufLen,MPI_BYTE,dest,tag+10001,mpi_comm,&mpi_request_dyn);

    /*
     * Set the handles of mpi_request_dyn to the request (the java one)
     */
    (*env)->SetIntField(env, request, dbuflenID, dbufLen);

    (*env)->SetLongField(env, request, dbufreqhandleID,
        (jlong) mpi_request_dyn);

  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeIsend
 * Signature: (JLmpjbuf/Buffer;IIIILmpjdev/natmpjdev/NativeSendRequest;I)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeIsend
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint dest, jint tag,
    jint sbufLen, jint dbufLen, jobject request) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;
  MPI_Request mpi_request;

  /*
   * Acquiring the handles on request (the java NativeSendRequest)
   * these can go in the JNI_OnLoad() 
   */

  jclass native_send_req_class = (*env)->GetObjectClass(env, request);
  jfieldID bufferhandleID =
  (*env)->GetFieldID(env, native_send_req_class, "bufferHandle",
      "Lmpjbuf/Buffer;");
  jfieldID dbufreqhandleID =
  (*env)->GetFieldID(env, native_send_req_class, "dbufHandle", "J");

  jfieldID dbuflenID =
  (*env)->GetFieldID(env, native_send_req_class, "dbuflen", "I");

  jfieldID reqhandleID = (*env)->GetFieldID(env,native_send_req_class, "handle","J");

  /*Declarations for staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;
  char * buffer_address = NULL;

  /* Declarations for dynamicBuffer */

  jboolean isCopy = JNI_TRUE;
  jbyteArray dynamicBuffer;
  jbyte * dBuffer;

  /* Get the static Buffer Related things.. */

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (*env)->GetObjectField(env,staticBuffer,FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,(jobject)directBuffer);

  if(dbufLen > 0) {

    char encoding = 1;
    buffer_address[0] = encoding;
    buffer_address[4] = (((unsigned int) dbufLen) >> 24) & 0xFF;
    buffer_address[5] = (((unsigned int) dbufLen) >> 16) & 0xFF;
    buffer_address[6] = (((unsigned int) dbufLen) >> 8) & 0xFF;
    buffer_address[7] = ((unsigned int) dbufLen) & 0xFF;

  } else {
    char encoding = 0;
    buffer_address[0] = encoding;
  }

  MPI_Isend(buffer_address,sbufLen,MPI_BYTE,dest,tag,mpi_comm, &mpi_request);

  /*
   * Set the handles of mpi_request to the request (the java one)
   */
  (*env)->SetLongField(env, request, reqhandleID, (jlong)mpi_request);

  (*env)->SetObjectField(env, request, bufferhandleID, buf);

  if(dbufLen > 0) {

    dynamicBuffer = (jbyteArray)(*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_dynamicBuffer);

    MPI_Request mpi_request_dyn;
    dBuffer = (*env)->GetByteArrayElements(env,dynamicBuffer,&isCopy);
    MPI_Isend(dBuffer,dbufLen,MPI_BYTE,dest,tag+10001,mpi_comm,&mpi_request_dyn);

    /*
     * Set the handles of mpi_request_dyn to the request (the java one)
     */
    (*env)->SetIntField(env, request, dbuflenID, dbufLen);

    (*env)->SetLongField(env, request, dbufreqhandleID,
        (jlong) mpi_request_dyn);

  }

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeIrecv
 * Signature: (JLmpjbuf/Buffer;IILmpjdev/Status;Lmpjdev/natmpjdev/NativeRecvRequest;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeIrecv
(JNIEnv *env, jobject thisObject, jlong comm, jobject buf, jint src, jint tag,
    jobject status, jobject request) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)comm;
  MPI_Request mpi_request;

  /* Trying to get the Request's bufferHandleID */
  jclass native_recv_req_class = (*env)->GetObjectClass(env, request);

  jfieldID reqhandleID = (*env)->GetFieldID(env,native_recv_req_class,"handle","J");

  jfieldID bufferhandleID =
  (*env)->GetFieldID(env, native_recv_req_class,
      "bufferHandle", "Lmpjbuf/Buffer;");

  jboolean isCopy = JNI_TRUE;

  /* Declarations of staticBuffer */

  jobject staticBuffer;
  jbyteArray directBuffer;

  int elements;
  char * buffer_address = NULL;

  /* Declarations of Dynamic Buffer */

  jbyteArray darr;
  jbyte * dBuffer;

  staticBuffer = (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directBuffer = (jbyteArray)(*env)->GetObjectField(env,staticBuffer,
      FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
      (jobject)directBuffer);

  jlong capacity = (*env)->GetDirectBufferCapacity(
      env,(jobject) directBuffer);

  int src_ = src;
  int tag_ = tag;

  if(src == -2)
  src_ = MPI_ANY_SOURCE;
  if(tag == -2)
  tag_ = MPI_ANY_TAG;

  MPI_Irecv(buffer_address,capacity,MPI_BYTE,src_,tag_,mpi_comm,&mpi_request);

  (*env)->SetLongField(env,request,reqhandleID,(jlong)mpi_request);

  (*env)->SetObjectField(env,request,bufferhandleID,buf);

}

/*
 * Class:     mpjdev_natmpjdev_Comm
 * Method:    nativeProbe
 * Signature: (JIILmpjdev/Status;)V
 */
JNIEXPORT void JNICALL Java_mpjdev_natmpjdev_Comm_nativeProbe
(JNIEnv *env, jobject thisObject, jlong comm, jint src, jint tag, jobject status) {

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t) comm;
  MPI_Status mpi_status;
  int elements;

  // Probe for an incoming message from process 
  MPI_Probe(src, tag, mpi_comm, &mpi_status);

  // probe returns, the status object with the size and other
  // attributes of the incoming message. Get the size of the message
  MPI_Get_count(&mpi_status, MPI_BYTE, &elements);

  // setting this because we need it in mpi/Status Get_count
  // method: TODO remove these magic numbers
  (*env)->SetIntField(env, status, mpjdev_Status_countInBytes,
      (jint) (elements - 8 - 8));// 8 is the size of section header
  // 8 recv overhead

  (*env)->SetIntField(env, status, mpjdev_Status_sourceID,
      mpi_status.MPI_SOURCE);

  (*env)->SetIntField(env, status, mpjdev_Status_tagID,
      mpi_status.MPI_TAG);

}

