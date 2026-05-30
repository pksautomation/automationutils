package com.innovaccer.utils.v2;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.dataHelper.PageLocatorHelper;
import com.innovaccer.utils.v2.dataHelper.TestDataHelper;

import pojo.How;

public class BasePage extends PageLocatorHelper{
	private Config scenarioContext;
	private WaitHelper WaitUtils=null;
	private UtilityObjectManager UtilityObjectManager=null;
	private LoggerUtils LoggerUtils;
	private BrowserUtils browserUtils;
	private ElementActionsUtils Actions;
	private TestDataHelper testDataHelper;
	
	public String getTestData(String datakey) {
		return testDataHelper.getTestData(datakey);
	}

	public boolean isButtonClickable(String buttonName) {
		return Actions.isButtonClickable(buttonName);
	}

	public void selectDropDown(String id) {
		Actions.selectDropDown(id);
	}

	public boolean isButtonEnable(String buttonName) {
		return Actions.isButtonEnable(buttonName);
	}

	public AssertionUtils AssertionUtils;


	public BasePage(Config scenariosInstance) {
		init(scenariosInstance);
		}

	private void init(Config scenariosInstance) {
		this.scenarioContext=scenariosInstance;
		this.UtilityObjectManager = new UtilityObjectManager(scenariosInstance);
		LoggerUtils=new LoggerUtils(scenarioContext);
		AssertionUtils = new AssertionUtils(scenarioContext);
		Actions = new ElementActionsUtils(scenarioContext);
		testDataHelper = new TestDataHelper(scenarioContext);
		PageFactory.initElements(scenarioContext.getDriver(), this);
		scenarioContext.putRunTimeProperty("PageObjectName", this.getClass().getSimpleName());
		
	}
	
	public WebElement waitForVisibility(By by, int timeInSeconds, String description) {
		return WaitUtils.waitForVisibility(by, timeInSeconds, description);
	}

	public void waitForVisibility(WebElement element, int timeInSeconds, String description) {
		WaitUtils.waitForVisibility(element, timeInSeconds, description);
	}

	public void waitForInvisibility(By by, String description) {
		WaitUtils.waitForInvisibility(by, description);
	}

	public WebElement waitForElementToBeClickable(By by, String description, int... maxWaitTimeInSecond) {
		return WaitUtils.waitForElementToBeClickable(by, description, maxWaitTimeInSecond);
	}

	public WebElement fluentWaitForVisibility(By by, String description, int... maxWaitTimeInSecond) {
		return WaitUtils.fluentWaitForVisibility(by, description, maxWaitTimeInSecond);
	}

	public WebElement fluentWaitForElementToBeClickable(By by, String description, int... maxWaitTimeInSecond) {
		return WaitUtils.fluentWaitForElementToBeClickable(by, description, maxWaitTimeInSecond);
	}

	public void waitForUrlToDisplay(String expectedUrl, int timeInSeconds) {
		WaitUtils.waitForUrlToDisplay(expectedUrl, timeInSeconds);
	}

	public boolean waitForElementToLoad(By by, int maxWaitTimeInSecond, String description) {
		return WaitUtils.waitForElementToLoad(by, maxWaitTimeInSecond, description);
	}

	public void logPass(String message, boolean... logPageInfo) {
		LoggerUtils.logPass(message, logPageInfo);
	}

	public void logFail(String message, boolean... logPageInfo) {
		LoggerUtils.logFail(message, logPageInfo);
	}

	public void logComment(String message) {
		LoggerUtils.logComment(message);
	}

	public void logWarning(String message) {
		LoggerUtils.logWarning(message);
	}

	public void logException(String message, Throwable e, boolean... takeScreenshot) {
		LoggerUtils.logException(message, e, takeScreenshot);
	}

	public void logException(Throwable e) {
		LoggerUtils.logException(e);
	}

	public void logFailureException(Throwable exception) {
		LoggerUtils.logFailureException(exception);
	}

	public void logExceptionAndSkipFailure(String message, Throwable e, boolean... isTakeScreenShot) {
		LoggerUtils.logExceptionAndSkipFailure(message, e, isTakeScreenShot);
	}

	public void navigateToURL(String url) {
		browserUtils.navigateToURL(url);
	}

	public void openBrowser() {
		browserUtils.openBrowser();
	}

	public void quitBrowser() {
		browserUtils.quitBrowser();
	}

	public String captureScreenShoot(File destination) {
		return browserUtils.captureScreenShoot(destination);
	}

	public WebElement getEnabledButtonEle(String buttonLabel) {
		return Actions.getEnabledButtonEle(buttonLabel);
	}

	public void fillData(String key, boolean... isDesignSystemComponent) {
		Actions.fillData(key, isDesignSystemComponent);
	}

	public WebElement getClickableElement(String text) {
		return Actions.getClickableElement(text);
	}

	public WebElement getClickableButtonElement(String text) {
		return Actions.getClickableButtonElement(text);
	}

	public void clickOnButton(String id) {
		Actions.clickOnButton(id);
	}

	public boolean isElementDisplay(WebElement element) {
		return Actions.isElementDisplay(element);
	}

	public void setData(String key, String value) {
		Actions.setData(key, value);
	}

	public void doubleClick(WebElement element, String description) {
		Actions.doubleClick(element, description);
	}

	public void enterData(WebElement element, String value, String description) {
		Actions.enterData(element, value, description);
	}

	public void enterDataAfterClick(WebElement element, String value, String description) {
		Actions.enterDataAfterClick(element, value, description);
	}

	public void verifyElementEnabled(WebElement element, String description) {
		Actions.verifyElementEnabled(element, description);
	}

	public Boolean IsElementDisplayed(WebElement element) {
		return Actions.IsElementDisplayed(element);
	}

	public boolean isElementDisplayed(String id) {
		return Actions.isElementDisplayed(id);
	}

	public void fillData(String xpathkey, String testDataKey, boolean... isDesignSystemComponent) {
		Actions.fillData(xpathkey, testDataKey, isDesignSystemComponent);
	}

	public void assertFail(String message,boolean... logPageInfo) {
		AssertionUtils.assertFail(message,logPageInfo);
	}

	public void assertPass(String message,boolean... logPageInfo) {
		AssertionUtils.assertPass(message,logPageInfo);
	}

	public void assertEquals(String what, String expected, String actual, boolean... logPageInfo) {
		AssertionUtils.assertEquals(what, expected, actual, logPageInfo);
	}

	public void assertEqualsAndLogWarning(String what, String expected, String actual, boolean... logPageInfo) {
		AssertionUtils.assertEqualsAndLogWarning(what, expected, actual, logPageInfo);
	}

	public void assertTrue(String what, boolean actual, boolean... logPageInfo) {
		AssertionUtils.assertTrue(what, actual, logPageInfo);
	}

	public void compareTrue(String what, boolean actual, boolean hardAssert) {
		AssertionUtils.compareTrue(what, actual, hardAssert);
	}

	public void compareFalse(String what, boolean actual, boolean hardAssert) {
		AssertionUtils.compareFalse(what, actual, hardAssert);
	}

	public void compareEqualsWarning(String what, String expected, String actual) {
		AssertionUtils.compareEqualsWarning(what, expected, actual);
	}

	public <T> void compareEquals(String what, T expected, T actual, boolean hardAssert) {
		AssertionUtils.compareEquals(what, expected, actual, hardAssert);
	}

	public void compareContains(String what, String expected, String actual, boolean hardAssert) {
		AssertionUtils.compareContains(what, expected, actual, hardAssert);
	}

	public BasePage() {
		init(Config.getConfig());
		}
	
	
	public BrowserUtils getBrowserUtils() {
		return UtilityObjectManager.getBrowserUtils();
	}


	public void setBrowserUtils(BrowserUtils browserUtils) {
		this.browserUtils = browserUtils;
	}


	public Config getScenarioContext() {
		return this.scenarioContext;
	}

	public void setScenarioContext(Config scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	public WaitHelper getWaitUtils() {
		return UtilityObjectManager.getWaitUtils();
	}

	public void setWaitUtils(WaitHelper waitUtils) {
		WaitUtils = waitUtils;
	}

	public UtilityObjectManager getUtilityObjectManager() {
		return UtilityObjectManager;
	}

	public void setUtilityObjectManager(UtilityObjectManager utilityObjectManager) {
		UtilityObjectManager = utilityObjectManager;
	}

	public LoggerUtils getLoggerUtils() {
		return LoggerUtils;
	}

	public void setLoggerUtils(LoggerUtils loggerUtils) {
		LoggerUtils = loggerUtils;
	}

	public WebDriver getDriver() {
		return UtilityObjectManager.getConfigInstant().getDriver();
	}

	public ElementActionsUtils getActions() {
		return Actions;
	}

	public void setActions(ElementActionsUtils actions) {
		Actions = actions;
	}
	
	public AssertionUtils getAssertionUtils() {
		return AssertionUtils;
	}

	public void setAssertionUtils(AssertionUtils assertionUtils) {
		AssertionUtils = assertionUtils;
	}
	
}
