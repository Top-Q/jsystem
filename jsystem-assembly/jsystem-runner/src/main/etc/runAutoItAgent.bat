@echo OFF
rem Runs AutoIt agent

set current_dir=%~dp0
set current_drive=%~d0
%current_drive%
cd %current_dir%

setlocal ENABLEDELAYEDEXPANSION
set CLASSPATH=classes
FOR /R ..\lib %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G
echo The Classpath definition is %CLASSPATH%

java -classpath "%CLASSPATH%" com.jsystem.j2autoit.AutoItAgent 8888

rem Make sure that we see the output
@echo ON

pause