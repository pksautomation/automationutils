package reflections;

import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;

public class LoginPage {

    private LoggerUtils loggerUtils;

    public LoginPage(TestContext testContext) {
        this.loggerUtils = new LoggerUtils(testContext.scenarioContext);
    }

    public void loginUsingCredentials(String... credentials) {
        loggerUtils.logComment("Logging into Application using Credentials from: " + credentials[0]);
    }

    public void validateSuccessfulLoginUsingUsername(String... username) {
        loggerUtils.logComment("User Login Successful for Username: " + username[0]);
    }

}
