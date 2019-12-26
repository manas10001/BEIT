/*
program to validate an assembly program
must start with start end with end
unary op: read print
binary move 

*/


#include<iostream>
#include<string.h>
#include<fstream>
using namespace std;
int main(){
	string ar[]={"START","END","MOV","READ","WRITE"};
	
	fstream fp;
	fp.open("asm.txt");
	
	int lc=1,endFlag=0,read=0;
	char c;
	string word="";
	
	//iterate char by char thr file
	while(fp.get(c)){

		//on space or newline check the word	
		if(c == ' ' || c == '\n'){
			//check the word only if read is 0
			for(int i = 0 ; i < 5 && read!=1 ; i++){
				if(strcmp(word.c_str(),ar[i].c_str())==0)
				{
					cout<<word<<endl;
					read = 1;	//read will be used as a flag to indicate that the name of operation is read and no further reading is necessary for that line
				}
			}
			if(read == 0)
				cout<<"ERROR ON LINE "<<lc<<" INVALID OPERATION "<<word;
				
		word="";
		if(c == '\n'){
			lc++;
			read = 0;
		}
		continue;
		}
		
		
		word+=c;
	}
	
	return 0;
}

