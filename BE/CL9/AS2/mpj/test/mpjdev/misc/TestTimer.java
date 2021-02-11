import timer.NativeTimer;

public class TestTimer {

  public static void main(String[] args) {

    System.out.println("#Making timer");
    NativeTimer microTimer = new NativeTimer();
    System.out.println("#Native timer online");

    double c = microTimer.getMicroseconds();
    System.out.println(c);

  }
}
