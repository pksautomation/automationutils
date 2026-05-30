package com.innovaccer.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.ElementNotSelectableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.Reporter;


/**
 * 
 * @author pramod.singh
 *
 */
public class Log {

	private static Boolean escapeOutput = false;
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @param color
	 * @author i0465
	 */
	public static void Comment(String message, Config testConfig, String color) {
		try {
			boolean test = testConfig.logToStandardOut;
			if (test && (testConfig.getRunTimeProperty("beforeHook") == null
					|| testConfig.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
				logToStandard(message);
			if ((testConfig.logsMode) && (testConfig.getRunTimeProperty("beforeHook") == null
					|| testConfig.getRunTimeProperty("beforeHook").equalsIgnoreCase("false")))
				writeMessageInReport(testConfig, message);
		} catch (Exception e) {
			testConfig.logException(e);
		}
	}
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void Comment(String message, Config testConfig) {
		Comment(message, testConfig, "Black");
	}

	// @Step("Fail:\"{0}\"")
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void Fail(String message, Config testConfig) {
		testConfig.isFailScenarioStatus = true;
		if (testConfig.getRunTimeProperty("LogPageInfo") != null && (testConfig.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true"))) 
			PageInfo(testConfig);
		  
		failure(message, testConfig);
		
	}

	

	// @Step("Fail:\"{0}\"")
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void FailWithoutPageInfoLogging(String message, Config testConfig) {
		failure(message, testConfig);
	}
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void failure(String message, Config testConfig) {
		String tempMessage =  message;
		testConfig.isFailScenarioStatus = true;
		testConfig.softAssert.fail(tempMessage);
		if (testConfig.logToStandardOut)
			logToStandard(tempMessage);
		if (!escapeOutput) {
			message = tempMessage;
		}
		if(testConfig.logsMode || testConfig.logsModeForException)
			writeMessageInReport(testConfig,message);

		// Stop the execution if end execution flag is ON
		if (testConfig.endExecutionOnfailure) {
			if(testConfig.logsMode) {
				Assert.fail(tempMessage);
			}
			else
				Assert.fail(" --> [Fail] Something went wrong in execution");
		}
	}
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void Failfinal(String message, Config testConfig) {
		try {
			if (testConfig.getRunTimeProperty("LogPageInfo") != null
					&& (testConfig.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true")))
				PageInfo(testConfig);
			testConfig.endExecutionOnfailure=true;
			Log.failure(message, testConfig);

		} catch (Exception e) {
			testConfig.logException("Unable to log page info:- " ,e, false);
		}
	}
	/**
	 * 
	 * @param message
	 * @author i0465
	 */
	private static void logToStandard(String message) {
		System.out.println(message);
	}

	/**
	 * Function for Embed Screen shot
	 * @param testConfig
	 * @author i0465
	 */
	public static void PageInfo(Config testConfig) {
		//testConfig.logComment("Is screenshot enabled = " + testConfig.enableScreenshot);
		testConfig.enableScreenshot = true;
		if (testConfig.enableScreenshot && testConfig.logsMode) {
			if (testConfig.driver != null && testConfig.testScenario != null) {
				File dest = Browser.getScreenShotFile(testConfig);
				Browser.takeScreenShoot(testConfig, dest);
			}
		}
	}
	

	// @Step("Pass:\"{0}\"")
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void Pass(String message, Config testConfig) {
		if (testConfig.logToStandardOut)
			logToStandard(message);
		if(testConfig.logsMode)
			writeMessageInReport(testConfig,message);
	}

	// @Step("Warning:\"{0}\"")
	/**
	 * 
	 * @param message
	 * @param testConfig
	 * @author i0465
	 */
	public static void Warning(String message, Config testConfig) {
		if (testConfig.logToStandardOut)
			logToStandard(message);
		if(testConfig.logsMode)
			writeMessageInReport(testConfig,message);
	}

	public static void Warning(String message, Config testConfig, boolean logPageInfo) {
		if (logPageInfo)
			PageInfo(testConfig);

		Warning(message, testConfig);
	}
	
	/**
	 * 
	 * @param testConfig
	 * @param message
	 * @author i0465
	 */
	private static void writeMessageInReport(Config testConfig,String message) {
		testConfig.testScenario.write(message);
		testConfig.testLog = testConfig.testLog.concat(message);
	}
	
	/**
	 * Function for Embed Message In Report
	 * @author ranjeet
	 * @param testConfig
	 * @param message
	 */
	public static void embedMessageAsHTMLInReport(Config testConfig ,String message) {		
		String msg = "<p style=\"color:red;\">"+message.replace("\n", "</br>")+"</span>";
		testConfig.testScenario.embed(msg.getBytes(), "text/html");
		testConfig.testLog = testConfig.testLog.concat(msg);
	}
}
