#include<iostream>
#include<string.h>
#include<bits/stdc++.h>
using namespace std;

void accept(int *graph,int v){
	for(int i=0;i<v;i++){
		for(int j=0;j<v;j++){
			if(i==j){
				*(graph+i*v+j) = 0;
				continue;
			}
			cout<<"Enter wight for edge : ["<<i<<"]["<<j<<"] : ";
			cin>>*(graph+i*v+j);
		}
	}
}

void display(int *graph,int v){
	for(int i=0;i<v;i++){
		for(int j=0;j<v;j++)
			cout<<*(graph+i*v+j)<<"\t";
		cout<<endl;
	}
}

//bellman ford algo considering source as 0
void bellmanFord(int *graph){
	
	int dist[]
	
}

int main(){

	int v;
	cout<<"Enter no of vertices: ";
	cin>>v;
	
	int *graph = new int[v*v];
	
	accept(graph,v);
	display(graph,v);
	bellmanFord()
	return 0;
}
