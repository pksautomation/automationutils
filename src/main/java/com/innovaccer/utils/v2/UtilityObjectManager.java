package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.fileutils.*;

public class UtilityObjectManager {


    public AssertionUtils AssertionUtils;
    public BrowserUtils BrowserUtils;
    public LoggerUtils LoggerUtils;
    public YamlUtils YamlUtils;
    public CSVUtils CSVUtils;
	public ExcelUtils ExcelUtils;
    public XMLUtils XMLUtils;
    public TextUtils TextUtils;

    public UtilityObjectManager(Config config) {
        this.AssertionUtils = new AssertionUtils(config);
        this.BrowserUtils = new BrowserUtils(config);
        this.LoggerUtils = new LoggerUtils(config);
        this.YamlUtils = new YamlUtils(config);
        this.CSVUtils = new CSVUtils(config);
        this.ExcelUtils = new ExcelUtils(config);
        this.XMLUtils = new XMLUtils(config);
        this.TextUtils = new TextUtils(config);
    }

	public AssertionUtils getAssertionUtils() {
		return AssertionUtils;
	}

	public void setAssertionUtils(AssertionUtils assertionUtils) {
		AssertionUtils = assertionUtils;
	}

	public BrowserUtils getBrowserUtils() {
		return BrowserUtils;
	}

	public void setBrowserUtils(BrowserUtils browserUtils) {
		BrowserUtils = browserUtils;
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