package com.innovaccer.utils.v2.fileutils;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextUtils {

    private Config config;
    private LoggerUtils loggerUtils;

    public TextUtils(Config testConfig) {
        init(testConfig);
    }

    public TextUtils() {
        init(Config.getConfig());
    }

    private void init(Config testConfig) {
        this.config = testConfig;
        loggerUtils = new LoggerUtils(config);
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
                loggerUtils.logComment("Data written in " + filename);
            else
                loggerUtils.logComment("Written Value " + text + " in " + filename);
            fileWriter.close();
        } catch (IOException e) {
            loggerUtils.logFailureException(e);
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
            loggerUtils.logFailureException(e);
        }
        return null;
    }


}
