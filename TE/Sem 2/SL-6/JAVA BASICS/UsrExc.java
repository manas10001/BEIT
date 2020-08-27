/*
 * USER DEFINED EXCEPTIONS CAN BE CREATED BY EXTENDING THE EXCEPTION CLASS
 * */

package basic;

import java.util.Scanner;

class Ex extends Exception{
	public Ex(String s) {
		//used to assign the msg string of error
		super(s);
	}
}

public class UsrExc{
	
	public static void main(String args[]) {
		Scanner sc  = new Scanner(System.in);
		int sal = 0;
		try {
			System.out.println("Enter Salary: ");
			sal = sc.nextInt();
			//throw the exception
			if(sal<5000)
				throw new Ex("Salary should at least be 5000");
			else 
				System.out.println("Valid salary");
		}catch(Ex e) {
			System.out.println("Exceptio caught: "+e.getMessage());
		}finally {
			sc.close();
		}
	}
}
