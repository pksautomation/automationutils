package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;

public class LogoutPage {

    public LogoutPage() {
        System.out.println("Logout Page Initialised");
    }

    public LogoutPage(TestContext testContext) {
        System.out.println("Logout Page Initialised from Test Context Constructor");
    }

    public void clickOnLogoutButton() {
        System.out.println("Logout Button Clicked");
    }

    public void validateLogoutPage() {
        System.out.println("Logout Page Validated");
    }
}
