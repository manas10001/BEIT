/** The MIT License 
	
Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)
*/	
#include "myriexpress.h"
#include <stdio.h>
#include "xdev_mxdev_MXDevice.h"
#include "jni.h"
#include "mxdev_const.h" 

//***********************************************************
//** block diagram for the tag ..it is a 64 bit number     **
//** <---16-----><------16------><--1--><--------31------->** 
//** ------------------------------------------------------**
//** |  context  |     src      |  PRI  |      tag        |**
//** ------------------------------------------------------**
//***********************************************************

static JavaVM *jvm;

int myRank ;  
int procs ;

jclass   CL_mpjdev_Status ;
jclass   CL_mpjbuf_Buffer;
jclass   CL_xdev_mxdev_MXProcessID;
jclass   CL_mpjbuf_NIOBuffer;
jclass   CL_mpjbuf_Type;
jclass   CL_xdev_ProcessID; 
jclass   CL_xdev_mxdev_MXRequest ; 

jfieldID FID_mpjbuf_Buffer_staticBuffer;
jfieldID FID_mpjbuf_Buffer_dynamicBuffer;
jfieldID FID_mpjbuf_Buffer_size;
jfieldID FID_mpjbuf_Buffer_capacity;
jfieldID FID_mpjbuf_NIOBuffer_buffer;
jfieldID FID_mpjbuf_Type_code;
jfieldID FID_xdev_ProcessID_uuid;
jfieldID FID_xdev_mxdev_MXRequest_status ; 
jfieldID FID_xdev_mxdev_MXRequest_requestStruct ; 
jfieldID processhandleID; 
jfieldID processidID; 
jfieldID status_src_ID ; 
jfieldID status_tag_ID ;
jfieldID countInBytesID;

/* Caching of JNI stuff ..*/

jint JNI_OnLoad(JavaVM *vm, void *reserved) {

  JNIEnv *env;        
  jvm=vm; 

  if (JNI_OK!=(*vm)->GetEnv(vm,(void **)&env,JNI_VERSION_1_4)) {
    exit(1);
  }

  // why am i not deleting these two global references ... (?)
  CL_mpjbuf_Buffer =  (*env)->NewGlobalRef(env, 
		  (*env)->FindClass(env,"mpjbuf/Buffer"));
  CL_xdev_mxdev_MXProcessID =  (*env)->NewGlobalRef(env, 
		  (*env)->FindClass(env,"xdev/mxdev/MXProcessID"));
  CL_xdev_mxdev_MXRequest = (*env)->FindClass(env,"xdev/mxdev/MXRequest");
  CL_mpjbuf_NIOBuffer =  (*env)->NewGlobalRef(env, 
		  (*env)->FindClass(env,"mpjbuf/NIOBuffer"));
  CL_mpjbuf_Type = (*env)->FindClass(env,"mpjbuf/Type");
  CL_xdev_ProcessID = (*env)->FindClass(env,"xdev/ProcessID");
  CL_mpjdev_Status = (*env)->FindClass(env,"mpjdev/Status");

  FID_mpjbuf_Buffer_size = (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"size","I");
  FID_mpjbuf_Buffer_capacity = 
	  (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"capacity","I");
  FID_mpjbuf_Buffer_staticBuffer = 
   (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"staticBuffer","Lmpjbuf/RawBuffer;");
  FID_mpjbuf_Buffer_dynamicBuffer = 
    (*env)->GetFieldID(env,CL_mpjbuf_Buffer,"dynamicBuffer","[B");
  FID_mpjbuf_NIOBuffer_buffer = 
   (*env)->GetFieldID(env,CL_mpjbuf_NIOBuffer,"buffer","Ljava/nio/ByteBuffer;");
  FID_mpjbuf_Type_code = (*env)->GetFieldID(env,CL_mpjbuf_Type,"code","I");
  FID_xdev_ProcessID_uuid = 
     (*env)->GetFieldID(env,CL_xdev_ProcessID,"uuid","Ljava/util/UUID;");
  processhandleID =
     (*env)->GetFieldID(env, CL_xdev_mxdev_MXProcessID,"processHandle", "J") ;
  processidID = (*env)->GetFieldID(env, CL_xdev_mxdev_MXProcessID, 
		    "id", "I");
  status_src_ID = (*env)->GetFieldID(env, CL_mpjdev_Status, 
		    "source", "I");
  status_tag_ID = (*env)->GetFieldID(env, CL_mpjdev_Status, 
		    "tag", "I");
  countInBytesID = (*env)->GetFieldID(env, CL_mpjdev_Status, 
		    "countInBytes", "I");
  FID_xdev_mxdev_MXRequest_status = (*env)->GetFieldID(env, 
		  CL_xdev_mxdev_MXRequest,"status","Lmpjdev/Status;"); 
  FID_xdev_mxdev_MXRequest_requestStruct = (*env)->GetFieldID(env, 
		  CL_xdev_mxdev_MXRequest,"requestStruct","J"); 
  
  if (FID_mpjbuf_Buffer_staticBuffer && FID_mpjbuf_Buffer_size \
      && FID_mpjbuf_NIOBuffer_buffer && FID_mpjbuf_Buffer_capacity \
      && FID_xdev_ProcessID_uuid) {
    return JNI_VERSION_1_4; 
  } else {
    {fprintf(stderr,"\n Fatal error getting FIDs"); exit(3);}
  }

}

/* Caching of JNI stuff (done)..*/

mx_endpoint_t local_endpoint; 

uint32_t filter = 0xcafebabe; 

//extern mx_endpoint_t local_endpoint;
static mx_endpoint_addr_t * peer_endpoints = NULL; 
  
/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeInit
 * Signature: ([Ljava/lang/String;I[Ljava/lang/String;[II[Lxdev/mxdev/MXProcessID;JJ)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeInit
  (JNIEnv *env, jobject jthis, jobjectArray argv, jint rank,
     jobjectArray processNames, jintArray ranks, jint nprocs, 
     jobjectArray pids , jlong msb, jlong lsb) {

  mx_return_t rc;
  rc = mx_init();
  mx_set_error_handler( MX_ERRORS_RETURN );

  if(rc == MX_SUCCESS) {
    //printf("mx_init called \n");
  }

  rc = mx_open_endpoint(MX_ANY_NIC, MX_ANY_ENDPOINT, filter, 0, 0, 
                      &local_endpoint);
    //printf(" nic id upper <%d> \n", MX_U32(nic_id));
    //printf(" nic id lower <%d> \n", MX_L32(nic_id));
  if(rc == MX_SUCCESS) { 
    //printf("opened a local end-point \n"); 
  }

  //sleep(5); 

  jclass CL_java_util_UUID = (*env)->FindClass(env, "java/util/UUID");
  jmethodID uuid_c =  (*env)->GetMethodID(env, CL_java_util_UUID,  
		       "<init>", "(JJ)V") ; 
  jmethodID pid_c =  (*env)->GetMethodID(env, CL_xdev_mxdev_MXProcessID,  
		       "<init>", "(Ljava/util/UUID;)V") ; 

  myRank = rank ; 
  procs = nprocs ;
  peer_endpoints = (mx_endpoint_addr_t *)  
	  malloc(procs*sizeof(mx_endpoint_addr_t)); 
  //.. .. 	
  uint64_t nic_id;
  //printf("native init method \n"); 	  
  

  jobject pid;
  int len = (*env)->GetArrayLength(env,processNames);
  char** pNames = (char**)calloc(len, sizeof(char*));
  int i=0;
  char *pName;
  
  for (i=0; i<len; i++) {
     pName =(jstring)(*env)->GetObjectArrayElement(env,processNames,i);
     pNames[i] = (*env)->GetStringUTFChars(env,pName,0);
  }
	
  /* connect loop */ 
  for(i=0 ; i<nprocs ; i++) {
    //printf(" connecting to <%s> \n", pNames[i]);
    rc = mx_hostname_to_nic_id( pNames[i] , &nic_id);
    if(rc == MX_SUCCESS) { 
      //printf("getting nic_id from hostname \n"); 
    }

    
    NC: 
    //printf("calling connect() \n"); 
    //printf(" nic id upper <%d> \n", MX_U32(nic_id));
    //printf(" nic id lower <%d> \n", MX_L32(nic_id));
    rc = mx_connect(local_endpoint, nic_id, 0, filter, 
  		  MX_INFINITE, &peer_endpoints [i]);
    //printf("called connect() \n");
    if(rc == MX_SUCCESS) { 
      //printf("connected to remote host <%s> \n", pNames[i]);
    } else {
      //printf("could not connect to <%s> \n", pNames[i]); 
      //printf("trying again ...");
      goto NC; //need to get rid of this ..we just need a do while loop. 
    }
    
  }

  //fflush(stdout);
  // ids set up could be done here ...
  //printf("nativeIdsSetup rank <%d> of <%d> \n", myRank, procs); 	  
  
  // sending accessories
  mx_segment_t buffer_desc[3];
  mx_request_t send_handle[procs] ;
  uint64_t send_tag ;
  mx_status_t send_status ;
  uint32_t result ;
  
  // irecv accessories 
  mx_segment_t recv_buffer[3];
  mx_request_t recv_handle ;
  uint64_t match_tag;
  uint64_t match_mask;
  mx_status_t recv_status;
  uint32_t recv_result;

  buffer_desc[0].segment_ptr = &msb; 
  buffer_desc[0].segment_length = 64; 
  buffer_desc[1].segment_ptr = &lsb; 
  buffer_desc[1].segment_length = 64;  
  buffer_desc[2].segment_ptr = &rank; 
  buffer_desc[2].segment_length = 4;  
  //printf("rank <%d> \n",rank); 
  //this needs to be fixed ..basically use the tag macros once I have 
  //defined them ...
  send_tag = myRank << 32; //| UINT64_C(0x0000000000000000);   

  for(i=0 ; i<procs ; i++) {
    if( i == myRank) { 
      continue;     	    
    } 
    //printf("pro <%d> calling send to pro <%d> \n", 
    //		    myRank, i); fflush(stdout);
    rc = mx_isend(local_endpoint, buffer_desc, 3, peer_endpoints[i], 
		    send_tag, NULL, &send_handle[i]); 
    //if( rc == MX_SUCCESS ) { 
    //  printf("pro <%d> called isend to pro <%d> \n", 
    //	    myRank, i); fflush(stdout);
    //}
  }

  //printf("pro <%d> reached A \n",myRank ); fflush(stdout);
  
  jlong _msb, _lsb; 
  jint _rank ; 
  match_mask = UINT64_C(0x00000000ffffffff);   

  for(i=0 ; i<procs ; i++) { 
    if( i == myRank) { 
      continue;     	    
    }

    recv_buffer[0].segment_ptr = &_msb; 
    recv_buffer[0].segment_length = 64; 
    recv_buffer[1].segment_ptr = &_lsb; 
    recv_buffer[1].segment_length = 64;  
    recv_buffer[2].segment_ptr = &_rank; 
    recv_buffer[2].segment_length = 4;  
    
    match_tag = i << 32 ;
    
    //printf("pro <%d> receiving from process <%d> \n", 
    //		    myRank, i); fflush(stdout);
    rc = mx_irecv(local_endpoint, recv_buffer, 3, match_tag, match_mask,
		 NULL , &recv_handle);
    //printf(" calling wait \n"); fflush(stdout); 
    rc = mx_wait(local_endpoint, & recv_handle, MX_INFINITE, 
		  &recv_status, &result);
    if(rc == MX_SUCCESS) { 
    //  printf("pro <%d> received from process <%d> \n", 
    //		    myRank, i); fflush(stdout);
    }else {
      printf("error \n");  	    
    }
    
    jobject uid = (*env)->NewObject(env, CL_java_util_UUID, 
	     uuid_c, _msb, _lsb);
    pid = (*env)->NewObject(env, CL_xdev_mxdev_MXProcessID,  
	     pid_c, uid); 

    (*env)->SetLongField(env,pid, processhandleID, (jlong)&peer_endpoints[i]);
    (*env)->SetIntField(env,pid, processidID, _rank);
    (*env)->SetObjectArrayElement(env,pids,i,pid);

  }
  
  //printf("process <%d> reached B \n",myRank ); fflush(stdout);
  for(i=0 ; i<procs ; i++) { 
    if( i == myRank) { 
      continue;     	    
    }
    //printf("pro <%d> calling send_wait to process <%d> \n", 
    //		    myRank, i); fflush(stdout);
    mx_wait(local_endpoint, &send_handle[i], MX_INFINITE, 
    		  &recv_status, &recv_result);  
    //printf("pro <%d> called send_wait to process <%d> \n", 
    //		    myRank, i); fflush(stdout);
  }

  //a. <get objectarrayelement for this process>
  pid = (*env)->GetObjectArrayElement(env,pids,myRank);

  //b. <set long field for it ...>
  (*env)->SetLongField(env,pid, processhandleID, 
		       (jlong)&peer_endpoints[myRank]);

  //c. <set integer field for it ...>
  (*env)->SetIntField(env,pid, processidID, myRank);
 

  //printf("process <%d> reached C \n",myRank ); fflush(stdout) ;
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeSsend
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IIII)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeSsend
  (JNIEnv *env, jobject this, jobject buf, jobject dstID, 
   jint tag, jint context, jint sbuf_length, jint dbuf_length) {
	  
  /* MX accessories for calling mx_isend */	  
  mx_return_t rc ; 
  mx_request_t send_handle;
  mx_status_t status;
  mx_segment_t buffer_desc[1];
  uint64_t match_send, dbuf_tag ;   
  uint32_t result;
  mx_endpoint_addr_t * dest;
  dest = (mx_endpoint_addr_t *) 
    ((*env)->GetLongField(env, dstID, processhandleID )) ;
  
  match_send = PRI_MATCH(context, myRank, tag);
  //printf("send_recv U32 <%x> \n",MX_U32(match_send));fflush(stdout);
  //printf("send_recv L32 <%x> \n",MX_L32(match_send));fflush(stdout); 

  /* static buffer related declarations */
  char *buffer_address=NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;

  /* dynamic buffer related declarations */
  jboolean isCopy=JNI_TRUE;
  jbyteArray dynamicBuffer ;
  jbyte* dBuffer;

  /* get static buffer related stuff */
  staticBuffer = 
	  (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                       staticBuffer, FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);

  /* get dynamic buffer related stuff */
  dynamicBuffer = 
    (jbyteArray) (*env)->GetObjectField(env,buf,
                         FID_mpjbuf_Buffer_dynamicBuffer);
  if(dbuf_length > 0) {
    dBuffer = (*env)->GetByteArrayElements(env, dynamicBuffer, &isCopy);
  }
  //.. write the first eight bytes ..
  //   _________________________  	  
  //   | E | X | X | X | DSIZE |
  //   -------------------------
  char encoding = 1; 
  buffer_address[0] = encoding ;
  buffer_address[4] = (((unsigned int) dbuf_length) >> 24) & 0xFF ; 
  buffer_address[5] = (((unsigned int) dbuf_length) >> 16) & 0xFF; 
  buffer_address[6] = (((unsigned int) dbuf_length) >> 8) & 0xFF ; 
  buffer_address[7] = ((unsigned int) dbuf_length) & 0xFF; 
	  
  /* compose message, sort out tag/context and remote endpoints */
  buffer_desc[0].segment_ptr = buffer_address ;
  buffer_desc[0].segment_length = sbuf_length+8; //+offset;

  /* send message */
  //printf("native:send sending \n"); 
  rc = mx_issend(local_endpoint, buffer_desc, 1, * dest, match_send, 
		  NULL, &send_handle); 
  //printf("native:send sent \n"); 

  mx_segment_t dbuf_desc [1] ;
  dbuf_tag = SEC_MATCH(context, myRank, tag); 
  mx_request_t dbufsend_handle;
  
  dbuf_desc[0].segment_ptr = dBuffer ; 
  dbuf_desc[0].segment_length = dbuf_length;
  
  if(dbuf_length > 0) { 
    rc = mx_issend(local_endpoint, dbuf_desc, 1, * dest, dbuf_tag, 
		  NULL, &dbufsend_handle); 
  }

  // so which one should be called here first? 
  rc = mx_wait(local_endpoint, &send_handle, MX_INFINITE, 
		  &status, &result);  
  if(dbuf_length > 0) { 
    rc = mx_wait(local_endpoint, &dbufsend_handle, MX_INFINITE, 
		  &status, &result);  
  }
}
/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeSend
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IIII)V
 */
  
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeSend
  (JNIEnv *env, jobject this, jobject buf, jobject dstID, 
   jint tag, jint context, jint sbuf_length, jint dbuf_length) {
  //printf(" nativeSend first statement \n"); fflush(stdout); 
  /* MX accessories for calling mx_isend */	  
  mx_return_t rc ; 
  mx_request_t send_handle;
  mx_status_t status;
  mx_segment_t buffer_desc[1];
  uint64_t match_send, dbuf_tag ;   
  uint32_t result;
  mx_endpoint_addr_t * dest;
  dest = (mx_endpoint_addr_t *) 
    ((*env)->GetLongField(env, dstID, processhandleID )) ;
  
  match_send = PRI_MATCH(context, myRank, tag);
  //printf("send_recv U32 <%x> \n",MX_U32(match_send));fflush(stdout);
  //printf("send_recv L32 <%x> \n",MX_L32(match_send));fflush(stdout); 

  /* static buffer related declarations */
  char *buffer_address=NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;

  /* dynamic buffer related declarations */
  jboolean isCopy=JNI_TRUE;
  jbyteArray dynamicBuffer ;
  jbyte* dBuffer;

  /* get static buffer related stuff */
  staticBuffer = 
	  (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                       staticBuffer, FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);

  //printf("1 \n");fflush(stdout); 
  /* get dynamic buffer related stuff */
  dynamicBuffer = 
    (jbyteArray) (*env)->GetObjectField(env,buf,
                         FID_mpjbuf_Buffer_dynamicBuffer);
  if(dbuf_length > 0) {
    dBuffer = (*env)->GetByteArrayElements(env, dynamicBuffer, &isCopy);
  }
  //.. write the first eight bytes ..
  //   _________________________  	  
  //   | E | X | X | X | DSIZE |
  //   -------------------------
  char encoding = 1; 
  buffer_address[0] = encoding ;
  buffer_address[4] = (((unsigned int) dbuf_length) >> 24) & 0xFF ; 
  buffer_address[5] = (((unsigned int) dbuf_length) >> 16) & 0xFF; 
  buffer_address[6] = (((unsigned int) dbuf_length) >> 8) & 0xFF ; 
  buffer_address[7] = ((unsigned int) dbuf_length) & 0xFF; 
	  
  /* compose message, sort out tag/context and remote endpoints */
  buffer_desc[0].segment_ptr = buffer_address ;
  buffer_desc[0].segment_length = sbuf_length+8; //+offset;

  /* send message */
  //printf("native:send sending \n"); 
  rc = mx_isend(local_endpoint, buffer_desc, 1, * dest, match_send, 
		  NULL, &send_handle); 
  //printf("native:send sent \n"); 

  mx_segment_t dbuf_desc [1] ;
  dbuf_tag = SEC_MATCH(context, myRank, tag); 
  mx_request_t dbufsend_handle;
  
  dbuf_desc[0].segment_ptr = dBuffer ; 
  dbuf_desc[0].segment_length = dbuf_length;
  
  if(dbuf_length > 0) { 
    rc = mx_isend(local_endpoint, dbuf_desc, 1, * dest, dbuf_tag, 
  		  NULL, &dbufsend_handle); 
  }

  // so which one should be called here first? 
  rc = mx_wait(local_endpoint, &send_handle, MX_INFINITE, 
		  &status, &result);  
  if(dbuf_length > 0) { 
    rc = mx_wait(local_endpoint, &dbufsend_handle, MX_INFINITE, 
		  &status, &result);  
  }
  //printf("nativeSend finished \n"); fflush(stdout);  
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeIssend
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IILxdev/mxdev/MXSendRequest;)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeIssend
  (JNIEnv *env, jobject obj1, jobject buf, jobject dstID, jint tag, 
   jint context, jint sbuf_length, jint dbuf_length, jobject req) {
  //.. .. 
 int offset = 8;		    
 /* stuff required for non-blocking */	  
  jclass req_class = (*env)->GetObjectClass(env, req);
  jfieldID reqhandleID =
	(*env)->GetFieldID(env, req_class, "handle", "J") ;
  jfieldID dbufreqhandleID = 
	(*env)->GetFieldID(env, req_class, "dbufHandle", "J");
  jfieldID lepID =
	(*env)->GetFieldID(env, req_class, "localEndpointHandle", "J") ;
  jfieldID bufferhandleID =
	(*env)->GetFieldID(env, req_class, "bufferHandle", "Lmpjbuf/Buffer;") ;
  jfieldID dbuflenID =
	(*env)->GetFieldID(env, req_class, "dbuflen", "I") ;
			     
  /* MX accessories for calling mx_isend */	  
  mx_return_t rc ; 
  mx_request_t send_handle;
  mx_status_t status;
  mx_segment_t buffer_desc[1];
  uint64_t match_send, dbuf_tag ;   
  uint32_t result;
  mx_endpoint_addr_t * dest;
  dest = (mx_endpoint_addr_t *) 
    ((*env)->GetLongField(env, dstID, processhandleID )) ;
  
  match_send = PRI_MATCH(context, myRank, tag);
  //printf("send_recv U32 <%x> \n",MX_U32(match_send));fflush(stdout);
  //printf("send_recv L32 <%x> \n",MX_L32(match_send));fflush(stdout); 

  /* static buffer related declarations */
  char *buffer_address=NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;

  /* dynamic buffer related declarations */
  jboolean isCopy=JNI_TRUE;
  jbyteArray dynamicBuffer ;
  jbyte* dBuffer;

  /* get static buffer related stuff */
  staticBuffer = 
	  (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                       staticBuffer, FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);

  /* get dynamic buffer related stuff */
  dynamicBuffer = 
    (jbyteArray) (*env)->GetObjectField(env,buf,
                         FID_mpjbuf_Buffer_dynamicBuffer);
  if(dbuf_length > 0) {
    dBuffer = (*env)->GetByteArrayElements(env, dynamicBuffer, &isCopy);
  }
  //.. write the first eight bytes ..
  //   _________________________  	  
  //   | E | X | X | X | DSIZE |
  //   -------------------------
  char encoding = 1; 
  buffer_address[0] = encoding ;
  buffer_address[4] = (((unsigned int) dbuf_length) >> 24) & 0xFF ; 
  buffer_address[5] = (((unsigned int) dbuf_length) >> 16) & 0xFF; 
  buffer_address[6] = (((unsigned int) dbuf_length) >> 8) & 0xFF ; 
  buffer_address[7] = ((unsigned int) dbuf_length) & 0xFF; 
	  
  /* compose message, sort out tag/context and remote endpoints */
  buffer_desc[0].segment_ptr = buffer_address ;
  buffer_desc[0].segment_length = sbuf_length+offset;

  /* send message */
  //printf("native:send sending \n"); 
  rc = mx_issend(local_endpoint, buffer_desc, 1, * dest, match_send, 
		  NULL, &send_handle); 
  //printf("native:send sent \n"); 

  (*env)->SetLongField(env,req,reqhandleID,(jlong)send_handle);
  (*env)->SetLongField(env,req,lepID,(jlong)local_endpoint);
  (*env)->SetObjectField(env,req,bufferhandleID,buf);

  mx_segment_t dbuf_desc [1] ;
  mx_request_t dbufsend_handle;
  
  dbuf_desc[0].segment_ptr = dBuffer ; 
  dbuf_desc[0].segment_length = dbuf_length;
  
  if(dbuf_length > 0) { 
    dbuf_tag = SEC_MATCH(context, myRank, tag); 
    rc = mx_isend(local_endpoint, dbuf_desc, 1, * dest, dbuf_tag, 
		  NULL, &dbufsend_handle); 
  }
  
  (*env)->SetIntField(env,req,dbuflenID,dbuf_length);
  (*env)->SetLongField(env,req,dbufreqhandleID,(jlong)dbufsend_handle );
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeIsend
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IILxdev/mxdev/MXSendRequest;I)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeIsend
  (JNIEnv *env, jobject obj1, jobject buf, jobject dstID, 
   jint tag, jint context, jint sbuf_length, jint dbuf_length,
   jobject req, jint offset) {
  	
 /* stuff required for non-blocking */	  
  jclass req_class = (*env)->GetObjectClass(env, req);
  jfieldID reqhandleID =
	(*env)->GetFieldID(env, req_class, "handle", "J") ;
  jfieldID dbufreqhandleID = 
	(*env)->GetFieldID(env, req_class, "dbufHandle", "J");
  jfieldID lepID =
	(*env)->GetFieldID(env, req_class, "localEndpointHandle", "J") ;
  jfieldID bufferhandleID =
	(*env)->GetFieldID(env, req_class, "bufferHandle", "Lmpjbuf/Buffer;") ;
  jfieldID dbuflenID =
	(*env)->GetFieldID(env, req_class, "dbuflen", "I") ;
			     
  /* MX accessories for calling mx_isend */	  
  mx_return_t rc ; 
  mx_request_t send_handle;
  mx_status_t status;
  mx_segment_t buffer_desc[1];
  uint64_t match_send, dbuf_tag ;   
  uint32_t result;
  mx_endpoint_addr_t * dest;
  dest = (mx_endpoint_addr_t *) 
    ((*env)->GetLongField(env, dstID, processhandleID )) ;
  
  match_send = PRI_MATCH(context, myRank, tag);
  //printf("send_recv U32 <%x> \n",MX_U32(match_send));fflush(stdout);
  //printf("send_recv L32 <%x> \n",MX_L32(match_send));fflush(stdout); 

  /* static buffer related declarations */
  char *buffer_address=NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;

  /* dynamic buffer related declarations */
  jboolean isCopy=JNI_TRUE;
  jbyteArray dynamicBuffer ;
  jbyte* dBuffer;

  /* get static buffer related stuff */
  staticBuffer = 
	  (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                       staticBuffer, FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);

  /* get dynamic buffer related stuff */
  dynamicBuffer = 
    (jbyteArray) (*env)->GetObjectField(env,buf,
                         FID_mpjbuf_Buffer_dynamicBuffer);
  if(dbuf_length > 0) {
    dBuffer = (*env)->GetByteArrayElements(env, dynamicBuffer, &isCopy);
  }
  //.. write the first eight bytes ..
  //   _________________________  	  
  //   | E | X | X | X | DSIZE |
  //   -------------------------
  char encoding = 1; 
  buffer_address[0] = encoding ;
  buffer_address[4] = (((unsigned int) dbuf_length) >> 24) & 0xFF ; 
  buffer_address[5] = (((unsigned int) dbuf_length) >> 16) & 0xFF; 
  buffer_address[6] = (((unsigned int) dbuf_length) >> 8) & 0xFF ; 
  buffer_address[7] = ((unsigned int) dbuf_length) & 0xFF; 
	  
  /* compose message, sort out tag/context and remote endpoints */
  buffer_desc[0].segment_ptr = buffer_address ;
  buffer_desc[0].segment_length = sbuf_length+offset;

  /* send message */
  //printf("native:send sending \n"); fflush(stdout);
  rc = mx_isend(local_endpoint, buffer_desc, 1, * dest, match_send, 
		  NULL, &send_handle); 

  if(rc != MX_SUCCESS) { 
    printf("error in isend"); fflush(stdout); 	  
  }
  //printf("native:send sent \n"); fflush(stdout);

  (*env)->SetLongField(env,req,reqhandleID,(jlong)send_handle);
  (*env)->SetLongField(env,req,lepID,(jlong)local_endpoint);
  (*env)->SetObjectField(env,req,bufferhandleID,buf);

  mx_segment_t dbuf_desc [1] ;
  dbuf_tag = SEC_MATCH(context, myRank, tag); 
  mx_request_t dbufsend_handle;
  
  dbuf_desc[0].segment_ptr = dBuffer ; 
  dbuf_desc[0].segment_length = dbuf_length;
  if(dbuf_length > 0) { 
    rc = mx_isend(local_endpoint, dbuf_desc, 1, * dest, dbuf_tag, 
		  NULL, &dbufsend_handle); 
  }
  
  (*env)->SetLongField(env,req,dbufreqhandleID,(jlong)dbufsend_handle );
  (*env)->SetIntField(env,req,dbuflenID,dbuf_length);

}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeRecv
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IILmpjdev/Status;I)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeRecv
  (JNIEnv *env, jobject this, jobject buf, jobject srcID, jint tag, 
   jint context, jobject status, jint any_src) {

  jboolean isCopy = JNI_TRUE;
  
  //jclass req_class = (*env)->GetObjectClass(env, req);
  //we probably need to get hold of status here ...
  int src_id = ((*env)->GetIntField(env, srcID, processidID )) ;

  /* static buffer declarations */
  char *buffer_address=NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;
  //int * sbuf_length; 
  int capacity ;

  /* dynamic buffer declarations */
  jbyteArray darr;
  jbyte* dBuffer;

  /* MX related declarations */
  mx_return_t rc ; 
  mx_request_t recv_handle ;
  mx_segment_t buffer_desc[1];
  uint64_t match_recv, match_recv2, match_mask , proc_mask, tag_mask ; 
  mx_status_t recv_status;
  uint32_t result;

  if(tag == ANY_TAG) {
    tag_mask = EMPTY ; 
  }
  else {
    tag_mask = MASK_TAG ; 
  }

  if(any_src == 1) {  
    src_id = 0 ;
    proc_mask = EMPTY; 
  }
  else {
    proc_mask = MASK_SRC ; 
  }

  match_mask =   MATCH_CONTEXT | proc_mask | tag_mask ;
  
  if(tag == ANY_TAG) { 	  
    match_recv = PRI_MATCH(context, src_id, 0);	  
    match_recv2 = SEC_MATCH(context, src_id, 0);
  } else {
    match_recv = PRI_MATCH(context, src_id, tag);	  
    match_recv2 = SEC_MATCH(context, src_id, tag);
  }

  //printf("src_id <%d> \n", src_id ); 
  //printf("context <%d> \n", context); 
  //printf("match_mask U32 <%x> \n",MX_U32(match_mask));
  //printf("match_recv U32 <%x> \n",MX_U32(match_recv));
  //printf("match_mask L32 <%x> \n",MX_L32(match_mask)); 
  //printf("match_recv L32 <%x> \n",MX_L32(match_recv)); 
  //fflush(stdout); 
  
  /* get static buffer related stuff */
  staticBuffer = 
          (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                                          staticBuffer,
                                          FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);
  capacity = (unsigned int) 
               (*env)->GetIntField(env,buf,FID_mpjbuf_Buffer_capacity);
 
  /* compose message ,sort out tag/context*/
  buffer_desc[0].segment_ptr = buffer_address; 
  buffer_desc[0].segment_length = capacity+8 ;//+offset ..

  /* recv the message */
  rc = mx_irecv(local_endpoint, buffer_desc, 1, match_recv, match_mask,
		  NULL, &recv_handle);

  /* wait for it complete */
  rc = mx_wait(local_endpoint, & recv_handle, MX_INFINITE, 
  		  &recv_status, &result);  
  //printf("********** \n");
  //printf("tag <%d> \n", GET_TAG(recv_status.match_info));
  //printf("src <%d> \n", GET_SRC(recv_status.match_info));
  //printf("********** \n");
  //status.src = recv_status.match_info ; 
  //need a translation back from src to srcID ...
  //status.tag = recv_status.match_info ; 

  (*env)->SetIntField(env,status, status_src_ID, 
		      GET_SRC(recv_status.match_info) );
  (*env)->SetIntField(env,status, status_tag_ID, 
		      GET_TAG(recv_status.match_info) );
  (*env)->SetIntField(env,status, countInBytesID , 
		      recv_status.msg_length - 16); //-8 is offset ...
  
  //printf("recv:req called wait for control message \n"); fflush(stdout);
  
  char encoding = 1; 
  int dbuf_length ; 
  //.. dbuf_length ..
  encoding = buffer_address[0] ;
  dbuf_length = 
	  (((int)(unsigned char) buffer_address[4]) << 24) |
	  (((int)(unsigned char) buffer_address[5]) << 16) |
	  (((int)(unsigned char) buffer_address[6]) << 8) |
	  (((int)(unsigned char) buffer_address[7]) ); 
  
  //printf("dbuf_length [after strange] <%d> \n",dbuf_length);
  
  //int dbuf_length = byte22int(buffer_address,0);
  int sbuf_length = recv_status.msg_length - 8 ; //- offset 
    
  if(dbuf_length > 0) { 
    darr = (*env)->NewByteArray (env, dbuf_length);
    dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);
    buffer_desc[0].segment_ptr = dBuffer; 
    buffer_desc[0].segment_length = dbuf_length;
  
    rc = mx_irecv(local_endpoint, buffer_desc, 1, match_recv2, match_mask,
		  NULL, &recv_handle);
  
    /* wait for it complete */
    rc = mx_wait(local_endpoint, & recv_handle, MX_INFINITE, 
		  &recv_status, &result); 
    
    (*env)->SetByteArrayRegion(env,darr,0,dbuf_length,dBuffer);       
    jmethodID setdbuf = (*env)->GetMethodID(env, CL_mpjbuf_Buffer, 
		       "setDynamicBuffer", "([B)V"); 
    (*env)->CallVoidMethod(env, buf, setdbuf, darr);

  }
 
  jmethodID set_size = (*env)->GetMethodID(env, CL_mpjbuf_Buffer,
		       "setSize", "(I)V"); 
  (*env)->CallVoidMethod(env, buf, set_size, sbuf_length ); 
  //---------------- this is non-blocking recv ...
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeIrecv
 * Signature: (Lmpjbuf/Buffer;Lxdev/ProcessID;IILmpjdev/Status;Lxdev/mxdev/MXRecvRequest;I)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeIrecv
  (JNIEnv *env, jobject obj1, jobject buf, jobject srcID, jint tag, 
   jint context, jobject status, jobject req, jint any_src) {

  jboolean isCopy = JNI_TRUE;
  
  /* We could move these to init methods */ 
  jclass req_class = (*env)->GetObjectClass(env, req);
  jfieldID matchrecvhandleID = 
	  (*env)->GetFieldID(env, req_class, "matchRecvHandle", "J") ;
  jfieldID ctrlmsghandleID = 
	  (*env)->GetFieldID(env, req_class, "ctrlMsgHandle", "J") ;
  jfieldID matchrecvmaskhandleID = 
	  (*env)->GetFieldID(env, req_class, "matchRecvMaskHandle", "J") ;
  jfieldID bufferaddresshandleID = 
	  (*env)->GetFieldID(env, req_class, "bufferAddressHandle", "J") ;
  jfieldID reqhandleID = 
	  (*env)->GetFieldID(env, req_class, "handle", "J") ;
  jfieldID lepID = 
	  (*env)->GetFieldID(env, req_class, "localEndpointHandle", "J") ;
  jfieldID bufferhandleID = 
	  (*env)->GetFieldID(env, req_class, 
			     "bufferHandle", "Lmpjbuf/Buffer;") ;

  int src_id = ((*env)->GetIntField(env, srcID, processidID )) ;

  /* static buffer declarations */
  char *buffer_address = NULL;
  jobject staticBuffer;
  jbyteArray directbuffer;
  //int * sbuf_length; 
  int capacity ;

  /* dynamic buffer declarations */
  jbyteArray darr;
  jbyte* dBuffer;

  /* MX related declarations */
  mx_return_t rc ; 
  mx_request_t recv_handle ;
  mx_segment_t buffer_desc[1];
  uint64_t match_recv, match_recv2, match_mask , proc_mask, tag_mask ; 
  mx_status_t recv_status;
  uint32_t result;

  if(tag == ANY_TAG) {
    tag_mask = EMPTY ; 
  }
  else {
    tag_mask = MASK_TAG ; 
  }

  if(any_src == 1) {  
    src_id = 0 ;
    proc_mask = EMPTY; 
  }
  else {
    proc_mask = MASK_SRC ; 
  }

  match_mask =   MATCH_CONTEXT | proc_mask | tag_mask ;
  
  if(tag == ANY_TAG) { 	  
    match_recv = PRI_MATCH(context, src_id, 0);	  
    match_recv2 = SEC_MATCH(context, src_id, 0);
  } else {
    match_recv = PRI_MATCH(context, src_id, tag);	  
    match_recv2 = SEC_MATCH(context, src_id, tag);
  }

  //printf("src_id <%d> \n", src_id ); 
  //printf("context <%d> \n", context); 
  //printf("match_mask U32 <%x> \n",MX_U32(match_mask));
  //printf("match_recv U32 <%x> \n",MX_U32(match_recv));
  //printf("match_mask L32 <%x> \n",MX_L32(match_mask)); 
  //printf("match_recv L32 <%x> \n",MX_L32(match_recv)); 
  //fflush(stdout); 
  
  /* get static buffer related stuff */
  staticBuffer = 
          (*env)->GetObjectField(env,buf,FID_mpjbuf_Buffer_staticBuffer);
  directbuffer = 
          (jbyteArray) (*env)->GetObjectField(env,
                                          staticBuffer,
                                          FID_mpjbuf_NIOBuffer_buffer);
  buffer_address = (char *)(*env)->GetDirectBufferAddress(env,
                            (jobject)directbuffer);
  capacity = (unsigned int) 
               (*env)->GetIntField(env,buf,FID_mpjbuf_Buffer_capacity);
 
  /* compose message ,sort out tag/context*/
  buffer_desc[0].segment_ptr = buffer_address; 
  buffer_desc[0].segment_length = capacity+8 ;//+offset ..
  
  //JRequestStruct *jreq = NULL; 
  //jreq = (JRequestStruct *) malloc(sizeof(JRequestStruct)) ;
  //jobject globalRequestObject = (*env)->NewGlobalRef(env, req); 
  //jreq->jrequest = globalRequestObject ; 

  /* recv the message */
  rc = mx_irecv(local_endpoint, buffer_desc, 1, match_recv, match_mask,
		  NULL, &recv_handle);  //jreq instead of NULL 
  if(rc != MX_SUCCESS) { 
    printf("error in irecv"); fflush(stdout); 	  
  }

  (*env)->SetLongField(env,req,reqhandleID,(jlong)recv_handle);
  (*env)->SetLongField(env,req,lepID,(jlong)local_endpoint);
  (*env)->SetObjectField(env,req,bufferhandleID,buf);
  (*env)->SetLongField(env,req,matchrecvhandleID,(jlong)match_recv2);
  (*env)->SetLongField(env,req,matchrecvmaskhandleID,(jlong)match_mask);
  (*env)->SetLongField(env,req,bufferaddresshandleID,(jlong)buffer_address);
  
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeIprobe
 * Signature: (Lxdev/ProcessID;IILmpjdev/Status;II)I
 */
JNIEXPORT jint JNICALL Java_xdev_mxdev_MXDevice_nativeIprobe
  (JNIEnv *env, jobject jthis, jobject srcID, jint tag, jint context, 
   jobject status, jint any_src, jint isCompleted ) {
  //printf("native probe method \n");	  
  mx_return_t rc; 
  mx_status_t c_status;
  uint64_t result = EMPTY ;
  uint64_t match_recv, proc_mask, tag_mask, match_mask ;
  int src_id = ((*env)->GetIntField(env, srcID, processidID )) ;
  
  if(tag == ANY_TAG) {
    tag_mask = EMPTY ; 
  }
  else {
    tag_mask = MASK_TAG ; 
  }

  if(any_src == 1) {  
    src_id = 0 ;
    proc_mask = EMPTY; 
  }
  else {
    proc_mask = MASK_SRC ; 
  }

  match_mask =   MATCH_CONTEXT | proc_mask | tag_mask ;
  
  if(tag == ANY_TAG) { 	  
    match_recv = PRI_MATCH(context, src_id, 0);	  
  } else {
    match_recv = PRI_MATCH(context, src_id, tag);	  
  }

  //printf(" result upper (b)<%d> \n", MX_U32(result));
  //printf(" result lower (b)<%d> \n", MX_L32(result));
  rc = mx_iprobe(local_endpoint, 
		match_recv,
		match_mask,
		&c_status,
		&result);
  //printf(" result upper (a)<%d> \n", MX_U32(result));
  //printf(" result lower (a)<%d> \n", MX_L32(result));
  
  if(result == EMPTY) { 
    //printf(" aint any message "); fflush(stdout);
  }
  else {
    //printf(" yes message "); fflush(stdout);
    isCompleted = 1; 	  
    (*env)->SetIntField(env,status, status_src_ID, 
		      GET_SRC(c_status.match_info) );
    (*env)->SetIntField(env,status, status_tag_ID, 
		      GET_TAG(c_status.match_info) );
    (*env)->SetIntField(env,status, countInBytesID , 
		      c_status.msg_length - 16); //-8 is offset ...
  }

  return isCompleted ; 
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeProbe
 * Signature: (Lxdev/ProcessID;IILmpjdev/Status;I)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeProbe
  (JNIEnv *env, jobject jthis, jobject srcID, jint tag, jint context, 
   jobject status, jint any_src) {
  //printf("native iprobe method \n");	  
  mx_return_t rc; 
  mx_status_t c_status;
  uint64_t result ;

  uint64_t match_recv, proc_mask, tag_mask, match_mask ;
  int src_id = ((*env)->GetIntField(env, srcID, processidID )) ;
  
  if(tag == ANY_TAG) {
    tag_mask = EMPTY ; 
  }
  else {
    tag_mask = MASK_TAG ; 
  }

  if(any_src == 1) {  
    src_id = 0 ;
    proc_mask = EMPTY; 
  }
  else {
    proc_mask = MASK_SRC ; 
  }

  match_mask =   MATCH_CONTEXT | proc_mask | tag_mask ;
  
  if(tag == ANY_TAG) { 	  
    match_recv = PRI_MATCH(context, src_id, 0);	  
    //match_recv2 = SEC_MATCH(context, src_id, 0);
  } else {
    match_recv = PRI_MATCH(context, src_id, tag);	  
    //match_recv2 = SEC_MATCH(context, src_id, tag);
  }

  rc = mx_probe(local_endpoint, 
		MX_INFINITE,   
		match_recv,
		match_mask,
		&c_status,
		&result);
  (*env)->SetIntField(env,status, status_src_ID, 
		      GET_SRC(c_status.match_info) );
  (*env)->SetIntField(env,status, status_tag_ID, 
		      GET_TAG(c_status.match_info) );
  (*env)->SetIntField(env,status, countInBytesID , 
		      c_status.msg_length - 16); //-8 is offset ...
} 

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativePeek
 * Signature: (Lmpjdev/Status;)J
 */
JNIEXPORT jlong JNICALL Java_xdev_mxdev_MXDevice_nativePeek
  (JNIEnv *env, jclass jthis, jobject status) {
  //printf(" nativePeek \n");fflush(stdout); 
  mx_request_t peekedRequest ; 
  mx_return_t rc ; 
  //mx_status_t status ; 
  uint32_t result ; 
  //JRequestStruct *jrequest ; 
  //jobject javaRequest, statusInRequest ; 
 
  rc = mx_peek(local_endpoint, MX_INFINITE, &peekedRequest, &result); 

  if(rc != MX_SUCCESS) { 
    printf(" error while peeking the message \n"); 
  }

  if(result) {
    //printf(" message peeked successfully \n");  fflush(stdout); 
  }

  /* commented because of hashtable approach ...

  rc = mx_wait( local_endpoint, &peekedRequest, 
                  MX_INFINITE, &status, &result); 

  jrequest = (JRequestStruct *) status.context ; 
  javaRequest =  jrequest->jrequest ; 
  statusInRequest = (*env)->GetObjectField( env, 
		  javaRequest, FID_xdev_mxdev_MXRequest_status ); 
  //.. save javaRequest in Request's long field ..
  (*env)->SetLongField(env,javaRequest, FID_xdev_mxdev_MXRequest_requestStruct, 
		       (jlong)jrequest);
  //printf(" returning from native peek \n"); fflush(stdout);  
  //jobject copiedJavaRequest = javaRequest ; 
  //(*env)->DeleteGlobalRef(env, javaRequest); 
  (*env)->SetIntField(env,statusInRequest, status_src_ID,
                      GET_SRC(status.match_info) );
  (*env)->SetIntField(env,statusInRequest, status_tag_ID,
                      GET_TAG(status.match_info) );
  (*env)->SetIntField(env,statusInRequest, countInBytesID,
                      status.msg_length - 8);
  printf(" length <%d> \n ",status.msg_length) ;  fflush(stdout); 
  return javaRequest ; 
  */ 
  // .. returnthe address of request struct .. 
  return peekedRequest ; 
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    deletePeekedRequest
 * Signature: (Lxdev/mxdev/MXRequest;J)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_deletePeekedRequest
  (JNIEnv *env, jclass jthis, jobject peekedRequest, jlong requestStruct) { 
  (*env)->DeleteGlobalRef(env, peekedRequest); 
  //JRequestStruct *jrequest = (JRequestStruct *) requestStruct ; 
  //free(jrequest); 
}

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativePeek
 * Signature: (Lmpjdev/Status;)V
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativePeek
  (JNIEnv *env, jobject jthis, jobject requestObject) {
  printf(" nativePeek \n"); 
  mx_request_t request ; 
  mx_return_t rc ; 
  uint32_t result ; 
  rc = mx_peek( local_endpoint, MX_INFINITE, &request, &result); 
  if(rc == MX_SUCCESS) { 
    //printf(" mx_peek successful \n"); fflush(stdout);           
  }
  if(result) { 
    printf(" a message has been peeked in nativeIwaitany \n"); fflush(stdout); 
  }
   
  // complete comms ...
  mx_status_t recv_status ; 

  rc = mx_wait( local_endpoint, &request, MX_INFINITE, &recv_status, &result); 
  JRequestStruct *jrequest = (JRequestStruct *) recv_status.context ; 
  jobject javaRequest =  jrequest->jrequest ; 
    printf("1 \n"); fflush(stdout); 

  jclass req_class = (*env)->GetObjectClass(env, javaRequest );
  
  //.. get the comms ..
  
  if(rc != MX_SUCCESS) {
    printf(" error while calling mx_wait \n"); 	  
  }
  
  //mx_status_t recv_status;
  mx_request_t reqhandle ;
  mx_endpoint_t mlep ;
  jobject buffer ;
  uint64_t match_recv;
  uint64_t match_mask;
  char * buffer_address;
  mx_segment_t buffer_desc[1];
  
  reqhandle = (mx_request_t) ((*env)->GetLongField(env, javaRequest, 
			  reqhandleID )) ;
  mx_request_t dreq ; 
  mlep = (mx_endpoint_t) ((*env)->GetLongField(env, javaRequest, 
			  m_local_endpoint ));
  
    printf("3 \n"); fflush(stdout); 
  match_recv = (uint64_t) ((*env)->GetLongField(env,
			  javaRequest, matchrecvhandleID)) ;
  match_mask = (uint64_t) 
	  ((*env)->GetLongField(env, javaRequest, matchrecvmaskhandleID)) ;
  buffer_address = (char *) ((*env)->GetLongField(env, 
			  javaRequest, bufferaddresshandleID)) ;
  buffer = ((*env)->GetObjectField(env, javaRequest, 
                          bufferhandleID )) ;
  jclass mpjbuf_class = (*env)->GetObjectClass(env, buffer);
  jbyteArray darr;
  jbyte* dBuffer;
  jboolean isCopy = JNI_TRUE;
  char encoding = 1; 
  int dbuf_length ; 
  //.. dbuf_length ..
  encoding = buffer_address[0] ;
  dbuf_length = 
	  (((int)(unsigned char) buffer_address[4]) << 24) |
	  (((int)(unsigned char) buffer_address[5]) << 16) |
	  (((int)(unsigned char) buffer_address[6]) << 8) |
	  (((int)(unsigned char) buffer_address[7]) ); 
  
  printf("dbuf_length [after strange] <%d> \n",dbuf_length); fflush(stdout); 
  
  //int dbuf_length = byte22int(buffer_address,0);
  int sbuf_length = recv_status.msg_length - 8 ; //- offset 

  if(dbuf_length > 0) { 
    darr = (*env)->NewByteArray (env, dbuf_length);
    dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);
    buffer_desc[0].segment_ptr = dBuffer; 
    buffer_desc[0].segment_length = dbuf_length;
    //printf(" calling mx_irecv in nativeIwait \n"); fflush(stdout); 
    
    rc = mx_irecv(mlep, buffer_desc, 1, match_recv, match_mask,
  		  NULL, &dreq);
    if(rc != MX_SUCCESS) { 
      printf(" return code is not successful \n"); fflush(stdout);  	    
    }
    
    rc = mx_wait(mlep, & dreq, MX_INFINITE, 
		  &recv_status, &result); 
    if(rc != MX_SUCCESS) { 
      printf(" return code is not successful \n"); fflush(stdout);  	    
    }
    //printf(" received \n"); fflush(stdout); 
    (*env)->SetByteArrayRegion(env,darr,0,dbuf_length,dBuffer);       
    jmethodID setdbuf = (*env)->GetMethodID(env, mpjbuf_class,  
		       "setDynamicBuffer", "([B)V"); 
    (*env)->CallVoidMethod(env, buffer, setdbuf, darr);

  } else {
    //printf(" no dynamic message \n") ; 
  }
 
  jmethodID set_size = (*env)->GetMethodID(env, mpjbuf_class,  
		       "setSize", "(I)V"); 
  (*env)->CallVoidMethod(env, buffer, set_size, sbuf_length ); 
  //printf("recvRequest wait returns \n"); 
  (*env)->SetIntField(env,javaRequest, testcalledID, 1); 
  //printf("recv:req calling wait for control message \n"); fflush(stdout);
  //printf("status.msg_length <%d> \n", recv_status.msg_length ); 
  //(*env)->SetIntField(env,status, status_src_ID,
  //                    GET_SRC(recv_status.match_info) );
  //(*env)->SetIntField(env,status, status_tag_ID,
  //                    GET_TAG(recv_status.match_info) );
  //(*env)->SetIntField(env,status, countInBytesID, 
  //		      recv_status.msg_length - 16);//-16

  //.. extract JRequest ..

  //.. figure out if it is send or recv request .. 

  //if(recvrequest) {
   //.. copy paste code from iwait of recvrequest ..
  //}
  
  //if(sendrequest) {
   //.. copy paste code from iwait of sendrequest ..	  
  //}
}
*/

/*
 * Class:     xdev_mxdev_MXDevice
 * Method:    nativeFinish
 * Signature: ()V
 */ 
JNIEXPORT void JNICALL Java_xdev_mxdev_MXDevice_nativeFinish
  (JNIEnv *env, jobject obj1) {

  //printf("native:finish process <%d> starting finish \n",myRank ); 
  //fflush(stdout);
  //sleep(5);
  mx_return_t rc;
  //printf("native:finish process <%d> will close endpoint \n",myRank ); 
  //fflush(stdout); 
  //fflush(stdout);
  rc = mx_close_endpoint(local_endpoint); 
  if(rc != MX_SUCCESS) { 
    //printf("error in nativeFinish "); fflush(stdout); 	  
  }
  //printf("native:finish process <%d> closed endpoint \n",myRank ); 
  //fflush(stdout); 
  //fflush(stdout);
  free(peer_endpoints); 
  peer_endpoints = NULL; 

  if(rc == MX_SUCCESS) { 
    //printf("closed endpoint \n");
  }

  //printf("native:finish process <%d> calling finalize \n",myRank ) ; 
  //fflush(stdout);  
  
  rc = mx_finalize();
  
  if(rc == MX_SUCCESS) {
    //printf("native:finish process <%d> called finalize \n", myRank ); 
    //fflush(stdout); 
  }

}
