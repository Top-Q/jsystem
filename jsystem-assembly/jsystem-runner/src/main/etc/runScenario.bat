@echo off

IF "%1"=="" goto help  
IF "%2"=="" goto help
IF "%3"=="" goto help
	
set PROJECT_CLASSES_PATH=%1
set SCENARIO_NAME=%2
set SUT_FILE=%3
 
set current_dir=%~dp0
set current_drive=%~d0
set path=%path%;.;.\thirdparty\lib;.\lib;.\customer_lib;.\thirdparty\commonLib;

%current_drive%
cd %current_dir%

rem don't remove this line (although you don't understand it's purpose)
echo # > .testPropertiesFile_Empty

del .run.properties

::add modules(xml.bind/java.desktop) in case running on java 9+ version
set ADD_MODULES_STR=
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j"
if %jver% NEQ 1 set ADD_MODULES_STR=--add-modules java.xml.bind --add-modules java.desktop


set ANT_HOME=thirdparty\ant
set ANT_CMD=%ANT_HOME%\bin\ant.bat
set ANT_OPTS=%ADD_MODULES_STR% -Djsystem.current.scenario.name=%SCENARIO_NAME% -Dbasedir=. -Dscenarios.base=%PROJECT_CLASSES_PATH% -DsutFile=%SUT_FILE% -Xms32M -Xmx256M
rem set ANT_OPTS=%ANT_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y
"%ANT_CMD%" -lib thirdparty\ant\lib -lib thirdparty\commonLib -lib thirdparty\lib -lib lib -lib customer_lib -lib %PROJECT_CLASSES_PATH%\..\..\lib -lib %PROJECT_CLASSES_PATH%\..\lib -lib %PROJECT_CLASSES_PATH% -listener jsystem.runner.AntExecutionListener -f %PROJECT_CLASSES_PATH%\%SCENARIO_NAME%.xml

:help
echo Expected script arguments:
echo Argument 1: automation project classes folder
echo Argument 2: scenario name without .xml postfix
echo Argument 3: sut file name
echo Example: runScenario.bat c:\jsystem\runner\projects\jsystemServices\classes scenarios/default mysut.xml

