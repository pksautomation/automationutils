package reflections;

import com.jayway.restassured.path.json.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class ScenarioRunner {

    Reflections reflections;

    public ScenarioRunner() {
        reflections = new Reflections();
    }

    private static String getValueFromString(String jsonString, String jsonPath) {
        return JsonPath.from(jsonString).getString(jsonPath);
    }

    public void executePredefinedScenario() {
        List<List<String>> scenario = Arrays.asList(
                Arrays.asList("LoginPage", "loginUsingCredentials", "test@mail.com", "password"),
                Arrays.asList("LoginPage", "validateSuccessfulLoginUsingUsername", "test"),
                Arrays.asList("HomePage", "clickOnHomePage"),
                Arrays.asList("HomePage", "validateHomePage"),
                Arrays.asList("UserPage", "clickOnUserInfoButton"),
                Arrays.asList("UserPage", "validateUserInfoPage", "Some User", "25", "03/12/1998", "123, Some Street, NY"),
                Arrays.asList("LoginPage", "validateSuccessfulLoginUsingUsername", "test")
        );
        for (List<String> step : scenario) {
            if (step.size() > 2) {
                String[] parameters = new String[step.size() - 2];
                step.subList(2, step.size()).toArray(parameters);
//                reflections.executeStep(step.get(0), step.get(1), parameters);
            } else {
//                reflections.executeStep(step.get(0), step.get(1));
            }
        }
    }

    public void executeScenarioFromJsonFile(String jsonFilePath) {
        String scenario, className, methodName, testData;
        try {
            scenario = new JSONParser().parse(new FileReader(jsonFilePath)).toString();
            System.out.println("Id: " + getValueFromString(scenario, "TestCaseId"));
            System.out.println("Description: " + getValueFromString(scenario, "Description"));
            System.out.println("Zephyr Scale Id: " + getValueFromString(scenario, "ZephyrScaleId"));
            System.out.println("Priority: " + getValueFromString(scenario, "Priority"));
            String packageName = getValueFromString(scenario, "Package");
            JSONArray listOfSteps = new JSONObject(scenario).getJSONArray("Steps");
            for (int i = 0; i < listOfSteps.length(); i++) {
                String step = listOfSteps.get(i).toString();
                className = JsonPath.from(step.toString()).getString("ClassName");
                methodName = JsonPath.from(step.toString()).getString("MethodName");
                testData = JsonPath.from(step.toString()).getString("TestData");
                if (testData.equals("")) {
                    reflections.executeStep(packageName, className, methodName);
                } else {
                    reflections.executeStep(packageName, className, methodName, testData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}