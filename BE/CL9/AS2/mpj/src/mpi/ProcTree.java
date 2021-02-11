/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

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
 * File         : ProcTree.java
 * Author       : Sang Lim, Bryan Carpenter
 * Created      : Thu Dec 20 16:39:43 BST 2001
 * Revision     : $Revision: 1.6 $
 * Updated      : $Date: 2005/08/06 15:21:36 $
 */

//package hpjava.lang;
package mpi;

public class ProcTree {

  public static final int PROCTREE_A = 4;
  public int numChildren;
  public int[] child = new int[PROCTREE_A];
  public int parent;
  public int root;
  public boolean isRoot;

  public ProcTree() {
    isRoot = false; //it was set to true ...
    numChildren = -1;
    for (int i = 0; i < child.length; i++) {
      child[i] = -1;
    }
    root = -1;
    parent = -1;
  }

  public static void main(String args[]) {
    //ProcTree main method ...
    ProcTree procTree = null;

    if (args.length < 3) {
      System.out.println("three arguments ...");
      System.out.println("rank, size, root");
      return;
    }

    int index = Integer.parseInt(args[0]);
    int extent = Integer.parseInt(args[1]);
    int root = Integer.parseInt(args[2]);
    procTree = new ProcTree();
    int places = ProcTree.PROCTREE_A * index;

    for (int i = 1; i <= ProcTree.PROCTREE_A; i++) {
      ++places;
      int ch = (ProcTree.PROCTREE_A * index) + i + root;
      System.out.println("places " + places);
      ch %= extent;

      if (places < extent) {
        System.out.println("ch <" + i + ">" + "=<" + ch + ">");
        System.out.println("adding to the tree at index <" + (i - 1) + ">\n\n");
        procTree.child[i - 1] = ch;
        procTree.numChildren++;
      }
      else {
        System.out.println("not adding to the tree");
      }

      //places = index*ProcTree.PROCTREE_A +i;
    }

    System.out.println("procTree.numChildren <" + procTree.numChildren + ">");

    if (index == root)
      procTree.isRoot = true;
    else {
      procTree.isRoot = false;
      int pr = (index - 1) / ProcTree.PROCTREE_A;
      procTree.parent = pr;
    }

    procTree.root = root;

    for (int i = 0; i < procTree.PROCTREE_A; i++) {
      System.out.print(" child[" + i + "]=>" + procTree.child[i]);
    }

    System.out.println("  ------- End --------");
    //ProcTree main method ...
  }

}
