#!/bin/sh

#Setting Files To Unix mode

chmod 777 -R /usr/local/aqua/reports/apache-tomcat-5.5.23/bin/*
chmod 777 -R /usr/local/aqua/runner/run
dos2unix /usr/local/aqua/reports/apache-tomcat-5.5.23/bin/startup.sh
dos2unix /usr/local/aqua/runner/run

#Setting Runner desktop shortcut

 echo "#!/bin/sh" > ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "" >> ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "#Runnig Aqua Runner">>  ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "Type=Application" >>  ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "Name=\"Aqua JSystem Runner\"">>  ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "cd /usr/local/aqua/runner/">>  ~/Desktop/Aqua\ JSystem\ Runner.desktop
 echo "./run" >>  ~/Desktop/Aqua\ JSystem\ Runner.desktop 

#Setting Apach desktop shortcut

 echo "#!/bin/sh" > ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "export JAVA_HOME=/usr/bin/java" >>  ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "#Runnig Apache" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "Type=Application" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "Name=\"Apache Tomcat 5.5\"" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "cd /usr/local/aqua/reports/apache-tomcat-5.5.23/bin/" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
 echo "./startup.sh" >> ~/Desktop/Apache\ Tomcat\ 5.5.desktop
