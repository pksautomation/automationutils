package com.innovaccer.utils.v2.dataHelper;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.CSVUtils;
import com.innovaccer.utils.v2.fileutils.ExcelUtils;
import com.innovaccer.utils.v2.fileutils.TextUtils;
import com.innovaccer.utils.v2.fileutils.XMLUtils;
import com.innovaccer.utils.v2.fileutils.YamlUtils;

public class TestDataHelper {
	private LoggerUtils LoggerUtils;

	private YamlUtils YamlUtils;
	private CSVUtils CSVUtils;
	private ExcelUtils ExcelUtils;
	private XMLUtils XMLUtils;
	private TextUtils TextUtils;
	Config configInstant;

	public TestDataHelper(Config config) {
		init(config);
	}

	private void init(Config config) {
		this.LoggerUtils = new LoggerUtils(config);
		this.YamlUtils = new YamlUtils(config);
		this.CSVUtils = new CSVUtils(config);
		this.ExcelUtils = new ExcelUtils(config);
		this.XMLUtils = new XMLUtils(config);
		this.TextUtils = new TextUtils(config);
		this.configInstant=config;
	}

	public TestDataHelper() {
		init(Config.getConfig());
	}

	/*
	 * 
	 */
	public String getTestData(String datakey) {
		String testDataName = configInstant.getRunTimeProperty("TestDataName");
		if (configInstant.testData.containsKey(testDataName)
				&& configInstant.testData.get(testDataName).containsKey(datakey))
			return configInstant.testData.get(testDataName).get(datakey);
		else
			return null;
	}

	public LoggerUtils getLoggerUtils() {
		return LoggerUtils;
	}

	public void setLoggerUtils(LoggerUtils loggerUtils) {
		LoggerUtils = loggerUtils;
	}

	public YamlUtils getYamlUtils() {
		return YamlUtils;
	}

	public void setYamlUtils(YamlUtils yamlUtils) {
		YamlUtils = yamlUtils;
	}

	public CSVUtils getCSVUtils() {
		return CSVUtils;
	}

	public void setCSVUtils(CSVUtils cSVUtils) {
		CSVUtils = cSVUtils;
	}

	public ExcelUtils getExcelUtils() {
		return ExcelUtils;
	}

	public void setExcelUtils(ExcelUtils excelUtils) {
		ExcelUtils = excelUtils;
	}

	public XMLUtils getXMLUtils() {
		return XMLUtils;
	}

	public void setXMLUtils(XMLUtils xMLUtils) {
		XMLUtils = xMLUtils;
	}

	public TextUtils getTextUtils() {
		return TextUtils;
	}

	public void setTextUtils(TextUtils textUtils) {
		TextUtils = textUtils;
	}

}
