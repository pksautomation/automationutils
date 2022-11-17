package com.innovaccer.utils.v2.fileutils;


import com.innovaccer.utils.Helper;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtils {

    private Config configInstance;
    private LoggerUtils loggerUtils;

    public ExcelUtils(Config testConfig) {
        init(testConfig);

    }

    public ExcelUtils() {
        init(Config.getConfig());
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @return Number of Rows in the Sheet
     * @throws IOException
     */
    public int getRowCountInWorkSheet(String workbookName, String worksheetName) throws IOException {
        int numberOfRows = 0;
        FileInputStream fileInputStream = new FileInputStream(workbookName);
        if (workbookName.contains(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet worksheet = workbook.getSheet(worksheetName);
            numberOfRows = worksheet.getLastRowNum();
        } else {
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            numberOfRows = worksheet.getLastRowNum();
        }
        return numberOfRows;
    }

    /**
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @return Number of Columns in the Row
     */
    public int getNumberOfColumnsInARow(String workbookName, String worksheetName, int rowIndex) {
        int numberOfColumns = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            numberOfColumns = row.getPhysicalNumberOfCells();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
        return numberOfColumns;
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     * @return Value at the Cell pointed be Row and Column
     */
    public String readValueUsingRowAndColumn(String workbookName, String worksheetName, int rowIndex, int columnIndex) {
        String cellValue = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            HSSFCell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            if (cell != null)
                cellValue = cell.getStringCellValue().trim();
            else {
                cellValue = "";
            }
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
        return cellValue;
    }

    /**
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @return Replica of Sheet Table Data
     */
    public String[][] createReplicaOfSheetData(String workbookName, String worksheetName) {
        String[][] sheetData = new String[100][100];
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            int numberOfRows, numberOfColumnsInRow;
            String temp = "";
            numberOfRows = worksheet.getLastRowNum();
            for (int i = 0; i <= numberOfRows; i++) {
                HSSFRow row = worksheet.getRow(i);
                numberOfColumnsInRow = row.getPhysicalNumberOfCells();
                for (int j = 0; j < numberOfColumnsInRow; j++) {
                    HSSFCell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    if (cell != null) {
                        temp = cell.getStringCellValue().trim();
                        HSSFCellStyle cells = cell.getCellStyle();
                        cell.setCellStyle(cells);
                    } else {
                        temp = "";
                    }
                    sheetData[i][j] = temp;
                }
            }
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
        return sheetData;
    }

    /**
     * *
     *
     * @param xmlFileName   -> XML File Name
     * @param excelFileName -> Excel File Name
     * @return Convert XML File into Excel File
     */
    public String saveXmlAsExcel(String xmlFileName, String excelFileName) {
        String fileTimeName = null;
        try {
            fileTimeName = Helper.getCurrentDateTime("HH-mm-ss");
            excelFileName = configInstance.getDownloadPath() + excelFileName + fileTimeName + ".xls";
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet SettlementInputXLS = wb.createSheet("SettlementInputXLS");
            HSSFRow xlsRow;
            HSSFCell xlsCell;
            File fXmlFile = new File(xmlFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList rows = doc.getElementsByTagName("Row");
            for (int rowSize = 0; rowSize < rows.getLength(); rowSize++) {
                Node row = rows.item(rowSize);
                NodeList cols = row.getChildNodes();
                xlsRow = SettlementInputXLS.createRow(rowSize);
                for (int Column = 0; Column < cols.getLength(); Column++) {
                    Node col = cols.item(Column);
                    String Inputdata = col.getTextContent();
                    if (Inputdata.equals("\n"))
                        continue;
                    xlsCell = xlsRow.createCell(Column);
                    SettlementInputXLS.setColumnWidth(Column, 4200);
                    xlsCell.setCellValue(Inputdata);
                    loggerUtils.logComment("Entered Value of " + Inputdata);
                }
            }
            FileOutputStream output = new FileOutputStream(new File(excelFileName));
            wb.write(output);
            output.flush();
            output.close();
            loggerUtils.logComment("File: " + xmlFileName + " has been saved as Excel with name as: " + excelFileName);
            return excelFileName;
        } catch (IOException e) {
            loggerUtils.logComment("IOException " + e.getMessage());
            return null;
        } catch (ParserConfigurationException e) {
            loggerUtils.logComment("ParserConfigurationException " + e.getMessage());
            return null;
        } catch (SAXException e) {
            loggerUtils.logComment("SAXException " + e.getMessage());
            return excelFileName;
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     */
    public void changeCellValueToBlank(String workbookName, String worksheetName, int rowIndex, int columnIndex) {
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex);
            }
            HSSFCell cell = row.createCell(columnIndex);
            worksheet.setColumnWidth(columnIndex, 4200);
            cell.removeCellComment();
            FileOutputStream fileOut = new FileOutputStream(workbookName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     */
    public void setRowAsBlank(String workbookName, String worksheetName, int rowIndex, int columnIndex) {
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex);
            }
            for (int i = 0; i < columnIndex; i++) {
                HSSFCell cell = row.createCell(i);
                worksheet.setColumnWidth(i, 4200);
                cell.removeCellComment();
                FileOutputStream fileOut = new FileOutputStream(workbookName);
                workbook.write(fileOut);
                fileOut.close();
            }
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     * @param string        -> String to be written in the Cell
     */
    public void writeStringToSheet(String workbookName, String worksheetName, int rowIndex, int columnIndex, String string) {
        try {
            FileOutputStream fileout = new FileOutputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet(worksheetName);
            HSSFFont font = workbook.createFont();
            HSSFRow row = worksheet.createRow(rowIndex);
            HSSFCell cell = row.createCell(columnIndex);
            cell.setCellValue(string);
            worksheet.setColumnWidth(columnIndex, 5000);
            font.setFontName("Calibri");
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
            workbook.write(fileout);
            fileout.flush();
            fileout.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     * @param string        -> String to be written in the Cell
     * @param description   -> Description to be added for Value
     */
    public void writeOrEditXlsFile(String workbookName, String worksheetName, int rowIndex, int columnIndex, String string, String description) {
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex);
            }
            HSSFCell cell = row.createCell(columnIndex);
            worksheet.setColumnWidth(columnIndex, 4200);
            cell.setCellValue(string);
            FileOutputStream fileOut = new FileOutputStream(workbookName);
            loggerUtils.logComment("Entered Value of " + description + " as " + string);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     * @param string        -> String to be written in the Cell
     * @param colourFlag    -> Font Colour
     */
    public void writeOrEditXssFile(String workbookName, String worksheetName, int rowIndex, int columnIndex, String string, int colourFlag) {
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            Workbook workbook = null;
            Sheet worksheet = null;
            Row row = null;
            Cell cell = null;
            Font font = null;
            CellStyle cellStyle2 = null;
            if (worksheetName.contains(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else {
                workbook = new HSSFWorkbook(fileInputStream);
            }
            worksheet = workbook.getSheet(worksheetName);
            row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex);
            }
            cell = row.createCell(columnIndex);
            worksheet.setColumnWidth(columnIndex, 4200);
            cell.setCellValue(string);
            font = workbook.createFont();
            if (colourFlag == 1) {
                font.setFontName("Calibri");
                font.setColor(HSSFColor.RED.index);
                cellStyle2 = workbook.createCellStyle();
                cellStyle2.setFont(font);
                cell.setCellStyle(cellStyle2);
            } else if (colourFlag == 0) {
                font.setFontName("Calibri");
                font.setColor(HSSFColor.GREEN.index);
                cellStyle2 = workbook.createCellStyle();
                cellStyle2.setFont(font);
                cell.setCellStyle(cellStyle2);
            }
            FileOutputStream fileOut = new FileOutputStream(workbookName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }

    /**
     * *
     *
     * @param workbookName  -> Excel Workbook Name
     * @param worksheetName -> Excel Sheet Name
     * @param rowIndex      -> Row Index
     * @param columnIndex   -> Column Index
     * @param string        -> String to be written in the Cell
     * @param colourFlag    -> Font Colour
     */
    public void writeOrEditExcelFile(String workbookName, String worksheetName, int rowIndex, int columnIndex, String string, int colourFlag) {
        try {
            FileInputStream fileInputStream = new FileInputStream(workbookName);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            HSSFRow row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex);
            }
            HSSFCell cell = row.createCell(columnIndex);
            worksheet.setColumnWidth(columnIndex, 4200);
            cell.setCellValue(string);
            HSSFFont font = workbook.createFont();
            if (colourFlag == 1) {
                font.setFontName("Calibri");
                font.setColor(HSSFColor.RED.index);
                HSSFCellStyle cellStyle2 = workbook.createCellStyle();
                cellStyle2.setFont(font);
                cell.setCellStyle(cellStyle2);
            } else if (colourFlag == 0) {
                font.setFontName("Calibri");
                font.setColor(HSSFColor.GREEN.index);
                HSSFCellStyle cellStyle2 = workbook.createCellStyle();
                cellStyle2.setFont(font);
                cell.setCellStyle(cellStyle2);
            }
            FileOutputStream fileOut = new FileOutputStream(workbookName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
        }
    }
    

}