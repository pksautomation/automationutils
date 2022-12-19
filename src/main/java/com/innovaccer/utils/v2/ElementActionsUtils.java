package com.innovaccer.utils.v2;

import com.innovaccer.utils.v2.dataHelper.TestDataHelper;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import pojo.How;

/**
 * @author i0465
 */
public class ElementActionsUtils {
	private Config scenarioContext;
	private WaitHelper WaitUtils = null;
	private LoggerUtils LoggerUtils;
	private TestDataHelper testDataHelper;

	public ElementActionsUtils(Config scenariosInstance) {
		init(scenariosInstance);
	}

	public ElementActionsUtils() {
		init(Config.getConfig());
	}

	private void init(Config scenariosInstance) {
		this.scenarioContext = scenariosInstance;
		WaitUtils = new WaitHelper(scenarioContext);
		LoggerUtils = new LoggerUtils(scenarioContext);
		testDataHelper = new TestDataHelper(scenarioContext);
		PageFactory.initElements(this.scenarioContext.getDriver(), this);
	}

	/**
	 * get WebElement using Text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getVisibleElement(By by) {
		return WaitUtils.fluentWaitForVisibility(by, "");

	}

	/**
	 * get WebElement using Text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getVisibleElement(By by, int maxwaitTime) {
		return WaitUtils.fluentWaitForVisibility(by, "", maxwaitTime);

	}


	/**
	 * Get WebElement of Button using Label
	 *
	 * @param buttonLabel
	 * @return
	 */
	public WebElement getEnabledButtonEle(String buttonLabel) {
		String buttonXpath = "//button[contains(text(),'" + buttonLabel + "')]";
		By by = By.xpath(buttonXpath);
		return WaitUtils.waitForElementToBeClickable(by, buttonLabel);
	}

	/**
	 * Click on Dropdown button
	 *
	 * @param Label
	 * @return
	 * @author pramod.singh
	 */
	public WebElement getDropDownButton(How how) {
		WebElement element;
		By by = getPageElementLocator( how.getStrategy(), how.getValue(), how.getDescription());
		element = WaitUtils.fluentWaitForElementToBeClickable(by, how.getDescription());
		return element;
	}


	/**
	 * Check Button enable or not
	 *
	 * @param buttonName
	 * @return
	 */
	public boolean isButtonClickable(String buttonName) {
		By by= By.xpath("//body//button[text()='" + buttonName + "']");
		return WaitUtils.fluentWaitForElementToBeClickable(by, buttonName) == null ? false : true;

	}

	/**
	 *
	 * @param how
	 */
	public void fillData(String key, boolean... isDesignSystemComponent) {
		WebElement element = null;
		String data = testDataHelper.getTestData(key);
		How how;
		if((isDesignSystemComponent.length > 0 && isDesignSystemComponent[0]))
			how = getHowForDesignSystemComponent(key);
		else
			how = getHow(key);
		String pageName = scenarioContext.getRunTimeProperty("PageObjectName");
		if(how == null) {
			LoggerUtils.failFinalTestScenarios(" Locators info " + key + " not found in " + pageName + ".json file ");
		}
		else if (data != null && !data.equals("")) {
			By by = getPageElementLocator( how.getStrategy(), how.getValue(), how.getDescription());
			element = WaitUtils.fluentWaitForVisibility(by, how.getDescription());
			if(element != null)
				enterData(element, data, how.getDescription());
			else {
					LoggerUtils.failFinalTestScenarios("Element not Found for " + how.getDescription());
				}
			}
	}

	/**
	 * get WebElement using Text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getClickableElement(String text) {
		String xpath = "//body//*[text()='" + text + "']";
		return WaitUtils.waitForElementToBeClickable(By.xpath(xpath), "", 30);
	}

	/**
	 * get WebElement using Text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getClickableButtonElement(String text) {
		String xpath = "//body//button[text()='" + text + "']";
		return WaitUtils.waitForElementToBeClickable(By.xpath(xpath), "", 30);
	}

	/**
	 * get WebElement using Text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getDisplayElement(String text) {
		String xpath = "//body//*[text()='" + text + "']";
		return WaitUtils.waitForVisibility(By.xpath(xpath), 30,text);
	}

	/**
	 * Select DropDown Value
	 *
	 * @param Label --> it is either lable or place holder of dropdown
	 */
	public void selectDropDown(String id) {
		String data = testDataHelper.getTestData(id);
		How how = getHow(id);
		String pageName = scenarioContext.getRunTimeProperty("PageObjectName");
		if(how == null) {
			LoggerUtils.failFinalTestScenarios(" Locators info " + id + " not found in " + pageName + ".json file ");
		}
		if (data != null && !data.equals("")) {
			click(getDropDownButton(how), how.getDescription());
			By by = By.xpath("//*[contains(@class,'Option')]//*[text()='" + data + "']");
			WebElement element = scenarioContext.getDriver().findElement(by);
			click(element, how.getDescription() + " Dropdown option " + data);
		}

	}

	/**
	 *
	 * @param how
	 */
	public void clickOnButton(String id)
	{
		How how = getHow(id);
		String pageName = scenarioContext.getRunTimeProperty("PageObjectName");
		if(how == null) {
			LoggerUtils.failFinalTestScenarios(" Locators info " + id + " not found in " + pageName + ".json file ");
		}
		By byLocator = this.getPageElementLocator(how.getStrategy(), how.getValue(),how.getDescription());
		WebElement element = WaitUtils.fluentWaitForElementToBeClickable(byLocator, how.getDescription());
		if(element !=null) {
			click(element, how.getDescription());
			WaitUtils.wait(4);
		}
		
		else {
			LoggerUtils.failFinalTestScenarios("Element not Found for " + how.getDescription());
		}
		
	}

	/**
	 *
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param isTestCaseFailedIfNoSuchExcetion
	 * @return
	 */
	public By getPageElementLocator(String stretegy, String what, String description) {
		By byLocator = null;
		switch (stretegy.toLowerCase()) {
		case "classname":
			byLocator = By.className(what);
			break;
		case "css":
			byLocator = By.cssSelector(what);
			break;
		case "id":
			byLocator = By.id(what);
			break;
		case "linktext":
			byLocator = By.linkText(what);
			break;
		case "name":
			byLocator = By.name(what);
			break;
		case "partialLinkText":
			byLocator = By.partialLinkText(what);
			break;
		case "tagname":
			byLocator = By.tagName(what);
			break;
		case "xpath":
			byLocator = By.xpath(what);

		}
		return byLocator;

	}


    /**
     * Check Button enable or not
     *
     * @param buttonName
     * @return
     */
    public boolean isButtonEnable(String buttonName) {
        return getDisplayElement(buttonName) != null;

    }

    public String switchToNewWindow() {
        if (scenarioContext.getDriver() != null) {
            LoggerUtils.logComment("Switching to the new window");
            String oldWindow = scenarioContext.getDriver().getWindowHandle();

            if (scenarioContext.getDriver().getWindowHandles().size() < 2) {
                LoggerUtils.logFail("No new window appeared, windows count available :-" + scenarioContext.getDriver().getWindowHandles().size());
            }

            for (String winHandle : scenarioContext.getDriver().getWindowHandles()) {
                if (!winHandle.equals(oldWindow)) {
                	scenarioContext.getDriver().switchTo().window(winHandle);
                    LoggerUtils.logComment("Switched to window with URL:- " + scenarioContext.getDriver().getCurrentUrl() + ". And title as :- " + scenarioContext.getDriver().getTitle());
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
        return (WaitUtils == null) ? WaitUtils = new WaitHelper(scenarioContext) : WaitUtils;
    }

    public void check(WebElement element, String description) {
        LoggerUtils.logComment("Check '" + description + "'");
        if (!element.isSelected()) {
            try {
                clickWithoutLog(element);
                WaitUtils.wait(1);
            } catch (StaleElementReferenceException e) {
                LoggerUtils.logComment("Stale element reference exception. Trying again...");
                clickWithoutLog(element);
            }

        }
    }

    /**
     * @param element     WebElement to be cleared
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void clear(WebElement element, String description) {
        element.clear();
        LoggerUtils.logComment("Clear data of '" + description + "'");
    }

    /**
     * @param element     WebElement to be clicked
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     * @author i0465
     */
    public void click(WebElement element, String description) {
        Actions builder = new Actions(scenarioContext.getDriver());
        builder.moveToElement(element).click().build().perform();
        LoggerUtils.logComment("Clicked on " + description);
    }

    /**
     * Clicks on element using JavaScript
     *
     * @param elementToBeClicked - Element to be clicked
     * @param description        For logging
     */
    public void clickThroughJS(WebElement elementToBeClicked, String description) {
        JavascriptExecutor js = (JavascriptExecutor) scenarioContext.getDriver();
        js.executeScript("arguments[0].click();", elementToBeClicked);
        LoggerUtils.logComment("Clicked on " + description);
    }

    /**
     * Clear on element using JavaScript
     *
     * @param elementToBeClicked
     * @param description
     */
    public void clearThroughJS(WebElement element, String description) {
        JavascriptExecutor js = (JavascriptExecutor) scenarioContext.getDriver();
        js.executeScript("arguments[0].value ='';", element);
        LoggerUtils.logComment("Cleared on " + description);
    }

    /**
     * Enter Data on element using JavaScript
     *
     * @param element
     * @param value
     * @param description
     */
    public void enterDataThroughJS(WebElement element, String value, String description) {
        JavascriptExecutor js = (JavascriptExecutor) scenarioContext.getDriver();
        String message = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
        js.executeScript("arguments[0].value='" + value + "';", element);
        LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
    }

    /**
     * @param element     WebElement to be double clicked
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     * @author i0465
     */
    public void doubleClick(WebElement element, String description) {
        Actions action = new Actions(scenarioContext.getDriver());
        action.doubleClick(element).perform();
        LoggerUtils.logComment("Successfully Double Click on '" + description + "'");
    }

    /**
     * Enters the given 'value'in the specified WebElement
     *
     * @param element     WebElement where data needs to be entered
     * @param value       value to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     * @author i0465
     */
    public void enterData(WebElement element, String value, String description) {
        String message = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
        element.clear();
        WaitUtils.wait(1);
        element.sendKeys(value);
        LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
    }

    /**
     * Enters the given 'value'in the specified WebElement after clicking on it
     *
     * @param element     WebElement where data needs to be entered
     * @param value       value to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void enterDataAfterClick(WebElement element, String value, String description) {
        if (!value.equalsIgnoreCase("{skip}")) {
            // encode the html characters so that they get printed correctly
            String message = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
            LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
            clickWithoutLog(element);
            element.clear();
            WaitUtils.wait(1);
            element.sendKeys(value);

        } else {
            LoggerUtils.logComment("Skipped data entry for " + description);
        }
    }

    /**
     * Click without logging
     *
     * @param element
     */
    private void clickWithoutLog(WebElement element) {
        try {
            JavascriptExecutor jse = (JavascriptExecutor) scenarioContext.getDriver();
            jse.executeScript("arguments[0].scrollIntoView(false)", element);
            element.click();
        } catch (WebDriverException wde) {
            element.click();
        }
    }

    /**
     * Enters the given 'value'in the specified WebElement without clear
     *
     * @param element     WebElement where data needs to be entered
     * @param value       value to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void enterDataWithoutClear(WebElement element, String value, String description) {
        if (!value.equalsIgnoreCase("{skip}")) {
            // encode the html characters so that they get printed correctly
            String message = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
            LoggerUtils.logComment("Enter the " + description + " as '" + message + "'");
            element.sendKeys(value);

        } else {
            LoggerUtils.logComment("Skipped data entry for " + description);
        }
    }

    /**
     * Enters the given 'value'in the specified File name WebElement
     *
     * @param element     Filename WebElement where data needs to be entered
     * @param value       value to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void enterFileName(WebElement element, String value, String description) {
        if (!value.equalsIgnoreCase("{skip}")) {

            LoggerUtils.logComment("Enter the " + description + " as '" + value + "'");
            element.sendKeys(value);

        } else {
            LoggerUtils.logComment("Skipped file entry for " + description);
        }
    }

    /**
     * Gets all the available string options in the Select Element
     *
     * @param element Select WebElement
     * @return String list of options
     * @author i0465
     */
    public List<String> getAllOptionsInSelect(WebElement element) {
        Select sel = new Select(element);
        List<WebElement> elements = sel.getOptions();
        List<String> options = new ArrayList<String>(elements.size());

        for (WebElement e : elements) {
            options.add(e.getText());
        }
        LoggerUtils.logComment("Retrieve all the Options present for this specified Select WebElement");
        return options;
    }


    private List<WebElement> getiFramesOnPage(WebDriver driver) {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        return iframes;
    }

    public void getOutOfFrame(Config scenariosInstance) {
    	scenarioContext.getDriver().switchTo().defaultContent();
    }


    /**
     * @param element     WebElement whose text is needed
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     * @author i0465
     */
    public String getText(WebElement element, String description) {
        LoggerUtils.logComment("Get text of '" + description + "'");
        String text = null;
        try {
            text = element.getText();
        } catch (StaleElementReferenceException e) {
            LoggerUtils.logComment("Stale element reference exception. Trying again...");

            text = element.getText();

        }

        return text;
    }

    /**
     * Verify is webelement is enable or not
     *
     * @param element
     * @return
     */
    public Boolean IsElementEnabled(WebElement element) {
        Boolean visible = true;
        if (element == null)
            return false;
        try {
        	scenarioContext.getDriver().manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            visible = element.isEnabled();
        } catch (StaleElementReferenceException e) {
            LoggerUtils.logComment("Stale element reference exception. Trying again...");
            scenarioContext.getDriver().manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            visible = element.isDisplayed();

        } catch (NoSuchElementException e) {
            visible = false;
        } catch (ElementNotVisibleException e) {
            visible = false;
        } finally {
            Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
            scenarioContext.getDriver().manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
        }
        return visible;
    }

    /**
     * Presses the given Key in the specified WebElement
     *
     * @param element     Filename WebElement where data needs to be entered
     * @param Key         key to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void KeyPress(WebElement element, Keys key, String description) {
        LoggerUtils.logComment("Press the key '" + key.toString() + "' on " + description + "");
        element.sendKeys(key);
    }

    /**
     * Method used to scroll up and down horizontally in browser
     *
     * @param from
     * @param to
     */
    public void pageScroll(String from, String to) {
        JavascriptExecutor js = (JavascriptExecutor) scenarioContext.getDriver();
        js.executeScript("window.scrollBy(" + from + "," + to + ")");
    }

    /**
     * @param element     WebElement to be submitted
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void submit(WebElement element, String description) {
        LoggerUtils.logComment("Submit '" + description + "'");
        element.submit();
    }

    /**
     * Get attribute value
     *
     * @param element
     * @param attributeName
     * @param comment
     * @return attributeValue
     */
    public String getAttribute(WebElement element, String attributeName, String comment) {
        LoggerUtils.logComment("Getting value of attribute '" + attributeName + "' for :" + comment);
        String value = "";
        try {
            value = element.getAttribute(attributeName);
        } catch (Exception wde) {
            LoggerUtils.logComment("Exception occurred in fetching value of attribute '" + attributeName + "' for :" + comment + " : " + wde.getMessage());
        }

        return value;
    }

    /**
     * Get css value
     *
     * @param element
     * @param css
     * @param comment
     * @return cssValue
     * @author i0465
     */
    public String getCSSValue(WebElement element, String css, String comment) {
        LoggerUtils.logComment("Getting value of CSS '" + css + "' for :" + comment);
        String value = "";
        try {
            value = element.getCssValue(css);
        } catch (Exception wde) {
            LoggerUtils.logComment("Exception occurred in fetching value of css '" + css + "' for :" + comment + " : " + wde.getMessage());
        }

        return value;
    }

    /**
     * Verify Element is Not Enabled
     *
     * @param element
     * @param description
     */
    public void verifyElementNotEnabled(WebElement element, String description) {
        try {
            if (!IsElementEnabled(element)) {
                LoggerUtils.logPass("Verified the disable of element '" + description + "' on the page", element, true);
            } else {
                LoggerUtils.logFail("Element '" + description + "' is enabled on the page");
            }
        } catch (StaleElementReferenceException e) {
            LoggerUtils.logComment("Stale element reference exception. Trying again...");
            if (!IsElementEnabled(element)) {
                LoggerUtils.logPass("Verified the disable of element '" + description + "' on the page", element, true);
            } else {
                LoggerUtils.logFail("Element '" + description + "' is enabled on the page");
            }
        }
    }

    /**
     * Verify Element is Enabled
     *
     * @param element
     * @param description
     */
    public void verifyElementEnabled(WebElement element, String description) {
        try {
            if (IsElementEnabled(element)) {
                LoggerUtils.logPass("Verified the enable of element '" + description + "' on the page", element, true);
            } else {
                LoggerUtils.logFail("Element '" + description + "' is disabled on the page");
            }
        } catch (StaleElementReferenceException e) {
            LoggerUtils.logComment("Stale element reference exception. Trying again...");
            if (IsElementEnabled(element)) {
                LoggerUtils.logPass("Verified the enable of element '" + description + "' on the page", element, true);
            } else {
                LoggerUtils.logFail("Element '" + description + "' is disabled on the page");
            }
        }
    }

    /**
     * This function is used to scroll an element into view
     *
     * @param element
     */
    public void scrollToView(WebElement element) {
        JavascriptExecutor jse = (JavascriptExecutor) scenarioContext.getDriver();
        jse.executeScript("arguments[0].scrollIntoView(false)", element);

    }

    /**
     * This method is used to move cursor from one web element to another element
     *
     * @param source
     * @param destination
     */
    public void moveCursorfromSourceToDestination(WebElement source, WebElement destination) {
        Actions actions = new Actions(scenarioContext.getDriver());
        actions.moveToElement(source);
        actions.moveToElement(destination);
        actions.click().perform();
    }

    /**
     * Upload file in on browser using robot class of java
     * Note: This function will work on windows machine only
     *
     * @param element
     * @return
     */
    public boolean uploadFileUsingRobot(Config testConfig, WebElement element, String filePath, String description) {
        boolean flag = true;
        WaitUtils.wait(2);
        click(element, description);
        StringSelection stringSelection = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        Robot robot;
        try {
            Robot robo = new Robot();
            WaitUtils.wait(2);
            robo.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robo.keyPress(java.awt.event.KeyEvent.VK_V);
            robo.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            robo.keyRelease(java.awt.event.KeyEvent.VK_V);
            robo.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            robo.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            flag = false;
        }
        WaitUtils.wait(2);
        return flag;
    }

    /**
     * Mouse hover on given web element
     *
     * @param element
     */
    public void mouseHoverOnElement(WebElement element) {
        Actions builder = new Actions(scenarioContext.getDriver());
        builder.moveToElement(element).perform();
    }

    /**
     * Returns true if the element is displayed on the WebPage
     *
     * @param element
     * @return
     * @author nikitagatagat
     */

    public Boolean IsElementDisplayed(WebElement element) {
        {
            Boolean visible = true;
            if (element == null)
                return false;
            try {
            	scenarioContext.getDriver().manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                visible = element.isDisplayed();
            } catch (StaleElementReferenceException e) {
                LoggerUtils.logComment("Stale element reference exception. Trying again...");
                scenarioContext.getDriver().manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                visible = element.isDisplayed();

            } catch (NoSuchElementException e) {
                visible = false;
            } catch (ElementNotVisibleException e) {
                visible = false;
            } finally {
                Long ObjectWaitTime = Long.parseLong(scenarioContext.getRunTimeProperty("ObjectWaitTime"));
                scenarioContext.getDriver().manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
            }
            return visible;
        }
    }

    public void pressEnter() {
        Actions action = new Actions(scenarioContext.getDriver());
        action.sendKeys(Keys.ENTER).perform();
    }

    /**
     * Scroll window below and up using arrow key
     *
     * @param element
     * @param isBelowScroll --> if true then scrolling will be below other wise scrolling will be up
     */
    public void scrollToViewUsingActionClass(WebElement element, boolean... isBelowScroll) {
        Actions actions = new Actions(scenarioContext.getDriver());
        actions.moveToElement(element);
        actions.click();
        if (isBelowScroll.length == 0)
            actions.perform();
        else if (isBelowScroll[0]) {
            actions.sendKeys(Keys.ARROW_DOWN);
            actions.perform();
        } else {
            actions.sendKeys(Keys.ARROW_UP);
            actions.perform();
        }
    }

    /**
     * Mouse hove on given web element
     *
     * @param element
     */
    public void mousehoverOnElementUsingJavaScript(WebElement element) {
        String strJavaScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        ((JavascriptExecutor) scenarioContext.getDriver()).executeScript(strJavaScript, element);
    }

    /**
     * Enters the given 'value' For the password type field
     *
     * @param element     WebElement where data needs to be entered
     * @param value       value to the entered
     * @param description logical name of specified WebElement, used for Logging
     *                    purposes in report
     */
    public void enterPassword(WebElement element, String value, String description) {
        String message = StringUtils.replaceEach("**************", new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
        element.clear();
        WaitUtils.wait(1);
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
    public void enterDataThroughActions(String Value, WebElement element) {
        clear(element, "Cleared the existing value");
        Actions action = new Actions(scenarioContext.getDriver());
        for (char c : Value.toCharArray()) {
            String text = getText(element, "");
            WaitUtils.wait(1);
            action.sendKeys(element, text + c).perform();
        }
        WaitUtils.wait(1);
    }

	/**
	 *
	 * @param locatorkey
	 * @return
	 */
	public How getHow(String id) {
		String pageName = scenarioContext.getRunTimeProperty("PageObjectName");
		if(pageName == null ) {
			LoggerUtils.logFail(" PageObjectName value not found in scenarios context");
			return null;
		}
		else if(scenarioContext.getPagesLocatorData().containsKey(pageName))
			return scenarioContext.getPagesLocatorData().get(pageName).get(id);
		else {
			LoggerUtils.logFail(" Locator : " + id + " not found in " + pageName + ".json file");
			return null;
		}
	}

	public How getHowForDesignSystemComponent(String designSystemComponent) {
		return scenarioContext.getPagesLocatorData().get("DesignSystem").get(designSystemComponent);
	}
	
	public WebElement getWebElement(String id) {
		How how = getHow(id);
		By byLocator = this.getPageElementLocator(how.getStrategy(), how.getValue(),how.getDescription());
		WebElement element = WaitUtils.fluentWaitForVisibility(byLocator, how.getDescription());
		return element;	
	}
	
	public boolean isElementDisplayed(String id) {
		if(getWebElement(id)== null)
			return false;
		else {
			return getWebElement(id).isDisplayed();
		}
	}
}
