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
    private APIHelper apiHelper;
  
	private EncryptionUtils encryptionUtils;

    private void init(Config config) {
        this.BrowserUtils = new BrowserUtils(config);
        this.testDataHelper = new TestDataHelper(config);
        this.WaitUtils = new WaitHelper(config);
        this.AssertionUtils = new AssertionUtils(config);
        this.encryptionUtils = new EncryptionUtils(config);
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
    public APIHelper getApiHelper() {
  		return apiHelper;
  	}

  	public void setApiHelper(APIHelper apiHelper) {
  		this.apiHelper = apiHelper;
  	}
  	 public EncryptionUtils getEncryptionUtils() {
 		return encryptionUtils;
 	}

 	public void setEncryptionUtils(EncryptionUtils encryptionUtils) {
 		this.encryptionUtils = encryptionUtils;
 	}
 	
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

//    public LoggerUtils getLoggerUtils() {
//        return LoggerUtils;
//    }
//
//    public void setLoggerUtils(LoggerUtils loggerUtils) {
//        LoggerUtils = loggerUtils;
//    }

}