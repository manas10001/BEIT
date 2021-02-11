#!/bin/sh 

declare -a MPJDEV_BUFFERTEST_TESTS=('BufferTest1' 'BufferTest2' 'BufferTest3'  'BufferTest4' 'BufferTest5' 'BufferTest6'  'BufferTest7' 'BufferTest8' 'BufferTestDyna1' 'BufferTestDyna2' 'BufferTestDyna3' 'BufferTestDyna4' 'BufferTestDyna5');

declare -a MPI_ENV_TESTS=('EnvTester' 'ErrStreamPrinter' 'abort' 'initialized' 'procname' 'wtime');

declare -a MPI_PT2PT_TESTS=('BreakANYSRC' 'BufferTest1' 'BufferTest3' 'BufferTest6' 'SimpleBsend' 'bsend_test1' 'getcount' 'iprobe' 'probe' 'rsend' 'SimpleBsend' 'rsend2' 'sendrecv' 'sendrecv_rep' 'seq' 'ssend' 'test1' 'test2' 'test3' 'testall' 'testany' 'testsome' 'waitall' 'waitall2' 'waitany' 'waitsome' 'waitnull' 'wildcard' 'buffer' 'isend' 'start' 'startall' 'SimpleNB' 'Send_rendez' 'Send_rendez_fairness' 'Send_eager_fairness' 'Bsend_test2' 'Ssend_test1' 'non_overtaking' 'intertwined' 'Isend_rendez');


declare -a MPI_PT2PT_OBJSER_TESTS=('rsendO' 'bsendO' 'test1O' 'testanyO' 'waitanyO' 'getcountO' 'sendrecvO' 'ssendO' 'test3O' 'test2O' 'waitsomeO' 'testallO' 'testsomeO' 'rsend2O' 'waitallO');

# FIXME 
# These test cases have known bugs for native device (natmpjdev) because of deadlock for Object type
declare -a MPI_PT2PT_OBJSER_TESTS_BUGS=('isendO' 'startO');

#Datatype Tests
declare -a MPI_DTYP_TESTS=('hvec' 'lbub' 'type_size' 'lbub2' 'Contiguous' 'Indexed' 'Vector' 'DtypTest' 'pack' 'zero5' 'zero1');

declare -a MPI_DTYP_OBJSER_TESTS=('hvecO' 'lbub2O' 'lbubO' 'packO');
########


#Comm Tests
declare -a MPI_COMM_TESTS=('comm_test' 'compare' 'commdup' 'CommTest' 'split2');

declare -a MPI_COMM_TESTS_BUGS=('intercomm');
#############

#Group Tests
declare -a MPI_GROUP_TESTS=('Group_com' 'Group_incl' 'Group_tranks' 'Group_diff' 'Group_intersect' 'Group_union' 'Group_excl' 'Group_self' 'group' 'range');
#############

#Topology Tests
declare -a MPI_TOPO_TESTS=('cart_topo' 'graph' 'sub2' 'sub' 'map' 'cart');
#############

declare -a MPI_CCL_TESTS=('bcast' 'alltoall' 'gather' 'barrier' 'reduce' 'reduce2' 'allgather' 'allreduce' 'allgatherv' 'alltoallv' 'gatherv' 'scatter' 'scatterv' 'reduce_scatter' 'scan' 'allreduce_maxminloc');

declare -a MPI_CCL_OBJSER_TESTS=('allgatherO' 'alltoallO' 'bcastO' 'alltoallvO' 'gatherO' 'allgathervO' 'gathervO' 'scatterO' 'scattervO' 'reduceO.reduceO' 'allreduceO.allreduceO' 'reduce_scatterO.reduce_scatterO' 'scanO.scanO');


# Check machinefile supplied

MACHINES=$1
#echo "$MACHINES"
MACHINES_ARG="-machinesfile $MACHINES"
if [ "$MACHINES" == "" ]; then
   MACHINES_ARG=""
fi

#Runing test cases

cd ..

for testcase in ${MPI_ENV_TESTS[@]}; do
			echo "Running:   mpi.env.$testcase"
			mpjrun.sh -np 2 -dev native $MACHINES_ARG mpi.env.$testcase
		  echo ""
done

for testcase in ${MPJDEV_BUFFERTEST_TESTS[@]}; do
			echo "Running:   mpjdev.buffertest.$testcase"
			mpjrun.sh -np 2 -dev native  $MACHINES_ARG mpjdev.buffertest.$testcase
		  echo ""
done



for testcase in ${MPI_PT2PT_TESTS[@]}; do
			echo "Running:   mpi.pt2pt.$testcase"
			mpjrun.sh -np 2 -dev native  $MACHINES_ARG mpi.pt2pt.$testcase
		  echo ""
done

for testcase in ${MPI_PT2PT_OBJSER_TESTS[@]}; do
			echo "Running:   mpi.pt2pt_ObjSer.$testcase"
			mpjrun.sh -np 2 -dev native  $MACHINES_ARG mpi.pt2pt_ObjSer.$testcase
		  echo ""
done

for testcase in ${MPI_DTYP_TESTS[@]}; do
			echo "Running:   mpi.dtyp.$testcase"
			mpjrun.sh -np 2 -dev native $MACHINES_ARG mpi.dtyp.$testcase
		  echo ""
done

for testcase in ${MPI_DTYP_OBJSER_TESTS[@]}; do
			echo "Running:   mpi.dtyp_ObjSer.$testcase"
			mpjrun.sh -np 2 -dev native  $MACHINES_ARG mpi.dtyp_ObjSer.$testcase
		  echo ""
done


for testcase in ${MPI_CCL_TESTS[@]}; do
			echo "Running:   mpi.ccl.$testcase"
			mpjrun.sh -np 4 -dev native  $MACHINES_ARG mpi.ccl.$testcase
		  echo ""
done

for testcase in ${MPI_CCL_OBJSER_TESTS[@]}; do
			echo "Running:   mpi.ccl_ObjSer.$testcase"
			mpjrun.sh -np 2 -dev native  $MACHINES_ARG mpi.ccl_ObjSer.$testcase
		  echo ""
done


for testcase in ${MPI_COMM_TESTS[@]}; do
			echo "Running:   mpi.comm.$testcase"
			mpjrun.sh -np 8 -dev native  $MACHINES_ARG mpi.comm.$testcase
		  echo ""
done

# split runs for 6 processors so writing it down here
echo "Running:   mpi.comm.split"
mpjrun.sh -np 6 -dev native   $MACHINES_ARG mpi.comm.split

for testcase in ${MPI_GROUP_TESTS[@]}; do
			echo "Running:   mpi.group.$testcase"
			mpjrun.sh -np 8 -dev native   $MACHINES_ARG mpi.group.$testcase
		  echo ""
done

# topo test cases run for different number of processors so
# writing them down here

#for testcase in ${MPI_TOPO_TESTS[@]}; do
#			echo "Running:   mpi.topo.$testcase"
#			mpjrun.sh -np 6 -dev native mpi.topo.$testcase
#		  echo ""
#done
			echo "Running:   mpi.topo.cart_topo"
			mpjrun.sh -np 8 -dev native   $MACHINES_ARG mpi.topo.cart_topo

			echo "Running:   mpi.topo.graph"
			mpjrun.sh -np 4 -dev native   $MACHINES_ARG mpi.topo.graph

			echo "Running:   mpi.topo.sub2"
			mpjrun.sh -np 8 -dev native  $MACHINES_ARG mpi.topo.sub2

			echo "Running:   mpi.topo.sub"
			mpjrun.sh -np 6 -dev native   $MACHINES_ARG mpi.topo.sub

			echo "Running:   mpi.topo.map"
			mpjrun.sh -np 8 -dev native  $MACHINES_ARG mpi.topo.map
			
			#This testcase has errors in all devices, not just for native
			#echo "Running:   mpi.topo.cart"
			#mpjrun.sh -np 6 -dev native mpi.topo.cart
		  
			#This testcase has errors in all devices, not just for native
			#echo "Running:   mpi.topo.dimscreate"
			#mpjrun.sh -np 6 -dev native mpi.topo.dimscreate

