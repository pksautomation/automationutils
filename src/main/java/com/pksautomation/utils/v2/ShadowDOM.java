package com.pksautomation.utils.v2;
/**
 * @author Pramod Singh
 */

import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ShadowDOM {

	 /**
	  * Get root element from Shadow Dom tree
	  * @param testConfig
	  * @param selectors
	  * @param element
	  * @return
	  */
    public static  WebElement getElement(Config testConfig ,String[] selectors, WebElement ...element) {
    	long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
    	WebDriverWait wait = new WebDriverWait(testConfig.getDriver(), ObjectWaitTime);
    	WebElement root = null;
        WebElement selectorElement = null;
        boolean baseCase = false;
        new WaitHelper(testConfig).waitForJStoLoad();     // Wait for Page Load complete
        
        String selector = selectors[0];          //Get the first selector from the array
        
        if (selectors.length == 1)
        {
            baseCase = true;
        }
        else {
            //If there are more selectors, then remove this selector and recurse with the rest
            selectors = Arrays.copyOfRange(selectors, 1, selectors.length);
        }
        //If this is the first call...
        if (element.length == 0)
        {

        	wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
            //Use the driver to select the element
            selectorElement = testConfig.getDriver().findElement(By.cssSelector(selector));
        }
        else {
        	wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
            //Otherwise, use the previously found element
            selectorElement = element[0].findElement(By.cssSelector(selector));

        }
        
        //Get the shadow root
        root = (WebElement)((JavascriptExecutor)testConfig.getDriver()).executeScript("return arguments[0].shadowRoot", selectorElement);

        if (baseCase){
            return root;
       }
        else {
            //Recurse
            root = getElement(testConfig ,selectors, root);
        }

    	return root;
    }
    
	
}
