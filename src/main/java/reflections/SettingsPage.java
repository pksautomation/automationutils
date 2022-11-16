package reflections;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;

public class SettingsPage {

    private LoggerUtils loggerUtils;

    public SettingsPage(TestContext testContext) {
        this.loggerUtils = new LoggerUtils(testContext.scenarioContext);
    }

    public void clickOnSettingsTab() {
        loggerUtils.logComment("Settings Tab Clicked");
    }

    public void validateSettingsTab() {
        loggerUtils.logComment("Settings Tab Validated");
    }
}
