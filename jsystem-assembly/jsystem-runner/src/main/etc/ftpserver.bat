echo off
rem ***** to use this batch do the following:
rem *****        1. add java.exe to your path.


set current_dir=%~dp0
set current_drive=%~d0
%current_drive%
cd %current_dir%

if exist jsystem.properties goto javaHome
if exist thirdparty\jdk\bin\java.exe goto localJdk

:javaHome
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto run

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
goto run

:localJdk
set _JAVACMD=thirdparty\jdk\bin\java.exe

:run
set JSYSTEM_USED_CLASSPATH=./thirdparty/commonLib/ftpserver-dev.jar;./thirdparty/commonLib/commons-logging-1.0.4.jar;./thirdparty/commonLib/commons-net.jar;./thirdparty/commonLib/xalan.jar;./thirdparty/commonLib/commons-io-1.3.1.jar;./lib/FileTransfer.jar;./lib/jsystemCore.jar;./lib/jsystemCommon.jar;./thirdparty/commonLib/junit.jar

echo on
:launch
rem ftp client user name: aqua
rem ftp client user password: aqua
rem use com.aqua.filetransfer.ftp.ftpserver=server_ip to control the ip on which the server listens for ftp requests
"%_JAVACMD%" -Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classpath "%JSYSTEM_USED_CLASSPATH%" com.aqua.filetransfer.ftp.FTPServer

:end
