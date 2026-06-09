package com.pksautomation.utils.v2;

import org.apache.commons.lang.StringUtils;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

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
    	String timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    	message = "[" + this.uniqueId + "] " + "[" + timeStamp + "]" + message;
        System.out.println(message);
    }

    private void writeMessageInReport(Config testConfig, String message) {
        String timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String regex = "\\[[^\\[]*\\]";
        String replaceString="";
        if(configInstance.getRunTimeProperty("ExtentReportEnable") != null || configInstance.getRunTimeProperty("ExtentReportEnable").equalsIgnoreCase("true")) {
 
    		if(message.contains("[Fail]"))
    		{
    			message=message.replaceFirst(regex, replaceString);
    			configInstance.getExtentTestLog().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
    		}
    		else if(message.contains("[INFO]") )
    		{
    			message=message.replaceFirst(regex, replaceString);
    			configInstance.getExtentTestLog().log(Status.INFO, MarkupHelper.createLabel(message, ExtentColor.WHITE));
    		}
    		else if(message.contains("[WARNING]")) {
    			message=message.replaceFirst(regex, replaceString);
    			configInstance.getExtentTestLog().log(Status.WARNING, MarkupHelper.createLabel(message, ExtentColor.ORANGE));
    		}
    		else {
    			message=message.replaceFirst(regex, replaceString);
    			configInstance.getExtentTestLog().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));   		
    	
    		}
    		message = "[" + this.uniqueId + "] " + "[" + timeStamp + "]" + message;
        }
    	else {
       			message = "[" + this.uniqueId + "] " + "[" + timeStamp + "]" + message;
        		testConfig.getScenario().write(message);
        	}
        testConfig.setTestLog(testConfig.getTestLog().concat(message + "\n"));
    }

    /**
     * This method logs Failure
     *
     * @param message    -> message to log with failure
     * @param testConfig -> config instance
     */
    private void failure(String message) {
    	configInstance.setTestResult(true);
    	AssertionUtils assertUtils = new AssertionUtils(configInstance);
    	configInstance.getSoftAssert().fail(message);
        if (configInstance.isLogToStandardOut())
            logToStandard(message);
        if (configInstance.isLogsMode())
            writeMessageInReport(configInstance, message);
        if (configInstance.isEndExecutionOnfailure()) {
            if (configInstance.isLogsMode()) {
            	assertUtils.assertFail(message,false);
            } else
            	assertUtils.assertFail("[Fail] Something went wrong during Execution",false);
        }
    }

    /**
     * This method is used to take screenshot
     *
     * @param testConfig -> config instance
     */
    private void getPageInfo() {
        if (configInstance.isEnableScreenshot() && configInstance.isLogsMode()) {
            if (configInstance.getDriver() != null) {
            	File screenshotFilepath=Config.getConfig().getUtilityObjectManager().getBrowserUtils().getScreenshotFile();
				Config.getConfig().getUtilityObjectManager().getBrowserUtils().captureScreenShoot(screenshotFilepath);
				attachScreenShot(screenshotFilepath.getAbsolutePath(), screenshotFilepath.getName());
            }
        }
    }
    
    public void attachScreenShot(String filePathurl,String fileName) {
    	logComment("<B>Screenshot : </B>:- <a href=" + filePathurl + " target='_blank' >"  + fileName + "</a>");
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
        String message;
        message= "[Pass] '" + what + "' as :-'" + actual + "'";
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
        if (logPageInfo)
            getPageInfo();
    }

    /**
     * @param message     -> message to be logged
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void logPass(String message, boolean... logPageInfo) {
        message= "[Pass] '" + message;
        //message = "[" + this.uniqueId + "] " + "[" + timeStamp + "] [Pass] --> " + message;
        
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
        
        String message = " [Fail] Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
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
        message="[Fail] --> " + message;
        failure(message);
        if (logPageInfo.length>0 && logPageInfo[0])
            getPageInfo();
    }

    /**
     * This method is used to log a message
     *
     * @param message -> message to be logged
     */
    public void logComment(String message) {
        message =  "[INFO]  " + message;
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
        message ="[WARNING] " + message;
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
            if (ss.getClassName().startsWith("com.pksautomation")) {
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
	 * @author Pramod Singh
	 */
	private void PageInfo() {
		configInstance.setEnableScreenshot(true);
		BrowserUtils BrowserUtils = new BrowserUtils();
		if (configInstance.isEnableScreenshot() && configInstance.isLogsMode()) {
			if (configInstance.getDriver() != null && configInstance.getScenario() != null) {
				File dest = BrowserUtils.getScreenshotFile();
				BrowserUtils.takeScreenShoot(dest);
			}
		}
	}
    
    /**
	 * This method fail test scenarios just after calling it
	 * @param msg
	 * @author Pramod Singh
	 */
	public void failFinalTestScenarios(String msg) {
		configInstance.setEndExecutionOnfailure(true);
		try {
			if (configInstance.getRunTimeProperty("LogPageInfo") != null
					&& (configInstance.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true")))
				PageInfo();
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

		   
			/**
			 * Log Exception But Skip Failure
			 * @param message
			 * @param e
			 * @param IsTakeScreenShot
			 * @author Pramod Singh
			 */
			public void logExceptionSkipFailure(String message , Throwable e , boolean ...IsTakeScreenShot) {
				 configInstance.setIslogExceptionSkip(true);
				 if(IsTakeScreenShot.length != 0)
					 if(IsTakeScreenShot[0])
						 this.logException(message, e, true);
				else
					this.logException(message, e, false);
				 
				 configInstance.setIslogExceptionSkip(false);
			}
			
			/**
			 * 
			 * @param <T>
			 * @param what
			 * @param expected
			 * @param actual
			 * @author Pramod Singh
			 */
			public <T> void logFail(String what, T expected, T actual,boolean... pageInfo)
			{
				String message = " Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
				logFail(message);
			}
			/**
			 * 
			 * @param what
			 * @param expected
			 * @param actual
			 * @author Pramod Singh
			 */
			public void logFail(String what, String expected, String actual)
			{
				String message = null;
				message = "Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
				timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
				logFail(message);
			}

			/**
			 * 
			 * @param what
			 * @param expected
			 * @param actual
			 * @author Pramod Singh
			 */
			public void logWarning(String what, String expected, String actual)
			{
				String message = what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
				logWarning(message);
			}


			/**
			 * 
			 * @param <T>
			 * @param what
			 * @param actual
			 * @author Pramod Singh
			 */
			public <T> void logPass(String what, T actual)
			{
				timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
				String message =  what + "' as :-'" + actual + "'";
				logPass(message);
			}

			/**
			 * 
			 * @param what
			 * @param actual
			 * @author Pramod Singh
			 */
			public void logPass(String what, String actual)
			{
				timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
				String message = StringUtils.replaceEach(actual, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
				message =  what + "' as :-'" + message + "'";
				logPass(message);
			}

			/**
			 * 
			 * @param message
			 * @param logPageInfo
			 * @author Pramod Singh
			 */
			public void logWarning(String message, boolean logPageInfo)
			{
				timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
				if(logPageInfo)
					PageInfo();
				logWarning(message);
			}
			
		    public void attachTestLogs(String filePathurl,String fileName) {
		    	logComment("<B>Log File  :</B>:- <a href=" + filePathurl + " target='_blank' >" + fileName + "</a>");
		    }


}