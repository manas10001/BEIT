
class Operations{
	void add(int a,int b){
		int c = a + b;
		System.out.println("Sum is : "+c);
	}

	void add(double a,double b){
		double c = a + b;
		System.out.println("Sum is : "+c);
	}
}


class Overload{

	public static void main(String args[]){
		Operations ov = new Operations();
	
		ov.add(10,20);
		ov.add(10.5,12.6);
	}
}
