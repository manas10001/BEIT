package runtime.daemonmanager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import runtime.common.MPJUtil;

public class WinBoot {

  public WinBoot() {
  }

  private String host = "localhost";
  private String port = "";// MPJUtil.getConfigValue(DMConstants.CONF_PORT_KEY);

  public void startMPJExpress(String port) throws IOException {
    this.port = port;
    if (validExecutionParams()) {
      try {
	String[] command = { "java", "-cp",
	    MPJUtil.getJarPath("daemon") + ";.", "runtime.daemon.MPJDaemon",
	    port, };
	ArrayList<String> consoleMessages = DaemonUtil.runProcess(command,
	    false);
	String pid = DaemonUtil.getMPJProcessID(host);
	if (!pid.equals("") && Integer.parseInt(pid) > -1) {
	  System.out.println(MPJUtil.FormatMessage(host,
	      DMMessages.MPJDAEMON_STARTED + pid));
	} else {
	  for (String message : consoleMessages)
	    System.out.println(message);
	}

      }
      catch (Exception ex) {
	System.out.print(ex.getMessage() + "\n" + ex.getStackTrace());
      }
    }

  }

  private boolean validExecutionParams() {

    InetAddress address = null;
    try {
      address = InetAddress.getByName(host);
    }
    catch (UnknownHostException e) {

      e.printStackTrace();
      System.out.println(e.getMessage());
      return false;
    }
    if (MPJUtil.IsBusy(address, Integer.parseInt(port))) {
      System.out.println(MPJUtil.FormatMessage(host, DMMessages.BUSY_PORT));
      return false;
    }
    return true;
  }

}
