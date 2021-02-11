#!/bin/bash
allArguments="$@"  

# Checking if native device is being invoked

IS_NATIVE="false"
for i in $@; do 
  if [ "$i" == "native" ]; then
    IS_NATIVE="true"
  fi
done

if [ "$IS_NATIVE" == "false" ]; then
  java -jar $MPJ_HOME/lib/starter.jar "$@"	
else
  # Else, use natmpjdev
  version=`grep mpjexpress.version $MPJ_HOME/conf/mpjexpress.conf |cut -d = -f2`
  echo "MPJ Express ($version) is started in cluster configuration with native device"  
  MACHINESFILE=""
  CP=$MPJ_HOME/lib/mpj.jar                                                                              
  for i in $@; do                                                      
    case $i in
      -np)
        shift;                        
        NP="$1"                                                                        shift; 
        ;;
                                                                               
      -dev) 
	shift;                                                       
        DEV="$1"                                                                       shift;                                                         
        ;;

      -machinesfile)                                                                   shift;
        MACHINESFILE="$1"
        shift;
        ;;                                                                     

      -wdir)
        shift;
        WDIR="$1"
        shift;
        ;;                                                                     

      -cp)
        shift;
        CP=$CP:"$1"i
        shift;                                                         
        ;;
	
      -Djava.library.path=*)
	oldIFS=$IFS
	export IFS="="
	line="$1"
	for path in $line; do
          DJAVA_LIBRARY_PATH="$path"
	done    
	IFS=$oldIF
	shift;  
        ;;
       
    esac                                                                         done                                                                                      
  CLASS_NAME=$1
  shift;
  APP_ARGUMENTS=$@

  DJAVA_LIBRARY_PATH=$DJAVA_LIBRARY_PATH:"$MPJ_HOME/lib"

  MPIRUN_ARGS=" -np $NP";
  if [ "$MACHINESFILE" == "" ]; then
    MPIRUN_ARGS="$MPIRUN_ARGS";
  else
    MPIRUN_ARGS="$MPIRUN_ARGS -machinefile $MACHINESFILE";
  fi

  COMMAND_TO_RUN="mpirun $MPIRUN_ARGS java -cp $CP:. -Djava.library.path=$DJAVA_LIBRARY_PATH $CLASS_NAME 0 0 $DEV $APP_ARGUMENTS"
  #run the command
  $COMMAND_TO_RUN	  

fi

