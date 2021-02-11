         
          QuickStart Guide: Running MPJ Express on Windows Platform 
                  Last Updated: Friday July 18 12:44:20 PKT 2014
                                Version 0.44

Introduction
============

MPJ Express is a reference implementation of the mpiJava 1.2 API, which
is an MPI-like API for Java defined by the Java Grande forum. 

MPJ Express can be configured in two ways: 

1. Multicore Configuration: This configuration is used by developers who want 
   to execute their parallel Java applications on multicore or shared 
   memory machines (laptops and desktops).

2. Cluster Configuration: This configuration is used by developers who want to 
   execute their parallel Java applications on distributed memory platforms
   including clusters and network of computers. There are four options in the 
   cluster configuration.
   
   i) niodev - uses Java NIO Sockets
   ii) mxdev - uses Myrinet eXpress (MX) library for Myrinet networks
   iii) hybdev - for clusters of multicore processors
   iv) native - uses a native MPI library (curretly only tested under MS-MPI
		  			   for Windows)
Pre-requisites
==============
1. Java 1.6 (stable) or higher (Mandatory)
2. Apache ant 1.6.2 or higher (Optional)
3. Perl (Optional)
4. A native MPI library (Optional): Native MPI library such as MS-MPI
   is required for running MPJ Express in cluster configuration with 
   native device. 
5. Visual Studio (Optional): Visual Studio is used to compile JNI C code
   used by native device.

Running MPJ Express Programs in the Multicore Configuration
===========================================================

1. Download MPJ Express and unpack it. 
2. Set MPJ_HOME and PATH environmental variables.
    - Windows XP, Vista, or 7 (assuming mpj is in 'c:\mpj')
      Right-click My Computer->Properties->Advanced tab->Environment Variables
      and export the following system variables (User variables are not enough)
	  Set the value of variable MPJ_HOME as c:\mpj 
	  Append the c:\mpj\bin directory to the PATH variable
    - Cygwin on Windows (assuming mpj is 'c:\mpj')
	  The recommended way to is to set variables as in Windows
	  If you want to set variables in cygwin shell
          export MPJ_HOME="c:\\mpj"
          export PATH=$PATH:"$MPJ_HOME\bin" 
3. Write your MPJ Express program (HelloWorld.java) and save it. 
4. Compile: javac -cp .;%MPJ_HOME%/lib/mpj.jar HelloWorld.java
5. Execute: mpjrun.bat -np 4 HelloWorld.java

Running MPJ Express Programs in the Cluster Configuration
=========================================================

1. Assuming you have completed step 1 to 4 of the Multicore Configuration. 
2. Write a machines file (name it "machines") stating host names or 
	 IP addresses of all machines involved in the parallel execution.
3. Execution:
-- For niodev, hybdev and mxdev
	i) Start daemons: mpjdaemon.bat -boot
		- You will need to manually run this command on every machine to start 
		  daemons.
	ii) Execute: mpjrun.bat -np 4 -dev niodev HelloWorld
		-- For -dev <device> here device can be niodev, hybdev or mxdev
	iii) Stop daemons: mpjdaemon.bat -halt
-- For native deive
	i) Compile JNI wrapper library: Follow the windowsguide.pdf 
					instructions on how to compile and
					generate .dll library
	ii) Execute: mpjrun.bat -np 4 -dev native HelloWorld


For detials read the windowsguide.pdf that can be found in $MPJ_HOME/doc


Additional Documentation
========================

For more details, see $MPJ_HOME/doc/windowsguide.pdf

Contact and Support
===================

In case you run into issues please consult $MPJ_HOME/doc/windowsguide.pdf. If 
your query/problem is still not resolved, contact us by emailing: 

1. MPJ Express mailing list: https://lists.sourceforge.net/lists/listinfo/mpjexpress-users
2. Aamir Shafi (aamir.shafi@seecs.edu.pk)
3. Mohsan Jameel (mohsan.jameel@seecs.edu.pk)
4. Bryan Carpenter (bryan.carpenter@port.ac.uk)
5. Muhammad Ansar Javed (muhammad.ansar@seecs.edu.pk)
6. Bibrak Qamar (bibrak.qamar@seecs.edu.pk)
7. Aleem Akhtar (aleem.akhtar@seecs.edu.pk)
8. Hamza Zafar (11bscshzafar@seecs.edu.pk)
