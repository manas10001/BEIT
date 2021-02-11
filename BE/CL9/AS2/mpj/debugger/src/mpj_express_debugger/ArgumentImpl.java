/*
The MIT License

Copyright (c) 2005 - 2013
  1. SEECS National University of Sciences and Technology (NUST), Pakistan
  2. Amjad Aziz (2013 - 2013)
  3. Rizwan Hanif (2013 - 2013)
  4. Mohsan Jameel (2013 - 2013)
  5. Aamir Shafi (2005 -2013) 
  6. Bryan Carpenter (2005 - 2013)

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


 * File         : plugin.xml 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */

package mpj_express_debugger;

public class ArgumentImpl implements com.sun.jdi.connect.Connector.Argument {
  private String fName;
  private String fDescription;
  private String fLabel;
  private boolean fMustSpecify;

  protected ArgumentImpl() {
  }

  protected ArgumentImpl(String name, String description, String label,
      boolean mustSpecify) {
    fName = name;
    fLabel = label;
    fDescription = description;
    fMustSpecify = mustSpecify;
  }

  public String name() {
    return fName;
  }

  public String description() {
    return fDescription;
  }

  public String label() {
    return fLabel;
  }

  public boolean mustSpecify() {
    return fMustSpecify;
  }

  public String value() {
    return "";
  };

  public void setValue(String value) {
  };

  public boolean isValid(String value) {
    return false;
  };

  public String toString() {
    return "";
  };
}

class StringArgumentImpl extends ArgumentImpl implements
    com.sun.jdi.connect.Connector.StringArgument {
  private static final long serialVersionUID = 6009335074727417445L;

  private String fValue;

  protected StringArgumentImpl(String name, String description, String label,
      boolean mustSpecify) {
    super(name, description, label, mustSpecify);
  }

  public String value() {
    return fValue;
  }

  public void setValue(String value) {
    fValue = value;
  }

  public boolean isValid(String value) {
    return true;
  }

  public String toString() {
    return fValue;
  }

}

class IntegerArgumentImpl extends ArgumentImpl implements
    com.sun.jdi.connect.Connector.IntegerArgument {
  private static final long serialVersionUID = 6009335074727417445L;
  private Integer fValue;
  private int fMin;
  private int fMax;

  protected IntegerArgumentImpl(String name, String description, String label,
      boolean mustSpecify, int min, int max) {
    super(name, description, label, mustSpecify);
    fMin = min;
    fMax = max;
  }

  public String value() {
    return (fValue == null) ? null : fValue.toString();
  }

  public void setValue(String value) {
    fValue = new Integer(value);
  }

  public boolean isValid(String value) {
    Integer val;
    try {
      val = new Integer(value);
    } catch (NumberFormatException e) {
      return false;
    }
    return isValid(val.intValue());
  }

  public String toString() {
    return value();
  }

  public int intValue() {
    return fValue.intValue();
  }

  public void setValue(int value) {
    fValue = new Integer(value);
  }

  public int min() {
    return fMin;
  }

  public int max() {
    return fMax;
  }

  public boolean isValid(int value) {
    return fMin <= value && value <= fMax;
  }

  public String stringValueOf(int value) {
    return new Integer(value).toString();
  }
}

class BooleanArgumentImpl extends ArgumentImpl implements
    com.sun.jdi.connect.Connector.BooleanArgument {
  private static final long serialVersionUID = 6009335074727417445L;
  private Boolean fValue;

  protected BooleanArgumentImpl(String name, String description, String label,
      boolean mustSpecify) {
    super(name, description, label, mustSpecify);
  }

  public String value() {
    return (fValue == null) ? null : fValue.toString();
  }

  public void setValue(String value) {
    fValue = Boolean.valueOf(value);
  }

  public boolean isValid(String value) {
    return true;
  }

  public String toString() {
    return value();
  }

  public boolean booleanValue() {
    return fValue.booleanValue();
  }

  public void setValue(boolean value) {
    fValue = Boolean.valueOf(value);
  }

  public String stringValueOf(boolean value) {
    return Boolean.valueOf(value).toString();
  }
}
