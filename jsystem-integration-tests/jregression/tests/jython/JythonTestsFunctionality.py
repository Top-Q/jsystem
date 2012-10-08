from jyutils import *
from java.io import File
from jsystem.utils import FileUtils
from jsystem.framework import FrameworkOptions
from com.aqua.jsystemobject import CreateEnvFixture
from com.aqua.general import ScenarioUtils

class JSysTestCase(SystemTestCase):
    """
    This class replicates most of the Java version of JSysTestCase's
    functionality for regression tests written in Jython. 
    """
    def setUp(self):
        self.envController = system.getSystemObject("envController")
        self.jsystem = self.envController.getJSystemEnv()
        report.report("jsystem is " + str(self.jsystem))
        self.backupJSystemProperties()
        self.jsystem.setJSystemProperty(FrameworkOptions.SCRIPT_ENGINES, "jsystem.framework.scripts.jython.JythonScriptEngine")

    def tearDown(self):
        self.restoreJSystemProperties()
    
    def backupJSystemProperties(self):
        self.userDir = self.jsystem.getUserDir()
        orig = File(self.userDir, "jsystem.properties")
        back = File(orig.getParentFile(), "jsystem.properties.back")
        FileUtils.copyFile(orig, back)
    
    def restoreJSystemProperties(self):
        if self.userDir is None:
            return

        orig = File(self.userDir, "jsystem.properties")
        back = File(orig.getParentFile(), "jsystem.properties.back")
        FileUtils.copyFile(back, orig)

class JythonTestsFunctionality(JSysTestCase):
    __fixture__ = CreateEnvFixture
    __tearDownFixture__ = RootFixture

    def testJythonTests(self):
        """5.2.7.4 Run Jython tests"""
        self.jsystem.launch()

        report.step("create a scenario with 2 jython tests")
        scenarioName = self.jsystem.getCurrentScenario()
        ScenarioUtils.createAndCleanScenario(self.jsystem, scenarioName)

        # add two jython tests to the scenario
        self.jsystem.addTest("JythonTests.test1", "jython")
        self.jsystem.addTest("JythonTests.test2", "jython")
        
        report.step("play the scenario and wait for it to end")
        self.jsystem.play()
        self.jsystem.waitForRunEnd()
        
        report.step("check that 2 tests ran - one succeeded and one failed")
        self.jsystem.checkNumberOfTestExecuted(2)
        self.jsystem.checkNumberOfTestsPass(1)

class JythonTestsParameters(JSysTestCase):
    def testParameters(self):
        """5.2.7.4 Run Jython tests with parameters"""
        self.jsystem.launch()

        report.step("create a scenario with 2 jython tests")
        scenarioName = self.jsystem.getCurrentScenario()
        ScenarioUtils.createAndCleanScenario(self.jsystem, scenarioName)

        # add two jython tests to the scenario
        self.jsystem.addTest("JythonParamTests.test1", "jython")
        self.jsystem.addTest("JythonParamTests.test2", "jython")

        report.step("set parameters")
        jsystem.setTestParameter(1, "General", "value", "1", false);
        jsystem.setTestParameter(2, "General", "value", "3", false);
        
        report.step("play the scenario and wait for it to end")
        self.jsystem.play()
        self.jsystem.waitForRunEnd()
        
        report.step("check that 2 tests ran - one succeeded and one failed")
        self.jsystem.checkNumberOfTestExecuted(2)
        self.jsystem.checkNumberOfTestsPass(1)
    
