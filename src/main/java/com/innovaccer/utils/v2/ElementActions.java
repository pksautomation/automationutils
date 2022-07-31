package com.innovaccer.utils.v2;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Element;
import com.innovaccer.utils.Element.How;

/**
 *
 * @author i0465
 *
 */
public class ElementActions extends WaitHelper {

	public Config scenarioContext;
	public WaitHelper waitHelper=null;
	public UtilityObjectManager UtilityObjectManager=null;

	public ElementActions(Config scenariosInstance) {
		super(scenariosInstance);
		this.scenarioContext=scenariosInstance;
		this.UtilityObjectManager = new UtilityObjectManager(scenariosInstance);
		PageFactory.initElements(scenariosInstance.driver, this);
		}

	public WebElement getTextField(String Label) {
		String xpath="//*[text()='"+Label+"']/..//*[(local-name()='input' and @type='text') or local-name()='textarea' or (local-name()='input' and @type!='file')]";
		return Element.getPageElement(scenarioContext, How.xPath, xpath);
	}


	public WebElement getButtonEle(String buttonLabel) {
		String buttonXpath = "//button[contains(text(),'"+buttonLabel+"')]";
		By by = By.xpath(buttonXpath);
		Element.waitForVisibility(scenarioContext, by,buttonLabel, 20l);
		List<WebElement> elements = Element.getListOfElements(scenarioContext, How.xPath, buttonXpath);
		for(WebElement ele : elements) {
			if(ele.isDisplayed() && ele.isEnabled()) {
				return Element.getPageElement(this.scenarioContext, Element.How.xPath, buttonXpath);
			}
		}
		return null;		
	}


	public WebElement getSelectField(String Label) {
		By by=By.xpath("//*[text()='"+Label+"']/../..//button");
		return Element.getPageElement(this.scenarioContext, Element.How.xPath, Label);
	}


	public void clickOnButton(String buttonName)
	{
		Element.click(this.scenarioContext, getButtonEle(buttonName), buttonName);
	}


	public void setTextField(String Label)
	{
		String data=getData(Label);;
		if(!data.equals(""))
			Element.enterData(this.scenarioContext, getTextField(Label), data, Label);
	}


	public void selectDropDown(String Label)
	{
		String data=getData(Label);
		if(!data.equals(""))
		{
		getSelectField(Label).click();
		this.scenarioContext.driver.findElement(By.xpath("//div[contains(@class,'Popover')]//span[text()='"+data+"']")).click();
		}

	}


	public boolean isElementDisplay(WebElement ele) {
		return Element.IsElementDisplayed(this.scenarioContext, ele);
	}

	public void setData(String key, String value) {
		this.scenarioContext.putRunTimeProperty(key, value);
	}

	public String getData(String lable) {
		return this.scenarioContext.getRunTimeProperty(lable);
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
