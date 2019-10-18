/*
ASIGN 9
PHONEBOOK USING FILE HANDLING BY LOW LEVEL SYSTEM COMMANADS
*/
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <fcntl.h>
#include <string.h>

typedef struct dat{
	char name[20];
	char phno[11];
	char addr[50];
}d;

int openIN(char fnme[20]){
	return open(fnme, O_CREAT | O_RDWR | O_APPEND, 0666);	
}

int openOUT(char fnme[20]){
	return open(fnme, O_RDONLY, 0666);
}

void accept(int in){	
	d data;
	printf("\nEnter data:\n");
	printf("Enter name: ");
	scanf("%s",data.name);
	
	printf("Enter phno:");
	scanf("%s",data.phno);
	
	printf("Enter addr:");
	scanf("%s",data.addr);

	write(in,&data,sizeof(data));
	printf("\t\tRecord inserted!\n");
	close(in);
}

void display(int out){
	d data;
	int flag = 0;
	printf("\n\tPhonebook:\n");
	printf("Name\tPhone No\tAddress\n");
	
	while(read(out,&data,sizeof(data))){
		flag  = 1;
		printf("%s\t%s\t%s\n",data.name,data.phno,data.addr);
	}
	if(flag == 0)
		printf("\n\tEnter some data in file to display\n");
		
	close(out);
}

int search(char key[50],int srch,int out){
	int flag = 0;
	d data;
	
	while(read(out,&data,sizeof(data))){
		
		//printf("Name\tPhone No\tAddress\n");
		//printf("%s\t%s\t%s\n\n",data.name,data.phno,data.addr);
		
		switch(srch){
			case 1:
				if(strcmp(data.name,key)==0)
					flag = 1;
				break;
			case 2:
				if(strcmp(data.phno,key)==0)
					flag = 1;
				break;
			case 3:
				if(strcmp(data.addr,key)==0)
					flag = 1;
				break;
		}
		if(flag == 1){
			printf("\n\tRecord found: \n");
			printf("Name\tPhone No\tAddress\n");
			printf("%s\t%s\t%s\n",data.name,data.phno,data.addr);
			break;
		}	
	}
	close(out);
	
	if(flag == 0)
		return -1;
	
	return 0;
}

int modify(char key[50],int mdch,int out,int tmp,char fnme[20],char rekey[50]){
	int flag = 0;
	d data;
	
	while(read(out,&data,sizeof(data))){
		
		//printf("Name\tPhone No\tAddress\n");
		//printf("%s\t%s\t%s\n\n",data.name,data.phno,data.addr);
		
		switch(mdch){
			case 1:
				if(strcmp(data.name,key)==0){
					flag = 1;
					strcpy(data.name,rekey);
				}
				break;
			case 2:
				if(strcmp(data.phno,key)==0){
					flag = 1;
					strcpy(data.phno,rekey);					
				}
				break;
			case 3:
				if(strcmp(data.addr,key)==0){
					flag = 1;
					strcpy(data.addr,rekey);					
				}
				break;
		}
		write(tmp,&data,sizeof(data));
	}
	close(out);
	close(tmp);
	
	if(flag == 1){
		printf("\n\tRecord modified: \n");
		remove(fnme);
		rename("tmp.txt",fnme);
		remove("tmp.txt");
	}
	else if(flag == 0)
		return -1;
	
	return 0;
}


int del(char key[50],int mdch,int out,int tmp,char fnme[20]){
	int flag = 0;
	d data;
	
	while(read(out,&data,sizeof(data))){
		
		//printf("Name\tPhone No\tAddress\n");
		//printf("%s\t%s\t%s\n\n",data.name,data.phno,data.addr);
		
		switch(mdch){
			case 1:
				if(strcmp(data.name,key)==0){
					flag = 1;
					continue;
				}
				break;
			case 2:
				if(strcmp(data.phno,key)==0){
					flag = 1;
					continue;					
				}
				break;
			case 3:
				if(strcmp(data.addr,key)==0){
					flag = 1;
					continue;					
				}
				break;
		}
		write(tmp,&data,sizeof(data));
	}
	close(out);
	close(tmp);
	
	if(flag == 1){
		printf("\n\tRecord Deleted: \n");
		remove(fnme);
		rename("tmp.txt",fnme);
		remove("tmp.txt");
	}
	else if(flag == 0)
		return -1;
	
	return 0;
}



int main(){

	int ch,in = 0,out = 0,tmp = 0, srch = 1,mdch = 1;
	char fnme[50]="data.txt",key[50],rekey[50];

	//create files
	in = openIN(fnme);
	out = openOUT(fnme);
	tmp = openIN("tmp.txt");

	do{
		printf("\nChoose your option:\n1.Create new phonebook\n2.Display records\n3.Insert Record\n4.Search record\n5.Modify Record\n6.Delete Record\n7.Exit: ");
		scanf("%d",&ch);
	
		switch(ch){
			case 1://create user file
				printf("\nEnter Filename: ");
				scanf("%s",fnme);

				in = openIN(fnme);
				out = openOUT(fnme);
			
				if(in == -1 || out == -1)
					printf("Error while creating file");
				else
					printf("\tFile created!\n");
				break;
			case 2://display data in file
				out = openOUT(fnme);
				display(out);
				break;
			case 3://accept data
				in = openIN(fnme);
				accept(in);
				break;
			case 4://search
				printf("What do you want to search with?\n1.Name\n2.Phone No\n3.Address: ");
				scanf("%d",&srch);
				printf("Enter what to search: ");
				scanf("%s",key);
				
				out = openOUT(fnme);
				
				int res = search(key,srch,out);
				if(res == -1)
					printf("No such record found!\n");
				
				break;
			case 5://modify
				printf("What do you want to modify?\n1.Name\n2.Phone No\n3.Address: ");
				scanf("%d",&mdch);
				printf("Enter search string: ");
				scanf("%s",key);
				printf("Enter replacement string: ");
				scanf("%s",rekey);
				
				
				tmp = openIN("tmp.txt");
				out = openOUT(fnme);
				
				res = modify(key,mdch,out,tmp,fnme,rekey);
				if(res == -1)
					printf("No such record found!\n");
				
				break;
			case 6://delete
				printf("What do you want to delete by?\n1.Name\n2.Phone No\n3.Address: ");
				scanf("%d",&mdch);
				printf("Enter search string: ");
				scanf("%s",key);
				
				tmp = openIN("tmp.txt");
				out = openOUT(fnme);
				
				res = del(key,mdch,out,tmp,fnme);
				if(res == -1)
					printf("No such record found!\n");
				
				break;
			case 7://exit
				printf("\n\tExiting....\n");
				return -1;
				break;
		}
	}while(ch!=7);
	return 0;	
}
