package reflections;

import com.innovaccer.utils.v2.cucumber.TestContext;
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

    public void executeScenarioFromJsonFile(String jsonFilePath, TestContext testContext) {
        String scenario, className, methodName, testData;
        try {
            System.out.println("-----Execution Started for Scenario-----");
            scenario = new JSONParser().parse(new FileReader(jsonFilePath)).toString();
            System.out.println("Id: " + getValueFromString(scenario, "TestCaseId"));
            System.out.println("Description: " + getValueFromString(scenario, "Description"));
            System.out.println("Zephyr Scale Id: " + getValueFromString(scenario, "ZephyrScaleId"));
            System.out.println("Priority: " + getValueFromString(scenario, "Priority"));
            String packageName = getValueFromString(scenario, "Package");
            JSONArray listOfSteps = new JSONObject(scenario).getJSONArray("Steps");
            System.out.println("-----Starting Steps Execution for Scenario-----");
            for (int i = 0; i < listOfSteps.length(); i++) {
                String step = listOfSteps.get(i).toString();
                className = JsonPath.from(step.toString()).getString("ClassName");
                methodName = JsonPath.from(step.toString()).getString("MethodName");
                testData = JsonPath.from(step.toString()).getString("TestData");
                if (testData.equals("")) {
                    reflections.executeStep(packageName, className, methodName, testContext);
                } else {
                    reflections.executeStep(packageName, className, methodName,  testContext,testData);
                }
            }
            System.out.println("-----Execution Completed for Scenario-----");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}