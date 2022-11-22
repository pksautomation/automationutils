package com.innovaccer.commonutilty.CommonUtility;

import com.innovaccer.utils.v2.cucumber.TestContext;
import com.innovaccer.utils.v2.testNG.TestExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reflections.ScenarioRunner;

public class TestNgRunner {

    private TestContext testContext;
    private ScenarioRunner scenarioRunner;
    String fileNameString = System.getProperty("user.dir") + "/src/test/resources/%s.json";

    @BeforeClass
    public void setup() {
        this.testContext = new TestContext();
        this.scenarioRunner = new ScenarioRunner(this.testContext.scenarioContext);
    }

    @Test(dataProvider = "testData", dataProviderClass = TestExecutor.class)
    public void test(String testScenarioId, String testScenarioName) {
        String scenarioFileName = String.format(fileNameString, testScenarioId);
        scenarioRunner.executeScenarioFromJsonFile(scenarioFileName, testContext);
    }
}
