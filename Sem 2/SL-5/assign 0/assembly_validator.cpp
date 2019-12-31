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
	string ar[]={"START","END","MOV","READ","WRITE","ADD","DIV"};
	
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
			for(int i = 0 ; i < 7 && read!=1 ; i++){
				if(strcmp(word.c_str(),ar[i].c_str())==0)
				{
					cout<<word<<endl;
					read = 1;	//read will be used as a flag to indicate that the name of operation is read and no further reading is necessary for that line
				}
				if(read == 1){
					
						if( strcmp(word.c_str(),"START")==0 ){
							//when start comes the next char should be newline char else its error
							if(c == '\n')
								break;
							else{
								cout<<"\n\tERROR ON LINE "<<lc<<" INVALID OPERATION THERE SHOUDNT BE ANYTHOING AFTER START";
								return -1;
							}
						}
						else if( strcmp(word.c_str(),"END") == 0 ){
							//there shoud be nothing after end
							if(fp.get(c)){
								cout<<"\n\tERROR ON LINE "<<lc<<" INVALID OPERATION THERE SHOULD BE NOTHING AFTER END";
									return -1;
								}
						}
						else if( strcmp(word.c_str(),"READ") == 0 ){
							//READ WRITE DIV TAKE ONLY ONE OPERATOR
							if(c == '\n')
								break;
								
							while(fp.get(c)){
								if(c == '\n')
									break;
								if(c == ',' || c == ' '){
									cout<<"\n\tERROR ON LINE "<<lc<<" ONLY ONE OPERATOR ALLOWED";
									return -1;
								}
							}								
						}
								
					
				}
			}
			if(read == 0){
				cout<<"\n\tERROR ON LINE "<<lc<<" INVALID OPERATION "<<word;
				return -1;
			}
			read = 1;	//read will be used as a flag to indicate that the name of operation is read and no further reading is necessary for that line
				
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

