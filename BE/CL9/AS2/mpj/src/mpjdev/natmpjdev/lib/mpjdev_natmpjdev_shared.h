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
 * File         : mpjdev_natmpjdev_shared.c
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

/* mpjdev_natmpjdev_shared */

#ifndef _Included_mpjdev_natmpjdev_shared
#define _Included_mpjdev_natmpjdev_shared
#ifdef __cplusplus
extern "C" {
#endif


extern jfieldID mpjdev_Status_sourceID;
extern jfieldID mpjdev_Status_tagID;
//    extern jfieldID mpjdev_Status_stathandleID;
extern jfieldID mpjdev_Status_indexID;
extern jfieldID mpjdev_Status_numEls;
extern jfieldID mpjdev_Status_countInBytes;
extern jfieldID mpjdev_natmpjdev_Comm_CommhandleID;

extern jfieldID FID_mpjbuf_Buffer_staticBuffer;
extern jfieldID FID_mpjbuf_Buffer_dynamicBuffer;
extern jfieldID FID_mpjbuf_NIOBuffer_buffer;

extern jclass CL_mpjbuf_Buffer;

//for Request
//extern jfieldID mpjdev_natmpjdev_NativeRequest_reqhandleID;

// As per MPJ Express 0.38 the values of these macros
#define IDENT		0
#define SIMILAR		1
#define UNEQUAL		2
#define CONGRUENT	3

// As per MPJ Express 0.38 mpi.Datatype -- the values of these macros
#define MPJ_UNDEFINED	-1
#define MPJ_NULL		0
#define MPJ_BYTE		1
#define MPJ_CHAR		2
#define MPJ_SHORT		3
#define MPJ_BOOLEAN		4
#define MPJ_INT			5
#define MPJ_LONG		6
#define MPJ_FLOAT		7
#define MPJ_DOUBLE		8
#define MPJ_PACKED		9

// after this non-primitive types begin
#define MPJ_PRIMITIVE_TYPE_RANGE_UB	9

#define MPJ_LB			10
#define MPJ_UB			11
#define MPJ_OBJECT		12

#define MPJ_SHORT2		3
#define MPJ_INT2		5
#define MPJ_LONG2		6
#define MPJ_FLOAT2		7
#define MPJ_DOUBLE2		8

// As per MPJ Express 0.38 Operator -- the values of these macros
// these are needed in Reduce ...
#define MPJ_MAX_CODE	1
#define MPJ_MIN_CODE	2
#define MPJ_SUM_CODE	3
#define MPJ_PROD_CODE	4
#define MPJ_LAND_CODE	5
#define MPJ_BAND_CODE	6
#define MPJ_LOR_CODE	7
#define MPJ_BOR_CODE	8
#define MPJ_LXOR_CODE	9
#define MPJ_BXOR_CODE	10
#define MPJ_MAXLOC_CODE	11
#define MPJ_MINLOC_CODE	12

#ifdef __cplusplus
}
#endif
#endif
