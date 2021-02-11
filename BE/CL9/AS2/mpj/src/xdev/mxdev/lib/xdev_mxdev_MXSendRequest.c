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
#include "xdev_mxdev_MXSendRequest.h"

jfieldID reqhandleID;
jfieldID dbuflenID ;
jfieldID dbufreqhandleID ;
jfieldID m_local_endpoint;
jfieldID bufferhandleID;
jfieldID testcalledID ;
jfieldID isPeekedID ; 
//jfieldID r_endpoint_handleID ; 
//jfieldID d_msg_tag_handleID ;

/*
 * Class:     xdev_mxdev_MXSendRequest
 * Method:    nativeIwait
 * Signature: (Lmpjdev/Status;)V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXSendRequest_nativeIwait
  (JNIEnv *env, jobject jthis, jobject status) {
  //printf("sendRequest wait called \n"); fflush(stdout); 
  mx_return_t rc ; 	  
  int testCalled = ((*env)->GetIntField(env,jthis,testcalledID)) ; 
  int isPeeked = ((*env)->GetIntField(env,jthis,isPeekedID)) ;

  if(testCalled == 1) {
    //((*env)->SetIntField(env,jthis,testcalledID,0)) ;               
    return ; 
  }
  
  //printf("native:recvrequest \n");	  
  uint32_t result;
  mx_status_t send_status;
  mx_request_t reqhandle ;
  mx_status_t send_status1;
  uint32_t result1;
  //mx_request_t reqhandle ;
  mx_request_t dbufreqhandle ;
  mx_endpoint_t mlep ;
  jobject buffer ; 
  int dbuf_length ; 

  jclass req_class = (*env)->GetObjectClass(env, jthis);

  reqhandle = (mx_request_t) ((*env)->GetLongField(env, jthis, reqhandleID )) ;
  dbuf_length = ((*env)->GetIntField(env, jthis, dbuflenID )) ;
  dbufreqhandle = (mx_request_t) 
	  ((*env)->GetLongField(env, jthis, dbufreqhandleID )) ;
  mlep = (mx_endpoint_t) ((*env)->GetLongField(env, jthis, m_local_endpoint )) ;
  buffer = ((*env)->GetObjectField(env, jthis, 
			  bufferhandleID )) ;

  if( dbuf_length > 0) { 
    //printf("sendreq calling mx_wait 1 \n"); fflush(stdout);
    /* this is for dbuf message */
    mx_wait(mlep, &dbufreqhandle, MX_INFINITE,
		    &send_status1, &result1); 
  }
  
  if(!isPeeked) { 
    rc = mx_wait(mlep, &reqhandle, MX_INFINITE,
                    &send_status, &result); 
    if(rc != MX_SUCCESS) { 
      printf("error in isend"); fflush(stdout); 	  
    }
  }
  //printf("sendreq calling mx_wait 2 \n"); fflush(stdout);
  /* this is for static buffer message */
  //printf("sendreq called two mx_waits \n"); fflush(stdout);
  /*
  jclass mpjbuf_class = (*env)->GetObjectClass(env, buffer);
  jmethodID set_size = (*env)->GetMethodID(env, mpjbuf_class,
                       "setSize", "(I)V");
  (*env)->CallVoidMethod(env, buffer, set_size, send_status.msg_length);
  */
  //printf("native:receiver printing the status information \n");
  //printf("native:receiver xfer_length <%d> \n", send_status.xfer_length );
  //printf("native:receiver buffer capacity <%d>", capacity );
  //printf("native:receiver msg_length <%d> \n", send_status.msg_length );
  //printf("native:receiver recv method ends \n");
  (*env)->SetIntField(env,jthis, testcalledID, 1);
}

/*
 * Class:     xdev_mxdev_MXSendRequest
 * Method:    nativeItest
 * Signature: (Lmpjdev/Status;)I
 */
JNIEXPORT jint JNICALL Java_xdev_mxdev_MXSendRequest_nativeItest
  (JNIEnv *env, jobject jthis, jobject status) {
  //printf("native itest method \n");	  
  int testCalled = ((*env)->GetIntField(env,jthis,testcalledID)) ; 
  int isPeeked = ((*env)->GetIntField(env,jthis,isPeekedID)) ;
    
  //printf("sendRequest test called \n"); fflush(stdout); 
  if(testCalled == 1) {
    printf(" no need to call test after test (sendRequest) \n");
    fflush(stdout); 
    //((*env)->SetIntField(env,jthis,testcalledID,0)) ;               
    return 1 ; 
  }
  uint32_t result = 0x00000000 ;
  mx_status_t send_status;
  mx_request_t reqhandle ;
  mx_status_t send_status1;
  uint32_t result1 = 0x00000000 ; 
  //mx_request_t reqhandle ;
  mx_request_t dbufreqhandle ;
  mx_endpoint_t mlep ;
  jobject buffer ; 
  int dbuf_length ; 

  jclass req_class = (*env)->GetObjectClass(env, jthis);

  reqhandle = (mx_request_t) ((*env)->GetLongField(env, jthis, reqhandleID )) ;
  dbuf_length = ((*env)->GetIntField(env, jthis, dbuflenID )) ;
  dbufreqhandle = (mx_request_t) 
	  ((*env)->GetLongField(env, jthis, dbufreqhandleID )) ;
  mlep = (mx_endpoint_t) ((*env)->GetLongField(env, jthis, m_local_endpoint )) ;
  buffer = ((*env)->GetObjectField(env, jthis, 
			  bufferhandleID )) ;
  
  if( dbuf_length > 0) { 
    //printf("sendreq calling mx_wait 1 \n"); fflush(stdout);
    /* this is for dbuf message */
    mx_test(mlep, &dbufreqhandle, 
		    &send_status1, &result1);  
    if(!result1) {
      return 0;	    
    }
  }
  
  //printf("sendreq calling mx_wait 2 \n"); fflush(stdout);
  /* this is for static buffer message */
  if(!isPeeked) { 
    mx_test(mlep, &reqhandle, 
                    &send_status, &result);
  }
  
  /* if there is only static section ...then this means stuff is completed */
  if(dbuf_length <= 0 && result) { 
    (*env)->SetIntField(env,jthis, testcalledID, 1);
    return 1; 
  }

  /*  in case we have primary and secondary section */
  if(result && result1) { 
    //printf("test completed the comms (sendRequest) \n"); fflush(stdout);
    //we are not checking if its been completed or not ...
    (*env)->SetIntField(env,jthis, testcalledID, 1);
    return 1; 
  }
  
  if(result && !result1) {
    printf("1. one of the request is tested -- not possible \n ");  	  
    fflush(stdout);
  }
  
  if(!result && result1) {
    printf("2. one of the request is tested -- not possible \n ");  	  
    fflush(stdout);
  }
  
  if(!result && !result1) {
    printf(" (sendRequest) both requests are not completed (unusual) \n"); 
  }

  return 0; 
  
}
  

/*
 * Class:     xdev_mxdev_MXSendRequest
 * Method:    nativeRequestInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_xdev_mxdev_MXSendRequest_nativeRequestInit
  (JNIEnv *env, jclass sendRequest) { 
  reqhandleID    = (*env)->GetFieldID(env, sendRequest , "handle", "J");
  dbuflenID    = (*env)->GetFieldID(env, sendRequest , "dbuflen", "I");
  dbufreqhandleID    = 
	  (*env)->GetFieldID(env, sendRequest , "dbufHandle", "J");
  m_local_endpoint = (*env)->GetFieldID(env, sendRequest,
		  "localEndpointHandle", "J");
  bufferhandleID = (*env)->GetFieldID(env, sendRequest,
		  "bufferHandle", "Lmpjbuf/Buffer;");
  testcalledID    = (*env)->GetFieldID(env, sendRequest, "testCalled", "I");
  isPeekedID = (*env)->GetFieldID(env,
                                  sendRequest, "isPeeked", "I");
  //r_endpoint_handleID = (*env)->GetFieldID(env, sendRequest,
//		  "remoteEndpointHandle", "J");
  //d_msg_tag_handleID = (*env)->GetFieldID(env, sendRequest, 
//		  "dynamicMsgTagHandle", "J");
}
  
