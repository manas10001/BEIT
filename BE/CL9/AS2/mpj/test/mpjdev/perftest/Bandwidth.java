package mpjdev.perftest;

import mpjdev.*;
import mpjbuf.*;
import java.io.*; 

public class Bandwidth {

  public Bandwidth() {
  }

  public Bandwidth(String[] args) throws Exception {

    MPJDev.init(args);
    int SEND_OVERHEAD = MPJDev.getSendOverhead() ; 
    int RECV_OVERHEAD = MPJDev.getRecvOverhead() ;
    Buffer buffer = null;
    Buffer rbuffer = null ; 
    byte[] byteArray = new byte[16*1024*1024] ;
    int WARM_UP = 10000 ;
    int REPEAT = 20000 ;
    int j = 1, i = 0;
    int LOG2N_MAX = 1000000, log2nbyte = 0, padding = 0;
    PrintStream out = null ;
    FileOutputStream fos = null ;
    long[] delays = new long[REPEAT];
    long[] latency = new long[REPEAT];
    long start= 0L, stop=0L, init = 0L;

    int me = MPJDev.WORLD.id() ; 

    if(me == 1) { 
      fos = new FileOutputStream("bw_delays.out");
      out = new PrintStream (fos);
    } 
    else { 
      fos = new FileOutputStream("bw.out");
      out = new PrintStream (fos);
    }

    /* Logrithmic Loop */
    for (log2nbyte = 0; log2nbyte <= LOG2N_MAX && j < 64*1024 ;
		    ++log2nbyte) {
	    
      j = (1 << log2nbyte);
	
      if ( ( (j / 8) > 1) && j % 8 != 0) {
        padding = j % 8;
      }
      else if ( ( (j / 8) == 0)) {
        padding = j;
      }
      else {
        padding = 0;
      }

      buffer = new Buffer(
                   BufferFactory.create(8+j+padding+SEND_OVERHEAD), 
		   SEND_OVERHEAD, 8+j+padding+SEND_OVERHEAD );
      buffer.putSectionHeader(Type.BYTE);
      buffer.write(byteArray, 0, j);
      buffer.commit();
      
      rbuffer = new Buffer( BufferFactory.create(8+j+padding+RECV_OVERHEAD),
			    RECV_OVERHEAD, 8+j+padding+RECV_OVERHEAD );

      /* Warm Up Loop */
      for (i = 0; i < WARM_UP; i++) {
        if (MPJDev.WORLD.id() == 0) {
          MPJDev.WORLD.recv(rbuffer, 1, j, true);
          MPJDev.WORLD.send(buffer, 1, j, true);
        }
        else if (MPJDev.WORLD.id() == 1) {
          MPJDev.WORLD.send(buffer, 0, j, true);
          MPJDev.WORLD.recv(rbuffer, 0, j, true);
        }
      }

     /* Latency Calculation Loop */
        for (i = 0; i < REPEAT; i++) {
          if (MPJDev.WORLD.id() == 0) {
            myDelay( (int) (Math.random() * 1000)) ;
            init = System.nanoTime() ;
            MPJDev.WORLD.send(buffer, 1, j, true);
            MPJDev.WORLD.recv(rbuffer, 1, j, true);
            latency[i] = ((System.nanoTime() - init)/1000) ;
          }
          else if (MPJDev.WORLD.id() == 1) {
            MPJDev.WORLD.recv(rbuffer, 0, j, true);
            start = System.nanoTime() ;
            myDelay( (int)(Math.random() * 1000));
            delays[i] = (System.nanoTime() - start)/1000 ;
            MPJDev.WORLD.send(buffer, 0, j, true);
          }
        }

	BufferFactory.destroy( buffer.getStaticBuffer());
	BufferFactory.destroy( rbuffer.getStaticBuffer());

        if(me == 0) {
          for(int k=WARM_UP; k<REPEAT ; k++) {
            out.println(latency[k]+"   ");
          }
        }

        else {
          for (i = WARM_UP ; i < REPEAT ; i++) {
            out.println(delays[i]+"   ");
          }
        }

        MPJDev.WORLD.barrier() ;
      } //end logrithmic loop

      MPJDev.finish();
  }

  static double tripsPerMS = 1000000 ;
  static int dummy ;

  static void myDelay(int us) {

    int trips = (int) (tripsPerMS * us) ;
    long start = System.nanoTime() ;

    for(int i = 0 ; i < trips ; i++) {
      dummy ++ ;
    }

    long actualDelay = System.nanoTime() - start ;

    if(actualDelay > 0 ) {
      long newTripsPerMS = (trips * 1000 ) / actualDelay ;

      if(newTripsPerMS > 0) {
        tripsPerMS = newTripsPerMS ;
      }
    } 
  } 
}
