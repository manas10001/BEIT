package runtime.daemonmanager;

import java.util.ArrayList;

public class ProcessInfoThread extends DMThread {

  private String host = "localhost";

  public ProcessInfoThread(String machineName) {
    host = machineName;
  }

  public void run() {
    getJavaProcesses();
  }

  public void getJavaProcesses() {
    String userName = System.getProperty("user.name");
    ArrayList<String> consoleMessages = DaemonUtil.getJavaProcesses(host);
    int messageCount = 0;
    for (String message : consoleMessages) {
      if (message.toLowerCase().indexOf("jps") < 0) {
	System.out.println("[" + userName + " @ " + host + "] " + message);
	messageCount++;
      }
    }
    if (messageCount == 0) {
      System.out.println("[" + userName + " @ " + host + "] "
	  + DMMessages.NO_JAVA_PROCESS_RUNNING);
    }
  }

}
