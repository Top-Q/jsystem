package com.aqua.services.lifecycle;

import jsystem.framework.report.*;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTest;
import junit.framework.SystemTestCase4;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestListenerExample extends SystemTestCase4 {

    @Before
    public void setup(){
        ReportPortalListener myListener = new ReportPortalListener();
        ListenerstManager.getInstance().addListener(myListener);
    }

    @Test
    public void test1(){
        report.report("In test 1");
    }

    @Test
    public void test2(){
        report.report("In test 2");
    }






}
