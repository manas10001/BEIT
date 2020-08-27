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
bool dbcharflag = false;
//sytb / idtb
vector<string> sytb;

//littb
vector<string> littb;

//terminal table 	contains all terminals except keywords
string arterm[] = {"auto", "break", "case", "char", "continue", "do", "default", "const", "double", "else", "enum", "extern","for","if","goto","float","int","long", "register", "return", "signed","static","sizeof","short", "struct", "switch", "typedef","union","void", "while", "volatile", "unsigned", "include", "stdio.h", "void", "main" , "printf", "scanf", "#", "\\", "+", "-", "*", "/", "++", "--", "==", "<", ">", "<=", ">=", ",", ";", "\"", "\'", "&", "<", ">"};

vector<string> trmtb(arterm,arterm+57);

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
		cout<<"\t\tTRM"<<endl;
		return 1;
	}else if(isdigit(c)){
		cout<<"\t\tLIT"<<endl;
		return 1;
	}else if(find(sym.begin(),sym.end(),c) != sym.end()){
		cout<<"\t\tTRM"<<endl;
		return 1;
	}else{
		if(quoteflag)
			cout<<"\t\tLIT \n";
		else
			cout<<"\t\tIDN\n";
		return 1;
	}
	return 0;
}

int chkstr(string str){
//	string key[] = {"auto", "break","case","char", "continue", "do","default","const", "double","else","enum","extern","for","if","goto","float","int","long", "register", "return", "signed","static","sizeof","short", "struct", "switch", "typedef","union","void", "while", "volatile", "unsigned"};
//	vector<string> ky(key,key+(32));
	
//	string trm[] = {"include","stdio.h","void","main"};
//	vector<string> tkn(trm,trm+4);
	
	/*if(strlen(str.c_str())==1){
		chkchar(str[0]);
		return 1;
	}*/
	if(find(trmtb.begin(),trmtb.end(),str) != trmtb.end()){
		cout<<"\t\tTRM\n";
		return 1;
	}
//	else if(find(ky.begin(),ky.end(),str) != ky.end()){
//		cout<<"\t\tKEYWORD\n";
//		return 1;
//	}
	else if(is_constant(str)!=-1){
		cout<<"\t\tLIT \n";
		return 1;
	}
	else{
		if(quoteflag)
			cout<<"\t\tLIT \n";
		else
			cout<<"\t\tIDN\n";
		return 1;
	}
	return 0;
}

int main(){
	fstream inpt;
	inpt.open("sum.c");
	
	string str="";
	string dbcharstr="";
	char c,prevchar;
	
	char ar[] = {' ' , '#' , '<' , '>' , '{' , '}' , '(' , ')' , '+' , '-' , '*' , '/' , '\'' , '"' , ',' , ';','&','=','[',']'};
	vector<char> sep(ar,ar+(sizeof(ar)/sizeof(ar[0])));
	
	while(inpt.get(c)){
		
		if(find(sep.begin(),sep.end(),c) != sep.end() || c == '\n' || c == '\t'){
			
			if(find(sep.begin(),sep.end(),c) != sep.end()){
				if(c=='"' && !quoteflag){
					quoteflag = true;
					cout<<c<<"\t\tTRM"<<endl;				
				}else if (c=='"' && quoteflag)
					quoteflag = false;
					
					//load entire string in double quotes in a variable 
				if(quoteflag){
					//cout<<"\t\t\t "<<str<<endl;
					if(c==' ' || (find(sep.begin(),sep.end(),c) == sep.end() && c != '\n' && c != '\t') ){
						str+=c;
						continue;
					}
				}else{
					//handle the string inside double quotes
					if(str!="" && c=='"'){
						cout<<str;
						cout<<"\t\tLIT\n";
					}
					else if(str!="" && !quoteflag){
						cout<<str;
						chkstr(str);
					}
					
					if(c!=' '){
						//cout<<"\t\t\tchar: "<<c<<" f: "<<dbcharflag<<endl; 
						if(dbcharflag){
							if(prevchar==c){
								dbcharstr += prevchar;
								dbcharstr += c;
								
								cout<<dbcharstr;
								chkstr(dbcharstr);
								
								dbcharstr.clear();
								dbcharflag = false;
								prevchar = '?';	//set to a random char
								continue;
							}else{
								cout<<c;
								chkchar(c);
								dbcharflag = false;
								prevchar='?';
							}
							dbcharflag = false;
						}
						//handle the double operators == ++ --
						
						else if( (c=='+' || c=='-' || c=='=') && (!dbcharflag) ) {
							dbcharflag = true;
							//cout<<"\t\t\ttrue for"<<c<<endl;
							prevchar = c;
							continue;
						}
						else{
							cout<<c;
							chkchar(c);
						}
					}
					str.clear();
				}
			}
		}
		if(find(sep.begin(),sep.end(),c) == sep.end() && c != '\n' && c != '\t' )
			str+=c;
		//cout<<c<<endl;
	}
	return 0;
}
