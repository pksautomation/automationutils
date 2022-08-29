package com.innovaccer.utils.filehandlers;

import com.innovaccer.utils.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileHandler {

    public static Config config;

    public TextFileHandler(Config testConfig) {
        config = testConfig;
    }

    public static void writeTextFile(String filename, String text, Boolean... logDataToBeWritten) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write(text);
            if (logDataToBeWritten != null && logDataToBeWritten.length > 0 && !logDataToBeWritten[0])
                config.logComment("Data written in " + filename);
            else
                config.logComment("Written Value " + text + " in " + filename);
            fileWriter.close();
        } catch (IOException e) {
            config.logException(e);
            e.printStackTrace();
        }
    }

    public static String getDataFromTextFile(int row, int column, String fileName, String separator) {
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
            e.printStackTrace();
        }
        return null;
    }


}
