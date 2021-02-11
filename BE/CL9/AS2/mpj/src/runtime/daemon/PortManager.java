/*
 The MIT License

 Copyright (c) 2013
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
   

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
 * File         : PortManager.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : Oct 27, 2013
 * Revision     : $
 * Updated      : Aug 26, 2014 
 */

package runtime.daemon;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortManager {

  public static int startingPort = 25000;
  public static int minPort = 25000;
  public static int maxPort = 49000;

  public static List<Integer> usedPorts = Collections
      .synchronizedList(new ArrayList<Integer>());

  public static int getNextAvialablePort() {
    int portNo = minPort;
    for (int i = minPort; i <= maxPort; i++) {
      if (isOpened(i) && !usedPorts.contains(i)) {
	portNo = i;
	usedPorts.add(i);
	minPort += 1;
	if (minPort >= maxPort - 2)
	  minPort = startingPort;
	return portNo;
      }
    }
    return portNo;
  }

  private static boolean isOpened(int port) {
    ServerSocket sock = null;
    DatagramSocket dataSock = null;
    try {
      sock = new ServerSocket(port);
      sock.setReuseAddress(true);
    }
    catch (final IOException e) {
      System.err.println("[PortManager.java]: Unable to open port..");
      e.printStackTrace();
      return false;
    }
    
    try {
      sock.close();
    }
    catch (final IOException e) {
      System.err.println("[PortManager.java]: Unable to close port..");
      e.printStackTrace();
    }

    return true;
  }
}

