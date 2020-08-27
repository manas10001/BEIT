/*
	program to count keywords in program
	
*/

#include<iostream>
#include<fstream>
#include<string.h>
#include<bits/stdc++.h>
using namespace std;
int main(){
	string ar[] = {"auto","break","case","char","const","continue","default","do","double","else","enum","extern","float","for","goto",
		"if","int","long","register","return","short","signed","sizeof","static","struct","switch","typedef","union","unsigned",
		"void","volatile","while"};
		
	int ctr=0;
	string word="";
	fstream fp;
	char c;
	fp.open("wc.c");
	
	//traverse by char
	while(fp.get(c)){
		//characters that might appear at start of line or in expressions that might contain keywords
		if(c=='}' || c==')' || c=='='){
			word="";
			continue;
		}
		//characters before which keyword might occur
		if(c == ' ' || c=='(' || c=='{' || c== '\n' || c=='\t' ){
			//check
			//loop to check
			for(int i=0;i<32;i++){
				if(strcmp(word.c_str(),ar[i].c_str())==0)
				{
					cout<<word<<endl;
				}
			}
			//cout<<endl<<word;
			word="";
			continue;
		}
		word+=c;
	}
}
