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
 * File         : Graphcomm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.8 $
 * Updated      : $Date: 2005/09/28 13:33:59 $
 */

package mpi;

public class Graphcomm
    extends Intracomm {
  GraphParms graphParms = null;

  Graphcomm(int[] index, int[] edges, boolean reorder,
            mpjdev.Comm mpjdevComm, mpjdev.Group group) throws MPIException {

    super(mpjdevComm,group) ;

    int rank = group.rank();
    //this.mpjdevComm = mpjdevComm;
    //this.group = new Group(group);
    graphParms = new GraphParms();

    graphParms.index = new int[index.length];
    System.arraycopy(index,0,graphParms.index,0,index.length);
    //graphParms.index = index;
    
    graphParms.edges = new int[edges.length];
    System.arraycopy(edges,0,graphParms.edges,0,edges.length);
    //graphParms.edges = edges;
    //nedges = index[index.length-1];
  }

  /**
   */
  public Object clone() throws MPIException {
    return this.Create_graph(graphParms.index, graphParms.edges, false);
  }

  /**
   * Returns graph topology information.
   * <p>
   * <table>
   * <tr><td><em> returns: </em></td><td> object defining node degress and
   *                                      edges of graph </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GRAPHDIMS_GET</tt>.
   * <p>
   * The number of nodes and number of edges can be extracted
   * from the sizes of the <tt>index</tt> and <tt>edges</tt> fields
   * of the returned object.
   */
  public GraphParms Get() throws MPIException {
    return graphParms;
  }

  /**
   * Provides adjacency information for general graph topology.
   * <p>
   * <table>
   * <tr><td><tt> rank     </tt></td><td> rank of a process in the group
   *                                      of this communicator </tr>
   * <tr><td><em> returns: </em></td><td> array of ranks of neighbouring
   *                                      processes to one specified </tr>
   * </table>
   * <p>
   * Java binding of the MPI operations <tt>MPI_GRAPH_NEIGHBOURS_COUNT</tt>
   * and <tt>MPI_GRAPH_NEIGHBOURS</tt>.
   * <p>
   * The number of neighbours can be extracted from the size of the result.
   */
  public int[] Neighbours(int rank) throws MPIException {
    int nnbrs = graphParms.index[rank];
    int i = 0;
    int p = 0; //index to graphParms edges

    if (rank > 0) {
      i = graphParms.index[rank - 1];
      nnbrs -= i;
      p = i;
    }

    int[] nbrs = new int[nnbrs]; //MPI says there is a need for maxnbrs here
    //but mpiJava seems to get rid of it ..
    //so discuss with Bryan, and if you introduce
    //maxnbrs as an arg, then which ever is lesser
    //(maxnbrs, or nnbrs) would be the size of
    //nbrs array ...

    for (i = 0; i < nnbrs; ++i, ++p) {
      nbrs[i] = graphParms.edges[p];
    }

    return nbrs; //current maxnbrs can be obtained by
    //nbrs.length ...

  }

  /**
   * Compute an optimal placement.
   * <p>
   * <table>
   * <tr><td><tt> index    </tt></td><td> node degrees </tr>
   * <tr><td><tt> edges    </tt></td><td> graph edges </tr>
   * <tr><td><em> returns: </em></td><td> reordered rank of calling
   *                                      process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GRAPH_MAP</tt>.
   * <p>
   * The number of nodes is taken to be size of the <tt>index</tt> argument.
   */
  public int Map(int[] index, int[] edges) throws MPIException {
    int myrank = this.group.Rank();
    //-1 in the next line is MPI.UNDEFINED ...
    return ( (myrank < 0) || (myrank >= index.length)) ? -1 : myrank;
  }

  public int Topo_test() throws MPIException {
    return MPI.GRAPH; //this means no topology ....
  }

}

