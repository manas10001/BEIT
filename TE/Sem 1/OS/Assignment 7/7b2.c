/*
FIFOs: Full duplex communication between two independent processes. First process accepts
sentences and writes on one pipe to be read by second process and second process counts
number of characters, number of words and number of lines in accepted sentences, writes this
output in a text file and writes the contents of the file on second pipe to be read by first process
and displays on standard output.
*/
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>

int main(){

	int fifo1,fifo2,lincnt=0,spccnt=0,charcnt=0;
	char data[500],str[100];
	
	//create 2 fifo
	mkfifo("fifo1",0777);
	mkfifo("fifo2",0777);
	
	memset(&data,0,sizeof(data));
	memset(&str,0,strlen(str));

	while(1){
		charcnt = spccnt = lincnt = 0;
		
		memset(&data[0],0,sizeof(data));
		memset(&str[0],0,strlen(str));	
		
		fifo1 = open("fifo1",O_RDONLY);
		read(fifo1,data,sizeof(data));
		close(fifo1);
		
		printf("DATA RECIVED: ");
		
		puts(data);
		
		for(int i=0;i<strlen(data);i++)
		{
			if(data[i]=='.')
				lincnt++;
			else if(data[i]==' ')
				spccnt++;	
			charcnt++; 
		}
		
		if(lincnt==0)
			lincnt++;
		
		sprintf(str,"\nCharacters: %d \nSpaces: %d \nLines: %d\n",charcnt,spccnt,lincnt);
		
		int fd = open("data.dat", O_CREAT |O_RDWR,0666);
		
		write(fd,&str,sizeof(str));
		close(fd);
		
		fifo2 = open("fifo2",O_WRONLY);
		write(fifo2,str,sizeof(str));
		close(fifo2);		
	}
	
	return 0;
}
