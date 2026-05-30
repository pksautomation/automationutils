package com.innovaccer.utils.v2;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.asserts.SoftAssert;

import com.epam.healenium.SelfHealingDriver;
import com.innovaccer.utils.v2.dataHelper.*;
import com.jayway.restassured.response.Response;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import cucumber.api.Scenario;
import pojo.How;

public class ConfigSingleton {

	
	private static ConfigSingleton single_instance = null; 
	private  boolean remoteExecution = false;
	private  boolean isBrowserInHeadlessMode = false;
	private  String encryptionKey = null;
	private  String privateKey = null;
	private  String Environment;
	private  String ResultsDir=null;
	private  String PlatformName;
	private  String SharedDirectory;
	private  String ProjectName;
	private  String BrowserVersion;
	private  String BrowserName;
	private  String remoteURL;

	private  Connection DBConnection = null;
	private 	MongoDatabase mongoAdminDatabase = null;
	private 	MongoClient mongoClientConnection = null;
	private 	MongoDatabase mongoRiskDBConnection = null;
	
    public static HashMap<String, HashMap<String, String>> testData;
    public static Map<String, Map<String, How>> pagesLocatorData ;
  
    //Singleton
    private ConfigSingleton() 
    { 
    } 
  
    // Static method 
    // Static method to create instance of Singleton class 
    public static ConfigSingleton getInstance() 
    { 
        if (single_instance == null) 
        	synchronized(ConfigSingleton.class){  
                if (single_instance == null){  
                	single_instance = new ConfigSingleton();
                	single_instance.testData = new HashMap<String, HashMap<String, String>>();
                	//single_instance.testDataReaderHashMap = new HashMap<String, ExcelDataReader>();
                	single_instance.pagesLocatorData = new HashMap<String, Map<String, How>>();
                }
        	}
  
        return single_instance; 
    } 
    
    public boolean isRemoteExecution() {
		return remoteExecution;
	}

	public void setRemoteExecution(boolean remoteExecution) {
			this.remoteExecution = remoteExecution;
	}

	public boolean isBrowserInHeadlessMode() {
		return isBrowserInHeadlessMode;
	}

	public void setBrowserInHeadlessMode(boolean isBrowserInHeadlessMode) {
		this.isBrowserInHeadlessMode = isBrowserInHeadlessMode;
	}
	
	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		if(this.encryptionKey != null)
			synchronized(ConfigSingleton.class) {
			this.encryptionKey = encryptionKey;
			}
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		if(this.privateKey != null)
			synchronized(ConfigSingleton.class) {
				this.privateKey = privateKey;
			}
	}

	public String getBrowserName() {
		return BrowserName;
	}

	public void setBrowserName(String browserName) {
		if(this.BrowserName != null)
			synchronized(ConfigSingleton.class) {
				BrowserName = browserName;
			}
	}

	public String getEnvironment() {
		return Environment;
	}

	public void setEnvironment(String environment) {
		if(this.Environment != null)
			synchronized(ConfigSingleton.class) {
				Environment = environment;
			}
	}

	public String getResultsDir() {
		return ResultsDir;
	}

	public void setResultsDir(String resultsDir) {
		if(this.ResultsDir != null)
			synchronized(ConfigSingleton.class) {
				ResultsDir = resultsDir;
			}
	}

	public String getPlatformName() {
		return PlatformName;
	}

	public void setPlatformName(String platformName) {
		if(this.PlatformName != null)
			synchronized(ConfigSingleton.class) {
				PlatformName = platformName;
			}
	}

	public String getSharedDirectory() {
		return SharedDirectory;
	}

	public void setSharedDirectory(String sharedDirectory) {
		if(this.SharedDirectory != null)
			synchronized(ConfigSingleton.class) {
				SharedDirectory = sharedDirectory;
			}
	}

	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		if(this.ProjectName != null)
			synchronized(ConfigSingleton.class) {
				ProjectName = projectName;
			}
	}

	public String getBrowserVersion() {
		return BrowserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		if(this.BrowserVersion != null)
			synchronized(ConfigSingleton.class) {
				BrowserVersion = browserVersion;
			}
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		if(this.remoteURL != null)
			synchronized(ConfigSingleton.class) {
				this.remoteURL = remoteURL;
			}
	}

	public Connection getDBConnection() {
		return DBConnection;
	}

	public void setDBConnection(Connection dBConnection) {
		if(this.DBConnection != null)
			synchronized(ConfigSingleton.class) {
				DBConnection = dBConnection;
			}
	}

	public MongoDatabase getMongoAdminDatabase() {
		return mongoAdminDatabase;
	}

	public void setMongoAdminDatabase(MongoDatabase mongoAdminDatabase) {
		if(this.mongoAdminDatabase != null)
			synchronized(ConfigSingleton.class) {
				this.mongoAdminDatabase = mongoAdminDatabase;
			}
	}

	public MongoClient getMongoClientConnection() {
		return mongoClientConnection;
	}

	public void setMongoClientConnection(MongoClient mongoClientConnection) {
		if(this.mongoClientConnection != null)
			synchronized(ConfigSingleton.class) {
				this.mongoClientConnection = mongoClientConnection;
			}
	}


	public HashMap<String, HashMap<String, String>> getTestData() {
		return testData;
	}
	
	synchronized public void putTestData(String testDataName,HashMap<String, String> testDataValue ) {
		 testData.put(testDataName, testDataValue);
	}

	public Map<String, Map<String, How>> getPagesLocatorData() {
		return pagesLocatorData;
	}
	
	synchronized public void putPagesLocatorData(String pageName,Map<String, How> mapOfLocatorValue) {
		 pagesLocatorData.put(pageName, mapOfLocatorValue);
	}
}
