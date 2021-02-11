/*
 The MIT License

 Copyright (c) 2005 - 2014
   1. Distributed Systems Group, University of Portsmouth (2014)
   2. Aamir Shafi (2005 - 2014)
   3. Bryan Carpenter (2005 - 2014)
   4. Mark Baker (2005 - 2014)

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
 * File         : Wrapper.java 
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Dec 12, 2004
 * Revision     : $Revision: 1.20 $
 * Updated      : Aug 27, 2014
 */

package runtime.daemon;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;

import java.net.DatagramSocket;
import java.net.ServerSocket;

public class Wrapper extends Thread {

  String portInfo = null;
  int processes = 0;
  String className = null;
  Class c = null;
  String deviceName = null;
  String rank = null;
  String[] nargs = null;
  String hostName = null;
  String args[] = null;
  
  String serverName = null;
  int serverPort = 0;
  private String WRAPPER_INFO = null;

  public Wrapper(ThreadGroup group, String name) {
    super(group, name);
  }

  /**
   * Executes MPJ program in a new JVM. This method is invoked in main
   * method of this class, which is started by MPJDaemon. This method 
   * can start multiple threads in a JVM.
   * 
   * @param args
   *          Arguments to this method. args[0] is portInfo 'String',
   *          args[1] is number of processes, args[2] is deviceName, args[3] is
   *          hostname of MPJRun.java server, args[4] is the port number of
   *          MPJRun.java server, args[5] is rank, args[6] is className
   */
  public void execute(String args[]) throws Exception {

    InetAddress localaddr = InetAddress.getLocalHost();
    hostName = localaddr.getHostName();

    deviceName = args[2];
    /* incase of mxdev the location of mpjdev.conf file is passed
     * else the complete conf file string is passed
     * spaces in conf file contents were replaced by new line char
     * by tau_java.
     */
    if(!deviceName.equals("mxdev")){
      args[0]=args[0].replace('|',' ');
    }
    portInfo = args[0];
    processes = (new Integer(args[1])).intValue();
    serverName = args[3];
    serverPort = Integer.parseInt(args[4]);
    rank = args[5];
    className = args[6];

   
    nargs = new String[(args.length - 7)];
    System.arraycopy(args, 7, nargs, 0, nargs.length);

    c = Class.forName(className);

    try {
      System.out.println("Starting process <"+rank+"> on <"+hostName+">");

      String arvs[] = new String[nargs.length + 3];

      arvs[0] = rank;
      if(!deviceName.equals("mxdev")){
        arvs[1] = portInfo.concat(";#Server Name;"+serverName+
                                  ";#Server Port;"+serverPort);
      }
      else{
        arvs[1] = portInfo; 
      }
        arvs[2] = deviceName;

      for (int i = 0; i < nargs.length; i++) {
	arvs[i + 3] = nargs[i];
      }
      Method m = c.getMethod("main", new Class[] { arvs.getClass() });
      m.setAccessible(true);
      int mods = m.getModifiers();
      if (m.getReturnType() != void.class || !Modifier.isStatic(mods)
	  || !Modifier.isPublic(mods)) {
	throw new NoSuchMethodException("main");
      }
      m.invoke(null, new Object[] { arvs });
      
      System.out.println("Stopping Process <"+rank+"> on <"+hostName+">");
    }
    catch (Exception ioe) {
      System.err.println("["+hostName+"-Wrapper.java]: Multi-threaded"+
                         " starter: exception" + ioe.getMessage());
      ioe.printStackTrace();
    }
  }

  public void run() {
    try {
      execute(args);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  

  public static void main(String args[]) throws Exception {
    ThreadGroup group = new ThreadGroup("MPI" + args[3]);
    Wrapper wrap = new Wrapper(group, args[3]);
    wrap.args = args;
    wrap.start();
    wrap.join();
  }
}
