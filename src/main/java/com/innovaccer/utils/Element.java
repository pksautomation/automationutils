package com.innovaccer.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.innovaccer.utils.Element.How;
import com.innovaccer.utils.v2.WaitHelper;

/**
 * 
 * @author pramod.singh
 *
 */
public class Element
{

	/**
	 * Locator technique
	 */
	public static enum How
	{
		className, css, id, linkText, name, partialLinkText, tagName, xPath
	};

	public static enum IFrameHow {
		className, css, name, id
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be checked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void check(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Check '" + description + "'");
		if (!element.isSelected())
		{
			try
			{
				clickWithoutLog(testConfig, element);
				Browser.wait(testConfig, 1);
			}
			catch (StaleElementReferenceException e)
			{
				testConfig.logComment("Stale element reference exception. Trying again...");
				clickWithoutLog(testConfig, element);
			}
			
		}
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be cleared
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void clear(Config testConfig, WebElement element, String description)
	{
		
		element.clear();
		testConfig.logComment("Clear data of '" + description + "'");
		
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be clicked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void click(Config testConfig, WebElement element, String description)
	{	
		
		Actions builder = new Actions(testConfig.driver);
		builder.moveToElement(element).click().build().perform();
		Browser.wait(testConfig, 1);
		Browser.waitForJStoLoad(testConfig);
		testConfig.logComment("Clicked on " + description);
	}
	
	/**
	 * Clicks on element using JavaScript
	 * 
	 * @param testConfig
	 *            For logging
	 * @param elementToBeClicked
	 *            - Element to be clicked
	 * @param description
	 *            For logging
	 * @author i0465
	 */
	public static void clickThroughJS(Config testConfig, WebElement elementToBeClicked, String description)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		
		js.executeScript("arguments[0].click();", elementToBeClicked);
		testConfig.logComment("Clicked on " + description);
		
	}
	/**
	 * Clear on element using JavaScript
	 * @param testConfig
	 * @param elementToBeClicked
	 * @param description
	 * @author ranjeet
	 */
	public static void clearThroughJS(Config testConfig, WebElement element, String description)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("arguments[0].value ='';", element);
		testConfig.logComment("Cleared on " + description);	
	}
	
	
	/**
	 * Enter Data on element using JavaScript
	 * 
	 * @param testConfig
	 * @param element
	 * @param value
	 * @param description
	 * @author ranjeet
	 */
	public static void enterDataThroughJS(Config testConfig, WebElement element, String value, String description) {
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		// encode the html characters so that they get printed correctly
		String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" },new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
		js.executeScript("arguments[0].value='"+value+"';", element);
		testConfig.logComment("Enter the " + description + " as '" + message + "'");
	}
	
	/**
	 * @param Config
	 *            test config instance for the driver
	 * @param element
	 *            WebElement to be double clicked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *   @author i0465
	 */
	public static void doubleClick(Config testConfig, WebElement element, String description)
	{
		Actions action = new Actions(testConfig.driver);
		action.doubleClick(element).perform();
		testConfig.logComment("Successfully Double Click on '" + description + "'");
	}
	
	/**
	 * Enters the given 'value'in the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *           @author i0465
	 */
	public static void enterData(Config testConfig, WebElement element, String value, String description)
	{
		// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			element.clear();
			Browser.wait(testConfig, 1);
			element.sendKeys(value);
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
	}
	
	/**
	 * Enters the given 'value'in the specified WebElement after clicking on it
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *            @author i0465
	 */
	public static void enterDataAfterClick(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
			clickWithoutLog(testConfig, element);
			element.clear();
			Browser.wait(testConfig, 1);
			element.sendKeys(value);
			
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	
	/**
	 * Enters the given 'value'in the specified WebElement without clear
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *            @author i0465
	 */
		public static void enterDataWithoutClear(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
			element.sendKeys(value);
			
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	
	/**
	 * Enters the given 'value'in the specified File name WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *            @author i0465
	 */
	public static void enterFileName(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			
			testConfig.logComment("Enter the " + description + " as '" + value + "'");
			element.sendKeys(value);
			
		}
		else
		{
			testConfig.logComment("Skipped file entry for " + description);
		}
	}
	/**
	 * 
	 * @param testConfig
	 * @param how
	 * @param what
	 * @author i0465
	 * @return
	 */
	private static WebElement findiFrameElement(Config testConfig, How how, String what)
	{
		List<WebElement> frames = getiFramesOnPage(testConfig.driver);
		if (frames.isEmpty())
			return null;
		WebElement element = null;
		
		for (WebElement fr : frames)
		{
			if (element != null)
			{
				return element;
			}
			
			try
			{
				testConfig.driver.switchTo().frame(fr);
			}
			catch (StaleElementReferenceException e)
			{
				testConfig.logComment("Stale element reference exception. Trying again...");
				testConfig.driver.switchTo().defaultContent();
				try
				{
					testConfig.driver.switchTo().frame(fr);
				}
				catch (StaleElementReferenceException ex)
				{
					testConfig.logWarning(ex.toString());
				}
			}
			
			element = getPageElement(testConfig, how, what);
			
			if (element == null)
			{
				element = findiFrameElement(testConfig, how, what);
			}
		}
		
		return element;
	}
	
	
	/**
	 * Gets all the available string options in the Select Element
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Select WebElement
	 * @return String list of options
	 * @author i0465
	 */
	public static List<String> getAllOptionsInSelect(Config testConfig, WebElement element)
	{
		Select sel = new Select(element);
		List<WebElement> elements = sel.getOptions();
		List<String> options = new ArrayList<String>(elements.size());
		
		for (WebElement e : elements)
		{
			options.add(e.getText());
		}
		testConfig.logComment("Retrieve all the Options present for this specified Select WebElement");
		return options;
	}
	
	/**
	 * Retrieve all the values(atribute=value) present for this specified Select WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Select WebElement
	 * @author i0465
	 * @return String list of options
	 */
	public static List<String> getAllValuesInSelect(Config testConfig, WebElement element)
	{
		testConfig.logComment("Retrieve all the values(atribute=value) present for this specified Select WebElement");
		Select sel = new Select(element);
		List<WebElement> elements = sel.getOptions();
		List<String> options = new ArrayList<String>(elements.size());
		
		for (WebElement e : elements)
		{
			options.add(e.getAttribute("value"));
		}
		return options;
	}
	
	/**
	 * Gets all the selected options in the Select Element
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Select WebElement
	 * @return String list of options
	 * @author i0465
	 */
	public static List<String> getAllSelectedOptions(Config testConfig, WebElement element)
	{
		Select sel = new Select(element);
		
		List<WebElement> elements = sel.getAllSelectedOptions();
		List<String> options = new ArrayList<String>(elements.size());
		
		for (WebElement e : elements)
		{
			options.add(e.getText());
		}
		testConfig.logComment("Retrieve all the Options selected for this specified Select WebElement");
		return options;
	}
	
	/**
	 * Get the first selected option in this select webelement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement whose first selected value is to be read
	 * @author i0465
	 * @return
	 */
	public static WebElement getFirstSelectedOption(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Get the first selected value for " + description);
		try
		{
			
			Select sel = new Select(element);
			return sel.getFirstSelectedOption();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			Select sel = new Select(element);
			return sel.getFirstSelectedOption();
		}
		
	}
	
	/**
	 * Returns the How locator used to find the specified Webelement
	 * 
	 * @param element
	 * @return String representation of locator
	 */
	public static String getIdentifier(WebElement element)
	{
		String elementStr = element.toString();
		return "[" + elementStr.substring(elementStr.indexOf("->") + 3);
	}
	
	/**
	 * Gets the WebElement using the specified locator technique in the frames
	 * present on the passed page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return found WebElement
	 */
	public static WebElement getiFrameElement(Config testConfig, How how, String what)
	{
		getOutOfFrame(testConfig);
		return findiFrameElement(testConfig, how, what);
	}
	
	private static List<WebElement> getiFramesOnPage(WebDriver driver)
	{
		// List<WebElement> frames = driver.findElements(By.tagName("frame"));
		List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
		// frames.addAll(iframes);
		return iframes;
	}
	
	public static WebElement getLastElementInCollection(Config testConfig, How how, String strDefinition)
	{
		List<WebElement> webElements = getListOfElements(testConfig, how, strDefinition);
		return webElements.get(webElements.size() - 1);
	}
	
	/**
	 * Gets the list of WebElements using the specified locator technique on the
	 * passed driver page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @author i0465
	 * @return List of WebElements Found
	 */
	public static List<WebElement> getListOfElements(Config testConfig, How how, String what)
	{
		testConfig.logComment("Get the List of WebElements with " + how + ":" + what);
		try
		{
			switch (how)
			{
				case className:
					return testConfig.driver.findElements(By.className(what));
				case css:
					return testConfig.driver.findElements(By.cssSelector(what));
				case id:
					return testConfig.driver.findElements(By.id(what));
				case linkText:
					return testConfig.driver.findElements(By.linkText(what));
				case name:
					return testConfig.driver.findElements(By.name(what));
				case partialLinkText:
					return testConfig.driver.findElements(By.partialLinkText(what));
				case tagName:
					return testConfig.driver.findElements(By.tagName(what));
				case xPath:
					return testConfig.driver.findElements(By.xpath(what));
				default:
					return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			// retry
			return getListOfElements(testConfig, how, what);
		}
		catch (Exception e)
		{
			testConfig.logWarning("Could not find the list of the elements on page");
			return null;
		}
	}
	
	/**
	 * Getting out of frame
	 */
	public static void getOutOfFrame(Config testConfig)
	{
		testConfig.driver.switchTo().defaultContent();
	}
	
	/**
	 * Gets the WebElement using the specified locator technique on the passed
	 * driver page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @param isTestCaseFailedIfNoSuchExcetion
	 *             ---> true : If NoSuchElement exception is thrown then test case will be failed immediately 
	 *             ---> false : If NoSuchElement exception is thrown then test case will never failed
	 * @author i0465
	 * @return found WebElement
	 */
	public static WebElement getPageElement(Config testConfig, How how, String what,Boolean isTestCaseFailedIfNoSuchExcetion)
	{
		if(!(testConfig.getRunTimeProperty("disableGetPageElementLogs")!=null && testConfig.getRunTimeProperty("disableGetPageElementLogs").equalsIgnoreCase("true")))
		{
			testConfig.logComment("Get the WebElement with " + how + ":" + what);
		}
		
		what = Helper.replaceArgumentsWithRunTimeProperties(testConfig, what);
		
		try
		{
			switch (how)
			{
				case className:
					return testConfig.driver.findElement(By.className(what));
				case css:
					return testConfig.driver.findElement(By.cssSelector(what));
				case id:
					return testConfig.driver.findElement(By.id(what));
				case linkText:
					return testConfig.driver.findElement(By.linkText(what));
				case name:
					return testConfig.driver.findElement(By.name(what));
				case partialLinkText:
					return testConfig.driver.findElement(By.partialLinkText(what));
				case tagName:
					return testConfig.driver.findElement(By.tagName(what));
				case xPath:
					return testConfig.driver.findElement(By.xpath(what));
				default:
					return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			// retry
			Browser.wait(testConfig, 3);
			testConfig.logComment("Retrying getting element" + how + ":" + what);
			return getPageElement(testConfig, how, what);
		}
		catch (NoSuchElementException e)
		{
			if(isTestCaseFailedIfNoSuchExcetion)
				testConfig.logWarning("Could not find the element on page", true);
			else
				testConfig.logWarning("Could not find the element on page");
			return null;
		}
		
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param how
	 *            locator strategy to find element
	 * @param what
	 *            element locator
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @return
	 * @author i0465
	 */
	public static String getText(Config testConfig, How how, String what, String description)
	{
		testConfig.logComment("Get text of '" + description + "'");
		String text = null;
		try
		{
			WebElement elm = Element.getPageElement(testConfig, how, what);
			text = Element.getText(testConfig, elm, description);
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			WebElement elm = Element.getPageElement(testConfig, how, what);
			text = Element.getText(testConfig, elm, description);
			
		}
		return text;
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement whose text is needed
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static String getText(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Get text of '" + description + "'");
		String text = null;
		try
		{
			text = element.getText();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			
			text = element.getText();
			
		}
		
		return text;
	}
	
	public static Boolean isElementDeleted(Config testConfig, How how, String what)
	{
		Boolean isDeleted = false;
		testConfig.logComment("Get the WebElement with " + how + ":" + what);
		what = Helper.replaceArgumentsWithRunTimeProperties(testConfig, what);
		WebElement element = null;
		try
		{
			switch (how)
			{
				case className:
					element = testConfig.driver.findElement(By.className(what));
					break;
				case css:
					element = testConfig.driver.findElement(By.cssSelector(what));
					break;
				case id:
					element = testConfig.driver.findElement(By.id(what));
					break;
				case linkText:
					element = testConfig.driver.findElement(By.linkText(what));
					break;
				case name:
					element = testConfig.driver.findElement(By.name(what));
					break;
				case partialLinkText:
					element = testConfig.driver.findElement(By.partialLinkText(what));
					break;
				case tagName:
					element = testConfig.driver.findElement(By.tagName(what));
					break;
				case xPath:
					element = testConfig.driver.findElement(By.xpath(what));
					break;
				default:
					testConfig.logFail("Invalid strategy to locate element");
			}
			if (element == null)
				testConfig.logFail("Failed to find element");
		}
		catch (NoSuchElementException e)
		{
			isDeleted = true;
		}
		
		return isDeleted;
	}
	
	/**
	 * Verify is webelement is enable or not
	 * @param testConfig
	 * @param element
	 * @author i0465
	 * @return
	 */
	public static Boolean IsElementEnabled(Config testConfig, WebElement element)
	{
		Boolean visible = true;
		if (element == null)
			return false;
		try
		{
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isEnabled();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isDisplayed();
			
		}
		catch (NoSuchElementException e)
		{
			visible = false;
		}
		catch (ElementNotVisibleException e)
		{
			visible = false;
		}
		
		finally
		{
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			testConfig.driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return visible;
	}
	
	/**
	 * Presses the given Key in the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param Key
	 *            key to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void KeyPress(Config testConfig, WebElement element, Keys key, String description)
	{
		testConfig.logComment("Press the key '" + key.toString() + "' on " + description + "");
		element.sendKeys(key);
		
	}
	
	
	/**
	 * Method used to scroll up and down horizontally in browser
	 * 
	 * @param testConfig
	 * @param from
	 * @param to
	 * @author i0465
	 */
	public static void pageScroll(Config testConfig, String from, String to)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("window.scrollBy(" + from + "," + to + ")");
	}
	
	/**
	 * Selects the given 'value' attribute for the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to select
	 * @param value
	 *            value to the selected
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void selectValue(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			testConfig.logComment("Select the " + description + " dropdown value '" + value + "'");
			
			Select sel = new Select(element);
			sel.selectByValue(value);
			
		}
		else
		{
			testConfig.logComment("Skipped value selection for " + description);
		}
	}
	
	/**
	 * Selects the given visible text 'value' for the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to select
	 * @param value
	 *            visible text value to the selected
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void selectVisibleText(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			testConfig.logComment("Select the " + description + " dropdown text '" + value + "'");
			
			Select sel = new Select(element);
			sel.selectByVisibleText(value);
			
			try
			{
				sel = new Select(element);
				element.click();
				sel.selectByVisibleText(value);
			}
			catch(Exception e){}
		}
		else
		{
			testConfig.logComment("Skipped text selection for " + description);
		}
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be submitted
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void submit(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Submit '" + description + "'");
		element.submit();
		
	}
	

	
	/**
	 * Wait for element to be stale on the page
	 * 
	 * @param Config
	 *            test config instance for the driver instance on which element
	 *            is to be searched
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void waitForStaleness(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be stable on the page.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
		try
		{
			wait.until(ExpectedConditions.stalenessOf(element));
		}
		catch (org.openqa.selenium.TimeoutException tm)
		{
			throw new TimeoutException("Waited for element " + description + " to get stale for " + ObjectWaitTime + " seconds");
		}
	}
	
	/**
	 * Click without logging
	 * @param testConfig
	 * @param element
	 * @author i0465
	 */
	private static void clickWithoutLog(Config testConfig, WebElement element)
	{
		try
		{
			JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
			jse.executeScript("arguments[0].scrollIntoView(false)", element);
			element.click();
		}
		catch(WebDriverException wde)
		{
			element.click();
		}
	}
	
	/**
	 * Get attribute value
	 * @param testConfig
	 * @param element
	 * @param attributeName
	 * @param comment
	 * @return attributeValue
	 * @author i0465
	 */
	public static String getAttribute(Config testConfig, WebElement element, String attributeName, String comment)
	{
		testConfig.logComment("Getting value of attribute '" + attributeName + "' for :" + comment);
		String value = "";
		try
		{
			value = element.getAttribute(attributeName);
		}
		catch(Exception wde)
		{
			testConfig.logComment("Exception occurred in fetching value of attribute '" + attributeName + "' for :" + comment + " : " + wde.getMessage());
		}
		
		return value;
	}
	
	/**
	 * Get css value
	 * @param testConfig
	 * @param element
	 * @param css
	 * @param comment
	 * @return cssValue
	 * @author i0465
	 */
	public static String getCSSValue(Config testConfig, WebElement element, String css, String comment)
	{
		testConfig.logComment("Getting value of CSS '" + css + "' for :" + comment);
		String value = "";
		try
		{
			value = element.getCssValue(css);
		}
		catch(Exception wde)
		{
			testConfig.logComment("Exception occurred in fetching value of css '" + css + "' for :" + comment + " : " + wde.getMessage());
		}
		
		return value;
	}
	
	/**
	 * Select by value in radio group
	 * @param testConfig
	 * @param webElements - List of elements
	 * @param value - Value to select
	 * @param comment - Comments
	 * @author i0465
	 */
	public static void selectByValueInRadioGroup(Config testConfig, List<WebElement> webElements, String value, String comment)
	{
		testConfig.logComment("Selecting value '" + value + "' in radio group :" + comment);
		String radioValue = null;
		boolean valueFound = false;
		try
		{
			for(WebElement element : webElements)
			{
				radioValue = element.getAttribute("value");
				if(value.equals(radioValue))
				{
					element.click();
					valueFound = true;
					break;
				}
			}
			
			if(!valueFound)
				testConfig.logFail("Value " + value + " could not found in radio group " + comment);
		}
		catch(Exception wde)
		{
			testConfig.logComment("Exception occurred in selecting value '" + value + "' for :" + comment + " : " + wde.getMessage());
		}
	}
	
	/**
	 * Execute javascript on given elements
	 * @param testConfig
	 * @param javaScriptToExecute
	 * @param elemen
	 * @author i0465
	 * @return result
	 */
	public static Object executeJavaScript(Config testConfig, String javaScriptToExecute, Object...element)
	{
		testConfig.logComment("Execute javascript:-" + javaScriptToExecute);
		JavascriptExecutor javaScript = (JavascriptExecutor) testConfig.driver;
		return javaScript.executeScript(javaScriptToExecute, element);
	}
	
	/**
	 * Verify Element is Not Enabled
	 * @param testConfig
	 * @param element
	 * @param description
	 * @author i0465
	 */
	public static void verifyElementNotEnabled(Config testConfig, WebElement element, String description)
	{
		try
		{
			if (!IsElementEnabled(testConfig, element))
			{
				testConfig.logPass("Verified the disable of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is enabled on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			if (!IsElementEnabled(testConfig, element))
			{
				testConfig.logPass("Verified the disable of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is enabled on the page");
			}
		}
	}
	
	/**
	 * Verify Element is Enabled
	 * @param testConfig
	 * @param element
	 * @param description
	 * @author i0465
	 */
	public static void verifyElementEnabled(Config testConfig, WebElement element, String description)
	{
		try
		{
			if (IsElementEnabled(testConfig, element))
			{
				testConfig.logPass("Verified the enable of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is disabled on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			if (IsElementEnabled(testConfig, element))
			{
				testConfig.logPass("Verified the enable of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is disabled on the page");
			}
		}
	}
	
	/**
	 * This function is used to scroll an element into view
	 * @param element
	 * @param testConfig
	 * @author i0465
	 */
	public static void scrollToView(Config testConfig, WebElement element)
	{
		JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
		jse.executeScript("arguments[0].scrollIntoView(false)", element);
		
	}
	/**
	 * Wait for invisibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 */
	public static void waitForInvisibility(Config testConfig, How how, String what, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be invisible on the page.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(testConfig.driver)
		        .withTimeout(Duration.ofSeconds(ObjectWaitTime))
		        .pollingEvery(Duration.ofSeconds(5));
		
		By by = null;
		switch (how)
		{
			case className:
				by = By.className(what);
				break;
			case css:
				by = By.cssSelector(what);
				break;
			case id:
				by = By.id(what);
				break;
			case linkText:
				by = By.linkText(what);
				break;
			case name:
				by = By.name(what);
				break;
			case partialLinkText:
				by = By.partialLinkText(what);
				break;
			case tagName:
				by = By.tagName(what);
				break;
			case xPath:
				by = By.xpath(what);
				break;
			default:
				testConfig.logFail("Invalid identification method is passed");
		}
		
		try
		{
			fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
		}
		catch (TimeoutException tm)
		{
			throw new TimeoutException(description + " found after waiting for " + ObjectWaitTime + " seconds");
		}

	}
	/**
	 * This method is used to move cursor from one web element to another element
	 * @param testConfig
	 * @param source
	 * @param destination
	 */
	public static void moveCursorfromSourceToDestination(Config testConfig,WebElement source,WebElement destination)
	{
		Actions actions = new Actions(testConfig.driver);
		actions.moveToElement(source);
		actions.moveToElement(destination);
		actions.click().perform();
	}
	
	/**
	 * Gets the WebElement using the specified locator technique on the passed
	 * driver page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return found WebElement
	 */
	public static WebElement getPageElement(Config testConfig, How how, String what){
		return Element.getPageElement(testConfig, how, what,true);
	}

	/**
	 * Function to get i frame by name
	 *
	 * @param testConfig test config object
	 * @param how        IFrameHow value
	 * @param name       name of Iframe element
	 * @return
	 */
	public static void findIFrameElementByName(Config testConfig, IFrameHow how, String name) {
		List<WebElement> frames = getiFramesOnPage(testConfig.driver);
		if (frames.isEmpty())
			return;
		WebElement element = null;

		try {
			testConfig.driver.switchTo().frame(name);
		} catch (StaleElementReferenceException e) {
			testConfig.logComment("Stale element reference found.");
			testConfig.driver.switchTo().defaultContent();
			try {
				testConfig.driver.switchTo().frame(name);
			} catch (StaleElementReferenceException ex) {
				testConfig.logWarning(ex.toString());
			}
		}
	}
	

	/**
	 * select specific list value from bootstarp dropdown
	 *
	 * @param value the value
	 * @param element the element
	 * @return true, if successful
	 * @author I0465
	 */
	public static boolean bootStrapSelectDropDown(Config testConfig,String value, List<WebElement> elementsOfListInDropDown,WebElement dropDownElement, String ...description) {
		boolean flag = false;
		try {
		List<WebElement> list1 = elementsOfListInDropDown;
		Element.click(testConfig, dropDownElement, "DropDown");
		for (WebElement e : list1) {
			String str = e.getText().trim();
			if (str.equalsIgnoreCase(value)) {
				e.click();
				flag = true;
				if(description.length > 0)
					testConfig.logComment("Selected Drop down Value - " + value + " Of " + description[0]);
				else{
					testConfig.logComment("Selected Drop down - " + value );
				}
				break;
			}
		}	
		if(!flag || list1.size() == 0)
		{
			throw new NullPointerException(value+" Item is not availabe in list");
		}
		Browser.wait(testConfig, 2);
		}catch (Exception e) {
			testConfig.logException("Failed : To Selected Drop down - " + value, e, true);
		}
		return flag;
	}

	/**
	 * select specific list value from bootstarp dropdown
	 * 
	 * @param testConfig
	 * @param value
	 * @param elementsOfListInDropDown
	 * @param description (optional)
	 * @return true, if successful
	 * @author ranjeet
	 */
	public static boolean bootStrapSelectDropDown(Config testConfig,String value, List<WebElement> elementsOfListInDropDown, String ...description) {
		boolean flag = false;
		try {
		List<WebElement> list1 = elementsOfListInDropDown;
		for (WebElement e : list1) {
			String str = e.getText().trim();
			if (str.equalsIgnoreCase(value)) {
				e.click();
				flag = true;
				if(description.length > 0)
					testConfig.logComment("Selected Drop down Value - " + value + " Of " + description[0]);
				else{
					testConfig.logComment("Selected Drop down - " + value );
				}
				break;
			}
		}	
		if(!flag || list1.size() == 0)
		  {
			throw new NullPointerException(value+" Item is not availabe in list");
		  }
		 Browser.wait(testConfig, 2);
		}catch (Exception e) {
			testConfig.logException("Failed : To Selected Drop down - " + value, e, true);
		}
		return flag;
	}
	
	/**
	 * @param testConfig
	 * @param how         : locator method
	 * @param what        : locator value
	 * @param description : about element
	 * @author i0465
	 */
	public static void verifyElementNotPresent(Config testConfig, How how, String what, String description) {
		List<WebElement> listofelements = null;
		try {
			listofelements = getListOfElements(testConfig, how, what);
			if (listofelements.size() == 0)
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			else
				testConfig.logFail("Element '" + description + "' is present on the page");

		} catch (StaleElementReferenceException e) {
			testConfig.logComment("Stale element reference exception. Trying again...");

			if (listofelements.size() == 0)
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			else
				testConfig.logFail("Element '" + description + "' is present on the page");
		}
	}
	
	/**
	 * @param testConfig
	 * @param how         : locator method
	 * @param what        : locator value
	 * @param description : about element
	 */
	public static boolean isElementPresent(Config testConfig, How how, String what, String description) {
		List<WebElement> listofelements = null;
		try {
			listofelements = getListOfElements(testConfig, how, what);
			if (listofelements.size() != 0) {
				testConfig.logComment("Element "+description+" is present.");
				return true;
			}
			else {
				testConfig.logComment("Element "+description+" is not present.");
				return false;
			}	
		} catch (StaleElementReferenceException e) {
			testConfig.logComment("Stale element reference exception. Trying again...");
			if (listofelements.size() != 0) {
				testConfig.logComment("Element "+description+" is present.");
				return true;
			}
			else {
				testConfig.logComment("Element "+description+" is not present.");
				return false;
			}	
		}
	}
	/**
	 * Upload file in on browser using robot class of java
	 * Note: This function will work on windows machine only
	 * @param testConfig
	 * @param element
	 * @return
	 * @author I0465
	 */
	public static boolean uploadFileUsingRobot(Config testConfig,WebElement element,String filePath,String description) {
		boolean flag=true;
		Browser.wait(testConfig, 2);
		Element.click(testConfig, element,description);
		StringSelection stringSelection = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		Robot robot;
		try {
			Robot robo = new Robot();
			Browser.wait(testConfig, 2);
			robo.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
			robo.keyPress(java.awt.event.KeyEvent.VK_V);
			robo.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
			robo.keyRelease(java.awt.event.KeyEvent.VK_V);
			robo.keyPress(java.awt.event.KeyEvent.VK_ENTER);
			robo.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			flag=false;
		}
		Browser.wait(testConfig, 2);
		return flag;
	}
	
	/**
	 * Mouse hove on given web element
	 * @param testConfig
	 * @param element
	 */
	public static void  mousehoverOnElement(Config testConfig,WebElement element) {
		Element.mousehoverOnElement(testConfig,element,"");
	}
	
	/**
	 * Mouse hove on given web element
	 * @param testConfig
	 * @param element
	 * @author i0465
	 */
	public static void  mousehoverOnElement(Config testConfig,WebElement element,String description) {
		Actions builder = new Actions(testConfig.driver);
		builder.moveToElement(element).perform();
		Browser.wait(testConfig, 1);
	}
	
	/***
	 * 
	 * @param testConfig
	 * @param by
	 * @param maxWaitTimeInSecond
	 * @param description
	 * @author i0465
	 * @return
	 */
	public static boolean waitForElementToLoad(Config testConfig,By by,int maxWaitTimeInSecond,String description) {
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		WebElement returnElement = null;
		boolean visibilityStatus =true;
		try {
			Browser.waitForJStoLoad(testConfig);
			WebDriverWait wait = new WebDriverWait(testConfig.driver, maxWaitTimeInSecond);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			testConfig.logComment( description + " is visible now.");
		}  
		catch (Exception ex) {
			visibilityStatus=false;
			testConfig.logComment("Element is not visible");
			testConfig.logException(ex);
		}
		return visibilityStatus;
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author i0465
	 */
	public static boolean waitForElementToLoad(Config testConfig, How how, String what, String description,int objectWaitTime)
	{
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		//Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));		
		By by = null;
		boolean visibilityStatus =true;
		WebElement returnElement = null;
		switch (how)
		{
			case className:
				by = By.className(what);
				break;
			case css:
				by = By.cssSelector(what);
				break;
			case id:
				by = By.id(what);
				break;
			case linkText:
				by = By.linkText(what);
				break;
			case name:
				by = By.name(what);
				break;
			case partialLinkText:
				by = By.partialLinkText(what);
				break;
			case tagName:
				by = By.tagName(what);
				break;
			case xPath:
				by = By.xpath(what);
				break;
			default:
				testConfig.logFail("Invalid identification method is passed");
		}
		
		try
		{
			Browser.waitForJStoLoad(testConfig);
			WebDriverWait wait = new WebDriverWait(testConfig.driver, objectWaitTime);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			returnElement=testConfig.driver.findElement(by);
			if(returnElement != null) 
				testConfig.logComment("Element is visible now.");
			else
				visibilityStatus=false;
		}
		catch (Exception e)
		{
			visibilityStatus=false;
			testConfig.logWarning("Element is not visible", true);
			//testConfig.logComment("Element is not visible");
			//testConfig.logException(e);
		}
		return visibilityStatus;
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @return   null in case of Element not found other wise return WebElement
	 * @author I0465
	 */
	public static WebElement waitForVisibility(Config testConfig, By by,String description, Long ...maxwaitTime) {
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		Long ObjectWaitTime;
		if(maxwaitTime.length ==0 ) {
			ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		}
		else {
			ObjectWaitTime=maxwaitTime[0];
		}
		WebElement returnElement = null;
		try {
			Browser.waitForJStoLoad(testConfig);
			WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
			returnElement=wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			//returnElement=testConfig.driver.findElement(by);
			testConfig.logComment(description + " is visible now.");
		}  catch (Exception we) {
			returnElement = null;
			testConfig.logExceptionSkipFailure(description + " not found on page", we, true);
		}
		return returnElement;
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author i0465
	 */
	public static WebElement waitForVisibility(Config testConfig, How how, String what, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));		
		By by = null;
		WebElement returnElement = null;
		switch (how)
		{
			case className:
				by = By.className(what);
				break;
			case css:
				by = By.cssSelector(what);
				break;
			case id:
				by = By.id(what);
				break;
			case linkText:
				by = By.linkText(what);
				break;
			case name:
				by = By.name(what);
				break;
			case partialLinkText:
				by = By.partialLinkText(what);
				break;
			case tagName:
				by = By.tagName(what);
				break;
			case xPath:
				by = By.xpath(what);
				break;
			default:
				testConfig.logFail("Invalid identification method is passed");
		}
		
		try
		{
			Browser.waitForJStoLoad(testConfig);
			WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			returnElement=testConfig.driver.findElement(by);
			testConfig.logComment(description + " is visible now.");
		}
		catch (Exception e)
		{
			returnElement=null;
			testConfig.logComment(description + " not found on page");
			testConfig.logException(e);
		}
		return returnElement;
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 */
	public static boolean waitForElementToLoad(Config testConfig, How how, String what, String description)
	{
		int ObjectWaitTime = Integer.parseInt(testConfig.getRunTimeProperty("ObjectWaitTime"));
		return Element.waitForElementToLoad(testConfig, how,what,description,ObjectWaitTime);
	}

	public static void waitForElementToBeClickable(Config testConfig, String description, WebElement element, long... objectWaitTime) {
		try {
			Browser.waitForJStoLoad(testConfig);
			WebDriverWait wait;
			if (objectWaitTime.length > 0) {
				wait = new WebDriverWait(testConfig.driver, objectWaitTime[0]);
			} else {
				wait = new WebDriverWait(testConfig.driver, Integer.parseInt(testConfig.getRunTimeProperty("ObjectWaitTime")));
			}
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			testConfig.logWarning("Element is not Clickable", true);
		}
	}

	/***
	 * 
	 * @param testConfig
	 * @param by
	 * @param maxWaitTimeInSecond
	 * @param description
	 * @author i0465
	 * @return
	 */
	public static boolean waitForElementToLoad(Config testConfig,By by,String description) {
		int ObjectWaitTime = Integer.parseInt(testConfig.getRunTimeProperty("ObjectWaitTime"));
		return waitForElementToLoad(testConfig,by,ObjectWaitTime,description);
	}
	
	public static Boolean IsElementDisplayed(Config testConfig, WebElement element)
		{
			Boolean visible = true;
			if (element == null)
				return false;
			try
			{
				testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				visible = element.isDisplayed();
			}
			catch (StaleElementReferenceException e)
			{
				testConfig.logComment("Stale element reference exception. Trying again...");
				testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				visible = element.isDisplayed();
				
			}
			catch (NoSuchElementException e)
			{
				visible = false;
			}
			catch (ElementNotVisibleException e)
			{
				visible = false;
			}
			finally
			{
				Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
				testConfig.driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
			}
			return visible;
		}
	
	public static void pressEnter(Config testConfig)
		{
			Actions action = new Actions(testConfig.driver);
			action.sendKeys(Keys.ENTER).perform();
		}
	
	/**
	 * Waits for text to be present in value attribute of specified element
	 * 
	 * @param testConfig
	 * @param element
	 * @param textToBePresentInValueAttribiute
	 * @param description
	 */

	public static void waitTillElementHasValue(Config testConfig, WebElement element, String textToBePresentInValueAttribiute, String description)
		{
			testConfig.logComment("Wait for element '" + description + "' to have :-" + textToBePresentInValueAttribiute + " in value attribute");
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			
			WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
			try
			{
				wait.until(ExpectedConditions.textToBePresentInElementValue(element, textToBePresentInValueAttribiute));
			}
			catch (TimeoutException tm)
			{
				throw new TimeoutException("Waited for text:'" + textToBePresentInValueAttribiute + "' to be present as value in element:" + description + " for " + ObjectWaitTime + " seconds");
			}
		}

	/**
	 * waits for element to disappear
	 */
	public static void waitForElementToDisappear(Config testConfig, WebElement elementName) {
		try {

			for (int i = 1; i <= 50; i++) {
				if (!(IsElementDisplayed(testConfig, elementName)))
					break;
			}

		} catch (NoSuchElementException e) {
			testConfig.logComment("element is not present on page");
		}

	}
	
	/**
	 * Verifies if element is absent on the page
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            element to be verified
	 * @param description
	 *            description logical name of specified WebElement, used for
	 *            Logging purposes in report
	 * @author i0465
	 */
	public static void verifyElementNotPresent(Config testConfig, WebElement element, String description)
	{
		try
		{
			if (!IsElementDisplayed(testConfig, element))
			{
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is present on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			if (!IsElementDisplayed(testConfig, element))
			{
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			}
			
			else
			{
				testConfig.logFail("Element '" + description + "' is present on the page");
			}
		}
	}
	
	/**
	 * Verifies if element is present on the page
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            element to be verified
	 * @param description
	 *            description logical name of specified WebElement, used for
	 *            Logging purposes in report
	 * @author i0465
	 */
	public static void verifyElementPresent(Config testConfig, WebElement element, String description)
	{
		if (element.isDisplayed())
		{
			testConfig.logPass("Verified the presence of element '" + description + "' on the page");
		}
		else
		{
			testConfig.logFail("Element '" + description + "' is not present on the page");
		}
		
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param Config
	 *            test config instance for the driver instance on which element
	 *            is to be searched
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @param timeInSeconds
	 *            Polling time
	 * @author i0465
	 */
	public static void waitForVisibility(Config testConfig, WebElement element, int timeInSeconds, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		WebDriverWait wait = new WebDriverWait(testConfig.driver, timeInSeconds);
		try
		{
			wait.until(ExpectedConditions.visibilityOf(element));
		}
		catch (org.openqa.selenium.TimeoutException tm)
		{
			throw new TimeoutException(description + " not found after waiting for " + timeInSeconds + " seconds");
		}
	}

	/**
	 * Mouse hove on given web element
	 * 
	 * @param testConfig
	 * @param element
	 */
	public static void mousehoverOnElementUsingJavaScript(Config testConfig, WebElement element) {
		String strJavaScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
		((JavascriptExecutor) testConfig.driver).executeScript(strJavaScript, element);
	}
	
	
	/**
	 * Scroll window below and up using arrow key
	 * @param testConfig
	 * @param element
	 * @param isBelowScroll  --> if true then scrolling will be below other wise scrolling will be up
	 */
	public static void scrollToViewUsingActionClass(Config testConfig, WebElement element, boolean ...isBelowScroll)
	{   Actions actions = new Actions(testConfig.driver);
		actions.moveToElement(element);
		actions.click();
		if(isBelowScroll.length == 0)
			actions.perform();
		else if(isBelowScroll[0]) {
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.perform();
		}
		else {
			actions.sendKeys(Keys.ARROW_UP);
			actions.perform();
		}		
	}
	
	/**
	 * Enters the given 'value' For the password type field
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public static void enterPassword(Config testConfig, WebElement element, String value, String description)
	{
			// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach("**************", new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			element.clear();
			Browser.wait(testConfig, 1);
			element.sendKeys(value);
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
	}

	/**
	 * Enter Data on element using Selenium Actions
	 * 
	 * @param testConfig
	 * @param element
	 * @param value
	 * @param description
	 * @author Shriram
	 */
	public static void enterDataThroughActions(Config testConfig, String Value, WebElement element) {
		Element.clear(testConfig, element, "Cleared the existing value");
		Actions action = new Actions(testConfig.driver);
		for (char c : Value.toCharArray()) {
			String text = Element.getText(testConfig, element, "");
			Browser.wait(testConfig, 1);
			action.sendKeys(element, text + c).perform();
			;
		}
		Browser.wait(testConfig, 1);
	}
	
	/**
	 * 
	 * @param testConfig
	 * @param locator
	 * @param description
	 */
	public static void waitForInvisibility(Config testConfig,By locator, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be invisible on the page.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(testConfig.driver)
		        .withTimeout(Duration.ofSeconds(ObjectWaitTime))
		        .pollingEvery(Duration.ofSeconds(5));
		try
		{
			fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
		}
		catch (TimeoutException tm)
		{
			throw new TimeoutException(description + " found after waiting for " + ObjectWaitTime + " seconds");
		}
	}
}
