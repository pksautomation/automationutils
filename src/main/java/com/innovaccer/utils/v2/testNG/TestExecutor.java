package com.innovaccer.utils.v2.testNG;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExecutor {

    private ExcelUtils excel = new ExcelUtils();
    private LoggerUtils loggerUtils = new LoggerUtils();

    @DataProvider(name = "testData", parallel = true)
    public Object[][] dataProviderMethod() throws IOException {
        Map<String, Integer> columnNameIndexMap = new HashMap<>();
        List<List<String>> testDetails = new ArrayList<>();
        String excelFilePath = System.getProperty("user.dir") + File.separator + "src/test/resources/TestData/ScenarioDetails.xlsx";
        try {
            FileInputStream file = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("ScenarioData");
            int rowCount = excel.getRowCountInWorkSheet(excelFilePath, sheet.getSheetName());
            for (Cell cell : sheet.getRow(0)) {
                columnNameIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
            for (int i = 0; i < rowCount; i++) {
                List<String> testRow = new ArrayList<>();
                Row row = sheet.getRow(i + 1);
                if (row.getCell(columnNameIndexMap.get("testEnabled")).toString().equalsIgnoreCase("Yes")) {
                    testRow.add(row.getCell(columnNameIndexMap.get("testScenarioID")).toString());
                    testRow.add(row.getCell(columnNameIndexMap.get("testScenarioName")).toString());
                }
                if (testRow.size() != 0)
                    testDetails.add(testRow);
            }
            return testDetails.stream().map(List::toArray).toArray(Object[][]::new);
        } catch (FileNotFoundException e) {
            loggerUtils.logException(e);
        }
        return null;
    }
}