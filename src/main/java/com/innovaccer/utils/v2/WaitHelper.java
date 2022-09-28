package com.innovaccer.utils.v2;

import com.innovaccer.utils.v2.ElementActionsUtils.How;
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

    /**
     * Wait For Visibility using By
     *
     * @param by            -> By
     * @param timeInSeconds -> time in seconds
     * @param description   -> description
     * @return webElement
     */
    public WebElement waitForVisibility(By by, int timeInSeconds, String description) {
        WebElement webElement = configInstance.getDriver().findElement(by);
        waitForVisibility(webElement, timeInSeconds, description);
        return webElement;
    }

    /**
     * Wait For Visibility using WebElement
     *
     * @param element       -> Web Element
     * @param timeInSeconds -> time in seconds
     * @param description   -> description
     */
    public void waitForVisibility(WebElement element, int timeInSeconds, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
        WebDriverWait wait = new WebDriverWait(configInstance.getDriver(), timeInSeconds);
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException tm) {
            loggerUtils.logException(description + " not found after waiting for " + timeInSeconds + " seconds", tm);
        }
    }

    /**
     * Wait For Staleness using WebElement
     *
     * @param element     -> Web Element
     * @param description -> description
     */
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

    /**
     * Wait For Invisibility using By
     *
     * @param by          -> By
     * @param description -> description
     */
    public void waitForInvisibility(By by, String description) {
        loggerUtils.logComment("Wait for element '" + description + "' to be invisible on the page.");
        Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.getDriver())
                .withTimeout(Duration.ofSeconds(ObjectWaitTime))
                .pollingEvery(Duration.ofSeconds(5));
        try {
            fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException tm) {
            loggerUtils.logException(description + " found after waiting for " + ObjectWaitTime + " seconds", tm);
        }
    }

    /**
     * Wait for Element has given Text Value
     *
     * @param element                         -> Web Element
     * @param textToBePresentInValueAttribute -> Text to match for
     * @param description                     -> description
     */
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

    /**
     * Wait for the element to disappear
     *
     * @param element -> Web Element
     */
    public void waitForElementToDisappear(WebElement element) {
        try {
            for (int i = 1; i <= 50; i++) {
                if (!(elementActionsUtils.IsElementDisplayed(element)))
                    break;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            loggerUtils.logComment("element is not present on page");
        }
    }

    /**
     * @param seconds -> Wait in Seconds
     */
    public void wait(int seconds) {
        int milliseconds = seconds * 1000;
        try {
            Thread.sleep(milliseconds);
            loggerUtils.logComment("Wait for '" + seconds + "' seconds");
        } catch (InterruptedException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * Wait for Element to be Clickable
     *
     * @param by                  -> By
     * @param description         -> Description
     * @param maxWaitTimeInSecond -> Max Wait Time in Seconds
     * @return
     */
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

    /**
     * Wait for Javascript to Load
     *
     * @return
     */
    public boolean waitForJStoLoad() {
        JavascriptExecutor javaScript = (JavascriptExecutor) configInstance.getDriver();
        WebDriverWait wait = new WebDriverWait(configInstance.getDriver(),
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

    /**
     * Fluent Wait for Element to be Visible
     *
     * @param by                    -> By
     * @param description           -> Description
     * @param maxWaitTimeInSecond-> Max Wait Time
     * @return WebElement
     */
    public WebElement fluentWaitForVisibility(By by, String description, int... maxWaitTimeInSecond) {
        WebElement returnElement = null;
        int ObjectWaitTime;
        if (maxWaitTimeInSecond.length == 0) {
            ObjectWaitTime = Integer.parseInt(configInstance.getRunTimeProperty("ObjectWaitTime"));
        } else {
            ObjectWaitTime = maxWaitTimeInSecond[0];
        }
        loggerUtils.logComment("Wait for element '" + description + "' to be visible on the page.");
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(configInstance.driver)
                .withTimeout(Duration.ofSeconds(ObjectWaitTime)).pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);

        try {
            returnElement = fluentWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            loggerUtils.logException(description, e, true);
            returnElement = null;
        }
        return returnElement;
    }

    /**
     * Fluent Wait for Element to be Clickable
     *
     * @param by                    -> By
     * @param description           -> Description
     * @param maxWaitTimeInSecond-> Max Wait Time
     * @return WebElement
     */
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

    /**
     * Wait for Pop-up
     *
     * @param pollTime -> number of times to check for pop-up
     */
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

    /**
     * Wait for Expected Url to Show
     *
     * @param expectedUrl   -> Expected Url
     * @param timeInSeconds -> Wait in Seconds
     */
    public void waitForUrlToDisplay(String expectedUrl, int timeInSeconds) {
        int count = 0;
        while (!configInstance.getDriver().getCurrentUrl().equals(expectedUrl) && count < timeInSeconds) {
            count += 1;
        }
    }

    /**
     * Wait For Element To Load
     *
     * @param how            -> locator type
     * @param what           -> value to match for
     * @param description    -> description
     * @param objectWaitTime -> wait time in seconds
     * @return Boolean to check whether WebElement is Loaded or Not
     */
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

    /**
     * Wait For Element To Load
     *
     * @param by                  -> By
     * @param maxWaitTimeInSecond -> Max Wait Time In Seconds
     * @param description         -> Description
     * @return Boolean to check whether WebElement is Loaded or Not
     */
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
