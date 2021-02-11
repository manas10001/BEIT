/*
The MIT License

Copyright (c) 2010
1. Aamir Shafi 

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
 * File         : MulticoreStarter.java 
 * Author       : Aamir Shafi
 * Created      : Sun Dec 12 12:22:15 BST 2004
 * Revision     : $Revision: 1.17 $
 * Updated      : $Date: 2009/08/03 12:48:55 $
 */
/**
 *  This class is used for SMP based system stater
 */
package runtime.starter;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.lang.Integer;

import runtime.common.RTConstants;
import runtime.daemon.JarClassLoader;
import xdev.smpdev.SMPDevProcess;

public class MulticoreStarter {

  String wdir = null;
  String config = null;
  int processes = 0;
  JarClassLoader classLoader = null;
  URLClassLoader urlClassLoader = null;
  String name = null;
  String className = null;
  String deviceName = null;
  String packageName = null;
  String cmdClassPath = null;
  String mpjHomeDir = null;
  String[] nargs = null;
  String loader = null;
  String hostName = null;
  String[] arvs = null;
  Method[] m;
  Method[] method;
  int x;
  Class[] c;
  int num;
  static final Object monitor = new Object();
  SMPDevProcess[] smpProcess;
  Integer rank = new Integer(-1);
  static String appPath = "";

  public MulticoreStarter() {
  }

  /**
   * Executes MPJ program in a new JVM. This method is invoked in main method of
   * this class, which is started by MPJDaemon. This method can only start a new
   * JVM but can't start multiple threads in one JVM.
   * 
   * @param args
   *          Arguments to this method. args[0] is wdir 'String', args[1] number
   *          of processes, args[2] is deviceName, and args[3] is rank started
   *          by this process. args[4] is className ... it will only be used if
   *          URL does not point to a JAR file.
   */
  public void execute(String args[]) throws Exception {

    InetAddress localaddr = InetAddress.getLocalHost();
    hostName = localaddr.getHostName();
    wdir = args[0]; // this contains working directory ...
    processes = (new Integer(args[1])).intValue();
    deviceName = args[2];
    loader = args[3];
    cmdClassPath = args[4];
    className = args[5];
    int ARGS_USED_HERE = 6;
    nargs = new String[(args.length - ARGS_USED_HERE)];
    System.arraycopy(args, ARGS_USED_HERE, nargs, 0, nargs.length);

    arvs = new String[(nargs.length + 3)];

    Runnable[] ab = new Runnable[processes];

    smpProcess = new SMPDevProcess[processes];
    c = new Class[processes];
    m = new Method[processes];
    method = new Method[processes];

    for (x = 0; x < processes; x++) {
      // System.out.println("x " + x);
      ab[x] = new Runnable() {

	String argNew[] = new String[arvs.length];

	public void run() {

	  int index = Integer.parseInt(Thread.currentThread().getName());
	  // String conf = wdir.substring(0, (wdir.lastIndexOf("/") + 1));
	  // config = conf + "mpjdev.conf";
	  // System.out.println("conf " + config);
	  /*
	   * if (loader.equals("useRemoteLoader")) { if
	   * (className.equals("dummy")) { try {
	   * System.out.println("Hello i am remote"); URL[] urls = {new
	   * URL(wdir), new URL(mpjURL)}; classLoader = new
	   * JarClassLoader(urls); name = classLoader.getMainClassName();
	   * c[index] = classLoader.loadClass(name); //packageName =
	   * c.getPackage().getName(); //name = packageName+name; } catch
	   * (Exception e) { e.printStackTrace(); } } else { try {
	   * System.out.println("Hello i am else of remote"); urlClassLoader =
	   * URLClassLoader.newInstance(new URL[]{ new URL(wdir), new
	   * URL(mpjURL)}); name = className; c[index] =
	   * urlClassLoader.loadClass(name); } catch (Exception e) {
	   * e.printStackTrace(); } } } else {
	   */

	  synchronized (monitor) {

	    try {
	      String mpjHome = RTConstants.MPJ_HOME_DIR;
	      	
	      String libPath = null;

	      if (!cmdClassPath.equals("EMPTY")) {
		libPath = cmdClassPath + File.pathSeparator + mpjHome
		    + "/lib/mpi.jar" + File.pathSeparator + mpjHome
		    + "/lib/mpjdev.jar";
	      } else {
		libPath = mpjHome + "/lib/mpi.jar" + File.pathSeparator
		    + mpjHome + "/lib/mpjdev.jar";
	      }

	      // System.out.println("className = "+className) ;

	      if (className.endsWith(".jar")) {
		if ((new File(className)).isAbsolute()) {
		  // System.out.println("AbsolutePath") ;
		  // System.out.println("className = "+className) ;
		  appPath = className;
		} else {
		  appPath = wdir + "/" + className;
		}
	      } else {
		appPath = wdir;
	      }

	      appPath = appPath + File.pathSeparator + libPath;

	      ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

	      // System.out.println("appPath = "+appPath) ;

	      StringTokenizer tok = new StringTokenizer(appPath,
		  File.pathSeparator);
	      int count = tok.countTokens();
	      String[] tokArr = new String[count];
	      File[] f = new File[count];
	      URL[] urls = new URL[count];

	      for (int i = 0; i < count; i++) {
		tokArr[i] = tok.nextToken();
		f[i] = new File(tokArr[i]);
		urls[i] = f[i].toURI().toURL();
	      }

	      URLClassLoader ucl = new URLClassLoader(urls);
	      Thread.currentThread().setContextClassLoader(ucl);

	      if (className.endsWith(".jar")) {
		// System.out.println("Hello i am jar loader");
		// System.out.println("wdir ="+wdir) ;
		String jarFileName = className;
		// System.out.println("jarFileName ="+jarFileName) ;
		JarFile jarFile = new JarFile(jarFileName);
		Attributes attr = jarFile.getManifest().getMainAttributes();
		name = attr.getValue(Attributes.Name.MAIN_CLASS);
		c[index] = Class.forName(name, true, ucl);
	      } else {
		name = className;
		// System.out.println("num --" + num + " Thread "
		// +Thread.currentThread()+" Time "+System.nanoTime());
		c[index] = Class.forName(name, true, ucl);
		// c[num] = Class.forName(name);
	      }

	    }
	    catch (Exception exx) {
	      exx.printStackTrace();
	    }

	    arvs[1] = config;
	    arvs[2] = deviceName;

	    for (int i = 0; i < nargs.length; i++) {
	      arvs[i + 3] = nargs[i];
	    }

	    try {

	      if (classLoader != null && loader.equals("useRemoteLoader")) {
		// System.out.println("Remote loader invoking class");
		classLoader.invokeClass(c[num], arvs);
	      } else {
		// System.out.println("getting method " +
		// num+" Thread "+Thread.currentThread()+" Time "+System.nanoTime());
		// System.out.println(" -- getting method "+num);

		m[index] = c[index].getMethod("main",
		    new Class[] { arvs.getClass() });
		m[index].setAccessible(true);
		int mods = m[index].getModifiers();
		if (m[index].getReturnType() != void.class
		    || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
		  throw new NoSuchMethodException("main");
		}
		// m.invoke(null, new Object[] {arvs});
		method[index] = m[index];
	      }
	    }
	    catch (Exception exp) {
	    }
	    // //// placed end //////
	  }
	  synchronized (monitor) {

	    int val = rank.intValue();
	    val++;
	    rank = new Integer(val);
	    arvs[0] = rank.toString();
	    argNew[0] = rank.toString();
	    // System.out.println("rank " + rank);
	  }

	  // argNew[1] = arvs[1];
	  // argNew[2] = arvs[2];
	  for (int k = 1; k < arvs.length; k++) {
	    argNew[k] = arvs[k];
	    // System.out.println(" arg new " + argNew[k]);
	  }

	  // FIXME: need an elegant way to fill the index 1
	  // element, the issue is that it's filled earlier
	  // and here we are actually re-writing it ..
	  // don't like it ..but atleast works now!
	  argNew[1] = (new Integer(processes)).toString();

	  boolean tryAgain = true;

	  while (tryAgain) {

	    try {
	      // System.out.println(" num " + index);
	      // System.out.println("Starting process <"+index+"> on <"
	      // +hostName+">");
	      method[index].invoke(null, new Object[] { argNew });
	      tryAgain = false;
	      // System.out.println("Stopping process <"+index+"> on <"
	      // +hostName+">");
	    }
	    catch (Exception e) {
	      // System.out.println("please try running the code again ..");
	      e.printStackTrace();
	      tryAgain = false;
	      System.exit(0);
	      // tryAgain = true;
	      // System.out.println(" exception while invoking in " +
	      // Thread.currentThread());
	      // goto TRY_AGAIN ;
	      // This should not happen, as we have disabled access checks
	    }
	  }

	}
      };
    }
    // #######################################################
    // /////////////// cutted

    try {

      int nprocs = processes;

      Thread procs[] = new Thread[nprocs];
      // System.out.println("nprocs " + nprocs);
      // FIX ME By Rizwan & Amjad :
      // setting all threads thred group to MPI${rank}.
      // so that we can differeniate them from other threads i.e. system threads
      for (num = 0; num < nprocs; num++) {

	// procs[num] = new Thread(ab[num]);
	// smpProcess[num] = new SMPDevProcess("smp-threadgroup"+num);
	smpProcess[num] = new SMPDevProcess("MPI" + num);
	procs[num] = new Thread(smpProcess[num], ab[num], "" + nprocs);
	String name = String.valueOf(num);
	procs[num].setName(name);
	procs[num].start();

	// System.out.println("thread after start" + num+" Thread "+
	// Thread.currentThread()+" Time "+System.nanoTime());
	// procs[num].join();
	// Thread.currentThread().sleep(500);

      }

      for (int i = 0; i < nprocs; i++) {
	procs[i].join();
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  } // end execute

  public static void main(String args[]) throws Exception {
    MulticoreStarter mstarter = new MulticoreStarter();
    mstarter.execute(args);
  } // end main

  public static void visitAllDirs(File dir) {
    if (dir.isDirectory()) {
      appPath += dir + ":";

      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
	visitAllDirs(new File(dir, children[i]));
      }
    }
  }

}
