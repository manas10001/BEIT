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
 * File         : Constants.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev;

public class Constants {

  /*
   * This class contains constants and flags
   */

  public static boolean isNative; // set if the selected device is native.

  public final static int NATIVE_SEND_OVERHEAD = 8;
  public final static int NATIVE_RECV_OVERHEAD = 8;

  /*
   * Operator constants (Macros) we may later put them in a constants class at
   * the mpi layer
   */
  public final static int MAX_CODE = 1;
  public final static int MIN_CODE = 2;
  public final static int SUM_CODE = 3;
  public final static int PROD_CODE = 4;
  public final static int LAND_CODE = 5;
  public final static int BAND_CODE = 6;
  public final static int LOR_CODE = 7;
  public final static int BOR_CODE = 8;
  public final static int LXOR_CODE = 9;
  public final static int BXOR_CODE = 10;
  public final static int MAXLOC_CODE = 11;
  public final static int MINLOC_CODE = 12;

}
