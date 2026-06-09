package com.pksautomation.utils.v2.fileutils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import com.pksautomation.utils.v2.Config;
import com.pksautomation.utils.v2.LoggerUtils;

/**
 * @author Pramod Singh
 *
 */

@SuppressWarnings("unchecked")
public class YamlReaderWriter {

	private static Yaml yaml = new Yaml();
	private static String yamlFilePath = null;
	public static FileReader yamlFileReader = null;
	public static Map<String, Object> yamlMap = null;

	/**
	 * This methods sets yaml file path and read the file
	 * @param testConfig
	 * @param yamlPath
	 */
	public static void setYamlPath(Config testConfig, String filePath) {
		yamlFilePath = filePath;
		new LoggerUtils(testConfig).logComment("Yaml File Path: " + yamlFilePath);
		readYamlFile(testConfig);
	}

	/**
	 * This methods load yaml file
	 * @param testConfig
	 * @return
	 */
	public static Map<String, Object> loadYamlFile(Config testConfig) {
		yamlMap = (Map<String, Object>) yaml.load(yamlFileReader);
		if(yamlMap!=null)
			return yamlMap;
		new LoggerUtils(testConfig).logFail("Yaml file is blank");
		return null;
	}

	/**
	 * This methods Gets value of a key provided in yaml path separated by . 
	 * @param testConfig
	 * @param yamlValuePath
	 * @return
	 */
	public static String getYamlValue(Config testConfig, String yamlValuePath) {
		LoggerUtils loggerUtils = new LoggerUtils(testConfig);
		try {
		if(yamlMap==null)
			loggerUtils.logFail("Yaml File is blank");
		else if(yamlValuePath.equals("") || yamlValuePath.equals(" "))
			loggerUtils.logFail("Provided yaml path is blank");
		else {
			String[] pathValues = yamlValuePath.split("\\.");
			return parseYamlValuePath(yamlMap, yamlValuePath).get(pathValues[pathValues.length - 1]).toString();
		}
		}catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * This methods Gets values of a yaml parent (provide in yamlpath as . separated) in map  
	 * @param testConfig
	 * @param yamlValuePath : Yaml Parent path for which map required . separated
	 * @return
	 */
	public static Map<String, Object> getYamlValues(Config testConfig, String yamlValuePath) {
		LoggerUtils loggerUtils = new LoggerUtils(testConfig);
		try {
		if(yamlMap==null)
			loggerUtils.logFail("Yaml File is blank");
		else if(yamlValuePath.equals("") || yamlValuePath.equals(" "))
			loggerUtils.logFail("Provided yaml path is blank");
		else 
			return parseYamlValuePath(yamlMap, yamlValuePath + ".");

		}catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Parser for yaml path
	 * @param yamlMap
	 * @param yamlValuePath
	 * @return
	 */
	private static Map<String, Object> parseYamlValuePath(Map<String, Object> yamlMap, String yamlValuePath) {
		if (yamlValuePath.contains(".")) {
			String[] pathValues = yamlValuePath.split("\\.");
			yamlMap = parseYamlValuePath((Map<String, Object>) yamlMap.get(pathValues[0]),
					yamlValuePath.replace(pathValues[0] + ".", ""));
		}
		return yamlMap;
	}

	/**
	 * This methods reads yaml file
	 * @param testConfig
	 * @return
	 */
	private static FileReader readYamlFile(Config testConfig) {
		try {
			yamlFileReader = new FileReader(yamlFilePath);
		} catch (FileNotFoundException e) {
			new LoggerUtils(testConfig).logException("File Not Found at :" + yamlFilePath ,e);
			return null;
		}
		return yamlFileReader;
	}

	/**
	 * This methods help in adding or updating runtime direct value in yaml file with no hierarchy
	 * @param testConfig
	 * @param key : Key to be added or updated
	 * @param value : Value for key
	 */
	public static void addRuntimeYamlValue(Config testConfig, String key, Object value) {
		yamlMap.put(key, value);
	}
	
	/**
	 * This methods help in adding or updating runtime value in specific yaml path
	 * @param testConfig
	 * @param key : Key to be added or updated
	 * @param value : Value for key
	 * @param yamlParentPath : Path of parent node for key in yaml file
	 */
	public static void addRuntimeYamlValue(Config testConfig, String key, Object value, String yamlParentPath) {
		getYamlValues(testConfig, yamlParentPath).put(key, value);
	}

}