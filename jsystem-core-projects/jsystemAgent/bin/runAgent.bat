set JSYSTEM_MAIN=jsystem.runner.agent.server.RunnerAgentMain
set JMX=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl.need.client.auth=false -Dcom.sun.management.jmxremote.ssl=false
call "%~dp0\runBase.bat" %1 %2 %3 %4 %5 %6