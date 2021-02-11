package mpi.env;

import mpi.*;
import java.util.Map;

public class EnvTester {
  static public void main(String[] args) throws Exception {
    try {
      EnvTester c = new EnvTester(args);
    }
    catch (Exception e) {
    }
  }

  public EnvTester() {
  }

  public EnvTester(String[] args) throws Exception {
    int me, size;
    String[] nargs = MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (me == 0) {
      System.out.println("-----");
      System.out.println("printing only at process 0 ");
      System.out.println("-----");

      Map<String, String> map = System.getenv();
      String pwd = map.get("PWD");
      System.out.println(" pwd: <" + pwd + ">");

      String[] key = { "java.version", "java.vendor", "java.class.version",
	  "os.name", "os.arch", "os.version", "file.separator",
	  "path.separator", "line.separator", "test_prop_1", "test_prop_2",
	  "user.dir" };

      for (int i = 0; i < key.length; i++) {
	System.out.println(i + ". " + key[i] + " = "
	    + System.getProperty(key[i]));
      }

      System.out.println("-----");
      System.out.println(" Test MPJ application parameters");
      System.out.println(" args.length is <" + args.length + ">");

      for (int j = 0; j < args.length; j++) {
	System.out.println(" args [" + j + "] == " + args[j]);
      }

      System.out.println("-----");
      System.out.println(" nargs.length is <" + nargs.length + ">");

      for (int j = 0; j < nargs.length; j++) {
	System.out.println(" nargs [" + j + "] == " + nargs[j]);
      }

      System.out.println("-----");

    }

    // do whatever you wanne do here ...

    if (me == 0)
      System.out.println("EnvTester TEST COMPLETE\n");
    MPI.Finalize();
  }
}
