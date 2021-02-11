/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

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
 * File         : Type.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 BST 2004
 * Revision     : $Revision: 1.5 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 *    
 */

package mpjbuf;

/**
 * Type of argument for
 * {\@link #putSectionHeader(Buffer.Type) putSectionHeader()} and
 * {\@link #getSectionHeader(Buffer.Type) getSectionHeader()}
 * Type-safe enumeration pattern.  Instances are
 * {\@link #BYTE BYTE},
 * {\@link #CHAR CHAR},
 * {\@link #SHORT SHORT},
 * {\@link #BOOLEAN BOOLEAN},
 * {\@link #INT INT},
 * {\@link #LONG LONG},
 * {\@link #FLOAT FLOAT},
 * {\@link #DOUBLE DOUBLE},
 * {\@link #OBJECT OBJECT},
 * {\@link #BYTE_DYNAMIC BYTE_DYNAMIC},
 * {\@link #CHAR_DYNAMIC CHAR_DYNAMIC},
 * {\@link #SHORT_DYNAMIC SHORT_DYNAMIC},
 * {\@link #BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC},
 * {\@link #INT_DYNAMIC INT_DYNAMIC},
 * {\@link #LONG_DYNAMIC LONG_DYNAMIC},
 * {\@link #FLOAT_DYNAMIC FLOAT_DYNAMIC}, and
 * {\@link #DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
 */
public class Type {

    public final static Type BYTE = new Type(0);
    public final static Type CHAR = new Type(1);
    public final static Type SHORT = new Type(2);
    public final static Type BOOLEAN = new Type(3);
    public final static Type INT = new Type(4);
    public final static Type LONG = new Type(5);
    public final static Type FLOAT = new Type(6);
    public final static Type DOUBLE = new Type(7);
  
    public final static Type OBJECT = new Type(8);
  
    // Data for sections specified with following types goes in
    // dynamic buffer, along with objects.
  
    public final static Type BYTE_DYNAMIC = new Type(9);
    public final static Type CHAR_DYNAMIC = new Type(10);
    public final static Type SHORT_DYNAMIC = new Type(11);
    public final static Type BOOLEAN_DYNAMIC = new Type(12);
    public final static Type INT_DYNAMIC = new Type(13);
    public final static Type LONG_DYNAMIC = new Type(14);
    public final static Type FLOAT_DYNAMIC = new Type(15);
    public final static Type DOUBLE_DYNAMIC = new Type(16);
  
    public final static Type UNDEFINED = new Type(17);
  
    private int code ;

    private Type(int code) {
        this.code = code ;
    }

    /**
     * Convenience method for serializing `Type' object.
     */
    public int getCode() {
        return code ;
    }

    /**
     * Convenience method for deserializing `Type' object.
     */
    public static Type getType(int code) {
        return table [code] ;
    }

    private static Type [] table = {

            BYTE,
            CHAR,
            SHORT,
            BOOLEAN,
            INT,
            LONG,
            FLOAT,
            DOUBLE,
  
            OBJECT,
  
            BYTE_DYNAMIC,
            CHAR_DYNAMIC,
            SHORT_DYNAMIC,
            BOOLEAN_DYNAMIC,
            INT_DYNAMIC,
            LONG_DYNAMIC,
            FLOAT_DYNAMIC,
            DOUBLE_DYNAMIC,
  
            UNDEFINED
    } ;
}

