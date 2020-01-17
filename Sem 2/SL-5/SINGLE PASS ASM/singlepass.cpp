/*
	SINGLE PASS ASSEMBLER
*/
#include <iostream>
#include <fstream>
#include <string.h>
#include <bits/stdc++.h>
//#include<ctype.h>
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
int itlc = 0;
//A FLAG THAT WILL BE 0 FOR MNEMONIC, 1 FOR AD, 2 FOR DL AND 3 FOR REG
int whatis = -1;
//INTEGER VARIABLE TO TEMPORARLY STORE ANY CONSTANT
int cnst = 0;
//INT VARIABLE TO STORE CURRENT LINE NO
int curline = 0;
int key_ind = 0;
//FLAGS
bool commaFlag = false;
bool getValueFlag = false;
bool afterds = false;
bool printsyminop = false;
bool lcprinted = false;
bool prevsymb = false;
bool changelc = false;

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
int symVal(string word);			//returns value of symbol	
int getVal(string word);			//returns value after processing equation if any
int generateIC(string word,ofstream& outFile);			//writes intermidate code of string to file
int loc_littb(string word);			//returns position of literal in littb
int chksytb();

int main(){
	fstream fp;
	fp.open("ASM.txt");
	
	ofstream outFile;
	outFile.open("output.txt");
	
	ofstream lcFile;
	lcFile.open("lc.txt");
	
	//to track file
	streampos oldpos;
	
	char c;
	string word = "";
	int line = 1;			//0 for line end; 1 for line start
	int lc = 0, newlc = 0; //for location counter
	bool chkconst = false;  //will be set true if we want to check the next word for constant

	while (fp.get(c)){
		//possible breakpoints between words
		
		if (c == ' ' || c == '\n' || c == '\t' || c == ','){
		
			if(changelc){
				lc = newlc;
				changelc = false;
			}
			//need for , seperator processing
			if (c == ',')
				commaFlag = true;
			
			if(prevsymb){
				if(is_keyword(word) == -1){
					cout<<"There must be a mnemonic after symbol!\n"<<word<<" Found";
					return -1;
				}
				prevsymb = false;
			}
			
			
			//if const is after ds then
			if(afterds){
				if(is_constant(word)){
					newlc = lc + is_constant(word);
					lc++;
					changelc = true;
				}
				else{
					cout<<"DS should always have a constant value after it";
					return -1;
				}
				
				//generateIC(word,outFile);
				afterds = false;
			}
			
			if(getValueFlag){
				lc = getVal(word);
				getValueFlag = false;
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
				//cout<<"printing to file: "<<word<<endl;
				generateIC(word,outFile);
				outFile<<endl;
				lcFile<<lc<<endl;
				word = "";
				continue;
			}

			//check for word being keyword
			if (is_keyword(word) != -1){
				//store the index returned by function
				key_ind = is_keyword(word);
				//if its the start of code then lc=0 & 1st word should be AD and it should be start
				//cout<<endl<<word<<c;
				if (lc == 0 && whatis == 1){
					//if the word is start then we have to get lc
					if (strcmp(word.c_str(), ad[0].c_str()) == 0){
						lcFile<<0<<endl;
						//set flag indicating that we will have to get a constant now
						//arlc[curline] = 0;
						//curline++;
						chkconst = true;
						
						generateIC(word,outFile);
						whatis = -1;
						word = "";
						continue;
					}else{
						cout << "Code must start with start!\n Aborting...";
						return -1;
					}
				}//end start check
				//check for end there should be nothing after end and literals are to be processed and lc to be manipulated
				else if (strcmp(word.c_str(), ad[1].c_str()) == 0){
					lcFile<<0<<endl;
					lcprinted = true;
					//nothing else should be read other than newline or eof or space or tab
					while (fp.get(c)){
						if (c != ' ' || c != '\n' || c != '\t'){
							cout << "Error there should be nothing after end!\n";
							return -1;
						}
					}
					//if we complete this loop then everything is fine
					//process literals and terminate
					if(chksytb()!=0){
						cout<<"\nAborting!";
						return -1;
					}
					lc = process_lit(lc);
					generateIC(word,outFile);
					break;
				}else{
					//shoudnt be reg
					if (is_reg(word) == -1){
						//can be any keyword
						//origin restart lc from here
						if (strcmp(word.c_str(), "ORIGIN") == 0){
							getValueFlag = true;
							lcFile.seekp(oldpos); 
							//arlc[curline] = 0;
							lcFile<<0<<endl;
							lcprinted = true;
						}
						//ltorg	process literals
						else if (strcmp(word.c_str(), "LTORG") == 0){
							lc = process_lit(lc);
							//arlc[curline] = 0;
							lcFile<<0<<endl;
							lcprinted = true;
						}
						//equ change lc for tht instruction only
						else if (strcmp(word.c_str(), "EQU") == 0){
							//arlc[curline] = 0;
							lcFile<<0<<endl;
							lcprinted = true;
						}
						//for ds add const to lc
						else if(strcmp(word.c_str(), "DS") == 0)
							afterds = true;

						//for other inst inc lc by 1
						else
							lc++;
					}
				}
				line = 0; //reset the line flag afer reading keyword
			}


//**************************non keywords*******************************************//


			else if (is_keyword(word) == -1){ 
				//can be label or literal or constant
				//if line has started and its the first word which is not a keyword then consider it as a label and keep the line flag 1;
				if (line == 1){
					//word is symbol and it is at start hence
					//check whether it is already in table
					if (in_sytb(word, lc) == -1){
						sytb[it_sytb].sym = word;
						sytb[it_sytb].addr = lc;
						it_sytb++;
						prevsymb = true;
						generateIC(word,outFile);
						word = " ";
					}
				}else{
					//after , next word will be a symb or literal so we set a flag anticipating the situation
					if (commaFlag){
						//literal starts with ='
						if (word[0] == '=' && word[1] == '\'' ){
							//store in literal pool
							if (in_litpt(word) == -1){
								litpt[it_litpool++] = word;
								///generateIC(word,outFile);
							}
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
				//cout<<"curline: "<<curline<<"lc: "<<lc<<endl;
				//if(!lcprinted && (strcmp(word.c_str(), "ORIGIN") != 0) )
				//	arlc[curline] = lc;
				curline++;
			}
			if(word != "")
				generateIC(word,outFile);
				
				printsyminop = false;
			if(c == '\n'){
				outFile<<endl;
				if(!lcprinted){
					oldpos = lcFile.tellp();
					lcFile<<lc<<endl;
				}
				else
					lcprinted = false;
			}
			whatis = -1;
			word = "";
		} //end for

		//skip dividers
		if (c == ' ' || c == '\n' || c == '\t' || c == ',')
			continue;
		//add character to word
		word += c;
	}

	fp.close();
	print_sytb();
	print_litpl();
	print_poolt();
	print_littb();
	//cout<<"\t\tline:"<<curline;
	
	
	return 0;
}

//returns 0 if all symjbols have been declared else returns -1;
int chksytb(){
	for(int i=0;i<it_sytb;i++){
		if(sytb[i].addr == 0)
			cout<<"No delcleration of symbol "<<sytb[i].sym<<" found!";
	}
	return 0;
}


//generates and writes ic to file per keyword
int generateIC(string word,ofstream& outFile){
	//mne
	//
	if(word == " ")
		return 0;
	//outFile<<"word:"<<word;
	if(whatis == 0)
		outFile<<"(IS,"<<key_ind<<")\t";
	//ad
	else if(whatis == 1)
		outFile<<"(AD,"<<key_ind<<")\t";	
	//dl
	else if(whatis == 2)
		outFile<<"(DL,"<<key_ind<<")\t";
	//reg
	else if(whatis == 3)
		outFile<<"("<<key_ind<<")\t";
	//constant
	else if(is_constant(word) != -1)
		outFile<<"(C,"<<word<<")\t";
	//literal
	else if (word[0] == '=' && word[1] == '\'' ){
		if(loc_littb(word) != -1)
			outFile<<"(L,"<<loc_littb(word)<<")\t";
	}
	//symb
	else if( (in_sytb(word,0) != -1) && printsyminop)
		outFile<<"(S,"<<in_sytb(word,0)<<")\t";
	printsyminop = true;
}

int loc_littb(string word){
	//if its in littab
	if (it_lit != 0){
		for (int it = 0; it < it_lit; it++){
			if(strcmp(word.c_str(),lit[it].literal.c_str()) == 0)
				return it;
		}
	}
	//if its in lit pool
	int loc = 0;
	if(it_lit!=0)
		loc += it_lit;
	for (int it = 0; it < it_litpool; it++){
		if(strcmp(word.c_str(),litpt[it].c_str()) == 0)
			return loc += it;
	}
	return -1;
}
//get value of expression after equ or origin
int getVal(string word){
	int lc2 = 0;
	lc2 = is_constant(word);
	if(lc2 != -1)
		return lc2;
	size_t loc;
	string symb = "";
	string con = "";
	loc = word.find('+');
	if(loc != string::npos){
		//seperate constant and symbol
		for(int i=0;i<strlen(word.c_str());i++){
			if(i<loc)
				symb += word[i];
			else if(i>loc)
				con += word[i];
		}
		//get and add the values
		if(symVal(symb) != -1 && is_constant(con) != -1){
			//cout<<"return "<<symVal(symb)+is_constant(con);
			return symVal(symb)+is_constant(con);
		}
		else
			cout<<"Illegle Operation";
	}
	else
		return symVal(symb);
}

//returns address of symbol
int symVal(string word){
	for (int it = 0; it < it_sytb; it++)
		if (strcmp(sytb[it].sym.c_str(), word.c_str()) == 0)
			return sytb[it].addr;
	return -1;
}
//handle literal
int process_lit(int addr){
	//print_litpl();
	//add new index of lit tabel to pool tabel
	if(it_litpool == 0){
		cout<<"NOTHING TO PROCESS IN LITERAL POOL!";
		return -1;
	}
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
	if(!isdigit(it))
		return -1;
	int i = 1;
	while (i != strlen(word.c_str())){
		if (!isdigit(it))
			return -1;
		i++;
		it = word[i];
	}
	return atoi(word.c_str());
}
