package com.innovaccer.utils.v2;

import com.innovaccer.utils.v2.dataHelper.pageobject.How;
import com.innovaccer.utils.v2.dataHelper.pageobject.PageObjectHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    private Config scenarioContext;
    private WaitHelper WaitUtils = null;
    private UtilityObjectManager UtilityObjectManager = null;
    private LoggerUtils LoggerUtils;
    private BrowserUtils browserUtils;
    private WebDriver driver;
    private ElementActionsUtils Actions;
    private PageObjectHelper PageObjectHelper;

    public BasePage(Config scenariosInstance) {
        init(scenariosInstance);
    }

    public BasePage() {
        init(Config.getConfig());
    }

    public PageObjectHelper getPageObjectHelper() {
        return PageObjectHelper;
    }

    public void setPageObjectHelper(PageObjectHelper pageObjectHelper) {
        this.PageObjectHelper = pageObjectHelper;
    }

    private void init(Config scenarioInstance) {
        this.scenarioContext = scenarioInstance;
        this.UtilityObjectManager = new UtilityObjectManager(scenarioInstance);
        WaitUtils = new WaitHelper(scenarioContext);
        LoggerUtils = new LoggerUtils(scenarioContext);
        driver = scenarioContext.driver;
        Actions = new ElementActionsUtils(scenarioContext);
        browserUtils = new BrowserUtils(scenarioContext);
        PageObjectHelper = new PageObjectHelper(scenarioContext);
        PageObjectHelper.initPage(this.getInstantClassName());
        PageFactory.initElements(scenarioInstance.driver, this);
    }

    public String getInstantClassName() {
        String className = this.getClass().getSimpleName();
        return className;
    }

    public BrowserUtils getBrowserUtils() {
        return browserUtils;
    }


    public void setBrowserUtils(BrowserUtils browserUtils) {
        this.browserUtils = browserUtils;
    }


    public Config getScenarioContext() {
        return scenarioContext;
    }

    public void setScenarioContext(Config scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    public WaitHelper getWaitUtils() {
        return WaitUtils;
    }

    public void setWaitUtils(WaitHelper waitUtils) {
        WaitUtils = waitUtils;
    }

    public UtilityObjectManager getUtilityObjectManager() {
        return UtilityObjectManager;
    }

    public void setUtilityObjectManager(UtilityObjectManager utilityObjectManager) {
        UtilityObjectManager = utilityObjectManager;
    }

    public LoggerUtils getLoggerUtils() {
        return LoggerUtils;
    }

    public void setLoggerUtils(LoggerUtils loggerUtils) {
        LoggerUtils = loggerUtils;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public ElementActionsUtils getActions() {
        return Actions;
    }

    public void setActions(ElementActionsUtils actions) {
        Actions = actions;
    }

    /**
     * @param locatorKey
     * @return
     */
    public How getHow(String locatorKey) {
        String key = this.getInstantClassName();
        if (Config.locatorPageWiseData.containsKey(key))
            return Config.locatorPageWiseData.get(key).get(locatorKey);
        else
            return null;
    }


}
