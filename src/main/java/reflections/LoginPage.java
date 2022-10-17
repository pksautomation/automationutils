package reflections;

public class LoginPage {

    public LoginPage() {
        System.out.println("Login Page Initialised");
    }

    public void loginUsingCredentials(String... credentials) {
        System.out.println("Logging into Application using Credentials from: " + credentials[0]);
    }

    public void validateSuccessfulLoginUsingUsername(String... username) {
        System.out.println("User Login Successful for Username: " + username[0]);
    }

}
