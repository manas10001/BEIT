/*
	PASS 2 
	READ INTERMEDIATE CODE AND GENERATE MACHINE CODE
	THE OUTPUT GENERATED IN FILE IS THE FINAL OUTPUT
*/

#include <iostream>
#include <fstream>
#include <string>
#include <bits/stdc++.h>
#include <cstring>
#include <queue>

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
void generate_code();	//generates machine code


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

//returns true for symbol
bool isSym(char *s)
{
	if(s[0] == 'S' && s[1] == ',')
		return true;
	else
		return false;
}

//returns true for AD
bool isAD(char *s)
{
	if(s[0] == 'A' && s[1] == 'D' && s[2] == ',')
		return true;
	else
		return false;
}

//returns true for IS
bool isIS(char *s)
{
	if(s[0] == 'I' && s[1] == 'S' && s[2] == ',')
		return true;
	else
		return false;
}

//returns true for reg
bool isReg(char *s)
{	
	if(isdigit(s[0]) && s[1] == '\0')
		return true;
	else
		return false;
}

//returns true for literal
bool isLit(char *s)
{
	if(s[0] == 'L' && s[1] == ',')
		return true;
	return false;
}

//returns true for dl
bool isDL(char *s)
{
	if(s[0] == 'D' && s[1] == 'L' && s[2] == ',')
		return true;
	return false;
}

void generate_code(){
	
	fstream input;
	input.open("input.txt");
	
	fstream output;
	output.open("output.txt");
	
	int lc_cnt = 0;		//loc counter
	string str;
	
	//will use this for ad and dl
	bool printnothing = false;
	
	//to track file
	streampos oldpos;
	oldpos = output.tellp();
	
	cout<<"\n\n\tMachine code:\n\n";
	cout<<"LC\tMN\tREG\tMEM\n";
	
	//loop through line
	while(getline(input,str)){
		int len = str.length();
		char c[len + 1];
		strcpy(c, str.c_str());
		char *token = strtok(c, " ( )");
		
		//process each token
		while(token){
			//cout<<token<<":";
			//for ad print nothing
			if(isAD(token)){
				output.seekp(oldpos);
				cout<<"-\t-\t-\t";
				output<<"-\t-\t-\t-";
				lc_cnt++;
				printnothing = true;
			}
			//for ad print nothing
			else if(isDL(token)){
				output.seekp(oldpos);			
				cout<<"-\t-\t-\t";
				output<<"-\t-\t-\t-";
				lc_cnt++;
				printnothing = true;
			}
			//for is print opcode
			else if(isIS(token) && !printnothing ){
				output<<token[3]<<"\t";
				cout<<token[3]<<"\t";
				lc_cnt++;
			}
			//for reg print opcode
			else if(isReg(token) && lc_cnt != 0 && !printnothing ){
				cout<<token[0]<<"\t";
				output<<token[0]<<"\t";
				lc_cnt++;
			}
			//for Literal print its address
			else if(isLit(token) && !printnothing ){
				const char *s = (token + 2);
				int i = atoi(s);
				cout<<lit[i].addr<<"\t";
				output<<lit[i].addr<<"\t";
			}
			//for symbo print its address
			else if(isSym(token) && !printnothing ){
				const char *s = (token + 2);
				int i = atoi(s);
				cout<<sytb[i].addr<<"\t";
				output<<sytb[i].addr<<"\t";
			}
			//lc will come at start of line only so lc_cnt must be zero
			else if(lc_cnt == 0 && token[0] != 0 && !printnothing ){
				//const char *s = (token + 2);
				int i = atoi(token);
				cout<<i<<"\t";
				output<<i<<"\t";
				lc_cnt = 1;
			}
			token = strtok(0," ( )");
		}
		
		cout<<endl;
		output<<endl;
		oldpos = output.tellp();
		lc_cnt = 0;
		printnothing = false;
	}
	
	
}

int main(){

	readdata();
	print_sytb();
	print_littb();
	generate_code();

	return 0;	 
}
