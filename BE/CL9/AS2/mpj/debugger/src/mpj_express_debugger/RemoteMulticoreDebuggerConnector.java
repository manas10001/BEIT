package mpj_express_debugger;

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


 * File         : RemoteMulticoreDebuggerConnector.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdi.internal.connect.*;
import com.sun.jdi.connect.Connector;

//import com.sun.jdi.connect.Connector; 
public class RemoteMulticoreDebuggerConnector extends ArgumentImpl implements
    IVMConnector {
  public RemoteMulticoreDebuggerConnector() {
  }

  public RemoteMulticoreDebuggerConnector(String name, String description,
      String label, boolean mustSpecify) {
    super(name, description, label, mustSpecify);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void connect(Map arguments, IProgressMonitor monitor, ILaunch launch)
      throws CoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public String getName() {
    return "Multicore";
  }

  @Override
  public String getIdentifier() {
    return "shmdev";
  }

  @Override
  public Map getDefaultArguments() throws CoreException {
    Map argumentsMap = new HashMap();
    Connector.StringArgument harg = new StringArgumentImpl("hostname",
        "Target Debugger", "Host", true);
    harg.setValue("localhost");
    argumentsMap.put("hostname", harg);
    Connector.IntegerArgument parg = new IntegerArgumentImpl("port",
        "Port Number", "Port", true, 0, 65000);
    argumentsMap.put("port", parg);
    parg.setValue("25000");
    return argumentsMap;
  }

  @Override
  public List getArgumentOrder() {
    List list = new ArrayList(2);
    list.add("hostname");
    list.add("port");
    return list;
  }

}
