package com.innovaccer.utils.v2.reflections;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.commons.collections4.map.SingletonMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;
import com.innovaccer.utils.v2.customexception.CustomRuntimeException;
import com.jayway.restassured.path.json.JsonPath;

public class TestScenarioExecuter {
	 Class<?> aClass;
	    Object classObject;
	    HashMap<String, SingletonMap<Class<?>, Object>> classHashMap = new HashMap<>();
	    Config configInstance;

	    private LoggerUtils loggerUtils;

	    public TestScenarioExecuter() {
	        this.loggerUtils = new LoggerUtils();
	        configInstance=Config.getConfig();
	    }

	    public void executeStep(String packageName, String className, String methodName, String... parameters) throws CustomRuntimeException {
	    	if (!classHashMap.containsKey(className)) {
	            try {
	                aClass = Class.forName(packageName + "." + className);
	            } catch (ClassNotFoundException e) {
	                throw new CustomRuntimeException("Class: " + packageName + "." + className + " not found.");
	            }
	            try {
	                if (methodName.equals("")) {

	                    Constructor<?> constructor = aClass.getConstructor();
	                    classObject = constructor.newInstance();
	                    loggerUtils.logComment("Instance using Constructor Created");
	                } else
	                    classObject = aClass.newInstance();
	            } catch (ReflectiveOperationException e) {
	                throw new CustomRuntimeException
	                        ("Exception while Calling or Finding Constructor with TextContext as parameter");
	            }
	            SingletonMap<Class<?>, Object> singletonMap = new SingletonMap<>(aClass, classObject);
	            classHashMap.put(className, singletonMap);
	        } else {
	            aClass = classHashMap.get(className).getKey();
	            classObject = classHashMap.get(className).getValue();
	        }
	        try {
	            if (!methodName.equals("")) {
	                if (parameters.length > 0)
	                    aClass.getMethod(methodName, parameters.getClass())
	                            .invoke(this.classObject, (Object) parameters);
	                else
	                    aClass.getMethod(methodName).invoke(this.classObject);
	            }
	        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
	            throw new CustomRuntimeException
	                    ("Exception while Invoking or Finding Method: " + methodName);
	        }
	    }
	    
	    private String getValueFromString(String jsonString, String jsonPath) {
	        return JsonPath.from(jsonString).getString(jsonPath);
	    }

	    public void executeScenarioFromJsonFile(String jsonFilePath)
	            throws CustomRuntimeException {
	        String scenario, className, methodName, testData;
	        Config configInstance = Config.getConfig();
	        String categoryName="";
	        try {
	            scenario = new JSONParser().parse(new FileReader(jsonFilePath)).toString();
	        } catch (IOException | ParseException e) {
	            throw new CustomRuntimeException("Scenario Json File not found or is MalFormed");
	        }
	        loggerUtils.logComment("<<<<<< Execution Started for Scenario Id: " +
	                getValueFromString(scenario, "TestCaseId") + " >>>>>>");
	        categoryName=getValueFromString(scenario, "FeatureName");
	        configInstance.getExtentTestLog().assignCategory(categoryName);
	        String packageName = getValueFromString(scenario, "Package");
	        JSONArray listOfSteps = new JSONObject(scenario).getJSONArray("Steps");
	        loggerUtils.logComment("-----Starting Steps Execution for Scenario-----");
	        for (int i = 0; i < listOfSteps.length(); i++) {
	            String step = listOfSteps.get(i).toString();
	            className = JsonPath.from(step.toString()).getString("ClassName");
	            methodName = JsonPath.from(step.toString()).getString("MethodName");
	            testData = JsonPath.from(step.toString()).getString("TestData");
	            if(testData != null && !testData.isEmpty())
	            	configInstance.putRunTimeProperty("TestDataName", testData);
	            if (methodName.equals(""))
	                loggerUtils.logComment(String.format("Calling Constructor for Class: %s", className));
	            else
	                loggerUtils.logComment(String.format("Executing Method: %s.%s()", className, methodName));
	            try {
	                //if (testData.equals("")) {
	                    executeStep(packageName, className, methodName);
//	                } else {
//	                    loggerUtils.logComment("Test Data given to method: " + testData);
//	                    executeStep(packageName, className, methodName, testData);
//	                }
	            } catch (Exception exception) {
	                loggerUtils.logComment(exception.toString());
	                break;
	            }
	        }
	        loggerUtils.logComment("-----Steps Execution for Scenario Completed-----");
	        loggerUtils.logComment("<<<<<< Execution Completed for Scenario >>>>>>");
	    }
}
