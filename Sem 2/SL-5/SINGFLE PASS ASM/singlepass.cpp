/*
	SINGLE PASS ASSEMBLER
*/

#include<iostream>
#include<fstream>
#include<string.h>

using namespace std;

//ARRAY TO STORE BASIC MNEMONICS 
string mne[] = {"PRINT","ADD","SUB","READ","MOVE","MOVER","PRINT","START","STOP"};

int is_mne(string word);

int main(){
	fstream fp;
	fp.open("ASM.txt");
	
	char c;
	string word="";
	
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
	
	fp.close();
	return 0;
}
