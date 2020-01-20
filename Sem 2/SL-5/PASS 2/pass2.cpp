/*
	PASS 2 
	READ INTERMEDIATE CODE AND GENERATE MACHINE CODE
*/

#include<iostream>
#include<string.h>
#include<bits/stdc++.h>

using namespace std;

//TABLES
//SYMBOL TABLE
int it_sytb = -1;
struct symbtb{
	string sym;
	int addr;
};
symbtb sytb[100];

//LITERAL TABLE
int it_lit = -1;

struct littb{
	int no;
	string literal;
	int addr;
};
littb lit[100];


void readdata();	//reads data from files to structures;
void print_sytb();	//prints syboltb
void print_littb();	//prints literal tabel

int main(){

	readdata();
	print_sytb();
	print_littb();
	

	return 0;	 
}

void readdata(){

	fstream syfp;
	syfp.open("st.txt");
	
	fstream ltfp;
 	ltfp.open("lt.txt");

	while(!syfp.eof()){
		it_sytb++;
		syfp >> sytb[it_sytb].sym >> sytb[it_sytb].addr;
	}
	syfp.close();
	
	while(!ltfp.eof()){
		it_lit++;
		ltfp >> lit[it_lit].no >> lit[it_lit].literal >> lit[it_lit].addr;
	}
	ltfp.close();
}


//prints symbol table
void print_sytb(){
	cout << "\n\tSymbol Table:\n";
	for (int it = 0; it < it_sytb; it++)
		cout << " sym: " << sytb[it].sym << " addr: " << sytb[it].addr << endl;
}

//prints literal table
void print_littb(){
	
	cout << "\n\tLiteral Tabel:\n";
	cout << "\nNo\tLITERAL\tADDRESS\n";
	for (int it = 0; it < it_lit; it++)
		cout << lit[it].no << "\t" << lit[it].literal << "\t" << lit[it].addr << endl;
}
