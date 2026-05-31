package com.pksautomation.utils.v2.fileutils;

import com.google.common.base.CharMatcher;
import com.pksautomation.utils.v2.Config;
import com.pksautomation.utils.v2.Helper;
import com.pksautomation.utils.v2.LoggerUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextFileUtils {

    private Config configInstance;
    private LoggerUtils loggerUtils;

    public TextFileUtils(Config testConfig) {
        init(testConfig);
    }

    public TextFileUtils() {
        init(Config.getConfig());
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
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

	/**
	 * This function is used to update in an existing text file. (If file is not
	 * present then will create new file also)
	 * 
	 * @param testConfig
	 * @param location
	 * @param textToUpdate
	 * @author pksautomation
	 */
	public  void updateTextFile(String location, String textToUpdate) {
		try {
			Path pathToFile = Paths.get(location);
			Files.createDirectories(pathToFile.getParent());

			File file = new File(location);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fstream = new FileWriter(location, true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(textToUpdate + ",");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This Method is used to create a file with given format
	 * 
	 * @param extension
	 * @author pksautomation
	 * @return -- File Path
	 */
	public  String createFileWithGivenFormat(Config testConfig, String extension) {
		String datetime = Helper.getCurrentDateTime("yyyy-MM-dd HH:mm:ss.SSS");
		loggerUtils.logComment("datetime=" + datetime);
		datetime = CharMatcher.is(':').removeFrom(datetime);
		String newFilePath = testConfig.getDownloadPath();
		File file = new File(newFilePath, datetime + extension);
		try {
			file.createNewFile();
			newFilePath = newFilePath + datetime + extension;
		} catch (IOException e) {
			newFilePath = null;
			e.printStackTrace();
		}
		return newFilePath;
	}

}
