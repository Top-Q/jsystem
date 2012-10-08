from jyutils import *
from java.io import File
from jsystem.utils import FileUtils
from jsystem.framework import FrameworkOptions

'''
To make this simple hello world test run from Eclipse 
add the following folders and jars to jython path:

runner/lib/jython
runner/lib/jsystemCore.jar
all jars under runner/thirdparty/commonLib

To run this test from the runner do the following:
in jsystem.properties  add the following property:
script.engines=jsystem.framework.scripts.jython.JythonScriptEngine

More information about jysthon tests can be found here:
http://www.jsystemtest.org/sites/default/files/help/Chapter 4 JSystem Framework Services.htm#_Toc203711554




'''
class JythonTestsFunctionality(SystemTestCase):
    '''    
    
    '''
    def testJythonTests(self):
        report.report('hello world')
