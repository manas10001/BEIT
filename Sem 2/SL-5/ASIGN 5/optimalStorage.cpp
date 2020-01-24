/*
	OPTIMAL STORAGE ON TAPES PROBLEM
*/

#include<iostream>
using namespace std;

//structure to store programs and their runtimes
typedef struct prog{
	int no;
	int len;
}prog;


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
void store(prog pr[],int sz){

	int capacity = 1;
	//create tape of user defined size
	cout<<"Enter capacity of tape: ";
	cin>>capacity;
	
	//validate array size
	while(capacity < 1){
		cout<<"Enter a valid capacity of tape: ";
		cin>>capacity;
	}
	
	prog tape[sz];
	
	//if the array isnt ascending sorted sort it
	if(isSorted(pr,sz) != 1){
		//sort it 
		mergeSort(pr,0,sz-1);
	}
	
	//copy programs on tape until its capacity fills
	int count = 0;	//counts how many programs are stored on tape
	while( (capacity - pr[count].len) > 0 && count != sz){
		tape[count] = pr[count];
		capacity -= pr[count++].len;
		cout<<"cap: "<<capacity<<endl;
	}
	
	cout<<"\n\tPrograms on Tape:\n";
	display(tape,count);
	
	int mrt = 0;
	//calculate mrt
	for(int i = 0;i < count; i++){
		for(int j = 0;j <= i;j++){
			mrt += tape[j].len;
		}
	}
	mrt = mrt/count;
	cout<<"\n\nMRT: "<<mrt<<endl;
	//sum+=i+sum
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
		
	prog pr[sz];
	
	accept(pr,sz);
	cout<<"\n\tPrograms:\n";
	display(pr,sz);

	store(pr,sz);

	return 0;
}
