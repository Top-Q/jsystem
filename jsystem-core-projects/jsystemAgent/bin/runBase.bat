@echo off

set current_dir=%~dp0
set current_drive=%~d0
set path=%path%;.;.\thirdparty\lib;.\lib;.\customer_lib;.\thirdparty\commonLib;

%current_drive%
cd %current_dir%
if exist jsystem.properties goto jsystemPropertiesExist
if exist .jsystembase copy .jsystembase jsystem.properties

:jsystemPropertiesExist
set _JAVACMD=java.exe
if exist thirdparty\jdk\bin\java.exe set _JAVACMD=thirdparty\jdk\bin\java.exe
if exist ..\jdk\bin\java.exe set _JAVACMD=..\jdk\bin\java.exe
if exist "%JAVA_HOME%\bin\java.exe" set _JAVACMD=%JAVA_HOME%\bin\java.exe

set JSYSTEM_USED_CLASSPATH=%current_dir%/lib/jsystem-launcher.jar
if not "%JSYSTEM_CUSTOMER_JARS%" == "" set JSYSTEM_USED_CLASSPATH=%JSYSTEM_USED_CLASSPATH%;%JSYSTEM_CUSTOMER_JARS%

:launch
rem echo %_JAVACMD% 
rem remove the remark on the DEBUG variable to debug the runner.
rem set DEBUG=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n
"%_JAVACMD%" %DEBUG% -Xms32M -Xmx256M %JMX% -Djsystem.main=%JSYSTEM_MAIN% -DentityExpansionLimit=1280000 -classpath "%JSYSTEM_USED_CLASSPATH%" %SPLASH% jsystem.framework.launcher.Launcher2 %1 %2 %3 %4 %5 %6 %7

if %ERRORLEVEL% == 6 goto launch
:end
pause
