# How to run the code written with MPI

We have a folder called mpj which is extracted form of the jar [mpj.jar](http://sourceforge.net/projects/mpjexpress/files/releases/)

We need to set MPJ_HOME enviroment variable because it is used by internal files

Set it by using `export MPJ_HOME=mpj/`

Compile code: `javac -cp $MPJ_HOME/lib/mpj.jar JavaCode.java`

Run code: `$MPJ_HOME/bin/mpjrun.sh -np 2 JavaCode`
