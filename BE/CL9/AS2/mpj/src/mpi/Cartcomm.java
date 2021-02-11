/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : Cartcomm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.11 $
 * Updated      : $Date: 2005/09/28 13:33:59 $
 */

package mpi;

public class Cartcomm
    extends Intracomm {
  CartParms cartParms = null;

  //so the index of this cartParms will be the rank of that process

  Cartcomm(int[] dims, boolean[] periods, boolean reorder,
           mpjdev.Comm mpjdevComm, mpjdev.Group group) throws MPIException {
    super(mpjdevComm,group) ;

    int rank = group.rank();

    //this.mpjdevComm = mpjdevComm;
    //this.group = new Group(group);

    cartParms = new CartParms();

    /* 
     * Bug: Using 'copying by reference' technique to store dims and 
     * periods array in cartParms object. This is detrimental because if 
     * the value of dims or periods array is changed later, it also
     * changes the value of stored dims or periods array.
     * Identified by: Andre Vehreschild
     * Fixed: Wed Sep 28 12:30:16 GMT 2005
     */
    cartParms.dims = new int[dims.length];
    System.arraycopy(dims,0,cartParms.dims,0,dims.length);
    //cartParms.dims = dims;
    
    cartParms.periods = new boolean[periods.length];
    System.arraycopy(periods,0,cartParms.periods,0,cartParms.periods.length);
    //cartParms.periods = periods;

    try {
      cartParms.coords = Coords(rank);
    }
    catch (Exception e) {
      throw new MPIException(e); 	    
    }
  }

  /**
   */
  public Object clone () throws MPIException {
    return this.Create_cart(cartParms.dims, cartParms.periods, false);
  }

  /**
   * Translate logical process coordinates to process rank.
   * <p>
   * <table>
   * <tr><td><tt> coords   </tt></td><td> Cartesian coordinates of a
   *                                      process </tr>
   * <tr><td><em> returns: </em></td><td> rank of the specified process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_RANK</tt>.
   */
  public int Rank(int[] coords) throws MPIException {
    int factor = 1;
    int rank = 0;
    int i = cartParms.dims.length - 1;
    int d = i; //d is basically dims index ....
    int c = i; //c is coords index ...
    int ord = 0;
    int dim = 0;

    for (; i >= 0; --i, --c, --d) {
      dim = (cartParms.dims[i] > 0) ? cartParms.dims[i] :
          - (cartParms.dims[i]);
      ord = coords[c];

      if ( (ord < 0) || (ord >= dim)) {

        if (dim > 0) {
          System.out.println("Error ");
        }
        
	System.out.println("ord "+ord); 
	System.out.println("dim "+dim); 

        ord %= dim;

        if (ord < 0) {
          ord += dim;
        }
      }

      rank += factor * ord;
      factor *= dim;
    }

    return rank;
  }

  /**
   * Translate process rank to logical process coordinates.
   * <p>
   * <table>
   * <tr><td><tt> rank     </tt></td><td> rank of a process </tr>
   * <tr><td><em> returns: </em></td><td> Cartesian coordinates of the
   *                                      specified process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_COORDS</tt>.
   */
  public int[] Coords(int rank) throws MPIException {
    int dim = 0;
    int nprocs = group.Size();
    int remprocs = nprocs;
    int orank = rank; //just for debugging
    int[] coords = new int[cartParms.dims.length];

    for (int i = 0; i < cartParms.dims.length; ++i) {
      /*
             dim = (cartParms.dims[i] > 0) ? cartParms.dims[i] :
                               -(cartParms.dims[i]); */
      dim = cartParms.dims[i];
      remprocs /= dim;
      coords[i] = rank / remprocs;
      rank %= remprocs;
    }

    /* temp printing ...
    StringBuffer buffer = new StringBuffer();
    buffer.append("rank[" + orank + "]=");
    for (int i = 0; i < cartParms.dims.length; i++) {
      buffer.append("<coords[" + i + "]=" + coords[i] + ">");
    }
    //System.out.println(buffer.toString());
    temp printing ...*/

    return coords;
  }

  /**
   * Compute source and destination ranks for ``shift'' communication.
   * <p>
   * <table>
   * <tr><td><tt> direction </tt></td><td> coordinate dimension of shift </tr>
   * <tr><td><tt> disp      </tt></td><td> displacement </tr>
   * <tr><td><em> returns:  </em></td><td> object containing ranks of source
   *                                       and destination processes </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_SHIFT</tt>.
   */
  public ShiftParms Shift(int direction, int disp) throws MPIException {

    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug(" Shift method called with args");
      MPI.logger.debug(" direction "+direction);
      MPI.logger.debug(" disp "+disp);
    }
    
    ShiftParms sParms = new ShiftParms();
    int factor = 0;
    int thisdim = 0;
    int thisperiod = 0;
    int ord = 0;
    int srcord = 0;
    int destord = 0;
    int i = 0;
    int p = 0;
    int rank_src = 0;
    int rank_dst = 0;

    ord = this.group.Rank();

    /*
     * Naive case ...
     */
    if (disp == 0) {
      rank_src = ord;
      rank_dst = ord;
      sParms.rank_source = rank_src;
      sParms.rank_dest = rank_dst;
      return sParms;
    }

    /* this is MPICH cloning */
    boolean periodic = cartParms.periods[direction] ;
    //save position ..dont understand this ...  
    int saved_position = cartParms.coords[direction] ; 
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug("saved_position "+saved_position);
    }
    int dest_position, source_position ;
    dest_position = source_position = saved_position ; 

    //Shift for the destination ...
    dest_position += disp ;
    
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug("dest_position "+dest_position);
    }

    if(dest_position >= cartParms.dims[direction] ) {
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("dest_position is >= dims[dir]");
      }
      if ( periodic )
         dest_position %= cartParms.dims[direction];
      else
         dest_position = -1;

      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("dest_position "+dest_position); 
      }
    }
    else if(dest_position < 0) {
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("dest_position is < 0");
      }
      if ( periodic )
        dest_position += cartParms.dims[direction];
      else
        dest_position = -1;
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("dest_position "+dest_position); 
      }
    }

    cartParms.coords[direction] = dest_position ; 

    if(dest_position != -1) {
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("cartParms.coords[0]=" + cartParms.coords[0] );
        MPI.logger.debug("cartParms.coords[1]=" + cartParms.coords[1] );
      }
      rank_dst = Rank(cartParms.coords);
    }
    else {
      rank_dst = -1;  
    } 

    //Shift for the source ..
    source_position -= disp;
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug("source_position "+source_position);
    }

    if ( source_position >= cartParms.dims[direction] ) {
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("source_position >= cartParms.coords "); 
      }
      if ( periodic ) {
          source_position %= cartParms.dims[direction];
      }
      else
        source_position = -1;
    }
    else if ( source_position < 0 ) {
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("source_position < 0"); 
      }
      if ( periodic ) {
        source_position += cartParms.dims[direction];
        //source_position = (source_position*-1); 
      }
      else
        source_position = -1;
    }
    
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug("source_position "+source_position);
    }
    cartParms.coords[direction] = source_position;

    if(source_position != -1) {

      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
        MPI.logger.debug("cartParms.coords[0]=" + cartParms.coords[0] );
        MPI.logger.debug("cartParms.coords[1]=" + cartParms.coords[1] );
      }

      rank_src = Rank(cartParms.coords);
    }
    else {
      rank_src = -1;
    }

    
    //Restore my position ..
    cartParms.coords[direction] = saved_position ; 
    
    /* MPICH cloning finishes */
/*
 *  LAM/MPI cloning ...
    factor = this.group.Size(); //number of processses ..
    p = 0; //index to dims array ...

    for (i = 0; (i < cartParms.dims.length)
         && (i <= cartParms.dims[i]); ++i, ++p) {

      if(cartParms.periods[p] == false ) {
          thisperiod = 0;
          thisdim = cartParms.dims[p];
      }else {
          thisperiod = 1;
          thisdim = - (cartParms.dims[p]);
      }
      // the above code should be able to replace the below code ..

      //if ( (thisdim = cartParms.dims[p]) > 0) {
      //  thisperiod = 0;
      //}
      //else {
      //  thisperiod = 1;
      //  thisdim = -thisdim;
      //}

      ord %= factor;
      factor /= thisdim;
    }

    ord /= factor;

    srcord = ord - disp;
    destord = ord + disp;

    if ( ( (destord < 0 || destord >= thisdim)) && (! (thisperiod == 1))) {
     System.out.println("setting rank_dst to -1");	    
      rank_dst = -1; //actually MPI_PROC_NULL
    }
    else {
      destord %= thisdim;
      if (destord < 0) destord += thisdim;
      rank_dst = this.group.Rank() + ( (destord - ord) * factor);
    }

    if ( ( (srcord < 0) || (srcord >= thisdim)) && (! (thisperiod == 1))) {
     System.out.println("setting rank_src to -1");	    
      rank_src = -1; //actually MPI_PROC_NULL;
    }
    else {
      srcord %= thisdim;
      if (srcord < 0) srcord += thisdim;
      rank_src = this.group.Rank() + ( (srcord - ord) * factor);
    }
*/

    sParms.rank_source = rank_src;
    sParms.rank_dest = rank_dst;

    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) { 
      MPI.logger.debug(" end of shift ");
      MPI.logger.debug(" rank_src "+rank_src);
      MPI.logger.debug(" rank_dst "+rank_dst);
    }
    
    return sParms;
  }

  /**
   * Partition Cartesian communicator into subgroups of lower dimension.
   * <p>
   * <table>
   * <tr><td><tt> remain_dims </tt></td><td> by dimension, <tt>true</tt> if
   *                                         dimension is to be kept,
   *                                         <tt>false</tt> otherwise </tr>
   * <tr><td><em> returns:    </em></td><td> communicator containing subgrid
   *                                         including this process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_SUB</tt>.
   */
  public Cartcomm Sub(boolean[] remain_dims) throws MPIException {
    int color = 0;
    int key = 0;
    int keyfactor = 1;
    int colfactor = 1;

    int ndim = 0;
    int dim = 0;
    int allfalse = 0;
    int i = cartParms.dims.length - 1;

    for (; i >= 0; --i) {
      /*dim = ( cartParms.dims[i] > 0 ) ? cartParms.dims[i] :
           (-cartParms.dims[i]);*/
      dim = cartParms.dims[i];

      if (remain_dims[i] == false) {
        color += colfactor * cartParms.coords[i];
        colfactor *= dim;
      }
      else {
        ++ndim;
        key += keyfactor * (cartParms.coords[i]);
        keyfactor *= dim;
      }
    }

    /* special case ...comm with just yourself 'this' process init */
    if (ndim == 0) {
      color = this.group.Rank();
      ndim = 1;
      allfalse = 1;
      //MPICH makes a dup of MPI_COMM_SELF and returns it.
    }

    /* split the communicator */
    Intracomm newcomm = this.Split(color, key);

    /* fill the comm with topology information */
    /* and compute the callers coordinate ... */
    if (newcomm != null) {
      //get total number of processes in this comm ...
      int nprocs = newcomm.Size();
      int[] ndimsArray = new int[ndim];
      boolean[] nperiods = new boolean[ndim];
      /*
        if(nprocs != ndim) {
          System.out.println("nprocs is not equal to ndim, ERROR");
          System.out.println("nprocs "+nprocs);
          System.out.println("ndim "+ndim);
        }*/

      int index = 0;

      if(allfalse == 1 ) {
        ndimsArray[0] = 1;	      
      } else {
       for (int l = 0; l < cartParms.dims.length; l++) {
         if (remain_dims[l] == true) {
           ndimsArray[index] = cartParms.dims[l];
           nperiods[index] = cartParms.periods[l];
           index++;
         }
       }
      }

      return newcomm.Create_cart(ndimsArray, nperiods, false);
    }
    else {
      return null;
    }
  }

  /**
   * Compute an optimal placement.
   * <p>
   * <table>
   * <tr><td><tt> dims     </tt></td><td> the number of processes in each
   *                                      dimension </tr>
   * <tr><td><tt> periods  </tt></td><td> <tt>true</tt> if grid is periodic,
   *                                      <tt>false</tt> if not, in each
   *                                      dimension </tr>
   * <tr><td><em> returns: </em></td><td> reordered rank of calling
   *                                      process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_MAP</tt>.
   * <p>
   * The number of dimensions is taken to be size of the <tt>dims</tt> argument.
   */
  public int Map(int[] dims, boolean[] periods) throws MPIException {
    int procs = 1;

    for (int i = 0; i < dims.length; i++) {
      if (dims[i] < 0) {
        throw new MPIException(" Error in Cartcomm.Map: dims["+i+"] is "+
			"less than zero" ); 
      }
      procs *= dims[i];
    }

    int size = this.group.Size();
    int rank = this.group.Rank();

    if (procs > size) {
      throw new MPIException(" Error in Cartcomm.Map: procs <"+procs+"> is "+
		      "greater than size <"+size+">");
    }

    return ( (rank < 0) || (rank >= procs)) ? -1 : rank; //-1 should UNDEF ...
  }

  /**
   * Returns Cartesian topology information.
   * <p>
   * <table>
   * <tr><td><em> returns: </em></td><td> object containing dimensions,
   *                                      periods and local coordinates </tr>
   * </table>
   * <p>
   * Java binding of the MPI operations <tt>MPI_CARTDIM_GET</tt> and
   * <tt>MPI_CART_GET</tt>.
   * <p>
   * The number of dimensions can be obtained from the size of (eg)
   * <tt>dims</tt> field of the returned object.
   */
  public CartParms Get() throws MPIException {
    return cartParms;
  }

  public int Topo_test() throws MPIException {
    return MPI.CART; //this means no topology ....
  }

  /**
   * Select a balanced distribution of processes per coordinate direction.
   * <p>
   * <table>
   * <tr><td><tt> nnodes   </tt></td><td> number of nodes in a grid </tr>
   * <tr><td><tt> ndims    </tt></td><td> number of dimensions of grid </tr>
   * <tr><td><tt> dims     </tt></td><td> array specifying the number of nodes
   *                                      in each dimension </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_DIMS_CREATE</tt>.
   * <p>
   * Size <tt>dims</tt> should be <tt>ndims</tt>.  Note that
   * <tt>dims</tt> is an <em>inout</em> parameter.
   */
  public static void Dims_create(int nnodes, int[] dims) throws MPIException {

    //int size = 1 ;
    int rank = dims.length ;

    while(rank > 0) {

      int ext = root(nnodes, rank) ;
      rank-- ;
      nnodes /= ext ;
      dims [rank] = ext ;
      //size       *= ext ;
    }
  }

  static int root(int n, int d) {

        switch(d) {
        case 1 :
            return n ;
        case 2 :
            return (int) Math.sqrt(n) ;
        default :
            int ceiling  = 1 ;
            int power    = 1 ;
            while(power < n) {
                ceiling++  ;
                power = ceiling ;
                for(int i = 1 ; i < d ; i++)
                    power *= ceiling ;
            }
            if(power == n)
                return ceiling ;
            else
                return ceiling - 1 ;
        }
  }


}

