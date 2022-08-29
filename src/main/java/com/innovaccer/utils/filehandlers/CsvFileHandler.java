package com.innovaccer.utils.filehandlers;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Log;
import com.innovaccer.utils.v2.LoggerHelper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CsvFileHandler {

    public static Config config;
    public static LoggerHelper loggerHelper;

    public CsvFileHandler(Config testConfig) {
        config = testConfig;
        loggerHelper = new LoggerHelper(config);
    }

    public static List<String[]> getCsvDataFromFile(String csvFileName) {
        try {
            loggerHelper.logComment("Reading CSV Data from File: " + csvFileName);
            CSVReader csvReader = new CSVReader(new FileReader(csvFileName));
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            loggerHelper.logException(e);
        }
        return null;
    }

    public static int getNumberOfRowInCsvFile(String csvFileName) {
        return getCsvDataFromFile(csvFileName).size();
    }

    public static String[] getSpecificRowDataUsingRowIndex(String csvFileName, int rowIndex) {
        return getCsvDataFromFile(csvFileName).get(rowIndex);
    }

    public static void writeOrEditCsvFileUsingRows(String csvFileName, List<String> row) {
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";
        int length = 0;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(csvFileName, true);
            length = row.size();
            for (int i = 0; i < length; i++) {
                if (row.get(i) != null)
                    fileWriter.append((String) row.get(i));
                if (i != length - 1) {
                    fileWriter.append(COMMA_DELIMITER);
                }
            }
            fileWriter.append(NEW_LINE_SEPARATOR);
            Log.Comment("CSV file was created successfully !!!", config);
        } catch (Exception e) {
            Log.Comment("Error in CsvFileWriter !!!", config);
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                Log.Comment("Error while flushing/closing fileWriter !!!", config);
                e.printStackTrace();
            }
        }
    }

    public static void writeOrEditCsvFileUsingColumns(String fileNameOfCSV, List<String> column) {
        final String NEW_LINE_SEPARATOR = "\n";
        int length = 0;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileNameOfCSV, false);
            length = column.size();
            for (int i = 0; i < length; i++) {
                if (column.get(i) != null)
                    fileWriter.append(column.get(i));
                if (i != length - 1) {
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
            Log.Comment("CSV file was created successfully !!!", config);
        } catch (Exception e) {
            Log.Comment("Error in CsvFileWriter !!!", config);
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                Log.Comment("Error while flushing/closing fileWriter !!!", config);
                e.printStackTrace();
            }
        }
    }


}
