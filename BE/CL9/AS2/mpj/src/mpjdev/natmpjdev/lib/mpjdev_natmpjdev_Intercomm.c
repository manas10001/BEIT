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
 * File         : mpjdev_natmpjdev_Intercomm.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

#include "mpjdev_natmpjdev_Intercomm.h"
#include "mpi.h"
#include <inttypes.h>
/* Implementation for class mpjdev_natmpjdev_Intercomm */

/*
 * Class:     mpjdev_natmpjdev_Intercomm
 * Method:    nativeMerge
 * Signature: (JZ)J
 */
JNIEXPORT jlong JNICALL Java_mpjdev_natmpjdev_Intercomm_nativeMerge(JNIEnv *env,
    jobject thisObject, jlong intercomm, jboolean _high) {

  MPI_Comm mpi_Intercomm = (MPI_Comm) (intptr_t) intercomm;
  MPI_Comm newintracomm;
  int errCode = -1;
  errCode = MPI_Intercomm_merge(mpi_Intercomm, _high, &newintracomm);

  if (errCode == MPI_SUCCESS) {
    //printf("Nativ: Intercomm  Merge MPI_SUCCESS \n");	
  } else {
    printf("Nativ: Intercomm  Merge returned is not MPI_SUCCESS\n");
  }

  if (newintracomm == MPI_COMM_NULL) {
    printf("Nativ: Intercomm  Merge- newintracomm returned is NULL\n");
    return 0;
  } else
    return (jlong) newintracomm;

}

