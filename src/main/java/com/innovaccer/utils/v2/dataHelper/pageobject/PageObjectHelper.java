package com.innovaccer.utils.v2.dataHelper.pageobject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import com.innovaccer.utils.v2.LoggerUtils;

public class PageObjectHelper {
	private Config configInstance;
	private  JSONUtils JSONUtils ;
	private LoggerUtils LoggerUtils;
	
	public PageObjectHelper(Config config) {
		init(config);
	}

	private void init(Config configInstance) {
		this.configInstance=configInstance;
		JSONUtils = new JSONUtils(configInstance); 
		LoggerUtils= new LoggerUtils(this.configInstance);
	}

	public PageObjectHelper() {
		init(Config.getConfig());
	}
	
	public void initPage(String pageName) {
		this.loadPageLocators(pageName);
	}
	
	/**
	 * load data from locator files
	 * @param fileName
	 */
	synchronized public void loadPageLocators(String fileName) {
		Map<String,How> locators = new HashMap<String,How>();
		if(configInstance.locatorsDataPageWise.containsKey(fileName))
			return;
		else {
			
			///Pojo for all json
			try {
				
				///generic path 
				String filePah = System.getProperty("user.dir") + File.separator
					+ "src/test/resources/TestData/PageObjectLocators" + File.separator + fileName + ".json";
				File f = new File(filePah);
				if(!f.exists()) { 
					LoggerUtils.logComment(fileName + ".json locators file not found ");
					return;
				}
				JSONArray jsonArray = JSONUtils.parseJSONFileInJSONArray(filePah);
				for(int i=0 ; i<jsonArray.length();i++) {
					JSONObject json = jsonArray.getJSONObject(i);
				
					String name=json.names().getString(0);
					Gson gson = new Gson();
					How how = gson.fromJson(json.getJSONObject(name).toString(), How.class);
					//how.setKey(name);
					locators.put(name,how);
				
				}
				configInstance.locatorsDataPageWise.put(fileName, locators);
			}catch(Exception e) {
				LoggerUtils.logExceptionAndSkipFailure(fileName + " file not found ", e, false);
			}
		}
	}
	
}
