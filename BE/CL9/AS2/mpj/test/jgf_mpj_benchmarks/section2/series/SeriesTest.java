/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                  Original version of this code by                       *
*                 Gabriel Zachmann (zach@igd.fhg.de)                      *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/

/**
* Class SeriesTest
*
* Performs the transcendental/trigonometric portion of the
* benchmark. This test calculates the first n fourier
* coefficients of the function (x+1)^x defined on the interval
* 0,2 (where n is an arbitrary number that is set to make the
* test last long enough to be accurately measured by the system
* clock). Results are reported in number of coefficients calculated
* per sec.
*
* The first four pairs of coefficients calculated shoud be:
* (2.83777, 0), (1.04578, -1.8791), (0.2741, -1.15884), and
* (0.0824148, -0.805759).
*/
package jgf_mpj_benchmarks.section2.series;

//package series; 
import jgf_mpj_benchmarks.jgfutil.*; 
import mpi.*;

class SeriesTest 
{

// Declare class data.


int array_rows; 
int p_array_rows; 
int ref_p_array_rows; 
int rem_p_array_rows; 
double [] [] p_TestArray = null;  // Array of arrays on each process.
double [] [] TestArray = null;  // Array of arrays.


/*
* buildTestData
*
*/

// Instantiate array(s) to hold fourier coefficients.

void buildTestData()
{
    // Allocate appropriate length for the double array of doubles.

    if(JGFSeriesBench.rank==0) {
      TestArray = new double [2][array_rows];
    }
    p_TestArray = new double [2][p_array_rows];
}



/*
* Do
*
* This consists of calculating the
* first n pairs of fourier coefficients of the function (x+1)^x on
* the interval 0,2. n is given by array_rows, the array size.
* NOTE: The # of integration steps is fixed at 1000. 
*/

void Do() throws MPIException
{
    double omega;       // Fundamental frequency.
    int ilow;
//System.out.println(" Do "+JGFSeriesBench.rank);

    if(JGFSeriesBench.rank==0) {
      // Start the stopwatch.
      JGFInstrumentor.startTimer("Section2:Series:Kernel"); 

      // Calculate the fourier series. Begin by calculating A[0].

      TestArray[0][0]=TrapezoidIntegrate((double)0.0, // Lower bound.
                              (double)2.0,            // Upper bound.
                              1000,                    // # of steps.
                              (double)0.0,            // No omega*n needed.
                              0) / (double)2.0;       // 0 = term A[0].
    }
 
//System.out.println(" Do(1) "+JGFSeriesBench.rank);
    // Calculate the fundamental frequency.
    // ( 2 * pi ) / period...and since the period
    // is 2, omega is simply pi.

    omega = (double) 3.1415926535897932;

    if(JGFSeriesBench.rank==0) {
      ilow = 1;
    } else {
      ilow = 0;
    }

//System.out.println(" Do(2) "+JGFSeriesBench.rank);
//System.out.println(" p_array_rows "+p_array_rows);
//System.exit(0);
    for (int i = ilow; i < p_array_rows; i++)
    {
	    //System.out.println("i :"+i);
        // Calculate A[i] terms. Note, once again, that we
        // can ignore the 2/period term outside the integral
        // since the period is 2 and the term cancels itself
        // out.

//System.out.println(" Do(3) "+JGFSeriesBench.rank);
        p_TestArray[0][i] = TrapezoidIntegrate((double)0.0,
                          (double)2.0,
                          1000,
                          omega * ((double)i + (ref_p_array_rows*JGFSeriesBench.rank)),
                          1);                       // 1 = cosine term.

        // Calculate the B[i] terms.

        p_TestArray[1][i] = TrapezoidIntegrate((double)0.0,
                          (double)2.0,
                          1000,
                          omega * ((double)i + (ref_p_array_rows*JGFSeriesBench.rank)),
                          2);                       // 2 = sine term.
    }

//System.out.println("barrier starts "+ JGFSeriesBench.rank);
    MPI.COMM_WORLD.Barrier();
//System.out.println("barrier ends "+JGFSeriesBench.rank);

    // Send all the data to process 0
   
    if(JGFSeriesBench.rank==0) {

      for(int k=1;k<p_array_rows;k++){
        TestArray[0][k] = p_TestArray[0][k]; 
        TestArray[1][k] = p_TestArray[1][k];
      }

      for(int k=1;k<JGFSeriesBench.nprocess;k++) {
//System.out.println("recv "+JGFSeriesBench.rank);
MPI.COMM_WORLD.Recv(p_TestArray[0],0,p_TestArray[0].length,MPI.DOUBLE,k,k);
MPI.COMM_WORLD.Recv(p_TestArray[1],0,p_TestArray[1].length,MPI.DOUBLE,k,
        k+JGFSeriesBench.nprocess);

        if(k==(JGFSeriesBench.nprocess-1)) {
          p_array_rows = rem_p_array_rows;
        }

        for(int j=0;j<p_array_rows;j++){
          TestArray[0][j+(ref_p_array_rows*k)] = p_TestArray[0][j];
          TestArray[1][j+(ref_p_array_rows*k)] = p_TestArray[1][j];
        }

      } 

      p_array_rows = ref_p_array_rows;

    } else {

//System.out.println("ssend "+JGFSeriesBench.rank);
      MPI.COMM_WORLD.Ssend(p_TestArray[0],0,p_TestArray[0].length,
		      MPI.DOUBLE,0,JGFSeriesBench.rank);
      MPI.COMM_WORLD.Ssend(p_TestArray[1],0,p_TestArray[1].length,
	      MPI.DOUBLE,0,JGFSeriesBench.rank+JGFSeriesBench.nprocess);
    } 

    MPI.COMM_WORLD.Barrier();
   
    if(JGFSeriesBench.rank==0) {
      // Stop the stopwatch.
      JGFInstrumentor.stopTimer("Section2:Series:Kernel"); 

    }
}

/*
* TrapezoidIntegrate
*
* Perform a simple trapezoid integration on the function (x+1)**x.
* x0,x1 set the lower and upper bounds of the integration.
* nsteps indicates # of trapezoidal sections.
* omegan is the fundamental frequency times the series member #.
* select = 0 for the A[0] term, 1 for cosine terms, and 2 for
* sine terms. Returns the value.
*/

private double TrapezoidIntegrate (double x0,     // Lower bound.
                        double x1,                // Upper bound.
                        int nsteps,               // # of steps.
                        double omegan,            // omega * n.
                        int select)               // Term type.
{
    double x;               // Independent variable.
    double dx;              // Step size.
    double rvalue;          // Return value.

    // Initialize independent variable.

    x = x0;

    // Calculate stepsize.

    dx = (x1 - x0) / (double)nsteps;

    // Initialize the return value.

    rvalue = thefunction(x0, omegan, select) / (double)2.0;

    // Compute the other terms of the integral.

    if (nsteps != 1)
    {
            --nsteps;               // Already done 1 step.
            while (--nsteps > 0)
            {
                    x += dx;
                    rvalue += thefunction(x, omegan, select);
            }
    }

    // Finish computation.

    rvalue=(rvalue + thefunction(x1,omegan,select) / (double)2.0) * dx;
    return(rvalue);
}

/*
* thefunction
*
* This routine selects the function to be used in the Trapezoid
* integration. x is the independent variable, omegan is omega * n,
* and select chooses which of the sine/cosine functions
* are used. Note the special case for select=0.
*/

private double thefunction(double x,      // Independent variable.
                double omegan,              // Omega * term.
                int select)                 // Choose type.
{

    // Use select to pick which function we call.

    switch(select)
    {
        case 0: return(Math.pow(x+(double)1.0,x));

        case 1: return(Math.pow(x+(double)1.0,x) * Math.cos(omegan*x));

        case 2: return(Math.pow(x+(double)1.0,x) * Math.sin(omegan*x));
    }

    // We should never reach this point, but the following
    // keeps compilers from issuing a warning message.

    return (0.0);
}

/*
* freeTestData
*
* Nulls array that is created with every run and forces garbage
* collection to free up memory.
*/

void freeTestData()
{
    TestArray = null;    // Destroy the array.
    System.gc();         // Force garbage collection.
}


}
