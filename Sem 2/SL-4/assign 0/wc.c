/*
CODE TO CALCULATE LINES; TOTAL CHAR; WORDS 
*/
#include<stdio.h>

int main(){
	
	FILE *fp,*fp2;
	
	int lc=0,cc=0,wc=0;
	char c;
	
	fp = fopen("text_file.txt","r");
	
	if(fp == NULL){
		printf("FILE OPENING FAILED");
		return -1;
	}else{
	//file is open now read
		while((c = fgetc(fp)) != EOF){
			if(c == ' ' || c == '\t')
				wc++;
			else if(c == '\n')
			{	
				lc++;
				wc++;
			}
			cc++;
		}
		
		//printf("lines:%d\nwords = %d\n char:%d",lc,wc,cc);
	}
	fclose(fp);
	//now write data to another file
	
	fp2 = fopen("op.txt","w");
	
	if(fp2==NULL){
		printf("FILE OPENING FAILED");
		return -1;
	}else{
		//write result to file!
		fprintf(fp2," Total lines:%d\n Total words = %d\n Total characters:%d",lc,wc,cc);
	}

	fclose(fp2);
	
	return 0;
}
