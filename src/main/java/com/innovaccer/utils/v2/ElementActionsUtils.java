package com.innovaccer.utils.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.epam.healenium.SelfHealingDriver;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Element;
import com.innovaccer.utils.Element.How;

/**
 *
 * @author i0465
 *
 */
public class ElementActionsUtils  {

	public Config scenarioContext;
	public WaitHelper WaitUtils=null;
	public UtilityObjectManager UtilityObjectManager=null;
	public LoggerUtils LoggerUtils;
	private WebDriver driver;

	public ElementActionsUtils(Config scenariosInstance) {
		this.scenarioContext=scenariosInstance;
		this.UtilityObjectManager = new UtilityObjectManager(scenariosInstance);
		WaitUtils = new WaitHelper(scenariosInstance);
		LoggerUtils=new LoggerUtils(scenarioContext);
		driver=scenarioContext.driver;
		PageFactory.initElements(scenariosInstance.driver, this);
		}
	
	/**
	 * get WebElement of Input field like file, text, textarea using Label or Placeholder
	 * @param Label
	 * @return
	 */
	public WebElement getTextField(String Label) {
		WebElement element=null;
		String xpath;
		int lebel =1;
		List<WebElement> elements=new ArrayList<WebElement>();
		By by1=By.xpath("//body//*[text()='"+Label+"']");
		WaitUtils.waitForVisibility(scenarioContext, by1, "Label " + Label, 10l);
		scenarioContext.driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
		while(true && lebel<4) {
			By by2=By.xpath("//*[text()='"+Label+"']/ancestor::div["+lebel+"]//input[@type!='file' and @type!='checkbox' ] ");
			elements =this.scenarioContext.driver.findElements(by2);
			if(elements.size() !=0) {
				break;
			}
			lebel++;
		}
		//List<WebElement> elements =this.scenarioContext.driver.findElements(by2);
		for(int i=0;i<elements.size(); i++) {
			if(elements.get(i).isDisplayed() || elements.get(i).isEnabled())
				return elements.get(i);
		}
		Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
		driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		return elements.isEmpty()?null:elements.get(0);
	}

	/**
	 * Get WebElement of Button using Label
	 * @param buttonLabel
	 * @return
	 */
	public WebElement getEnabledButtonEle(String buttonLabel) {
		String buttonXpath = "//button[contains(text(),'"+buttonLabel+"')]";
		By by = By.xpath(buttonXpath);
		Element.waitForVisibility(scenarioContext, by,buttonLabel, 40l);
		List<WebElement> elements = Element.getListOfElements(scenarioContext, How.xPath, buttonXpath);
		for(WebElement ele : elements) {
			if(ele.isDisplayed() && ele.isEnabled()) {
				return Element.getPageElement(this.scenarioContext, Element.How.xPath, buttonXpath);
			}
		}
		return null;		
	}
	/**
	 * get element of Button
	 * @param buttonLabel
	 * @return
	 */
	public WebElement getButtonEle(String buttonLabel) {
		String buttonXpath = "//button[contains(text(),'"+buttonLabel+"')]";
		By by = By.xpath(buttonXpath);
		Element.waitForVisibility(scenarioContext, by,buttonLabel, 40l);
		List<WebElement> elements = Element.getListOfElements(scenarioContext, How.xPath, buttonXpath);
		for(WebElement ele : elements) {
			if(ele.isDisplayed()) {
				return Element.getPageElement(this.scenarioContext, Element.How.xPath, buttonXpath);
			}
		}
		return null;		
	}

	/**
	 * Click on Dropdown button
	 * @param Label
	 * @return
	 * @author pramod.singh
	 */
	public WebElement getDropDownButton(String Label) {
		By by1=By.xpath("//*[text()='"+Label+"']");
		int lebel=1;
		List<WebElement> elements=new ArrayList<WebElement>();
		WaitUtils.waitForVisibility(scenarioContext, by1, "Label " + Label, 2l);
		scenarioContext.driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
		while(true && lebel<4) {
			By by2=By.xpath("//*[text()='"+Label+"']/ancestor::div["+lebel+"]//button ");
			elements =this.scenarioContext.driver.findElements(by2);
			if(elements.size() !=0) {
				break;
			}
			lebel++;
		}
		//List<WebElement> elements =this.scenarioContext.driver.findElements(by2);
		for(int i=0;i<elements.size(); i++) {
			if(elements.get(i).isDisplayed() || elements.get(i).isEnabled())
				return elements.get(i);
		}
		Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
		driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		return elements.isEmpty()?null:elements.get(0);
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
		WebElement element = getButtonEle(buttonName);
		if(element!=null && element.isEnabled())
			return true;
		else 
			return false;
	}


	public void setTextField(String Label)
	{
		String data=getData(Label);;
		if(data!= null && !data.equals(""))
			Element.enterData(this.scenarioContext, getTextField(Label), data, Label);
	}

	/**
	 * Select DropDown Value
	 * @param Label --> it is either lable or place holder of dropdown
	 */
	public void selectDropDown(String Label)
	{
		String data=getData(Label);
		if( data!= null && !data.equals(""))
		{
			click(getDropDownButton(Label),Label);
			By by = By.xpath("//*[contains(@class,'Option')]//*[text()='"+data+"']");
			WebElement element = scenarioContext.driver.findElement(by);
			click(element, Label + " Dropdown option " + data);	
		}

	}


	public boolean isElementDisplay(WebElement ele) {
		return Element.IsElementDisplayed(this.scenarioContext, ele);
	}

	public void setData(String key, String value) {
		this.scenarioContext.putRunTimeProperty(key, value);
	}

	public String getData(String lable) {
		String testDataName = scenarioContext.getRunTimeProperty("TestDataName");
		String data=null;
		if(scenarioContext.testData.containsKey(testDataName) && scenarioContext.testData.get(testDataName).containsKey(lable))
			return scenarioContext.testData.get(testDataName).get(lable);
		else 
			return null;		
		//return this.scenarioContext.getRunTimeProperty(lable);
	}

	public WaitHelper getWait() {
		return (WaitUtils == null) ? WaitUtils = new WaitHelper(scenarioContext) : WaitUtils;
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
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getClickableElement(String text) {
		String xpath = "//body//*[text()='"+text+"']";
		WaitUtils.wait(2);
		WebElement ele = WaitUtils.waitForVisibility(scenarioContext, By.xpath(xpath),"", 30l);
		List<WebElement> elemens = driver.findElements(By.xpath(xpath));
		for(int i=0; i<elemens.size(); i++) {
			ele=elemens.get(i);
			if(ele.isEnabled())
			return ele;
		}
		return null;
				
	}
	
	/**
	 * get WebElement using Text
	 * @param text
	 * @return
	 */
	public WebElement getDisplayElement(String text) {
		String xpath = "//body//*[text()='"+text+"']";
		WebElement ele = WaitUtils.waitForVisibility(scenarioContext, By.xpath(xpath),"", 30l);
		List<WebElement> elemens = driver.findElements(By.xpath(xpath));
		for(int i=0; i<elemens.size(); i++) {
			ele=elemens.get(i);
			if(ele.isDisplayed() || ele.isEnabled())
			return ele;
		}
		return null;
		
	}

}
