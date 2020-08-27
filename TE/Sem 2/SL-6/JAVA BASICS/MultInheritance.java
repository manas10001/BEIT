package basic;

interface Programmer{
	String email = "phanricks@gmail.com";
	void code();
	void showDetails();
}

interface Writer{
	String name = "Richerd";
	void writeBook();
	void readBook();
	void review();
}

class Person implements Programmer,Writer{

	@Override
	public void writeBook() {
		System.out.println(name+" is Writing");
	}

	@Override
	public void readBook() {
		System.out.println(name+" is Reading");
		
	}

	@Override
	public void review() {
		System.out.println(name+" is reviewing a book");
	}

	@Override
	public void code() {
		System.out.println(name+" is programming");
	}

	@Override
	public void showDetails() {
		System.out.println("Persons name is "+name+"\nEmail: "+email);
	}
	
}

public class MultInheritance {
	
	public static void main(String args[]) {
		Person pr = new Person();
		
		pr.showDetails();
		pr.readBook();
		pr.writeBook();
		pr.review();
		pr.code();
	}
}
