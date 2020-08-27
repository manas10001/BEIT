import java.math.BigInteger;
import java.util.Random;

public class RSA{

	private BigInteger p, q, n, phi, e, d;
	private Random r;
	private int bitlen = 1024;
	
	public RSA(){
		//get random
		r = new Random();
		//get two prime nos
		p = BigInteger.probablePrime(bitlen, r);
		q = BigInteger.probablePrime(bitlen, r);
		//get n = pq
		n = p.multiply(q);
		//phi = (p-1)(q-1)
		phi = p.substract(BigInteger.ONE).multiply(q.substract(BigInteger.ONE));
		//1<e<phi and gcd(e,phi)==1
		e = BigInteger.probablePrime(bitlength / 2, r);
		while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
		{
		    e.add(BigInteger.ONE);
		}
		
		d = e.modInverse(phi);
		
	}

	public static void main(String[] args){
		RSA rsa = new RSA();
		DataInputStream inp = new DataInputStream(System.in);
		string msg;
		System.out.print("Enter your message : ");
		msg = inp.readLine();
		System.out.println("String to encrypt: " + msg);
		
	}

}
