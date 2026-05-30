package com.innovaccer.utils.dbconnection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.TestDataReader;

/**
 * 
 * @author pramod.singh
 *
 */
public class ColumnMappingOfTables {

	public Map<String,String> getRiskOutputColumnMapping(Config testConfig,int rowNum,String SheetName){
		HashMap<String,String> columnMapping = new HashMap<String,String>();
		String excelFilePath =  System.getProperty("user.dir") + File.separator + testConfig.getRunTimeProperty("ColumnMappingSheetOfTable");
		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject(SheetName, excelFilePath);
		int columnCount = testDataReader.getColumnNum();
		for(int i=0; i<columnCount; i++) {
			columnMapping.put(testDataReader.GetHeaderData(i),testDataReader.GetData(rowNum, testDataReader.GetHeaderData(i)));
		}
		return columnMapping;
		
	}
}
