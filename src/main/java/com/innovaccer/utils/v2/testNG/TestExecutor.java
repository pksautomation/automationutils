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
import java.util.*;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.*;

public class TestExecutor {
	
	private ExcelUtils excel = new ExcelUtils();
    private LoggerUtils loggerUtils = new LoggerUtils();

    @DataProvider(name = "testData", parallel = true)
    public Object[][] dataProviderMethod() throws IOException {
        Map<String, Integer> requiredHeaders = new HashMap<>();
        List<List<String>> testData = new ArrayList<>();
        String excelPath = System.getProperty("user.dir") + File.separator + "src/test/resources/TestData/ScenarioDetails.xlsx";
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("ScenarioData");
            int rowCount = excel.getRowCountInWorkSheet(excelPath, sheet.getSheetName());
            for (Cell cell : sheet.getRow(0)) {
                requiredHeaders.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
            for (int i = 0; i < rowCount; i++) {
                List<String> testRow = new ArrayList<>();
                Row row = sheet.getRow(i + 1);
                if (row.getCell(requiredHeaders.get("testEnabled")).toString().equalsIgnoreCase("Yes")) {
                    testRow.add(row.getCell(requiredHeaders.get("testScenarioID")).toString());
                    testRow.add(row.getCell(requiredHeaders.get("testScenarioName")).toString());
                }
                if (testRow.size() != 0) {
                    testData.add(testRow);
                    testRow = null;
                }
            }
            //System.out.println(testData);
        } catch (FileNotFoundException e) {
            loggerUtils.logException(e);
        }
         finally {
        	if(file != null) 
        		file.close();
        }
        return null;
    }
		 
}


