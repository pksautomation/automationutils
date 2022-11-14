package com.innovaccer.utils.v2.testNG;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.*;

public class TestExecutor {
	
	private ExcelUtils excel = new ExcelUtils();
	private LoggerUtils loggerUtils = new LoggerUtils();
	
	@DataProvider(name = "testData", parallel=true)
    public Object[][] dataProviderMethod() throws IOException {
		Object[][] testScenarios = new Object[][] {};
		Map<String, Integer> requiredHeaders = new HashMap<>();
		String excelPath = System.getProperty("user.dir") + File.separator + "src/test/resources/TestData/ScenarioDetails.xlsx";
		FileInputStream file;
		try {
			file = new FileInputStream(new File(excelPath));
			Workbook workbook = new XSSFWorkbook(file);
		    DataFormatter formatter = new DataFormatter();
		    Sheet sheet = workbook.getSheet("ScenarioData");
		   // String[][] sheetData = excel.createReplicaOfSheetData(excelPath, sheet.toString());
		    int rowCount = excel.getRowCountInWorkSheet(excelPath, sheet.toString());
		    for (Cell cell : sheet.getRow(0)) {
				  requiredHeaders.put(cell.getStringCellValue(), cell.getColumnIndex()); 
		    }
		    
		    for(int i = 0; i< rowCount; i++) 
			 { 
				 Row row = sheet.getRow(i);
				 if(row.getCell(requiredHeaders.get("testEnabled")).toString().equalsIgnoreCase("Yes")) {
					 testScenarios[i][0]  = row.getCell(requiredHeaders.get("testScenarioID")).toString();
				     testScenarios[i][1]  = row.getCell(requiredHeaders.get("testScenarioName")).toString();
				 }
				 else
					 i--;
			 }
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			loggerUtils.logException(e);
		}
		
          return testScenarios;
       }
		 
}


