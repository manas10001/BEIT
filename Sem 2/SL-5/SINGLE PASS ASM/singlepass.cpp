/*
	SINGLE PASS ASSEMBLER

	START	 200
	MOVER	 AREG,	A
L 	MOVEM	 BREG,	='2'
	ADD	 BREG,	='2'
	ADD	 CREG,	='3'
	ORIGIN	 L+20
	LTORG
	MOVER	 AREG,	C
C	EQU	 L+15
	ADD	 AREG,	='2'
	ADD	 BREG,	='5'
A	DS	 5
	END

*/

#include<iostream>
#include<fstream>
#include<string>
#include<bits/stdc++.h>

using namespace std;

//ARRAY TO STORE MNEMONICS, ASSEMBLER DIRECTIVES AND DECLERATIVES 
string mne[11] = {"STOP","ADD","SUB","MULT","MOVER","MOVEM","COMP","BC","DIV","READ","PRINT"};//START FROM 00
string ad[5] = {"START","END","ORIGIN","EQU","LTORE"};//START FROM 01
string dl[2] = {"DC","DS"};//START FROM 01
string reg[3] = {"AREG","BREG","CREG"};//start from 1

//TABLES

//SYMBOL TABLE
int it_sytb = 0;
//string sytb_sym[100];//int sytb_addr[100];
struct symbtb{
	string sym;
	int addr;
};

symbtb sytb[100];

//LITERAL TABLE
int it_lit = 0;
//int lit_no[100];//string lit_lit[100];//int lit_addr[100];

struct littb{
	int no;
	string literal;
	int addr;
};

littb lit[100];

//LITERAL POOL
int it_litpool = 0;
int lippt[100];

//POOL TABLE
int it_pool = 0;
int pt[100];

//ARRAY TO STORE LC PER LINE
int arlc[100];

//A FLAG THAT WILL BE 0 FOR MNEMONIC, 1 FOR AD, 2 FOR DL AND 3 FOR REG
int whatis = 0;

//INTEGER VARIABLE TO TEMPORARLY STORE ANY CONSTANT
int cnst = 0;

//INT VARIABLE TO STORE CURRENT LINE NO
int curline = 0;

//FUNCTION DECLARATIONS		//returns
int is_keyword(string word);	//-1 for fail else code
int is_mne(string word);	//-1 for fail else code
int is_ad(string word);		//-1 for fail else code
int is_dl(string word);		//-1 for fail else code
int is_reg(string word);	//-1 for fail else code
int is_constant(string word);	//-1 fro fail else integer value

int main(){
	fstream fp;
	fp.open("ASM.txt");
	
	char c;
	string word = "";
	int line = 1;//0 for line end; 1 for line start
	int lc = 0;//for location counter
	bool chkconst = false;//will be set true if we want to check the next word for constant
	
	while(fp.get(c)){
		//cout<<"in loop!\n";
		
		//possible breakpoints between words
		if( c == ' ' || c == '\n' || c == '\t' || c == ',' ){
			//cout<<word<<c;
			//if we read start we will check the next word for being constant
			//this block will execute only at start of code
			if(chkconst == true){
				cout<<"checking const!\t";
				cnst = is_constant(word);
				if(cnst == -1){
					//not a constant hence end exec
					cout<<"Invalid start of program!\nThere must be a memory location specified after start!\nAborting";
					return -1;
				}else{
					//assign const to lc
					cout<<"const: "<<cnst;
					lc = cnst;
				}
				chkconst = false;
				word = "";
				continue;
			}
		
			//check for word being keyword
			if(is_keyword(word) != -1){
				//if its the start of code then lc=0 & 1st word should be AD and it should be start			
				//cout<<endl<<word<<c;
				if(lc == 0 && whatis == 1){

					//if the word is start then we have to get lc
					if( strcmp( word.c_str() , ad[0].c_str() ) == 0 ){
						//set flag indicating that we will have to get a constant now
						chkconst = true;
						
						//reset the word
						word = "";
						continue;
					}else{
						cout<<"Code must start with start!\n Aborting...";
						return -1;
					}
				}else{
					//can be any keyword
					lc++;
				}
				line = 0;//reset the line flag afer reading keyword
			
			}else{		//if the word is not a keyword then...
				//can be label or literal or constant
				//if line has started and its the first word which is not a keyword then consider it as a label and keep the line flag 1;
				if(line == 1){
					//word is symbol and it is at start hence
//					strcpy(sytb[it_sytb].sym,word.c_str());
					sytb[it_sytb].sym = word;
					sytb[it_sytb].addr = lc;
					cout<<"literal: "<<word<<endl;
					it_sytb++;
				}
				//literals or constants will occur after , or space
			}
			if(c=='\n'){
				line = 1;
				curline++;
			}
			word="";
		}
	
		//skip dividers
		if( c == ' ' || c == '\n' || c == '\t' || c == ',' )
			continue;
		//add character to word
		word += c;
		
		//cout<<c;
	}
	
	
	fp.close();
	
	for(int it = 0;it<it_sytb;it++)
		cout<<sytb[it_sytb].addr<<endl;
	return 0;
}

//returns -1 if word is not keyword else returns its code
int is_keyword(string word){
	int ret = -1;
	
	ret = is_mne(word);
//	cout<<word<<" ret mne : "<<ret<<endl;		
	if(ret == -1)
		ret = is_ad(word);
//	cout<<word<<" ret ad : "<<ret<<endl;	
	if(ret == -1)
		ret = is_dl(word);
//	cout<<word<<" ret dl : "<<ret<<endl;	
	if(ret == -1)
		ret = is_reg(word);
//	cout<<word<<" ret reg : "<<ret<<endl;	
	return ret;
}

//returns 0 if word is not mnemonic else returns its code
int is_mne(string word){
	int i=0;
	
	for(i = 0;i < 11;i++){
		if( strcmp( word.c_str() , mne[i].c_str() ) == 0 ){
			whatis = 0;
			return i;		
		}
	}
	return -1;
}

//returns 0 if word is not assembler directive else returns its code
int is_ad(string word){
	int i=0;
	
	for(i = 0;i < 5;i++){
		if( strcmp( word.c_str() , ad[i].c_str() ) == 0 ){
			whatis = 1;
			return i+1;		
		}
	}
	return -1;
}

//returns 0 if word is not decleration else returns its code
int is_dl(string word){
	int i=0;
	
	for(i = 0;i < 2;i++){
		if( strcmp( word.c_str() , dl[i].c_str() ) == 0 ){
			whatis = 2;
			return i+1;		
		}
	}
	return -1;
}

//returns 0 if word is not register else returns its code
int is_reg(string word){
	int i=0;
	
	for(i = 0;i < 3;i++){
		if( strcmp( word.c_str() , reg[i].c_str() ) == 0 ){
			whatis = 3;
			return i+1;		
		}
	}
	return -1;
}

//if the passed string is a constant retrurns its integer value else returns -1
int is_constant(string word){
	char it = word[0];
	
	int i=1;

	while(i != strlen(word.c_str()) ){
		if(!isdigit(it))
			return -1;
		i++;
		it = word[i];
	}
	return atoi(word.c_str());
}


/*
blk1

else{
if(const != 0){
//we may come here if we try to read a const after start
else{
cout<<"INVALID START OF PROGRAM!";
return -1;
}
}*/
