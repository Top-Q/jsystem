@ECHO OFF

set _JAVACMD=java.exe
if exist thirdparty\jdk\bin\java.exe set _JAVACMD=thirdparty\jdk\bin\java.exe
if exist ..\jdk\bin\java.exe set _JAVACMD=..\jdk\bin\java.exe
if exist "%JAVA_HOME%\bin\java.exe" set _JAVACMD=%JAVA_HOME%\bin\java.exe

IF "%RUNNER_ROOT%"=="" GOTO NO_RUNNER_ENV
	SET root=%RUNNER_ROOT%
	GOTO ENDIF
:NO_RUNNER_ENV
	echo No RUNNER_ROOT environment variable was set
	SET root="c:\jsystem\runner"
:ENDIF
SET CLASSPATH=JSystemRefactorUtils.jar;%root%\lib\jsystemCore.jar;%root%\lib\jsystemApp.jar;.\lib\commons-cli-1.2.jar;
"%_JAVACMD%" -Djsystem.main=il.co.topq.refactor.Main -classpath "%CLASSPATH%" il.co.topq.refactor.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

@ECHO ON