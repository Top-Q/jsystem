package com.aqua.services.lifecycle;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import junit.framework.AssertionFailedError;

import java.io.IOException;
import java.util.List;

public class ReportPortalListener implements ExtendTestListener, Reporter {


    @Override
    public void addWarning(junit.framework.Test test) {

    }

    @Override
    public void startTest(TestInfo testInfo) {
        System.out.println("Start test");
    }

    @Override
    public void endRun() {

    }

    @Override
    public void startLoop(AntForLoop loop, int count) {

    }

    @Override
    public void endLoop(AntForLoop loop, int count) {

    }

    @Override
    public void startContainer(JTestContainer container) {

    }

    @Override
    public void endContainer(JTestContainer container) {

    }

    @Override
    public void addError(junit.framework.Test test, Throwable throwable) {

    }

    @Override
    public void addFailure(junit.framework.Test test, AssertionFailedError assertionFailedError) {

    }

    @Override
    public void endTest(junit.framework.Test test) {
        System.out.println("End test");
    }

    @Override
    public void startTest(junit.framework.Test test) {

    }

    @Override
    public int showConfirmDialog(String title, String message, int optionType, int messageType) {
        return 0;
    }

    @Override
    public void report(String title, String message, boolean status, boolean bold) {

    }

    @Override
    public void report(String title, String message, boolean status) {

    }

    @Override
    public void report(String title, boolean status) {

    }

    @Override
    public void report(String title) {
        System.out.println("Something was reporter");
    }

    @Override
    public void step(String stepDescription) {

    }

    @Override
    public void report(String title, Throwable t) {

    }

    @Override
    public void setSilent(boolean status) {

    }

    @Override
    public boolean isSilent() {
        return false;
    }

    @Override
    public void setTimeStamp(boolean enable) {

    }

    @Override
    public void reportHtml(String title, String html, boolean status) {

    }

    @Override
    public void addLink(String title, String link) {

    }

    @Override
    public void saveFile(String fileName, byte[] content) {

    }

    @Override
    public void report(String title, String message, int status, boolean bold) {

    }

    @Override
    public void setData(String data) {

    }

    @Override
    public void startLevel(String level, int place) throws IOException {

    }

    @Override
    public void startLevel(String level) throws IOException {

    }

    @Override
    public void startLevel(String level, EnumReportLevel place) throws IOException {

    }

    @Override
    public void stopLevel() throws IOException {

    }

    @Override
    public void closeAllLevels() throws IOException {

    }

    @Override
    public void startReport(String methodName, String parameters) {

    }

    @Override
    public void startReport(String methodName, String parameters, String classDoc, String testDoc) {

    }

    @Override
    public void endReport() {

    }

    @Override
    public void endReport(String steps, String failCause) {

    }

    @Override
    public boolean isFailToPass() {
        return false;
    }

    @Override
    public void setFailToPass(boolean failToPass) {

    }

    @Override
    public boolean isFailToWarning() {
        return false;
    }

    @Override
    public void setFailToWarning(boolean failToWarning) {

    }

    @Override
    public void report(String title, String message, int status, boolean bold, boolean html, boolean step, boolean link) {

    }

    @Override
    public void report(String title, String message, int status, boolean bold, boolean html, boolean step, boolean link, long time) {

    }

    @Override
    public String getCurrentTestFolder() {
        return null;
    }

    @Override
    public String getLastReportFile() {
        return null;
    }

    @Override
    public void startBufferingReports() {

    }

    @Override
    public void startBufferingReports(boolean printBufferdReportsInRunTime) {

    }

    @Override
    public void stopBufferingReports() {

    }

    @Override
    public List<ReportElement> getReportsBuffer() {
        return null;
    }

    @Override
    public void clearReportsBuffer() {

    }

    @Override
    public void report(ReportElement report) {

    }

    @Override
    public void addProperty(String key, String value) {

    }

    @Override
    public void setContainerProperties(int ancestorLevel, String key, String value) {

    }

    @Override
    public void report(String title, int status) {

    }

    @Override
    public void report(String title, String message, int status) {

    }

    @Override
    public void report(String title, ReportAttribute attribute) {

    }

    @Override
    public void report(String title, String message, ReportAttribute attribute) {

    }

    @Override
    public void report(String title, String message, int status, ReportAttribute attribute) {

    }
}
