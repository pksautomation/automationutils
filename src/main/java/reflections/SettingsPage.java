package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;

public class SettingsPage {

    public SettingsPage() {
        System.out.println("Settings Page Initialised");
    }

    public SettingsPage(TestContext testContext) {
        System.out.println("Settings Page Initialised from Test Context Constructor");
    }

    public void clickOnSettingsTab() {
        System.out.println("Settings Tab Clicked");
    }

    public void validateSettingsTab() {
        System.out.println("Settings Tab Validated");
    }
}
