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
 * File         : KillYarnApp.java 
 * Author       : Hamza Zafar
 */


package runtime.starter;

import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;

public class KillYarnApp extends Thread {

  private ApplicationId appId;
  private YarnClient appManager;

  public KillYarnApp(ApplicationId appId, YarnClient appManager) {
    this.appId = appId;
    this.appManager = appManager;
  }

  @Override
  public void run() {
    if (MPJYarnClient.isRunning) {
      try {
          System.out.println("Killing Application Forcefully");
          appManager.killApplication(appId); 
      } catch (Exception exp) {
        System.err.println("Error when Killing Application");
        exp.printStackTrace();
      }
    }
  }

}
