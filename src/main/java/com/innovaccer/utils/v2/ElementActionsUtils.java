package com.innovaccer.utils.v2;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.innovaccer.utils.v2.dataHelper.TestDataHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.innovaccer.utils.Element;
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
		Element.click(this.scenarioContext, getEnabledButtonEle(buttonName), buttonName);
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
			Element.enterData(this.scenarioContext, getTextField(Label), data, Label);
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
		return waitHelper.waitForVisibility( By.xpath(xpath),30,"");
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


	public boolean isElementDisplay(WebElement ele) {
		return Element.IsElementDisplayed(this.scenarioContext, ele);
	}

	public void setData(String key, String value) {
		this.scenarioContext.putRunTimeProperty(key, value);
	}

	public WaitHelper getWait() {
		return (waitHelper == null) ? waitHelper = new WaitHelper(scenarioContext) : waitHelper;
	}

	public void check(WebElement element, String description) {
		Element.check(scenarioContext, element, description);
	}
	public void clear(WebElement element, String description)
	{
		Element.clear(scenarioContext, element, description);
	}

	public void click(WebElement element, String description)
	{
		Element.click(scenarioContext, element, description);
	}
	public void clickThroughJS(WebElement elementToBeClicked, String description)
	{
		Element.clickThroughJS(scenarioContext, elementToBeClicked, description);
	}
	public void clearThroughJS(WebElement element, String description)
	{
		Element.clearThroughJS(scenarioContext, element, description);
	}
	public void enterDataThroughJS(WebElement element, String value, String description) {
		Element.enterDataThroughJS(scenarioContext, element, value, description);
	}
	public void doubleClick(WebElement element, String description)
	{
		Element.doubleClick(scenarioContext, element, description);
	}

	public void enterData(WebElement element, String value, String description)
	{
		Element.enterData(scenarioContext, element, value, description);
	}
	public void enterDataAfterClick(WebElement element, String value, String description)
	{
		Element.enterDataAfterClick(scenarioContext, element, value, description);
	}
	public void enterDataWithoutClear(WebElement element, String value, String description)
	{
		Element.enterDataWithoutClear(scenarioContext, element, value, description);
	}

	public void enterFileName(WebElement element, String value, String description)
	{
		Element.enterFileName(scenarioContext, element, value, description);
	}

	public List<String> getAllOptionsInSelect(WebElement element)
	{
		return Element.getAllOptionsInSelect(scenarioContext, element);
	}

	public WebElement getiFrameElement(How how, String what)
	{
		return Element.getiFrameElement(scenarioContext, how, what);
	}
	public List<WebElement> getListOfElements(How how, String what){
		return Element.getListOfElements(scenarioContext, how, what);
	}

	public void getOutOfFrame(Config scenariosInstance) {
		Element.getOutOfFrame(scenariosInstance);
	}

	public WebElement getPageElement(How how, String what,Boolean isTestCaseFailedIfNoSuchExcetion)
	{
		return Element.getPageElement(scenarioContext, how, what,isTestCaseFailedIfNoSuchExcetion);
	}
	public String getText(WebElement element, String description)
	{
		return Element.getText(scenarioContext, element, description);
	}
	public Boolean IsElementEnabled(WebElement element) {
		return Element.IsElementEnabled(scenarioContext, element);
	}

	public void KeyPress(WebElement element, Keys key, String description)
	{
		Element.KeyPress(scenarioContext, element, key, description);
	}
	public void pageScroll(String from, String to)
	{
		Element.pageScroll(scenarioContext, from, to);
	}
	public void submit(WebElement element, String description)
	{
		Element.submit(scenarioContext, element, description);
	}


	public String getAttribute(WebElement element, String attributeName, String comment)
	{
		return Element.getAttribute(scenarioContext, element, attributeName, comment);
	}
	public String getCSSValue(WebElement element, String css, String comment)
	{
		return Element.getCSSValue(scenarioContext, element, css, comment);
	}

	public void verifyElementNotEnabled(WebElement element, String description)
	{
		Element.verifyElementNotEnabled(scenarioContext, element, description);
	}

	public void verifyElementEnabled(WebElement element, String description)
	{
		Element.verifyElementEnabled(scenarioContext, element, description);
	}

	public void scrollToView(WebElement element)
	{
		Element.scrollToView(scenarioContext, element);
	}
	public void moveCursorFromSourceToDestination(WebElement source,WebElement destination)
	{
		Element.moveCursorfromSourceToDestination(scenarioContext, source, destination);
	}

	public boolean uploadFileUsingRobot(WebElement element,String filePath,String description)
	{
		return Element.uploadFileUsingRobot(scenarioContext, element, filePath, description);
	}

	public void mouseHoverOnElement(WebElement element) {
		Element.mousehoverOnElement(scenarioContext, element);
	}

	public Boolean IsElementDisplayed(WebElement element) {
		return Element.IsElementDisplayed(scenarioContext, element);
	}


	public void pressEnter() {
		Element.pressEnter(scenarioContext);
	}


	public void scrollToViewUsingActionClass(WebElement element, boolean ...isBelowScroll)
	{
		Element.scrollToViewUsingActionClass(scenarioContext, element, isBelowScroll);
	}

	public void mouseHoverOnElementUsingJavaScript(WebElement element) {
		Element.mousehoverOnElementUsingJavaScript(scenarioContext, element);
	}

	public void enterPassword(WebElement element, String value, String description)
	{
		Element.enterPassword(scenarioContext, element, value, description);
	}

	public void enterDataThroughActions(String Value, WebElement element) {
		 Element.enterDataThroughActions(scenarioContext, Value, element);
	}

}
