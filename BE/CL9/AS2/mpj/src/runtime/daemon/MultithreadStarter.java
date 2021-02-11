/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)

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
 * File         : MultithreadStarter.java 
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sun Dec 12 12:22:15 BST 2004
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 */

/* 
 *@author Aamir Shafi, Bryan Carpenter
 */
//package xdev.smpdev; 
package runtime.daemon;

/** 
 * This class is meant to be part of MPJ runtime the main aim of this class
 * is to simply start N number of threads, where each thread is an MPI 
 * process (when it ll be part of MPJ runtime)
 */

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MultithreadStarter {
  Method method = null;
  String[] newArgs = new String[2];
  Integer rank = new Integer(-1);

  public static void main(String args[]) throws Exception {
    if (args.length < 2) {
      System.out.println("java className nprocs");
      return;
    }
    MultithreadStarter starter = new MultithreadStarter(args);
  }

  public MultithreadStarter(String[] args) throws Exception {

    Runnable ab = new Runnable() {
      public void run() {
	synchronized (rank) {
	  int val = rank.intValue();
	  val++;
	  rank = new Integer(val);
	  newArgs[1] = rank.toString();
	  // System.out.println(" newArgs[0] "+ newArgs[0] + "__"+
	  // Thread.currentThread() );
	  // System.out.println(" newArgs[1] "+ newArgs[1] + "__"+
	  // Thread.currentThread() );
	}

	try {
	  System.out.println(" invoking for <" + Thread.currentThread());
	  MultithreadStarter.this.method.invoke(null,
	      new Object[] { MultithreadStarter.this.newArgs });
	  System.out.println(" invoked for <" + Thread.currentThread());
	}
	catch (Exception e) {
	  System.out.println(" exception while invoking in "
	      + Thread.currentThread());
	  e.printStackTrace();
	  // This should not happen, as we have disabled access checks
	}

      }

    };

    String className = args[0];
    int nprocs = Integer.parseInt(args[1]);
    Thread procs[] = new Thread[nprocs];
    newArgs[0] = args[1];
    Class c = Class.forName(className);
    method = c.getMethod("main", new Class[] { args.getClass() });
    method.setAccessible(true);
    int mods = method.getModifiers();

    if (method.getReturnType() != void.class || !Modifier.isStatic(mods)
	|| !Modifier.isPublic(mods)) {
      throw new NoSuchMethodException("main");
    }

    for (int i = 0; i < nprocs; i++) {

      procs[i] = new Thread(ab);
      // newArgs[0] = String.valueOf(i);
      procs[i].start();
      Thread.currentThread().sleep(50);
    }
    // System.out.println("className<"+className+">");
    // System.out.println("nprocs<"+nprocs+">");
    System.out.println("Calling join ");
    for (int i = 0; i < nprocs; i++) {
      procs[i].join();
    }
    System.out.println("Exiting multithreadstarter");

  }

}
