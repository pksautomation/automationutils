package com.innovaccer.utils.v2.fileutils;

import com.innovaccer.utils.YamlReaderWriter;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class YamlUtils extends YamlReaderWriter {

    public FileReader yamlFileReader = null;
    public Map<String, Object> yamlMap = null;
    private final Config testConfig;
    private final Yaml yaml = new Yaml();
    private String yamlFilePath = null;
    private LoggerUtils loggerHelper;

    public YamlUtils(Config testConfig) {
        this.testConfig = testConfig;
        loggerHelper = new LoggerUtils(testConfig);
    }

    public YamlUtils() {
        this.testConfig = Config.getConfig();
    }

    /**
     * This methods sets yaml file path and read the file
     *
     * @param yamlPath
     */
    public void setYamlPath(String filePath) {
        yamlFilePath = filePath;
        loggerHelper.logComment("Yaml File Path: " + filePath);
        readYamlFile();
    }

    /**
     * This methods Gets values of a yaml parent (provide in yamlpath as . separated) in map
     *
     * @param yamlPath : Yaml Parent path for which map required . separated
     * @return
     */
    public Map<String, Object> getYamlValues(String yamlPath) {
        try {
            if (yamlMap == null)
                loggerHelper.logFail("Yaml File is blank");
            else if (yamlPath.equals("") || yamlPath.equals(" "))
                loggerHelper.logFail("Provided yaml path is blank");
            else
                return parseYamlValuePath(yamlMap, yamlPath + ".");

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * This methods help in adding or updating runtime direct value in yaml file with no hierarchy
     *
     * @param key   : Key to be added or updated
     * @param value : Value for key
     */
    public void addRuntimeYamlValue(String key, Object value) {
        yamlMap.put(key, value);
    }

    /**
     * This methods help in adding or updating runtime value in specific yaml path
     *
     * @param key            : Key to be added or updated
     * @param value          : Value for key
     * @param yamlParentPath : Path of parent node for key in yaml file
     */
    public void addRuntimeYamlValue(String key, Object value, String yamlParentPath) {
        getYamlValues(yamlParentPath).put(key, value);
    }

    /**
     * This methods reads yaml file
     */
    private FileReader readYamlFile() {
        try {
            yamlFileReader = new FileReader(yamlFilePath);
        } catch (FileNotFoundException e) {
            loggerHelper.logException("File Not Found at :" + yamlFilePath, e);
            return null;
        }
        return yamlFileReader;
    }

    /**
     * This methods load yaml file
     *
     * @return
     */
    public Map<String, Object> loadYamlFile() {
        yamlMap = yaml.load(yamlFileReader);
        if (yamlMap != null)
            return yamlMap;
        loggerHelper.logFail("Yaml file is blank");
        return null;
    }

    /**
     * This methods Gets value of a key provided in yaml path separated by .
     *
     * @param testConfig
     * @param yamlValuePath
     * @return
     */
    public String getYamlValue(String yamlValuePath) {
        try {
            if (yamlMap == null)
                loggerHelper.logFail("Yaml File is blank");
            else if (yamlValuePath.equals("") || yamlValuePath.equals(" "))
                loggerHelper.logFail("Provided yaml path is blank");
            else {
                String[] pathValues = yamlValuePath.split("\\.");
                return parseYamlValuePath(yamlMap, yamlValuePath).get(pathValues[pathValues.length - 1]).toString();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Parser for yaml path
     *
     * @param yamlMap
     * @param yamlValuePath
     * @return
     */
    private Map<String, Object> parseYamlValuePath(Map<String, Object> yamlMap, String yamlValuePath) {
        if (yamlValuePath.contains(".")) {
            String[] pathValues = yamlValuePath.split("\\.");
            yamlMap = parseYamlValuePath((Map<String, Object>) yamlMap.get(pathValues[0]),
                    yamlValuePath.replace(pathValues[0] + ".", ""));
        }
        return yamlMap;
    }

}
