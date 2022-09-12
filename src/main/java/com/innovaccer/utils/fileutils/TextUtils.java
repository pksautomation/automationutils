package com.innovaccer.utils.fileutils;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.LoggerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextUtils {

    private Config config;
    private LoggerUtils loggerHelper;

    public TextUtils(Config testConfig) {
        config = testConfig;
        loggerHelper = new LoggerUtils(config);
    }

    /**
     * @param filename
     * @param text
     * @param logDataToBeWritten
     */
    public void writeTextFile(String filename, String text, Boolean... logDataToBeWritten) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write(text);
            if (logDataToBeWritten != null && logDataToBeWritten.length > 0 && !logDataToBeWritten[0])
                loggerHelper.logComment("Data written in " + filename);
            else
                loggerHelper.logComment("Written Value " + text + " in " + filename);
            fileWriter.close();
        } catch (IOException e) {
            loggerHelper.logException(e);
        }
    }

    /**
     * *
     *
     * @param row       -> row number
     * @param column    -> column number
     * @param fileName  -> text file name
     * @param separator -> character separator used in the file
     * @return data at row/column position
     */
    public String getDataFromTextFile(int row, int column, String fileName, String separator) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String rowData = null;
            int j = 0;
            while (j <= row) {
                rowData = bufferedReader.readLine();
                j++;
            }
            bufferedReader.close();
            String[] arraylist = rowData.split("\\|");
            return arraylist[column];
        } catch (Exception e) {
            loggerHelper.logException(e);
        }
        return null;
    }


}
