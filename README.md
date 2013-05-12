# JSystem Framework #

[JSystem Site](http://www.jsystem.org/)</br>

[JSystem Release Notes](https://github.com/Top-Q/jsystem/wiki/Release-Notes)</br>

JSystem is a professional open-source framework for writing and managing automated system tests.

JSystem is a collaborative environment for all members of the QA lifecycle:

* The automation expert: JSystem helps the test automation expert manage any type of user, JSystem has built in integration with Selenium, Autoit, and practically any API based protocols like Telnet/SSH/RS232, SNMP, XML, Tcl, etc

* QA engineer: with JSystem the QA engineer can create and run test-scenarios without any need to see or touch code using simple drag and drop approach.

* The testing manager: JSystem provides a central framework for the most distributed and complex testing environments, enabling central management and reporting that give a clear picture of project status.

With a unique methodology that handles automation as a structured software project, JSystem enables true scalability of the testing project - by leveraging pre-written test-scripts to create new tests.
JSystem is used by many enterprise organizations like: Juniper, Nokia-Siemens, ECI, Alcatel-Lucent, Avaya Ericson and many more
## The JSystem framework is comprised of the following components: ##

* JSystem Services (Java API) - exposes JSystem services.
* JSystem Drivers- Java modules used to interface with devices and applications in the system under test.
* JSystem GUI Interface (JRunner) - GUI application interface used for creating and running test scenarios.
* JSystem Agent - an execution engine used to run scenarios on a distributed setup.
* JSystem Eclipse plug-in - accelerates the development environment setup and enforces JSystem conventions.
* JSystem deploys several open source projects, two of the central open source projects are JUnit used for writing tests and Ant used as the scenario execution engine.

# How to work with the project #
1. Clone the whole jsystem root folder
2. Eclipse - import the jsystem root folder with all its projects
3. Run maven install on the jsystem-parent project (without the tests)
4. Run maven install on the jsystem-runner project
