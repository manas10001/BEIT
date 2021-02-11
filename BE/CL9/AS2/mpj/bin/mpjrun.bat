@echo off
::java -jar "%MPJ_HOME%"/lib/starter.jar %*
::goto :EOF
::echo %*
SET IS_NATIVE=false
SET CP="%MPJ_HOME%"/lib/mpj.jar
for %%x in (%*) do (
	::echo %%x
	IF %%x==native (
	SET IS_NATIVE=true
	::echo "setting native"
	goto :OUT_OF_CHECK_NATIVE
	)
)
:OUT_OF_CHECK_NATIVE

IF %IS_NATIVE%==false (
	::echo "running javampjdev"
	java -jar "%MPJ_HOME%"/lib/starter.jar %*
	goto :EOF
)

for /f "eol=: tokens=2 delims==" %%a in ('find "mpjexpress.version" %MPJ_HOME%/conf/mpjexpress.conf') do (
   set version=%%a
   )
echo "MPJ Express (%version%) is started in the native MPI configuration"

SET MACHINESFILE=""
SET CP="%MPJ_HOME%"/lib/mpj.jar

set argC=0
for %%x in (%*) do Set /A argC+=1

::echo "Total number of Arguments %argC%"

set argParsed=0
set /A argToBeParsed=argC-2

::echo "Total number of Arguments %argC% Arguments to be parsed %argToBeParsed%"

:LOOP

	::echo %1
    IF %1==-np (
	::echo "in if %1"
	SET NP=%2
	SHIFT
	SHIFT
	SET /A argParsed+=2
	IF %argParsed% GEQ %argToBeParsed% (
		GOTO :END_LOOP
	)
	GOTO :LOOP
   )
   IF %1==-dev (
	::echo "in if %1"
	SET DEV=%2
	SHIFT
	SHIFT
	SET /A argParsed+=2
	IF %argParsed% GEQ %argToBeParsed% (
		GOTO :END_LOOP
	)
	GOTO :LOOP
   )
   IF %1==-cp (
	::echo "in if %1"
	SET CP="%CP%:%2"
	SHIFT
	SHIFT
	SET /A argParsed+=2
	IF %argParsed% GEQ %argToBeParsed% (
		GOTO :END_LOOP
	)
	GOTO :LOOP
   )
   IF %1==-machinesfile (
	::echo "in if %1"
	SET MACHINESFILE=%2
	SHIFT
	SHIFT
	SET /A argParsed+=2
	IF %argParsed% GEQ %argToBeParsed% (
		GOTO :END_LOOP
	)
	GOTO :LOOP
   )
  
:END_LOOP

SET /A argRemaining= %argC%-%argParsed%

IF %argRemaining%==0 (
	ECHO Missing arguments. Please refer to the windowsguide.pdf for correct usage.	
	GOTO :EOF
)

SET CLASS_NAME=%1
SHIFT
SET /A argParsed=1
SET /A argRemaining-=1

::ECHO "argParsed is %argParsed%, argRemaining is %argRemaining%"

SET APP_ARGUMENTS=

:LOOP_PARSE_APP_ARGUMENTS
IF %argRemaining%==0 (
	GOTO :END_LOOP_PARSE_APP_ARGUMENTS
)
SET APP_ARGUMENTS=%APP_ARGUMENTS% %1
SHIFT
SET /A argParsed+=1
SET /A argRemaining-=1
::ECHO "argParsed is %argParsed%, argRemaining is %argRemaining%"
GOTO :LOOP_PARSE_APP_ARGUMENTS
:END_LOOP_PARSE_APP_ARGUMENTS

::echo "Num Proc is %NP%, DEV is %DEV%, MACHINESFILE is %MACHINESFILE%, CP is %CP%, CLASSNAME is %CLASSNAME%, APP_ARGUMENTS is %APP_ARGUMENTS%"

SET MPIRUN_ARGS= -np %NP%
IF NOT %MACHINESFILE%=="" (
	SET MPIRUN_ARGS=%MPIRUN_ARGS% -machinefile %MACHINESFILE%
)
SET COMMAND_TO_RUN=mpiexec %MPIRUN_ARGS% java -cp %CP%;. -Djava.library.path="%MPJ_HOME%"\lib %CLASS_NAME% 0 0 %DEV% %APP_ARGUMENTS%


%COMMAND_TO_RUN%



