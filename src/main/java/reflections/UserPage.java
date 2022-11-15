package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;

public class UserPage {

    public UserPage() {
        System.out.println("User Page Initialised");
    }

    public UserPage(TestContext testContext) {
        System.out.println("User Page Initialised from Test Context Constructor");
    }

    public void clickOnUserInfoButton() {
        System.out.println("Clicked on User Info Button");
    }

    public void validateUserInfoPage(String... parameters) {
        System.out.println("Validating User Page Info using data from: " + parameters[0]);
    }
}