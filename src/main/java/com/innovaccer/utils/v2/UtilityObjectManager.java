package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;

public class UtilityObjectManager {


    private AssertionHelper assertHelper;
    private BrowserHelper browserHelper;
    private LoggerHelper loggerHelper;
    private YamlHelper yamlHelper;

    public UtilityObjectManager(Config config) {
        this.assertHelper = new AssertionHelper(config);
        this.browserHelper = new BrowserHelper(config);
        this.loggerHelper = new LoggerHelper(config);
        this.yamlHelper = new YamlHelper(config);
    }

    public AssertionHelper getAssertion() {
        return this.assertHelper;
    }

    public void setAssertionHelper(AssertionHelper assertionManager) {
        this.assertHelper = assertionManager;
    }

    public BrowserHelper getBrowser() {
        return this.browserHelper;
    }

    public void setBrowser(BrowserHelper browserManager) {
        this.browserHelper = browserManager;
    }

    public LoggerHelper getLogger() {
        return this.loggerHelper;
    }

    public void setLoggerHelper(LoggerHelper loggerManager) {
        this.loggerHelper = loggerManager;
    }

    public YamlHelper getYamlHelper() {
        return this.yamlHelper;
    }

    public void setYamlHelper(YamlHelper yamlHelper) {
        this.yamlHelper = yamlHelper;
    }

}