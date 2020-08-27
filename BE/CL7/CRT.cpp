#include<iostream>
#include<vector>
using namespace std;


int calcXi(int N,int ni)
{
    int i = 1;
    while((N * i) % ni != 1)
        i++;

    return i;
}


int crt(vector<int> n1, vector<int> n2){
	int N = 1, i, binixi_total = 0;
	vector<int> ni;
	vector<int> xi;
	vector<int> binixi;
	
	//calculate n
	for(i = 0; i < n2.size(); i++)
		N *= n2.at(i);
		
	//calculate Nis
	for(i = 0; i < n2.size(); i++){
        int temp = N/ n2.at(i);
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
	no1.push_back(3);
	no1.push_back(1);
	no1.push_back(6);
	
	vector<int> no2;
	no2.push_back(5);
	no2.push_back(7);
	no2.push_back(8);
	
	cout<<crt(no1,no2);
	return 0;
}
