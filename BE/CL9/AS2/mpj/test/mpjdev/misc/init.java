import mpjdev.*;
import mpjbuf.*;
import java.util.Arrays;

public class init {

  public static void main(String args[]) throws Exception {
    NIODevice dev = (NIODevice) Device.newInstance();
    long t1 = System.nanoTime();
    ProcessID[] ids = dev.init(args);
    ProcessID myID = dev.myID();

    if (myID.rank() == 0) {
      System.out.print("\n" + myID.rank() + ">su-time<" +
                       (System.nanoTime() - t1) / (1000 * 1000 * 1000) + ">");
      System.out.println("myID " + myID);
      System.out.println("rank " + myID.rank());
      System.out.println("uuid " + myID.uuid());

      for (int i = 0; i < ids.length; i++) {
        System.out.println("\n -----<" + i + ">------");
        System.out.print("ids[" + i + "]=>" + ids[i] + "\t");
        System.out.print("rank " + ids[i].rank() + "\t");
        System.out.println("uuid " + ids[i].uuid());
      }
      System.out.print("init<" + myID.rank() + ">\n");
    }
    dev.finish();
  }
}
