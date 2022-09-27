package com.innovaccer.utils.v2;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.innovaccer.utils.Element;
import com.innovaccer.utils.Popup;
import com.innovaccer.utils.Element.How;

/**
 * 
 * @author i0465
 *
 */
public class WaitHelper {

	private Config configInstance;
	private LoggerUtils loggerUtils;
	private PopupUtils popupUtils;

	public WaitHelper(Config config) {
		init(config);
	}

	public WaitHelper() {
		init(Config.getConfig());
	}

	private void init(Config config) {
		this.configInstance = config;
		loggerUtils = new LoggerUtils(configInstance);
		popupUtils = new PopupUtils(this.configInstance);
	}

	public void waitForVisibility(WebElement element, int timeInSeconds, String description) {
		Element.waitForVisibility(this.configInstance, element, timeInSeconds, description);
	}

	public void waitForStaleness(WebElement element, String description) {
		Element.waitForStaleness(configInstance, element, description);
	}

	public void waitForInvisibility(By by, String description) {
		Element.waitForInvisibility(configInstance, by, description);
	}

	public WebElement waitForVisibility(Config configInstance, By by, String description, Long... maxwaitTime) {
		if (maxwaitTime.length > 0)
			return Element.waitForVisibility(configInstance, by, description, maxwaitTime[0]);
		else
			return Element.waitForVisibility(configInstance, by, description);
	}

	public void waitTillElementHasValue(WebElement element, String textToBePresentInValueAttribiute,
			String description) {
		Element.waitTillElementHasValue(configInstance, element, textToBePresentInValueAttribiute, description);
	}

	public void waitForElementToDisappear(WebElement elementName) {
		Element.waitForElementToDisappear(configInstance, elementName);
	}

	public void verifyElementNotPresent(WebElement element, String description) {
		Element.verifyElementNotPresent(configInstance, element, description);
	}

	public void verifyElementPresent(WebElement element, String description) {
		Element.verifyElementPresent(configInstance, element, description);

	}

	/**
	 * Pause the execution for given seconds
	 * 
	 * @param seconds
	 * @author pramod.singh
	 */
	public void wait(int seconds) {
		int milliseconds = seconds * 1000;
		try {
			Thread.sleep(milliseconds);
			configInstance.logComment("Wait for '" + seconds + "' seconds");

		} catch (InterruptedException e) {

		}
	}

	/**
	 * Wait till Element is clickable
	 * 
	 * @param configInstance
	 * @param by
	 * @param maxWaitTimeInSecond
	 * @param description
	 * @return --> WebElement
	 * @author pramod.singh
	 */
	public WebElement waitForElementToBeClickable(By by, String description, int... maxWaitTimeInSecond) {
		WebElement element = null;
		int ObjectWaitTime;
		if (maxWaitTimeInSecond.length == 0) {
			ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
		} else {
			ObjectWaitTime = maxWaitTimeInSecond[0];
		}
		try {
			WebDriverWait wait = new WebDriverWait(configInstance.driver, ObjectWaitTime);
			element = wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			configInstance.logExceptionSkipFailure(description, e, true);
		}
		return element;

	}

	/**
	 * wait for js to load
	 * 
	 * @param configInstance
	 * @author pramod.singh
	 * @return
	 */
	public boolean waitForJStoLoad() {
		JavascriptExecutor javaScript = (JavascriptExecutor) configInstance.driver;
		WebDriverWait wait = new WebDriverWait(configInstance.driver,
				Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime")));
		// System.out.println("Wait for jquery to load");
		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long) javaScript.executeScript("return jQuery.active") == 0);
				} catch (Exception e) {
					return true;
				}
			}
		};

		// wait for Javascript to load
		// System.out.println("Wait for JS to load");
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return javaScript.executeScript("return document.readyState").toString().equals("complete");
			}
		};

		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}

	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element     element to be searched
	 * @param description logical name of specified WebElement, used for Logging
	 *                    purposes in report
	 * @author pramod.singh
	 */
	public WebElement waitForVisibility(By by, int maxWaitTimeInSecond, String description) {
		Long waitTime = Long.valueOf(maxWaitTimeInSecond);
		return Element.waitForVisibility(configInstance, by, description, waitTime);
	}

	/**
	 * overloaded method - Pause the execution for given less than one seconds
	 * 
	 * @param seconds
	 * @author pramod.singh
	 */
	public static void wait(Config configInstance, double seconds) {
		int milliseconds = (int) (seconds * 1000);
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			configInstance.logException(e);
		}
	}

	/**
	 * Wait for visibility of element
	 * 
	 * @param configInstance
	 * @param how
	 * @param what
	 * @param description
	 * @author pramod.singh
	 */
	public WebElement waitForVisibility(How how, String what, String description) {
		return Element.waitForVisibility(configInstance, how, what, description);
	}

	/**
	 * Wait for visibility of element
	 * 
	 * @param configInstance
	 * @param how
	 * @param what
	 * @param description
	 * @author pramod.singh
	 */
	public boolean waitForElementToLoad(By by, String description) {
		return Element.waitForElementToLoad(configInstance, by, description);
	}

	/**
	 * Wait for visibility of element
	 * 
	 * @param configInstance
	 * @param how
	 * @param what
	 * @param description
	 * @author pramod.singh
	 */
	public boolean waitForElementToLoad(How how, String what, String description, int objectWaitTime) {
		return Element.waitForElementToLoad(configInstance, how, what, description, objectWaitTime);
	}

	/**
	 * Wait for visibility of element
	 * 
	 * @param configInstance
	 * @param how
	 * @param what
	 * @param description
	 * @author pramod.singh
	 */
	public boolean waitForElementToLoad(How how, String what, String description) {
		return Element.waitForElementToLoad(configInstance, how, what, description);
	}

	/**
	 * Wait for visibility of element
	 * 
	 * @param configInstance
	 * @param how
	 * @param what
	 * @param description
	 * @author pramod.singh
	 */
	public boolean waitForElementToLoad(By by, int maxWaitTime, String description) {
		return Element.waitForElementToLoad(configInstance, by, maxWaitTime, description);
	}

	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element     element to be searched
	 * @param description logical name of specified WebElement, used for Logging
	 *                    purposes in report
	 * @author pramod.singh
	 */
	public WebElement waitForVisibility(By by, String description) {
		return Element.waitForVisibility(configInstance, by, description);
	}

	/**
	 * 
	 * @param testConfig
	 * @param locator
	 * @param description
	 * @return
	 */
	public WebElement fluentWaitForVisibility(By locator, String description, int... timeinsecons) {
		WebElement returnElement = null;
		int ObjectWaitTime;
		if (timeinsecons.length == 0) {
			ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
		} else {
			ObjectWaitTime = timeinsecons[0];
		}
		configInstance.logComment("Wait for element '" + description + "' to be visible on the page.");
		Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.driver)
				.withTimeout(Duration.ofSeconds(ObjectWaitTime)).pollingEvery(Duration.ofSeconds(2))
				.ignoring(NoSuchElementException.class);

		try {
			returnElement = fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (Exception e) {
			loggerUtils.logExceptionAndSkipFailure(description, e, true);
			returnElement = null;
		}
		return returnElement;
	}

	/**
	 * Fluent Wait till Element is clickable
	 * 
	 * @param configInstance
	 * @param by
	 * @param maxWaitTimeInSecond
	 * @param description
	 * @return --> WebElement
	 * @author pramod.singh
	 */
	public WebElement fluentWaitForElementToBeClickable(By by, String description, int... maxWaitTimeInSecond) {
		WebElement element = null;
		int ObjectWaitTime;
		
		if (maxWaitTimeInSecond.length == 0) {
			ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
		} else {
			ObjectWaitTime = maxWaitTimeInSecond[0];
		}
		configInstance.logComment("Wait for element '" + description + "' to be clickable on the page.");
		Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.driver)
				.withTimeout(Duration.ofSeconds(ObjectWaitTime)).pollingEvery(Duration.ofSeconds(2))
				.ignoring(NoSuchElementException.class);

		try {
			element = fluentWait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			loggerUtils.logExceptionAndSkipFailure(description, e, true);
			element = null;
		}
		return element;

	}
	
	public void waitForPopup(int pollTime) {
		// Time to poll for every 5 seconds whether popup is present or not
				int threshold = 5;

				for (int i = 0; i < pollTime; i++)
				{

					// Time to poll for every 5 seconds whether popup is present or not
					if (popupUtils.isAlertPresent())
					{
						popupUtils.ok();
						loggerUtils.logComment("Alert closed successfully");
						break;
					}
					wait(threshold);
				}
		//Browser.waitForPopUp(configInstance, timeInSeconds);
	}
	
	public void waitForUrlToDisplay(String expectedUrl, int timeInSeconds) {
		int count = 0;
		while(!configInstance.driver.getCurrentUrl().equals(expectedUrl) && count < timeInSeconds)
		{
			count +=1;
		}
	}

}
