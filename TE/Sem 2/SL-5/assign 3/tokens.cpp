#include<iostream>
#include<fstream>
#include<string.h>

using namespace std;


int main(){
	fstream inpt;
	inpt.open("input.txt");
	
	string str;
	
	while(getline(inpt,str))
	{
		int len = str.length();
		char c[len+1];
		strcpy(c,str.c_str());
		char *token = strtok(c," ( )");
		while(token){
			cout<<token<<"\t\t";
			token = strtok(0," ( ) , ");
		}
		cout<<endl;
	
	}
	return 0;
}
