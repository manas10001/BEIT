@echo off

set count=0
for %%i in (%*) do set /A count+=1

if NOT %count% == 1 goto :notValid


if %1 == -boot (
java -jar %MPJ_HOME%/lib/daemonmanager.jar -winboot
goto :eof
)

if %1 == -halt (
java -jar %MPJ_HOME%/lib/daemonmanager.jar -winhalt 
goto :eof
)

if %1 == -status (
java -jar %MPJ_HOME%/lib/daemonmanager.jar -status -hosts localhost
goto :eof
)

:notValid
echo "Usage: mpjdaemon.bat { -boot | -halt | -status }"
goto :eof
