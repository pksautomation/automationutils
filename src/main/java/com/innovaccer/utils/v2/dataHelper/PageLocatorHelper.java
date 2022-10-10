package com.innovaccer.utils.v2.dataHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.fileutils.JSONUtils;

import pojo.How;

import com.innovaccer.utils.v2.LoggerUtils;

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
		if(Config.pagesLocatorData.containsKey(fileName))
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
		Config.pagesLocatorData.put(fileName, mapOfLocators);
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
		else if(Config.pagesLocatorData.containsKey(pageName))
			return Config.pagesLocatorData.get(pageName).get(id);
		else {
			LoggerUtils.logFail(" Locator : " + id + " not found in " + pageName + ".json file");
			return null;
		}
	}
	
}
