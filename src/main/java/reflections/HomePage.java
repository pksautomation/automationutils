package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;

public class HomePage {

    public HomePage() {
        System.out.println("Home Page Initialised");
    }

    public HomePage(TestContext testContext) {
        System.out.println("Home Page Initialised from Test Context Constructor");
    }

    public void clickOnHomePage() {
        System.out.println("Home Page Button Clicked");
    }

    public void validateHomePage() {
        System.out.println("Home Page Validated");
    }
}
