package com.innovaccer.commonutilty.CommonUtility;

import com.innovaccer.utils.v2.cucumber.TestContext;
import com.innovaccer.utils.v2.testNG.TestExecutor;
import reflections.ScenarioRunner;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author pramod.singh
 */
public class Main {

    public static void main(String[] str) throws IOException {
        TestContext testContext = new TestContext();
        String fileNameString = "/Users/adityapandey/Documents/Workspace/automationutils/src/test/resources/%s.json";
        TestExecutor testExecutor = new TestExecutor();
        Object[][] testData = testExecutor.dataProviderMethod();
        ScenarioRunner scenarioRunner = new ScenarioRunner();
        System.out.println(Arrays.deepToString(testData));
        for (Object[] testDatum : testData) {
            String scenarioFileName = String.format(fileNameString, testDatum[0]);
            System.out.println(scenarioFileName);
            scenarioRunner.executeScenarioFromJsonFile(scenarioFileName, testContext);
        }
    }

}