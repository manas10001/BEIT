/*
 * CODE IMPLEMNETS BASICS OF INTERFACE AND ABSTRACT METHODS IN AN INTERFACE WE CAN LSO USE DEFAULT BODY FOR METHODS IN AN INTERFACE
 * interface has only final objects and abstract methods the methods can have default body though
 * also the use of static methods is allowed from jdk-8
 * */

package basic;


//interface
interface SpaceCraft{
	
	final int minspeed = 10;
	void takeOff(String planet);
	void land(String planet);
	void speedup(int increment);
	void status();
	
	//jdk 8 allows static method implementation nad default method specification
	default void showHelp() {
		System.out.println("\n\tWelcome to help\n Use takeoff land and speedup\n May the force be with you!");
		System.out.println("Maintain minimum Speed of "+minspeed+"\n");
	}
	
	static void method() {
		
		System.out.println("THIS IS STATIC METHOD!");
	}
}


class Nautilus implements SpaceCraft {

	int speed = 100;
	String destinationPlanet = "Unknown";
	String prevPlanet = "Unknown";
	String status;
	
	@Override
	public void takeOff(String planet) {
		// TODO Auto-generated method stub
		destinationPlanet = planet;
		status = "FLYING";
	}

	@Override
	public void land(String planet) {
		// TODO Auto-generated method stub
		speed = speed/2;
		prevPlanet = planet;
		status = "Landed";
				
	}

	@Override
	public void speedup(int increment) {
		// TODO Auto-generated method stub
		speed += increment;
		status = "Speeding up";
	}
	
	public void status() {
		System.out.println(status+" Current speed of Nautilus is: "+speed+" Previous planet was: "+prevPlanet+" Destination is: "+destinationPlanet);
	}
	
}


public class InterfaceExample {
	
	public static void main(String args[]) {
		
		Nautilus nau = new Nautilus();

		nau.showHelp();
		
		nau.takeOff("Earth");
		
		nau.status();
		
		nau.speedup(10);
	
		nau.status();
		
		nau.land("Earth");
		
		nau.status();
		
		nau.takeOff("Mars");
		
		nau.speedup(100);
		
		nau.status();
		
		nau.land("Mars");
		
		nau.status();
	}
	
}
