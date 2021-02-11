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

import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public interface IMPJLaunchConfigurationConstants extends
    IJavaLaunchConfigurationConstants {
  public static final String ID_JAVA_MPJ = LaunchingPlugin
      .getUniqueIdentifier() + ".mpjJavaApplication";

  public static final String ATTR_MPJ_NP = LaunchingPlugin
      .getUniqueIdentifier() + ".MPJ_NP";

  public static final String ATTR_MPJ_DEV = LaunchingPlugin
      .getUniqueIdentifier() + ".MPJ_DEV";

  public static final String ATTR_MPJ_CONF = LaunchingPlugin
      .getUniqueIdentifier() + ".MPJ_CONF";

  public static final String ATTR_MPJ_HOME = LaunchingPlugin
      .getUniqueIdentifier() + ".MPJ_HOME";

  public static final String ATTR_MPJ_PARAMETERS = LaunchingPlugin
      .getUniqueIdentifier() + ".MPJ_PARAMETERS";

}
