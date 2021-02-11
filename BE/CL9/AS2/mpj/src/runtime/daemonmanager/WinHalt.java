package runtime.daemonmanager;

import java.util.ArrayList;
import runtime.common.MPJUtil;

public class WinHalt {

  private String host = "localhost";

  public void haltMPJExpress() {
    String pid = DaemonUtil.getMPJProcessID(host);
    if (pid != "") {
      String[] command = { "taskkill", "/f", "/pid", pid, };
      ArrayList<String> consoleMessages = DaemonUtil.runProcess(command);
      for (String message : consoleMessages) {
	if (message.indexOf(DMMessages.UNKNOWN_HOST) > 0)
	  System.out.println(MPJUtil.FormatMessage(host,
	      DMMessages.HOST_INACESSABLE));
      }
      pid = DaemonUtil.getMPJProcessID(host);
      if (pid == "")
	System.out.println(MPJUtil.FormatMessage(host,
	    DMMessages.MPJDAEMON_STOPPED));
    } else
      System.out.println(MPJUtil.FormatMessage(host,
	  DMMessages.MPJDAEMON_NOT_AVAILABLE));
  }

}
