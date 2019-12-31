/*
	SINGLE PASS ASSEMBLER
*/

#include<iostream>
#include<fstream>
#include<string.h>
#include<bits/stdc++.h>

using namespace std;

//ARRAY TO STORE MNEMONICS, ASSEMBLER DIRECTIVES AND DECLERATIVES 
string mne[] = {"STOP","ADD","SUB","MULT","MOVER","MOVEM","COMP","BC","DIV","READ","PRINT"};//START FROM 00
string ad[] = {"START","END","ORIGIN","EQU","LTORE"};//START FROM 01
string dl[] = {"DC","DS"};//START FROM 01
string reg[] = {"AREG","BREG","CREG"};//start from 1

//TABLES

//SYMBOL TABLE
string sytb_sym[100];
int sytb_addr[100];

//LITERAL TABLE
int lit_no[100];
string lit_lit[100];
int lit_addr[100];

//POOL TABLE
int pt[100];

//A FLAG THAT WILL BE 0 FOR MNEMONIC, 1 FOR AD, 2 FOR DL AND 3 FOR REG
int whatis = 0;

//INTEGER VARIABLE TO TEMPORARLY STORE ANY CONSTANT
int const = 0;

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
		
		//possible breakpoints between words
		if( c == ' ' || c == '\n' || c == '\t' || c == ',' ){
		
			if(chkconst){
				
				chkconst = false;
			}
		
			if(is_keyword(word) != -1){
				//if its the start of code then lc=0 & 1st word should be AD and it should be start			
				if(lc == 0 && whatis == 1){
					//if the word is start then we have to get lc
					if( strcmp( word.c_str() , ad[0].c_str() ) == 0 ){
						//traverse after start and pick up the start of lc
						word = "";
						chkconst = true;
						continue;						
					}else{
						if(const != 0){
							//we may come here if we try to read a const after start
						else{
							cout<<"INVALID START OF PROGRAM!";
							return -1;
						}
					}
				}else{
					//can be any keyword
				}
				line = 0;//reset the line flag afer readinga keyword
			}else{
				//can be label or literal or constant
				//if line has started and its the first word which is not a keyword 
				//then consider it as a label and keep the line flag 1;
				//literals or constants will occur after , or space
			}
			if(c=='\n')
				line = 1;
				
		}
	
		//add character to word
		word += c;
		
		//cout<<c;
	}
	
	
	fp.close();
	return 0;
}

//returns -1 if word is not keyword else returns its code
int is_keyword(string word){
	int ret = -1;
	
	ret = is_mne(word);
	
	if(ret == -1)
		ret = is_ad(word);
	
	if(ret == -1)
		ret = is_dl(word);
	if(ret == -1)
		ret = is_reg(word);
	return ret;
}

//returns 0 if word is not mnemonic else returns its code
int is_mne(string word){
	int i=0;
	
	for(i = 0;i < 11;i++){
		if( strcmp( word.c_str() , mne[i].c_str() ) ){
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
		if( strcmp( word.c_str() , ad[i].c_str() ) ){
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
		if( strcmp( word.c_str() , dl[i].c_str() ) ){
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
		if( strcmp( word.c_str() , reg[i].c_str() ) ){
			whatis = 3;
			return i+1;		
		}
	}
	return -1;
}

//if the passed string is a constant retrurns its integer value else returns -1
int is_constant(string word){
	char it = word[0];
	int i;
	while(i != strlen(word)){
		if(!isdigit(it))
			return -1;
		i++;
		it = word[i];
	}
	return atoi(word);
}

/*
//iterte through all file
	while(fp.get(c)){
		//breakpoints for possible words
		if( c == ' ' || c == '\n' || c == '\t' || c == ',' ){
			//check word being mnemonic
			int index = is_mne(word);
			if(index == -1){
				cout<<"NOT A MNEMONIC ";
				return -1;
			}
		}
		
		//add character to word
		word += c;
	}
*/
