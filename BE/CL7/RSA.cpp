//Assignment 1  - RSA implementation

#include<iostream>
#include<string.h>
#include<math.h>

using namespace std;

//find gcd
double gcd(int n, int m){
	int temp;
	while(true){
		temp = n % m;
		if(temp == 0)
			return m;
		n = m;
		m = temp;
	}
	return 1;
}

int main(){
	double p,q,n,e,d,phi;

	//two primes	
	p = 4517;
	q = 4513;

	//calc n 
	n = p * q;
	
	//calc phi
	phi = ((p - 1) * (q - 1));

	//calculation for e	
	e = 2;
	while (e < phi)
    {
        if (gcd(e, phi) == 1)
            break;
        else
            e++;
    }
	
//	cout<<"Generated e: "<<e<<endl;
	
	double msg;
	cout<<"Enter integer to encrypt: ";
	cin>>msg;
	
	cout<<"Original msg: "<<msg<<endl;
	
	//get d
	d =  fmod((1/e),phi);
	
	// enc = m^e mod n && dec = enc^d mod n
	double enc = pow(msg,e);
	double dec = pow(enc,d);
	enc = fmod(enc,n);
	dec = fmod(dec,n);	
	
	cout<<"Encrypted: "<<enc<<endl;
	
	cout<<"Decrypted: "<<dec<<endl;
}
