package com.innovaccer.utils.v2.dataHelper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import org.json.JSONObject;
import pojo.How;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PageLocatorHelper {
	private Config configInstance;
	private  JSONUtils JSONUtils ;
	private LoggerUtils LoggerUtils;

	public PageLocatorHelper(Config config) {
		init(config);
	}

	private void init(Config configInstance) {
		this.configInstance=configInstance;
		JSONUtils = new JSONUtils(configInstance);
		LoggerUtils= new LoggerUtils(this.configInstance);
		this.loadPageLocators(this.getClass().getSimpleName());
	}

	public PageLocatorHelper() {
		init(Config.getConfig());
	}


	/**
	 * load data from locator files
	 * @param fileName
	 */
	 private void loadPageLocators(String fileName) {
		if(configInstance.getPagesLocatorData().containsKey(fileName))
		{
			LoggerUtils.logComment(fileName + ".json Locator file already loaded");
			return;
		}
		else {
			try {
				String filePath = System.getProperty("user.dir") + configInstance.getRunTimeProperty("PageLocatorFilePath");
				filePath=filePath+"/"+fileName+".json";
				File f = new File(filePath);
				if(!f.exists()) {
					LoggerUtils.logComment(fileName + ".json locators file not found ");
					return;
				}
				JSONObject jsonData = JSONUtils.parseJSONFileInJSONObject(filePath);
				Gson gson = new Gson();
				Type listType = new TypeToken<HashMap<String, How>>(){}.getType();
				Map<String,How> mapoflocators= gson.fromJson(jsonData.toString(), listType);
				this.storeLocatorMap(mapoflocators, fileName);
			}catch(JsonSyntaxException jsonex) {
				LoggerUtils.failFinalTestScenarios(jsonex.getMessage());
			}
			catch(Exception e) {
				e.printStackTrace();
				LoggerUtils.failFinalTestScenarios(e.getMessage());
			}
		}
	}

	/**
	 *
	 * @param mapOfLocators
	 * @param fileName
	 */
	synchronized private void storeLocatorMap(Map<String,How> mapOfLocators, String fileName) {
		configInstance.getPagesLocatorData().put(fileName, mapOfLocators);
	}

	public void loadDesignSystemObjects() {
		if (configInstance.getPagesLocatorData().containsKey("DesignSystem")) {
			LoggerUtils.logComment("DesignSystem.json Locators already Loaded");
		} else {
			try {
				String filePath = System.getProperty("user.dir") +
						configInstance.getRunTimeProperty("PageLocatorFilePath") + "/DesignSystem.json";
				File file = new File(filePath);
				if (!file.exists()) {
					LoggerUtils.logComment("DesignSystem.json locators file not found.");
					return;
				}
				JSONObject jsonData = JSONUtils.parseJSONFileInJSONObject(filePath);
				Gson gson = new Gson();
				Type listType = new TypeToken<HashMap<String, How>>() {
				}.getType();
				Map<String, How> locators = gson.fromJson(jsonData.toString(), listType);
				this.storeLocatorMap(locators, "DesignSystem");
			} catch (JsonSyntaxException jsonSyntaxException) {
				LoggerUtils.failFinalTestScenarios(jsonSyntaxException.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				LoggerUtils.failFinalTestScenarios(e.getMessage());
			}
		}
	}

	/**
	 *
	 * @param locatorkey
	 * @return
	 */
	public How getHow(String id) {
		String pageName = configInstance.getRunTimeProperty("PageObjectName");
		if(pageName == null ) {
			LoggerUtils.logFail(" PageObjectName value not found in scenarios context");
			return null;
		}
		else if(configInstance.getPagesLocatorData().containsKey(pageName))
			return configInstance.getPagesLocatorData().get(pageName).get(id);
		else {
			LoggerUtils.logFail(" Locator : " + id + " not found in " + pageName + ".json file");
			return null;
		}
	}

}
