package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Log;

public class LoggerHelper {

    public Config testConfig;

    public LoggerHelper(Config testConfig) {
        this.testConfig = testConfig;
    }

    public void logComment(String message) {
        testConfig.logComment(message);
    }

    public void logWarning(String message) {
        testConfig.logWarning(message);
    }

    public void logHighlight(String message) {
        testConfig.logHighLight(message);
    }

    public void logException(Exception exception) {
        testConfig.logException(exception);
    }

    public void logExceptionAndSkipFailure(String message, Throwable e, boolean... isTakeScreenShot) {
        testConfig.logExceptionSkipFailure(message, e, isTakeScreenShot);
    }

    public void logException(String message, Throwable e, boolean... isTakeScreenShot) {
        testConfig.logException(message, e, isTakeScreenShot);
    }

    public void logFailureException(Exception exception) {
        testConfig.logFailureException(exception);
    }
    
    public void embedStringAsHTMLInReport(String text) {
    	Log.embedMessageAsHTMLInReport(testConfig, text);
    }

}