/** The MIT License 
	
Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)
*/	
#include "myriexpress.h"
#include "jni.h" 

#define ANY_TAG -2
#define PRI_MATCH(CONTEXT, SRC, TAG) \
	 ((((uint64_t) CONTEXT) << 32)|(((uint64_t) SRC)<< 48)|0x80000000|\
	          ((uint64_t)TAG))
#define SEC_MATCH(CONTEXT, SRC, TAG) \
	((((uint64_t) CONTEXT) << 32)|(((uint64_t) SRC ) << 48) | ((uint64_t) TAG )) 
#define MASK_TAG (UINT64_C(0x000000007fffffff))
#define MASK_SRC (UINT64_C(0xffff000000000000))
#define EMPTY (UINT64_C(0x0000000000000000))
#define MATCH_CONTEXT (UINT64_C(0x0000ffff80000000))
#define GET_TAG(M) ((M) & 0x7fffffff)
#define GET_SRC(M) (((M) >> 48 ) & 0xffff)

//typedef struct jrequeststruct { 
//  jobject jrequest ; 
//} JRequestStruct; 

