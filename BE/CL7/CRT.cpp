#include<iostream>
#include<vector>
using namespace std;


int calcXi(int n,int ni)
{
    int i = 1;
    while((n * i) % ni != 1)
        i++;

    return i;
}


int crt(vector<int> n1, vector<int> n2){
	int N = 1, i, binixi_total = 0;
	vector<int> ni;
	vector<int> xi;
	vector<int> binixi;
	
	//calculate N
	for(i = 0; i < n2.size(); i++)
		N *= n2.at(i);
		
	//calculate Ni's
	for(i = 0; i < n2.size(); i++){
        int temp = N / n2.at(i);
        ni.push_back(temp);
    }
    
    //calculate xi
    for(i = 0; i < n1.size(); i++){
    	int temp = calcXi(ni.at(i), n2.at(i));
    	xi.push_back(temp); 
    }
    
    //calculate binixi and its total
    for(int i = 0; i < n1.size(); i++)
    {
        int temp = n1.at(i) * ni.at(i) * xi.at(i);
        binixi.push_back(temp);
        binixi_total += temp;
    }
    
    return (binixi_total % N);
}


int main(){

	vector<int> no1;
	vector<int> no2;
//	no1.push_back(2);
//	no1.push_back(4);
//	no1.push_back(5);
//	
	int max = 3,t1, t2;
	while(max--){
		cout<<"Enter values of expression: ";
		cin>>t1>>t2;
		no1.push_back(t1);
		no2.push_back(t2);
	}
//	no2.push_back(3);
//	no2.push_back(5);
//	no2.push_back(7);
	
	cout<<"x = "<<crt(no1,no2)<<endl;
	return 0;
}
