import mpjdev.buffertest.*;
import mpi.*;

import mpi.pt2pt.*;
import mpi.pt2pt_ObjSer.*;
import mpi.dtyp.*;
import mpi.dtyp_ObjSer.*;
import mpi.comm.*;
import mpi.group.*;
import mpi.ccl.*;
import mpi.ccl_ObjSer.*;
import mpi.ccl_ObjSer.reduceO.*;
import mpi.ccl_ObjSer.reduce_scatterO.*;
import mpi.ccl_ObjSer.allreduceO.*;
import mpi.ccl_ObjSer.scanO.*;
import mpi.topo.*; 
import mpi.signals.*;
import mpi.env.*;
import mpi.perf.*;
import mpi.threads.*; 
import jgf_mpj_benchmarks.section1.*;
import jgf_mpj_benchmarks.section2.*;
import jgf_mpj_benchmarks.section3.*;

import microbenchmarkmpiJava.allgather.*;
import microbenchmarkmpiJava.alltoall.*;
import microbenchmarkmpiJava.broadcast.*;
import microbenchmarkmpiJava.gather.*;
import microbenchmarkmpiJava.reduce.*;
import microbenchmarkmpiJava.scan.*;
import microbenchmarkmpiJava.allreduce.*;
import microbenchmarkmpiJava.barrier.*;
import microbenchmarkmpiJava.pingpong.Bsend.*;
import microbenchmarkmpiJava.pingpong.Ssend.*;
import microbenchmarkmpiJava.pingpong.Send.*;
import microbenchmarkmpiJava.pingpong.Rsend.*;
import microbenchmarkmpiJava.reducescatter.*;
import microbenchmarkmpiJava.scatter.*;

import xdev.*;

public class TestSuite {
	
  public TestSuite() {
  }

  public static void main(String args[]) throws Exception {      
   
   System.out.print("TestSuite has started ...") ; 
   //dacian_test a = new dacian_test(args); 	  
   //mpi.threads.SimulSend threadsafe1 = new mpi.threads.SimulSend(args);
   //WaitAnyKiller k = new WaitAnyKiller(args) ; 
   //SeqMatrix matrix = new SeqMatrix(args) ; 
   //mpi.threads.ProgressionTest1 threadsafe2 = 
   //	   new mpi.threads.ProgressionTest1 (args);
   //mpi.threads.SimulSendRecv threadsafe3 = 
   //	   new mpi.threads.SimulSendRecv(args);
		                                 
   //InputTester inputTester = new InputTester(args);	  
   //mpi.pt2pt.TestSendInit bug2 = new mpi.pt2pt.TestSendInit(args);
   // Latency la = new Latency(args);	  
   //for(int h=0 ; h<100; h++) { 
   //}
   /*
   xdev.Iwaitany wAny = new xdev.Iwaitany(args); 
   xdev.Isend simplenbcomms = new xdev.Isend(args);	  
   xdev.Send simplecomms = new xdev.Send(args);
   xdev.Issend simplenbscomms = new xdev.Issend(args);	  
   xdev.Ssend simplescomms = new xdev.Ssend(args);
   xdev.Probe simpleprobe = new xdev.Probe(args); 
   xdev.Iprobe simpleiprobe = new xdev.Iprobe(args);
   xdev.Itest simpletest = new xdev.Itest(args); 
   */ 
   // mpi.pt2pt.BufferBench1 bench1 = new mpi.pt2pt.BufferBench1( args );
   // mpi.pt2pt.BufferBench2 bench2 = new mpi.pt2pt.BufferBench2( args );	  
   //mpjdev.perftest.Bandwidth bw = new mpjdev.perftest.Bandwidth(args);	   
   
   //mpi.pt2pt.BufferTest3 bufferTest3 = 
   //	   new mpi.pt2pt.BufferTest3(args); 	  
   //Isend_rendez_fairness f = new Isend_rendez_fairness(args) ;
    //String [] newArgs = new String[args.length + 3];
    //for(int i=0 ; i< args.length ; i++) {
    //    newArgs[i] = args[i] ;
    //}
    //newArgs[args.length ] = "dummy" ;
    //newArgs[args.length + 1] = "--conf" ;
    //newArgs[args.length + 2] = "office/office.accurate.conf.008" ;
    //Mpi2MPI demo = new Mpi2MPI(newArgs) ; 
    //mpjdev.buffertest.MappedBuffer test = 
    //	    new mpjdev.buffertest.MappedBuffer(args) ;
    //BufferRoller roller = new BufferRoller(args);	  

    Long start = System.nanoTime() ; 
    int me = 0;
    int size = -1;
    BreakANYSRC breakIT = new BreakANYSRC( args );
    EnvTester envTester = new EnvTester(args) ; 	 
    ErrStreamPrinter printer = new ErrStreamPrinter(args) ; 
    
    for(int y=0 ; y<10 ; y++) {	  
      //System.out.println("init<"+me+">y<"+y+">");
      MPI.Init(args); 
      me = MPI.COMM_WORLD.Rank(); 
      size = MPI.COMM_WORLD.Size(); 
      MPI.COMM_WORLD.Barrier() ; 
      //System.out.println("mid<"+me+">y<"+y+">");
      MPI.Finalize();
      //System.out.println("end<"+me+">y<"+y+">");
    }

    if(size == 1) { 
      System.out.println("please run the MPJ Express test suite with more"+
                      "number of processes .."); 
      return ; 

    }

    
    for(int i =0 ; i< 1 ; i++) {
    //*****TWO PROCESSESES TESTS *****
    //mpjdev.perftest.Bandwidth bw2 = new mpjdev.perftest.Bandwidth(args);
    
    if(size <= 2) {
      mpjdev.buffertest.BufferTest1 bufferTest1 = 
        new mpjdev.buffertest.BufferTest1(args);                 
      BufferTest2 bufferTest2 = new BufferTest2(args);                
      mpjdev.buffertest.BufferTest3 bufferTest3_ = 
	      new mpjdev.buffertest.BufferTest3(args);  
      BufferTest4 bufferTest4 = new BufferTest4(args);    
      BufferTest5 bufferTest5 = new BufferTest5(args);       
      mpjdev.buffertest.BufferTest6 bufferTest6 = 
        new mpjdev.buffertest.BufferTest6(args);            
      BufferTest7 bufferTest7 = new BufferTest7(args);       
      BufferTest8 bufferTest8 = new BufferTest8(args);      
      BufferTestDyna1 bufferTestDyna1 = new BufferTestDyna1(args);  
      BufferTestDyna2 bufferTestDyna2 = new BufferTestDyna2(args);  
      BufferTestDyna3 bufferTestDyna3 = new BufferTestDyna3(args);  
      BufferTestDyna4 bufferTestDyna4 = new BufferTestDyna4(args);  
      BufferTestDyna5 bufferTestDyna5 = new BufferTestDyna5(args);        
    }
    //MPI point to point communications (basic datatypes).

    SimpleBsend bsend_test = new SimpleBsend(args); 
    bsend_test1 test = new bsend_test1(args);   
    getcount p1 = new getcount(args); 
    iprobe p2 = new iprobe(args);     
    probe p3 = new probe(args);        
    rsend p4 = new rsend(args);                               
    rsend2 p5 = new rsend2(args); 
    sendrecv p6 = new sendrecv(args); 
    sendrecv_rep p7 = new sendrecv_rep(args); 
    seq p8 = new seq(args);  
    ssend p9 = new ssend(args); 
    test1 p10 = new test1(args);   
    test2 p11 = new test2(args);    
    test3 p12 = new test3(args); 
    testall p13 = new testall(args);  //sometimes with 4 processes ...
    testany p14 = new testany(args); 
    testsome p15 = new testsome(args);     
    waitall p16_ = new waitall(args); 
    waitall2 p16 = new waitall2(args);
    waitany p17 = new waitany(args);   
    waitsome p19 = new waitsome(args);
    waitnull p18 = new waitnull(args);  
    wildcard p20 = new wildcard(args); 
    buffer p21 = new buffer(args);  
    isend p22_ = new isend(args);  //FIXME - shmdev hangs ..
    start p23_ = new start(args); 
    startall p24 = new startall(args);  //again same test/wait problem ...
    mpi.pt2pt.BufferTest1 p25 = new mpi.pt2pt.BufferTest1(args);     
    mpi.pt2pt.BufferTest6 p26 = new mpi.pt2pt.BufferTest6(args);  
    SimpleNB p26_ = new SimpleNB(args) ;

    Send_rendez p27 = new Send_rendez(args); 
    Send_rendez_fairness p28 = new Send_rendez_fairness(args); 
    Send_eager_fairness p29 = new Send_eager_fairness(args);
    Bsend_test2 p30 = new Bsend_test2(args);  
    Ssend_test1 p31 = new Ssend_test1(args);   //hangs for 4 processes  
    non_overtaking p32 = new non_overtaking(args);    
    intertwined p33 = new intertwined(args);   
    Isend_rendez p35 = new Isend_rendez(args);     

    //for(int t=0 ; t<100 ; t++) { 
    //MPI point to point communications (Objects).
    rsendO p37 = new rsendO(args);
    bsendO p36 = new bsendO(args);
    test1O p38 = new test1O(args);      
    testanyO p39 = new testanyO(args);
    waitanyO p40 = new waitanyO(args);
    getcountO p41 = new getcountO(args);
    sendrecvO p42 = new sendrecvO(args); 
    ssendO p47 = new ssendO(args);
    test3O p48 = new test3O(args); 
    test2O p43 = new test2O(args); 
    waitsomeO p45 = new waitsomeO(args);
    testallO p52 = new testallO(args);  //hangs for 4 processes ..
    isendO p46 = new isendO(args);   
    testsomeO p49 = new testsomeO(args); 
    rsend2O p50 = new rsend2O(args);    
    startO p51 = new startO(args);   //again same test/wait problem ...
    waitallO p53 = new waitallO(args);         
    //}

    hvec p54 = new hvec(args);
    lbub p55 = new lbub(args);
    type_size p56 = new type_size(args);    
    lbub2 p58 = new lbub2(args); 
    mpi.dtyp.Contiguous p61 = new mpi.dtyp.Contiguous(args);      
    mpi.dtyp.Indexed p62 = new mpi.dtyp.Indexed(args); 
    mpi.dtyp.Vector p63 = new mpi.dtyp.Vector(args);      
    DtypTest p64 = new DtypTest(args); 
    hvecO p65  = new hvecO (args);   
    lbub2O p66 = new lbub2O(args);  
    lbubO p67  = new lbubO (args); 
    //environmental stuff ...
    abort p123 = new abort(args);
    initialized p124 = new initialized(args);
    procname p125 = new procname(args);
    wtime p126 = new wtime(args);    
    //comes from mpiJava.
    //SendTest_Conv2 p122 = new SendTest_Conv2(args);     
    // **** RUN WITH 8 PROCESSES ****	
    comm_test p70 = new comm_test(args);    
    compare p71 = new compare(args); 
    //intercomm_test p72 = new intercomm_test(args);  
    commdup p73 = new commdup(args); 
    CommTest p74 = new CommTest(args);  
    intercomm p75 = new intercomm(args); 
    split2 p77 = new split2(args); 
    Group_com p78 = new Group_com(args);
    Group_incl p79 = new Group_incl(args);
    Group_tranks p80 = new Group_tranks(args);     
    Group_diff p81 = new Group_diff(args);  
    Group_intersect p82 = new Group_intersect(args);
    Group_union p83 = new Group_union(args); 
    Group_excl p84 = new Group_excl(args); 
    Group_self p84_ = new Group_self(args);
    group p85 = new group(args);
    range p86 = new range(args); 	
    bcast p87 = new bcast(args);
    alltoall p88 = new alltoall(args);
    gather p89 = new gather(args);    
    barrier p96 = new barrier(args); 
    reduce p97 = new reduce(args);
    allgather p100 = new allgather(args);
    allreduce p95 = new allreduce(args);
    allgatherv p91 = new allgatherv(args);
    alltoallv p92 = new alltoallv(args);
    gatherv p93 = new gatherv(args);
    scatter p94 = new scatter(args);
     scatterv p98 = new scatterv(args);
     reduce_scatter p101 = new reduce_scatter(args); 
     scan p90 = new scan(args); 
     allgatherO p102 = new allgatherO(args);   	
     alltoallO p103 = new alltoallO(args);
     bcastO p104 = new bcastO(args); 
     alltoallvO p105 = new alltoallvO(args);
     gatherO p106 = new gatherO(args);    
     allgathervO p109 = new allgathervO(args);
     gathervO p110 = new gathervO(args);
     scatterO p111 = new scatterO(args);
     scattervO p112 = new scattervO(args); 
     cart_topo p119 = new cart_topo(args);
     graph p120 = new graph(args); 
     sub2 p121 = new sub2(args); 
     sub p118 = new sub(args);  
     map p117 = new map(args); 
     split p76 = new split(args); 
     cart p115 = new cart(args);
     pack p59 = new pack(args);   
     packO p68  = new packO (args); 
     zero5 p57 = new zero5(args); 
     zero1 p60 = new zero1(args); 
     reduceO p107 = new reduceO(args);
     allreduceO p108 = new allreduceO(args); 
     reduce_scatterO p113 = new reduce_scatterO(args);
     scanO p114 = new scanO(args);  
     reduce2 p99_ = new reduce2(args) ;	
     allreduce_maxminloc p99 = new allreduce_maxminloc(args);  
     dimscreate p116 = new dimscreate(args); 

     Long stop = System.nanoTime(); 
     Long time = (stop - start)/1000000000 ; 
 
     if( me == 0) { 
       System.out.println("**********************************************");
       System.out.println("***** TestSuite finished in "+time+" secs*****"); 
       System.out.println("**********************************************");  
     }
     }
     
    /*

     // 2. this test is crazy.
     //attr p69 = new attr(args); 
     // 4. cancel not implemented.
     //cancel1 p127 = new cancel1(args); 
     
     //FORGET THESE FOR THE TIME BEING.
     //System.out.println(" *********************** ");
     //System.out.println(" test ends <"+j+">");
     //System.out.println(" *********************** ");
     
     //********************************
     //* MPJ benchmarks by JavaGrande 
     //********************************
     //JGFAll p128 = new JGFAll(args); 
     JGFAlltoallBench p148 = new JGFAlltoallBench(args);
     JGFBarrierBench p149 = new JGFBarrierBench(args); 
     JGFBcastBench p150 = new JGFBcastBench(args);   
     JGFGatherBench p151 = new JGFGatherBench(args);
     //JGFPingPongBench p152 = new JGFPingPongBench(args);
     JGFReduceBench p153 = new JGFReduceBench(args);
     JGFScatterBench p154 = new JGFScatterBench(args); 
  String [] ar = MPI.Init(args);
  MPI.Finalize(); 
  String whichTest = ar[0] ; 
  System.out.println(" running test <"+whichTest+">"); 

  if(whichTest.equals("2a")) { 	  
  jgf_mpj_benchmarks.section2.JGFAllSizeA p129 =  
      new jgf_mpj_benchmarks.section2.JGFAllSizeA(args); 
  } else if(whichTest.equals("2b")) { 
   jgf_mpj_benchmarks.section2.JGFAllSizeB p130 = 
       new jgf_mpj_benchmarks.section2.JGFAllSizeB(args);
     //passed.
  }
  else if(whichTest.equals("2c")) { 
  jgf_mpj_benchmarks.section2.JGFAllSizeC p131 = 
	     new jgf_mpj_benchmarks.section2.JGFAllSizeC(args); 
  }
  else if(whichTest.equals("3a")) {
   jgf_mpj_benchmarks.section3.JGFAllSizeA p132 = 
         new jgf_mpj_benchmarks.section3.JGFAllSizeA(args); 
    //1. changed MPI.DOUBLE to MPI.INT in montecarlo code ..
    //2. this also uses hitData file ..and its been hard-coded to read 
    //from /conf/hitData ..assuming that the code is started from 
    //$mpj_home/bin dir.
    //3. it went out of memory, so gave it a gig of memory.
  }
  else if(whichTest.equals("3b")) { 
     jgf_mpj_benchmarks.section3.JGFAllSizeB p133 = 
     new jgf_mpj_benchmarks.section3.JGFAllSizeB(args);
     //passed.
  }
*/  
/*     
     //************************************** 
     //* Guillermo Taboada's micro-benchmarks
     //**************************************
     microbenchmarkmpiJava.allgather.Allgather p134 = new 
          microbenchmarkmpiJava.allgather.Allgather(args); 
	     
     microbenchmarkmpiJava.alltoall.Alltoall p135 = new 
          microbenchmarkmpiJava.alltoall.Alltoall(args);
	     
     microbenchmarkmpiJava.broadcast.Broadcast p136 = new 
          microbenchmarkmpiJava.broadcast.Broadcast(args); 
	     
     microbenchmarkmpiJava.gather.Gather p137 = new 
          microbenchmarkmpiJava.gather.Gather(args);
	     
     microbenchmarkmpiJava.reduce.Reduce p138 = new 
          microbenchmarkmpiJava.reduce.Reduce(args); 
     
     microbenchmarkmpiJava.scan.Scan p139 = new 
          microbenchmarkmpiJava.scan.Scan(args);
	     
     microbenchmarkmpiJava.allreduce.Allreduce p140 = new 
          microbenchmarkmpiJava.allreduce.Allreduce(args); 
	     
     microbenchmarkmpiJava.barrier.Barrier p141 = new 
          microbenchmarkmpiJava.barrier.Barrier(args); 
	     
     microbenchmarkmpiJava.pingpong.Bsend.PingPong p142 = new 
          microbenchmarkmpiJava.pingpong.Bsend.PingPong(args); 
	     
     microbenchmarkmpiJava.pingpong.Ssend.PingPong p143 = new 
          microbenchmarkmpiJava.pingpong.Ssend.PingPong(args); 
	     
     microbenchmarkmpiJava.pingpong.Send.PingPong p144 = new 
          microbenchmarkmpiJava.pingpong.Send.PingPong(args); 
	     
     microbenchmarkmpiJava.pingpong.Rsend.PingPong p145 = new 
          microbenchmarkmpiJava.pingpong.Rsend.PingPong(args); 
	     
     microbenchmarkmpiJava.reducescatter.Reduce_scatter p146 = new 	     
         microbenchmarkmpiJava.reducescatter.Reduce_scatter(args); 
 //        Arrayoutofbound exceptions.
     
     microbenchmarkmpiJava.scatter.Scatter p147 = new 
          microbenchmarkmpiJava.scatter.Scatter(args); 
*/  
  
    }   
  }
	  
//}
