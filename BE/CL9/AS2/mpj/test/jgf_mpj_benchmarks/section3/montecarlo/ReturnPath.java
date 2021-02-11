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
*      Original version of this code by Hon Yau (hwyau@epcc.ed.ac.uk)     *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package jgf_mpj_benchmarks.section3.montecarlo;


//package montecarlo;

/**
  * Class for representing the returns of a given security.
  *
  * <p>To do list:
  * <ol>
  *   <li>Define a window over which the mean drift and volatility
  *       are calculated.</li>
  *   <li>Hash table to reference {DATE}->{pathValue-index}.</li>
  * </ol>
  *
  * @author H W Yau
  * @version $Revision: 1.1 $ $Date: 2005/04/29 17:43:42 $
  */
public class ReturnPath extends PathId {

  //------------------------------------------------------------------------
  // Class variables.
  //------------------------------------------------------------------------
  /**
    * A class variable, for setting whether to print debug messages or not.
    */
  public static boolean DEBUG=true;
  /**
    * Class variable, for defining the prompt to print in front of debug
    * messages.
    */
  protected static String prompt="ReturnPath> ";
  /**
    * Flag for indicating one of the return definitions, via:
    *       u_i = \ln{\frac{S_i}{S_{i-1}}}
    * corresponding to the instantaneous compounded return.
    */
  public static int COMPOUNDED = 1;

  /**
    * Flag for indicating one of the return definitions, via:
    *       u_i = \frac{S_i - S_{i-1}}{S_i}
    * corresponding to the instantaneous non-compounded return.
    */
  public static int NONCOMPOUNDED = 2;

  //------------------------------------------------------------------------
  // Instance variables.
  //------------------------------------------------------------------------
  /**
    * An instance variable, for storing the return values.
    */
  private double[] pathValue;
  /**
    * The number of accepted values in the rate path.
    */
  private int nPathValue=0;
  /**
    * Integer flag for indicating how the return was calculated.
    */
  private int returnDefinition = 0;
  /**
    * Value for the expected return rate.
    */
  private double expectedReturnRate = Double.NaN;
  /**
    * Value for the volatility, calculated from the return data.
    */
  private double volatility = Double.NaN;
  /**
    * Value for the volatility-squared, a more natural quantity
    * to use for many of the calculations.
    */
  private double volatility2 = Double.NaN;
  /**
    * Value for the mean of this return.
    */
  private double mean = Double.NaN;
  /**
    * Value for the variance of this return.
    */
  private double variance = Double.NaN;

  //------------------------------------------------------------------------
  // Constructors.
  //------------------------------------------------------------------------
  /**
    * Default constructor.
    */
  public ReturnPath() {
    super();
    set_prompt(prompt);
    set_DEBUG(DEBUG);
  }

  /**
    * Another constructor.
    *
    * @param pathValue for creating a return path with a precomputed path
    *                  value.  Indexed from 1 to <code>nPathArray-1</code>.
    * @param nPathValue the number of accepted data points in the array.
    * @param returnDefinition to tell this class how the return path values
    *                         were computed.
    */
  public ReturnPath(double[] pathValue, int nPathValue, int returnDefinition) {
    set_prompt(prompt);
    set_DEBUG(DEBUG);
    this.pathValue = pathValue;
    this.nPathValue = nPathValue;
    this.returnDefinition = returnDefinition;
  }

  //------------------------------------------------------------------------
  // Methods.
  //------------------------------------------------------------------------
  //------------------------------------------------------------------------
  // Accessor methods for class ReturnPath.
  // Generated by 'makeJavaAccessor.pl' script.  HWY.  20th January 1999.
  //------------------------------------------------------------------------
  /**
    * Accessor method for private instance variable <code>pathValue</code>.
    *
    * @return Value of instance variable <code>pathValue</code>.
    * @exception DemoException thrown if instance variable <code>pathValue</code> is undefined.
    */
  public double[] get_pathValue() throws DemoException {
    if( this.pathValue == null )
      throw new DemoException("Variable pathValue is undefined!");
    return(this.pathValue);
  }
  /**
    * Set method for private instance variable <code>pathValue</code>.
    *
    * @param pathValue the value to set for the instance variable <code>pathValue</code>.
    */
  public void set_pathValue(double[] pathValue) {
    this.pathValue = pathValue;
  }
  /**
    * Accessor method for private instance variable <code>nPathValue</code>.
    *
    * @return Value of instance variable <code>nPathValue</code>.
    * @exception DemoException thrown if instance variable <code>nPathValue</code> is undefined.
    */
  public int get_nPathValue() throws DemoException {
    if( this.nPathValue == 0 )
      throw new DemoException("Variable nPathValue is undefined!");
    return(this.nPathValue);
  }
  /**
    * Set method for private instance variable <code>nPathValue</code>.
    *
    * @param nPathValue the value to set for the instance variable <code>nPathValue</code>.
    */
  public void set_nPathValue(int nPathValue) {
    this.nPathValue = nPathValue;
  }
  /**
    * Accessor method for private instance variable <code>returnDefinition</code>.
    *
    * @return Value of instance variable <code>returnDefinition</code>.
    * @exception DemoException thrown if instance variable <code>returnDefinition</code> is undefined.
    */
  public int get_returnDefinition() throws DemoException {
    if( this.returnDefinition == 0 )
      throw new DemoException("Variable returnDefinition is undefined!");
    return(this.returnDefinition);
  }
  /**
    * Set method for private instance variable <code>returnDefinition</code>.
    *
    * @param returnDefinition the value to set for the instance variable <code>returnDefinition</code>.
    */
  public void set_returnDefinition(int returnDefinition) {
    this.returnDefinition = returnDefinition;
  }
  /**
    * Accessor method for private instance variable <code>expectedReturnRate</code>.
    *
    * @return Value of instance variable <code>expectedReturnRate</code>.
    * @exception DemoException thrown if instance variable <code>expectedReturnRate</code> is undefined.
    */
  public double get_expectedReturnRate() throws DemoException {
    if( this.expectedReturnRate == Double.NaN )
      throw new DemoException("Variable expectedReturnRate is undefined!");
    return(this.expectedReturnRate);
  }
  /**
    * Set method for private instance variable <code>expectedReturnRate</code>.
    *
    * @param expectedReturnRate the value to set for the instance variable <code>expectedReturnRate</code>.
    */
  public void set_expectedReturnRate(double expectedReturnRate) {
    this.expectedReturnRate = expectedReturnRate;
  }
  /**
    * Accessor method for private instance variable <code>volatility</code>.
    *
    * @return Value of instance variable <code>volatility</code>.
    * @exception DemoException thrown if instance variable <code>volatility</code> is undefined.
    */
  public double get_volatility() throws DemoException {
    if( this.volatility == Double.NaN )
      throw new DemoException("Variable volatility is undefined!");
    return(this.volatility);
  }
  /**
    * Set method for private instance variable <code>volatility</code>.
    *
    * @param volatility the value to set for the instance variable <code>volatility</code>.
    */
  public void set_volatility(double volatility) {
    this.volatility = volatility;
  }
  /**
    * Accessor method for private instance variable <code>volatility2</code>.
    *
    * @return Value of instance variable <code>volatility2</code>.
    * @exception DemoException thrown if instance variable <code>volatility2</code> is undefined.
    */
  public double get_volatility2() throws DemoException {
    if( this.volatility2 == Double.NaN )
      throw new DemoException("Variable volatility2 is undefined!");
    return(this.volatility2);
  }
  /**
    * Set method for private instance variable <code>volatility2</code>.
    *
    * @param volatility2 the value to set for the instance variable <code>volatility2</code>.
    */
  public void set_volatility2(double volatility2) {
    this.volatility2 = volatility2;
  }
  /**
    * Accessor method for private instance variable <code>mean</code>.
    *
    * @return Value of instance variable <code>mean</code>.
    * @exception DemoException thrown if instance variable <code>mean</code> is undefined.
    */
  public double get_mean() throws DemoException {
    if( this.mean == Double.NaN )
      throw new DemoException("Variable mean is undefined!");
    return(this.mean);
  }
  /**
    * Set method for private instance variable <code>mean</code>.
    *
    * @param mean the value to set for the instance variable <code>mean</code>.
    */
  public void set_mean(double mean) {
    this.mean = mean;
  }
  /**
    * Accessor method for private instance variable <code>variance</code>.
    *
    * @return Value of instance variable <code>variance</code>.
    * @exception DemoException thrown if instance variable <code>variance</code> is undefined.
    */
  public double get_variance() throws DemoException {
    if( this.variance == Double.NaN )
      throw new DemoException("Variable variance is undefined!");
    return(this.variance);
  }
  /**
    * Set method for private instance variable <code>variance</code>.
    *
    * @param variance the value to set for the instance variable <code>variance</code>.
    */
  public void set_variance(double variance) {
    this.variance = variance;
  }
  //------------------------------------------------------------------------
  /**
    * Method to calculate the expected return rate from the return data,
    * using the relationship:
    *    \mu = \frac{\bar{u}}{\Delta t} + \frac{\sigma^2}{2}
    *
    * @exception DemoException thrown one tries to obtain an undefined variable.
    */
  public void computeExpectedReturnRate() throws DemoException {
    this.expectedReturnRate = mean/get_dTime() + 0.5*volatility2;
  }
  /**
    * Method to calculate <code>volatility</code> and <code>volatility2</code>
    * from the return path data, using the relationship, based on the
    * precomputed <code>variance</code>. 
    *   \sigma^2 = s^2\Delta t
    * 
    * @exception DemoException thrown if one of the quantites in the
    *                          computation are undefined.
    */
  public void computeVolatility() throws DemoException {
    if( this.variance == Double.NaN ) 
      throw new DemoException("Variable variance is not defined!");
    this.volatility2 = variance / get_dTime();
    this.volatility  = Math.sqrt(volatility2);
  }
  /**
    * Method to calculate the mean of the return, for use by other
    * calculations.
    *
    * @exception DemoException thrown if <code>nPathValue</code> is
    *            undefined.
    */
  public void computeMean() throws DemoException{
    if( this.nPathValue == 0 )
      throw new DemoException("Variable nPathValue is undefined!");
    this.mean = 0.0;
    for( int i=1; i < nPathValue; i++ ) {
      mean += pathValue[i];
    }
    this.mean /= ((double)(nPathValue - 1.0));
  }
  /**
    * Method to calculate the variance of the retrun, for use by other
    * calculations.
    *
    * @exception DemoException thrown if the <code>mean</code> or
    *            <code>nPathValue</code> values are undefined.
    */
  public void computeVariance() throws DemoException{
    if( this.mean == Double.NaN || this.nPathValue == 0)
      throw new DemoException("Variable mean and/or nPathValue are undefined!");
    this.variance = 0.0;    
    for( int i=1; i < nPathValue; i++ ) {
      variance += (pathValue[i] - mean)*(pathValue[i] - mean);
    }
    this.variance /= ((double)(nPathValue - 1.0));
  }
  /**
    * A single method for invoking all the necessary methods which
    * estimate the parameters.
    *
    * @exception DemoException thrown if there is a problem reading any
    *            variables.
    */
  public void estimatePath() throws DemoException{
    computeMean();
    computeVariance();
    computeExpectedReturnRate();
    computeVolatility();
  }
  /**
    * Dumps the contents of the fields, to standard-out, for debugging.
    */
  public void dbgDumpFields() {
    super.dbgDumpFields();
//    dbgPrintln("nPathValue="        +this.nPathValue);
//    dbgPrintln("expectedReturnRate="+this.expectedReturnRate);
//    dbgPrintln("volatility="        +this.volatility);
//    dbgPrintln("volatility2="       +this.volatility2);
//    dbgPrintln("mean="              +this.mean);
//    dbgPrintln("variance="          +this.variance);
  }
}
