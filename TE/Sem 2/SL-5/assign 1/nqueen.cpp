#include<iostream>
#include<bits/stdc++.h>

using namespace std;

int ar[100],c=0;

void print(int n){
	int i,j;
	cout<<"\n------------------------------------------\nSequence of queens is: ";
	for(i = 1;i <= n;i++){
		cout<<"\t"<<ar[i];
	}
	
	//final matrix print:
	
	int mat[n][n];
	
	//fill the mat with queen pos from the array we have
	int k=0;
	
	//static array hence need to assign 0 at each location at start
	for(i=0;i<n;i++)
		for(j=0;j<n;j++)
			mat[i][j] = 0;
	
	//assign 1 at quens place for visualization
	for(i=1;i<=n;i++){
		//cout<<"fill: "<<k<<ar[i]<<endl;
		mat[k++][ar[i]-1]=1;
	}
	
	cout<<"\n\nFinal mat:\n\n";
	for(i=0;i<n;i++){
		for(j=0;j<n;j++){
			cout<<mat[i][j]<<"\t";
		}
		cout<<endl;
	}
	
	
}

//no is the no of queen to be placed and i has the current index of the array
bool place(int no,int i){
	int j;
		
	//we hve to check all the previous values of placed queens
	for(j = 1;j <= no-1;j++){
		//check the row and digonal
		//for digonal the x1(ar[j])-x2(i) and y1(j)-y2(k) shoudnt be same
		if( (ar[j] == i) ||  ( abs(ar[j]-i) == abs(j - no))  )
			return false;
	}
	return true;
}

void nqueen(int no,int n){
	int x,i;
	//if the function place returns true then we will place the queen at that loctaion else we will check the next location for the same queen
	for(i=1;i<=n;i++){
		//the function place will return a bool true or false stating whether we can place the queen or not
		if(place(no,i)){
			//place the queen 
			ar[no] = i;	
			
			//if the last queen has not yet been placed then give recursive call else print mai
			if(no == n){
				print(n);	
				c++;		//counts no of solutions	
			}else
				nqueen(no+1,n);
				
		}
	}
}

int main(){
	//accept no of queens
	int n;
	cout<<"Enter no of queens:";
	cin>>n;
	if(n<4){
		cout<<"There should be at least 4 queens!";
		return -1;
	}
	cout<<"Considering a "<<n<<"-"<<n<<" chessboard!"<<endl;

	//the function nqueen will take input the no of queen to be inserted and the total no of queens
	nqueen(1,n);
	cout<<"\nFinal count:"<<c<<endl;
}
