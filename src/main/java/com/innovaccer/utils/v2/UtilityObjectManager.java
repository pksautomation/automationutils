package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.fileutils.*;

public class UtilityObjectManager {


    private AssertionUtils AssertionUtils;
    private BrowserUtils BrowserUtils;
    private LoggerUtils LoggerUtils;
    private TestDataHelper testDataHelper;
    
	Config configInstant;

    public UtilityObjectManager(Config config) {
        init(config);
    }

	private void init(Config config) {
		this.AssertionUtils = new AssertionUtils(config);
        this.BrowserUtils = new BrowserUtils(config);
        this.LoggerUtils = new LoggerUtils(config);
        this.testDataHelper=new TestDataHelper();
	}
	
    public TestDataHelper getTestDataHelper() {
		return testDataHelper;
	}

	public void setTestDataHelper(TestDataHelper testDataHelper) {
		this.testDataHelper = testDataHelper;
	}
    
    public UtilityObjectManager() {
    	init(Config.getConfig());
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

	public LoggerUtils getLoggerUtils() {
		return LoggerUtils;
	}

	public void setLoggerUtils(LoggerUtils loggerUtils) {
		LoggerUtils = loggerUtils;
	}

}