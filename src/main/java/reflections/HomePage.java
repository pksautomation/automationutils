package reflections;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;

public class HomePage {

    private LoggerUtils loggerUtils;

    public HomePage(TestContext testContext) {
        this.loggerUtils = new LoggerUtils(testContext.scenarioContext);
    }

    public void clickOnHomePage() {
        loggerUtils.logComment("Home Page Button Clicked");
    }

    public void validateHomePage() {
        loggerUtils.logComment("Home Page Validated");
    }
}
