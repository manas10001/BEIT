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

	int fifo1,fifo2;
	char data[500],str[100];
	
	//create 2 fifo
	mkfifo("fifo1",0777);
	mkfifo("fifo2",0777);
	
	memset(&data[0],0,sizeof(data));
	memset(&str[0],0,sizeof(str));

	while(1){
	
		printf("Enter a line: ");
		gets(data);
		
		fifo1 = open("fifo1",O_WRONLY);
		write(fifo1,data,sizeof(data));
		close(fifo1);
		
		fifo2 = open("fifo2",O_RDONLY);
		read(fifo2,str,sizeof(str));
		close(fifo2);
		
		printf("data recieved by parent is : %s ",str);
	}
	
	return 0;
}
