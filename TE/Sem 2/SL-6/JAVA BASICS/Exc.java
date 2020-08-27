/*
 * IMPLEMENTATION OF EXCEPTION HANDLING TRY CATCH FINALLY
 * */

package basic;

public class Exc {

	public static void main(String args[]) {
		
		int a = 100;
		int b;
		float res;
		/*try catch on division by 0
			we will divide a by b and decrement b using for loop till 0 
			it should throw exception on divide by zero
		*/

		try{
			for(b = 10 ; b >= 0; b--) {
				res = a/b;
				System.out.println("Dividing "+a+" by "+b+" Result is: "+res);
			}
			
		}catch(Exception ex) {
			System.out.println("Exception occured: "+ex.toString());
		}finally{
			System.out.println("Whatever happens this will execute!");
		}
		
	}
	
}
