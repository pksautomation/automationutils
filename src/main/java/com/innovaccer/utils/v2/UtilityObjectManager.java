package com.innovaccer.utils.v2;

import com.innovaccer.utils.v2.dataHelper.TestDataHelper;

public class UtilityObjectManager {


    Config configInstant;
    public Config getConfigInstant() {
		return configInstant;
	}

	public void setConfigInstant(Config configInstant) {
		this.configInstant = configInstant;
	}

	private AssertionUtils AssertionUtils;
    private BrowserUtils BrowserUtils;
    private TestDataHelper testDataHelper;
    private WaitHelper WaitUtils;

    public WaitHelper getWaitUtils() {
		return WaitUtils;
	}

	public void setWaitUtils(WaitHelper waitUtils) {
		WaitUtils = waitUtils;
	}

	public UtilityObjectManager(Config config) {
        init(config);
    }

    public UtilityObjectManager() {
        init(Config.getConfig());
    }

    private void init(Config config) {
        this.BrowserUtils = new BrowserUtils(config);
        this.testDataHelper = new TestDataHelper(config);
        this.WaitUtils = new WaitHelper(config);
        //this.LoggerUtils = new LoggerUtils(config);
        this.AssertionUtils = new AssertionUtils(config);
    }

    public TestDataHelper getTestDataHelper() {
        return testDataHelper;
    }

    public void setTestDataHelper(TestDataHelper testDataHelper) {
        this.testDataHelper = testDataHelper;
    }

    public AssertionUtils getAssertionUtils() {
        return AssertionUtils;
    }

    public void setAssertionUtils(AssertionUtils assertionUtils) {
        AssertionUtils = assertionUtils;
    }

    public BrowserUtils getBrowserUtils() {
        return BrowserUtils;
    }

    public void setBrowserUtils(BrowserUtils browserUtils) {
        BrowserUtils = browserUtils;
    }

//    public LoggerUtils getLoggerUtils() {
//        return LoggerUtils;
//    }
//
//    public void setLoggerUtils(LoggerUtils loggerUtils) {
//        LoggerUtils = loggerUtils;
//    }

}