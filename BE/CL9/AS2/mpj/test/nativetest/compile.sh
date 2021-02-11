#!/bin/sh 

# mpi level tests 
declare -a MPI_DIRECTORIES=('ccl' 'ccl_ObjSer' 'ccl_ObjSer/reduceO' 'ccl_ObjSer/allreduceO' 'ccl_ObjSer/reduce_scatterO' 'ccl_ObjSer/scanO' 'comm' 'dtyp' 'dtyp_ObjSer' 'env' 'group' 'perf' 'pt2pt' 'pt2pt_ObjSer' 'signals' 'threads' 'topo');
#
# The standart testsuit for javampjdev doesn't include them. So currently ignoring them
declare -a MPI_DIRECTORIES_NOT_INCLUDED=('mpi');
# #########################


# mpjdev level tests
declare -a MPJDEV_DIRECTORIES=('buffertest');
#
# The standart testsuit for javampjdev doesn't include them. So currently ignoring them
declare -a MPJDEV_DIRECTORIES_NOT_INCLUDED=('commtest' 'grouptest' 'killall' 'misc' 'nbcomms' 'perftest');
# #########################

cd ..

for directory in ${MPI_DIRECTORIES[@]}; do
    echo "Compiling mpi/$directory"
    javac -cp $MPJ_HOME/lib/mpj.jar:. mpi/$directory/*.java
done

for directory in ${MPJDEV_DIRECTORIES[@]}; do
    echo "Compiling mpjdev/$directory"
    javac -cp $MPJ_HOME/lib/mpj.jar:. mpjdev/$directory/*.java
done
