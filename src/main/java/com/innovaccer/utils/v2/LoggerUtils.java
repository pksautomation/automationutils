package com.innovaccer.utils.v2;

import org.openqa.selenium.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtils {

    private Config testConfig;
    private AssertionUtils assertionUtils;
    public String uniqueId = null;
    public String timeStamp = null;

    public LoggerUtils(Config testConfig) {
        init(testConfig);
    }

    public LoggerUtils() {
        init(Config.getConfig());
    }

    private void init(Config config) {
        this.testConfig = config;
        uniqueId = config.uniqueId;
        assertionUtils = new AssertionUtils(config);
    }

    private void logToStandard(String message) {
        System.out.println(message);
    }

    private void writeMessageInReport(Config testConfig, String message) {
        testConfig.testScenario.write(message);
        testConfig.testLog = testConfig.testLog.concat(message);
    }

    /**
     * This method logs Failure
     *
     * @param message    -> message to log with failure
     * @param testConfig -> config instance
     */
    public void failure(String message, Config testConfig) {
        testConfig.isFailScenarioStatus = true;
        testConfig.softAssert.fail(message);
        if (testConfig.logToStandardOut)
            logToStandard(message);
        if (testConfig.logsMode || testConfig.logsModeForException)
            writeMessageInReport(testConfig, message);
        if (testConfig.endExecutionOnfailure) {
            if (testConfig.logsMode) {
                assertionUtils.assertFail(message);
            } else
                assertionUtils.assertFail(" --> [Fail] Something went wrong during Execution");
        }
    }

    /**
     * This method is used to take screenshot
     *
     * @param testConfig -> config instance
     */
    private void getPageInfo(Config testConfig) {
        testConfig.enableScreenshot = true;
        if (testConfig.enableScreenshot && testConfig.logsMode) {
            if (testConfig.driver != null && testConfig.testScenario != null) {
//           TODO: To be resoled -> BrowserUtils.takeScreenshot();
            }
        }
    }

    /**
     * This method adds a message to HTML Report
     *
     * @param testConfig -> config instance
     * @param message    -> message to be added on HTML Report
     */
    public void embedMessageAsHTMLInReport(Config testConfig, String message) {
        String msg = "<p style=\"color:red;\">" + message.replace("\n", "</br>") + "</span>";
        testConfig.testScenario.embed(msg.getBytes(), "text/html");
        testConfig.testLog = testConfig.testLog.concat(msg);
    }

    /**
     * @param what        -> Object to be verified
     * @param actual      -> Actual Value of Object
     * @param logPageInfo -> Boolean to enable/disable logging page info
     * @param <T>
     */
    public <T> void logPass(String what, T actual, boolean logPageInfo) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String message = "[" + this.uniqueId + "] " + "[" + timeStamp + "]  Verified '" + what + "' as :-'" + actual + "'";
        logComment(message);
        if (logPageInfo)
            getPageInfo(this.testConfig);
    }

    /**
     * @param what        -> Object to be verified
     * @param expected    -> Expected Value of Object
     * @param actual      -> Actual Value of Object
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public <T> void logFail(String what, T expected, T actual, boolean logPageInfo) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String message = " [Fail] --> Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Fail] --> " + message;
        failure(message, this.testConfig);
        if (logPageInfo)
            getPageInfo(this.testConfig);
    }

    /**
     * This method is used to log a failure message
     *
     * @param message     -> message to be logged
     * @param logPageInfo -> boolean to enable/disable logging page info
     */
    public void logFail(String message, boolean logPageInfo) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Fail] --> " + message;
        failure(message, this.testConfig);
        if (logPageInfo)
            getPageInfo(this.testConfig);
    }

    /**
     * This method is used to log a message
     *
     * @param message -> message to be logged
     */
    public void logComment(String message) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [INFO] -->  " + message;
        try {
            boolean test = testConfig.logToStandardOut;
            if (test && (testConfig.getRunTimeProperty("beforeHook") == null
                    || testConfig.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
                logToStandard(message);
            if ((testConfig.logsMode) && (testConfig.getRunTimeProperty("beforeHook") == null
                    || testConfig.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
                writeMessageInReport(testConfig, message);
        } catch (Exception e) {
            logFailureException(e);
        }
    }

    /**
     * This method is used to log a warning message
     *
     * @param message -> warning message to be logged
     */
    public void logWarning(String message) {
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [WARNING] --> " + message;
        if (testConfig.logToStandardOut)
            logToStandard(message);
        if (testConfig.logsMode)
            writeMessageInReport(testConfig, message);
    }

    /**
     * This method is used to log a exception along with a message and take screenshot(if needed)
     *
     * @param message        -> message to be logged
     * @param e              -> Exception
     * @param takeScreenshot -> boolean to enable/disable screenshot
     */
    public void logException(String message, Exception e, boolean... takeScreenshot) {
        String errorFilePath = "";
        testConfig.logsModeForException = true;
        StringBuffer stbr = new StringBuffer();
        stbr.append("Error location:- ");
        StackTraceElement[] s = e.getStackTrace();
        for (StackTraceElement ss : s) {
            if (ss.getClassName().startsWith("com.innovaccer")) {
                errorFilePath = ss.getClassName() + ":" + ss.getLineNumber();
                stbr.append(errorFilePath).append("\n");
            }
        }
        switch (e.getClass().getSimpleName()) {
            case "IllegalArgumentException":
                message = message.concat("\n Illegal Argument Exception : ").concat(((IllegalArgumentException) e).getMessage().split("\n")[0].concat("\n" + stbr.toString()));
                break;
            case "ElementClickInterceptedException":
                message = message.concat("\nElement Click Not Intercepted On Page : ").concat(((ElementClickInterceptedException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "NoSuchElementException":
                message = message.concat("\nElement Not Available On Page : ").concat(((NoSuchElementException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "NoSuchWindowException":
                message = message.concat("\nNo Such Window Available : ").concat(((NoSuchWindowException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "NoSuchFrameException":
                message = message.concat("\nNo Such Frame Available : ").concat(((NoSuchFrameException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "NoAlertPresentException":
                message = message.concat("\nNo Alert Present On This Page : ").concat(((NoAlertPresentException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "InvalidSelectorException":
                message = message.concat("\nInvalid Selector By Using Locator : ").concat(((InvalidSelectorException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "ElementNotVisibleException":
                message = message.concat("\nElement Not Selectable By Using Locator : ").concat(((ElementNotVisibleException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "ElementNotSelectableException":
                message = message.concat("\nElement Not Selectable By Using Locator : ").concat(((ElementNotSelectableException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "TimeoutException":
                message = message.concat("\nTimeout Occur By Using Locator : ").concat(((TimeoutException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "NoSuchSessionException":
                message = message.concat("\nNo Such Session Exception Occur : ").concat(((NoSuchSessionException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "StaleElementReferenceException":
                message = message.concat("\nStale Element Reference Occur By Using Locator : ").concat(((StaleElementReferenceException) e).getMessage().split("\n")[0]).concat("\n" + stbr.toString());
                break;
            case "AssertionError":
                message = message.concat("\n Assertion Error : ").concat(e.getMessage()).concat("\n" + stbr.toString());
                break;
            default:
                message = message.concat("\n Java Exception : " + e.getMessage()).concat("\n" + stbr.toString());
                break;
        }
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Exception] --> " + message;

        if (!testConfig.islogExceptionSkip) {
            if (takeScreenshot.length == 0) {
                failure(message, testConfig);
            } else if (takeScreenshot[0]) {
                failure(message, testConfig);
                embedMessageAsHTMLInReport(testConfig, message);
            } else if (!takeScreenshot[0]) {
                failure(message, testConfig);
                embedMessageAsHTMLInReport(testConfig, message);
            }
        } else {
            if (takeScreenshot.length == 0) {
                logComment(message);
            } else if (takeScreenshot[0]) {
                logComment(message);
                if (testConfig.getRunTimeProperty("LogPageInfo") != null && (testConfig.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true")))
                    getPageInfo(testConfig);
                embedMessageAsHTMLInReport(testConfig, message);
            } else if (!takeScreenshot[0]) {
                logComment(message);
                embedMessageAsHTMLInReport(testConfig, message);
            }
        }
        stbr.delete(0, stbr.length());
        testConfig.logsModeForException = false;
    }

    /**
     * This method is used to log a failure exception
     *
     * @param exception -> Exception
     */
    public void logFailureException(Exception exception) {
        logException("", exception, false);
    }

    /**
     * This method is used to embed text to HTML Report
     *
     * @param text -> text to be embedded
     */
    public void embedStringAsHTMLInReport(String text) {
        embedMessageAsHTMLInReport(testConfig, text);
    }
}