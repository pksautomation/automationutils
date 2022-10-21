package com.innovaccer.commonutilty.CommonUtility;

import com.innovaccer.utils.v2.Config;
import reflections.ScenarioRunner;

import java.io.File;

/**
 * @author pramod.singh
 */
public class Main {
    public static void main(String[] str) {
        String localConfigPath = System.getProperty("user.dir") + File.separator + "src/test/resources/Config/"
                + "defaultConfig.properties";
        Config testConfig = new Config(localConfigPath);
        String fileName = "/Users/adityapandey/Documents/Workspace/automationutils/src/test/resources/ReflectionsScenario.json";
        ScenarioRunner scenarioRunner = new ScenarioRunner();
//        scenarioRunner.executePredefinedScenario();
        scenarioRunner.executeScenarioFromJsonFile(fileName);
    }
}