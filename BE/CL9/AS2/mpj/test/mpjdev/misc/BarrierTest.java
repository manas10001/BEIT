import mpjdev.*;

public class BarrierTest {

  public BarrierTest(String args[]) throws Exception {

    int REPEAT_TIME = 1;
    Comm.init(args);

    int LOG2N_MAX = 1000000;

    for (int j = 0; j <= REPEAT_TIME; j++) {

      if (Comm.WORLD.id() == 0) {
        Comm.WORLD.nbarrier();
      }
      else {
        //try { Thread.currentThread().sleep(1000*Comm.WORLD.id()); }catch(Exception e){}
        Comm.WORLD.nbarrier();
      }
    }

    System.out.print("Finish <" + Comm.WORLD.id() + ">");
    Comm.finish();

  }

  public static void main(String args[]) throws Exception {
    BarrierTest test = new BarrierTest(args);
  }

}
