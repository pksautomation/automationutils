package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;

public class LoginPage {

    public LoginPage() {
        System.out.println("Login Page Initialised");
    }

    public LoginPage(TestContext testContext) {
        System.out.println("Login Page Initialised from Test Context Constructor");
    }

    public void loginUsingCredentials(String... credentials) {
        System.out.println("Logging into Application using Credentials from: " + credentials[0]);
    }

    public void validateSuccessfulLoginUsingUsername(String... username) {
        System.out.println("User Login Successful for Username: " + username[0]);
    }

}
