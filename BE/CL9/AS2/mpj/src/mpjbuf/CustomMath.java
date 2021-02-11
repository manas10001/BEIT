/*
The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
/*
 * File         : CustomMath.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 BST 2005
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 *    
 */
package mpjbuf;

/**
 * A class that helps calculate natural log
 * @author Dirk Bosmans Dirk.Bosmans@tijd.com 
 */
public class CustomMath { 

/**
* natural log method.
* Calculate how many bits wide a number is,
* i.e. position of highest 1 bit.
* @return p where 2**p is first power of two >= n.
* e.g. binary 0001_0101 -> 5, 0xffffffff -> 32,
* 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 2, 4 -> 3
*/
static public int widthInBits1(int n )
   {
   if ( n < 0 )
      {
      return 32;
      }
   if ( n > 0x3fffffff )
      {
      return 31;
      }
   return(int)Math.ceil( Math.log( n+1 ) * invln2 );
   } // end widthInBits1

private static double invln2 = 1.0 / Math.log(2.0);

/**
* Calculate how many bits wide a number is,
* i.e. position of highest 1 bit.
* Fully unraveled binary search method.
* @return p where 2**p is first power of two >= n.
* e.g. binary 0001_0101 -> 5, 0xffffffff -> 32,
* 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 2, 4 -> 3 */
static public final int widthInBits( int n )
   {
   if ( n < 0 ) return 32;
   if ( n > 0x0000ffff )
      {
      if ( n > 0x00ffffff )
         {
         if ( n > 0x0fffffff )
            {
            if ( n > 0x3fffffff )
               {
               // if ( n > 0x7fffffff )
               // return 32
               // else
               return 31;
               }
            else
               {
               // !( n > 0x3fffffff )
               if ( n > 0x1fffffff ) return 30;
               else return 29;
               }
            }
         else
            {
            // !( n > 0x0fffffff )
            if ( n > 0x03ffffff )
               {
               if ( n > 0x07ffffff ) return 28;
               else return 27;
               }
            else
               {
               // !( n > 0x03ffffff )
               if ( n > 0x01ffffff ) return 26;
               else return 25;
               }
            }
         }
      else
         {
         // !( n > 0x00ffffff )
         if ( n > 0x000fffff )
            {
            if ( n > 0x003fffff )
               {
               if ( n > 0x007fffff ) return 24;
               else return 23;
               }
            else
               {
               // !( n > 0x003fffff )
               if ( n > 0x001fffff ) return 22;
               else return 21;
               }
            }
         else
            {
            // !( n > 0x000fffff )
            if ( n > 0x0003ffff )
               {
               if ( n > 0x0007ffff ) return 20;
               else return 19;
               }
            else
               {
               // !( n > 0x0003ffff )
               if ( n > 0x0001ffff ) return 18;
               else return 17;
               }
            }
         }
      }
   else
      {
      // !( n > 0x0000ffff )
      if ( n > 0x000000ff )
         {
         if ( n > 0x00000fff )
            {
            if ( n > 0x00003fff )
               {
               if ( n > 0x00007fff ) return 16;
               else return 15;
               }
            else
               {
               // !( n > 0x00003fff )
               if ( n > 0x00001fff ) return 14;
               else return 13;
               }
            }
         else
            {
            // !( n > 0x00000fff )
            if ( n > 0x000003ff )
               {
               if ( n > 0x000007ff ) return 12;
               else return 11;
               }
            else
               {
               // !( n > 0x000003ff )
               if ( n > 0x000001ff ) return 10;
               else return 9;
               }
            }
         }
      else
         {
         // !( n > 0x000000ff )
         if ( n > 0x0000000f )
            {
            if ( n > 0x0000003f )
               {
               if ( n > 0x0000007f ) return 8;
               else return 7;
               }
            else
               {
               // !( n > 0x0000003f )
               if ( n > 0x0000001f ) return 6;
               else return 5;
               }
            }
         else
            {
            // !( n > 0x0000000f )
            if ( n > 0x00000003 )
               {
               if ( n > 0x00000007 ) return 4;
               else return 3;
               }
            else
               {
               // !( n > 0x00000003 )
               if ( n > 0x00000001 ) return 2;
               return n;
               /*
               else if ( n > 0x00000000 )
               return 1;
               else
               return 0;
               */
               }
            }
         }
      }
   } // end widthInBits
}
