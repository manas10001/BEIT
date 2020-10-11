import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class ShaEncryption{

	static String encrypt(String data){
		StringBuilder sb = new StringBuilder();
		try{
			//get instance of algo throws NoSuchAlgorithmException
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			
			md.update(data.getBytes());
			
			//get hash in byte array
			byte hash[] = md.digest();
			
			//convert byte to hex and store it
			for(byte b : hash)
				sb.append(String.format("%02x", b & 0xff));
			
		}catch(NoSuchAlgorithmException nsae){
			System.out.println(nsae.toString());
		}
			
		return sb.toString();
	}

	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter tedxt to encrypt: ");
		
		String data = sc.nextLine();
		
		sc.close();
		
		System.out.println("Encrypted string is: "+encrypt(data));
	}
}
