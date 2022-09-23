package com.innovaccer.utils.v2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.dataHelper.pageobject.How;
import com.innovaccer.utils.v2.dataHelper.pageobject.PageObjectHelper;

public class PageBase {
	private Config scenarioContext;
	private WaitHelper WaitUtils=null;
	private UtilityObjectManager UtilityObjectManager=null;
	private LoggerUtils LoggerUtils;
	private BrowserUtils browserUtils;
	private WebDriver driver;
	private ElementActionsUtils Actions;
	private PageObjectHelper  PageObjectHelper;
	
	public PageObjectHelper getPageObjectHelper() {
		return PageObjectHelper;
	}

	public void setPageObjectHelper(PageObjectHelper pageObjectHelper) {
		this.PageObjectHelper = pageObjectHelper;
	}

	public PageBase(Config scenariosInstance) {
		init(scenariosInstance);
		}

	private void init(Config scenariosInstance) {
		this.scenarioContext=scenariosInstance;
		this.UtilityObjectManager = new UtilityObjectManager(scenariosInstance);
		WaitUtils = new WaitHelper(scenariosInstance);
		LoggerUtils=new LoggerUtils(scenarioContext);
		driver=scenarioContext.driver;
		Actions = new ElementActionsUtils(scenarioContext);	
		browserUtils = new BrowserUtils(scenarioContext);
		PageObjectHelper=new PageObjectHelper(scenarioContext);
		PageObjectHelper.initPage(this.getInstantClassName());
		PageFactory.initElements(scenariosInstance.driver, this);
	}
	
	public String getInstantClassName() {
		String className=this.getClass().getSimpleName();
		return className;
	}
	
	public PageBase() {
		init(Config.getConfig());
		}
	
	
	public BrowserUtils getBrowserUtils() {
		return browserUtils;
	}


	public void setBrowserUtils(BrowserUtils browserUtils) {
		this.browserUtils = browserUtils;
	}


	public Config getScenarioContext() {
		return scenarioContext;
	}

	public void setScenarioContext(Config scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	public WaitHelper getWaitUtils() {
		return WaitUtils;
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
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public ElementActionsUtils getActions() {
		return Actions;
	}

	public void setActions(ElementActionsUtils actions) {
		Actions = actions;
	}
	/**
	 * 
	 * @param locatorkey
	 * @return
	 */
	public How getHow(String locatorkey) {
		String key = this.getInstantClassName();
		if(Config.locatorsDataPageWise.containsKey(key))
			return Config.locatorsDataPageWise.get(key).get(locatorkey);
		else
			return null;
	}
	
	
	
	
}
