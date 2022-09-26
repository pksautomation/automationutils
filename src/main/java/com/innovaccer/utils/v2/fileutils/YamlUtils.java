package com.innovaccer.utils.v2.fileutils;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.YamlReaderWriter;

import java.util.Map;

public class YamlUtils extends YamlReaderWriter {

    private Config testConfig;

    public YamlUtils(Config testConfig) {
        this.testConfig = testConfig;
    }
    public YamlUtils() {
        this.testConfig = Config.getConfig();
    }
    

    public void setYamlPath(String filePath) {
        setYamlPath(testConfig, filePath);
    }

    public Map<String, Object> loadYamlFile() {
        YamlReaderWriter.yamlMap = loadYamlFile(testConfig);
        return YamlReaderWriter.yamlMap;
    }

    public String getYamlValue(String yamlPath) {
        return getYamlValue(testConfig, yamlPath);
    }

    public Map<String, Object> getYamlValues(String yamlPath) {
        return getYamlValues(testConfig, yamlPath);
    }

    public void addRuntimeYamlValue(String key, Object value) {
        addRuntimeYamlValue(testConfig, key, value);
    }

    public void addRuntimeYamlValue(String key, Object value, String yamlParentPath) {
        addRuntimeYamlValue(testConfig, key, value, yamlParentPath);
    }

}
