package reflections;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;

public class UserPage {

    private LoggerUtils loggerUtils;

    public UserPage(TestContext testContext) {
        this.loggerUtils = new LoggerUtils(testContext.scenarioContext);
    }

    public void clickOnUserInfoButton() {
        loggerUtils.logComment("Clicked on User Info Button");
    }

    public void validateUserInfoPage(String... parameters) {
        loggerUtils.logComment("Validating User Page Info using data from: " + parameters[0]);
    }
}