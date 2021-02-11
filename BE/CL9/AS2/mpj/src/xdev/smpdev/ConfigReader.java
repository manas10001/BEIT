/*
 The MIT License

 Copyright (c) 2005 - 2010
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2004)
   3. Aamir Shafi (2005 - 2010)
   4. Bryan Carpenter (2005 - 2010)
   5. Jawad Manzoor (2009)

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
 * File         : ConfigReader.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 BST 2004
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2005/07/31 15:14:15 $
 *    
 */

package xdev.smpdev ;
import java.io.*;
import java.util.*;
import java.net.*;

/**
 * <p>This class is a utiliy class that is used to read the start-up information from a configuration file. The users
 * of the mpjdev do not have to deal with this file. </p>
 */

public class ConfigReader {
  private BufferedReader bufferedReader = null;
  URL aURL = null;
  InputStream in = null;

  private String delimiter = null;
  private int numberOfHosts = 0;
  int count = 1;
  String fileName = null;
  /**
   * Constructor of this utility class
   * @param fileName String The name of the configuration file. This can be on the local file system or
   * on some server accessible via http.
   * @throws IOException If some I/O error happens.
   * @throws FileNotFoundException If the file is not found.
   * @throws MalformedURLException If the http URL is malformed.
   */
  public ConfigReader(String fileName) throws IOException,
      FileNotFoundException, MalformedURLException {

    this.fileName = fileName;

    if (fileName.startsWith("http://")) {

      try {
        aURL = new URL(new String(fileName));
      }
      catch (MalformedURLException mue) {
        mue.printStackTrace();
        System.exit(0);
      }

      try {
        in = aURL.openStream();
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }

      bufferedReader = new BufferedReader(new InputStreamReader(in));
      //this is the http URL now !!!!
    }
    else {
      try {
        bufferedReader = new BufferedReader(new FileReader(this.fileName));
      }
      catch (FileNotFoundException fnfe) {
        System.out.println("File Not Found Exception" + this.fileName);
        System.out.println("Please locate and Make sure that the appropriate configuration file is lying in " +
                           "the appropriate directory .....");
        System.exit(0);
      }
    }
  } //end ConfigReader constructor !

  /**
   * Read the number of processes mentioned in the configuration file.
   * @throws IOException If some I/O error occurs
   * @return String The number of total processes
   */
  public String readNoOfProc() throws IOException {
    String temp = null;
    boolean loop = true;
    while (loop) {
      try {
        temp = bufferedReader.readLine();
      }
      catch (IOException ioe) {
        System.out.println("Problem reading the config file" + this.fileName);
        System.exit(0);
      }

      if (temp == null || temp.equals("")) {
        continue;
      }

      if (temp.startsWith("#")) {
        continue;
      }
      temp = temp.trim();
      numberOfHosts = (new Integer(temp)).intValue();
      loop = false;
    }
    return temp;
  }

  /**
   * Reads integer as a string
   * @throws IOException
   * @return String
   */
  public String readIntAsString() throws IOException {
    String temp = null;
    boolean loop = true;
    while (loop) {
      try {
        temp = bufferedReader.readLine();
      }
      catch (IOException ioe) {
        System.out.println("Problem reading the config file" + this.fileName);
        System.exit(0);
      }

      if (temp == null || temp.equals("")) {
        continue;
      }

      if (temp.startsWith("#")) {
        continue;
      }
      temp = temp.trim();
      loop = false;
    }
    return temp;
  }

  /**
   * Reads line from the configuration file.
   * @throws IOException
   * @return String
   */
  public String readLine() throws IOException {
    if (count > numberOfHosts) {
      return null;
    }
    String temp;
    try {
      temp = bufferedReader.readLine();
    }
    catch (IOException ioe) {
      System.out.println("IOException");
      ioe.printStackTrace();
      return null;
    }

    if (temp == null || temp.equals("")) {
      System.out.println("empty or whitespace");
      return "empty";
    }

    if (temp.startsWith("#")) {
      //System.out.println("#");
      return "#";
    }

    //return if we have read whatever the user has asked for, we dont want to
    //read more than what is required ...

    //System.out.println("Before trmming in readLine"+temp);
    temp = temp.trim();
    count++;
    return temp;
  }

  /**
   * Close all the streams.
   */
  public void close() {
    try {
      bufferedReader.close();
      in.close();
    }
    catch (Exception ioe) {
    }
    finally {
      bufferedReader = null;
      aURL = null;
      in = null;
    }
  } //end close()

  /** Just for testing this class ! ! ! **/
  public static void main(String args[]) throws Exception {

    String CONFIGURATION_FILE = new String("mpjdev.conf");
    int NUMBER_OF_HOSTS = 0;
    ConfigReader reader = new ConfigReader(CONFIGURATION_FILE);
    NUMBER_OF_HOSTS = (new Integer(reader.readNoOfProc())).intValue();
    System.out.println("NUMBER_OF_HOSTS " + NUMBER_OF_HOSTS);
    //this would get the total hosts

    String line = null;
    while ( (line = reader.readLine()) != null) {
      if (line.equals("empty") || line.equals("#")) {
        continue;
      }
      line = line.trim();
      StringTokenizer tokenizer = new StringTokenizer(line, "@");
      System.out.println("Node<" + tokenizer.nextToken() + ">");
      System.out.println("Num<" + tokenizer.nextToken() + ">"); //this gets you names of individual machines,
    }
    reader.close();
  } //end static void main()
}
