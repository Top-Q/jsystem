#!/bin/sh
if [ $# -ne 3 ]; then
	echo "Expected script arguments:"
	echo "Argument 1: automation project classes folder"
	echo "Argument 2: scenario name without .xml postfix"
	echo "Argument 3: sut file name. "
	echo "Example: runScenario.sh /home/myuser/jsystemServices/classes scenarios/default mySut.xml"
    exit 127
fi

#don't remove this line (although you don't understand it's purpose)
> .testPropertiesFile_Empty

PROJECT_CLASSES_PATH=$1
SCENARIO_NAME=$2
SUT_FILE=$3
PATH=$PATH:.:./thirdparty/lib:./lib:./customer_lib:./thirdparty/commonLib

rm -f ./.run.properties

export ANT_HOME=./thirdparty/ant
export ANT_CMD=$ANT_HOME/bin/ant
export ANT_OPTS="-Djsystem.current.scenario.name=$SCENARIO_NAME -Dbasedir=. -Dscenarios.base=$PROJECT_CLASSES_PATH -DsutFile=$3 -Xms32M -Xmx256M "
#export ANT_OPTS="$ANT_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"
$ANT_CMD -listener jsystem.runner.AntExecutionListener -lib thirdparty/ant/lib -lib thirdparty/commonLib -lib thirdparty/lib -lib thirdparty/selenium  -lib lib -lib customer_lib -lib $PROJECT_CLASSES_PATH/../lib -lib $PROJECT_CLASSES_PATH -f $PROJECT_CLASSES_PATH/$SCENARIO_NAME.xml

