/** The MIT License 
	
Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)
*/	
#include <stdio.h>
#include "jni.h"
#include "myriexpress.h"
#include "mxdev_const.h"
#include "xdev_mxdev_MXRecvRequest.h"

jfieldID reqhandleID;
jfieldID m_local_endpoint;
jfieldID bufferhandleID;
jfieldID matchrecvhandleID; 
jfieldID matchrecvmaskhandleID;
jfieldID bufferaddresshandleID ;
jfieldID status_src_ID ;
jfieldID status_tag_ID ;
jfieldID testcalledID ; 
jfieldID countInBytesID ; 
jfieldID isPeekedID ; 

/*
 * Class:     xdev_mxdev_MXRecvRequest
 * Method:    nativeIwait
 * Signature: (Lmpjdev/Status;)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXRecvRequest_nativeIwait
  (JNIEnv *env, jobject jthis, jobject status) {
  //printf(" native Iwait (Request) \n"); fflush(stdout);	  
  int testCalled = ((*env)->GetIntField(env,jthis,testcalledID)) ; 
  int isPeeked = ((*env)->GetIntField(env,jthis,isPeekedID)) ; 
  
  if(testCalled == 1) { 
    //((*env)->SetIntField(env,jthis,testcalledID,0)) ; 
    return 1; 
  }

  uint32_t result;
  mx_status_t recv_status;
  mx_request_t reqhandle ;
  mx_return_t rc;
  mx_endpoint_t mlep ;
  jobject buffer ;
  uint64_t match_recv;
  uint64_t match_mask;
  char * buffer_address;
  mx_segment_t buffer_desc[1];
  jclass req_class = (*env)->GetObjectClass(env, jthis);
  reqhandle = (mx_request_t) ((*env)->GetLongField(env, jthis, reqhandleID )) ;
  mx_request_t dreq ; 
  mlep = (mx_endpoint_t) ((*env)->GetLongField(env, jthis, m_local_endpoint ));
  match_recv = (uint64_t) ((*env)->GetLongField(env, 
			  jthis, matchrecvhandleID)) ;
  match_mask = (uint64_t) 
	  ((*env)->GetLongField(env, jthis, matchrecvmaskhandleID)) ;
  buffer_address = (char *) ((*env)->GetLongField(env, 
			  jthis, bufferaddresshandleID)) ;
  buffer = ((*env)->GetObjectField(env, jthis, 
                          bufferhandleID )) ;
  jclass mpjbuf_class = (*env)->GetObjectClass(env, buffer);
  jbyteArray darr;
  jbyte* dBuffer;
  jboolean isCopy = JNI_TRUE;
  //printf("recv:req calling wait for control message \n"); fflush(stdout);
  if(!isPeeked) { 
    /* wait for it complete */
    rc = mx_wait(mlep, & reqhandle, MX_INFINITE, 
    		   &recv_status, &result);  
    if(rc != MX_SUCCESS) { 
      printf("error in recvRequest"); fflush(stdout); 	  
    }
    //printf("status.msg_length <%d> \n", recv_status.msg_length ); 
    (*env)->SetIntField(env,status, status_src_ID,
                        GET_SRC(recv_status.match_info) );
    (*env)->SetIntField(env,status, status_tag_ID,
                        GET_TAG(recv_status.match_info) );
    (*env)->SetIntField(env,status, countInBytesID, 
    		        recv_status.msg_length - 16 );//-16
  } 
  
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
  int sbuf_length = 0; 

  if(!isPeeked) {
	  //this should really be - 16 ..8 is sendoverhead and device also 
	  //uses the first 8 bytes ..so in all should be 16 ...
    sbuf_length = recv_status.msg_length - 8 ; //- offset 
  }
  else {
    sbuf_length = (*env)->GetIntField(env, status, countInBytesID);  
  }

  if(dbuf_length > 0) { 
    darr = (*env)->NewByteArray (env, dbuf_length);
    dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);
    buffer_desc[0].segment_ptr = dBuffer; 
    buffer_desc[0].segment_length = dbuf_length;
    //printf(" calling mx_irecv in nativeIwait \n"); fflush(stdout); 
    /* recv the message */
    rc = mx_irecv(mlep, buffer_desc, 1, match_recv, match_mask,
  		  NULL, &dreq);
    if(rc != MX_SUCCESS) { 
      printf(" return code is not successful \n"); fflush(stdout);  	    
    }
    /* wait for it complete */
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
  (*env)->SetIntField(env,jthis, testcalledID, 1); 
  
}

/*
 * Class:     xdev_mxdev_MXRecvRequest
 * Method:    nativeItest
 * Signature: (Lmpjdev/Status;)I
 */
JNIEXPORT jint JNICALL Java_xdev_mxdev_MXRecvRequest_nativeItest
  (JNIEnv *env, jobject jthis, jobject status) {
  //printf("recvRequest test called \n");  fflush(stdout); 
  int testCalled = ((*env)->GetIntField(env,jthis,testcalledID)) ; 
  int isPeeked = ((*env)->GetIntField(env,jthis,isPeekedID)) ; 
  
  if(testCalled == 1) { 
    //((*env)->SetIntField(env,jthis,testcalledID,0)) ; 
    return 1; 
  }

  //printf("nativeItest method \n!");
  mx_status_t recv_status;
  mx_request_t reqhandle,dreq ;
  mx_return_t rc;
  mx_endpoint_t mlep ;
  jobject buffer ;
  uint64_t match_recv;
  uint64_t match_mask;
  char * buffer_address;
  mx_segment_t buffer_desc[1];
  jbyteArray darr;
  jbyte* dBuffer;
  jboolean isCopy = JNI_TRUE;
  char encoding = 1; 
  int dbuf_length ; 
  uint32_t result = UINT64_C(0x00000000) ;
  
  jclass req_class = (*env)->GetObjectClass(env, jthis);
  reqhandle = (mx_request_t) ((*env)->GetLongField(env, jthis, reqhandleID )) ;
  mlep = (mx_endpoint_t) ((*env)->GetLongField(env, jthis, m_local_endpoint ));
  match_recv = (uint64_t) ((*env)->GetLongField(env, 
			  jthis, matchrecvhandleID)) ;
  match_mask = (uint64_t) 
	  ((*env)->GetLongField(env, jthis, matchrecvmaskhandleID)) ;
  buffer_address = (char *) ((*env)->GetLongField(env, 
			  jthis, bufferaddresshandleID)) ;
  buffer = ((*env)->GetObjectField(env, jthis, 
                          bufferhandleID )) ;
  jclass mpjbuf_class = (*env)->GetObjectClass(env, buffer);
  
  /* wait for it complete */
  if(!isPeeked) { 
    rc = mx_test(mlep, &reqhandle, &recv_status, &result); 
  }

  if(result || isPeeked) {  	
    if(!isPeeked) { 	  
      (*env)->SetIntField(env,status, status_src_ID,
                          GET_SRC(recv_status.match_info) );
      (*env)->SetIntField(env,status, status_tag_ID,
                          GET_TAG(recv_status.match_info) );
      (*env)->SetIntField(env,status, countInBytesID, 
   		          recv_status.msg_length - 16);//-16
    }
    //.. dbuf_length ..
    encoding = buffer_address[0] ;
    dbuf_length = 
	    (((int)(unsigned char) buffer_address[4]) << 24) |
	    (((int)(unsigned char) buffer_address[5]) << 16) |
	    (((int)(unsigned char) buffer_address[6]) << 8) |
	    (((int)(unsigned char) buffer_address[7]) ); 
    //printf("dbuf_length [after strange] <%d> \n",dbuf_length);
    //int dbuf_length = byte22int(buffer_address,0);
    int sbuf_length = 0; 
    if(!isPeeked) { 
      sbuf_length = recv_status.msg_length - 8 ; //- offset 
    }
    else { 
      sbuf_length = (*env)->GetIntField(env, status, countInBytesID);
    }

    //printf("status.msg_length <%d> \n", recv_status.msg_length ); 
    if(dbuf_length > 0) { 
      darr = (*env)->NewByteArray (env, dbuf_length);
      dBuffer = (*env)->GetByteArrayElements(env, darr, &isCopy);
      buffer_desc[0].segment_ptr = dBuffer; 
      buffer_desc[0].segment_length = dbuf_length;
      //printf(" receiving \n"); fflush(stdout); 
      //printf("receivng dbuf message \n"); fflush(stdout);      
      /* recv the message */
      rc = mx_irecv(mlep, buffer_desc, 1, match_recv, match_mask,
    		  NULL, &dreq);
  
      /* wait for it complete */
      rc = mx_wait(mlep, & dreq, MX_INFINITE, 
  		  &recv_status, &result); 
      //printf("received dbuf message \n"); fflush(stdout);      
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
    (*env)->SetIntField(env,jthis, testcalledID, 1); 
                       
    return 1; 
  }
  
  return 0;
}
  

/*
 * Class:     xdev_mxdev_MXRecvRequest
 * Method:    nativeRequestInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXRecvRequest_nativeRequestInit
  (JNIEnv *env, jclass recvRequest) { 
  reqhandleID    = (*env)->GetFieldID(env, recvRequest, "handle", "J");
  testcalledID    = (*env)->GetFieldID(env, recvRequest, "testCalled", "I");
  //ctrlmsghandleID = (*env)->GetFieldID(env, recvRequest, "ctrlMsgHandle", "J");
  matchrecvhandleID    = (*env)->GetFieldID(env, recvRequest, 
		  "matchRecvHandle", "J");
  matchrecvmaskhandleID    = 
	  (*env)->GetFieldID(env, recvRequest, 
	          "matchRecvMaskHandle", "J");
  //sbuflengthhandleID = (*env)->GetFieldID(env, recvRequest, 
//		  "sBufLengthHandle", "J");
  //dbuflengthhandleID = (*env)->GetFieldID(env, recvRequest, 
//		  "dBufLengthHandle", "J");
  bufferaddresshandleID = (*env)->GetFieldID(env, recvRequest, 
		  "bufferAddressHandle", "J");
  m_local_endpoint = (*env)->GetFieldID(env, recvRequest, 
		  "localEndpointHandle", "J");
  bufferhandleID = (*env)->GetFieldID(env, recvRequest, 
		  "bufferHandle", "Lmpjbuf/Buffer;"); 
  jclass CL_mpjdev_Status = (*env)->FindClass(env,"mpjdev/Status");
  status_src_ID = (*env)->GetFieldID(env, CL_mpjdev_Status,
                   "source", "I");
  status_tag_ID = (*env)->GetFieldID(env, CL_mpjdev_Status,
                   "tag", "I");
  countInBytesID = (*env)->GetFieldID(env, CL_mpjdev_Status,
                   "countInBytes", "I");
  isPeekedID = (*env)->GetFieldID(env,
                     recvRequest, "isPeeked", "I");
}
  
