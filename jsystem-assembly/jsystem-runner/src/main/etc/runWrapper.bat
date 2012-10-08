set current_dir=%~dp0
set current_drive=%~d0
%current_drive%
cd %current_dir%

java -classpath ../classes;c:/Ignis/runner/thirdparty/lib/xmlrpc-2.0.jar;c:/Ignis/runner/thirdparty/lib/jemmy.jar;c:/Ignis/runner/thirdparty/lib/commons-codec-1.3.jar com.jsystem.jemmywrapper.ApplicationRPCWrapper