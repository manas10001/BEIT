/*
IMPLEMENT LEXICAL ANALYSER FOR SUBSET OF C
*/

#include<iostream>
#include<fstream>
#include<string.h>
#include<bits/stdc++.h>

using namespace std;

//flag
bool quoteflag = false;

//sytb / idtb
vector<string> sytb;

//littb
vector<string> littb;

//terminal table 	contains all terminals except keywords
string arterm[] = {"#","include","<","stdio.h",">","conio.h","(",")","printf","scanf","[","]","\"","\'",",",";","\\","","","",};

vector<string> trmtb(arterm,arterm+5);

//ust
struct ust{
	string cls,symb;
	int index;
};

int is_constant(string str){
	char it = str[0];
	if(!isdigit(it))
		return -1;
	int i = 1;
	while (i != strlen(str.c_str())){
		if (!isdigit(it))
			return -1;
		i++;
		it = str[i];
	}
	return atoi(str.c_str());
}

//set flags insted
int chkchar(char c){

	char arss[] = {'#' , '<' , '>' , '{' , '}' , '(' , ')' , '\'' , '"' , ',' , ';'};
	vector<char> sym(arss,arss+(sizeof(arss)/sizeof(arss[0])));
	

	if(c=='+' || c=='-' || c=='*' || c=='/' || c=='='|| c=='&'|| c=='%' ){
		cout<<"\t\tOPERATOR"<<endl;
		return 1;
	}else if(isdigit(c)){
		cout<<"\t\tLITERAL"<<endl;
		return 1;
	}else if(find(sym.begin(),sym.end(),c) != sym.end()){
		cout<<"\t\tSPECIAL CHARACTER"<<endl;
		return 1;
	}else{
		cout<<"\t\tSYMBOL"<<endl;
		return 1;
	}
	return 0;
}

int chkstr(string str){
	string key[] = {"auto", "break","case","char", "continue", "do","default","const", "double","else","enum","extern","for","if","goto","float","int","long", "register", "return", "signed","static","sizeof","short", "struct", "switch", "typedef","union","void", "while", "volatile", "unsigned"};
	vector<string> ky(key,key+(32));
	
	string trm[] = {"include","stdio.h","void","main"};
	vector<string> tkn(trm,trm+4);
	
	/*if(strlen(str.c_str())==1){
		chkchar(str[0]);
		return 1;
	}*/
	if(find(tkn.begin(),tkn.end(),str) != tkn.end()){
		cout<<"\t\tTKN\n";
		return 1;
	}
	else if(find(ky.begin(),ky.end(),str) != ky.end()){
		cout<<"\t\tKEYWORD\n";
		return 1;
	}
	else if(is_constant(str)!=-1 || quoteflag){
		cout<<"\t\tLITERAL \n";
		return 1;
	}
	else{
		cout<<"\t\tVARIABLE\n";
		return 1;
	}
	return 0;
}

int main(){
	fstream inpt;
	inpt.open("sum.c");
	
	string str="";
	char c;
	
	char ar[] = {' ' , '#' , '<' , '>' , '{' , '}' , '(' , ')' , '+' , '-' , '*' , '/' , '\'' , '"' , ',' , ';','&','=','[',']'};
	vector<char> sep(ar,ar+(sizeof(ar)/sizeof(ar[0])));
	
	while(inpt.get(c)){
		
		if(find(sep.begin(),sep.end(),c) != sep.end() || c == '\n' || c == '\t'){
			//cout<<"\t\tseP: "<<c<<endl;
			//cout<<"string empty?: "<<str.empty();
			
			if(find(sep.begin(),sep.end(),c) != sep.end()){
				if(c=='"' && !quoteflag){
					quoteflag = true;
					cout<<c<<"\t\tSPECIAL CHARACTER"<<endl;				
				}else if (c=='"' && quoteflag)
					quoteflag = false;
				if(quoteflag){
					//cout<<"\t\t\t "<<str<<endl;
					if(c==' ' || (find(sep.begin(),sep.end(),c) == sep.end() && c != '\n' && c != '\t') ){
						str+=c;
						continue;
					}
				}else{
					if(str!=""){
						cout<<str;
						chkstr(str);
					}
					if(c!=' '){
						cout<<c;
						chkchar(c);
						
					}
					str.clear();
				}
			}
				//cout<<"char: "<<c<<endl;
			/*}else{
				cout<<str<<endl;
				str.clear();	
				//cout<<"\t\t\t s: "<<str<<endl;
			}*/
			
			//continue;
		}
		if(find(sep.begin(),sep.end(),c) == sep.end() && c != '\n' && c != '\t' )
			str+=c;
		//cout<<c<<endl;
	}
	
	/*while(getline(inpt,str))
	{
		int len = str.length();
		char c[len+1];
		strcpy(c,str.c_str());
		char *token = strtok(c," ");
		while(token){
			if
			cout<<token<<"\t\t";
			token = strtok(0," ");
		}
		cout<<endl;
		
	}*/
	return 0;
}
