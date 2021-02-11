#!/bin/sh
#set -x

if [ $# -ne 2 ]; then
  echo "**************************************";
  echo "Usage: ./mpjrun.sh conf_file classname";
  echo "mpjdev_home and xdev_home must be set";
  echo "CL_SWITCH may be used if you wish to provide";
  echo "any command line args"; 
  echo "**************************************";
  exit 127
fi

lines=`cat $1  | egrep -v "#" | egrep "@"`
dir=`pwd`
cl_switch=$CL_SWITCH
name=$2
conf=$1
count=0

# export PATH=/bin:/usr/bin:/usr/local/bin

for i in `echo $lines`; do 

  host=`echo $i | cut -d "@" -f 1`
  rank=`echo $i | cut -d "@" -f 3`    
  
 # if [ $count -eq "0" ]; then
  	echo "Compiling the sources"
	#javac -cp $xdev_home/src/:.:$mpi_home/src/ $mpjdev_home/src/**/*.java	
	#javac -cp $mpjdev_home/src/:. $xdev_home/src/**/*.java
	#echo "Compiling $name.java"
	#javac -classpath $mpjdev_home/src/:$xdev_home/src/:. "$name.java"
 # fi	
 
  #sleep 1;
  ssh $host "cd $dir; java -Djava.library.path=$mpj_home/lib \
  -cp $mpj_home/lib/mpj.jar:. $name $count $conf ;" &

  count=`expr $count + 1`

done
