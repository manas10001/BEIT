/*

/*
 * File         : Type.java
 * Author       : Jawad Manzoor
 * Created      : Tue Jul  21 2004
 * Revision     : 
 * Updated      : $Date: 2009/07/21
 *    
 */
package mpjbuf;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.net.URLClassLoader;

public class CustomObjectInputStream extends ObjectInputStream {


    URLClassLoader urlCl;

    protected CustomObjectInputStream() throws IOException {
    }

    public CustomObjectInputStream(InputStream in) throws IOException {
          super(in);
    }

    public void setClassLoader(URLClassLoader ucl){
    urlCl = ucl;
    }


    @Override
    public Class resolveClass(ObjectStreamClass desc) throws IOException {

        String name = desc.getName();
        try {
            URLClassLoader u = (URLClassLoader)Thread.currentThread().getContextClassLoader();
          //  System.out.println("CL "+u.toString());
            return Class.forName(name, false, u);
        } catch (ClassNotFoundException ex) {
            /*  Class cl = (Class) primClasses.get(name);
            if (cl != null) {
            return cl;
            } else {
            throw ex;
            }
             */
            return null;
        }
    }
}

