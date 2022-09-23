package com.innovaccer.utils.v2.fileutils;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVUtils {

    private Config config;
    private LoggerUtils loggerHelper;

    public CSVUtils(Config testConfig) {
        config = testConfig;
        loggerHelper = new LoggerUtils(config);
    }

    /**
     * @param csvFileName -> File Name for the CSV File
     * @return List of Arrays of String for the data from the CSV File
     */
    public List<String[]> getCsvDataFromFile(String csvFileName) {
        try {
            loggerHelper.logComment("Reading CSV Data from File: " + csvFileName);
            CSVReader csvReader = new CSVReader(new FileReader(csvFileName));
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            loggerHelper.logException(e);
        }
        return null;
    }

    /**
     * @param csvFileName -> File Name for the CSV File
     * @return Number of rows in the CSV File
     */
    public int getNumberOfRowInCsvFile(String csvFileName) {
        return getCsvDataFromFile(csvFileName).size();
    }

    /**
     * *
     *
     * @param csvFileName -> File Name for the CSV File
     * @param rowIndex    -> Row Index
     * @return Number of columns at the row index in the CSV File
     */
    public String[] getSpecificRowDataUsingRowIndex(String csvFileName, int rowIndex) {
        return getCsvDataFromFile(csvFileName).get(rowIndex);
    }

    /**
     * @param csvFileName -> File Name for the CSV File
     * @param row         -> Row Index
     */
    public void writeOrEditCsvFileUsingRows(String csvFileName, List<String> row) {
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        int length = 0;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(csvFileName, true);
            length = row.size();
            for (int i = 0; i < length; i++) {
                if (row.get(i) != null)
                    fileWriter.append(row.get(i));
                if (i != length - 1) {
                    fileWriter.append(COMMA_DELIMITER);
                }
            }
            fileWriter.append(NEW_LINE_SEPARATOR);
            loggerHelper.logComment("CSV file was created successfully !!!");
        } catch (Exception e) {
            loggerHelper.logComment("Error in CsvFileWriter !!!");
            loggerHelper.logException(e);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                loggerHelper.logComment("Error while flushing/closing fileWriter !!!");
                loggerHelper.logException(e);
            }
        }
    }

    /**
     * @param csvFileName -> File Name for the CSV File
     * @param column      -> List of Strings for Data to be entered in Columns
     */
    public void writeOrEditCsvFileUsingColumns(String csvFileName, List<String> column) {
        final String NEW_LINE_SEPARATOR = "\n";
        int length = 0;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(csvFileName, false);
            length = column.size();
            for (int i = 0; i < length; i++) {
                if (column.get(i) != null)
                    fileWriter.append(column.get(i));
                if (i != length - 1) {
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
            loggerHelper.logComment("CSV file was created successfully !!!");
        } catch (Exception e) {
            loggerHelper.logComment("Error in CsvFileWriter !!!");
            loggerHelper.logException(e);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                loggerHelper.logComment("Error while flushing/closing fileWriter !!!");
                loggerHelper.logException(e);
            }
        }
    }

    /**
     * *
     *
     * @param fileName -> File Name for the CSV File
     * @param data     -> 2D array of String to be written into CSV File
     */
    public void writeDataToCsvFile(String fileName, List<List<String>> data) {
        final String NEW_LINE_SEPARATOR = "\n";
        final String NEW_VALUE_SEPARATOR = ",";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, false);
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).size(); j++) {
                    if (data.get(i).get(j) != null)
                        fileWriter.append(data.get(i).get(j));
                    if (j != data.get(i).size() - 1)
                        fileWriter.append(NEW_VALUE_SEPARATOR);
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
        } catch (Exception e) {
            loggerHelper.logComment("Error Occurred.");
            loggerHelper.logException(e);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                loggerHelper.logComment("Error while flushing/closing File Writer.");
                loggerHelper.logException(e);
            }
        }
    }
}