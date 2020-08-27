package basic;

import java.util.ArrayList;

interface Exp{
	void func(int x,int y);
}

public class LambdaEx {
	
	public static void main(String args[]) {
		Exp ex = (int x,int y) -> System.out.println("Multiplication is: "+(x*y))	;
		
		ex.func(10,12);
		
		ArrayOp ar = new ArrayOp();
		ar.doOp();
		ArrayOp arr = new ArrayOp();
		
	}
}


class ArrayOp{
	//default/ initialization block
	{
		System.out.println("\n\tThis is default block! It gets invoked everytime a coustructor is created!");
	}
	
	//static block executes only once the first time you create an object of class or the first time you access static object of class
	static {
		System.out.println("This is static block");
	}
	
	void doOp() {
		ArrayList<Integer> arr = new ArrayList();
		arr.add(20);
		arr.add(30);
		arr.add(455);
		arr.add(965);
		arr.add(4);
		System.out.println("Arraylist is: ");
		arr.forEach(n -> System.out.print(" "+n));
		
		System.out.println("\nEven are : ");
		
		arr.forEach(n -> 
			{
				if(n%2 == 0 )
					System.out.print(" "+n);
			}
		);
	}
}