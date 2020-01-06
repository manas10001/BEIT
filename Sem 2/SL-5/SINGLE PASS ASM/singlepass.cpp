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

#include <iostream>
#include <fstream>
#include <string>
#include <bits/stdc++.h>

using namespace std;

//ARRAY TO STORE MNEMONICS, ASSEMBLER DIRECTIVES AND DECLERATIVES
string mne[11] = {"STOP", "ADD", "SUB", "MULT", "MOVER", "MOVEM", "COMP", "BC", "DIV", "READ", "PRINT"}; //START FROM 00
string ad[5] = {"START", "END", "ORIGIN", "EQU", "LTORG"};												 //START FROM 01
string dl[2] = {"DC", "DS"};																			 //START FROM 01
string reg[3] = {"AREG", "BREG", "CREG"};																 //start from 1

//TABLES

//SYMBOL TABLE
int it_sytb = 0;
//string sytb_sym[100];//int sytb_addr[100];
struct symbtb
{
	string sym;
	int addr;
};

symbtb sytb[100];

//LITERAL TABLE
int it_lit = 0;
//int lit_no[100];//string lit_lit[100];//int lit_addr[100];

struct littb
{
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
int curline = 0;

//FLAG TO ENCOUNTER ,
bool commaFlag = false;

//FUNCTION DECLARATIONS			    //returns
int is_keyword(string word);		//-1 for fail else code
int is_mne(string word);			//-1 for fail else code
int is_ad(string word);				//-1 for fail else code
int is_dl(string word);				//-1 for fail else code
int is_reg(string word);			//-1 for fail else code
int in_sytb(string word, int addr); //returns -1 for fail else 1
int is_constant(string word);		//-1 fro fail else integer value
void printsytb();					//print syboltb
void print_litpl();					//print literal pool

int main()
{
	fstream fp;
	fp.open("ASM.txt");

	char c;
	string word = "";
	int line = 1;		   //0 for line end; 1 for line start
	int lc = 0;			   //for location counter
	bool chkconst = false; //will be set true if we want to check the next word for constant

	while (fp.get(c))
	{
		//cout<<"in loop!\n";

		//possible breakpoints between words
		if (c == ' ' || c == '\n' || c == '\t' || c == ',')
		{
			//cout<<word<<c;
			//if we read start we will check the next word for being constant
			//this block will execute only at start of code
			if (chkconst == true)
			{
				cout << "checking const!\t";
				cnst = is_constant(word);
				if (cnst == -1)
				{
					//not a constant hence end exec
					cout << "Invalid start of program!\nThere must be a memory location specified after start!\nAborting";
					return -1;
				}
				else
				{
					//assign const to lc
					cout << "const: " << cnst;
					lc = cnst;
				}
				chkconst = false;
				word = "";
				continue;
			}

			//check for word being keyword
			if (is_keyword(word) != -1)
			{
				//if its the start of code then lc=0 & 1st word should be AD and it should be start
				//cout<<endl<<word<<c;
				if (lc == 0 && whatis == 1)
				{

					//if the word is start then we have to get lc
					if (strcmp(word.c_str(), ad[0].c_str()) == 0)
					{
						//set flag indicating that we will have to get a constant now
						chkconst = true;

						//reset the word
						word = "";
						continue;
					}
					else
					{
						cout << "Code must start with start!\n Aborting...";
						return -1;
					}
				}
				//check for end there should be nothing after end and literals are to be processed and lc to be manipulated
				else if (strcmp(word.c_str(), ad[1].c_str()) == 0)
				{
					//nothing else should be read other than newline or eof or space or tab
					while (fp.get(c))
					{
						if (c != ' ' || c != '\n' || c != '\t')
						{
							cout << "Error there should be nothing after end!\n";
							return -1;
						}
					}
					//if we complete this loop then everything is fine
					//process literals and terminate
					break;
				}
				else
				{
					//can be any keyword
					lc++;
				}
				line = 0; //reset the line flag afer reading keyword
			}
			else
			{ //if the word is not a keyword then...
				//can be label or literal or constant
				//if line has started and its the first word which is not a keyword then consider it as a label and keep the line flag 1;
				if (line == 1)
				{
					//word is symbol and it is at start hence
					//check whether it is already in table
					if (in_sytb(word, lc) == -1)
					{
						sytb[it_sytb].sym = word;
						sytb[it_sytb].addr = lc;
						cout << "symb: " << word << endl;
						it_sytb++;
					}
				}
				//after , next word will be a symb or literal so we set a flag anticipating the situation
				if (c == ',')
				{
					commaFlag = true;
					cout << "true\t";
				}
				if (commaFlag)
				{
					//literal starts with =
					if (word[0] == '=')
					{
						//store in literal pool
						litpt[it_litpool++] = word;
					}
					else
					{
						//it must be a symbol or const
						if (is_constant(word) == -1)
						{
							cout << "not const " << word;
							//then its a symbol we have to check whether its already in the table
							if (in_sytb(word, 0) == -1)
							{
								//and if it is not add it in table without address
								sytb[it_sytb].sym = word;
								sytb[it_sytb].addr = 0;
								cout << "symb nad: " << word << endl;
								it_sytb++;
							}
						} //else its a constant
					}
					commaFlag = false;
				}
			}
			if (c == '\n')
			{
				line = 1;
				curline++;
			}
			word = "";
		}

		//skip dividers
		if (c == ' ' || c == '\n' || c == '\t' || c == ',')
			continue;
		//add character to word
		word += c;

		//cout<<c;
	}

	fp.close();

	printsytb();
	print_litpl();
	return 0;
}

//check if a symbol is already in the table if it is and has no address then assign it address
int in_sytb(string word, int addr)
{
	for (int it = 0; it < it_sytb; it++)
	{
		cout << "check " << word;
		if (strcmp(sytb[it].sym.c_str(), word.c_str()) == 0)
		{
			//set address if not already set
			if (sytb[it].addr == 0 && addr != 0)
			{
				sytb[it].addr = addr;
				cout << "Set ad to " << sytb[it].sym;
			}
			return 1;
		}
	}
	return -1;
}

//prints symbol table
void printsytb()
{
	cout << "\n\tSymbol Table:\n";
	for (int it = 0; it < it_sytb; it++)
		cout << " sym: " << sytb[it].sym << " addr: " << sytb[it].addr << endl;
}

void print_litpl()
{
	cout << "Literal pool:";
	for (int it = 0; it < it_litpool; it++)
		cout << " lit: " << litpt[it] << endl;
}

//returns -1 if word is not keyword else returns its code
int is_keyword(string word)
{
	int ret = -1;

	ret = is_mne(word);
	//	cout<<word<<" ret mne : "<<ret<<endl;
	if (ret == -1)
		ret = is_ad(word);
	//	cout<<word<<" ret ad : "<<ret<<endl;
	if (ret == -1)
		ret = is_dl(word);
	//	cout<<word<<" ret dl : "<<ret<<endl;
	if (ret == -1)
		ret = is_reg(word);
	//	cout<<word<<" ret reg : "<<ret<<endl;
	return ret;
}

//returns 0 if word is not mnemonic else returns its code
int is_mne(string word)
{
	int i = 0;

	for (i = 0; i < 11; i++)
	{
		if (strcmp(word.c_str(), mne[i].c_str()) == 0)
		{
			whatis = 0;
			return i;
		}
	}
	return -1;
}

//returns 0 if word is not assembler directive else returns its code
int is_ad(string word)
{
	int i = 0;

	for (i = 0; i < 5; i++)
	{
		if (strcmp(word.c_str(), ad[i].c_str()) == 0)
		{
			whatis = 1;
			return i + 1;
		}
	}
	return -1;
}

//returns 0 if word is not decleration else returns its code
int is_dl(string word)
{
	int i = 0;

	for (i = 0; i < 2; i++)
	{
		if (strcmp(word.c_str(), dl[i].c_str()) == 0)
		{
			whatis = 2;
			return i + 1;
		}
	}
	return -1;
}

//returns 0 if word is not register else returns its code
int is_reg(string word)
{
	int i = 0;

	for (i = 0; i < 3; i++)
	{
		if (strcmp(word.c_str(), reg[i].c_str()) == 0)
		{
			whatis = 3;
			return i + 1;
		}
	}
	return -1;
}

//if the passed string is a constant retrurns its integer value else returns -1
int is_constant(string word)
{
	char it = word[0];

	int i = 1;

	while (i != strlen(word.c_str()))
	{
		if (!isdigit(it))
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
