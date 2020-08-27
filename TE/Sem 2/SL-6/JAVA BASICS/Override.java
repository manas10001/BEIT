
class A{
	public void msg(){
		System.out.println("THIS IS CLASS A");
	}
}

class B extends A{
	public void msg(){
		System.out.println("THIS IS CLASS B");
	}
}


class Override{
	public static void main(String args[]){
		A a = new A();
		B b = new B();
		a.msg();
		b.msg();

/*		//calls method of class b
		A b = new B();
		b.msg();
*/
	}
}
