/*
	MAX AND MIN IN ARRAY USING DI
	IF ARRAY IA ALREADY SORTED DIRECTLY DISPLAY ELSE 
*/

#include<iostream>
using namespace std;

int MIN = 999;
int MAX = 0;

//accept the array
void accept(int ar[],int sz){
	int i;
	for(i = 0;i < sz; i++){
		cout<<"Enter element: ["<<i+1<<"] : ";
		cin>>ar[i];
	}
}

//display the array
void display(int ar[], int sz){
	cout<<"Array elements: ";
	for(int i = 0;i < sz; i++)
		cout<<ar[i]<<"\t";
}

//finds MIN and MAX of an array
int minmax(int ar[],int start,int end){
	//if there is only one element in array
		
	if(start == end){
		//MIN
		if(MIN > ar[start])
			MIN = ar[start];
		if(MAX < ar[start])
			MAX = ar[start];
		return ar[start];
	}
	//else if two elements
	else if(end == start+1){

		if(ar[start] < ar[end]){
			if(MIN > ar[start])
				MIN = ar[start];
			if(MAX < ar[end])
				MAX = ar[end];
		}else{
			if(MIN > ar[end])
				MIN = ar[end];
			if(MAX < ar[start])
				MAX = ar[start];
		}
		return 0;
	}else{
		int mid = (start + end) / 2;
		cout<<"\tstart "<<start<<" mid "<<mid;
		minmax(ar,start,mid);
		cout<<"\tmid "<<mid+1<<" end "<<end;
		minmax(ar,mid+1,end);
		
	}
}

//controller
int main(){
	
	int sz=0;
	//accept array size
	cout<<"Enter size of array: ";
	cin>>sz;
	//validate array size
	while(sz < 1){
		cout<<"Enter a valid size of array: ";
		cin>>sz;
	}
		
	int ar[sz];

	accept(ar,sz);
	display(ar,sz);
	minmax(ar,0,sz-1);
	cout<<"\nMIN: "<<MIN<<" MAX: "<<MAX<<endl;
	return 0;
}
