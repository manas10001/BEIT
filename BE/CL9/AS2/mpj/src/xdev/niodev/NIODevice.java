/*
 The MIT License

 Copyright (c) 2005 - 2014
   1. Distributed Systems Group, University of Portsmouth (2014)
   2. Aamir Shafi (2005 - 2014)
   3. Bryan Carpenter (2005 - 2014)
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
 * File         : NIODevice.java
 * Author(s)    : Aamir Shafi, Bryan Carpenter, Farrukh Khan
 * Created      : Apr 09, 2004
 * Revision     : Revision: 1.30
 * Updated      : Aug 27, 2014
 *
 */

package xdev.niodev;

import runtime.daemon.Wrapper;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.util.*;
import mpjbuf.*;
import mpjdev.*;
import java.util.concurrent.Semaphore;
import xdev.*;
import java.io.IOException;
import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import org.apache.log4j.Logger;

/**
 * <p>
 * This class is implementation of <i>xdev</i> based on the Java New I/O
 * package.
 * </p>
 * 
 * <h3>Overview</h3>
 * <p>
 * Java New I/O adds non-blocking I/O to the Java language, which is extensively
 * used in this device, to provide MPI functionality. Instead of directly using
 * {@link java.net.Socket java.net.Socket}, niodev uses
 * {@link java.nio.channels.SocketChannel java.nio.channels.SocketChannel}. This
 * device alongwith the request classes like {@link xdev.niodev.NIORequest
 * xdev.niodev.NIORequest}, {@link xdev.niodev.NIOSendRequest
 * xdev.niodev.NIOSendRequest}, and {@link xdev.niodev.NIORecvRequest
 * xdev.niodev.NIORecvRequest} forms the basis of communication functionality.
 * </p>
 * 
 * <h3>Initialization</h3>
 * <p>
 * 'niodev' reads a configuration file which could be placed in the local/shared
 * file system, or is accessbile through http server. The device reads this
 * configuration file, and tries to find the IP@PORT@RANK entry. The basis for
 * this search is the rank provided to the device by the runtime infrastructure.
 * Once this entry is located , the device knows which ports to start the
 * {@link ServerSocketChannel ServerSocketChannel} on. Once the server socket
 * channels are started at this port and port+1, these are registered with the
 * selector to accept connections. Every time a client socket connects to this
 * server socket channel, an OP_ACCEPT event is generated. After starting these
 * server sockets, a process connects to the process with rank lesser than its
 * own. This essentially means that if there are four processes, then Process 0
 * will start two server sockets, Process 1 will start two server socket, and
 * then try to connect to server sockets of Process 0. Similarly after starting
 * two server sockets, Process 2 and 3 will connect to Processes 0&1, and
 * Processes 0&1&2 respectively.
 * </p>
 * <p>
 * Every time, niodev accepts or connects, it puts the
 * {@link java.nio.channels.SocketChannel java.nio.channels.SocketChannel} into
 * an instance of {@link java.util.Vector java.util.Vector} writableChannels
 * (for writing messages) or readableChannels (for reading messages), depending
 * on the serverSocketPort. Note that accepting client request is done in the
 * selector thread, and connecting to server socket is done in the user-thread.
 * This may result in concurrent access to writableChannels and
 * readableChannels, and thus access to these should be synchronized. Once
 * alltoall connectivity has been acheived, which means [writableChannels.size()
 * == N-1] and [readableChannels.size() == N-1], then each process need to send
 * information like its rank and UUID to every other process. These rank are the
 * ones read from the configuration file provided by the MPJ runtime
 * infrastructure. Once all the processes have exchanged this information,
 * niodev has worldWritableTable and worldReadableTable, which are instances of
 * {@link java.util.Hashtable java.util.Hashtable}. These two hashtables contain
 * UUID as keys, and SocketChannels as values. Note that the channels in
 * 'worldWritableTable' are in blocking mode and are only used for writing
 * messages. For 'niodev', we have decided to keep different channels for
 * reading and writing. The reason is that we want to use non-blocking reads and
 * blocking writes. Non-blocking writes could hurt 'thread-safety' of niodev, or
 * result in very complex code. These hashtables would be later used in
 * send/recv method to obtain the reference of SocketChannel while providing key
 * as the UUID of each process. These UUID, are contained within the
 * {@link xdev.ProcessID xdev.ProcessID} objects. Again, while exchanging
 * information, access to worldWriteTable, and worldReadableTable should be
 * synchronized. Normally, the user thread sends all the information, and then
 * waits to selector thread to receive similar messages from all the other
 * processes. When the selector thread reads a message, it first looks at the
 * first four bytes, and after looking at the header information, adds the
 * information received appropriately to one of the hashtables. The value of
 * headers could be INIT_MSG_HEADER_DATA_CHANNEL, and
 * INIT_MSG_HEADER_CONTROL_CHANNEL. Once all of this is done, niodev has been
 * initialized.
 * </p>
 * 
 * <h3>Modes of Send</h3>
 * <p>
 * <a href="http://www.mpi-forum.org"> MPI specifications </a> defines four
 * modes of send operation. These are: standard mode of send, buffered mode of
 * send, ready mode of send, and synchronous mode of send. <i> xdev </i>
 * supports two modes of send -- standard and synchronous send. Ready send is
 * similar to standard mode of send, and buffered mode is supported at the
 * higher level alongwith the MPJ buffering API.
 * </p>
 * <h4>Standard Mode of Send</h4>
 * <p>
 * The standard mode of send uses two communication protocols. The first is
 * 'Eager-Send Protocol' and the second is 'Rendezvous Protocol'.
 * </p>
 * <h5>EagerSend Protocol</h5>
 * <p>
 * niodev uses eager send protocol to communicate small messages. The rationale
 * behind using this communication protcol is to minimize the latency for small
 * messages. This protocol assumes that the receiver has buffer space to store
 * the messages in case the matching recv is not posted. Eager-send protocol is
 * used for messages of size less than and equal to 128K bytes.
 * </p>
 * <img src="../../res/eagersend.png"/> <h5>Rendezvous Protocol</h5>
 * <p>
 * niodev uses rendezvous protocol to communicate large messages. Before
 * communicating large messages, there is an exchange of control messages to
 * make sure that a matching recv is posted. This is necessary to avoid
 * additional copying to temporary xdev buffer.
 * </p>
 * <img src="../../res/rendezvous.png"/> <h4>Synchronous Mode of Send</h4>
 * <p>
 * The synchronous mode of send uses rendezvous protocol described above for
 * communication.
 * </p>
 * <img src="../../res/syncmode.png" /> <h3>User and Selector Threads</h3>
 * <p>
 * During the initialization of xdev, xdev.NIODevice.init( ...) creates a
 * selector thread which is used to first accept connections. Once all-to-all
 * connectivity has been acheived, then the channels (both control and data)
 * register with the selectors for READ_EVENT. This essentially means that
 * whenever a channel receives some data, it generates OP_READ event, which
 * basically informs that there is some data to read on this channel. Thus, the
 * selector-thread is used normally for reading data from the channels. Also,
 * when there is a short write -- suppose a thread is trying to write 10K
 * message and only succeeds to write 5K bytes, then the channel register with
 * the selector for OP_WRITE event, and comes back to complete writing the
 * message into the SocketChannel.
 * </p>
 * <p>
 * The user thread is basically invoked when isend/issend/send/ssend/ recv/irecv
 * methods are called. <i> xdev </i> also attempts to provide multiple thread
 * functionality, which basically means there could be multiple user-threads and
 * trying to make calls to these (non) blocking send/recv methods.
 * </p>
 * <p>
 * This poses a great programming challenge, because user threads and selector
 * threads should synchronize before accessing send/recv queues that contain
 * pending messages that are waiting for the data to be written or read from the
 * channel
 * </p>
 * <h3>Send and Recv Queues</h3>
 * <p>
 * </p>
 * <h3>Same Process Communications</h3>
 * <p>
 * There is special case, when a process is trying to send and recv a message to
 * itself. In this case, the message is just copied from the sender buffer into
 * the receiver buffer. The complexity comes in when wild-card like ANY_SOURCE
 * are used.
 * </p>
 */
public class NIODevice implements Device {

  int index, root, extent, places;

  ProcTree procTree;

  long nextSequenceNum = 1L;

  /*
   * This semaphore is used to hold lock on send communication-sets
   */
  CustomSemaphore sLock = new CustomSemaphore(1);

  /*
   * This semaphore is used to hold lock while reading data from the
   * SocketChannel
   */
  CustomSemaphore sem = new CustomSemaphore(1);

  /*
   * For rendezvous protocol, selector thread receives the ACK messages and a
   * new thread is started that actually sends the messages. Selector thread
   * receives the message in a ByteBuffer which is read by rendezSend thread.
   * This semaphore is used to synchronize access to the buffer
   */
  CustomSemaphore buffer_sem = new CustomSemaphore(1);

  static Logger logger = Logger.getLogger("mpj");

  Vector<SocketChannel> writableChannels = new Vector<SocketChannel>();

  Vector<SocketChannel> readableChannels = new Vector<SocketChannel>();

  Hashtable<UUID, SocketChannel> worldWritableTable = new Hashtable<UUID, SocketChannel>();

  Hashtable<UUID, SocketChannel> worldReadableTable = new Hashtable<UUID, SocketChannel>();

  Hashtable<SocketChannel, CustomSemaphore> writeLockTable = new Hashtable<SocketChannel, CustomSemaphore>();

  // private static final boolean DEBUG = false ;
  // static final boolean DEBUG = true ;

  InetAddress localaddr = null;

  Selector selector = null;

  volatile boolean selectorFlag = true;

  private HashMap<Integer, NIOSendRequest> sendMap = new HashMap<Integer, NIOSendRequest>();

  private int sendCounter = 0;

  private int recvCounter = 0;

  HashMap<Integer, NIORecvRequest> recvMap = new HashMap<Integer, NIORecvRequest>();

  class RecvQueue {

    private HashMap<Key, NIORecvRequest> map = new HashMap<Key, NIORecvRequest>();

    private NIORecvRequest get(Key key) {
      return map.get(key);
    }

    private void add(Key key, NIORecvRequest recv) {
      NIORecvRequest head = map.get(key);

      if (head == null) {
	recv.recvNext = recv;
	recv.recvPrev = recv;
	map.put(key, recv);
      } else {
	NIORecvRequest last = head.recvPrev;

	last.recvNext = recv;
	head.recvPrev = recv;

	recv.recvPrev = last;
	recv.recvNext = head;
      }
    }

    private void rem(Key key, NIORecvRequest recv) {
      NIORecvRequest head = map.get(key);

      if (recv == head) {
	if (recv.recvNext == recv) {
	  map.remove(key);
	} else {
	  NIORecvRequest next = recv.recvNext;
	  NIORecvRequest last = recv.recvPrev;

	  last.recvNext = next;
	  next.recvPrev = last;

	  map.put(key, next);
	}
      } else {
	NIORecvRequest next = recv.recvNext;
	NIORecvRequest prev = recv.recvPrev;

	prev.recvNext = next;
	next.recvPrev = prev;
      }
    }

    void add(NIORecvRequest request) {
      /*
       * Checking if Hybrid device is enabled then source and destination of
       * message in key should be Hybrid source and destination, not those of
       * Network source and destination
       */
      if (isHybrid) {
	request.recvKey = new NIODevice.Key(request.context,
	    request.dstHybUUID, request.srcHybUUID, request.tag);

      } else {
	request.recvKey = new NIODevice.Key(request.context, request.dstUUID,
	    request.srcUUID, request.tag);
      }

      add(request.recvKey, request);
    }

    NIORecvRequest rem(int context, UUID dstUUID, UUID srcUUID, int tag) {

      Key[] keys = new NIODevice.Key[] {
	  new NIODevice.Key(context, dstUUID, srcUUID, tag),
	  new NIODevice.Key(context, dstUUID, srcUUID, xdev.Device.ANY_TAG),
	  new NIODevice.Key(context, dstUUID, xdev.Device.ANY_SRC.uuid(), tag),
	  new NIODevice.Key(context, dstUUID, xdev.Device.ANY_SRC.uuid(),
	      xdev.Device.ANY_TAG) };

      NIORecvRequest matchingRecv = null;

      long minSequenceNum = Long.MAX_VALUE;

      for (int i = 0; i < keys.length; i++) {
	NIORecvRequest recv = get(keys[i]);

	if (recv != null && recv.sequenceNum < minSequenceNum) {
	  minSequenceNum = recv.sequenceNum;
	  matchingRecv = recv;
	}
      }

      if (matchingRecv != null) {
	rem(matchingRecv.recvKey, matchingRecv);
      }

      return matchingRecv;
    }

  }

  RecvQueue recvQueue = new RecvQueue();

  class ArrvQueue {

    private HashMap<Key, NIORecvRequest> map = new HashMap<Key, NIORecvRequest>();

    NIORecvRequest rem(int context, UUID dstUUID, UUID srcUUID, int tag) {

      Key key = new Key(context, dstUUID, srcUUID, tag);
      NIORecvRequest matchingSend = get(key);

      if (matchingSend != null) {
	Key[] keys = matchingSend.arrKeys;
	for (int i = 0; i < keys.length; i++) {
	  rem(i, keys[i], matchingSend);
	}
      }

      return matchingSend;
    }

    NIORecvRequest remForIprobeAndFetch(int context, UUID dstUUID,
	UUID srcUUID, int tag) {

      Key key = new Key(context, dstUUID, srcUUID, tag);
      NIORecvRequest request = get(key);

      if (request != null) {

	if (((request.sBufSize + request.dBufSize) <= psl)
	    && request.commMode == STD_COMM_MODE) {

	  Key[] keys = request.arrKeys;
	  for (int i = 0; i < keys.length; i++) {
	    rem(i, keys[i], request);
	  }
	  request.notifyMe();
	  return request;
	} else if ((((request.sBufSize + request.dBufSize) > psl) && request.commMode == STD_COMM_MODE)
	    || (request.commMode == SYNC_COMM_MODE)) {
	  return request;
	}
      }

      return request;
    }

    private NIORecvRequest get(Key key) {
      return map.get(key);
    }

    private void add(int i, Key key, NIORecvRequest send) {
      NIORecvRequest head = map.get(key);

      if (head == null) {
	send.arrNext[i] = send;
	send.arrPrev[i] = send;
	map.put(key, send);
      } else {
	NIORecvRequest last = head.arrPrev[i];
	last.arrNext[i] = send;
	head.arrPrev[i] = send;

	send.arrPrev[i] = last;
	send.arrNext[i] = head;
      }
    }

    private void rem(int i, Key key, NIORecvRequest send) {

      NIORecvRequest head = map.get(key);

      if (send == head) {
	if (send.arrNext[i] == send) {
	  map.remove(key);
	} else {
	  NIORecvRequest next = send.arrNext[i];
	  NIORecvRequest last = send.arrPrev[i];
	  last.arrNext[i] = next;
	  next.arrPrev[i] = last;
	  map.put(key, next);
	}
      } else {
	NIORecvRequest next = send.arrNext[i];
	NIORecvRequest prev = send.arrPrev[i];

	prev.arrNext[i] = next;
	next.arrPrev[i] = prev;
      }
    }

    NIORecvRequest check(int context, UUID dstUUID, UUID srcUUID, int tag) {
      Key key = new Key(context, dstUUID, srcUUID, tag);
      return get(key);
    }

    void add(NIORecvRequest request) {

      /*
       * Checking if Hybrid device is enabled then source and destination of
       * message in key should be Hybrid source and destination, not those of
       * Network souruce and destination. 50 context is reserved for finish
       * method message so that message should always be treat as NIO message,
       * as it is used to finshi NIO device.
       */

      if (isHybrid && (request.context != 50)) {
	request.arrKeys = new NIODevice.Key[] {
	    new NIODevice.Key(request.context, request.dstHybUUID,
		request.srcHybUUID, request.tag),
	    new NIODevice.Key(request.context, request.dstHybUUID,
		request.srcHybUUID, xdev.Device.ANY_TAG),
	    new NIODevice.Key(request.context, request.dstHybUUID,
		xdev.Device.ANY_SRC.uuid(), request.tag),
	    new NIODevice.Key(request.context, request.dstHybUUID,
		xdev.Device.ANY_SRC.uuid(), xdev.Device.ANY_TAG) };
	// System.out.println(" ArrQue.add() =>  HYB Src:"+request.srcHybUUID+" HYB Dst:"+request.dstHybUUID
	// +
	// " tag:"+request.tag+" Context:"+request.context+" isHybrid:"+isHybrid)
	// ;
      } else {

	request.arrKeys = new NIODevice.Key[] {
	    new NIODevice.Key(request.context, request.dstUUID,
		request.srcUUID, request.tag),
	    new NIODevice.Key(request.context, request.dstUUID,
		request.srcUUID, xdev.Device.ANY_TAG),
	    new NIODevice.Key(request.context, request.dstUUID,
		xdev.Device.ANY_SRC.uuid(), request.tag),
	    new NIODevice.Key(request.context, request.dstUUID,
		xdev.Device.ANY_SRC.uuid(), xdev.Device.ANY_TAG) };

      }
      for (int i = 0; i < request.arrKeys.length; i++) {
	add(i, request.arrKeys[i], request);
      }
    }

  }

  class Key {
    /*
     * key is updated to have four tuples, as in Hybrid communication source,
     * destination, tag and context are minimum requirements to uniqely identify
     * the message. So key mechanism of NIODevice is updated to meet
     * requirements. All addition and removal from the message queues is based
     * on the 4-tuple key
     */

    private int context, tag;

    private UUID srcUUID;
    private UUID dstUUID;

    Key(int context, UUID dstUUID, UUID srcUUID, int tag) {
      this.context = context;
      this.dstUUID = dstUUID;
      this.srcUUID = srcUUID;
      this.tag = tag;
    }

    public int hashCode() {
      return tag + context * 5 + srcUUID.hashCode() * 17 + dstUUID.hashCode()
	  * 19;
    }

    public boolean equals(Object obj) {

      if (obj instanceof Key) {
	Key other = (Key) obj;
	return (other.context == context) && (dstUUID.equals(other.dstUUID))
	    && (srcUUID.equals(other.srcUUID)) && (other.tag == tag);
      }

      return false;

    }
  }

  ArrvQueue arrQue = new ArrvQueue();

  /*
   * Name of machine where this xdev process is running
   */
  String localHostName = null;
  /*
   * isHybrid switch is useful for using NIODevice within Hybrid Device. It is
   * set to true at the end of Hybrid Device init mehtod. isHybird switch is
   * checked before adding and removing any message in the Arrive Queueu and
   * Recv Queue. If isHybrid switch is ON then in the message key Hybrid Source
   * and Destination are used else NIO Source and destination are used.
   */
  public static boolean isHybrid = false;

  /* Server Socket Channel */
  ServerSocketChannel writableServerChannel = null;

  ServerSocketChannel readableServerChannel = null;

  /*
   * Initially CTRL_MSG_LENGTH was set to 45, to use NIODevice within Hybird
   * device its CTRL_MSG_LENGTH is increased by 32, so it is 77 now. Hybrid
   * Source and Destination is added in the message that means two UUID values
   * so 32 bytes are required. NIODevice works fine on CTRL_MSG_LENGTH=77 as
   * well, first 45 bytes will be used and rest of the 32 bytes will be null.
   */
  public static int CTRL_MSG_LENGTH = 77;

  ByteBuffer rcb = ByteBuffer.allocate(CTRL_MSG_LENGTH);

  ByteBuffer rendezBuffer = ByteBuffer.allocate(8);

  ByteBuffer rendez_send_buffer = ByteBuffer.allocate(17);

  ByteBuffer wcb = ByteBuffer.allocate(49);

  ByteBuffer e_wcb = ByteBuffer.allocate(49);

  static ByteBuffer _wcb = ByteBuffer.allocate(21); // eendezCtrlMsgR2S ...

  ByteBuffer s_wcb = ByteBuffer.allocate(20); // rendezCtrlMsgR2S ...

  /* Threads for two selectors */
  Thread selectorThreadStarter = null;

  int psl = 0, nprocs = 0, rank = 0, size = 0, my_server_port = 0;

  ProcessID[] pids = null;

  ProcessID id = null;

  /*
   * This integer is used as the header to send initial control messages
   */
  private final int INIT_MSG_HEADER_DATA_CHANNEL = -21;

  private final int INIT_MSG_HEADER_CTRL_CHANNEL = -20;

  private final int RENDEZ_CTRL_MSG_LENGTH = 4;

  private final int ACK_LENGTH = 17;

  // private final int CTRL_MSG_LENGTH = 45;

  int SEND_OVERHEAD = CTRL_MSG_LENGTH + 4;

  int RECV_OVERHEAD = 0;

  private final int STD_COMM_MODE = 3;

  private final int SYNC_COMM_MODE = 2;

  private final boolean NO_ACK_RECEIVED = false;

  private final boolean REQ_NOT_COMPLETED = false;

  private final boolean RECV_POSTED = true;

  private final int READY_TO_SEND = -24;

  private static final int ACK_HEADER = -23;

  private final int RENDEZ_HEADER = -22;

  private final int SEND_ACK_TO_SENDER = -80;

  private final int RECV_IN_USER_MEMORY = -81;

  private final int RECV_IN_DEV_MEMORY = -82;

  private final int MORE_TO_WRITE = -83;

  private final int MORE_TO_READ = -84;

  private String mpjHomeDir = null;

  SocketChannel msgReceivedFrom; // what is this doing here?

  boolean finished = false;
  DataOutputStream out = null;
  DataInputStream in = null;
  Socket clientSock = null;
  int wport;
  int rport;
  String WRAPPER_INFO;
  String serverName;
  int serverPort;
  public NIODevice() {
    // this.deviceName = "niodev";
  }

  /**
   * Initializes niodev.
   * 
   * @param args
   *          Arguments to NIODevice.
   * @return ProcessID[] An array of ProcessIDs.
   */

  private int bindPort(ServerSocketChannel sock){
    int minPort = 25000;
    int maxPort = 40000;
    int selectedPort;

    Random rand = new Random();
    /* The loop generates a random port number, opens a socket on
     * the generated port
     */

    while(true){
      selectedPort = (rand.nextInt((maxPort - minPort) + 1) + minPort);

      try {
        sock.socket().bind(new InetSocketAddress(selectedPort));
      }
      catch (IOException e) {
        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.info("[NIODev.java]:"+localHostName+"-"+selectedPort+
                       "]Port already in use. Checking for a new port..");
        }
        continue;
      }
      break;
    }
    return selectedPort;
  }


  public ProcessID[] init(String args[]) throws XDevException {

    List<String> nodes = new ArrayList<String>();
    List<Integer> rPorts = new ArrayList<Integer>();
    List<Integer> wPorts = new ArrayList<Integer>();
    List<Integer> ranks = new ArrayList<Integer>();

    /*
     * 
     * The init method reads names/ports/ranks from an input string. It finds 
     * its own entry in the arguments (by comparing ranks), and creates a server
     * socket at the port specified for that entry. Also, it creates another
     * server socket at (portspecified+1). It connects to server sockets (not
     * one, two server sockets) of processes with rank higher than its own.
     * 
     * At the end of this process, each process is connected to every other
     * process with two socketChannels. The reason for two channels is that
     * every process has writable and reable channel. The writable channel is in
     * blocking mode, whereas, the readable channel is in non-blocking mode. In
     * terms of datastructures, 'writableChannels' (Vector) contains all
     * writable channels, and 'readableChannels' (Vector) contains all readable
     * channels for every process. The next step is that each process send its
     * own rank, ProcessID to all the other processes. At the end of this, each
     * process knows about all the peers and have ProcessID (key), SocketChannel
     * (val) in 'worldWritableTable' and 'worldReadableTable'.
     * 
     * As the name suggests, worldWritableTable is used for writing messages
     * into channels, and worldReadableTable is used for receiving. The
     * selector-thread would generate events for worldReadableTable
     * SocketChannels whereas, the ones (SocketChannels) in worldWritableTable
     * have nothing to do with selector thread as they are in blocking mode.
     */
    //System.out.println("NIODEV SOCK:"+cl.getInetAddress());
    if (args.length < 3) {

      throw new XDevException("Usage: "
	  + "java NIODevice <myrank> <conf_file_arguments> <device_name>"
	  + "conf_file can be, ../conf/xdev.conf <Local>"
	  + "OR http://holly.dsg.port.ac.uk:15000/xdev.conf <Remote>");

    }

    rank = Integer.parseInt(args[0]);
    UUID myuuid = UUID.randomUUID();
    id = new ProcessID(myuuid);
    Map<String, String> map = System.getenv();

    try {

      localaddr = InetAddress.getLocalHost();
      localHostName = localaddr.getHostName();

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.info("--init method of niodev is called--");
	logger.info("Address: " + localaddr);
	logger.info("Name :" + localHostName);
	logger.info("rank :" + rank);
      }

    }
    catch (UnknownHostException unkhe) {
      throw new XDevException(unkhe);
    }

    /*
     * String arguments are being parsed here one by one to obtain
     * the values of (1) number of processes, (2) protocol switch
     * limit, (3) IPs of all nodes, (4) read and write ports for
     * all nodes and (5) ranks for all nodes.
     */
    
    StringTokenizer arguments = new StringTokenizer(args[1],";");

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("Orignal string: "+ args[1]);
    }
    while(arguments.hasMoreTokens()) {
      String token = arguments.nextToken();
      if(token.equals("#Number of Processes")) {
        nprocs = new Integer(arguments.nextToken()).intValue();
      }
      else if(token.equals("#Protocol Switch Limit")) {
        psl = new Integer(arguments.nextToken()).intValue();
      }
      else if(token.equals("#Server Name")) {
        serverName = arguments.nextToken();
      }
      else if(token.equals("#Server Port")) {
        serverPort = new Integer(arguments.nextToken()).intValue();
      }
    } 
    
    
    /* Old code for reading information from mpjdev.conf commented out.
    ConfigReader reader = null;

    try {
      reader = new ConfigReader(args[1]);
      nprocs = (new Integer(reader.readNoOfProc())).intValue();
      psl = (new Integer(reader.readIntAsString())).intValue();
      if (psl < 12) {
	logger.debug("lowest possible psl is 12 bytes");
	psl = 12;
      }
    }
    catch (Exception config_error) {
      throw new XDevException(config_error);
    }
    */

    pids = new ProcessID[nprocs];
    int count = 0;

    /* This segment of the code converts the dynamic lists into
     * fixed sized arrays. This is done so because rest of the code
     * was working with arrays, and lists were a modification added
     * by me - Farrukh.
     */

    String[] nodeList = new String[nprocs];
    int[] rPortList = new int[nprocs];
    int[] wPortList = new int[nprocs];
    int[] rankList = new int[nprocs];
    

    /* Old code for reading information from mpjdev.conf commented out */
    /*
    while (count < nprocs) {
      String line = null;

      try {
	line = reader.readLine();
      }
      catch (IOException ioe) {
	throw new XDevException(ioe);
      }

      if (line == null || line.equals("") || line.equals("#")) {
	continue;
      }
      line = line.trim();
      StringTokenizer tokenizer = new StringTokenizer(line, "@");
      nodeList[count] = tokenizer.nextToken();
      wPortList[count] = (new Integer(tokenizer.nextToken())).intValue();
      rPortList[count] = (new Integer(tokenizer.nextToken())).intValue();
      rankList[count] = (new Integer(tokenizer.nextToken())).intValue();
      count++;
    }
    reader.close();
    *********/

    /* Open the selector */
    try {
      selector = Selector.open();
    }
    catch (IOException ioe) {
      throw new XDevException(ioe);
    }

    /* Create server socket */
    SocketChannel[] rChannels = new SocketChannel[nodeList.length - 1];
    /* Create control server socket */
    SocketChannel[] wChannels = new SocketChannel[nodeList.length - 1];


    
      try {
	writableServerChannel = ServerSocketChannel.open();
	writableServerChannel.configureBlocking(false);
        wport=bindPort(writableServerChannel);

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("created writableServerChannel on port "
	      + wport);
	}
      
	writableServerChannel.register(selector, SelectionKey.OP_ACCEPT);

	my_server_port = wport;

	readableServerChannel = ServerSocketChannel.open();
	readableServerChannel.configureBlocking(false);
        rport = bindPort(readableServerChannel);

	readableServerChannel.register(selector, SelectionKey.OP_ACCEPT);

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("created readableServerChannel on port "
	      + rport);
	}

      }
      catch (Exception ioe)  {
        ioe.printStackTrace();
      }
	

     try{
       logger.debug("Connecting to :ServerName "+
                     serverName+" ServerPort "+serverPort);
       
       clientSock = new Socket(serverName,serverPort);   
       
       logger.debug("Socket Connected "+clientSock.getInetAddress());
       
       out = new DataOutputStream(clientSock.getOutputStream());
       in = new DataInputStream(clientSock.getInputStream());
       
       logger.debug("Sending Write Port "+wport);
       logger.debug("Sending Read Port"+rport);
       
       out.writeUTF("Sending Info");
       out.flush();
       out.writeInt(wport);
       out.flush();
       out.writeInt(rport);
       out.flush();
       out.writeInt(rank);
       out.flush();

       WRAPPER_INFO = in.readUTF();
       clientSock.close();
     }
     catch (IOException e){
       e.printStackTrace();
     } 

    arguments = new StringTokenizer(WRAPPER_INFO,";");
    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("Peer Processes Info " + WRAPPER_INFO);
    }
    String token = arguments.nextToken();

    if(token.equals("#Peer Information")) {
        token = arguments.nextToken();
        for(int i = 0; i<nprocs; i++) {
          StringTokenizer peer= new StringTokenizer(token, "@");
          String peerToken = peer.nextToken();

          nodes.add(peerToken);

          peerToken = peer.nextToken();
          rPorts.add(Integer.parseInt(peerToken));

          peerToken = peer.nextToken();
          wPorts.add(Integer.parseInt(peerToken));

          peerToken = peer.nextToken();
          ranks.add(Integer.parseInt(peerToken));

          if(arguments.hasMoreTokens())
            token = arguments.nextToken();
        }
      }
    
    for(int i=0; i<nprocs; i++) {
      nodeList[i] = nodes.get(i);
      rPortList[i] = rPorts.get(i);
      wPortList[i] = wPorts.get(i);
      rankList[i] = ranks.get(i);
    }
    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {

      logger.debug("Nodes list");
      for(int i=0;i< nprocs;i++){
        logger.debug("node["+i+"] "+nodeList[i]);
      }

      logger.debug("Read Ports List");
      for(int i=0;i< nprocs;i++){
        logger.debug("rPortList["+i+"] "+rPortList[i]);
      }

      logger.debug("Write Ports List");
      for(int i=0;i< nprocs;i++){
        logger.debug("wPortList["+i+"] "+wPortList[i]);
      }

      logger.debug("Ranks List");
      for(int i=0;i< nprocs;i++){
        logger.debug("ranks["+i+"] "+rankList[i]);
      }
    }
    /* This is connection-code for data-channels. */
    boolean connected = false;
    int temp = 0, index = 0;
    /*
     * This while loop is connecting to server sockets of other peers. If there
     * are 4 processes, process 0 will not connect to any process, process 1
     * will connect to process 0, process 2 will connect to pro 0&1, and process
     * 3 will connect to pro 0&1&2
     */

    while (temp < nprocs - 1) {

      if (rank == rankList[temp]) {
	temp++;
	continue;
      }

      if (rankList[temp] < rank) {

	while (!connected) {

	  try {
	    rChannels[index] = SocketChannel.open();
	    rChannels[index].configureBlocking(true);
	  }
	  catch (Exception e) {
	    throw new XDevException(e);
	  }

	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("Connecting to " + nodeList[temp] + "@"
		+ wPortList[temp]);
	  }

	  try {
	    connected = rChannels[index].connect(new InetSocketAddress(
		nodeList[temp], wPortList[temp]));
	  }
	  catch (AlreadyConnectedException ace) {
	    throw new XDevException(ace);
	  }
	  catch (ConnectionPendingException cpe) {
	    throw new XDevException(cpe);
	  }
	  catch (ClosedChannelException cce) {
	    throw new XDevException(cce);
	  }
	  catch (UnresolvedAddressException uae) {
	    throw new XDevException(uae);
	  }
	  catch (UnsupportedAddressTypeException uate) {
	    throw new XDevException(uate);
	  }
	  catch (SecurityException se) {
	    throw new XDevException(se);
	  }
	  catch (IOException ioe) {
	    // this is continuing coz process 1 alwayz connect to
	    // process 0
	    // server socket. If process 0 is not up, then this
	    // exception
	    connected = false;

	    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	      logger.debug("connecting error ->" + ioe.getMessage());
	    }

	    continue;
	  }

	  try {
	    rChannels[index].configureBlocking(false);
	    rChannels[index].register(selector, SelectionKey.OP_READ);
	    rChannels[index].socket().setTcpNoDelay(true);
	    // these are useful if running MPJ on gigabit ethernet.
	    rChannels[index].socket().setSendBufferSize(524288);
	    rChannels[index].socket().setReceiveBufferSize(524288);
	  }
	  catch (Exception e) {
	    throw new XDevException(e);
	  }

	  synchronized (readableChannels) {
	    readableChannels.add(rChannels[index]);
	    if (readableChannels.size() == nprocs - 1) {
	      readableChannels.notify();
	    }
	  } // end synch

	  connected = true;
	} // end while

	connected = false;
      } // end if

      index++;
      temp++;

    } // end while

    /* This is connection-code for control-channels. */
    connected = false;
    temp = 0;
    index = 0;

    /*
     * This while loop is connecting to server sockets of other peers. If there
     * are 4 processes, process 0 will not connect to any process, process 1
     * will connect to process 0, process 2 will connect to pro 0&1, and process
     * 3 will connect to pro 0&1&2
     */

    while (temp < nprocs - 1) {

      if (rank == rankList[temp]) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("Dont connect to itself, so contine;");
	}

	temp++;
	continue;
      }

      if (rankList[temp] < rank) {

	while (!connected) {

	  try {
	    wChannels[index] = SocketChannel.open();
	    wChannels[index].configureBlocking(true);

	    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	      logger.debug("Connecting to " + nodeList[temp] + "@"
		  + rPortList[temp]);
	    }

	  }
	  catch (Exception e) {
	    throw new XDevException(e);
	  }

	  try {

	    connected = wChannels[index].connect(new InetSocketAddress(
		nodeList[temp], rPortList[temp]));

	  }
	  catch (AlreadyConnectedException ace) {
	    throw new XDevException(ace);
	  }
	  catch (ConnectionPendingException cpe) {
	    throw new XDevException(cpe);
	  }
	  catch (ClosedChannelException cce) {
	    throw new XDevException(cce);
	  }
	  catch (UnresolvedAddressException uae) {
	    throw new XDevException(uae);
	  }
	  catch (UnsupportedAddressTypeException uate) {
	    throw new XDevException(uate);
	  }
	  catch (SecurityException se) {
	    throw new XDevException(se);
	  }
	  catch (IOException ioe) {
	    // this is continuing coz process 1 alwayz connect to
	    // process 0
	    // server socket. If process 0 is not up, then this
	    // exception
	    connected = false;

	    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	      logger.debug("connecting error ->" + ioe.getMessage());
	    }

	    continue;
	  }

	  try {
	    wChannels[index].configureBlocking(true);
	    wChannels[index].socket().setTcpNoDelay(true);
	    // these are useful if running MPJ on gigabit ethernet
	    wChannels[index].socket().setSendBufferSize(524288);
	    wChannels[index].socket().setReceiveBufferSize(524288);
	  }
	  catch (Exception e) {
	    throw new XDevException(e);
	  }

	  synchronized (writableChannels) {

	    writableChannels.add(wChannels[index]);

	    if (writableChannels.size() == nprocs - 1) {
	      writableChannels.notify();
	    }

	  } // end synch

	  connected = true;
	} // end while

	connected = false;
      } // end if

      index++;
      temp++;

    } // end while

    index = rank;
    root = 0;
    procTree = new ProcTree();
    extent = nprocs;
    places = ProcTree.PROCTREE_A * index;

    for (int i = 1; i <= ProcTree.PROCTREE_A; i++) {
      ++places;
      int ch = (ProcTree.PROCTREE_A * index) + i + root;
      ch %= extent;

      if (places < extent) {
	procTree.child[i - 1] = ch;
	procTree.numChildren++;
      }
    }

    if (index == root) {
      procTree.isRoot = true;
    } else {
      procTree.isRoot = false;
      int pr = (index - 1) / ProcTree.PROCTREE_A;
      procTree.parent = pr;
    }

    procTree.root = root;

    selectorThreadStarter = new Thread(selectorThread);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Starting the selector thread ");
    }

    selectorThreadStarter.start();

    // addShutdownHook();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("testing if all peers are connected?");
    }

    count = 0;

    /*
     * doAccept() and/or while loop above adds SocketChannels to
     * writableChannels so access to writableChannels should be synchronized.
     */
    synchronized (writableChannels) {

      if (writableChannels.size() != nprocs - 1) {
	try {
	  writableChannels.wait();
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      }

    } // end sync.

    /* This is for control-channels. */
    synchronized (readableChannels) {

      if (readableChannels.size() != nprocs - 1) {
	try {
	  readableChannels.wait();
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      }

    } // end sync.

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info(" Yes all nodes are connected to each other ");
    }

    /*
     * At this point, all-to-all connectivity has been acheived. Each process
     * has all SocketChannels (for peers) in writableChannels (Vector object).
     * Now each process will send rank(this rank is the one read from
     * config-file), msb (most significant bits), lsb(least significant bits) to
     * all the other processes. After receiving this info, all processes will
     * have constructed worldTable (Hashtable object), which contains <k,v>,
     * where k=UUID of a process, and v=SocketChannel object. This worldTable is
     * used extensively throughout the niodev.
     */

    SocketChannel socketChannel = null;
    ByteBuffer initMsgBuffer = ByteBuffer.allocate(24);
    long msb = myuuid.getMostSignificantBits();
    long lsb = myuuid.getLeastSignificantBits();
    initMsgBuffer.putInt(INIT_MSG_HEADER_DATA_CHANNEL);
    initMsgBuffer.putInt(rank);
    initMsgBuffer.putLong(msb);
    initMsgBuffer.putLong(lsb);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("rank<" + rank
	  + ">is sending its rank,msb,lsb, to all data channels");
    }

    /* Writing stuff into writable-channels */
    for (int i = 0; i < writableChannels.size(); i++) {
      socketChannel = writableChannels.get(i);
      initMsgBuffer.flip();

      /* Do we need to iterate here? */
      while (initMsgBuffer.hasRemaining()) {
	try {
	  if (socketChannel.write(initMsgBuffer) == -1) {
	    throw new XDevException(new ClosedChannelException());
	  }
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      } // end while.
      _wcb.clear();
    } // end for.

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("rank<" + rank + "> testing if everything is received? ");
    }

    /*
     * worldTable is accessed from doBarrierRead or here, so their access should
     * be synchronized
     */
    synchronized (worldReadableTable) {
      if ((worldReadableTable.size() != nprocs - 1)) {
	try {
	  worldReadableTable.wait();
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      }
    } // end sync

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("worldReadableTable is filled ");
    }

    /* Writing stuff into readable-channels */
    for (int i = 0; i < readableChannels.size(); i++) {
      socketChannel = readableChannels.get(i);
      initMsgBuffer.flip();

      /* Do we need to iterate here? */
      while (initMsgBuffer.hasRemaining()) {
	try {
	  if (socketChannel.write(initMsgBuffer) == -1) {
	    throw new XDevException(new ClosedChannelException());
	  }
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      } // end while.
    } // end for.

    /*
     * Do blocking-reads, is this correct? will work but wont scale i think.
     */
    for (int i = 0; i < writableChannels.size(); i++) {
      socketChannel = writableChannels.get(i);
      try {
	doBarrierRead(socketChannel, worldWritableTable, true);
      }
      catch (XDevException xde) {
	throw xde;
      }
    }

    synchronized (worldWritableTable) {
      if ((worldWritableTable.size() != nprocs - 1)) {
	try {
	  worldWritableTable.wait();
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      }
    } // end sync

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("worldWritable is filled ");
    }

    // writableServerChannel.close();
    // readableServerChannel.close();
    pids[rank] = id;

    for (int k = 0; k < writableChannels.size(); k++) {
      writeLockTable.put(writableChannels.elementAt(k), new CustomSemaphore(1));
    }

    try {
      writableServerChannel.close();
      readableServerChannel.close();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    return pids;

  } // end init

  /**
   * Returns the id of this process.
   * 
   * @return ProcessID An object containing UUID of the process
   */
  public ProcessID id() {
    return id;
  }

  public int getSendOverhead() {
    return SEND_OVERHEAD;
  }

  public int getRecvOverhead() {
    return RECV_OVERHEAD;
  }

  /**
   * Non-Blocking probe method.
   * 
   * @param srcID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */
  public mpjdev.Status iprobe(ProcessID srcID, int tag, int context)
      throws XDevException {

    UUID dstUUID = id.uuid(), srcUUID = srcID.uuid();
    mpjdev.Status status = null;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---iprobe---");
      logger.debug("srcUUID:" + srcUUID + "tag:" + tag);
      logger.debug("id.uuid():" + id.uuid());
      // logger.debug("srcID.rank():" + srcID.rank());
      logger.debug("ANY_SOURCE:" + ANY_SOURCE);
      logger.debug("Looking whether this req has been posted or not");
    }

    try {
      sem.acquire();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    NIORecvRequest request = arrQue.check(context, dstUUID, srcUUID, tag);

    if (request != null) {
      // now this is a tricky one ...
      status = new mpjdev.Status(request.srcUUID, // srcID.rank(),
	  request.tag, -1, request.type, request.numEls);
    }
    sem.signal();
    return status;
  }

  /**
   * Non-Blocking iprobeAndFetch method.
   * 
   * @param srcID
   * @param dstID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */
  public mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID,
      int tag, int context, mpjbuf.Buffer buf) throws XDevException {

    UUID dstUUID = dstID.uuid(), srcUUID = srcID.uuid();

    mpjdev.Status status = null;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---iprobe---");
      logger.debug("srcUUID:" + srcUUID + "tag:" + tag);
      logger.debug("id.uuid():" + id.uuid());
      logger.debug("ANY_SOURCE:" + ANY_SOURCE);
      logger.debug("Looking whether this req has been posted or not");
    }
    try {
      sem.acquire();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    NIORecvRequest request = arrQue.remForIprobeAndFetch(context, dstUUID,
	srcUUID, tag);

    if (request != null) {
      status = new mpjdev.Status(request.srcHybUUID, request.tag, -1,
	  request.type, request.numEls);

      request.status = status;

      if (((request.sBufSize + request.dBufSize) <= psl)
	  && request.commMode == STD_COMM_MODE) {
	sem.signal();
	if (request.sBufSize > 0) {
	  request.staticBuffer = ((NIOBuffer) buf.getStaticBuffer())
	      .getBuffer();
	  ByteBuffer eagerBuffer = ((NIOBuffer) request.eagerBuffer)
	      .getBuffer();
	  request.staticBuffer.position(0);
	  request.staticBuffer.limit(request.sBufSize);
	  eagerBuffer.limit(request.sBufSize);
	  eagerBuffer.position(0);
	  request.staticBuffer.put(eagerBuffer);
	  BufferFactory.destroy(request.eagerBuffer);
	} // end if

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("setting the buf size " + request.sBufSize);
	}

	buf.setSize(request.sBufSize);

	if (request.dBufSize > 0) {
	  buf.setDynamicBuffer(request.dynamicBuffer);
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("removed ");
	}
	return status;
      } else if ((((request.sBufSize + request.dBufSize) > psl) && request.commMode == STD_COMM_MODE)
	  || (request.commMode == SYNC_COMM_MODE)) {
	sem.signal();
	mpjdev.Request req = irecv(buf, new ProcessID(request.srcUUID), tag,
	    context, status);

	return req.iwait();
      }
    }
    // System.out.println
    // (" niodev: incomplete Request, releasing lock in Fetch ");
    sem.signal();
    return status;

  }

  /**
   * Non-Blocking overloaded probe method.
   * 
   * @param srcID
   * @param dstID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */

  public mpjdev.Status iprobe(ProcessID srcID, ProcessID dstID, int tag,
      int context) throws XDevException {

    UUID dstUUID = dstID.uuid(), srcUUID = srcID.uuid();

    mpjdev.Status status = null;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---iprobe---");
      logger.debug("srcUUID:" + srcUUID + "tag:" + tag);
      logger.debug("id.uuid():" + id.uuid());
      logger.debug("ANY_SOURCE:" + ANY_SOURCE);
      logger.debug("Looking whether this req has been posted or not");
    }
    try {
      sem.acquire();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    NIORecvRequest request = arrQue.check(context, dstUUID, srcUUID, tag);

    if (request != null) {
      // now this is a tricky one ...
      status = new mpjdev.Status(request.srcHybUUID, // srcID.rank(),
	  request.tag, -1, request.type, request.numEls);
    }

    sem.signal();

    return status;
  }

  /**
   * Blocking probe method
   * 
   * @param srcID
   *          The sourceID of the sender
   * @param tag
   *          The tag of the message
   * @param context
   *          The integer specifying the context
   * @return mpjdev.Status The status object
   */
  public mpjdev.Status probe(ProcessID srcID, int tag, int context)
      throws XDevException {

    mpjdev.Status status = null;
    boolean comp = false;

    while (!comp) {
      status = this.iprobe(srcID, tag, context);
      if (status != null) {
	comp = true;
      }
    }

    return status;
  }

  private synchronized int sendCounter() {
    return ++sendCounter;
  }

  private synchronized int recvCounter() {
    return ++recvCounter;
  }

  private synchronized int hashCode(int tag, int context, int srcHash,
      int dstHash) {
    return tag + context * 5 + dstHash * 11 + srcHash * 17 + dstHash * 19;
  }

  /**
   * Non-blocking send method
   * 
   * @param buf
   *          The mpjbuf.Buffer object containing the data.
   * @param dstID
   *          ProcessID of the destination process.
   * @param tag
   *          The unique identifier of the message.
   * @param context
   *          An integer providing "safe universe" for messages.
   * @return mpjdev.Request The Request object, which is later used to check the
   *         status of the message.
   */
  public mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID dstID, int tag,
      int context) throws XDevException {

    UUID dstUUID = dstID.uuid();

    UUID srcUUID = id.uuid();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---isend---<" + tag + ">");
      logger.debug("sender :" + id.uuid());
      logger.debug("receiver :" + dstUUID);
      logger.debug("tag :" + tag);
      // logger.debug("staticBufferSize :" + req.sBufSize );
      // logger.debug("dynamicBufferSize :" + req.dBufSize );
      // logger.debug("req.sendCounter :" + req.sendCounter );
    }

    if (dstUUID.equals(srcUUID)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.info("sender and receiver are same process ");
      }

      try {
	sem.acquire();
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      NIORecvRequest recvRequest = recvQueue
	  .rem(context, dstUUID, srcUUID, tag);

      NIOSendRequest sendRequest = new NIOSendRequest(tag, // NO_ACK_RECEIVED,
	  id(), dstID, buf, context, STD_COMM_MODE, -1);

      if (recvRequest != null) {

	sem.signal();
	recvRequest.type = sendRequest.type;
	recvRequest.numEls = sendRequest.numEls;
	recvRequest.buffer.setSize(sendRequest.sBufSize);
	recvRequest.sBufSize = sendRequest.sBufSize;
	recvRequest.dBufSize = sendRequest.dBufSize;

	/* copy the dynamic portion */
	recvRequest.buffer.setDynamicBuffer(sendRequest.dynamicBuffer);

	/* copy the static portion */
	recvRequest.staticBuffer.limit(recvRequest.sBufSize);
	recvRequest.staticBuffer.position(0);
	sendRequest.staticBuffer.limit(recvRequest.sBufSize
	    + sendRequest.bufoffset);
	sendRequest.staticBuffer.position(sendRequest.bufoffset);
	recvRequest.staticBuffer.put(sendRequest.staticBuffer);

	recvRequest.staticBuffer.flip();

	/* comms complete */
	// completedList.add(sendRequest);
	// completedList.add(recvRequest);
	recvRequest.setCompleted(true);
	sendRequest.setCompleted(true);

	return sendRequest;
      } else {

	recvRequest = new NIORecvRequest(id.uuid(), tag, false, context,
	    sendRequest.sBufSize, sendRequest.dBufSize, sendRequest.commMode,
	    null // (socketChannel)
	    , sendRequest.numEls, sendRequest.type, -1, -1, srcUUID);

	recvRequest.sendRequest = sendRequest;
	arrQue.add(recvRequest);
	sem.signal();
	return sendRequest;
      }
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("isend with remote process connections");
    }

    NIOSendRequest req = new NIOSendRequest(tag, id(), dstID, buf, context,
	STD_COMM_MODE, sendCounter());

    SocketChannel channel = worldWritableTable.get(dstUUID);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("channel :" + channel);
    }

    if ((req.sBufSize + req.dBufSize) <= psl) {

      /* Eager-Send Procotol */

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("get writeLock for this channel");
      }

      CustomSemaphore wLock = writeLockTable.get(channel);

      try {
	wLock.acquire();
	eagerSend(req, channel);
	wLock.signal();
	// completedList.add( req );
	req.notifyMe();
      }
      catch (Exception e) {
	throw new XDevException(e);
      }

    }
    /* Rendezvous Protocol */
    else if ((req.sBufSize + req.dBufSize) > psl) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("rendezvous protocol.");
	logger.debug(" get send-comms set lock ");
      }

      try {
	sLock.acquire();
      }
      catch (Exception e) {
      }

      sendMap.put(new Integer(req.sendCounter), req);
      sLock.signal();
      CustomSemaphore wLock = writeLockTable.get(channel);

      try {
	wLock.acquire();
	rendezCtrlMsgSend(req, channel);
	wLock.signal();
      }
      catch (Exception e) {
	throw new XDevException(e);
      }

    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---isend ends---<" + tag + ">");
    }

    return req;

  } // end isend.

  /**
   * Non-blocking synchronous send.
   */
  public mpjdev.Request issend(mpjbuf.Buffer buf, ProcessID dstID, int tag,
      int context) throws XDevException {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---issend---");
    }

    NIOSendRequest req = null;
    SocketChannel channel = null;
    UUID dstUUID = dstID.uuid();
    UUID srcUUID = id.uuid();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---isend---<" + tag + ">");
      logger.debug("sender :" + id.uuid());
      logger.debug("receiver :" + dstUUID);
      logger.debug("tag :" + tag);
      // logger.debug("staticBufferSize :" + req.sBufSize );
      // logger.debug("dynamicBufferSize :" + req.dBufSize );
      // logger.debug("req.sendCounter :" + req.sendCounter );
    }

    if (dstUUID.equals(srcUUID)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.info("sender and receiver are same process ");
      }

      try {
	sem.acquire();
      }
      catch (Exception e) {
      }

      NIORecvRequest recvRequest = recvQueue
	  .rem(context, dstUUID, srcUUID, tag);

      NIOSendRequest sendRequest = new NIOSendRequest(tag, id(), dstID, buf,
	  context, SYNC_COMM_MODE, -1);

      if (recvRequest != null) {

	sem.signal();
	recvRequest.type = sendRequest.type;
	recvRequest.numEls = sendRequest.numEls;
	recvRequest.buffer.setSize(sendRequest.sBufSize);
	recvRequest.sBufSize = sendRequest.sBufSize;
	recvRequest.dBufSize = sendRequest.dBufSize;

	/* copy the dynamic portion */
	recvRequest.buffer.setDynamicBuffer(sendRequest.dynamicBuffer);

	/* copy the static portion */
	recvRequest.staticBuffer.limit(recvRequest.sBufSize);
	recvRequest.staticBuffer.position(0);
	sendRequest.staticBuffer.limit(recvRequest.sBufSize
	    + sendRequest.bufoffset);
	sendRequest.staticBuffer.position(sendRequest.bufoffset);
	recvRequest.staticBuffer.put(sendRequest.staticBuffer);

	recvRequest.staticBuffer.flip();

	/* comms complete */
	// completedList.add(sendRequest);
	// completedList.add(recvRequest);
	recvRequest.setCompleted(true);
	sendRequest.setCompleted(true);

	return sendRequest;
      } else {

	recvRequest = new NIORecvRequest(id.uuid(), tag, false, context,
	    sendRequest.sBufSize, sendRequest.dBufSize, sendRequest.commMode,
	    null // (socketChannel)
	    , sendRequest.numEls, sendRequest.type, -1, -1, srcUUID);

	recvRequest.sendRequest = sendRequest;
	arrQue.add(recvRequest);
	sem.signal();
	return sendRequest;
      }
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("isend with remote process connections");
    }

    req = new NIOSendRequest(tag, id(), dstID, buf, context, SYNC_COMM_MODE,
	sendCounter());

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("sender :" + id.uuid());
      logger.debug("receiver :" + dstUUID);
      logger.debug("tag :" + tag);
      logger.debug("staticBufferSize :" + req.sBufSize);
      logger.debug("dynamicBufferSize :" + req.dBufSize);
      logger.debug("buffset :" + 0);
      logger.debug("Rendezous(isend), calling rendezCtrlMsgSend");
    }

    channel = worldWritableTable.get(dstUUID);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("channel (can never be null) " + channel);
    }

    try {
      sLock.acquire();
    }
    catch (Exception e) {
    }

    sendMap.put(new Integer(req.sendCounter), req);
    sLock.signal();
    CustomSemaphore wLock = writeLockTable.get(channel);

    try {
      wLock.acquire();
      rendezCtrlMsgSend(req, channel);
      wLock.signal();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---issend ends---<" + tag + ">");
    }

    return req;

  } // end issend

  /**
   * Blocking send method.
   * 
   * @param buf
   *          The mpjbuf.Buffer object containing the data.
   * @param dstID
   *          ProcessID of the destination
   * @param tag
   *          The unique identifier of the message
   * @param context
   *          An integer providing "safe universe" for messages.
   * @throws MPJException
   *           If the buffer is null, dest process ID is insane.
   * @throws java.nio.BufferOverflowException
   * @throws ReadOnlyBufferException
   * @throws IOException
   *           If some I/O error occurs
   */
  public void send(mpjbuf.Buffer buf, ProcessID dstID, int tag, int context)
      throws XDevException {

    Request request = isend(buf, dstID, tag, context);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Calling request.iwait() in send method, it may not return");
    }

    request.iwait();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Called request.iwait() in sng this, means it returned");
    }

  }

  /**
   * Blocking synchronous send
   */
  public void ssend(mpjbuf.Buffer buf, ProcessID dstID, int tag, int context)
      throws XDevException {

    Request request = issend(buf, dstID, tag, context);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Calling request.iwait() in send method, it may not return");
    }

    request.iwait();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Called request.iwait()eeing this, means it returned");
    }

  }

  /**
   * This method is used by the sender to send the control message to the
   * receiver
   */
  private void rendezCtrlMsgSend(NIOSendRequest request,
      SocketChannel socketChannel) throws Exception {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---rendezCtrlMsgSend---");
      logger.debug(" request.tag " + request.tag);
    }

    request.staticBuffer.limit(request.bufoffset);
    request.staticBuffer.position(0);

    request.staticBuffer.putInt(READY_TO_SEND);
    request.staticBuffer.putLong(id().uuid().getMostSignificantBits());
    request.staticBuffer.putLong(id().uuid().getLeastSignificantBits());
    request.staticBuffer.putInt(request.tag);
    request.staticBuffer.putInt(request.sBufSize);
    request.staticBuffer.putInt(request.dBufSize);
    request.staticBuffer.putInt(request.commMode);
    request.staticBuffer.putInt(request.context);
    request.staticBuffer.putInt(request.numEls);
    request.staticBuffer.putInt(request.sendCounter);
    request.staticBuffer.put((byte) request.type.getCode());
    /*
     * Checking if Hybrid device is enabled then source and destination of
     * message should be Hybrid source and destination, not those of Network
     * souruce and destination. So UUIDs of Hybrid source and destination should
     * be sent in the Ctrl message.
     */
    if (isHybrid) {
      request.staticBuffer.putLong(request.srcHybUUID.getMostSignificantBits());
      request.staticBuffer
	  .putLong(request.srcHybUUID.getLeastSignificantBits());
      request.staticBuffer.putLong(request.dstHybUUID.getMostSignificantBits());
      request.staticBuffer
	  .putLong(request.dstHybUUID.getLeastSignificantBits());
    }

    request.staticBuffer.limit(request.bufoffset);
    request.staticBuffer.position(0);

    int w = 0;
    int ww = 0;

    while (request.staticBuffer.hasRemaining()) {
      if ((w = socketChannel.write(request.staticBuffer)) == -1) {
	throw new ClosedChannelException();
      }
      ww += w;
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---rendezCtrlMsgSend ENDS ---");
    }
  } // end rendezCtrlMsgSend

  private void eagerSend(NIOSendRequest request, SocketChannel socketChannel)
      throws Exception {

    // long strt = System.nanoTime() ;
    // long stop = 0L, intv = 0L;

    int w = 0, ww = 0;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---eagerSend---");
      logger.debug(" request.bufoffset " + request.bufoffset);
    }

    request.staticBuffer.limit(request.bufoffset);
    request.staticBuffer.position(0);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("sendBuffer " + request.staticBuffer);
    }

    request.staticBuffer.putInt(READY_TO_SEND);
    request.staticBuffer.putLong(id().uuid().getMostSignificantBits());
    request.staticBuffer.putLong(id().uuid().getLeastSignificantBits());
    request.staticBuffer.putInt(request.tag);
    request.staticBuffer.putInt(request.sBufSize);
    request.staticBuffer.putInt(request.dBufSize);
    request.staticBuffer.putInt(request.commMode);
    request.staticBuffer.putInt(request.context);
    request.staticBuffer.putInt(request.numEls);
    request.staticBuffer.putInt(request.sendCounter);
    request.staticBuffer.put((byte) request.type.getCode());

    /*
     * Checking if Hybrid device is enabled then source and destination of
     * message should be Hybrid source and destination, not those of Network
     * souruce and destination. So UUIDs of Hybrid source and destination should
     * be sent in the eager message.
     */
    if (isHybrid) {
      request.staticBuffer.putLong(request.srcHybUUID.getMostSignificantBits());
      request.staticBuffer
	  .putLong(request.srcHybUUID.getLeastSignificantBits());
      request.staticBuffer.putLong(request.dstHybUUID.getMostSignificantBits());
      request.staticBuffer
	  .putLong(request.dstHybUUID.getLeastSignificantBits());
    }

    // stop = System.nanoTime() ;
    // intv = stop - strt ;
    // strt = stop;
    // logger.debug("isend_packing_time_route1 <"+intv/1000);

    /* Writing the static section of the buffer */
    if (request.sBufSize > 0) {

      request.staticBuffer.limit(request.sBufSize + request.bufoffset);
      request.staticBuffer.position(0);
      w = 0;
      ww = 0;

      while (request.staticBuffer.hasRemaining()) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("request.staticBuffer (1)<" + request.staticBuffer
	      + "> w=" + w);
	}

	if ((w = socketChannel.write(request.staticBuffer)) == -1) {
	  throw new ClosedChannelException();
	}

	ww += w;

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("request.staticBuffer (2)<" + request.staticBuffer
	      + "> w=" + w);
	}

	/* Some error conditions */
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {

	  if (ww > (request.sBufSize + request.bufoffset)) {
	    logger.fatal(" Fatal-Bug (1) <" + request.tag + ">");
	    logger.fatal("request.staticBuffer " + request.staticBuffer);
	    System.exit(1);
	  }

	  if (request.staticBuffer.hasRemaining()) {
	    logger.fatal(" Bug (1) <" + request.tag + ">");
	    logger.fatal("request.staticBuffer " + request.staticBuffer);
	    System.exit(1);
	  }

	  if (request.staticBuffer.position() != (request.sBufSize + request.bufoffset)) {
	    logger.fatal(" Bug (2) <" + request.tag + ">");
	    logger.fatal("request.staticBuffer " + request.staticBuffer);
	    System.exit(1);
	  }

	  if (request.staticBuffer.position() != request.staticBuffer.limit()) {
	    logger.fatal(" Bug (3) <" + request.tag + ">");
	    logger.fatal("request.staticBuffer " + request.staticBuffer);
	    System.exit(1);
	  }
	} // error condition
      }// end while writing.

    } // end writing static section.

    // stop = System.nanoTime() ;
    // intv = stop - strt ;
    // strt = stop;
    // logger.debug("isend_packing_time_route1 <"+intv/1000);
    // strt = System.nanoTime() - strt ;
    // logger.debug("isend_writing_time <"+intv/1000);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("request.dBufSize <" + request.dBufSize + ">");
    }

    /* Writing the dynamic section of the buffer */
    if (request.dynamicBuffer != null && request.dBufSize > 0) {
      RawBuffer rawBuffer = BufferFactory.create(request.dBufSize);
      ByteBuffer buffer = ((NIOBuffer) rawBuffer).getBuffer();
      buffer.position(0);
      buffer.limit(request.dBufSize);

      buffer.put(request.dynamicBuffer, 0, request.dBufSize);
      buffer.flip();
      ww = 0;
      w = 0;

      while (buffer.hasRemaining()) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("buffer (1)<" + buffer + ">");
	}

	if ((w = socketChannel.write(buffer)) == -1) {
	  throw new ClosedChannelException();
	}
	ww += w;

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("buffer (2)<" + buffer + ">");
	}

      } // end while.

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	if (buffer.hasRemaining()) {
	  logger.fatal(" Bug (4) <" + request.tag + ">");
	  logger.fatal("buffer " + buffer);
	  System.exit(1);
	}

	if (buffer.position() != (request.dBufSize)) {
	  logger.fatal("Bug (5) <" + request.tag + ">");
	  logger.fatal("buffer " + buffer);
	  System.exit(1);
	}

	if (buffer.position() != buffer.limit()) {
	  logger.fatal("Bug (6) <" + request.tag + ">");
	  logger.fatal("buffer " + buffer);
	  System.exit(1);
	}

      }

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("written down bytes " + buffer.position());
      }

      BufferFactory.destroy(rawBuffer);

    } // end writing dynamic section

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--eagerSend finishes--");
    }

  } // end eagerSend

  public mpjdev.Request peek() throws XDevException {
    return completedList.remove();
  }

  static CompletedList completedList = new CompletedList();

  static class CompletedList {

    NIORequest front, back;
    int size;

    /**
     * Remove request from any position in the completedList
     */
    synchronized void remove(NIORequest request) {
      if (request.inCompletedList) {
	if (front == back) {
	  front = null;
	  back = null;
	} else if (front == request) {
	  front.prevCompleted.nextCompleted = front.nextCompleted;
	  front.nextCompleted.prevCompleted = front.prevCompleted;
	  front = front.prevCompleted;
	} else if (back == request) {
	  back.prevCompleted.nextCompleted = back.nextCompleted;
	  back.nextCompleted.prevCompleted = back.prevCompleted;
	  back = back.nextCompleted;
	} else {
	  request.prevCompleted.nextCompleted = request.nextCompleted;
	  request.nextCompleted.prevCompleted = request.prevCompleted;
	}

	request.inCompletedList = false;
	size--;
	// System.out.println(" size "+size);
      }
    }

    /**
     * Remove request from the front of completedList Wait until a request is
     * found
     */
    synchronized NIORequest remove() {

      while (listEmpty()) {
	try {
	  wait();
	}
	catch (Exception e) {
	}
      }

      NIORequest oldFront = null;
      oldFront = front;
      if (front == back) {
	front = null;
	back = null;
      } else {
	front.prevCompleted.nextCompleted = front.nextCompleted;
	front.nextCompleted.prevCompleted = front.prevCompleted;
	front = front.prevCompleted;
      }

      oldFront.inCompletedList = false;
      size--;
      // System.out.println(" size "+size);

      return oldFront;
    }

    /**
     * Add request at the front of completedList
     */
    synchronized void add(NIORequest request) {
      if (listEmpty()) {
	front = request;
	back = request;
	request.nextCompleted = request;
	request.prevCompleted = request;
      } else {
	front.nextCompleted.prevCompleted = request;
	request.nextCompleted = front.nextCompleted;
	front.nextCompleted = request;
	request.prevCompleted = front;
	back = request;
      }
      size++;
      // System.out.println(" size "+size);
      request.inCompletedList = true;
      notify();
    }

    boolean listEmpty() {
      return (front == null && back == null);
    }

  }

  /**
   * iwaitany
   * 
   * public static mpjdev.Status iwaitany(mpjdev.Request[] requests) { boolean
   * found = false; boolean inActive = true ; mpjdev.Status completedStatus =
   * null ;
   * 
   * // check if there is a valid request which could be peeked
   * 
   * for(int i=0 ; i< requests.length ; i++) { if(requests[i] != null) {
   * inActive = false; } }
   * 
   * if(inActive) { return null; }
   * 
   * do { for(int j=0 ; j <requests.length ; j++) {
   * 
   * if(requests[j] == null) { continue; }
   * 
   * completedStatus = requests[j].itest() ;
   * 
   * if(completedStatus == null) { continue; }
   * 
   * completedStatus = requests[j].iwait() ; completedStatus.index = j; found =
   * true ; break ;
   * 
   * } } while(!found);
   * 
   * return completedStatus ; }
   */

  /**
   * This method is the non-blocking recv method.
   * 
   * @param buf
   *          The mpjbuf.Buffer object where the user wishes to receive the
   *          actual message
   * @param srcID
   *          The process id of the sending process
   * @param tag
   *          The unique identifier of the message
   * @return Status The status object containing the details of recv
   * @throws MPJException
   *           If the buffer is null or the src is insane
   * @throws IOException
   *           If some I/O error occurs
   * @throws java.lang.IllegalArgumentException
   */
  public mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
      int context) throws XDevException {

    mpjdev.Status status = new mpjdev.Status(srcID.uuid(), tag, -1);
    Request request = irecv(buf, srcID, tag, context, status);

    return request.iwait();

  }

  /**
   * Blocking receive method.
   * 
   * @param buf
   *          The mpjbuf.Buffer objereceive the actual message
   * @param srcID
   *          The process id of the sending process
   * @param tag
   *          The unique identifier of the message
   * @param context
   *          An integer that provides "safe communication" universe
   * @return mpjdev.Status The status object containing the details of recv
   */
  public Request irecv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
      int context, mpjdev.Status status) throws XDevException {

    UUID dstUUID = id.uuid();
    UUID srcUUID = srcID.uuid();
    UUID srcHybUUID = null, dstHybUUID = null;

    if (isHybrid) {
      ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(0);
      long msb = 0L, lsb = 0L;
      msb = ((NIOBuffer) buf.getStaticBuffer()).getBuffer().getLong();
      lsb = ((NIOBuffer) buf.getStaticBuffer()).getBuffer().getLong();
      srcHybUUID = new UUID(msb, lsb);
      msb = ((NIOBuffer) buf.getStaticBuffer()).getBuffer().getLong();
      lsb = ((NIOBuffer) buf.getStaticBuffer()).getBuffer().getLong();
      dstHybUUID = new UUID(msb, lsb);

      try {
	buf.clear();
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---irecv---<" + tag + ">");
      logger.debug("Looking whether this req has been posted or not");
    }

    // long strt = System.nanoTime() ;
    // long stop = 0L, intv = 0L;
    try {
      sem.acquire();
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    NIORecvRequest request = null;

    try {
      /*
       * Checking if Hybrid device is enabled then source and destination of
       * message should be Hybrid source and destination, not those of Network
       * souruce and destination. Key of message will be Hybrid Source and
       * Destination.
       */
      if (isHybrid) {
	request = arrQue.rem(context, dstHybUUID, srcHybUUID, tag);
      } else {
	request = arrQue.rem(context, dstUUID, srcUUID, tag);
      }
    }
    catch (Exception e) {
      throw new XDevException(e);
    }

    if (request != null) {
      // stop = System.nanoTime() ;
      // intv = stop - strt ;
      // strt = stop;
      // logger.debug("irecv_determing_its_posted <"+intv/1000);

      /*
       * some stuff is only known when the recv is posted ...so setting that
       * kinda stuff in the next few lines
       */
      request.staticBuffer = ((NIOBuffer) buf.getStaticBuffer()).getBuffer();
      request.status = status;
      // request.rank_source = srcID.rank();

      if (request.srcUUID.equals(dstUUID)) {
	sem.signal();

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug(" request.sendRequest.staticBuffer "
	      + request.sendRequest.staticBuffer);
	  logger.debug(" request.sendRequest.bufoffset "
	      + request.sendRequest.bufoffset);
	  logger.debug(" request.sBufSize " + request.sBufSize);
	}

	buf.copy(request.sendRequest.staticBuffer,
	    request.sendRequest.bufoffset, request.sBufSize, 0,
	    request.sendRequest.dynamicBuffer, request.dBufSize);

	// completedList.add(request);
	// completedList.add(request.sendRequest);
	request.setCompleted(true);
	request.sendRequest.setCompleted(true);
	return request;

      }

      else if (((request.sBufSize + request.dBufSize) <= psl)
	  && request.commMode == STD_COMM_MODE) {

	/*
	 * (Eager-Send), The message has already been copied to xdev buffad so
	 * we just need to copy it from the xdevBuffer to the user buffer
	 */

	if (request.sBufSize > 0) {
	  ByteBuffer eagerBuffer = ((NIOBuffer) request.eagerBuffer)
	      .getBuffer();
	  request.staticBuffer.position(0);
	  request.staticBuffer.limit(request.sBufSize);
	  eagerBuffer.limit(request.sBufSize);
	  eagerBuffer.position(0);
	  request.staticBuffer.put(eagerBuffer);
	  BufferFactory.destroy(request.eagerBuffer);
	} // end if

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("setting the buf size " + request.sBufSize);
	}

	buf.setSize(request.sBufSize);

	if (request.dBufSize > 0) {
	  buf.setDynamicBuffer(request.dynamicBuffer);
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("removed ");
	}

	// completedList.add( request );
	request.notifyMe();
	sem.signal();
	// stop = System.nanoTime() ;
	// intv = stop - strt ;
	// strt = stop;
	// logger.debug("irecv_copying_from_devM <"+intv/1000);
	return request;

      } // end eager-send looop.

      else if ((((request.sBufSize + request.dBufSize) > psl) && request.commMode == STD_COMM_MODE)
	  || (request.commMode == SYNC_COMM_MODE)) {

	/*
	 * (Rendezous), writing the ctrl msg back, because (1) we have received
	 * the control message from the sender, (2)a matching receive is also
	 * posted
	 */

	request.buffer = buf;
	SocketChannel tc = worldReadableTable.get(request.srcUUID);
	SocketChannel c = worldWritableTable.get(request.srcUUID);
	recvMap.put(new Integer(request.recvCounter), request);
	sem.signal();
	CustomSemaphore wLock = writeLockTable.get(c);

	try {
	  wLock.acquire();
	}
	catch (Exception e) {
	}

	rendezCtrlMsgR2S(c, request);
	wLock.signal();
	return request;

      }

    }

    /*
     * There is no matching receive method posted, so we post it here in the
     * user thread and when the selector thread will be posting some thing,
     * it'll check it first in the recvQue.
     */
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("No Matching req, so posting ourselves");
    }

    request = new NIORecvRequest(srcID, null, null, id(), tag,
	REQ_NOT_COMPLETED, buf, context, status, ++recvCounter,
	nextSequenceNum++);

    /*
     * If Hybrid device is being used then Initialize Hybrid Souce and
     * Destination fields in Request object.
     */
    if (isHybrid) {
      request.srcHybUUID = srcHybUUID;
      request.dstHybUUID = dstHybUUID;
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("request.staticBuffer (in recv) " + request.staticBuffer);
    }

    recvQueue.add(request);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Added request in irecv ");
    }

    // stop = System.nanoTime() ;
    // intv = stop - strt ;
    // strt = stop;
    // logger.debug("irecv_posting_and_adding_to_queue <"+intv/1000);
    sem.signal();
    return request;

  } // end irecv()

  void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
	logger.debug("shutdownHook thread");
	try {
	  selector.wakeup();
	  selectorFlag = false;
	  try {
	    // serverChannel.close();
	    SocketChannel peerChannel = null;
	    SocketChannel controlChannel = null;

	    for (int i = 0; i < writableChannels.size(); i++) {
	      peerChannel = writableChannels.get(i);
	      peerChannel.close();
	    }

	    peerChannel = null;
	    selector.close();

	  }
	  catch (IOException e) {
	    // System.exit(0);
	  }
	}
	catch (Throwable e) {
	  // System.exit(0);
	}
      }
    });
  }

  private void realFinish() throws XDevException {
    selectorFlag = false;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---finish---");
      logger.debug("Waking up the selector");
    }
    try {
      selector.wakeup();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Closed the two selectors");
    }

    try {
      selector.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    SocketChannel peerChannel = null;
    SocketChannel peerCtrlChannel = null;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("peerChannelSize " + writableChannels.size());
      logger.debug("peerCtrlChannelSize " + readableChannels.size());
    }

    for (int i = 0; i < writableChannels.size(); i++) {

      peerChannel = writableChannels.get(i);
      peerCtrlChannel = readableChannels.get(i);

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("closing data-channel " + peerChannel);
      }

      try {

	if (peerChannel.isOpen()) {
	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("the channel was open, so closing it");
	  }
	  peerChannel.close();
	}

      }
      catch (Exception e) {
	e.printStackTrace();
      }

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("closing control-channel " + peerCtrlChannel);
      }

      try {

	if (peerCtrlChannel.isOpen()) {
	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("the channel was open, so closing it");
	  }
	  peerCtrlChannel.close();
	}

      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("closed all the channels");
    }

    synchronized (finishLock) {
      finished = true;
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("selector thread notifying the user thread");
      }
      finishLock.notify();
    }

  }

  Object finishLock = new Object();

  static final int SHUTDOWN_SIGNAL = -13;
  static final int END_OF_STREAM = -14;

  /**
   * This method shuts down the device.
   * 
   * @throws MPJException
   * @throws IOException
   *           If some I/O error occurs
   */
  public synchronized void finish() throws XDevException {
    /*
     * Calling NIO device finish method. in NIO finish method all the
     * communication is purely for NIO device. No messages of Hybrid device is
     * expected, setting the switch to false.
     */

    isHybrid = false;

    synchronized (finishLock) {
      if (finished) {
	return;
      }
    }

    // do this fanning bit ..
    int offset = 0;
    int[] data = new int[1];
    int count = 1;
    int btag = -994576;
    int context = 50;
    mpjbuf.Buffer sbuf = new mpjbuf.Buffer(
	BufferFactory.create(23 + SEND_OVERHEAD), SEND_OVERHEAD,
	23 + SEND_OVERHEAD);
    mpjbuf.Buffer rbuf = new mpjbuf.Buffer(BufferFactory.create(16), 0, 16);

    if (procTree.numChildren == -1 || !procTree.isRoot) {
      try {
	sbuf.putSectionHeader(Type.INT);
	sbuf.write(data, offset, count);
	sbuf.commit();
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    if (procTree.isRoot) {
      for (int i = 0; i < procTree.child.length; i++) {
	if (procTree.child[i] != -1) {
	  recv(rbuf, pids[procTree.child[i]], btag, context);
	  try {
	    rbuf.clear();
	  }
	  catch (Exception e) {
	  }
	}
      }
    } else {
      if (procTree.parent == -1) {
	// System.out.println("non root's node parent doesn't exist");
      }

      for (int i = 0; i < procTree.child.length; i++) {
	if (procTree.child[i] != -1) {
	  recv(rbuf, pids[procTree.child[i]], btag, context);
	  try {
	    rbuf.clear();
	  }
	  catch (Exception e) {
	  }
	}
      }

      send(sbuf, pids[procTree.parent], btag, context);
    }

    if (procTree.isRoot) {
      realFinish();
    }

    synchronized (finishLock) {

      while (!finished) {
	try {
	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("user thread going to sleep");
	  }
	  finishLock.wait();
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    BufferFactory.destroy(sbuf.getStaticBuffer());
    BufferFactory.destroy(rbuf.getStaticBuffer());

    // System.out.println(" finish "+rank);

  }

  /*
   * This method is used during initialization.
   */
  void doBarrierRead(SocketChannel socketChannel, Hashtable table,
      boolean ignoreFirstFourBytes) throws XDevException {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---barrierRead---");
    }
    long lsb, msb;
    int read = 0, tempRead = 0, rank;
    UUID ruid = null;
    ByteBuffer barrBuffer = ByteBuffer.allocate(24); // changeallocate

    if (ignoreFirstFourBytes) {
      barrBuffer.limit(24);
    } else {
      barrBuffer.limit(20);
    }

    while (barrBuffer.hasRemaining()) {
      try {
	if (socketChannel.read(barrBuffer) == -1) {
	  throw new XDevException(new ClosedChannelException());
	}
      }
      catch (Exception e) {
	throw new XDevException(e);
      }
    }

    barrBuffer.flip();

    if (ignoreFirstFourBytes) {
      barrBuffer.getInt();
    }

    rank = barrBuffer.getInt();
    msb = barrBuffer.getLong();
    lsb = barrBuffer.getLong();
    barrBuffer.clear();
    ruid = new UUID(msb, lsb);
    pids[rank] = new ProcessID(ruid); // , rank);
    size = nprocs;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("(after ck) trying to add rank " + rank + "into table "
	  + table);
    }

    synchronized (table) {
      table.put(ruid, socketChannel);
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("Adding rank " + rank + "into table " + table);
      }

      if ((table.size() == nprocs - 1)) {
	try {
	  table.notify();
	}
	catch (Exception e) {
	  throw new XDevException(e);
	}
      }

    }

  }

  /* called from the selector thread, and accept the connections */
  boolean doAccept(SelectableChannel keyChannel, Vector channelCollection,
      boolean blocking) throws Exception {
    SocketChannel peerChannel = null;

    synchronized (channelCollection) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("---doAccept---");
      }

      if (keyChannel.isOpen()) {
	peerChannel = ((ServerSocketChannel) keyChannel).accept();
      } else {
	return false;
      }

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("Added channel " + peerChannel);
      }
      channelCollection.add(peerChannel);
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("Now the size is <" + channelCollection.size() + ">");
      }

      if (blocking == false) {
	peerChannel.configureBlocking(blocking);
	peerChannel.register(selector, SelectionKey.OP_READ
	    | SelectionKey.OP_WRITE);
      } else {
	peerChannel.configureBlocking(blocking);
      }

      peerChannel.socket().setTcpNoDelay(true);

      peerChannel.socket().setSendBufferSize(524288);
      peerChannel.socket().setReceiveBufferSize(524288);

      if (channelCollection.size() == nprocs - 1) {
	channelCollection.notify();
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug(" notifying and returning true");
	}
	return true;
      }

    } // end sync.

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--doAccept ends--");
    }
    peerChannel = null;
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" returning false");
    }
    return false;
  }

  /*
   * This method receives the message into the user specified memory. This is
   * the case, when the receiver has received the message from the sender, and a
   * matching recv has also been found.
   */
  private void eagerRecv2UserMem(NIORecvRequest request,
      SocketChannel socketChannel) throws Exception {
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---eagerRecv2UserMem---");
      logger.debug("request.sBufSize (is it zero?) " + request.sBufSize);
    }

    if (request.sBufSize > 0) {
      request.staticBuffer.limit(request.sBufSize);
      request.staticBuffer.position(0);

      while (request.staticBuffer.hasRemaining()) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("b(1) " + request.staticBuffer);
	}

	if (socketChannel.read(request.staticBuffer) == -1) {
	  throw new ClosedChannelException();
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("b(2) " + request.staticBuffer);
	}

      }

      request.buffer.setSize(request.sBufSize);
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("request.dBufSize " + request.dBufSize);
    }

    if (request.dBufSize > 0) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("reading the dynamic buffer bytes");
      }

      RawBuffer rawBuffer = BufferFactory.create(request.dBufSize);
      ByteBuffer tmpBuffer = ((NIOBuffer) rawBuffer).getBuffer();
      tmpBuffer.position(0);
      tmpBuffer.limit(request.dBufSize);

      byte[] tmpArray = new byte[request.dBufSize];

      while (tmpBuffer.hasRemaining()) {

	if (socketChannel.read(tmpBuffer) == -1) {
	  throw new ClosedChannelException();
	}

      }

      tmpBuffer.flip();
      tmpBuffer.get(tmpArray, 0, tmpArray.length);
      request.dynamicBuffer = tmpArray;
      request.buffer.setDynamicBuffer(tmpArray);
      BufferFactory.destroy(rawBuffer);
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--eagerRecv2UserMem ends--");
    }

  }

  /*
   * This method receives the message into the xdev memory. This is the case,
   * when the receiver has received the message from the sender, but no matching
   * recv is posted. We have to save the message at some place.
   */
  private void eagerRecv2mpjMem(NIORecvRequest request,
      SocketChannel socketChannel) throws Exception {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---eagerRecv2mpjMem_" + request.tag);
    }

    if (request.sBufSize > 0) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("static section ...");
	logger.debug(" creating eager buffer ");
      }

      request.eagerBuffer = BufferFactory.create(request.sBufSize);

      ByteBuffer eagerBuffer = ((NIOBuffer) request.eagerBuffer).getBuffer();
      eagerBuffer.limit(request.sBufSize);
      eagerBuffer.position(0);
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug(" creating eager buffer" + eagerBuffer);
      }

      while (eagerBuffer.hasRemaining()) {

	if (socketChannel.read(eagerBuffer) == -1) {
	  throw new ClosedChannelException();
	}

      }

      // eagerBuffer.flip(); should have no effect ..
    }

    if (request.dBufSize > 0) {
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("reading dynamic buffer bytes");
      }

      RawBuffer rawBuffer = BufferFactory.create(request.dBufSize);
      ByteBuffer tmpBuffer = ((NIOBuffer) rawBuffer).getBuffer();
      tmpBuffer.position(0);
      tmpBuffer.limit(request.dBufSize);

      byte[] tmpArray = new byte[request.dBufSize];

      while (tmpBuffer.hasRemaining()) {
	if (socketChannel.read(tmpBuffer) == -1) {
	  throw new ClosedChannelException();
	}
      }

      tmpBuffer.flip();
      tmpBuffer.get(tmpArray, 0, tmpArray.length);
      tmpBuffer.clear();
      BufferFactory.destroy(rawBuffer);
      request.dynamicBuffer = tmpArray;
    }

  }

  /*
   * Sender will complete communication.
   */
  void doRendezSendCompletion(SocketChannel socketChannel, SelectionKey key)
      throws Exception {

    synchronized (rendez_send_buffer) {
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("---doRendezSendCompletion---");
      }

      rendez_send_buffer.position(0);
      rendez_send_buffer.limit(ACK_LENGTH);

      while (rendez_send_buffer.hasRemaining()) {
	if (socketChannel.read(rendez_send_buffer) == -1) {
	  throw new ClosedChannelException();
	}
      }

      rendez_send_buffer.flip();
      msgReceivedFrom = socketChannel;

    }
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("rendezCtrlPart followed by a send ....(release rLock)");
    }
  } // end doRendezSendCompletion.

  /*
   * This method is called from the selector thread and it receives the message
   * for eager-send, control message for rendezous
   */
  private NIORecvRequest recvCtrlMsgFromSender(SocketChannel socketChannel,
      SelectionKey skey) throws Exception {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("-- recvCtrlMsgFromSender ---");
    }

    rcb.clear();
    rcb.limit(CTRL_MSG_LENGTH);

    while (rcb.hasRemaining()) {
      if (socketChannel.read(rcb) == -1) {
	throw new ClosedChannelException();
      }

    } // end while.

    rcb.flip();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Receiver getting the control message");
      logger.debug("Could be rend, eager, posted b4 or not");
    }
    long msb = rcb.getLong();
    long lsb = rcb.getLong();
    int tag = rcb.getInt();
    int staBufferSize = rcb.getInt();
    int dynaBufferSize = rcb.getInt();
    int commMode = rcb.getInt();
    int context = rcb.getInt();
    int numEls = rcb.getInt();
    int sendCounter = rcb.getInt();
    byte t = rcb.get();
    mpjbuf.Type type = mpjbuf.Type.getType(t);
    UUID srcUUID = new UUID(msb, lsb);
    UUID srcHybUUID = null, dstHybUUID = null;

    // for Hybrid device
    if (isHybrid) {
      msb = rcb.getLong();
      lsb = rcb.getLong();
      srcHybUUID = new UUID(msb, lsb);
      msb = rcb.getLong();
      lsb = rcb.getLong();
      dstHybUUID = new UUID(msb, lsb);
    }

    rcb.clear();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("msb :" + msb);
      logger.debug("lsb :" + lsb);
      logger.debug("sendCounter :" + sendCounter);
      logger.debug("tag :" + tag);
      logger.debug("staBufferSize :" + staBufferSize);
      logger.debug("dynaBufferSize :" + dynaBufferSize);
      logger.debug("context :" + context);
      logger.debug("commMode :" + commMode);
      logger.debug("type :" + type);
      logger.debug("sendCounter :" + sendCounter);
      logger.debug("numEls :" + numEls);
    }

    sem.acquire();

    NIORecvRequest request = null;

    if (isHybrid) {
      request = recvQueue.rem(context, dstHybUUID, srcHybUUID, tag);

    } else {
      request = recvQueue.rem(context, id().uuid(), srcUUID, tag);
    }

    if (request != null) {

      /*
       * Rendezous, recv posted, message arrived, would write control message
       * back
       */
      if (isHybrid) {
	// TODO check if this is already populated
	request.srcHybUUID = srcHybUUID;
	request.dstHybUUID = dstHybUUID;
      }

      request.srcUUID = srcUUID;
      request.dstUUID = id().uuid();
      request.tag = tag;
      request.numEls = numEls;
      request.type = type;

      // std. mode AND using rendezvous protocol OR sync. mode.
      if ((((staBufferSize + dynaBufferSize) > psl) && commMode == STD_COMM_MODE)
	  || (commMode == SYNC_COMM_MODE)) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("receiver receving the ctrl, sending back to sender");
	}
	request.sBufSize = staBufferSize;
	request.dBufSize = dynaBufferSize;
	request.sendCounter = sendCounter;

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("setting readPending to true " + request);
	}
	recvMap.put(new Integer(request.recvCounter), request);
	sem.signal();
	request.code = SEND_ACK_TO_SENDER;
	return request;
      } else {
	request.sBufSize = staBufferSize;
	request.dBufSize = dynaBufferSize;
	sem.signal();
	request.code = RECV_IN_USER_MEMORY;
	return request;
      }

    } // end for

    request = new NIORecvRequest(id.uuid(), tag, false, context, staBufferSize,
	dynaBufferSize, commMode, socketChannel, numEls, type, sendCounter,
	++recvCounter, srcUUID);

    // for Hybrid Device
    if (isHybrid) {
      request.srcHybUUID = srcHybUUID;
      request.dstHybUUID = dstHybUUID;
    }

    if ((((staBufferSize + dynaBufferSize) <= psl) && commMode == STD_COMM_MODE)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("setting the request.code to RECV_IN_DEV_MEMORY");
      }

      request.code = RECV_IN_DEV_MEMORY;

    }

    else if ((((staBufferSize + dynaBufferSize) > psl) && commMode == 3)
	|| (commMode == 2)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("setting the request.code to NOTHING ");
      }

      /* For Rendezous, do nothing? */
      arrQue.add(request);

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("Adding the request");
      }

      sem.signal();
      return request;
    }

    return request;
  } // end recvMsgFromSender

  synchronized void rendezSendCtrlMsg(SocketChannel socketChannel,
      NIOSendRequest request) throws Exception {
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("rendezBuffer " + rendezBuffer + "me" + rank);
    }
    try {
      rendezBuffer.putInt(RENDEZ_HEADER);
      rendezBuffer.putInt(request.recvCounter);
    }
    catch (Exception e) {
      throw e;
    }
    rendezBuffer.flip();

    while (rendezBuffer.hasRemaining()) {
      if (socketChannel.write(rendezBuffer) == -1) {
	throw new ClosedChannelException();
      }
    }

    rendezBuffer.clear();
  }

  // -83 => more to write ...
  // 0 => nothing to write
  // 2 => no matching request.
  // -1 => error.

  int rendezSendData(SocketChannel socketChannel, NIOSendRequest request)
      throws Exception {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("---rendezSendData ---");
      logger.debug("socketChannel " + socketChannel);
      logger.debug("request.tag " + request.tag);
    }

    /* writing the static buffer contents */
    int wrote = request.bufoffset;
    int tempWrote = 0;
    int ww = request.bufoffset;
    int w = 0;

    if (request.sBufSize > 0 && request.sSection) {

      request.staticBuffer.limit(request.sBufSize + request.bufoffset);
      request.staticBuffer.position(request.bufoffset);

      while (request.staticBuffer.hasRemaining()) {

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("buf(first)" + request.staticBuffer + "w" + wrote);
	}

	request.staticBuffer.position(wrote);

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("buf(second)" + request.staticBuffer + "w" + wrote);
	}

	if ((tempWrote = socketChannel.write(request.staticBuffer)) == -1) {
	  throw new ClosedChannelException();
	}

	wrote += tempWrote;
	w = tempWrote;
	ww += w;

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {

	  if (request.staticBuffer.hasRemaining()) {
	    System.out.println("Bug (7) <" + request.tag + ">");
	    logger.debug("Bug (7) <" + request.tag + ">");
	    System.out.println("request.staticBuffer " + request.staticBuffer);
	    // System.out.println("buf " + buf);
	    System.out.println("wrote " + wrote);
	    System.exit(1);
	  }

	  if (request.staticBuffer.position() != (request.sBufSize + request.bufoffset)) {
	    System.out.println("Bug (8) <" + request.tag + ">" + rank);
	    logger.debug("Bug (8) <" + request.tag + ">");
	    System.out.println("request.staticBuffer " + request.staticBuffer);
	    // System.out.println("buf " + buf);
	    System.out.println("wrote " + wrote);
	    System.exit(1);
	  }

	  if (request.staticBuffer.position() != request.staticBuffer.limit()) {

	    System.out.println("Bug (9) <" + request.tag + ">");
	    logger.debug("Bug (9) <" + request.tag + ">");
	    System.out.println("request.staticBuffer " + request.staticBuffer);
	    // System.out.println("buf " + buf);
	    System.out.println("wrote " + wrote);
	    System.exit(1);
	  }

	  /*
	   * assuming when this happens, then no byte is actually written into
	   * the channel
	   */

	  if (wrote != request.staticBuffer.position()) {
	    System.out.println("send:staticSend:insane");
	    logger.debug("insane (rendezSend--staticBuffer) ");
	    logger.debug("request.staticBuffer [2-3] " + request.staticBuffer);

	    if (wrote > request.staticBuffer.limit()) {
	      System.out.println("cant even recover from this");
	    }

	    System.exit(1);
	  }

	  logger.debug("buf(third)" + request.staticBuffer);
	  // logger.debug("buf(third)" + buf );
	  logger.debug("tempWrote " + tempWrote);
	  logger.debug("wrote " + wrote);
	}

      } // end while.

      request.sSection = false;
      request.bytesWritten = 0;
      // buf = null;
    } // end if

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("Sending dynamic portion <" + request.dBufSize);
      logger.debug("request.dBufSize" + request.dBufSize);
      logger.debug("request.dSection" + request.dSection);
    }

    wrote = 0;
    tempWrote = 0;
    ww = 0;
    w = 0;

    if (request.dBufSize > 0 && request.dSection) {

      if (request.dBuffer == null) {

	// get bytebuffer from bufferFactory ...
	request.eagerBuffer = BufferFactory.create(request.dBufSize);
	request.dBuffer = ((NIOBuffer) request.eagerBuffer).getBuffer();
	request.dBuffer.position(0);
	request.dBuffer.limit(request.dBufSize);
	request.dBuffer.put(request.dynamicBuffer, 0, request.dBufSize);
	request.dBuffer.flip();
	request.bytesWritten = 0;

      }

      request.dBuffer.position(request.bytesWritten);
      wrote = request.bytesWritten;

      // while(request.dBuffer.hasRemaining()) {
      while (ww != request.dBufSize) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("ww " + ww);
	  logger.debug("request.dBuffer(1) " + request.dBuffer);
	}
	request.dBuffer.position(wrote);

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("request.dBuffer (2)" + request.dBuffer);
	}

	if ((tempWrote = socketChannel.write(request.dBuffer)) == -1) {
	  throw new ClosedChannelException();
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("request.dBuffer (3)" + request.dBuffer);
	}
	wrote += tempWrote;
	w = tempWrote;
	ww += w;

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  if (request.dBuffer.hasRemaining()) {
	    System.out.println("Bug (10) <" + request.tag + ">");
	    logger.debug("Bug (10) <" + request.tag + ">");
	    System.out.println("request.dBuffer " + request.dBuffer);
	    System.exit(1);
	  }

	  if (request.dBuffer.position() != (request.dBufSize)) {
	    System.out.println("Bug (11) <" + request.tag + ">");
	    logger.debug("Bug (11) <" + request.tag + ">");
	    System.out.println("request.dBuffer " + request.dBuffer);
	    System.exit(1);
	  }

	  if (request.dBuffer.position() != request.dBuffer.limit()) {
	    System.out.println("Bug (12) <" + request.tag + ">");
	    logger.debug("Bug (12) <" + request.tag + ">");
	    System.out.println("request.dBuffer " + request.dBuffer);
	    System.exit(1);
	  }

	  if (wrote != request.dBuffer.position()) {
	    System.out.println("dynamic: insane");
	    System.exit(1);
	  }

	  logger.debug("request.dBuffer(4) " + request.dBuffer);
	}

      } // end while.

      request.dSection = false;
      BufferFactory.destroy(request.eagerBuffer);

      return 0;
    } // if ends

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--rendez Send Data Ends--");
    }
    return 2;
  }

  private int rendezRecvData(SocketChannel socketChannel, int datum,
      NIORecvRequest request) throws Exception {

    int read = 0, tempRead = 0;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--rendezRecvData--");
      logger.debug("request.tag " + request.tag);
      logger.debug("request.sBufSize " + request.sBufSize);
      logger.debug("request.dBufSize " + request.dBufSize);
      logger.debug("request.bytesRead " + request.bytesRead);
      logger.debug("datum <" + datum + ">");
    }

    if (request.sBufSize > 0 && request.sSection) {

      request.staticBuffer.limit(request.sBufSize);
      request.staticBuffer.position(request.bytesRead);

      if (datum != RENDEZ_HEADER) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("Putting it on buffer because its not header data ");
	}
	request.staticBuffer.putInt(datum);
	request.bytesRead = request.bytesRead + 4;
	request.staticBuffer.position(request.bytesRead);
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("request.staticBuffer " + request.staticBuffer);
	}
	datum = RENDEZ_HEADER;
      } else if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("static-section:rendezRecvData called for the first time");
      }

      read = request.bytesRead;

      while (request.staticBuffer.hasRemaining()) {

	// logger.debug("request.staticBuffer(1st) "+request.staticBuffer);
	request.staticBuffer.position(read);
	// logger.debug("request.staticBuffer(2nd) "+request.staticBuffer);

	if ((tempRead = socketChannel.read(request.staticBuffer)) == -1) {
	  throw new ClosedChannelException();
	}

	read += tempRead;
	// logger.debug("request.staticBuffer (3) "+request.staticBuffer);
	// logger.debug("read "+read);

	if (request.staticBuffer.remaining() > 3) {
	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("got a short tempRead <" + tempRead);
	  }
	  request.bytesRead = read;
	  return MORE_TO_READ;
	}

      } // end while.

      request.sSection = false;
      request.bytesRead = 0;

    } // end static buffer reading.

    read = 0;
    tempRead = 0;

    if (request.dBufSize > 0 && request.dSection) {

      if (request.dBuffer == null) {

	// get bytebuffer from bufferFactory ...
	request.dBuffer = ByteBuffer.allocate(request.dBufSize);
	request.bytes = new byte[request.dBufSize];
	request.bytesRead = 0;

      }

      if (datum != RENDEZ_HEADER) {
	request.dBuffer.putInt(datum);
	request.bytesRead = request.bytesRead + 4;
	request.dBuffer.position(request.bytesRead);
	datum = RENDEZ_HEADER;
      } else {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("dynamic section: rendezRecvData called");
	  logger.debug("for the first time");
	}
      }

      read = request.bytesRead;
      request.dBuffer.limit(request.dBufSize);
      request.dBuffer.position(request.bytesRead);

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug("initial read" + read);
	logger.debug("reading the dynamic portion");
      }

      while (request.dBuffer.hasRemaining()) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("dBuffer (1)" + request.dBuffer);
	}
	request.dBuffer.position(read);

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("dBuffer (2)" + request.dBuffer);
	}
	if ((tempRead = socketChannel.read(request.dBuffer)) == -1) {
	  throw new ClosedChannelException();
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("dBuffer (3)" + request.dBuffer);
	}
	read = read + tempRead;

	if (request.dBuffer.remaining() > 3) {
	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("got a short read <" + read);
	    logger.debug("dBuffer (3.5)" + request.dBuffer);
	  }
	  request.bytesRead = read;
	  return MORE_TO_READ;
	  // continue;
	}

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("dBuffer (4)" + request.dBuffer);
	}
      } // end reading dynamic section

      request.dSection = false;
      request.dBuffer.flip();
      request.dBuffer.get(request.bytes, 0, request.bytes.length);
      request.dynamicBuffer = request.bytes;
      request.buffer.setDynamicBuffer(request.bytes);
    }

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("setting the size to " + request.sBufSize);
      logger.debug("request.buffer " + request.buffer);
    }
    request.buffer.setSize(request.sBufSize);
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("--rendez Recv Ends--");
    }
    return -1;

  } // end rendezRecvData ...

  /**
   * why is this method static synchronized??? i think there is no need for it
   * to be ... This method is used by the receiver to send the acknowledgement
   * of the control message. Used in Rendezous Protocol, at the receiver side
   * only (1) sender sends the control message to the receiver (2) receiver send
   * the control message acknowledgement back to the sender (this method) (3)
   * once the sender receives the acknowledgement, it sends the actual data (4)
   * it uses and clears _wcb buffer ...we dont want buffer clashes as they may
   * corrupt the message ... (5) it doesn't have to be static sync. as write
   * lock for this channel is first acquired, but just being on the paranoid
   * side. :-)
   */
  static synchronized void rendezCtrlMsgR2S(SocketChannel socketChannel,
      NIORecvRequest request) throws XDevException {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("---rendezCtrlMsgR2S---");
      logger.debug("request.tag :" + request.tag);
      logger.debug("request.sendCounter :" + request.sendCounter);
    }
    _wcb.putInt(ACK_HEADER);
    _wcb.put(((byte) 0));
    _wcb.putInt(request.tag);
    _wcb.putInt(request.context);
    _wcb.putInt(request.sendCounter);
    _wcb.putInt(request.recvCounter);

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("tag " + request.tag);
    }
    _wcb.flip();

    int ww = 0;
    int w = 0;

    while (_wcb.hasRemaining()) {
      // while (ww != 21) {
      try {
	if ((w = socketChannel.write(_wcb)) == -1) {
	  throw new XDevException(new ClosedChannelException());
	}
      }
      catch (Exception e) {
	throw new XDevException(e);
      }

      ww += w;

      if (_wcb.hasRemaining()) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.fatal("Bug (13) <" + request.tag + ">");
	  logger.fatal("_wcb" + _wcb);
	  System.out.println(" eixting ");
	  System.exit(1);
	}
      }

      if (_wcb.position() != _wcb.limit()) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.fatal("Bug (14) <" + request.tag + ">");
	  logger.fatal("_wcb" + _wcb);
	}
	System.out.println(" eixting ");
	System.exit(1);
      }

      if (_wcb.position() != 21) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.fatal("Bug (15) <" + request.tag + ">");
	  logger.fatal("_wcb" + _wcb);
	}
	System.out.println(" eixting ");
	System.exit(1);
      }
    } // end while.

    _wcb.clear();
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("rendezCtrlMsgR2S --FINISHED");
    }

  } // end rendezCtrlMsgSend

  /*
   * Static anonymous inner class that is basically the selector thread
   */
  Runnable selectorThread = new Runnable() {

    /* This is selector thread */
    public void run() {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.info("selector Thread started ");
      }
      Set readyKeys = null;
      long t_start = 0l, t_end = 0l;
      long diff_start = 0l, diff_end = 0l;
      long stop_ready_sendrecv = 0l, start_ready_sendrecv = 0l;
      Iterator<SelectionKey> readyItor = null;
      SelectionKey key = null;
      SelectableChannel keyChannel = null;
      SocketChannel socketChannel = null;
      ByteBuffer lilBuffer = ByteBuffer.allocate(4);
      int tempRead = 0, read = 0, shutdownCounter = 0;
      NIOSendRequest sendRequest = null;
      NIORecvRequest recvRequest = null;
      SocketChannel pendingReadChannel = null;
      int header = 0;
      // long strt = 0L, stop = 0L, intv = 0L ;

      try {
	while (selectorFlag && selector.select() > -1) {

	  // strt = System.nanoTime() ;

	  readyKeys = selector.selectedKeys();
	  readyItor = readyKeys.iterator();

	  while (readyItor.hasNext()) {

	    key = readyItor.next();
	    readyItor.remove();
	    keyChannel = (SelectableChannel) key.channel();
	    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	      logger.debug("---selector EVENT---");
	    }

	    if (key.isValid() && key.isAcceptable()) {

	      ServerSocketChannel sChannel = (ServerSocketChannel) keyChannel;
	      if (sChannel.socket().getLocalPort() == my_server_port) {
		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  logger.debug("selector calling doAccept (data-channel) ");
		}
		doAccept(keyChannel, writableChannels, true);
	      } else {
		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  logger.debug("selector calling doAccept (ctrl-channel) ");
		}
		doAccept(keyChannel, readableChannels, false);
	      }

	    } else if (key.isValid() && key.isReadable()) {

	      socketChannel = (SocketChannel) keyChannel;

	      if (key.attachment() == null) {

		/* Read the first 4 bytes */
		lilBuffer.clear();
		header = 0;

		while (lilBuffer.hasRemaining()) {
		  if ((header = socketChannel.read(lilBuffer)) == -1) {
		    // throw new ClosedChannelException();
		    break;
		  }
		}

		if (header != -1) {
		  lilBuffer.flip();
		  header = lilBuffer.getInt();
		  lilBuffer.clear();
		} else {
		  header = END_OF_STREAM;
		}

		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  logger.debug("---READ_EVENT---" + header);
		}

		/**
		 * It could be (1) control message at receiver side OR (2)
		 * control message response at the sender
		 * 
		 * if(1) { This means, it's receiver getting the control message
		 * from the sender. The receiver at this time doesn't know if it
		 * is Eager, or Rendezous, so first the receiver checks if it is
		 * Eager-Send or Rendezous, If it's Eager-Send, read the control
		 * message and the actual data, and if it's Rendezous, read the
		 * control-message ONLY, and write the response back to the
		 * sender. } else if (2) { If its sender getting the
		 * acknowledgement back, then write the actual data }
		 */
		switch (header) {

		/* Receiver got a control-message from sender */
		case READY_TO_SEND:
		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    diff_end = System.nanoTime();
		    start_ready_sendrecv = System.nanoTime();
		  }
		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug(" received READY_TO_SEND or "
			+ " READY_TO_RECV");
		  }

		  recvRequest = recvCtrlMsgFromSender(socketChannel, key);

		  /* Receiver will send ACK to sender */
		  if (recvRequest.code == SEND_ACK_TO_SENDER) {

		    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		      logger.debug("calling r2s " + socketChannel);
		    }

		    SocketChannel c = worldWritableTable
			.get(recvRequest.srcUUID);
		    CustomSemaphore wLock = writeLockTable.get(c);
		    long acq = System.nanoTime();
		    wLock.acquire();
		    long rel = System.nanoTime();
		    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		      logger.debug("lock=<" + (rel - acq) / (1000000000.0));
		    }
		    rendezCtrlMsgR2S(c, recvRequest);
		    wLock.signal();

		    /*
		     * Receiver will recv message in user memory This
		     * essentially means that recv is posted
		     */
		  } else if (recvRequest.code == RECV_IN_USER_MEMORY) {

		    // stop = System.nanoTime() ;
		    // intv = stop - strt ;
		    // strt = stop;
		    // logger.debug("isend_packing_time_route1 <"+intv/1000);
		    // strt = System.nanoTime() - strt ;
		    // logger.debug("irecv_determing_its_userM <"+intv/1000);
		    eagerRecv2UserMem(recvRequest, socketChannel);
		    // stop = System.nanoTime() ;
		    // intv = stop - strt ;
		    // strt = stop;
		    // logger.debug("irecv_receiving_in_userM <"+intv/1000);
		    // completedList.add( recvRequest );
		    recvRequest.notifyMe();

		    /*
		     * Receiver will recv message in device memory. This means
		     * that recv is not posted, and eager protocol is being
		     * used.
		     */

		  } else if (recvRequest.code == RECV_IN_DEV_MEMORY) {
		    /* has rLock, and getting readLock */
		    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		      logger.debug("Calling eagerRecv2mpjMem");
		    }
		    // stop = System.nanoTime() ;
		    // intv = stop - strt ;
		    // strt = stop;
		    // logger.debug("irecv_determing_its_devM <"+intv/1000);
		    eagerRecv2mpjMem(recvRequest, socketChannel);

		    arrQue.add(recvRequest);
		    // stop = System.nanoTime() ;
		    // intv = stop - strt ;
		    // strt = stop;
		    // logger.debug("irecv_receiving_in_devM <"+intv/1000);

		    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		      logger.debug("Adding the request");
		    }

		    sem.signal();

		  }

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    diff_start = System.nanoTime();
		    stop_ready_sendrecv = System.nanoTime();
		    logger
			.debug("time_ready_sendrecv="
			    + ((stop_ready_sendrecv - start_ready_sendrecv) / 1000000000.0));
		  }

		  break;

		/* Sender is receving ACK back from Receiver */
		case ACK_HEADER:

		  /* Recv ACK from Receiver */
		  buffer_sem.acquire();
		  doRendezSendCompletion(socketChannel, key);
		  (new Thread(rendezSenderThread)).start();
		  break;

		/*
		 * Receiver will be receiving data (rendezvous protocol)
		 */
		case RENDEZ_HEADER:

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug(" RENDEZ_HEADER ");
		  }

		  rcb.limit(RENDEZ_CTRL_MSG_LENGTH);
		  rcb.position(0);

		  while (rcb.hasRemaining()) {
		    if (socketChannel.read(rcb) == -1) {
		      throw new ClosedChannelException();
		    }
		  }

		  rcb.flip();

		  int recvCounter = rcb.getInt();

		  /* Find the matching request */
		  sem.acquire();
		  recvRequest = recvMap.remove(new Integer(recvCounter));
		  sem.signal();

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug("recvCounter " + recvCounter);
		    logger.debug("recvRequest " + recvRequest);
		  }

		  if (recvRequest == null) {
		    continue;
		  }

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    diff_end = System.nanoTime();
		  }

		  header = RENDEZ_HEADER;

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    t_start = System.nanoTime();
		  }

		  int code2 = rendezRecvData(socketChannel, header, recvRequest);

		  if (code2 != MORE_TO_READ) {

		    /*
		     * If all data is received, notify User Thread, and remove
		     * this request from recvQue
		     */
		    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		      logger.debug("notified that recvData is complete ");
		    }
		    // completedList.add(recvRequest);
		    recvRequest.notifyMe();
		    key.attach(null);
		    recvRequest = null;

		  } else {
		    key.attach(recvRequest);
		  }

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug("last statement in RENDEZ_HEADER");
		  }

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    diff_start = System.nanoTime();
		  }
		  break;

		case INIT_MSG_HEADER_DATA_CHANNEL:
		  doBarrierRead(((SocketChannel) keyChannel),
		      worldReadableTable, false);
		  break;

		case INIT_MSG_HEADER_CTRL_CHANNEL:
		  doBarrierRead(((SocketChannel) keyChannel),
		      worldReadableTable, false);
		  break;

		case END_OF_STREAM:
		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug("END OF STREAM ");
		    logger.debug("leaf is -1" + procTree.numChildren);
		  }

		  realFinish();

		  break;

		default:

		  System.out.println(" impossible ");
		  break;

		} // end switch-case

	      } // end if key.attachment == null

	      else {
		recvRequest = (NIORecvRequest) key.attachment();

		/* Receiver whatever data you can */
		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  logger.debug("recvRequest " + recvRequest);
		}

		if (recvRequest == null) {
		  System.out.println("recvRequest cannot be null");
		  continue;
		}

		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  diff_end = System.nanoTime();
		  logger.debug("time_diff=<"
		      + ((diff_end - diff_start) / 1000000000.0) + ">");
		}

		header = RENDEZ_HEADER;
		int code4 = rendezRecvData(socketChannel, header, recvRequest);

		if (code4 != MORE_TO_READ) {

		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    t_end = System.nanoTime();
		  }

		  /*
		   * If all data is received, notify User Thread, and remove
		   * this request from recvQueue
		   */
		  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		    logger.debug("notified that recvData is complete ");
		    logger.debug("time=" + ((t_end - t_start) / 1000000000.0));
		  }
		  // completedList.add(recvRequest);
		  recvRequest.notifyMe();
		  key.attach(null);
		}

		if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		  diff_start = System.nanoTime();
		}
	      }
	    } else if (key.isValid() && key.isWritable()) {

	      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
		logger.debug("WRITE_EVENT (should not see it)");
		logger.debug("In, WRITABLE, changing"
		    + " interestOps to READ_ONLY");
	      }
	      key.interestOps(SelectionKey.OP_READ);

	    } // end else writable.

	  } // end while iterator
	} // end while
      }
      catch (Exception ioe1) {
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug(" error in selector thread " + ioe1.getMessage());
	}
      } // end catch(Exception e) ...

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	logger.debug(" last statement in selector thread");
      }

    } // end run()

  }; // end selectorThread which is an inner class

  Runnable rendezSenderThread = new Runnable() {

    public void run() {

      SocketChannel channel;
      // synchronized (rendez_send_buffer) {
      // }

      try {
	byte goAhead = 0;
	int tag = 0;
	int context = 0;
	int sendCounter = 0;
	int recvCounter = 0;

	/* Read what the receiver just sent */
	synchronized (rendez_send_buffer) {
	  channel = msgReceivedFrom;
	  goAhead = rendez_send_buffer.get();
	  tag = rendez_send_buffer.getInt();
	  context = rendez_send_buffer.getInt();
	  sendCounter = rendez_send_buffer.getInt();
	  recvCounter = rendez_send_buffer.getInt();
	  rendez_send_buffer.clear();
	}

	buffer_sem.signal();

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug(" Started rendezSenderThread " + tag);
	}

	/* Manipulate send comms-set */
	sLock.acquire();

	NIOSendRequest sendRequest = sendMap.remove(new Integer(sendCounter));
	sendRequest.recvCounter = recvCounter;

	s_wcb.clear();
	sLock.signal();

	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	  logger.debug("sendRequest " + sendRequest);
	  logger.debug("sendCounter " + sendCounter);
	  logger.debug("recvCounter " + recvCounter);
	  logger.debug("tag " + tag);
	}

	SocketChannel ch = worldWritableTable.get(sendRequest.dstUUID);
	CustomSemaphore wLock = writeLockTable.get(ch);
	wLock.acquire();

	if (sendRequest == null) {
	  System.out.println("Calling rendezSendData (WRITE_EVENT)");
	  System.out.println("Problem ");
	  System.exit(0);
	}

	rendezSendCtrlMsg(ch, sendRequest);
	int code = rendezSendData(ch, sendRequest);

	if (code != MORE_TO_WRITE) {

	  if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
	    logger.debug("writing complete for <" + sendRequest.tag + ">");
	    logger.debug("notifying " + sendRequest);
	  }
	  // completedList.add(sendRequest);
	  sendRequest.notifyMe();

	} else {

	  System.out.println(" The channel is in blocking mode ");
	  System.out.println(" This shouldn't happen ");
	  System.exit(0);

	}

	wLock.signal();
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
  };

  class CustomSemaphore {

    private int s;

    public CustomSemaphore(int s) {
      this.s = s;
    }

    public synchronized void acquire() throws InterruptedException {
      while (s == 0)
	wait(0);
      s--;
    }

    public synchronized void signal() {
      s++;
      notify();
    }
  }
}
