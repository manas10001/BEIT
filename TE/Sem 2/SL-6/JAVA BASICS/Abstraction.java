/*
 * ABSTRACT CLASS CAN HAVE ABSTRACT AND NON ABSTRACT METHODS
 * FROM JDK-8 ABSTRACT CLASSES CAN HAVE STATIC AS WELL AS DEFAULT METHODS
 * INTERFACES CAN BE IMPLEMENTED IN ABSTRACT CLASSES BUT NOT THE OTHER WAY AROUND
 * */

package basic;

abstract class NaturalObjects {
	
	String name = " ";
	
	NaturalObjects(String name){
		this.name = name;
	}
	
	void showDetails() {
		System.out.println("Name = "+name);
	}
	
	abstract public void grow(int inc);
	abstract public void showAge();
	abstract public void die();
	
}

class ApplePlant extends NaturalObjects{

	int age;
	
	//constructor will take age and name and assign
	ApplePlant(String name,int age) {
		super(name);
		this.age = age;
	}

	@Override
	public void grow(int inc) {
		age += inc;
	}

	@Override
	public void showAge() {
		System.out.println("Current age of "+super.name+" is: "+age);
	}

	@Override
	public void die() {
		System.out.println(super.name+" died at age of: "+age);
		age = 0;
	}
	
}


public class Abstraction {
	public static void main(String args[]) {
		
		//NaturalObjects no = new NaturalObjects("asfd");
		//We cant creat instance of abstract class we have to create references
		NaturalObjects apple = new ApplePlant("Apple Plant",5);
		
		apple.showAge();
		apple.grow(2);
		apple.showAge();
		apple.grow(20);
		apple.showAge();
		apple.die();
		apple.showAge();
	}
}
