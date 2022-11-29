package com.innovaccer.utils.v2;

import org.openqa.selenium.*;

import com.innovaccer.utils.Browser;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.Log;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtils {

    public String uniqueId = null;
    public String timeStamp = null;
    Config configInstance=null;

    public LoggerUtils(Config testConfig) {
        init(testConfig);
    }

    public LoggerUtils() {
        init(Config.getConfig());
    }

    private void init(Config config) {
    	configInstance=config;
        uniqueId = configInstance.getUniqueId();

    }

    private void logToStandard(String message) {
        System.out.println(message);
    }

    private void writeMessageInReport(Config testConfig, String message) {
//        testConfig.getScenario().write(message);
//        testConfig.setTestLog(testConfig.getTestLog().concat(message));
    }

    /**
     * This method logs Failure
     *
     * @param message    -> message to log with failure
     * @param testConfig -> config instance
     */
    private void failure(String message) {
    	configInstance.setFailScenarioStatus(true);
    	AssertionUtils assertUtils = new AssertionUtils(configInstance);
        if (configInstance.isLogToStandardOut())
            logToStandard(message);
        if (configInstance.isLogsMode())
            writeMessageInReport(configInstance, message);
        if (configInstance.isEndExecutionOnfailure()) {
            if (configInstance.isLogsMode()) {
            	assertUtils.assertFail(message);
            } else
            	assertUtils.assertFail(" --> [Fail] Something went wrong during Execution");
        }
    }

    /**
     * This method is used to take screenshot
     *
     * @param testConfig -> config instance
     */
    private void getPageInfo() {
    	configInstance.setEnableScreenshot(true);
        if (configInstance.isEnableScreenshot() && configInstance.isLogsMode()) {
            if (configInstance.getDriver() != null && configInstance.getScenario()!= null) {
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
        configInstance.getScenario().embed(msg.getBytes(), "text/html");
        configInstance.setTestLog(configInstance.getTestLog().concat(msg));
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
            getPageInfo();
    }

    /**
     * @param message     -> message to be logged
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void logPass(String message, boolean... logPageInfo) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Fail] --> " + message;
        logComment(message);
        if (logPageInfo[0])
            getPageInfo();
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
        failure(message);
        if (logPageInfo)
            getPageInfo();
    }

    /**
     * This method is used to log a failure message
     *
     * @param message     -> message to be logged
     * @param logPageInfo -> boolean to enable/disable logging page info
     */
    public void logFail(String message, boolean... logPageInfo) {
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Fail] --> " + message;
        failure(message);
        if (logPageInfo.length>0)
            getPageInfo();
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
            boolean test = configInstance.isLogToStandardOut();
            if (test && (configInstance.getRunTimeProperty("beforeHook") == null
                    || configInstance.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
                logToStandard(message);
            if ((configInstance.isLogsMode()) && (configInstance.getRunTimeProperty("beforeHook") == null
                    || configInstance.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
                writeMessageInReport(configInstance, message);
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
        if (configInstance.isLogToStandardOut())
            logToStandard(message);
        if (configInstance.isLogsMode())
            writeMessageInReport(configInstance, message);
    }

    /**
     * This method is used to log a exception along with a message and take screenshot(if needed)
     *
     * @param message        -> message to be logged
     * @param e              -> Exception
     * @param takeScreenshot -> boolean to enable/disable screenshot
     */
    public void logException(String message, Throwable e, boolean... takeScreenshot) {
        String errorFilePath = "";
        configInstance.setLogsModeForException(true);
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
                message = message.concat("\n Illegal Argument Exception : ").concat(e.getMessage().split("\n")[0].concat("\n" + stbr));
                break;
            case "ElementClickInterceptedException":
                message = message.concat("\nElement Click Not Intercepted On Page : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "NoSuchElementException":
                message = message.concat("\nElement Not Available On Page : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "NoSuchWindowException":
                message = message.concat("\nNo Such Window Available : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "NoSuchFrameException":
                message = message.concat("\nNo Such Frame Available : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "NoAlertPresentException":
                message = message.concat("\nNo Alert Present On This Page : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "InvalidSelectorException":
                message = message.concat("\nInvalid Selector By Using Locator : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "ElementNotVisibleException":
                message = message.concat("\nElement Not Selectable By Using Locator : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "ElementNotSelectableException":
                message = message.concat("\nElement Not Selectable By Using Locator : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "TimeoutException":
                message = message.concat("\nTimeout Occur By Using Locator : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "NoSuchSessionException":
                message = message.concat("\nNo Such Session Exception Occur : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "StaleElementReferenceException":
                message = message.concat("\nStale Element Reference Occur By Using Locator : ").concat(e.getMessage().split("\n")[0]).concat("\n" + stbr);
                break;
            case "AssertionError":
                message = message.concat("\n Assertion Error : ").concat(e.getMessage()).concat("\n" + stbr);
                break;
            default:
                message = message.concat("\n Java Exception : " + e.getMessage()).concat("\n" + stbr);
                break;
        }
        timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Exception] --> " + message;

        if (!configInstance.isIslogExceptionSkip()) {
            if (takeScreenshot.length == 0) {
                failure(message);
            } else if (takeScreenshot[0]) {
                failure(message);
                embedMessageAsHTMLInReport(configInstance, message);
            } else if (!takeScreenshot[0]) {
                failure(message);
                embedMessageAsHTMLInReport(configInstance, message);
            }
        } else {
            if (takeScreenshot.length == 0) {
                logComment(message);
            } else if (takeScreenshot[0]) {
                logComment(message);
                if (configInstance.getRunTimeProperty("LogPageInfo") != null && (configInstance.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true")))
                    getPageInfo();
                embedMessageAsHTMLInReport(configInstance, message);
            } else if (!takeScreenshot[0]) {
                logComment(message);
                embedMessageAsHTMLInReport(configInstance, message);
            }
        }
        stbr.delete(0, stbr.length());
        configInstance.setLogsModeForException(false);
    }
    
    public void logException(Throwable e) {
		this.logException("", e);
    }

    /**
     * This method is used to log a failure exception
     *
     * @param exception -> Exception
     */
    public void logFailureException(Throwable exception) {
        logException("", exception, false);
    }

    /**
     * This method is used to embed text to HTML Report
     *
     * @param text -> text to be embedded
     */
    public void embedStringAsHTMLInReport(String text) {
        embedMessageAsHTMLInReport(configInstance, text);
    }
    
    /**
	 * Function for Embed Screen shot
	 * @param testConfig
	 * @author i0465
	 */
	private void PageInfo(Config testConfig) {
		testConfig.setEnableScreenshot(true);
		BrowserUtils BrowserUtils = new BrowserUtils(testConfig);
		if (testConfig.isEnableScreenshot() && testConfig.isLogsMode()) {
			if (testConfig.getDriver() != null && testConfig.getScenario() != null) {
				File dest = BrowserUtils.getScreenshotFile();
				BrowserUtils.takeScreenShoot(dest);
			}
		}
	}
    
    /**
	 * This method fail test scenarios just after calling it
	 * @param msg
	 * @author pramod.singh
	 */
	public void failFinalTestScenarios(String msg) {
		configInstance.setEndExecutionOnfailure(true);
		try {
			if (configInstance.getRunTimeProperty("LogPageInfo") != null
					&& (configInstance.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true")))
				PageInfo(configInstance);
			failure(msg);

		} catch (Exception e) {
			logException("Unable to log page info:- " ,e, false);
		}
	}
	
	public void logExceptionAndSkipFailure(String message, Throwable e, boolean... isTakeScreenShot) {
		configInstance.setIslogExceptionSkip(true);
		 if(isTakeScreenShot.length != 0 && isTakeScreenShot[0])
				 logException(message, e, true);
		else
			 logException(message, e, false);
		 configInstance.setIslogExceptionSkip(false);
    }

}