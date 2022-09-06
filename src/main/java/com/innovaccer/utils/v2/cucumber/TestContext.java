package com.innovaccer.utils.v2.cucumber;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.asserts.SoftAssert;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Log;
import com.innovaccer.utils.v2.BrowserHelper;
import com.innovaccer.utils.v2.UtilityObjectManager;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;

public class TestContext {

	public Config scenarioContext;
	public BrowserHelper browserManager;
	public UtilityObjectManager utilityObjectManager;
	
	public TestContext(){
		String localConfigPath = System.getProperty("user.dir") + File.separator
				+ "src/test/resources/Config/config.properties";
		scenarioContext = new Config(localConfigPath);
		//apply Condition{
		if(Config.threadLocalConfig == null)
			Config.threadLocalConfig= new ThreadLocal<Config[]>();
			
		Config.threadLocalConfig.set(new Config[] { scenarioContext });
		browserManager=new BrowserHelper(scenarioContext);
		utilityObjectManager=new UtilityObjectManager(scenarioContext);
	}	
	public UtilityObjectManager getUtilityObjectManager() {
		return utilityObjectManager;
	}
}
