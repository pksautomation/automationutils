package com.innovaccer.utils.v2;

import com.epam.healenium.SelfHealingDriver;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.TestDataReader;
import com.jayway.restassured.response.Response;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import cucumber.api.Scenario;
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

    public static ThreadLocal<Config[]> threadLocalConfig;
    public static String BrowserName;
    public static String Environment;
    public static String ResultsDir;
    public static String PlatformName;
    public static String SharedDirectory;
    public static String ProjectName;
    public static String BrowserVersion;
    public static String fileSeparator = File.separator;
    public static HashMap<String, TestDataReader> testDataReaderHashMap = new HashMap<String, TestDataReader>();
    public static HashMap<Integer, HashMap<String, String>> genericErrors = new HashMap<Integer, HashMap<String, String>>();
    public static HashMap<String, HashMap<String, String>> testData = new HashMap<String, HashMap<String, String>>();
    public static String scenarioName;
    public static String featureName;
    public static boolean remoteExecution = false;
    public static String remoteURL;
    public static boolean isBrowserInHeadlessMode = false;
    public static boolean logsMode = true;
    public static boolean logsModeForException = false;
    public static boolean takeScreenShotOfPage = false;
    public boolean endExecutionOnfailure = false;
    public boolean debugMode = false;
    public boolean recordPageHTMLOnFailure = false;
    public Connection DBConnection = null;
    public SelfHealingDriver driver;
    public SelfHealingDriver defaultBrowser;
    public SelfHealingDriver tempDriver;
    public WebDriver delegatedriver;
    public String downloadPath = null;
    public boolean enableScreenshot = true;
    public boolean logToStandardOut = true;
    public MongoDatabase mongoAdminDatabase = null;
    public MongoClient mongoClientConnection = null;
    public MongoDatabase mongoRiskDBConnection = null;
    public List<String> listOfFailedStep;
    public List<String> listOfLogsOfEachFailedStep;
    public int stepNumber = 0;
    public boolean isFailScenarioStatus = false;
    public Scenario scenario = null;
    public SoftAssert softAssert;
    public String testLog;
    public Scenario testScenario;
    public boolean testResult;
    public Response apiResponse = null;
    public StringBuilder authorizationToken;
    public String previousPage = "";
    public SessionId session = null;
    public String uniqueId = null;
    public String timeStamp = null;
    public String encryptionKey = "amVxSX10V0ppZHlJal1qXHx3Z1x+Vw0N";
    public String privateKey = "010100000000000";
    public boolean islogExceptionSkip = false;
    Properties runtimeProperties;
    TestDataReader testDataReaderObj;
    String testEndTime;
    String testStartTime;
    private WaitHelper waitHelper;

    com.innovaccer.utils.Config config;

    /**
     * Load Config
     *
     * @param configPath
     * @param scenario
     * @author pramod.singh
     */
    public Config(String configPath, Scenario scenario) {
        config = new com.innovaccer.utils.Config();
        this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
                Helper.generateRandomAlphaNumericString(5) + "-" +
                Helper.generateRandomAlphaNumericString(4);
        this.testResult = true;
        this.DBConnection = null;
        this.testLog = "";
        this.softAssert = new SoftAssert();
        this.testScenario = scenario;
        this.runtimeProperties = new Properties();
        try {
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/elasticConfig.properties"));
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/mongoConfig.properties"));
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/SQLDBConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPropertiesFile(configPath);
        this.debugMode = getRunTimeProperty("DebugMode").equalsIgnoreCase("true");
        this.logToStandardOut = getRunTimeProperty("LogToStandardOut").equalsIgnoreCase("true");
        this.recordPageHTMLOnFailure = getRunTimeProperty("RecordPageHTMLOnFailure").equalsIgnoreCase("true");
        scenarioName = scenario.getName();
        String rawFeatureName = scenario.getId().split(";")[0].replace("-", "_");
        featureName = rawFeatureName.substring(0, 1).toUpperCase() + rawFeatureName.substring(1);
        String testDataSheet = System.getProperty("user.dir") + getRunTimeProperty("TestDataSheet");
        if (debugMode)
            config.logComment("Test data sheet is:-" + testDataSheet);
        putRunTimeProperty("TestDataSheet", testDataSheet);
        remoteExecution = getRunTimeProperty("RemoteExecution") != null && getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true");
        endExecutionOnfailure = false;
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
        BrowserName = this.getBrowserNameFromRunTimeProperty();
        if (this.getIsHeadLessModeFromRunTimeProperty() != null && !this.getIsHeadLessModeFromRunTimeProperty().isEmpty()) {
            isBrowserInHeadlessMode = this.getIsHeadLessModeFromRunTimeProperty().equalsIgnoreCase("true");
        } else {
            isBrowserInHeadlessMode = getRunTimeProperty("isHeadlessMode").equalsIgnoreCase("true");
        }
        if (this.getLogModeFromRunTimeProperty() != null && !this.getLogModeFromRunTimeProperty().isEmpty()) {
            logsMode = this.getLogModeFromRunTimeProperty().equalsIgnoreCase("true");
        }
        this.putRunTimeProperty("privateKey", this.privateKey);
        this.putRunTimeProperty("encryptionKey", this.encryptionKey);
    }

    public Config(String... configPath) {
        String defaultConfigPath = System.getProperty("user.dir") + File.separator
                + "src/test/resources/Config/config.properties";
        this.uniqueId = Helper.generateRandomAlphaNumericString(4) + "-" +
                Helper.generateRandomAlphaNumericString(5) + "-" +
                Helper.generateRandomAlphaNumericString(4);
        this.testResult = true;
        this.DBConnection = null;
        this.testLog = "";
        this.softAssert = new SoftAssert();
        this.runtimeProperties = new Properties();
        try {
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/defaultConfig.properties"));
            URL resource = this.getClass().getClassLoader().getResource("config/defaultConfig.properties");
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/elasticConfig.properties"));
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/mongoConfig.properties"));
            loadPropertiesFile(this.getClass().getResourceAsStream("/config/SQLDBConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        defaultConfigPath = configPath.length > 0 ? configPath[0] : defaultConfigPath;
        loadPropertiesFile(defaultConfigPath);
        this.debugMode = getRunTimeProperty("DebugMode").equalsIgnoreCase("true");
        this.logToStandardOut = getRunTimeProperty("LogToStandardOut").equalsIgnoreCase("true");
        this.recordPageHTMLOnFailure = getRunTimeProperty("RecordPageHTMLOnFailure").equalsIgnoreCase("true");
        remoteExecution = getRunTimeProperty("RemoteExecution") != null && getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true");
        String testDataSheet = System.getProperty("user.dir") + getRunTimeProperty("TestDataSheet");
        if (debugMode)
            config.logComment("Test data sheet is:-" + testDataSheet);
        putRunTimeProperty("TestDataSheet", testDataSheet);

        BrowserName = this.getBrowserNameFromRunTimeProperty();

        if (this.getIsHeadLessModeFromRunTimeProperty() != null && !this.getIsHeadLessModeFromRunTimeProperty().isEmpty()) {
            isBrowserInHeadlessMode = this.getIsHeadLessModeFromRunTimeProperty().equalsIgnoreCase("true");
        } else {
            isBrowserInHeadlessMode = getRunTimeProperty("isHeadlessMode").equalsIgnoreCase("true");
        }

        if (this.getLogModeFromRunTimeProperty() != null && !this.getLogModeFromRunTimeProperty().isEmpty()) {
            logsMode = this.getLogModeFromRunTimeProperty().equalsIgnoreCase("true");
        }

        endExecutionOnfailure = false;
        this.putRunTimeProperty("privateKey", this.privateKey);
        this.putRunTimeProperty("encryptionKey", this.encryptionKey);

    }

    public static Config getConfig() {
        return threadLocalConfig.get()[0];
    }

    @SuppressWarnings("unchecked")
    public ArrayList<JSONObject> getJSONArrayListFromRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        ArrayList<JSONObject> value;
        try {
            value = (ArrayList<JSONObject>) runtimeProperties.get(keyName);
            if (debugMode)
                config.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode) {
                config.logComment(e.toString());
                config.logComment("'" + key + "' not found in Run Time Properties");
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
                config.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode) {
                config.logComment(e.toString());
                config.logComment("'" + key + "' not found in Run Time Properties");
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
//            value = Helper.replaceArgumentsWithRunTimeProperties(this, value);
            if (debugMode)
                config.logComment("Reading Run-Time key-" + keyName + " value:-'" + value + "'");
        } catch (Exception e) {
            if (debugMode && !keyName.equalsIgnoreCase("beforehook")) {
                config.logComment(e.toString());
                config.logComment("'" + key + "' not found in Run Time Properties");
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
            config.logComment("Putting Run-Time key-" + keyName + " value:-'" + table.toString() + "'");
    }

    public void putRunTimeProperty(String key, Object value) {
        String keyName = key.toLowerCase();
        runtimeProperties.put(keyName, value);
        if (debugMode)
            config.logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
    }

    public void putRunTimeProperty(String key, String value) {
        String keyName = key.toLowerCase();
        runtimeProperties.put(keyName, value);
        if (debugMode) {
            config.logComment("Putting Run-Time key-" + keyName + " value:-'" + value + "'");
        }
    }

    public void removeRunTimeProperty(String key) {
        String keyName = key.toLowerCase();
        if (debugMode)
            config.logComment("Removing Run-Time key-" + keyName);
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
            config.logFail(e.getMessage(), true);
        }
    }


    public void loadPropertiesFromMap(Map<String, String> data) {
        try {
            for (String key : data.keySet()) {
                String value = data.get(key);
                putRunTimeProperty(key, value);
            }
        } catch (Exception e) {
            config.logException(e);
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
    
    /**
     * Return the instance of WebDriver
     * @author nikitagatagat
     */
    public WebDriver getDriver(){
    	return this.driver;
    	}

}