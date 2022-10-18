package com.innovaccer.utils.v2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.dataHelper.PageLocatorHelper;

import pojo.How;

public class BasePage extends PageLocatorHelper{
	private Config scenarioContext;
	private WaitHelper WaitUtils=null;
	private UtilityObjectManager UtilityObjectManager=null;
	private LoggerUtils LoggerUtils;
	private BrowserUtils browserUtils;
	private ElementActionsUtils Actions;

	public BasePage(Config scenariosInstance) {
		init(scenariosInstance);
		}

	private void init(Config scenariosInstance) {
		this.scenarioContext=scenariosInstance;
		this.UtilityObjectManager = new UtilityObjectManager(scenariosInstance);
		LoggerUtils=new LoggerUtils(scenarioContext);
		Actions = new ElementActionsUtils(scenarioContext);
		PageFactory.initElements(scenarioContext.getDriver(), this);
		scenarioContext.putRunTimeProperty("PageObjectName", this.getClass().getSimpleName());
		
	}
	
	public BasePage() {
		init(Config.getConfig());
		}
	
	
	public BrowserUtils getBrowserUtils() {
		return UtilityObjectManager.getBrowserUtils();
	}


	public void setBrowserUtils(BrowserUtils browserUtils) {
		this.browserUtils = browserUtils;
	}


	public Config getScenarioContext() {
		return UtilityObjectManager.getConfigInstant();
	}

	public void setScenarioContext(Config scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	public WaitHelper getWaitUtils() {
		return UtilityObjectManager.getWaitUtils();
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
		return UtilityObjectManager.getConfigInstant().getDriver();
	}

	public ElementActionsUtils getActions() {
		return Actions;
	}

	public void setActions(ElementActionsUtils actions) {
		Actions = actions;
	}
	
}
