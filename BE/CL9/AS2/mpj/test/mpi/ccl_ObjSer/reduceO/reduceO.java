package mpi.ccl_ObjSer.reduceO;

import mpi.ccl_ObjSer.test;
import mpi.*;
import java.io.*;

class complexNum implements Serializable {
  complexNum() {
  }

  float realPart;
  float imaginPart;
}

class complexAdd extends User_function {
  complexAdd() {
  }

  public void Call(Object invec, int inoffset, Object outvec, int outoffset,
      int count, Datatype datatype) {
    Object[] in_array = (Object[]) invec;
    Object[] out_array = (Object[]) outvec;

    for (int i = 0; i < count; i++) {
      complexNum ocd = (complexNum) out_array[outoffset + i];
      complexNum icd = (complexNum) in_array[inoffset + i];

      ocd.realPart += icd.realPart;
      ocd.imaginPart += icd.imaginPart;
    }
  }
}

public class reduceO {
  static public void main(String[] args) throws Exception {
    try {
      reduceO c = new reduceO(args);
    }
    catch (Exception e) {
    }
  }

  public reduceO() {
  }

  public reduceO(String[] args) throws Exception {
    final int MAXLEN = 100;

    int root, i, j, k;
    complexNum out[] = new complexNum[MAXLEN];
    complexNum in[] = new complexNum[MAXLEN];
    int myself, tasks;
    boolean bool = false;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    root = tasks / 2;

    for (i = 0; i < MAXLEN; i++) {
      in[i] = new complexNum();
      out[i] = new complexNum();
      out[i].realPart = i;
      out[i].imaginPart = i;
    }
    complexAdd cadd = new complexAdd();
    Op op = new Op(cadd, bool);
    MPI.COMM_WORLD.Reduce(out, 0, in, 0, MAXLEN, MPI.OBJECT, op, root);

    if (myself == root) {
      for (k = 0; k < MAXLEN; k++) {
	if (in[k].realPart != k * tasks) {
	  System.out.println("bad answer (" + (in[k].realPart) + ") at index "
	      + k + "(should be " + (k * tasks) + ")");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("ReduceO TEST COMPLETE");
    MPI.Finalize();
  }
}
