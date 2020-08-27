/*
	OPTIMAL STORAGE ON TAPES PROBLEM MULTI TAPE
*/

#include<iostream>
#include<bits/stdc++.h>
using namespace std;

//structure to store programs and their runtimes
typedef struct prog{
	int no;
	int len;
}prog;

typedef struct tapes{
	int no;
	int capacity;
	int r_capacity;
	vector<prog> progs;
	int count;	//cont no of progs stored in tape
}tapes;
//maintain no of tapes
int TCOUNT = 1;

//accept the array
void accept(prog pr[],int sz){
	int i;
	for(i = 0;i < sz; i++){
		cout<<"Enter length of program: ["<<i+1<<"] : ";
		cin>>pr[i].len;
		pr[i].no = i;
	}
}

//display the array
void display(prog pr[], int sz){
	cout<<"\nNumber\tLength\n";
	for(int i = 0;i < sz; i++)
		cout<<pr[i].no+1<<"\t"<<pr[i].len<<endl;
	cout<<endl;
}

//CALCULATE MRT 
int calculateMrt(vector<prog> tape,int count){
	float mrt = 0;
	//calculate mrt
	for(int i = 0;i < count; i++){
		for(int j = 0;j <= i;j++){
			mrt += tape[j].len;
		}
	}
	mrt = (float)mrt/(float)count;
	return mrt;
}


//DISPLAYS CONTENT OF TAPES
void displayTape(struct tapes tape){
	cout<<"-------------Contents of tape------------ \n";
	cout<<"Tape no: "<<tape.no<<endl;
	cout<<"Total capacity: "<<tape.capacity<<endl;
	cout<<"Utilized capacity: "<<tape.capacity - tape.r_capacity<<endl;
	cout<<"Programs in tape: "<<endl;
	cout<<"\nNumber\tLength\n";
	for(int i = 0;i < tape.count; i++)
		cout<<tape.progs[i].no+1<<"\t"<<tape.progs[i].len<<endl;
	cout<<endl;
	cout<<"MRT: "<<calculateMrt(tape.progs,tape.count)<<endl<<endl;
}

//merge function for merge sort
void merge(prog *pr,int start,int mid,int end){
	prog temp[end-start+0];
	int i = start,j = mid+1, k = 0;
	
	while(i <= mid && j <= end){
		if(pr[i].len < pr[j].len)
			temp[k++] = pr[i++];
		else
			temp[k++] = pr[j++];
	}
	while(i <= mid)
		temp[k++] = pr[i++];
		
	while(j <= end)
		temp[k++] = pr[j++];
	
	for(i = start;i <= end;i++)
		pr[i] = temp[i-start];	
}

//merge sort
void mergeSort(prog *pr,int start,int end){
	
	int mid=0;
	
	if(start < end){
		mid = (start+end)/2;
		mergeSort(pr,start,mid);
		mergeSort(pr,mid+1,end);
		merge(pr,start,mid,end);
	}
}

//function to check if the provided array is already sorted or not!
//returns -1 for non sorted //1 for aescending sorted //2 for descending sorted
int isSorted(prog pr[],int sz){
	//if struct has only one element
	if(sz == 1)
		return 1;

	//consider that the structure is already sorted then check whether it is ascending or descending based on first two elements
	
	//corner case: if several elements at start are same skip them
	int it = 0;
	
	while(pr[it].len == pr[it + 1].len)
		it++;
	
	//desc else aesc
	if(pr[it].len > pr[it+1].len){
		for(int i = it; i < sz-1 ; i++ ){
			if(pr[i].len < pr[i+1].len)
				return -1;
		}
		return 2;			//desc
	}else{
		for(int i = it; i < sz-1 ; i++ ){
			if(pr[i].len > pr[i+1].len)
				return -1;
		}
		return 1;			//aesc
	}
	return -1;
}

//function to handle optimal storage on a tape
void store(prog pr[], int sz, tapes tape[]){
	//if the array isnt ascending sorted sort it
	if(isSorted(pr,sz) != 1){
		//sort it 
		mergeSort(pr,0,sz-1);
		//display(pr,sz);
	}
	
	
	//add programs on tape
	int count = 0;	//counts how many programs are stored on tape
	//until there are prog available
	while(count < sz ){
		//until capacity of tape dosent exceed limit
		for(int i = 0;(count < sz) && (i < TCOUNT); i++) {
			
			if((tape[i].r_capacity - pr[count].len) > 0) {		
				tape[i].progs.push_back(pr[count]);
				tape[i].r_capacity -= pr[count].len;
				tape[i].count++;
				count++;
			}else if(i==(TCOUNT-1)){
				break;
			}
		}
		
	}
	
	//DISPLAY TAPES
	for(int j=0;j<TCOUNT;j++){
		displayTape(tape[j]);
	}
	
	//CALCULATE MRT
	
}


//controller
int main(){
	
	int sz=0,i=0;
	//accept array size
	cout<<"Enter size of array: ";
	cin>>sz;
	//validate array size
	while(sz < 1){
		cout<<"Enter a valid size of array: ";
		cin>>sz;
	}
		
	prog pr[sz];
	
	
	accept(pr,sz);
	cout<<"\n\tPrograms:\n";
	display(pr,sz);
	
	//accept tapes
	cout<<"Enter no of tapes: ";
	cin>>TCOUNT;
	while(TCOUNT < 1){
		cout<<"Enter a valid no of tapes: ";
		cin>>TCOUNT;
	}
	
	tapes tape[TCOUNT];

	for(i=0;i<TCOUNT;i++){
		cout<<"For tape: "<<i+1;
		cout<<"\nEnter tape capacity: ";
		cin>>tape[i].capacity;
		tape[i].no = i+1;
		tape[i].r_capacity = tape[i].capacity;
		tape[i].count = 0;
	}

	store(pr,sz,tape);

	return 0;
}
