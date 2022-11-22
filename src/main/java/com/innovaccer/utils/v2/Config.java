package com.innovaccer.utils.v2;

import com.epam.healenium.SelfHealingDriver;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.v2.dataHelper.ExcelDataReader;
import com.jayway.restassured.response.Response;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import cucumber.api.Scenario;
import pojo.How;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

public class Config {

	public static  ThreadLocal<Config[]> threadLocalConfig;
    public ConfigSingleton singleton_data_instance=null;
    private static String fileSeparator = File.separator;
    private  String scenarioName;
    private  String featureName;
    private  LoggerUtils loggerUtils;
    private boolean endExecutionOnfailure = false;
    private boolean debugMode = false;
    private SelfHealingDriver driver;
    private SelfHealingDriver defaultBrowser;
    private SelfHealingDriver tempDriver;
    private WebDriver delegatedriver;
    private String downloadPath = null;
    private boolean enableScreenshot = true;
    private boolean isFailScenarioStatus = false;
    private Scenario scenario = null;
    private SoftAssert softAssert;
	private  boolean recordPageHTMLOnFailure = false;
	private boolean isLogsModeForException=false;
	private  boolean logsMode = true;
	private  boolean logsModeForException = false;
	private  boolean islogExceptionSkip = false;     
	private  boolean takeScreenShotOfPage = false;
	private  boolean logToStandardOut = true;
	private String testLog;
    private Scenario testScenario;
    private boolean testResult;
    private Response apiResponse = null;
    private StringBuilder authorizationToken;
    private String previousPage = "";
    private SessionId session = null;
    private String uniqueId = null;
	private String timeStamp = null;
    Properties runtimeProperties;
    ExcelDataReader testDataReaderObj;
    String testEndTime;
    String testStartTime;
    private List<String> listOfFailedStep;
    private List<String> listOfLogsOfEachFailedStep;
    private int stepNumber = 0;
    private boolean isNewDBInstance =false;
	private WaitHelper waitHelper;
    private UtilityObjectManager UtilityObjectManager=null;

	/**
     * Load Config
     *
     * @param configPath
     * @param scenario
     * @author pramod.singh
     */
    public Config(String configPath, Scenario scenario) {
    	boolean isRemoteExecution=false;
        this.runtimeProperties = new Properties();
        try {
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPropertiesFile(configPath);
        
        /**
         * need to decide location for downloaded file corresponding to each test scenarios
         */
        downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator + scenario.getName().replaceAll("[^a-zA-Z0-9]+", "");
        boolean status = Helper.createFolder(downloadPath);
        if (status) {
            downloadPath = downloadPath + fileSeparator;
        } else {
            System.out.println("Something went Wrong.!! Error in Creating Folder -" + downloadPath + " switching to predefined download Path - " + System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator);
            downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator;
        }
        
        scenarioName = scenario.getName();
        String rawFeatureName = scenario.getId().split(";")[0].replace("-", "_");
        featureName = rawFeatureName.substring(0, 1).toUpperCase() + rawFeatureName.substring(1);
        
        initInfo();
    }

    public Config(String... configPath) {
        String defaultConfigPath = System.getProperty("user.dir") + File.separator
                + "src/test/resources/Config/config.properties";
        this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
                Helper.generateRandomAlphaNumericString(5) + "-" +
                Helper.generateRandomAlphaNumericString(4);
        this.runtimeProperties = new Properties();
        try {
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        defaultConfigPath = configPath.length > 0 ? configPath[0] : defaultConfigPath;
        loadPropertiesFile(defaultConfigPath);
        initInfo();

    }
    
    private void initInfo() {
		boolean isRemoteExecution;
        endExecutionOnfailure = false;
        String BrowserName=null;
		
		boolean isBrowserInHeadlessMode=false;
		String privateKey=null;
		String encryptedKey=null;
		String browserVersion=null;
		
		singleton_data_instance=ConfigSingleton.getInstance();
		this.testResult = true;
	    this.testLog = "";
	    this.softAssert = new SoftAssert();
	    this.testScenario = scenario;
	    UtilityObjectManager = new UtilityObjectManager(this);
	    loggerUtils = new LoggerUtils(this);
	    this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
	                Helper.generateRandomAlphaNumericString(5) + "-" +
	                Helper.generateRandomAlphaNumericString(4);
        
		if (this.getIsHeadLessModeFromRunTimeProperty() != null && !this.getIsHeadLessModeFromRunTimeProperty().isEmpty()) {
            isBrowserInHeadlessMode = this.getIsHeadLessModeFromRunTimeProperty().equalsIgnoreCase("true");
        } else {
            isBrowserInHeadlessMode = getRunTimeProperty("isHeadlessMode").equalsIgnoreCase("true");
        }
        if (this.getLogModeFromRunTimeProperty() != null && !this.getLogModeFromRunTimeProperty().isEmpty()) {
            logsMode = this.getLogModeFromRunTimeProperty().equalsIgnoreCase("true");
        }
        
        this.logToStandardOut = getRunTimeProperty("LogToStandardOut")!=null && getRunTimeProperty("LogToStandardOut").equalsIgnoreCase("true");
        this.recordPageHTMLOnFailure =  getRunTimeProperty("RecordPageHTMLOnFailure")!=null && getRunTimeProperty("RecordPageHTMLOnFailure").equalsIgnoreCase("true");
        this.debugMode = getRunTimeProperty("DebugMode") != null && getRunTimeProperty("DebugMode").equalsIgnoreCase("true");

        String testDataSheet = System.getProperty("user.dir") + getRunTimeProperty("TestDataSheet");
        if (debugMode)
            loggerUtils.logComment("Test data sheet is:-" + testDataSheet);
        putRunTimeProperty("TestDataSheet", testDataSheet);
        isRemoteExecution = getRunTimeProperty("RemoteExecution") != null && getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true");
        privateKey=this.getRunTimeProperty("privateKey");
        encryptedKey=this.getRunTimeProperty("encryptionKey");
        if(this.getBrowserNameFromRunTimeProperty()!=null)
        	BrowserName = this.getBrowserNameFromRunTimeProperty();
        if(this.getRunTimeProperty("BrowserVersion")!=null)
        	browserVersion=this.getRunTimeProperty("BrowserVersion");
        this.setPrivateKey(privateKey);
        this.setEncryptionKey(encryptedKey);
        this.setRemoteExecution(isRemoteExecution);
        this.setBrowserInHeadlessMode(isBrowserInHeadlessMode);
        this.setBrowserName(BrowserName);
        this.setBrowserVersion(browserVersion);
	}

    public static Config getConfig() {
        return threadLocalConfig.get()[0];
    }

    /**
     * Replaces the arguments like {$someArg} present in input string with its value
     * from RuntimeProperties
     *
     * @param input string in which some Arguments are present
     * @return replaced string
     * @author i0465
     */
    public static String replaceArgumentsWithRunTimeProperties(String input) {
        String value = null;
        if (input.contains("{$set:"))
            return input;
        if (input.contains("{$")) {
            int index = input.indexOf("{$");
            String key = input.substring(index + 2, input.indexOf("}", index + 2));
            value = getConfig().getRunTimeProperty(key);
            input = input.replace("{$" + key + "}", value);
            return replaceArgumentsWithRunTimeProperties(input);
        } else {
            return input;
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<JSONObject> getJSONArrayListFromRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        ArrayList<JSONObject> value;
        try {
            value = (ArrayList<JSONObject>) runtimeProperties.get(keyName);
            if (debugMode)
                loggerUtils.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode) {
                loggerUtils.logComment(e.toString());
                loggerUtils.logComment("'" + key + "' not found in Run Time Properties");
            }
            return null;
        }
        return value;
    }

    public Object getObjectRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        Object value = "";
        try {
            value = runtimeProperties.get(keyName);
            if (debugMode)
                loggerUtils.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode) {
                loggerUtils.logFailureException(e);
                loggerUtils.logComment("'" + key + "' not found in Run Time Properties");
            }
            return null;
        }
        return value;
    }

    public String getRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        String value = "";
        try {
            value = runtimeProperties.get(keyName).toString();
            value = this.replaceArgumentsWithRunTimeProperties(value);
            if (debugMode)
                loggerUtils.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode && !keyName.equalsIgnoreCase("beforehook")) {
                loggerUtils.logFailureException(e);
                loggerUtils.logComment("'" + key + "' not found in Run Time Properties");
            }
            return null;
        }
        return value;
    }

    /* Get Scenarios Name
     * @author i0465
     */
    public String getScenarioName() {
        return scenarioName;
    }

    public boolean getTestCaseResult() {
        return testResult;
    }

    public void putJSONArrayListInRunTimeProperty(String key, ArrayList<JSONObject> table) {
        String keyName = key.toLowerCase();
        runtimeProperties.put(keyName, table);
        if (debugMode)
            loggerUtils.logComment("Putting Run-Time key-" + keyName + " value:-'" + table.toString() + "'");
    }

    public void putRunTimeProperty(String key, Object value) {
        String keyName = key.toLowerCase();
        runtimeProperties.put(keyName, value);
        if (debugMode)
            loggerUtils.logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
    }

    public void putRunTimeProperty(String key, String value) {
        String keyName = key.toLowerCase();
        runtimeProperties.put(keyName, value);
        if (debugMode) {
            loggerUtils.logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
        }
    }

    public void removeRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        if (debugMode)
            loggerUtils.logComment("Removing Run-Time key-" + keyName);
        runtimeProperties.remove(keyName);
    }

    public void loadPropertiesFile(Object file) {
        Properties property = new Properties();
        try {
            if (file instanceof String) {
                FileInputStream fis = new FileInputStream(file.toString());
                property.load(fis);
            } else if (file instanceof InputStream) {
                InputStream in = (InputStream) file;
                property.load(in);

            }
            Enumeration<Object> em = property.keys();
            while (em.hasMoreElements()) {
                String str = (String) em.nextElement();
                putRunTimeProperty(str, (String) property.get(str));
            }
        } catch (Exception e) {
            loggerUtils.logException(e.getMessage(), e);
        }
    }

    public void loadPropertiesFromMap(Map<String, String> data) {
        try {
            for (String key : data.keySet()) {
                String value = data.get(key);
                putRunTimeProperty(key, value);
            }
        } catch (Exception e) {
            loggerUtils.logFailureException(e);
        }
    }

    public String getBrowserNameFromRunTimeProperty() {
        return System.getProperty("Browser");
    }

    public String getIsHeadLessModeFromRunTimeProperty() {
        return System.getProperty("isHeadlessMode");
    }

    public String getLogModeFromRunTimeProperty() {
        return System.getProperty("logsMode");
    }

    public SelfHealingDriver getDriver() {
        return this.driver;
    }
    
    
    public String getUniqueId() {
		return uniqueId;
	}
    
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
    
	public ConfigSingleton getSingleton_data_instance() {
		return singleton_data_instance;
	}

	public void setSingleton_data_instance() {
		this.singleton_data_instance = ConfigSingleton.getInstance();
	}

	public void setThreadLocalConfig(ThreadLocal<Config[]> threadLocalConfig) {
		this.threadLocalConfig = threadLocalConfig;
	}

    public String getBrowserName() {
		return singleton_data_instance.getBrowserName();
	}
    
    public void setBrowserName(String browserName) {
		 singleton_data_instance.setBrowserName(browserName);
	}

	public String getEnvironment() {
		return singleton_data_instance.getEnvironment();
	}
	public void setEnvironment(String envronment) {
		singleton_data_instance.setEnvironment(envronment);
	}

	public String getPlatformName() {
		return singleton_data_instance.getPlatformName();
	}

	public String getSharedDirectory() {
		return singleton_data_instance.getSharedDirectory();
	}

	public void setSharedDirectory(String sharedDirectory) {
		singleton_data_instance.setSharedDirectory(sharedDirectory);
	}

	public String getProjectName() {
		return singleton_data_instance.getPlatformName();
	}

	public void setProjectName(String projectName) {
		singleton_data_instance.setProjectName(projectName);
	}

	public String getBrowserVersion() {
		return singleton_data_instance.getBrowserVersion();
	}

	public void setBrowserVersion(String browserVersion) {
		singleton_data_instance.setBrowserVersion(browserVersion);
	}


	public boolean isRemoteExecution() {
		return singleton_data_instance.isRemoteExecution();
	}

	public void setRemoteExecution(boolean remoteExecution) {
		singleton_data_instance.setRemoteExecution(remoteExecution);
	}

	public String getRemoteURL() {
		return singleton_data_instance.getRemoteURL();
	}

	public void setRemoteURL(String remoteURL) {
		singleton_data_instance.setRemoteURL(remoteURL);
	}

	public boolean isBrowserInHeadlessMode() {
		return singleton_data_instance.isBrowserInHeadlessMode();
	}

	public void setBrowserInHeadlessMode(boolean isBrowserInHeadlessMode) {
		singleton_data_instance.setBrowserInHeadlessMode(isBrowserInHeadlessMode);
	}

	public boolean isRecordPageHTMLOnFailure() {
		return this.isRecordPageHTMLOnFailure();
	}

	public void setRecordPageHTMLOnFailure(boolean recordPageHTMLOnFailure) {
		this.setRecordPageHTMLOnFailure(recordPageHTMLOnFailure);
	}

	public Connection getDBConnection() {
		return singleton_data_instance.getDBConnection();
	}

	public void setDBConnection(Connection dBConnection) {
		singleton_data_instance.setDBConnection(dBConnection);
	}
	
	public MongoDatabase getMongoAdminDatabase() {
		return singleton_data_instance.getMongoAdminDatabase();
	}

	public void setMongoAdminDatabase(MongoDatabase mongoAdminDatabase) {
		singleton_data_instance.setMongoAdminDatabase(mongoAdminDatabase);
	}

	public MongoClient getMongoClientConnection() {
		return singleton_data_instance.getMongoClientConnection();
	}

	public void setMongoClientConnection(MongoClient mongoClientConnection) {
		singleton_data_instance.setMongoClientConnection(mongoClientConnection);
	}
	
	public String getEncryptionKey() {
		return singleton_data_instance.getEncryptionKey();
	}

	public void setEncryptionKey(String encryptionKey) {
		singleton_data_instance.setEncryptionKey(encryptionKey);;
	}

	public String getPrivateKey() {
		return singleton_data_instance.getPrivateKey();
	}

	public void setPrivateKey(String privateKey) {
		singleton_data_instance.setPrivateKey(privateKey);
	}

//	public HashMap<String, ExcelDataReader> getTestDataReaderHashMap() {
//		return singleton_data_instance.getTestDataReaderHashMap();
//	}

	public HashMap<String, HashMap<String, String>> getTestData() {
		return singleton_data_instance.getTestData();
	}
	
	public void storeTestData(String testDataName,HashMap<String, String> testDataValue) {
		 singleton_data_instance.putTestData(testDataName, testDataValue);
	}


	public Map<String, Map<String, How>> getPagesLocatorData() {
		return singleton_data_instance.getPagesLocatorData();
	}
	
	public void storePagesLocatorData(String pageName,Map<String, How> mapOflocatorData) {
		 singleton_data_instance.putPagesLocatorData(pageName, mapOflocatorData);
	}


	public SelfHealingDriver getDefaultBrowser() {
		return defaultBrowser;
	}

	public void setDefaultBrowser(SelfHealingDriver defaultBrowser) {
		this.defaultBrowser = defaultBrowser;
	}

	public SelfHealingDriver getTempDriver() {
		return tempDriver;
	}

	public void setTempDriver(SelfHealingDriver tempDriver) {
		this.tempDriver = tempDriver;
	}

	public WebDriver getDelegatedriver() {
		return delegatedriver;
	}

	public void setDelegatedriver(WebDriver delegatedriver) {
		this.delegatedriver = delegatedriver;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public List<String> getListOfFailedStep() {
		return listOfFailedStep;
	}

	public void setListOfFailedStep(List<String> listOfFailedStep) {
		this.listOfFailedStep = listOfFailedStep;
	}

	public List<String> getListOfLogsOfEachFailedStep() {
		return listOfLogsOfEachFailedStep;
	}

	public void setListOfLogsOfEachFailedStep(List<String> listOfLogsOfEachFailedStep) {
		this.listOfLogsOfEachFailedStep = listOfLogsOfEachFailedStep;
	}

	public boolean isFailScenarioStatus() {
		return isFailScenarioStatus;
	}

	public void setFailScenarioStatus(boolean isFailScenarioStatus) {
		this.isFailScenarioStatus = isFailScenarioStatus;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public String getTestLog() {
		return testLog;
	}

	public void setTestLog(String testLog) {
		this.testLog = testLog;
	}

	public Scenario getTestScenario() {
		return testScenario;
	}

	public void setTestScenario(Scenario testScenario) {
		this.testScenario = testScenario;
	}

	public boolean isTestResult() {
		return testResult;
	}

	public void setTestResult(boolean testResult) {
		this.testResult = testResult;
	}

	public Response getApiResponse() {
		return apiResponse;
	}

	public void setApiResponse(Response apiResponse) {
		this.apiResponse = apiResponse;
	}

	public StringBuilder getAuthorizationToken() {
		return authorizationToken;
	}

	public void setAuthorizationToken(StringBuilder authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

	public String getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	public SessionId getSession() {
		return session;
	}

	public void setSession(SessionId session) {
		this.session = session;
	}
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public void setDriver(SelfHealingDriver driver) {
		this.driver = driver;
	}
	
	public String getResultsDir() {
		return singleton_data_instance.getResultsDir();
	}
	
	public void setResultsDir(String resultDirectory) {
		singleton_data_instance.setResultsDir(resultDirectory);
	}
	
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	
	public boolean isEndExecutionOnfailure() {
		return endExecutionOnfailure;
	}

	public void setEndExecutionOnfailure(boolean endExecutionOnfailure) {
		this.endExecutionOnfailure = endExecutionOnfailure;
	}
	
    public SoftAssert getSoftAssert() {
		return softAssert;
	}

	public void setSoftAssert(SoftAssert softAssert) {
		this.softAssert = softAssert;
	}
	
	public boolean isLogsMode() {
		return logsMode;
	}

	public void setLogsMode(boolean logsMode) {
		this.logsMode = logsMode;
	}

	public boolean isLogsModeForException() {
		return logsModeForException;
	}

	public void setLogsModeForException(boolean logsModeForException) {
		this.logsModeForException = logsModeForException;
	}

	public boolean isIslogExceptionSkip() {
		return islogExceptionSkip;
	}

	public void setIslogExceptionSkip(boolean islogExceptionSkip) {
		this.islogExceptionSkip = islogExceptionSkip;
	}

	public boolean isTakeScreenShotOfPage() {
		return takeScreenShotOfPage;
	}

	public void setTakeScreenShotOfPage(boolean takeScreenShotOfPage) {
		this.takeScreenShotOfPage = takeScreenShotOfPage;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isEnableScreenshot() {
		return enableScreenshot;
	}

	public void setEnableScreenshot(boolean enableScreenshot) {
		this.enableScreenshot = enableScreenshot;
	}

	public boolean isLogToStandardOut() {
		return logToStandardOut;
	}

	public void setLogToStandardOut(boolean logToStandardOut) {
		this.logToStandardOut = logToStandardOut;
	}
	
    public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	
    public UtilityObjectManager getUtilityObjectManager() {
		return UtilityObjectManager;
	}

	public void setUtilityObjectManager(UtilityObjectManager utilityObjectManager) {
		UtilityObjectManager = utilityObjectManager;
	}
	
	
	public boolean isNewDBconnection () {
		return this.isNewDBInstance;
	}
	
	public void enableNewDBConnection () {
		this.isNewDBInstance = true;
	}

	
//	public ExcelDataReader getCachedTestDataReaderObject(String sheetName)
//	{	
//		String path = getRunTimeProperty("TestDataSheet");
//		if(sheetName.contains("."))
//		{	
//			path=System.getProperty("user.dir")+getRunTimeProperty(sheetName.split("\\.")[0]);
//			sheetName=sheetName.split("\\.")[1];
//
//		}
//		return getCachedTestDataReaderObject(sheetName, path);
//	}
//	
//	public ExcelDataReader getCachedTestDataReaderObject(String sheetName, String path)
//	{
//		ExcelDataReader obj = getTestDataReaderHashMap().get(path + sheetName);
//		// Object is not in the cache
//		if (obj == null)
//		{
//			// cache for future use
//			synchronized(Config.class)
//			{
//				cacheTestDataReaderObject(sheetName, path);
//				obj = getTestDataReaderHashMap().get(path + sheetName);
//			}
//		}
//		return obj;
//	}
	
	/**
	 * Create TestDataReader object for the given sheet and cache it can be
	 * fetched using - getCachedTestDataReaderObject()
	 * 
	 * @param sheetName
	 * @author pramod.singh
	 */
//	private void cacheTestDataReaderObject(String sheetName, String path)
//	{
//		if (getTestDataReaderHashMap().get(path + sheetName) == null)
//		{
//			testDataReaderObj = new ExcelDataReader(this, sheetName, path);
//			getTestDataReaderHashMap().put(path + sheetName, testDataReaderObj);
//		}
//	}
	

}