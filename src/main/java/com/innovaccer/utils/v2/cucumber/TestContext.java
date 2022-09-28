package com.innovaccer.utils.v2.cucumber;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.UtilityObjectManager;

import java.io.File;

public class TestContext {

    public Config scenarioContext;
    private final UtilityObjectManager utilityObjectManager;

    public TestContext() {
        String localConfigPath = System.getProperty("user.dir") + File.separator
                + "src/test/resources/Config/config.properties";
        scenarioContext = new Config(localConfigPath);
        if (Config.threadLocalConfig == null)
            Config.threadLocalConfig = new ThreadLocal<Config[]>();

        Config.threadLocalConfig.set(new Config[]{scenarioContext});
        utilityObjectManager = new UtilityObjectManager(scenarioContext);
    }

    public UtilityObjectManager getUtilityObjectManager() {
        return utilityObjectManager;
    }
}
