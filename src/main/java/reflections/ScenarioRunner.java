package reflections;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.CustomRuntimeException;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;
import com.jayway.restassured.path.json.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class ScenarioRunner {

    private Reflections reflections;
    private Config testConfig;
    private LoggerUtils loggerUtils;

    public ScenarioRunner(Config config) {
        this.testConfig = config;
        this.reflections = new Reflections(this.testConfig);
        this.loggerUtils = new LoggerUtils(this.testConfig);
    }

    private String getValueFromString(String jsonString, String jsonPath) {
        return JsonPath.from(jsonString).getString(jsonPath);
    }

    public void executeScenarioFromJsonFile(String jsonFilePath, TestContext testContext)
            throws CustomRuntimeException {
        String scenario, className, methodName, testData;
        try {
            scenario = new JSONParser().parse(new FileReader(jsonFilePath)).toString();
        } catch (IOException | ParseException e) {
            throw new CustomRuntimeException("Scenario Json File not found or is MalFormed");
        }
        loggerUtils.logComment("<<<<<< Execution Started for Scenario Id: " +
                getValueFromString(scenario, "TestCaseId") + " >>>>>>");
        String packageName = getValueFromString(scenario, "Package");
        JSONArray listOfSteps = new JSONObject(scenario).getJSONArray("Steps");
        loggerUtils.logComment("-----Starting Steps Execution for Scenario-----");
        for (int i = 0; i < listOfSteps.length(); i++) {
            String step = listOfSteps.get(i).toString();
            className = JsonPath.from(step.toString()).getString("ClassName");
            methodName = JsonPath.from(step.toString()).getString("MethodName");
            testData = JsonPath.from(step.toString()).getString("TestData");
            if (methodName.equals(""))
                loggerUtils.logComment(String.format("Calling Constructor for Class: %s", className));
            else
                loggerUtils.logComment(String.format("Executing Method: %s.%s()", className, methodName));
            try {
                if (testData.equals("")) {
                    reflections.executeStep(packageName, className, methodName, testContext);
                } else {
                    loggerUtils.logComment("Test Data given to method: " + testData);
                    reflections.executeStep(packageName, className, methodName, testContext, testData);
                }
            } catch (Exception exception) {
                loggerUtils.logComment(exception.toString());
                break;
            }
        }
        loggerUtils.logComment("-----Steps Execution for Scenario Completed-----");
        loggerUtils.logComment("<<<<<< Execution Completed for Scenario >>>>>>");
    }

}