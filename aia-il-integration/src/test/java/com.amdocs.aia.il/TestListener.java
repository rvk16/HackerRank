package com.amdocs.aia.il;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.time.LocalDateTime;

import static com.amdocs.aia.il.utils.LogUtils.log;

public class TestListener extends TestListenerAdapter {

    private String jenkinsGitCommit;
    private String url;

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        final String suiteName;
        String gitCommit = System.getenv("GIT_COMMIT");
        String gitBuildTag = System.getenv("BUILD_TAG");
        if (gitCommit != null) {
            jenkinsGitCommit = gitCommit.substring(0, 7);
            suiteName = testContext.getName() + '-' + jenkinsGitCommit;
        } else {
            suiteName = testContext.getName() + '-' + LocalDateTime.now();
        }
        String hostIP = System.getenv("HOST_IP");
        if (jenkinsGitCommit != null) {
            url = "http://" + hostIP + ".eaas.amdocs.com:9090" + '/' + jenkinsGitCommit;
        }
        log(">>>>>>>>>>>>>>> Suite " + suiteName + ' ' + gitBuildTag + "  STARTED....");
    }

    @Override
    public void onTestStart(ITestResult tr) {
        log(">>>>>>>>>>>>>>> Test " + tr.getTestClass().getName() + '.' + tr.getName() + " STARTED....");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        log(">>>>>>>>>>>>>>> Test " + tr.getTestClass().getName() + '.' + tr.getName() + " PASSED. \n"
                + " Logs:" + url);

    }

    @Override
    public void onTestFailure(ITestResult tr) {
        log(">>>>>>>>>>>>>>> Test " + tr.getTestClass().getName() + '.' + tr.getName()
                + " FAILED.... \n"
                + " Logs:" + url);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        log(">>>>>>>>>>>>>>> Test " + tr.getTestClass().getName() + '.' + tr.getName()
                + " SKIPPED.... \n"
                + " Logs:" + url);
    }

    @Override
    public void onConfigurationFailure(ITestResult tr) {
        super.onConfigurationFailure(tr);
        log(">>>>>>>>>>>>>>> Test Configuration " + tr.getTestClass().getName() + '.' + tr.getName()
                + " FAILED.... \n"
                + " Logs:" + url);
    }

    @Override
    public void onConfigurationSuccess(ITestResult tr) {
        super.onConfigurationFailure(tr);
        log(">>>>>>>>>>>>>>> Test Configuration " + tr.getTestClass().getName() + '.' + tr.getName() + " PASSED....");
    }

    @Override
    public void onConfigurationSkip(ITestResult tr) {
        super.onConfigurationFailure(tr);
        log(">>>>>>>>>>>>>>> Test Configuration " + tr.getTestClass().getName() + '.' + tr.getName() + " SKIPPED....");
    }
}