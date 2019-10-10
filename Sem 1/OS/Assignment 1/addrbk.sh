#!/bin/bash

#assigning filename to variables for globle use
#the file idcount.dat stores the highest assigned id
#phonebook.dat is the database
#temp.dat is a temporary file

fnme="phonebook.dat"
tfnme="temp.dat"
idf="idcount.dat"

#function to initialize the code
init(){
	#an idcounter is to be maintained to auto generate the id
	if [ ! -e $idf ]
	then
		touch $idf
		echo "0">>$idf
	fi

	#  id=$(wc -l $fnme | head -c 2)	 returns the number of lines in databse file
	echo -e "Database currently has total $(wc -l $fnme | head -c 2) records!\n\n"

	#getting last id location
	id=$(cat $idf)

	#if the database file dosent exist create it
	if [ ! -e $fnme ]
	then
		touch "$fnme"
		echo -e "New Databse Created!\n"
	else
		echo -e "Database already exists procedding with it\n"
	fi
}

#functions
#accept function accepts data of student
accept(){
 	#accept data
	echo -e "Add a new record\n"
	echo -e "Enter name:\c "
	read name
	
	#validation thet name shoudnt contain any digits
	while [ true ]
	do
		#checking regular expression for all characters
		if ! [[ $name =~ ^[a-zA-Z]+$ ]]
		then
			echo -e "Enter valid name: \c"
			read name
		else
			break;
		fi
	done

	echo -e "Enter phone number:\c "
	read phno

	#validation for phone number being 10 digit

	while [ true ]
	do
		if [ ${#phno} -ne 10 ]
		then
			echo -e "Phone number should be of 10 digits Enter again:\c "
			read phno
		elif ! [[ "$phno" =~ ^[0-9]+$ ]]
		then

			echo -e "Characters not allowed enter again:\c "
			read phno
		else
			break
		fi
	done

	echo -e "Enter city:\c "
	read city

	#validation thet city name shoudnt contain any digits
	while [ true ]
	do
		if ! [[ $city =~ ^[a-zA-Z]+$ ]]
		then
			echo -e "Enter valid city name: \c"
			read city
		else
			break;
		fi	
	done
	
	echo -e "Enter pincode:\c "
	read pin

	while [ true ]
	do
		if [ ${#pin} -ne 6 ]
		then
			echo -e "Pin code should be of 6 digits Enter again:\c "
			read pin
		elif ! [[ "$pin" =~ ^[0-9]+$ ]]
		then
			echo -e "Characters not allowed enter again:\c "
			read pin
		else
			break
		fi
	done
	
}

#displays all records it takes a argument which specifies that by which column the data is to be sorted
display(){
	if [ -e $fnme ]
	then
	
		echo -e "\n\nID NAME     PH-NUMBER   CITY \t   PINCODE"
		sort -k "$1" "$fnme" | column -t
	else
		echo -e "\nProblem occured while accessing database file\n"
	fi
}

#searches records by rollno
search(){
	echo -e "\nEnter name or id or phone-number or pincode to search records: "
	read src
	
	#w for word match i for ignore case h for display whole line q for not printing anything on console
	grep -w -i -h -q "$src" "$fnme"
	stat=$?
	#grep returns 0 if something is found	
	if [ $stat -eq 0 ]
	then
		echo -e "\nSearch success record found\nRecord is:\n"
		grep -i -w -h "$src" "$fnme"
	else
		echo -e "\nNo records found\n"
	fi
}

#delete a record
delete(){
	echo -e "Records are: \n"
	display 1
	echo -e "\nEnter id or phone-number or pincode to delete record: "
	read src
	
	#w for word match i for ignore case h for display whole line q for not printing anything on console
	grep -w -i -h -q "$src" "$fnme"
	stat=$?		
		
	if [ $stat -eq 0 ]
	then
		touch "$tfnme"
		grep -i -w -v "$src" "$fnme">>"$tfnme"
		rm "$fnme"
		mv "$tfnme" "$fnme"
		echo -e "Record deleted! Remaining records are: \n"
		display 1
	else
		echo -e "\nRecord not found in database\n"
	fi
}

#modify record of rollno
modify(){
	echo -e "Records are: \n"
	display 1
	echo -e "\nEnter id or phonenumber or pincode to modify record: "
	read src
	
	#w for word match i for ignore case h for display whole line q for not printing anything on console
	str=$(grep -w -i "$src" "$fnme")
	stat=$?
	#if the record exists
	if [ $stat -eq 0 ]
	then
		#this selects the first two characters of the string and stores them in tid
		tid=$(echo $str | head -c 2)
		#accept new data by calling accept function
		accept
		
		sed -i "/$str/c\\$tid \t $name \t $phno \t  $city \t $pin " $fnme
		echo -e "Record Modified! New records are: \n"
		display 1
	else
		echo -e "\nRecord not found in database\n"
	fi
}

drop(){
	echo -e "Are you sure you want to delete the whole databse?\nEnter 1 to proceed: \c"
	read ip
	
	if [ $ip -eq 1 ] 
	then
		rm $fnme
		touch $fnme
		echo "0">$idf
		id=0
		echo -e "Database Cleared!"
		
	else
		echo -e "No changes done to database"
	fi
}

#assign choice to something
ch=2;
	#initializing the system
	init

#MENU

while [ $ch -ne 7 ]
do
	echo -e "\n\tMENU:\n"
	echo -e "1.Add Records\n2.Display Records\n3.Search Records\n4.Delete Record\n5.Update Record\n6.Delete whole database\n7.Exit: \c"
	read ch
	
	case "$ch" in
		1)
			accept
			#write data to file
			#INCREMENTING THE ID
			id=$(($id+1))
			echo -e "$id \t $name \t $phno \t  $city \t $pin " >> "$fnme"
			echo -e "Data inserted"
			echo -e "$(sort -k 2 $fnme)">$fnme
			;;	  
		2) 
			#display the records according to need
			echo -e "\nEnter how you want to display records: "
			echo -e "1.By id\n2.By name\n3.By Phone number\n4.By City\n5.By pincode: \c"
			read arg
			
			#validating input choice
			while [ true ]
			do
				if [ "$arg" -le 0 ]
				then
					echo -e "Invalid choice Enter Again: \c"
					read arg
				elif [ "$arg" -gt 5 ]
				then 
					echo -e "Invalid choice Enter Again: \c"
					read arg
				else
					break
				fi
			done
			
			#callingt the display function with argument
			display "$arg"
			;;
		3)
			search
			;;
		4)
			delete
			;;
		5)
			modify
			;;
		6)	drop
			;;
		7)
			#write the final id to the idcount file
			echo "$id">$idf
			echo -e "Exiting...";;
		*)echo -e "Do something "
	esac
done
