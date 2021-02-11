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
 * File         : MPJYarnWrapper.java 
 * Author       : Hamza Zafar
 */

package runtime.starter;

import java.io.*;
import java.lang.reflect.*;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.*;
import java.net.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

  public class MPJYarnWrapper {

    private Socket clientSock;
    private int np;
    private String serverName;
    private int ioServerPort;
    private String wireUpPort;
    private String deviceName;
    private String className;
    private Class c;
    private String wdir;
    private int psl;
    private String rank;
    private String[] appArgs;
    private String portInfo;
    private Options opts;
    private CommandLine cliParser;
   
    public MPJYarnWrapper(){
      opts = new Options();

      opts.addOption("serverName",true,"Hostname where the stdout and stderr "+
                                       "will be redirected");
      opts.addOption("ioServerPort",true,"Port required for a socket"+
                                                         " redirecting IO");
      opts.addOption("deviceName",true,"Specifies the MPJ device name");
      opts.addOption("className",true,"Main Class name");
      opts.addOption("psl",true,"Protocol Switch Limit");
      opts.addOption("np",true,"Number of Processes");
      opts.addOption("rank",true,"Rank of the process, it is set by AM");
      opts.addOption("wireUpPort",true,"Port required by NIODev to share"+
                                       "wireup information");
      opts.addOption("appArgs",true,"Specifies the User Application args");
      opts.getOption("appArgs").setArgs(Option.UNLIMITED_VALUES);

    }
     
    public void init(String [] args){
      try{
         cliParser = new GnuParser().parse(opts, args);
 
         np = Integer.parseInt(cliParser.getOptionValue("np"));
         serverName = cliParser.getOptionValue("serverName");
         ioServerPort =Integer.parseInt(cliParser.getOptionValue
                                                            ("ioServerPort"));
         wireUpPort = cliParser.getOptionValue("wireUpPort");
         deviceName = cliParser.getOptionValue("deviceName");
         className = cliParser.getOptionValue("className");
         wdir = cliParser.getOptionValue("wdir");
         psl = Integer.parseInt(cliParser.getOptionValue("psl"));
         rank = cliParser.getOptionValue("rank");
 
         if(cliParser.hasOption("appArgs")){
           appArgs = cliParser.getOptionValues("appArgs");
         }
    
         portInfo ="#Number of Processes;" + np +
                  ";#Protocol Switch Limit;" + psl +
                  ";#Server Name;" + serverName +
                  ";#Server Port;"+ wireUpPort;
       }
       catch(Exception exp){
         exp.printStackTrace();
       }
    }
    public void run(){

      try{
        clientSock = new Socket(serverName, ioServerPort);
      }
      catch(UnknownHostException exp){
        System.err.println("Unknown Host Exception, Host not found");
        exp.printStackTrace();
      }
      catch(IOException exp){
        exp.printStackTrace(); 
      }
     
      // Redirecting Output Stream 
      try{
        System.setOut(new PrintStream(clientSock.getOutputStream(),true)); 
        System.setErr(new PrintStream(clientSock.getOutputStream(),true));
      }
      catch(IOException e){
       e.printStackTrace();
      }

      try{
        c = Class.forName(className);
      }
      catch(ClassNotFoundException exp){
        exp.printStackTrace();
      }
      
      try {
        String [] arvs = new String[3];
        
        if(appArgs!=null){
          arvs = new String[3 + appArgs.length];
        }
        arvs[0] = rank;
        arvs[1] = portInfo;
        arvs[2] = deviceName;
	
        if(appArgs !=null){  
          for(int i=0; i < appArgs.length; i++){
            arvs[3+i] = appArgs[i];
          }
        }

        InetAddress localaddr = InetAddress.getLocalHost();
        String hostName = localaddr.getHostName();

        System.out.println("Starting process <"+rank+"> on <"+hostName+">");

        Method m = c.getMethod("main", new Class[] { arvs.getClass() });
        m.setAccessible(true);
        int mods = m.getModifiers();
        
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods)
            || !Modifier.isPublic(mods)) {
          throw new NoSuchMethodException("main");
        }

        m.invoke(null, new Object[] { arvs });

        System.out.println("Stopping process <"+rank+"> on <"+hostName+">");

        System.out.println("EXIT");//Stopping IOThread
        
        try{
          clientSock.close();
        }
	catch(IOException e){
          e.printStackTrace();
        }
      }
      catch (Exception ioe) {
        ioe.printStackTrace();
      }

    }


    public static void main(String args[]) throws Exception {
      MPJYarnWrapper wrapper = new MPJYarnWrapper();
      wrapper.init(args);
      wrapper.run(); 
    }
  }
                                                                             
