package com.innovaccer.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import cucumber.api.Scenario;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.*;
import org.json.JSONObject;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotSelectableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.epam.healenium.SelfHealingDriver;
import com.innovaccer.utils.v2.WaitHelper;
import com.innovaccer.utils.v2.dataHelper.pageobject.How;
import com.jayway.restassured.response.Response;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author pramod.singh
 *
 */


public class Config
{

	public Config testConfig;
	public enum SheetToBeUsed
	{
		Measure_GPRO_API,ValueSet,InGrpah
	}

	// parameters that can be overridden through command line and are same for
	// all executing tests
	public static ThreadLocal<Config[]> threadLocalConfig;
	public boolean endExecutionOnfailure = false;
	public boolean debugMode = false;
	public boolean recordPageHTMLOnFailure = false;
	public static String BrowserName;
	public static String Environment;
	public static String ResultsDir;
	public static String PlatformName;
	public static String SharedDirectory;
	public static String ProjectName;
	public static String BrowserVersion;
	public Connection DBConnection = null;
	public static String fileSeparator = File.separator;	
	// parameters different for every test
	public SelfHealingDriver driver;
	public SelfHealingDriver defaultBrowser;
	public SelfHealingDriver tempDriver;
	public WebDriver delegatedriver;
	public String downloadPath = null;
	public  boolean enableScreenshot = true;
	public  boolean logToStandardOut = true;
	public MongoDatabase mongoAdminDatabase=null;
	public MongoClient mongoClientConnection=null;
	// Connection objects to be used in DataBase.java
	public MongoDatabase mongoRiskDBConnection=null;
	public List<String> listOfFailedStep ;
	public List<String> listOfLogsOfEachFailedStep;
	public int stepNumber = 0;
	
	//scenarioTestStatus :  true means pass and false means fail 
	public boolean isFailScenarioStatus = false;
	public Scenario scenario=null;

	// stores the run time properties (different for every test)
	Properties runtimeProperties;

	public SoftAssert softAssert;
	//////////////////
	public static HashMap<String, TestDataReader> testDataReaderHashMap = new HashMap<String, TestDataReader>();
	
	public static HashMap<Integer, HashMap<String, String>> genericErrors = new HashMap<Integer, HashMap<String, String>>();
	///
	public static HashMap<String, HashMap<String, String>> testData = new HashMap<String, HashMap<String, String>>();
	
	TestDataReader testDataReaderObj;
	String testEndTime;
	public String testLog;
	public static String scenarioName;
	public static String featureName;
	public Scenario testScenario;
	public boolean testResult;
	public Response apiResponse=null;
	public StringBuilder authorizationToken;
	public static boolean remoteExecution=false;
	public static String remoteURL;
	public static boolean isBrowserInHeadlessMode=false;
	public static boolean logsMode=true;
	public static boolean logsModeForException=false;
	public static boolean takeScreenShotOfPage = false;
	public static Map<String,Map<String,How>> locatorsDataPageWise = new HashMap<String,Map<String,How>>();

	// package fields
	String testStartTime;

	public String previousPage="";
	public SessionId session=null;
	public String uniqueId = null;
	public String timeStamp = null;
	public String encryptionKey = "amVxSX10V0ppZHlJal1qXHx3Z1x+Vw0N";
	public String privateKey="010100000000000";
	
	private WaitHelper waitHelper;

	/**
	 * Load Config
	 * @param configPath
	 * @param scenario
	 * @author pramod.singh
	 */
	public Config(String configPath,Scenario scenario)
	{
		//endExecutionOnfailure = true;
		this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
				Helper.generateRandomAlphaNumericString(5) + "-" +
				Helper.generateRandomAlphaNumericString(4);
		this.testResult = true;
		this.DBConnection = null;
		this.testLog = "";
		this.softAssert = new SoftAssert();
		this.testScenario = scenario;

		this.runtimeProperties = new Properties();

		// load AutomationUtil Config.properties File 
		try {
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/elasticConfig.properties"));
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/mongoConfig.properties"));
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/SQLDBConfig.properties"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		// Read and load the above specified properties file in constructor
		loadPropertiesFile(configPath);

		this.debugMode = (getRunTimeProperty("DebugMode").toLowerCase().equals("true")) ? true : false;
		this.logToStandardOut = (getRunTimeProperty("LogToStandardOut").toLowerCase().equals("true")) ? true : false;
		this.recordPageHTMLOnFailure = (getRunTimeProperty("RecordPageHTMLOnFailure").toLowerCase().equals("true")) ? true : false;


		//Set feature name and scenarios name
		this.scenarioName = scenario.getName();
		String rawFeatureName = scenario.getId().split(";")[0].replace("-","_");
		this.featureName = rawFeatureName.substring(0, 1).toUpperCase() + rawFeatureName.substring(1);


		// Set the full path of test data sheet
		String testDataSheet = System.getProperty("user.dir") + getRunTimeProperty("TestDataSheet");
		if (debugMode)
			logComment("Test data sheet is:-" + testDataSheet);
		putRunTimeProperty("TestDataSheet", testDataSheet);
		
		this.remoteExecution = getRunTimeProperty("RemoteExecution") != null && getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true") ? true : false;

		endExecutionOnfailure = false;


		/**
		 * need to decide location for downloaded file corresponding to each test scenarios
		 */

		if(scenario != null)
		{
			downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator + scenario.getName().replaceAll("[^a-zA-Z0-9]+","");


			boolean status = Helper.createFolder(downloadPath);

			if(status)
			{
				downloadPath = downloadPath + fileSeparator;
			}
			else
			{
				System.out.println("Something went Wrong.!! Error in Creating Folder -" + downloadPath + " switching to predefined download Path - " + System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator);
				downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator;
			}
		}
		
		this.BrowserName = this.getBrowserNameFromRunTimeProperty();
		//System.out.println("==========================   browser Name =========" + this.BrowserName );
		
		if(this.getIsHeadLessModeFromRunTimeProperty() != null && !this.getIsHeadLessModeFromRunTimeProperty().isEmpty()) {
			this.isBrowserInHeadlessMode = this.getIsHeadLessModeFromRunTimeProperty().equalsIgnoreCase("true")? true:false;
		}
		else {
			this.isBrowserInHeadlessMode =getRunTimeProperty("isHeadlessMode").equalsIgnoreCase("true")? true:false;
		}
		
		if(this.getLogsModeFromRunTimeProperty() != null && !this.getLogsModeFromRunTimeProperty().isEmpty()) {
			this.logsMode = this.getLogsModeFromRunTimeProperty().equalsIgnoreCase("true")? true:false;
		}
		
		this.putRunTimeProperty("privateKey", this.privateKey);
		this.putRunTimeProperty("encryptionKey", this.encryptionKey);
		
	}

	//@Attachment(value = "Logs For \"{0}\"", type = "text/html")
	public String attachLogs(String testName)
	{
		return this.testLog;
	}

	/**
	 * Create TestDataReader object for the given sheet and cache it can be
	 * fetched using - getCachedTestDataReaderObject()
	 * 
	 * @param sheetName
	 * @author pramod.singh
	 */
	private void cacheTestDataReaderObject(String sheetName, String path)
	{
		if (testDataReaderHashMap.get(path + sheetName) == null)
		{
			testDataReaderObj = new TestDataReader(this, sheetName, path);
			testDataReaderHashMap.put(path + sheetName, testDataReaderObj);
		}
	}
	
	/**
	 * Open Browser
	 * @author pramod.singh
	 */
	public void openBrowser()
	{
		int retryCnt = 10;
		while (this.driver == null && retryCnt > 0)
		{
			try
			{
				this.driver = Browser.openBrowser(this);

			}
			catch (Exception e)
			{
				Log.Warning("Retrying the browser launch:-" + e.toString(), this);
				System.out.println(e.toString());
			}
			if (this.driver == null)
			{
				retryCnt--;
				if (retryCnt == 0)
				{
					logFail("Browser could not be opened for : "+getScenarioName());
					Assert.assertTrue(false);
				}
				Browser.wait(this, 2);
			}

		}
		endExecutionOnfailure = false;
	}
	
	/* Close the browser
	 * @author pramod.singh
	 */
	public void closeBrowser()
	{
		logToStandardOut = true;

		Browser.quitBrowser(this);
		driver = null;
	}

	/**
	 * Get the cached TestDataReader Object for the given sheet. If it is not
	 * cached, it will be cached for future use
	 * 
	 * To read datasheet other than TestDataSheet, pass filename and sheet name separated by dot (i.e filename.sheetname)
	 * 
	 * @param sheetName
	 * @return TestDataReader object or null if object is not in cache
	 * @author pramod.singh
	 */
	public TestDataReader getCachedTestDataReaderObject(String sheetName)
	{	
		String path = getRunTimeProperty("TestDataSheet");
		if(sheetName.contains("."))
		{	
			path=System.getProperty("user.dir")+getRunTimeProperty(sheetName.split("\\.")[0]);
			sheetName=sheetName.split("\\.")[1];

		}
		return getCachedTestDataReaderObject(sheetName, path);
	}


	/**
	 * Get the cached TestDataReader Object for the given sheet in the excel
	 * specified by path. If it is not cached, it will be cached for future use
	 * 
	 * @param sheetName
	 * @param path
	 *            Path of excel sheet to read
	 *  @author pramod.singh
	 * @return TestDataReader object or null if object is not in cache
	 */
	public TestDataReader getCachedTestDataReaderObject(String sheetName, String path)
	{
		TestDataReader obj = testDataReaderHashMap.get(path + sheetName);
		// Object is not in the cache
		if (obj == null)
		{
			// cache for future use
			synchronized(Config.class)
			{
				cacheTestDataReaderObject(sheetName, path);
				obj = testDataReaderHashMap.get(path + sheetName);
			}
		}
		return obj;
	}

	/**
	 * Get the Run Time Property value
	 * 
	 * @param key
	 *            key name whose value is needed
	 *            @author pramod.singh
	 * @return value of the specified key
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<JSONObject> getJSONArrayListFromRunTimeProperty(String key)
	{
		String keyName = key.toLowerCase();
		ArrayList<JSONObject> value;
		try
		{
			value = (ArrayList<JSONObject>) runtimeProperties.get(keyName);
			if (debugMode)
				logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
		}
		catch (Exception e)
		{
			if (debugMode)
			{
				logComment(e.toString());
				logComment("'" + key + "' not found in Run Time Properties");
			}
			return null;
		}
		return value;
	}

	/**
	 * Get the Run Time Property value
	 * 
	 * @param key
	 *            key name whose value is needed
	 * @return value of the specified key
	 * @author pramod.singh
	 */
	public Object getObjectRunTimeProperty(String key)
	{
		String keyName = key.toLowerCase();
		Object value = "";
		try
		{
			value = runtimeProperties.get(keyName);
			if (debugMode)
				logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
		}
		catch (Exception e)
		{
			if (debugMode)
			{
				logComment(e.toString());
				logComment("'" + key + "' not found in Run Time Properties");
			}
			return null;
		}
		return value;
	}

	/**
	 * Refreshes the cache for the given sheet in excel, and gets TestDataReader
	 * Object Also it will be cached for future use
	 * 
	 * @param sheetName
	 * @param path
	 *            Path of excel sheet to read
	 *            @author i0465
	 * @return TestDataReader object or null if object is not in cache
	 */
	public TestDataReader getRefreshedTestDataReaderObject(String sheetName, String path)
	{
		TestDataReader obj = new TestDataReader(this, sheetName, path);
		// cache for future use
		testDataReaderHashMap.put(path + sheetName, obj);
		obj = testDataReaderHashMap.get(path + sheetName);

		return obj;
	}

	/**
	 * Get the Run Time Property value
	 * 
	 * @param key
	 *            key name whose value is needed
	 * @return value of the specified key
	 * @author i0465
	 */
	
	public String getRunTimeProperty(String key)
	{
		String keyName = key.toLowerCase();
		String value = "";
		try
		{
			value = runtimeProperties.get(keyName).toString();
			value = Helper.replaceArgumentsWithRunTimeProperties(this, value);
			if (debugMode)
				logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
		}
		catch (Exception e)
		{
			if (debugMode && !keyName.equalsIgnoreCase("beforehook"))
			{
				logComment(e.toString());
				logComment("'" + key + "' not found in Run Time Properties");
			}

			return null;
		}
		return value;
	}
	
	/* Get Scenarios Name
	 * @author i0465
	 */
	public String getScenarioName()
	{
		return this.scenarioName;
	}

	public boolean getTestCaseResult()
	{
		return testResult;
	}
	/**
	 * 
	 * @param message
	 * @author pramod.singh
	 */
	public void logComment(String message)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		Log.Comment(message, this);
	}

	public void logHighLight(String message)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		Log.Comment(message, this, "HotPink");
	}
	

    //logExceptionSkip :  true means pass and false means fail 
   public boolean islogExceptionSkip = false;
   
	/**
	 * 
	 * @param e
	 * @author i0465
	 */
	public void logException(Throwable e) {
			this.logException("", e);
	}
   
	/**
	 * Log Exception But Skip Failure
	 * @param message
	 * @param e
	 * @param IsTakeScreenShot
	 * @author pramod.singh
	 */
	public void logExceptionSkipFailure(String message , Throwable e , boolean ...IsTakeScreenShot) {
		 this.islogExceptionSkip = true;
		 if(IsTakeScreenShot.length != 0)
			 if(IsTakeScreenShot[0])
				 this.logException(message, e, true);
		else
			this.logException(message, e, false);
		 
		 this.islogExceptionSkip = false;
	}
	/**
	 * Function For Custom Exception with Capture Screenshot
	 * @param message
	 * @param throwable
	 * @param pageLogInfo optionnal
	 * @author ranjeet
	 */
	public void logException(String message , Throwable e , boolean ...IsTakeScreenShot)
	{
		String errorFilePath = "";
		testResult = false;
		this.logsModeForException=true;
		StringBuffer stbr = new StringBuffer();
		stbr.append("Error location:- ");
		
		StackTraceElement[] s =  e.getStackTrace();
		for (StackTraceElement ss  : s ) {
			if(ss.getClassName().startsWith("com.innovaccer")) {
				errorFilePath  = ss.getClassName()+":"+ss.getLineNumber();
				stbr.append(errorFilePath+"\n");
			}
		}
		 
		switch (e.getClass().getSimpleName()) {
		
		case "IllegalArgumentException":
 			message = message.concat("\n Illegal Argument Exception : ").concat(((IllegalArgumentException) e).getMessage().split("\n")[0].concat("\n"+stbr.toString()));
 			break;
		case "ElementClickInterceptedException":
			 message = message.concat("\nElement Click Not Intercepted On Page : ").concat(((ElementClickInterceptedException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "NoSuchElementException":
			message = message.concat("\nElement Not Available On Page : ").concat(((NoSuchElementException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "NoSuchWindowException":
			 message = message.concat("\nNo Such Window Available : ").concat(((NoSuchWindowException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			 break;
		case "NoSuchFrameException":
			message = message.concat("\nNo Such Frame Available : ").concat(((NoSuchFrameException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "NoAlertPresentException":
			message = message.concat("\nNo Alert Present On This Page : ").concat(((NoAlertPresentException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "InvalidSelectorException":
			message = message.concat("\nInvalid Selector By Using Locator : ").concat(((InvalidSelectorException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "ElementNotVisibleException":
			 message = message.concat("\nElement Not Selectable By Using Locator : ").concat(((ElementNotVisibleException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			 break;
		case "ElementNotSelectableException":
			message = message.concat("\nElement Not Selectable By Using Locator : ").concat(((ElementNotSelectableException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "TimeoutException":
			message = message.concat("\nTimeout Occur By Using Locator : ").concat(((TimeoutException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "NoSuchSessionException":
			message = message.concat("\nNo Such Session Exception Occur : ").concat(((NoSuchSessionException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "StaleElementReferenceException":
			message = message.concat("\nStale Element Reference Occur By Using Locator : ").concat(((StaleElementReferenceException) e).getMessage().split("\n")[0]).concat("\n"+stbr.toString());
			break;
		case "AssertionError" :
			 message = message.concat("\n Assertion Error : ").concat(e.getMessage()).concat("\n"+stbr.toString());;
			break;
		default:
			//message = message.concat("\n Java Exception : "+ExceptionUtils.getFullStackTrace(e)).concat("\n"+stbr.toString());
			message = message.concat("\n Java Exception : "+e.getMessage()).concat("\n"+stbr.toString());;
			break;
		}

		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Exception] --> " + message;
		 	
		if (!islogExceptionSkip) {
			if (IsTakeScreenShot.length == 0) {
				Log.Fail(message, this);
			} else if (IsTakeScreenShot[0]) {
				Log.Fail(message, this);
				Log.embedMessageAsHTMLInReport(this, message);
			} else if (!IsTakeScreenShot[0]) {
				Log.failure(message, this);
				Log.embedMessageAsHTMLInReport(this, message);
			}
		}else {
			if (IsTakeScreenShot.length == 0) {
				Log.Comment(message, this);
			} else if (IsTakeScreenShot[0]) {
				Log.Comment(message, this);
				if (this.getRunTimeProperty("LogPageInfo") != null && (this.getRunTimeProperty("LogPageInfo").equalsIgnoreCase("true"))) 
					Log.PageInfo(this);
				Log.embedMessageAsHTMLInReport(this, message);
			} else if (!IsTakeScreenShot[0]) {
				Log.Comment(message, this);
				Log.embedMessageAsHTMLInReport(this, message);
			}
		}
	
		stbr.delete(0, stbr.length());
		this.logsModeForException=false;
	}
	
	/**
	 * 
	 * @param message
	 * @param isLoadPageinfo
	 * @author pramod.singh
	 */
	public void logFail(String message,boolean ...isLoadPageinfo)
	{
		testResult = false;
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		Log.Fail(message, this);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param what
	 * @param expected
	 * @param actual
	 * @author pramod.singh
	 */
	public <T> void logFail(String what, T expected, T actual)
	{
		testResult = false;
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		String message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "][Fail] -->  Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Fail(message, this);
	}
	/**
	 * 
	 * @param what
	 * @param expected
	 * @param actual
	 * @author pramod.singh
	 */
	public void logFail(String what, String expected, String actual)
	{
		testResult = false;
		String projectName = this.getRunTimeProperty("ProjectName");
		String message = null;

		if(projectName != null && projectName.equalsIgnoreCase("acs"))
			message = " [Fail] --> Expected '" + what + "' was :-\n<br/>" + StringUtils.replaceEach(expected, new String[] { "&", "\"", "<", ">" }, 
					new String[] { "&amp;", "&quot;", "&lt;", "&gt;" }).replace("&lt;br/&gt;", "\n<br/>") + ".\n<br/>But actual is :-\n<br/>" + StringUtils.replaceEach(actual, new String[] { "&", "\"", "<", ">" }, 
							new String[] { "&amp;", "&quot;", "&lt;", "&gt;" }).replace("&lt;br/&gt;", "\n<br/>") + "";
		else
			message = " [Fail] --> Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";

		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		Log.Fail(message, this);
	}

	/**
	 * 
	 * @param what
	 * @param expected
	 * @param actual
	 * @author pramod.singh
	 */
	public void logWarning(String what, String expected, String actual)
	{
		//testResult = false;
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		String message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Warning]--> Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Warning(message, this);
	}

	/**
	 * 
	 * @param e
	 * @author pramod.singh
	 */
	public void logFailureException(Throwable e)
	{
		testResult = false;
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		Log.Failfinal("[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Fail]--> " + ExceptionUtils.getFullStackTrace(e), this);
	}
	
	/**
	 * 
	 * @param message
	 * @author pramod.singh
	 */
	public void logPass(String message)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		Log.Pass(message, this);
	}
	/**
	 * 
	 * @param <T>
	 * @param what
	 * @param actual
	 * @author pramod.singh
	 */
	public <T> void logPass(String what, T actual)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		String message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "]  Verified '" + what + "' as :-'" + actual + "'";
		Log.Pass(message, this);
	}

	/**
	 * 
	 * @param what
	 * @param actual
	 * @author pramod.singh
	 */
	public void logPass(String what, String actual)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		String message = StringUtils.replaceEach(actual, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] Verified '" + what + "' as :-'" + message + "'";
		Log.Pass(message, this);
	}

	/**
	 * @author pramod.singh
	 * @param message
	 */
	public void logWarning(String message)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Warning] --> " + message;
		Log.Warning(message, this);
	}

	/**
	 * 
	 * @param message
	 * @param logPageInfo
	 * @author pramod.singh
	 */
	public void logWarning(String message, boolean logPageInfo)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Warning]--> " + message;
		Log.Warning(message, this, logPageInfo);
	}
	
	/**
	 * 
	 * @param message
	 * @param logPageInfo
	 * @author pramod.singh
	 */
	public void logFail(String message, boolean logPageInfo)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] [Fail] --> " + message;
		if(logPageInfo)
			Log.Fail(message, this);
		else
			Log.FailWithoutPageInfoLogging(message, this);
	}


	/**
	 * Add the given key ArrayListJSONObject pair in the Run Time Properties
	 *  @author pramod.singh
	 */
	public void putJSONArrayListInRunTimeProperty(String key, ArrayList<JSONObject> table)
	{
		String keyName = key.toLowerCase();
		runtimeProperties.put(keyName, table);
		if (debugMode)
			logComment("Putting Run-Time key-" + keyName + " value:-'" + table.toString() + "'");
	}

	/**
	 * Add the given key value pair in the Run Time Properties
	 * 
	 * @param key
	 * @param value
	 ** @author pramod.singh
	 */
	public void putRunTimeProperty(String key, Object value)
	{
		String keyName = key.toLowerCase();
		runtimeProperties.put(keyName, value);
		if (debugMode)
			logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
	}

	/**
	 * Add the given key value pair in the Run Time Properties
	 * 
	 * @param key
	 * @param value
	 * @author pramod.singh
	 */
	public void putRunTimeProperty(String key, String value)
	{
		String keyName = key.toLowerCase();
		runtimeProperties.put(keyName, value);
		if (debugMode)
		{
			logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
		}
	}

	/**
	 * Removes the given key from the Run Time Properties
	 * 
	 * @param key
	 * @author pramod.singh
	 */
	public void removeRunTimeProperty(String key)
	{
		String keyName = key.toLowerCase();
		if (debugMode)
			logComment("Removing Run-Time key-" + keyName);
		runtimeProperties.remove(keyName);
	}


	/**
	 * read and load properties file into RunTime Properties
	 * @param configPath
	 * @author pramod.singh
	 */
	public void loadPropertiesFile(Object file ) {
		// Read the Config file
		Properties property = new Properties();
		try {
			if(file instanceof String) {
				FileInputStream fis = new FileInputStream(file.toString());
				property.load(fis);
			}
			else if(file instanceof InputStream ) {
				InputStream in = (InputStream) file;
				property.load(in);
			
			}
			//fn.close();
			Enumeration<Object> em = property.keys();
			while (em.hasMoreElements())
			{
				String str = (String) em.nextElement();
				putRunTimeProperty(str, (String) property.get(str));
			}
		}catch(Exception e) {
			this.logFail(e.getMessage(), true);
		}
	}
	
	/**
	 * read and load properties file into RunTime Properties
	 * @param configPath
	 * @author pramod.singh
	 */
	public void loadPropertiesFromMap(Map<String, String> data ) {
		// Read the Config file
		try {
			for (String key : data.keySet()) 
	        {
	            String value = data.get(key);
	            putRunTimeProperty(key, value);
	        }
		}
		catch(Exception e) {
			this.logException(e);
		}
	}
	
	/**
	 * Load Config
	 * @param configPath
	 * @param scenario
	 * @author pramod.singh
	 */
	public Config(String ...configPath)
	{
		//endExecutionOnfailure = true;
		String defaultConfigPath = System.getProperty("user.dir") + File.separator
				+ "src/test/resources/Config/config.properties";
		this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
				Helper.generateRandomAlphaNumericString(5) + "-" +
				Helper.generateRandomAlphaNumericString(4);
		this.testResult = true;
		this.DBConnection = null;
		this.testLog = "";
		this.softAssert = new SoftAssert();
		//this.testScenario = scenario;

		this.runtimeProperties = new Properties();

		try {
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
			URL resource = this.getClass().getClassLoader().getResource("config/defaultConfig.properties");
			//System.out.println(" Properties file Path -> " + resource.getPath());
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/elasticConfig.properties"));
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/mongoConfig.properties"));
			loadPropertiesFile(this.getClass().getResourceAsStream("/config/SQLDBConfig.properties"));
		}catch(Exception e) {
			e.printStackTrace();
		}

		// Read and load the above specified properties file in constructor
		defaultConfigPath=configPath.length>0?configPath[0]:defaultConfigPath;
		loadPropertiesFile(defaultConfigPath);

		this.debugMode = (getRunTimeProperty("DebugMode").toLowerCase().equals("true")) ? true : false;
		this.logToStandardOut = (getRunTimeProperty("LogToStandardOut").toLowerCase().equals("true")) ? true : false;
		this.recordPageHTMLOnFailure = (getRunTimeProperty("RecordPageHTMLOnFailure").toLowerCase().equals("true")) ? true : false;
		//this.takeScreenShotOfPage = (getRunTimeProperty("takeScreenShotOfPage").toLowerCase().equals("true")) ? true : false;
		this.remoteExecution = getRunTimeProperty("RemoteExecution") != null && getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true") ? true : false;

		// Set the full path of test data sheet
		String testDataSheet = System.getProperty("user.dir") + getRunTimeProperty("TestDataSheet");
		if (debugMode)
			logComment("Test data sheet is:-" + testDataSheet);
		putRunTimeProperty("TestDataSheet", testDataSheet);
		
		this.BrowserName = this.getBrowserNameFromRunTimeProperty();
		//System.out.println("==========================   browser Name =========" + this.BrowserName );
		
		if(this.getIsHeadLessModeFromRunTimeProperty() != null && !this.getIsHeadLessModeFromRunTimeProperty().isEmpty()) {
			this.isBrowserInHeadlessMode = this.getIsHeadLessModeFromRunTimeProperty().equalsIgnoreCase("true")? true:false;
		}
		else {
			this.isBrowserInHeadlessMode =getRunTimeProperty("isHeadlessMode").equalsIgnoreCase("true")? true:false;
		}

		if(this.getLogsModeFromRunTimeProperty() != null && !this.getLogsModeFromRunTimeProperty().isEmpty()) {
			this.logsMode = this.getLogsModeFromRunTimeProperty().equalsIgnoreCase("true")? true:false;
		}
		
		endExecutionOnfailure = false;
		this.putRunTimeProperty("privateKey", this.privateKey);
		this.putRunTimeProperty("encryptionKey", this.encryptionKey);

	}
	
	/**
	 * Launch windows app
	 * @param appPath : Application path in system
	 * @author charitra.kumar
	 */
	public void launchWindowsApp() {
		int retryCnt = 10;
		while (this.driver == null && retryCnt > 0) {
			try {
				this.driver = Browser.launchWindowsApp(this);

			} catch (Exception e) {
				Log.Warning("Retrying the browser launch:-" + e.toString(), this);
				System.out.println(e.toString());
			}
			if (this.driver == null) {
				retryCnt--;
				if (retryCnt == 0) {
					logFail("Browser could not be opened for : " + getScenarioName());
					Assert.assertTrue(false);
				}
				Browser.wait(this, 2);
			}
		}
		endExecutionOnfailure = false;
	}

	/**
	 * Quits windows application and kills task manager instance
	 * 
	 * @author charitra.kumar
	 */
	public void quitWindowsApp()
	{
		//logToStandardOut = true;
		Browser.quitWindowsApp(this);
		this.driver = null;
	}
	
	/**
	 * create repo in download folder corresponding to each  scenarios
	 * Note : Before calling this function must set current scenarios to testConfig
	 * @param testConfig
	 * @return
	 * @author I0465
	 */
	public static String setDownLoadPathForEachScenarios(Config testConfig) {
		/**
		 * need to decide location for downloaded file corresponding to each test scenarios
		 */

		if(testConfig.scenario != null)
		{
			testConfig.downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator + testConfig.scenario.getName().replaceAll("[^a-zA-Z0-9]+","");


			boolean status = Helper.createFolder(testConfig.downloadPath);

			if(status)
			{
				testConfig.downloadPath = testConfig.downloadPath + fileSeparator;
			}
			else
			{
				System.out.println("Something went Wrong.!! Error in Creating Folder -" + testConfig.downloadPath + " switching to predefined download Path - " + System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator);
				testConfig.downloadPath = System.getProperty("user.home") + fileSeparator + "Downloads" + fileSeparator;
			}
		}
		return testConfig.downloadPath;
	}
	
	/**
	 * get browser type from pom.xml at run time
	 * @author pramod.singh
	 * @return
	 */
	public String getBrowserNameFromRunTimeProperty() {
		String browserName = System.getProperty("Browser");
		//System.out.println(browserName);
		return browserName;
	}
	/**
	 * get browser type from pom.xml at run time
	 * @author pramod.singh
	 * @return
	 */
	public String getIsHeadLessModeFromRunTimeProperty() {
		String isHeadlessMode = System.getProperty("isHeadlessMode");
		return isHeadlessMode;
	}
	
	/**
	 * get browser type from pom.xml at run time
	 * @author pramod.singh
	 * @return
	 */
	public String getLogsModeFromRunTimeProperty() {
		String logsMode = System.getProperty("logsMode");
		return logsMode;
	}

	/**
	 * this function is used to write message in logs file without any PHM data
	 * @param message
	 * @author pramod.singh
	 */
	public void logCommentWithoutPHMData(String message)
	{
		timeStamp = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
		message = "[" + this.uniqueId + "] "+  "[" + timeStamp+ "] " + message;
		if(message.length() <=21)
			Log.Comment(message, this);
		else {
			try {
				throw new IllegalAccessException("[ Fail ] -> Length of message should be less than 15 character"); 
			}
			catch(IllegalAccessException e) {
				this.logException(e);
			}
		}
	}
	
	/**
	 * This method fail test scenarios just after calling it
	 * @param msg
	 * @author pramod.singh
	 */
	public void failFinalTestScenarios(String msg) {
		this.endExecutionOnfailure=true;
		Log.Failfinal(msg, this);
	}
	
	/*
	 * 
	 */
	public void storeTestData(HashMap<String,String> testDataOfMap,String testDataName) {
		this.testData.put(testDataName, testDataOfMap);
	}
	
	/*
	 * 
	 */
	public Map<String,String> getMapOfTestData(String testDataName){
		return this.testData.get(testDataName);
	}
	
	
	/**
	 */
	public static Config getConfig() {
		return threadLocalConfig.get()[0];
	}
	
}
