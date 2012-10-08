"""
Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.

This module contains utility functions for use within Jython unit tests written
for Aqua JSystem. They allow logging and access to various jsystem components 
(sut, system objects, etc.).
"""

import unittest

from java.util.logging import Logger
from jsystem.framework.system import SystemManagerImpl
from jsystem.framework.report import ListenerstManager
from jsystem.framework.sut import SutFactory
from jsystem.framework.monitor import MonitorsManager
from jsystem.framework import RunProperties
from jsystem.framework.fixture import RootFixture

log = Logger.getLogger("Jython")
report = ListenerstManager.getInstance()
system = SystemManagerImpl.getInstance()
sut = SutFactory.getInstance().getSutInstance()
monitors = MonitorsManager.getInstance()
runProperties = RunProperties.getInstance()

class Parameter(object):
    pass
        
class SystemTestCase(unittest.TestCase):
    __fixture__ = RootFixture
    __tearDownFixture__ = RootFixture
    __parameters__ = dict()
    def __init__(self, name):
        unittest.TestCase.__init__(self, name)
        for name, info in self.__parameters__.items():
            setattr(self, name, "")
