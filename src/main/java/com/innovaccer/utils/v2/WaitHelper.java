package com.innovaccer.utils.v2;

import com.innovaccer.utils.Element.How;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.NoSuchElementException;

public class WaitHelper {

    private Config configInstance;
    private LoggerUtils loggerUtils;
    private PopupUtils popupUtils;
    private ElementActionsUtils elementActionsUtils;

    public WaitHelper(Config config) {
        init(config);
    }

    public WaitHelper() {
        init(Config.getConfig());
    }

    private void init(Config config) {
        this.configInstance = config;
        loggerUtils = new LoggerUtils(this.configInstance);
        popupUtils = new PopupUtils(this.configInstance);
        elementActionsUtils = new ElementActionsUtils(this.configInstance);
    }

    public WebElement waitForVisibility(By by, int timeInSeconds, String description) {
        WebElement webElement = configInstance.getDriver().findElement(by);
        waitForVisibility(webElement, timeInSeconds, description);
        return webElement;
    }

    public void waitForVisibility(WebElement element, int timeInSeconds, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
        WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), timeInSeconds);
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException tm) {
            loggerUtils.logException(description + " not found after waiting for " + timeInSeconds + " seconds", tm);
        }
    }

    public void waitForStaleness(WebElement element, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be stable on the page.");
        Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));
        WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), ObjectWaitTime);
        try {
            wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException tm) {
            loggerUtils.logException("Waited for element " + description + " to get stale for " + ObjectWaitTime + " seconds", tm);
        }
    }

    public void waitForInvisibility(By locator, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be invisible on the page.");
        Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.getDriver())
                .withTimeout(Duration.ofSeconds(ObjectWaitTime))
                .pollingEvery(Duration.ofSeconds(5));
        try {
            fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException tm) {
            loggerUtils.logException(description + " found after waiting for " + ObjectWaitTime + " seconds", tm);
        }
    }

    public void waitTillElementHasValue(WebElement element, String textToBePresentInValueAttribute,
                                        String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to have :-" + textToBePresentInValueAttribute + " in value attribute");
        Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));

        WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), ObjectWaitTime);
        try {
            wait.until(ExpectedConditions.textToBePresentInElementValue(element, textToBePresentInValueAttribute));
        } catch (TimeoutException tm) {
            loggerUtils.logException("Waited for text:'" + textToBePresentInValueAttribute +
                    "' to be present as value in element:" + description + " for " + ObjectWaitTime + " seconds", tm);
        }
    }

    public void waitForElementToDisappear(WebElement elementName) {
        try {
            for (int i = 1; i <= 50; i++) {
                if (!(elementActionsUtils.IsElementDisplayed(elementName)))
                    break;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            loggerUtils.logComment("element is not present on page");
        }
    }

    public void verifyElementNotPresent(WebElement element, String description) {

        try {
            if (!elementActionsUtils.IsElementDisplayed(element)) {
                loggerUtils.logPass("Verified the absence of element '" + description + "' on the page");
            } else {
                loggerUtils.logFail("Element '" + description + "' is present on the page");
            }
        } catch (StaleElementReferenceException e) {
            loggerUtils.logComment("Stale element reference exception. Trying again...");
            if (!elementActionsUtils.IsElementDisplayed(element)) {
                loggerUtils.logPass("Verified the absence of element '" + description + "' on the page");
            } else {
                loggerUtils.logFail("Element '" + description + "' is present on the page");
            }
        }
    }

    public void verifyElementPresent(WebElement element, String description) {
        if (element.isDisplayed()) {
            loggerUtils.logPass("Verified the presence of element '" + description + "' on the page");
        } else {
            loggerUtils.logFail("Element '" + description + "' is not present on the page");
        }
    }

    public void wait(int seconds) {
        int milliseconds = seconds * 1000;
        try {
            Thread.sleep(milliseconds);
            loggerUtils.logComment("Wait for '" + seconds + "' seconds");
        } catch (InterruptedException e) {
            loggerUtils.logFailureException(e);
        }
    }

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
            loggerUtils.logException(description, e, true);
        }
        return element;

    }

    public boolean waitForJStoLoad() {
        JavascriptExecutor javaScript = (JavascriptExecutor) configInstance.driver;
        WebDriverWait wait = new WebDriverWait(configInstance.driver,
                Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime")));
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
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return javaScript.executeScript("return document.readyState").toString().equals("complete");
            }
        };
        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    public void wait(Config configInstance, double seconds) {
        int milliseconds = (int) (seconds * 1000);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            loggerUtils.logFailureException(e);
        }
    }

    public WebElement fluentWaitForVisibility(By locator, String description, int... timeinsecons) {
        WebElement returnElement = null;
        int ObjectWaitTime;
        if (timeinsecons.length == 0) {
            ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
        } else {
            ObjectWaitTime = timeinsecons[0];
        }
        loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.driver)
                .withTimeout(Duration.ofSeconds(ObjectWaitTime)).pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);

        try {
            returnElement = fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            loggerUtils.logException(description, e, true);
            returnElement = null;
        }
        return returnElement;
    }

    public WebElement fluentWaitForElementToBeClickable(By by, String description, int... maxWaitTimeInSecond) {
        WebElement element = null;
        int ObjectWaitTime;
        if (maxWaitTimeInSecond.length == 0) {
            ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
        } else {
            ObjectWaitTime = maxWaitTimeInSecond[0];
        }
        loggerUtils.logComment("Wait for element '" + description + "' to be clickable on the page.");
        Wait<WebDriver> fluentWait = new FluentWait<>(configInstance.getDriver())
                .withTimeout(Duration.ofSeconds(ObjectWaitTime)).pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);
        try {
            element = fluentWait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            loggerUtils.logException(description, e, true);
            element = null;
        }
        return element;
    }

    public void waitForPopup(int pollTime) {
        int threshold = 5;
        for (int i = 0; i < pollTime; i++) {
            if (popupUtils.isAlertPresent()) {
                popupUtils.ok();
                loggerUtils.logComment("Alert closed successfully");
                break;
            }
            wait(threshold);
        }
    }

    public void waitForUrlToDisplay(String expectedUrl, int timeInSeconds) {
        int count = 0;
        while (!configInstance.getDriver().getCurrentUrl().equals(expectedUrl) && count < timeInSeconds) {
            count += 1;
        }
    }

    public boolean waitForElementToLoad(How how, String what, String description, int objectWaitTime) {
        {
            loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
            By by = null;
            boolean visibilityStatus = true;
            WebElement returnElement = null;
            switch (how) {
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
                    loggerUtils.logFail("Invalid identification method is passed");
            }
            try {
                waitForJStoLoad();
                WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), objectWaitTime);
                wait.until(ExpectedConditions.visibilityOfElementLocated(by));
                returnElement = configInstance.getDriver().findElement(by);
                if (returnElement != null)
                    loggerUtils.logComment("Element is visible now.");
                else
                    visibilityStatus = false;
            } catch (Exception e) {
                visibilityStatus = false;
                loggerUtils.logWarning("Element is not visible");
            }
            return visibilityStatus;
        }
    }

    public boolean waitForElementToLoad(How how, String what, String description) {
        int ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
        return waitForElementToLoad(how, what, description, ObjectWaitTime);
    }

    public boolean waitForElementToLoad(By by, int maxWaitTimeInSecond, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
        WebElement returnElement = null;
        boolean visibilityStatus = true;
        try {
            waitForJStoLoad();
            WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), maxWaitTimeInSecond);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            loggerUtils.logComment(description + " is visible now.");
        } catch (Exception ex) {
            visibilityStatus = false;
            loggerUtils.logComment("Element is not visible");
            loggerUtils.logFailureException(ex);
        }
        return visibilityStatus;
    }

}
