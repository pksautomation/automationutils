package com.innovaccer.utils.v2.cucumber;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.LoggerHelper;
import com.innovaccer.utils.v2.UtilityObjectManager;

public class CommonTestBase extends LoggerHelper {

	public UtilityObjectManager utilityObjectManager;
	public Config scenarioContext;
	public CommonTestBase(TestContext testContext) {
		super(testContext.scenarioContext);
		scenarioContext=testContext.scenarioContext;
		utilityObjectManager= testContext.getUtilityObjectManager();	
	}		
}
