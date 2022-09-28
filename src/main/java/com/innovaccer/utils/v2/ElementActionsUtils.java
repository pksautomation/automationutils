package com.innovaccer.utils.v2;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.innovaccer.utils.v2.dataHelper.TestDataHelper;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.innovaccer.utils.Browser;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.Element;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Element.How;

/**
 *
 * @author i0465
 *
 */
public class ElementActionsUtils  {

	private Config scenarioContext;
	private WaitHelper waitHelper =null;
	private LoggerUtils LoggerUtils;
	private WebDriver driver;
	private TestDataHelper testDataHelper;

	public ElementActionsUtils(Config scenariosInstance) {
		init(scenariosInstance);
		}
	
	public ElementActionsUtils() {
		init(Config.getConfig());
	}

	private void init(Config scenariosInstance) {
		this.scenarioContext=scenariosInstance;
		waitHelper = new WaitHelper(scenarioContext);
		LoggerUtils=new LoggerUtils(scenarioContext);
		driver=scenarioContext.driver;
		testDataHelper=new TestDataHelper(scenarioContext);
		PageFactory.initElements(scenariosInstance.driver, this);
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getVisibleElement(By by) {
		return waitHelper.fluentWaitForVisibility(by, "");
		
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getVisibleElement(By by,int maxwaitTime) {
		return waitHelper.fluentWaitForVisibility(by, "",maxwaitTime);
		
	}
	
	
	/**
	 * get WebElement of Input field like file, text, textarea using Label or Placeholder
	 * @param Label
	 * @return
	 */
	public WebElement getTextField(String Label) {
		scenarioContext.driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
		WebElement element=null;
		try {
		//finding element at first parent level
		By by2=By.xpath("//*[text()='"+Label+"']/../..//input[@type!='file' and @type!='checkbox' ] | //input[@placeholder='"+Label+"']");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		
		//finding element at grand parent level
		by2=By.xpath("//*[text()='"+Label+"']/ancestor::div[2]//input[@type!='file' and @type!='checkbox' ] | //input[@placeholder='"+Label+"']");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		
		//finding element at parent of grand parent level
		by2=By.xpath("//*[text()='"+Label+"']/ancestor::div[3]//input[@type!='file' and @type!='checkbox' ] | //input[@placeholder='"+Label+"']");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		}
		catch(Exception e) {
			LoggerUtils.logException("Not found drop down button " + Label, e, true);
		}
		finally {
			Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
			driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return element;
	}

	/**
	 * Get WebElement of Button using Label
	 * @param buttonLabel
	 * @return
	 */
	public WebElement getEnabledButtonEle(String buttonLabel) {
		String buttonXpath = "//button[contains(text(),'"+buttonLabel+"')]";
		By by = By.xpath(buttonXpath);
		return waitHelper.waitForElementToBeClickable(by,buttonLabel,10);
	}

	/**
	 * Click on Dropdown button
	 * @param Label
	 * @return
	 * @author pramod.singh
	 */
	public WebElement getDropDownButton(String Label) {
		scenarioContext.driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
		WebElement element=null;
		try {
		//finding element at first parent level
		By by2=By.xpath("//*[text()='"+Label+"']/ancestor::div[1]//button ");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		
		//finding element at grand parent level
		by2=By.xpath("//*[text()='"+Label+"']/ancestor::div[2]//button ");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		
		//finding element at parent of grand parent level
		by2=By.xpath("//*[text()='"+Label+"']/ancestor::div[3]//button ");
		if((element=getVisibleElement(by2,2)) != null)
			return element;
		}
		catch(Exception e) {
			LoggerUtils.logException("Not found drop down button " + Label, e, true);
		}
		finally {
			Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
			driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return element;
	}

	public void clickOnButton(String buttonName)
	{
		click(getEnabledButtonEle(buttonName), buttonName);
	}
	
	/**
	 * Check Button enable or not
	 * @param buttonName
	 * @return
	 */
	public boolean isButtonEnable(String buttonName) {
		return getDisplayElement(buttonName)==null?false:true;
		
	}


	public void setTextField(String Label)
	{
		String data=testDataHelper.getTestData(Label);
		if(data!= null && !data.equals(""))
			enterData(getTextField(Label), data, Label);
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getClickableElement(String text) {
		String xpath = "//body//*[text()='"+text+"']";
		return waitHelper.waitForElementToBeClickable(By.xpath(xpath),"",30);
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getClickableButtonElement(String text) {
		String xpath = "//body//button[text()='"+text+"']";
		return waitHelper.waitForElementToBeClickable(By.xpath(xpath),"",30);
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getDisplayElement(String text) {
		String xpath = "//body//*[text()='"+text+"']";
		return waitHelper.waitForVisibility(By.xpath(xpath),30,"");
	}
	

	/**
	 * Select DropDown Value
	 * @param Label --> it is either lable or place holder of dropdown
	 */
	public void selectDropDown(String Label)
	{
		String data=testDataHelper.getTestData(Label);
		if( data!= null && !data.equals(""))
		{
			click(getDropDownButton(Label),Label);
			By by = By.xpath("//*[contains(@class,'Option')]//*[text()='"+data+"']");
			WebElement element = scenarioContext.driver.findElement(by);
			click(element, Label + " Dropdown option " + data);	
		}

	}
	
	public String switchToNewWindow() {
		if (driver != null)
		{
			LoggerUtils.logComment("Switching to the new window");
			String oldWindow = driver.getWindowHandle();

			if (driver.getWindowHandles().size() < 2)
			{
				LoggerUtils.logFail("No new window appeared, windows count available :-" + driver.getWindowHandles().size());
			}

			for (String winHandle : driver.getWindowHandles())
			{
				if (!winHandle.equals(oldWindow))
				{
					driver.switchTo().window(winHandle);
					LoggerUtils.logComment("Switched to window with URL:- " + driver.getCurrentUrl() + ". And title as :- " + driver.getTitle());
				}
			}

			return oldWindow;
		}
		return null;
	}


	public boolean isElementDisplay(WebElement element) {
		return IsElementDisplayed(element);
	}

	public void setData(String key, String value) {
		this.scenarioContext.putRunTimeProperty(key, value);
	}

	public WaitHelper getWait() {
		return (waitHelper == null) ? waitHelper = new WaitHelper(scenarioContext) : waitHelper;
	}

	public void check(WebElement element, String description) {
		LoggerUtils.logComment("Check '" + description + "'");
		if (!element.isSelected())
		{
			try
			{
				clickWithoutLog(element);
				waitHelper.wait(1);
			}
			catch (StaleElementReferenceException e)
			{
				LoggerUtils.logComment("Stale element reference exception. Trying again...");
				clickWithoutLog(element);
			}
			
		}
	}
	
	/**
	 * @param element
	 *            WebElement to be cleared
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void clear(WebElement element, String description)
	{
		element.clear();
		LoggerUtils.logComment("Clear data of '" + description + "'");
	}

	/**
	 * @param element
	 *            WebElement to be clicked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public void click(WebElement element, String description)
	{	
		Actions builder = new Actions(driver);
		builder.moveToElement(element).click().build().perform();
		waitHelper.wait(1);
		waitHelper.waitForJStoLoad();
		LoggerUtils.logComment("Clicked on " + description);
	}
	
	/**
	 * Clicks on element using JavaScript
	 * @param elementToBeClicked
	 *            - Element to be clicked
	 * @param description
	 *            For logging
	 */
	public void clickThroughJS(WebElement elementToBeClicked, String description) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", elementToBeClicked);
		LoggerUtils.logComment("Clicked on " + description);
	}
	
	/**
	 * Clear on element using JavaScript
	 * @param elementToBeClicked
	 * @param description
	 */
	public void clearThroughJS(WebElement element, String description)
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].value ='';", element);
		LoggerUtils.logComment("Cleared on " + description);	
	}
	
	/**
	 * Enter Data on element using JavaScript
	 * @param element
	 * @param value
	 * @param description
	 */
	public void enterDataThroughJS(WebElement element, String value, String description) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" },new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
		js.executeScript("arguments[0].value='"+value+"';", element);
		LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
	}
	
	/**
	* @param element
	 *            WebElement to be double clicked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *   @author i0465
	 */
	public void doubleClick(WebElement element, String description)
	{
		Actions action = new Actions(driver);
		action.doubleClick(element).perform();
		LoggerUtils.logComment("Successfully Double Click on '" + description + "'");
	}

	/**
	 * Enters the given 'value'in the specified WebElement
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *           @author i0465
	 */
	public void enterData(WebElement element, String value, String description)
	{
		String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
		element.clear();
		waitHelper.wait(1);
		element.sendKeys(value);
		LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
	}
	
	/**
	 * Enters the given 'value'in the specified WebElement after clicking on it
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void enterDataAfterClick(WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
			clickWithoutLog(element);
			element.clear();
			waitHelper.wait(1);
			element.sendKeys(value);
			
		}
		else
		{
			LoggerUtils.logComment("Skipped data entry for " + description);
		}
	}
	
	/**
	 * Click without logging
	 * @param element
	 */
	private void clickWithoutLog(WebElement element)
	{
		try
		{
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			jse.executeScript("arguments[0].scrollIntoView(false)", element);
			element.click();
		}
		catch(WebDriverException wde)
		{
			element.click();
		}
	}
	
	/**
	 * Enters the given 'value'in the specified WebElement without clear
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void enterDataWithoutClear(WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			// encode the html characters so that they get printed correctly
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
			element.sendKeys(value);
			
		}
		else
		{
			LoggerUtils.logComment("Skipped data entry for " + description);
		}
	}

	/**
	 * Enters the given 'value'in the specified File name WebElement
	 
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void enterFileName(WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			
			LoggerUtils.logComment("Enter the " + description + " as '" + value + "'");
			element.sendKeys(value);
			
		}
		else
		{
			LoggerUtils.logComment("Skipped file entry for " + description);
		}
	}

	/**
	 * Gets all the available string options in the Select Element
	 * @param element
	 *            Select WebElement
	 * @return String list of options
	 * @author i0465
	 */
	public List<String> getAllOptionsInSelect(WebElement element)
	{
		Select sel = new Select(element);
		List<WebElement> elements = sel.getOptions();
		List<String> options = new ArrayList<String>(elements.size());
		
		for (WebElement e : elements)
		{
			options.add(e.getText());
		}
		LoggerUtils.logComment("Retrieve all the Options present for this specified Select WebElement");
		return options;
	}

	/**
	 * Gets the WebElement using the specified locator technique in the frames
	 * present on the passed page
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return found WebElement
	 */
	public WebElement getiFrameElement(Config testConfig, How how, String what)
	{
		getOutOfFrame(testConfig);
		return findiFrameElement(how, what);
	}
	
	/**
	 * 
	 * @param how
	 * @param what
	 * @return WebElement
	 */
	private  WebElement findiFrameElement(How how, String what)
	{
		List<WebElement> frames = getiFramesOnPage(driver);
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
				driver.switchTo().frame(fr);
			}
			catch (StaleElementReferenceException e)
			{
				LoggerUtils.logComment("Stale element reference exception. Trying again...");
				driver.switchTo().defaultContent();
				try
				{
					driver.switchTo().frame(fr);
				}
				catch (StaleElementReferenceException ex)
				{
					LoggerUtils.logWarning(ex.toString());
				}
			}
			
			element = getPageElement(how, what);
			
			if (element == null)
			{
				element = findiFrameElement(how, what);
			}
		}
		
		return element;
	}
	
	private List<WebElement> getiFramesOnPage(WebDriver driver)
	{
		List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
		return iframes;
	}
	
	/**
	 * Gets the list of WebElements using the specified locator technique on the
	 * passed driver page
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return List of WebElements Found
	 */
	public List<WebElement> getListOfElements(How how, String what)
	{
		LoggerUtils.logComment("Get the List of WebElements with " + how + ":" + what);
		try
		{
			switch (how)
			{
				case className:
					return driver.findElements(By.className(what));
				case css:
					return driver.findElements(By.cssSelector(what));
				case id:
					return driver.findElements(By.id(what));
				case linkText:
					return driver.findElements(By.linkText(what));
				case name:
					return driver.findElements(By.name(what));
				case partialLinkText:
					return driver.findElements(By.partialLinkText(what));
				case tagName:
					return driver.findElements(By.tagName(what));
				case xPath:
					return driver.findElements(By.xpath(what));
				default:
					return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			return getListOfElements(how, what);
		}
		catch (Exception e)
		{
			LoggerUtils.logWarning("Could not find the list of the elements on page");
			return null;
		}
	}

	public void getOutOfFrame(Config scenariosInstance) {
		driver.switchTo().defaultContent();
	}

	/**
	 * Gets the WebElement using the specified locator technique on the passed
	 * driver page
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @param isTestCaseFailedIfNoSuchExcetion
	 *             ---> true : If NoSuchElement exception is thrown then test case will be failed immediately 
	 *             ---> false : If NoSuchElement exception is thrown then test case will never failed
	 * @return found WebElement
	 */
	public WebElement getPageElement(How how, String what,Boolean isTestCaseFailedIfNoSuchExcetion)
	{
		if(!(scenarioContext.getRunTimeProperty("disableGetPageElementLogs")!=null && scenarioContext.getRunTimeProperty("disableGetPageElementLogs").equalsIgnoreCase("true")))
		{
			LoggerUtils.logComment("Get the WebElement with " + how + ":" + what);
		}
		
		what = Helper.replaceArgumentsWithRunTimeProperties(scenarioContext,what);
		
		try
		{
			switch (how)
			{
				case className:
					return driver.findElement(By.className(what));
				case css:
					return driver.findElement(By.cssSelector(what));
				case id:
					return driver.findElement(By.id(what));
				case linkText:
					return driver.findElement(By.linkText(what));
				case name:
					return driver.findElement(By.name(what));
				case partialLinkText:
					return driver.findElement(By.partialLinkText(what));
				case tagName:
					return driver.findElement(By.tagName(what));
				case xPath:
					return driver.findElement(By.xpath(what));
				default:
					return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			// retry
			waitHelper.wait(3);
			LoggerUtils.logComment("Retrying getting element" + how + ":" + what);
			return getPageElement(how, what);
		}
		catch (NoSuchElementException e)
		{
			if(isTestCaseFailedIfNoSuchExcetion)
				LoggerUtils.logException("Could not find the element on page", e, true);
			else
				LoggerUtils.logWarning("Could not find the element on page");
			return null;
		}
		
	}
	
	public  WebElement getPageElement(How how, String what){
		return getPageElement(how, what,true);
	}

	/**
	 * @param element
	 *            WebElement whose text is needed
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author i0465
	 */
	public String getText( WebElement element, String description)
	{
		LoggerUtils.logComment("Get text of '" + description + "'");
		String text = null;
		try
		{
			text = element.getText();
		}
		catch (StaleElementReferenceException e)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			
			text = element.getText();
			
		}
		
		return text;
	}
	
	/**
	 * Verify is webelement is enable or not
	 * @param element
	 * @return
	 */
	public Boolean IsElementEnabled(WebElement element)
	{
		Boolean visible = true;
		if (element == null)
			return false;
		try
		{
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isEnabled();
		}
		catch (StaleElementReferenceException e)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
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
			Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
			driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return visible;
	}

	/**
	 * Presses the given Key in the specified WebElement
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param Key
	 *            key to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void KeyPress(WebElement element, Keys key, String description)
	{
		LoggerUtils.logComment("Press the key '" + key.toString() + "' on " + description + "");
		element.sendKeys(key);
		}
	
	
	/**
	 * Method used to scroll up and down horizontally in browser
	 * @param from
	 * @param to
	 */
	public void pageScroll(String from, String to)
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(" + from + "," + to + ")");
	}
	
	/**
	
	 * @param element
	 *            WebElement to be submitted
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void submit(WebElement element, String description)
	{
		LoggerUtils.logComment("Submit '" + description + "'");
		element.submit();
	}


	/**
	 * Get attribute value
	 * @param element
	 * @param attributeName
	 * @param comment
	 * @return attributeValue
	 */
	public  String getAttribute(WebElement element, String attributeName, String comment)
	{
		LoggerUtils.logComment("Getting value of attribute '" + attributeName + "' for :" + comment);
		String value = "";
		try
		{
			value = element.getAttribute(attributeName);
		}
		catch(Exception wde)
		{
			LoggerUtils.logComment("Exception occurred in fetching value of attribute '" + attributeName + "' for :" + comment + " : " + wde.getMessage());
		}
		
		return value;
	}
	
	/**
	 * Get css value
	 * @param element
	 * @param css
	 * @param comment
	 * @return cssValue
	 * @author i0465
	 */
	public String getCSSValue(WebElement element, String css, String comment)
	{
		LoggerUtils.logComment("Getting value of CSS '" + css + "' for :" + comment);
		String value = "";
		try
		{
			value = element.getCssValue(css);
		}
		catch(Exception wde)
		{
			LoggerUtils.logComment("Exception occurred in fetching value of css '" + css + "' for :" + comment + " : " + wde.getMessage());
		}
		
		return value;
	}
	

	/**
	 * Verify Element is Not Enabled
	 * @param element
	 * @param description
	 */
	public void verifyElementNotEnabled(WebElement element, String description)
	{
		try
		{
			if (!IsElementEnabled(element))
			{
				LoggerUtils.logPass("Verified the disable of element '" + description + "' on the page",element,true);
			}
			
			else
			{
				LoggerUtils.logFail("Element '" + description + "' is enabled on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			if (!IsElementEnabled(element))
			{
				LoggerUtils.logPass("Verified the disable of element '" + description + "' on the page",element,true);
			}
			
			else
			{
				LoggerUtils.logFail("Element '" + description + "' is enabled on the page");
			}
		}
	}
	
	/**
	 * Verify Element is Enabled
	 * @param element
	 * @param description
	 */
	public void verifyElementEnabled(WebElement element, String description)
	{
		try
		{
			if (IsElementEnabled(element))
			{
				LoggerUtils.logPass("Verified the enable of element '" + description + "' on the page",element, true);
			}
			
			else
			{
				LoggerUtils.logFail("Element '" + description + "' is disabled on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			LoggerUtils.logComment("Stale element reference exception. Trying again...");
			if (IsElementEnabled(element))
			{
				LoggerUtils.logPass("Verified the enable of element '" + description + "' on the page",element, true);
			}
			
			else
			{
				LoggerUtils.logFail("Element '" + description + "' is disabled on the page");
			}
		}
	}
	

	/**
	 * This function is used to scroll an element into view
	 * @param element
	 */
	public void scrollToView(Config testConfig, WebElement element)
	{
		JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
		jse.executeScript("arguments[0].scrollIntoView(false)", element);
		
	}
	
	/**
	 * This method is used to move cursor from one web element to another element
	 * @param source
	 * @param destination
	 */
	public void moveCursorfromSourceToDestination(WebElement source,WebElement destination)
	{
		Actions actions = new Actions(driver);
		actions.moveToElement(source);
		actions.moveToElement(destination);
		actions.click().perform();
	}

	/**
	 * Upload file in on browser using robot class of java
	 * Note: This function will work on windows machine only
	 * @param element
	 * @return
	 */
	public boolean uploadFileUsingRobot(Config testConfig,WebElement element,String filePath,String description) {
		boolean flag=true;
		waitHelper.wait(2);
		click(element,description);
		StringSelection stringSelection = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		Robot robot;
		try {
			Robot robo = new Robot();
			waitHelper.wait(2);
			robo.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
			robo.keyPress(java.awt.event.KeyEvent.VK_V);
			robo.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
			robo.keyRelease(java.awt.event.KeyEvent.VK_V);
			robo.keyPress(java.awt.event.KeyEvent.VK_ENTER);
			robo.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			flag=false;
		}
		waitHelper.wait(2);
		return flag;
	}

	/**
	 * Mouse hover on given web element
	 * @param element
	 */
	public void mouseHoverOnElement(WebElement element) {
		Actions builder = new Actions(driver);
		builder.moveToElement(element).perform();
	}
	
	/**
	 * Returns true if the element is displayed on the WebPage
	 * @param element
	 * @return
	 * @author nikitagatagat
	 */

	public Boolean IsElementDisplayed(WebElement element) {
		{
			Boolean visible = true;
			if (element == null)
				return false;
			try
			{
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				visible = element.isDisplayed();
			}
			catch (StaleElementReferenceException e)
			{
				LoggerUtils.logComment("Stale element reference exception. Trying again...");
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
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
				Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
				driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
			}
			return visible;
		}
	}


	public void pressEnter() {
		Actions action = new Actions(driver);
		action.sendKeys(Keys.ENTER).perform();
	}


	/**
	 * Scroll window below and up using arrow key
	 * @param element
	 * @param isBelowScroll  --> if true then scrolling will be below other wise scrolling will be up
	 */
	public void scrollToViewUsingActionClass(WebElement element, boolean ...isBelowScroll)
	{   Actions actions = new Actions(driver);
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
	 * Mouse hove on given web element
	 * @param element
	 */
	public void mousehoverOnElementUsingJavaScript(WebElement element) {
		String strJavaScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
		((JavascriptExecutor) driver).executeScript(strJavaScript, element);
	}

	/**
	 * Enters the given 'value' For the password type field
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public void enterPassword(WebElement element, String value, String description)
	{
		String message = StringUtils.replaceEach("**************", new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
		element.clear();
			waitHelper.wait(1);
			element.sendKeys(value);
			LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
	}

	/**
	 * Enter Data on element using Selenium Actions
	 * 
	 * @param element
	 * @param value
	 * @param description
	 */
	public void enterDataThroughActions(Config testConfig, String Value, WebElement element) {
		clear(element, "Cleared the existing value");
		Actions action = new Actions(testConfig.driver);
		for (char c : Value.toCharArray()) {
			String text = getText(element, "");
			waitHelper.wait(1);
			action.sendKeys(element, text + c).perform();
			;
		}
		waitHelper.wait(1);
	}

}
