@echo off

cd ..
:: mpi level tests 
:: excluding mpi/mpi 
for %%d in (ccl ccl_ObjSer ccl_ObjSer/reduceO ccl_ObjSer/allreduceO ccl_ObjSer/reduce_scatterO ccl_ObjSer/scanO comm dtyp dtyp_ObjSer env group perf pt2pt pt2pt_ObjSer signals threads topo) do (
   echo Compiling mpi/%%d
   javac -cp "%MPJ_HOME%"/lib/mpj.jar;. mpi/%%d/*.java
)

:: mpi level tests 
:: excluding mpjdev/commtest mpjdev/grouptest mpjdev/killall mpjdev/misc mpjdev/nbcomms mpjdev/perftest
for %%d in (buffertest) do (
   echo Compiling mpjdev/%%d
   javac -cp "%MPJ_HOME%"/lib/mpj.jar;. mpjdev/%%d/*.java
)

cd nativetest
