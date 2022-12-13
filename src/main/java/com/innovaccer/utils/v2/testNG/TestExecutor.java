package com.innovaccer.utils.v2.testNG;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.ExcelUtils;
import com.innovaccer.utils.v2.Config;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.innovaccer.utils.v2.CustomRuntimeException;

public class TestExecutor {
	private ExcelUtils excel = new ExcelUtils();
    private LoggerUtils loggerUtils = new LoggerUtils();
    private Config configInstance = new Config();


    @DataProvider(name = "testData", parallel = true)
    public Object[][] dataProviderMethod() throws IOException {
        Map<String, Integer> requiredHeaders = new HashMap<>();
        List<List<String>> testData = new ArrayList<>();
        boolean flag=false;
        String excelPath = System.getProperty("user.dir") + File.separator + configInstance.getRunTimeProperty("ScenarioSheet");
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("ScenarioData");
            String featureName = configInstance.getRunTimeProperty("FeatureName");
            List<String> suiteType = new ArrayList<>();
            suiteType = Stream.of(configInstance.getRunTimeProperty("SuiteType").split("\\s*,\\s*"))
            	     .map(String::trim)
            	     .collect(Collectors.toList());
            int rowCount = excel.getRowCountInWorkSheet(excelPath, sheet.getSheetName());
            for (Cell cell : sheet.getRow(0)) {
                requiredHeaders.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
            for (int i = 0; i < rowCount; i++) {
                List<String> testRow = new ArrayList<>();
                Row row = sheet.getRow(i + 1);
                if (row.getCell(requiredHeaders.get("FeatureName")).toString().equalsIgnoreCase(featureName)) {
                	for (int k = 0 ; k < suiteType.size() ; k++) {
                		if(row.getCell(requiredHeaders.get(suiteType.get(k)+"Suite")).toString().equalsIgnoreCase("Yes"))
                			flag=true;
                		else if(row.getCell(requiredHeaders.get(suiteType.get(k)+"Suite")).toString().equalsIgnoreCase("No")) {
                			flag = false;
                			break;
                		}
                	}
                    if(flag==true && row.getCell(requiredHeaders.get("ParallelMode")).toString().equalsIgnoreCase(configInstance.getRunTimeProperty("ParallelMode")) && row.getCell(requiredHeaders.get("RunTest")).toString().equalsIgnoreCase("Yes")) {
                    	testRow.add(row.getCell(requiredHeaders.get("ScenarioID")).toString());
                    	testRow.add(row.getCell(requiredHeaders.get("ScenarioName")).toString());
                  }
                }
                if (testRow.size() != 0) {
                    testData.add(testRow);
                    testRow = null;
                }
            }
            return testData.stream().map(List::toArray).toArray(Object[][]::new);
        } catch (FileNotFoundException e) {
            loggerUtils.logException(e);
        }
         finally {
        	if(file != null)
        		file.close();
        }
        return null;
    }

    public void scenarioSheetValidation(String excelPath) throws IOException, CustomRuntimeException {
        DataFormatter dataFormatter = new DataFormatter();
        Map<Integer, String> requiredHeaders = new HashMap<>();
        List<String> columnNames = Arrays.asList("ScenarioID", "ScenarioName", "RunTest", "FeatureName","SmokeSuite","RegressionSuite","IntegrationSuite","ParallelMode");
        Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(excelPath)));
        Sheet sheet = workbook.getSheet("ScenarioData");
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = 0;
        try { 
        for (Cell cell : sheet.getRow(0)) {
        	requiredHeaders.put(cell.getColumnIndex(),cell.getStringCellValue());
        	String cellValue = dataFormatter.formatCellValue(cell);
            if (!columnNames.get(colCount).trim().equalsIgnoreCase(cellValue.trim())) {
            	throw new CustomRuntimeException("Number of column mismatched from required format.Please verify file headers");
            }
            else 
            	colCount++; 
        }
   
        	for (int j = 1; j < rowCount; j++) {
        		Row row = sheet.getRow(j);
        		for(int i = 0; i<4;i++) {
        			if (!(excel.isCellEmpty(row.getCell(i)))) {
        				switch(requiredHeaders.get(i)) {
        					
        					case "RunTest":
        						if(!(row.getCell(i).toString().trim().equalsIgnoreCase("Yes") ||row.getCell(i).toString().trim().equalsIgnoreCase("No"))) {
        							throw new CustomRuntimeException("Value should be Yes or No in the column "+ requiredHeaders.get(i) + " and row number "+ j);
        						}
        						break;
        					default:
        						if(row.getCell(i).toString().equalsIgnoreCase(""))
        							throw new CustomRuntimeException("Value should not be blank in the column " + requiredHeaders.get(i));
        				}
        		}
        			else
        				throw new CustomRuntimeException ("Cell value should not be empty. Please check column  "+ requiredHeaders.get(i) + " and row number "+ j);
        }
        
    } 
        } catch (CustomRuntimeException e) {
        	 loggerUtils.logFailureException(e);
        }
    }
}
