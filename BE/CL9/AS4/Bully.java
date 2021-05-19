import java.util.*;

public class Bully{

	int max_processes;
	int coordinator;
	boolean processes[];

	//init everything
	public Bully(int no_max_processes){
		max_processes = no_max_processes;
		processes = new boolean[max_processes];
		coordinator = max_processes;
		
		//create processes
		System.out.println("\nCreating processes");
		for(int i = 0; i < max_processes; i++){
			System.out.println("P[" + (i + 1) + "] Created");
			processes[i] = true;
		}
		System.out.println("Process P[" + coordinator + "] is Coordinator\n");
	}
	
	//displays all processes
	void displayProcesses(){
		System.out.println("\nProcesses:");
		
		for(int i = 0; i < max_processes; i++){
			if(processes[i])
				System.out.println("Process P[" + (i + 1) + "] is Active");
			else
				System.out.println("Process P[" + (i + 1) + "] is Down");
		}
		System.out.println("Process P[" + coordinator + "] is Coordinator\n");
	}
	
	//take a process down
	void takeDown(int process_no){
		if(!processes[process_no-1]){
			System.out.println("\nProcess P[" + (process_no) + "] is Already Down\n");
		}else{
			processes[process_no-1] = false;
			System.out.println("\nProcess P[" + (process_no) + "] is now Down\n");
		}
	}

	//election
	void initElection(int process_no){
		//assign next process with bigger pid as coordinator
		coordinator = process_no;
		boolean keepGoing = true;
		
		for(int i = process_no; i < max_processes && keepGoing; i++){
			System.out.println("Election message sent from P[" + (process_no) + "] to P[" + (i + 1) + "]");
			
			if(processes[i]){
				keepGoing = false;
				initElection(i + 1);
			}
		}
	}

	public static void main(String args[]){
		Bully bully = null;
		int max_processes = 0, process_no = 0;
		int ch = 0;
		Scanner sc = new Scanner(System.in);

		while( ch != 5 ){
			System.out.println("Menu:");
			System.out.println("1. Create Processes");
			System.out.println("2. Display Processes");
			System.out.println("3. Down a process");
			System.out.println("4. Send election message");
			System.out.println("5. Exit \nEnter your Choice: ");
			ch = sc.nextInt();
			
			switch(ch){
				case 1: 
					System.out.print("Enter no of processes to create: ");
					max_processes = sc.nextInt();
					bully = new Bully(max_processes);
					break;
					
				case 2: 
					bully.displayProcesses();
					break;
					
				case 3: 
					System.out.print("Enter processes no to take it down: ");
					process_no = sc.nextInt();
					bully.takeDown(process_no);
					
					//if the process taken down was the coordinator we start election
					if(process_no != bully.coordinator)
						break;
					
				case 4: 
					System.out.print("Which process will initiate election?: ");
					process_no = sc.nextInt();
					bully.initElection(process_no);
					bully.displayProcesses();
					break;
					
				case 5: 
					System.out.println("Exit..");
					break;
					
				default:
					System.out.println("Invalid Option!");
					break;
			}
			
		}
		sc.close();
	}
	
}
