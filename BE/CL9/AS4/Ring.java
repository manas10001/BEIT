import java.util.*;

public class Ring{
	
	boolean processes[];
	int coordinator, max_processes;
	ArrayList<Integer> pid;
	
	
	public Ring(int no_max_processes){
		
		pid = new ArrayList<Integer>();
		coordinator = no_max_processes;
		max_processes = no_max_processes;
		processes = new boolean[max_processes];
		
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
	
	void displayArrayList(ArrayList<Integer> pid){
		System.out.print("[ ");
		for(Integer n : pid){
			System.out.print(n+" ");
		}
		System.out.println("]");
	}
	
	//election
	void initElection(int process_no){
		//selected process should be active
		if(processes[process_no-1]){
		
			pid.add(process_no);
			
			int temp = process_no;
			
			System.out.print("Process P["+process_no+"] sending following list:");
			displayArrayList(pid);
			
			//add all available pid to arraylist
			while(temp != process_no - 1){
				if(processes[temp]){
					pid.add(temp+1);
					System.out.print("Process P["+(temp+1)+"] sending following list:");
					displayArrayList(pid);
				}
				temp = (temp+1) % max_processes;
			}
		//after getting complete list choose max pid as coordinator	
		coordinator = Collections.max(pid);
		System.out.println("Process P["+process_no+"] declares P["+coordinator+"] as new Coordinator");
		pid.clear();
		}
	}
	
	
	public static void main(String args[]){
		Ring ring = null;
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
					ring = new Ring(max_processes);
					break;
					
				case 2: 
					ring.displayProcesses();
					break;
					
				case 3: 
					System.out.print("Enter processes no to take it down: ");
					process_no = sc.nextInt();
					ring.takeDown(process_no);
					
					//if the process taken down was the coordinator we start election
					if(process_no != ring.coordinator)
						break;
					
				case 4: 
					System.out.print("Which process will initiate election?: ");
					process_no = sc.nextInt();
					ring.initElection(process_no);
					//ring.displayProcesses();
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
