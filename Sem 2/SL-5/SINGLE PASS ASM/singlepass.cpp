/*
	SINGLE PASS ASSEMBLER
START 100	
MOVER AREG,A		100
L MOVEM	BREG,='2'	101	104
ADD BREG,='2'		102
ADD CREG,='3'		103	105
ORIGIN L+20		
LTORG			
MOVER AREG,C		106
C EQU L+15		
ADD AREG,='2'		107	110
ADD BREG,='5'		108	111
A DS 5			109
END
*/
#include <iostream>
#include <fstream>
#include <string>
#include <bits/stdc++.h>
using namespace std;

//ARRAY TO STORE MNEMONICS, ASSEMBLER DIRECTIVES AND DECLERATIVES
string mne[11] = {"STOP", "ADD", "SUB", "MULT", "MOVER", "MOVEM", "COMP", "BC", "DIV", "READ", "PRINT"}; //START FROM 00
string ad[5] = {"START", "END", "ORIGIN", "EQU", "LTORG"};			 //START FROM 01
string dl[2] = {"DC","DS"};						 //START FROM 01
string reg[3] = {"AREG", "BREG", "CREG"};							 //start from 1

//TABLES
//SYMBOL TABLE
int it_sytb = 0;
struct symbtb{
	string sym;
	int addr;
};
symbtb sytb[100];

//LITERAL TABLE
int it_lit = 0;

struct littb{
	int no;
	string literal;
	int addr;
};
littb lit[100];

//LITERAL POOL
int it_litpool = 0;
string litpt[100];

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
int curline = 1;
//FLAG TO ENCOUNTER ,
bool commaFlag = false;
bool getValueFlag = false;
//FUNCTION DECLARATIONS			        //returns
int is_keyword(string word);		//-1 for fail else code
int is_mne(string word);			//-1 for fail else code
int is_ad(string word);				//-1 for fail else code
int is_dl(string word);				//-1 for fail else code
int is_reg(string word);			//-1 for fail else code
int in_sytb(string word, int addr); //returns -1 for fail else 1
int in_litpt(string word);			//returns -1 for fail else 1
int is_constant(string word);		//-1 fro fail else integer value
void print_sytb();					//print syboltb
void print_litpl();					//print literal pool
void print_poolt();					//print pool tabel
void print_littb();					//print literal tabel
int process_lit(int addr);			//process literals after end or ltorg returns next lc
int getValue(string word);			//returns value of expression after origin/equ
int symVal(string word);			//returns value of ymbol	

int main(){
	fstream fp, fp2;
	fp.open("ASM.txt");
	fp2.open("output.txt");
	
	char c;
	string word = "";
	int line = 1;			//0 for line end; 1 for line start
	int lc = 0, prevlc = 0; //for location counter
	bool chkconst = false;  //will be set true if we want to check the next word for constant

	while (fp.get(c)){
		//possible breakpoints between words
		if (c == ' ' || c == '\n' || c == '\t' || c == ','){
			//need for , seperator processing
			if (c == ',')
				commaFlag = true;

			if(getValueFlage){
				lc = getVal(word);
			}
			//if we read start we will check the next word for being constant
			//this block will execute only at start of code
			if (chkconst == true){
				//cout << "checking const!\t";
				cnst = is_constant(word);
				if (cnst == -1){
					//not a constant hence end exec
					cout << "Invalid start of program!\nThere must be a memory location specified after start!\nAborting";
					return -1;
				}else
					lc = cnst;
				chkconst = false;
				word = "";
				continue;
			}

			//check for word being keyword
			if (is_keyword(word) != -1){
				//if its the start of code then lc=0 & 1st word should be AD and it should be start
				//cout<<endl<<word<<c;
				if (lc == 0 && whatis == 1){
					//if the word is start then we have to get lc
					if (strcmp(word.c_str(), ad[0].c_str()) == 0){
						//set flag indicating that we will have to get a constant now
						chkconst = true;

						//reset the word
						word = "";
						continue;
					}else{
						cout << "Code must start with start!\n Aborting...";
						return -1;
					}
				}
				//end check
				//check for end there should be nothing after end and literals are to be processed and lc to be manipulated
				else if (strcmp(word.c_str(), ad[1].c_str()) == 0){
					//nothing else should be read other than newline or eof or space or tab
					while (fp.get(c)){
						if (c != ' ' || c != '\n' || c != '\t'){
							cout << "Error there should be nothing after end!\n";
							return -1;
						}
					}
					//if we complete this loop then everything is fine
					//process literals and terminate
					lc = process_lit(lc);
					break;
				}else{
					//shoudnt be reg
					if (is_reg(word) == -1){
						//can be any keyword
						//origin restart lc from here
						if (strcmp(word.c_str(), "ORIGIN") == 0)
							getValueFlag = true;
						//ltorg	process literals
						else if (strcmp(word.c_str(), "LTORG") == 0)
							lc = process_lit(lc);
						//equ change lc for tht instruction only
						//else if (strcmp(word.c_str(), "EQU") == 0){
							//we change addr of last symbol here
						//}
						//for other inst inc lc by 1
						else
							lc++;
					}
				}
				line = 0; //reset the line flag afer reading keyword
			}else if (is_keyword(word) == -1){ 
				//can be label or literal or constant
				//if line has started and its the first word which is not a keyword then consider it as a label and keep the line flag 1;
				if (line == 1){
					//word is symbol and it is at start hence
					//check whether it is already in table
					if (in_sytb(word, lc) == -1){
						sytb[it_sytb].sym = word;
						sytb[it_sytb].addr = lc;
						//cout << "symb: " << word << endl;
						it_sytb++;
						word = " ";
						lastSymbol = curline;
					}
				}else{
					//after , next word will be a symb or literal so we set a flag anticipating the situation
					if (commaFlag){
						//literal starts with ='
						if (word[0] == '=' && word[1] == '\'' ){
							//store in literal pool
							if (in_litpt(word) == -1)
								litpt[it_litpool++] = word;
						}else{
							if (in_sytb(word, 0) == -1){
								//and if it is not add it in table without address
								sytb[it_sytb].sym = word;
								sytb[it_sytb].addr = 0;
								//cout << "symb nad: " << word << endl;
								it_sytb++;
							}
						}
						commaFlag = false;
					}
				}
			}
			if (c == '\n'){
				line = 1;
				curline++;
			}
			word = "";
		} //end for

		//skip dividers
		if (c == ' ' || c == '\n' || c == '\t' || c == ',')
			continue;
		//add character to word
		word += c;
	}

	fp.close();
	fp2.close();
	print_sytb();
	print_litpl();
	print_poolt();
	print_littb();
	return 0;
}

//get value of expression after equ or origin
int getValue(string word){
	int lc2 = 0;
	if(lc2 = is_constant(word) != -1)
		return lc2;
	size_t loc;
	string symb = "";
	string con = "";
	loc = word.find('+');
	if(loc != string::npos){
		//seperate constant and symbol
		for(int i=0;i<strlen(word);i++){
			if(i<loc)
				symb += word[i];
			else if(i>loc)
				con += word[i]
		}
		//get and add the values
		if(lc2 = symVal(symb) != -1 && is_const(con) != -1)
			return lc2+is_const(con);
		else
			cout<<"Illegle Operation";
	}
	else
		return symVal(symb);
}

int symVal(string word){
	for (int it = 0; it < it_sytb; it++)
		if (strcmp(sytb[it].sym.c_str(), word.c_str()) == 0)
			return sytb[it].addr;
	return -1;
}
//handle literal
int process_lit(int addr){
	print_litpl();
	//add new index of lit tabel to pool tabel
	pt[it_pool++] = it_lit;
	//put contents of literal pool to literal tabel and give add
	for (int it = 0; it < it_litpool; it++){
		lit[it_lit].no = it_lit;
		lit[it_lit].literal = litpt[it];
		lit[it_lit++].addr = addr++;
	}
	it_litpool = 0;
	return addr;
}

//check if a symbol is already in the table if it is and has no address then assign it address
int in_sytb(string word, int addr){
	for (int it = 0; it < it_sytb; it++){
		//cout << "check " << word<<endl;
		if (strcmp(sytb[it].sym.c_str(), word.c_str()) == 0){
			//set address if not already set
			if (sytb[it].addr == 0 && addr != 0)
				sytb[it].addr = addr;
			return 1;
		}
	}
	return -1;
}

//check if a literal is already in literal pool
int in_litpt(string word){
	for (int it = 0; it < it_litpool; it++)	{
		if (strcmp(litpt[it].c_str(), word.c_str()) == 0)
			return 1;
	}
	return -1;
}

//prints symbol table
void print_sytb(){
	cout << "\n\tSymbol Table:\n";
	for (int it = 0; it < it_sytb; it++)
		cout << " sym: " << sytb[it].sym << " addr: " << sytb[it].addr << endl;
}

void print_litpl(){
	cout << "\n\tLiteral pool:\n";
	for (int it = 0; it < it_litpool; it++)
		cout << " lit: " << litpt[it] << endl;
}

void print_poolt(){
	cout << "\n\tPool Tabel:\n";
	for (int it = 0; it < it_pool; it++)
		cout << " index: " << pt[it] << endl;
}
void print_littb(){
	cout << "\n\tLiteral Tabel:\n";
	cout << "\nNo\tLITERAL\tADDRESS\n";
	for (int it = 0; it < it_lit; it++)
		cout << lit[it].no << "\t" << lit[it].literal << "\t" << lit[it].addr << endl;
}

//returns -1 if word is not keyword else returns its code
int is_keyword(string word){
	int ret = -1;
	ret = is_mne(word);
	if (ret == -1)
		ret = is_ad(word);
	if (ret == -1)
		ret = is_dl(word);
	if (ret == -1)
		ret = is_reg(word);
	return ret;
}

//returns 0 if word is not mnemonic else returns its code
int is_mne(string word){
	int i = 0;
	for (i = 0; i < 11; i++){
		if (strcmp(word.c_str(), mne[i].c_str()) == 0){
			whatis = 0;
			return i;
		}
	}
	return -1;
}

//returns 0 if word is not assembler directive else returns its code
int is_ad(string word){
	int i = 0;
	for (i = 0; i < 5; i++){
		if (strcmp(word.c_str(), ad[i].c_str()) == 0){
			whatis = 1;
			return i + 1;
		}
	}
	return -1;
}

//returns 0 if word is not decleration else returns its code
int is_dl(string word){
	int i = 0;
	for (i = 0; i < 2; i++){
		if (strcmp(word.c_str(), dl[i].c_str()) == 0){
			whatis = 2;
			return i + 1;
		}
	}
	return -1;
}

//returns 0 if word is not register else returns its code
int is_reg(string word){
	int i = 0;
	for (i = 0; i < 3; i++){
		if (strcmp(word.c_str(), reg[i].c_str()) == 0){
			whatis = 3;
			return i + 1;
		}
	}
	return -1;
}

//if the passed string is a constant retrurns its integer value else returns -1
int is_constant(string word){
	char it = word[0];
	int i = 1;
	while (i != strlen(word.c_str())){
		if (!isdigit(it))
			return -1;
		i++;
		it = word[i];
	}
	return atoi(word.c_str());
}
