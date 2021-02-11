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
 * File         : JarClassLoader.java 
 * Author       : Taken from URL below ...
 * Created      : Sun Dec 12 12:22:15 BST 2004
 * Revision     : $Revision: 1.7 $
 * Updated      : $Date: 2005/08/23 17:09:31 $
 */

/* 
 * This file is taken from a Sun's tutorial on JAR files. It can be 
 * seen on the following URL ...
 * http://java.sun.com/developer/Books/javaprogramming/JAR/api/
 */

package runtime.daemon;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

/**
 * A class loader for loading jar files, both local and remote.
 */
public class JarClassLoader extends URLClassLoader {
  private URL url;

  /**
   * Creates a new JarClassLoader for the specified url. First url should point
   * to user app jar. This jar would be used to find the main class name
   * 
   * @param url
   *          the url of the jar file
   */
  public JarClassLoader(URL[] urls) {
    super(urls);
    this.url = urls[0];
  }

  /**
   * Returns the name of the jar file main class, or null if no "Main-Class"
   * manifest attributes was defined.
   */
  public String getMainClassName() throws IOException {
    URL u = new URL("jar", "", url + "!/");
    JarURLConnection uc = (JarURLConnection) u.openConnection();
    Attributes attr = uc.getMainAttributes();
    return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
  }

  /**
   * Invokes the application in this jar file given the name of the main class
   * and an array of arguments. The class must define a static method "main"
   * which takes an array of String arguemtns and is of return type "void".
   * 
   * @param name
   *          the name of the main class
   * @param args
   *          the arguments for the application
   * @exception ClassNotFoundException
   *              if the specified class could not be found
   * @exception NoSuchMethodException
   *              if the specified class does not contain a "main" method
   * @exception InvocationTargetException
   *              if the application raised an exception
   */
  public void invokeClass(Class c, String[] args)
      throws ClassNotFoundException, NoSuchMethodException,
      InvocationTargetException {
    // Class c = loadClass(name);
    Method m = c.getMethod("main", new Class[] { args.getClass() });
    m.setAccessible(true);
    int mods = m.getModifiers();
    if (m.getReturnType() != void.class || !Modifier.isStatic(mods)
	|| !Modifier.isPublic(mods)) {
      throw new NoSuchMethodException("main");
    }
    try {
      m.invoke(null, new Object[] { args });
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      // This should not happen, as we have disabled access checks
    }
  }

  public static void main(String args[]) {
    /*
     * //if (args.length < 2) { //
     * System.out.println("Usage: java JarClassLoader <URL> <rank>"); //
     * System.exit(0); //}
     * 
     * // JarClassLoader loader = new JarClassLoader(); //args[0] ==
     * http://holly.dsg.port.ac.uk:15000/client.jar //args[1] == 0 (the rank)
     * 
     * /* String config = null; String conf =
     * args[0].substring(0,(args[0].lastIndexOf("/")+1));
     * System.out.println("conf ==<"+conf+">"); config = conf+"mpjdev.conf";
     * System.out.println("config ==<"+config+">"); try { JarClassLoader
     * classLoader = new JarClassLoader( new URL(args[0]) ); String name =
     * classLoader.getMainClassName(); System.out.println("name ==>"+name);
     * Class c = classLoader.loadClass(name); //String arvs[] = new String[2];
     * //arvs[0] = args[1]; //arvs[1] = config; classLoader.invokeClass(c,new
     * String[0]); }catch(Exception ioe) { ioe.printStackTrace(); }
     */
  }

}
