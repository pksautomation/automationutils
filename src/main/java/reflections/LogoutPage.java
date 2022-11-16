package reflections;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;

public class LogoutPage {

    private LoggerUtils loggerUtils;

    public LogoutPage(TestContext testContext) {
        this.loggerUtils = new LoggerUtils(testContext.scenarioContext);
    }

    public void clickOnLogoutButton() {
        loggerUtils.logComment("Logout Button Clicked");
    }

    public void validateLogoutPage() {
        loggerUtils.logComment("Logout Page Validated");
    }
}
