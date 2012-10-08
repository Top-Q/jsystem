"""
Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.

This module contains functions for loading and running individual tests
from PyUnit test case classes for use by the JSystem runner.
"""

import sys
import os
import unittest
from jyutils import SystemTestCase

class StringTestResult(unittest.TestResult):
    def getErrors(self):
        return "%s\n\n%s" % (
            self.getErrorList('FAILURES', self.failures),
            self.getErrorList('ERRORS', self.errors),
        )

    def getDescription(self, test):
        return test.shortDescription() or str(test)
        
    def getErrorList(self, flavour, errors):
        lines = []
        for test, err in errors:
            lines.append("%s: %s" % (flavour,self.getDescription(test)))
            lines.append("-" * 70)
            lines.append("%s" % err)
        return "\n".join(lines)    

def loadTestsFromModule(modulePath):
    modulePath, moduleFile = os.path.split(modulePath)
    moduleName = os.path.splitext(moduleFile)[0]
    if modulePath not in sys.path:
        sys.path.insert(0, modulePath)
    tests = []
    moduleTestSuite = unittest.defaultTestLoader.loadTestsFromModule(__import__(moduleName))
    for classTestSuite in moduleTestSuite._tests:
        for test in classTestSuite._tests:
            if isinstance(test, SystemTestCase):
                tests.append(test)
    return tests
    
def getTestTagsFromModule(modulePath):
    tags = []
    for test in loadTestsFromModule(modulePath):
        tags.append(".".join(test.id().split(".")[-2:]))
    return tags
    
def getTestByTag(modulePath, tag):
    className, methodName = tag.split(".", 1)
    modulePath, moduleFile = os.path.split(modulePath)
    moduleName = os.path.splitext(moduleFile)[0]
    if modulePath not in sys.path:
        sys.path.insert(0, modulePath)
    module = __import__(moduleName)
    testCaseClass = getattr(module, className)
    return testCaseClass(methodName)

def runTests(tests):
    result = StringTestResult()
    unittest.TestSuite(tests).run(result)
    return result
    
def runTest(test):
    result = StringTestResult()
    test.run(result)
    return result

