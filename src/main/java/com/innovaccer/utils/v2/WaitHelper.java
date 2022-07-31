package com.innovaccer.utils.v2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Element;
import com.innovaccer.utils.Element.How;

/**
 * 
 * @author i0465
 *
 */
public class WaitHelper extends LoggerHelper{

	private Config configInstance;
	
	public WaitHelper(Config config) {
		super(config);
		this.configInstance=config;
	}
	
	public void waitForVisibility(WebElement element, int timeInSeconds, String description)
	{
		Element.waitForVisibility(this.configInstance, element, timeInSeconds, description);
	}
	
	public void waitForStaleness(WebElement element, String description)
	{
		Element.waitForStaleness(configInstance, element, description);
	}
	
	public void waitForInvisibility( By by, String description)
	{
		Element.waitForInvisibility( configInstance ,by , description);
	}
	
	
	public void waitForElementToLoad(By by,int maxWaitTimeInSecond,String description) {
		Element.waitForElementToLoad(configInstance, by, description);
	}
	
	
	public WebElement waitForVisibility(Config configInstance, By by,String description, Long ...maxwaitTime) {
		if(maxwaitTime.length>1)
			return Element.waitForVisibility(configInstance, by, description, maxwaitTime[0]);
		else
			return Element.waitForVisibility(configInstance, by, description);
	}
	
	
	public void waitTillElementHasValue(WebElement element, String textToBePresentInValueAttribiute, String description)
	{
		Element.waitTillElementHasValue(configInstance,element, textToBePresentInValueAttribiute, description);
	}
	
	
	public  void waitForElementToDisappear(WebElement elementName) {
		Element.waitForElementToDisappear(configInstance, elementName);
	}
	
	public  void verifyElementNotPresent(WebElement element, String description) {
		Element.verifyElementNotPresent(configInstance, element, description);
	}
	
	public  void verifyElementPresent(WebElement element, String description)
	{
		Element.verifyElementPresent(configInstance,element, description);
		
	}

	
}
