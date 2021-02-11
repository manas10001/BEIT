@echo off
cd ..

SET MPJDEV_BUFFERTEST_TESTS=(BufferTest1 BufferTest2 BufferTest3  BufferTest4 BufferTest5 BufferTest6  BufferTest7 BufferTest8 BufferTestDyna1 BufferTestDyna2 BufferTestDyna3 BufferTestDyna4 BufferTestDyna5)

SET MPI_ENV_TESTS=(EnvTester ErrStreamPrinter abort initialized procname wtime)

SET MPI_PT2PT_TESTS=(BreakANYSRC BufferTest1 BufferTest3 BufferTest6 SimpleBsend bsend_test1 getcount iprobe probe rsend SimpleBsend rsend2 sendrecv sendrecv_rep seq ssend test1 test2 test3 testall testany testsome waitall waitall2 waitany waitsome waitnull wildcard buffer isend start startall SimpleNB Send_rendez Send_rendez_fairness Send_eager_fairness Bsend_test2 Ssend_test1 non_overtaking intertwined Isend_rendez)


SET MPI_PT2PT_OBJSER_TESTS=(rsendO bsendO test1O testanyO waitanyO getcountO sendrecvO ssendO test3O test2O waitsomeO testallO testsomeO rsend2O waitallO)

REM  FIXME 
REM  These test cases have known bugs for native device (natmpjdev) because of deadlock for Object type
SET MPI_PT2PT_OBJSER_TESTS_BUGS=(isendO startO)

REM Datatype Tests
SET MPI_DTYP_TESTS=(hvec lbub type_size lbub2 Contiguous Indexed Vector DtypTest pack zero5 zero1)

SET MPI_DTYP_OBJSER_TESTS=(hvecO lbub2O lbubO packO)
REM REM REM REM REM REM REM REM 


REM Comm Tests
SET MPI_COMM_TESTS=(compare commdup CommTest split2 intercomm)


REM Group Tests
REM SET MPI_GROUP_TESTS=(Group_self group range)

SET MPI_GROUP_TESTS=(Group_com Group_incl Group_tranks Group_diff Group_intersect Group_union Group_excl Group_self group range)
REM REM REM REM REM REM REM REM REM REM REM REM REM 

REM Topology Tests
SET MPI_TOPO_TESTS=(cart_topo graph sub2 sub map cart)
REM REM REM REM REM REM REM REM REM REM REM REM REM 

SET MPI_CCL_TESTS=(bcast alltoall gather barrier reduce reduce2 allgather allreduce allgatherv alltoallv gatherv scatter scatterv reduce_scatter scan allreduce_maxminloc)

SET MPI_CCL_OBJSER_TESTS=(allgatherO alltoallO bcastO alltoallvO gatherO allgathervO gathervO scatterO scattervO reduceO.reduceO allreduceO.allreduceO reduce_scatterO.reduce_scatterO scanO.scanO)



REM  Check machinefile supplied

REM  count the number of arguments
set argC=0
for %%x in (%*) do Set /A argC+=1

SET MACHINES=
SET MACHINES_ARG=
IF NOT %argC%==0 (
	SET MACHINES_ARG=-machinefile %1
	SET MACHINES=%1
	REM ECHO Using machinefile %MACHINES%
)



REM Running test cases
for %%t in %MPI_ENV_TESTS% do (
	echo Running:   mpi.env.%%t
	call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.env.%%t
)

for %%t in %MPJDEV_BUFFERTEST_TESTS% do (
	echo Running:   mpjdev.buffertest.%%t
	call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpjdev.buffertest.%%t
)

for %%t in %MPI_PT2PT_TESTS% do (
		echo Running:   mpi.pt2pt.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.pt2pt.%%t
)

for %%t in %MPI_PT2PT_OBJSER_TESTS% do (
		echo Running:   mpi.pt2pt_ObjSer.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.pt2pt_ObjSer.%%t
)

for %%t in %MPI_DTYP_TESTS% do (
		echo Running:   mpi.dtyp.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.dtyp.%%t
)

for %%t in %MPI_DTYP_OBJSER_TESTS% do (
		echo Running:   mpi.dtyp_ObjSer.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.dtyp_ObjSer.%%t
)

for %%t in %MPI_CCL_TESTS% do (
		echo Running:   mpi.ccl.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.ccl.%%t
)

for %%t in %MPI_CCL_OBJSER_TESTS% do (
		echo Running:   mpi.ccl_ObjSer.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.ccl_ObjSer.%%t
)

for %%t in %MPI_COMM_TESTS% do (
		echo Running:   mpi.comm.%%t
		call mpjrun.bat -np 2 -dev native %MACHINES_ARG% mpi.comm.%%t
)

REM  split runs for 6 processors so writing it down here
echo "Running:   mpi.comm.split"
call mpjrun.bat -np 6 -dev native %MACHINES_ARG% mpi.comm.split
REM  comm_test runs for 8 processors so writing it down here
echo "Running:   mpi.comm.comm_test"
call mpjrun.bat -np 8 -dev native %MACHINES_ARG% mpi.comm.comm_test

for %%t in %MPI_GROUP_TESTS% do (
		echo Running:   mpi.group.%%t
		call mpjrun.bat -np 8 -dev native %MACHINES_ARG% mpi.group.%%t
)

REM  topo test cases run for different number of processors so
REM  writing them down here

		echo "Running:   mpi.topo.cart_topo"
		call mpjrun.bat -np 8 -dev native %MACHINES_ARG% mpi.topo.cart_topo
		
		echo "Running:   mpi.topo.graph"
		call mpjrun.bat -np 4 -dev native %MACHINES_ARG% mpi.topo.graph
	
		echo "Running:   mpi.topo.sub2"
		call mpjrun.bat -np 8 -dev native %MACHINES_ARG% mpi.topo.sub2

		echo "Running:   mpi.topo.sub"
		call mpjrun.bat -np 6 -dev native %MACHINES_ARG% mpi.topo.sub
			
		echo "Running:   mpi.topo.map"
		call mpjrun.bat -np 8 -dev native %MACHINES_ARG% mpi.topo.map
			
		REM This testcase has errors in all devices, not just for native
		REM echo "Running:   mpi.topo.cart"
		REM call mpjrun.bat -np 6 -dev native mpi.topo.cart
		  
		REM This testcase has errors in all devices, not just for native
		REM echo "Running:   mpi.topo.dimscreate"
		REM call mpjrun.bat -np 6 -dev native mpi.topo.dimscreate

cd nativetest