#include<iostream>
using namespace std;

void merge(int *ar,int start,int mid,int end){
	
	//make a temp array of same size
	//we pass logical end ie 9 for size 10 hece add 1
	int temp[end-start+0];

	//our first array will go from start to mid and second will fo from mid+1 to end
	//hene we take a tracer from start and another from mid+1 and one more for temp array
	int i = start,j = mid+1, k = 0;
	
	//take smalleer ele from both array and put it in temp array
	while(i <= mid && j <= end){
		if(ar[i] < ar[j])
			temp[k++] = ar[i++];
		else
			temp[k++] = ar[j++];
	}
	
	//put all remaining elements of left array in temp
	while(i <= mid)
		temp[k++] = ar[i++];
		
	//put all remaining elements of right array in temp
	while(j <= end)
		temp[k++] = ar[j++];
	
	//copy temp to originar array
	//we assign start to i insted of 0 because we want to insert sorted elements at appropriate position in main array
	//hence we stubstract start from i for temp to get proper location of element in temp
	for(i = start;i <= end;i++)
		ar[i] = temp[i-start];
	
}

void mergeSort(int *ar,int start,int end){
	
	int mid=0;
	
	if(start < end){
		//get mid
		mid = (start+end)/2;
		//start sort on left part
		mergeSort(ar,start,mid);
		//start sort on right part
		mergeSort(ar,mid+1,end);
		//do the merge
		merge(ar,start,mid,end);
	}
}

int main(){
	int *ar;
	int i=0;
	ar = new int[10];
	
	cout<<"Enter 10 random array elements:";
	
	for(i = 0;i < 10;i++){
		cin>>ar[i];
	}
		
	mergeSort(ar,0,9);
	
	cout<<"Sorted array!:\n";
	for(i = 0;i < 10;i++)
		cout<<*(ar + i)<<"\t";
}
