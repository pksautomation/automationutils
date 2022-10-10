package com.innovaccer.utils.v2.cucumber;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.UtilityObjectManager;

public class CommonTestBase {

    public UtilityObjectManager UtilityObjectManager;
    public Config scenarioContext;
    public LoggerUtils LoggerUtils;

    public CommonTestBase(TestContext testContext) {
        scenarioContext = testContext.scenarioContext;
        UtilityObjectManager = testContext.getUtilityObjectManager();
        LoggerUtils = new LoggerUtils(scenarioContext);
    }
}
