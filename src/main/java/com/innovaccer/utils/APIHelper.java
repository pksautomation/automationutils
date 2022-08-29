package com.innovaccer.utils;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.networknt.schema.*;
import enums.APIMethodType.APIMethodsType;
import filehandling.JSONHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class APIHelper.
 *
 * @author pramod.singh
 */
public class APIHelper {

	public static boolean isCreatedEncryptedFile=false;
	/**
	 *  Execute and Get response on the Basis of Request Method type.
	 * @implNote If logsDetailsMode=true from config file, it will Print response details
	 * @param testConfig
	 * @param fullUrl
	 * @param methodType
	 * @param apiHeaders
	 * @param apiInBody
	 * @author i0465
	 * @return Response object
	 */
	public static Response executeAndGetResponse(Config testConfig, String fullUrl, String methodType, Map<String, String> apiHeaders,String jsonStringBody) {
		return executeAndGetResponse(testConfig,fullUrl,methodType,null,apiHeaders,jsonStringBody,true);
	}
	
	/**
	 * Get Authorization Token From Yaml Config with Proxy Option 
	 * It is optional.
	 * @implNote if it is true then (proxyHost , proxyPort ) will be considered from Yaml.
	 * @author ranjeetkumar-i0803
	 * @param testConfig
	 * @param isProxyEnable
	 * @return
	 */
	public static Response getAuthorizationTokenFromYamlConfig(Config testConfig, boolean ...isProxyEnable) {
		HashMap<String, String> header = new HashMap<String, String>();
		HashMap<String, String> apiParameters = new HashMap<String, String>();
		try {
			testConfig.logComment("<<---------------Need to Get Authorization for this API------------------->>");
   		
			apiParameters.put(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".userName").split("=")[0], YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".userName").split("=")[1]);
			apiParameters.put(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".passWord").split("=")[0], YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".passWord").split("=")[1]);
			
			// Enable when proxy is On
			if(isProxyEnable.length > 0 && isProxyEnable[0]) {
				System.setProperty(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".proxyHost").split("=")[0],YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".proxyHost").split("=")[1]);
				System.setProperty(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".proxyPort").split("=")[0],YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".proxyPort").split("=")[1]);
			}
			
			String fullUrl = YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".baseURL") + YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName").toLowerCase()+".authorizationAPIEndPoint");
			String jsonBody = JSONHandler.createJsonParameters(testConfig,apiParameters);
			header.put("Content-Type", "application/json");
			
			Response response = APIHelper.executeAndGetResponse(testConfig, fullUrl, APIMethodsType.POST.getValue(), header, jsonBody);
  
			return response;
		} catch (Exception e) {
			testConfig.logException("Exception in get Authorization Token From YamlConfig :", e, false);

		}

		return null;
	}
	
	public static Response getAuthorizationTokenFromYamlConfig(Config testConfig, boolean withEncryption, boolean isProxyEnable) {
		HashMap<String, String> header = new HashMap<String, String>();
		HashMap<String, String> apiParameters = new HashMap<String, String>();
		String loginReq = null;
		try {
			testConfig.logComment("<<---------------Need to Get Authorization for this API------------------->>");
   		    if(withEncryption) {
   		    	apiParameters.put("email", YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".userName"));
   		    	apiParameters.put("password", YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".passWord"));
   		    	String jsonBody = JSONHandler.createJsonParameters(testConfig,apiParameters);
   		    	loginReq = JSONHandler.encryptJson(testConfig, jsonBody);

   		    }else {
   		    	apiParameters.put(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".userName").split("=")[0], YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".userName").split("=")[1]);
   				apiParameters.put(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".passWord").split("=")[0], YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".passWord").split("=")[1]);
   			    loginReq = JSONHandler.createJsonParameters(testConfig,apiParameters);
   		    }
			// Enable when proxy is On
			if(isProxyEnable) {
				System.setProperty(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".proxyHost").split("=")[0],YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".proxyHost").split("=")[1]);
				System.setProperty(YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".proxyPort").split("=")[0],YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".proxyPort").split("=")[1]);
			}
			
			String fullUrl = YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".baseURL") + YamlReaderWriter.getYamlValue(testConfig, testConfig.getRunTimeProperty("projectName")+".authorizationAPIEndPoint");
			header.put("Content-Type", "application/json");
			Response response = APIHelper.executeAndGetResponse(testConfig, fullUrl, APIMethodsType.POST.getValue(), header, loginReq);
  
			return response;
		} catch (Exception e) {
			testConfig.logException("Exception in get Authorization Token From YamlConfig :", e, false);

		}

		return null;
				
	}
	

	/**
	 * Execute and get response.
	 *
	 * @author i0465
	 * @param testConfig the test config
	 * @param fullUrl Complete API request URL (baseUrl + command + parameters)
	 * @param methodType the method type
	 * @param apiParameters API Query parameters, if it is null the excel parameters will be used
	 * @param apiHeaders API Headers
	 * @param jsonBody the json body
	 * @param paraminbody the paraminbody
	 * @return complete raw restassured Response
	 */
	public static Response executeAndGetResponse(Config testConfig, String fullUrl, String methodType, Map<String, String> apiParameters, Map<String, String> apiHeaders,String jsonBody,Boolean paraminbody){
		boolean disablecharsetflag=false;
		int responseCode;
		//remove the query params from full url
		String[] cmd = fullUrl.split("\\?");
		String requestUrl = cmd[0];
		
		// Prepare request
		RequestSpecification reqspec = RestAssured.given();
		if(testConfig.getRunTimeProperty("Disable_Encoding")!=null && testConfig.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
			reqspec.urlEncodingEnabled(false);
		}
		if (apiHeaders != null && apiHeaders.size() > 0)
		{   
			if(apiHeaders.containsKey("Content-Type")) {
				String value = apiHeaders.get("Content-Type");	
				if(value.contains("application/fhir+json") || value.contains("application/fhir+xml"))
					disablecharsetflag=true;
			}
			reqspec = reqspec.headers(apiHeaders);
		}
		
		
		
		if(testConfig.getRunTimeProperty("Disable_Encoding")!=null && testConfig.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
			reqspec.urlEncodingEnabled(false);
		}
		
		if(jsonBody!=null && (jsonBody.endsWith(".xlsx") || jsonBody.endsWith(".xls"))){
			reqspec = reqspec.multiPart("file_storage", new File(jsonBody));
		}else if(jsonBody!=null) {
			reqspec = reqspec.body(jsonBody);
		}

		if(apiParameters != null)
		{
			// set request query params from passed in HashMap
			if (apiParameters != null && apiParameters.size() > 0 ) 
			{
				reqspec = reqspec
						.queryParams(apiParameters);
			}
		}
		else if(cmd.length >1){
			String parameters[] = cmd[1].split("&");
			for(int i=0 ; i<parameters.length; i++) {
				String key = parameters[i].split("=")[0];
				String value=parameters[i].split("=")[1];
				reqspec = reqspec
						.queryParam(key, value);
			}
		}

		// Log the request details
		if(testConfig.getRunTimeProperty("logsDetailsMode") != null && testConfig.getRunTimeProperty("logsDetailsMode").equals("true")) {
			reqspec = reqspec.log().all();
		}

		// Execute API
		reqspec = reqspec
				.when();

		Response response = null;
		switch(methodType.toLowerCase())
		{
		case "get":
			response = reqspec
			.get(requestUrl);
			break;
		case "post":
			response = reqspec
			.post(requestUrl);
			break;
		case "delete":
			response = reqspec
			.delete(requestUrl);
			break;
		case "put":
			response = reqspec
			.put(requestUrl);
			break;
		case "patch":
			response = reqspec.patch(requestUrl);
			break;
		}
			
		
		if(testConfig.getRunTimeProperty("logsDetailsMode") != null && testConfig.getRunTimeProperty("logsDetailsMode").equals("true")) {
			response = response
				.then()
				.log().all()
				.extract()
				.response();
			testConfig.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			testConfig.logComment("Response Cookie :"+response.getCookies());
			testConfig.logComment("Response Time :"+response.getTime());
			testConfig.logComment("Response Headers :"+response.getHeaders());
		}
		else {
			testConfig.logComment("RequestUrl :"+requestUrl);
			testConfig.logComment("Request Headers :"+apiHeaders);
			
			if(paraminbody)
				testConfig.logComment("Request Body :"+jsonBody);
			if(cmd.length > 1)
				testConfig.logComment("Request Parameters :"+Arrays.toString(cmd[1].split("&")));
			responseCode=response.getStatusCode();
			if(responseCode != 200 && responseCode != 201) {
				testConfig.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			}
		}
		testConfig.logComment("Response Code :"+response.getStatusCode());
		Browser.wait(testConfig, 5);
		return response;
	}

	/**
	 * Gets the Authorization value, which is required by the API's.
	 *
	 * @param testConfig the test config
	 * @return the authorization header from login API
	 */
	public static HashMap<String, String> getAuthorizationHeaderFromLoginAPI(Config testConfig)
	{
		HashMap<String, String> authorization = new HashMap<String, String>();
		HashMap<String,String> header = new HashMap<String,String>();
		try
		{
			testConfig.logComment("<<---------------Need to Get Authorization for this API------------------->>");
			HashMap<String, String> apiParameters = new HashMap<String, String>();
			apiParameters.put(testConfig.getRunTimeProperty("userName").split("=")[0], testConfig.getRunTimeProperty("userName").split("=")[1]);
			apiParameters.put(testConfig.getRunTimeProperty("password").split("=")[0], testConfig.getRunTimeProperty("password").split("=")[1]);
			String fullUrl = testConfig.getRunTimeProperty("AuthorizationAPIBaseURL") + testConfig.getRunTimeProperty("AuthorizationAPIEndPoint");
			String jsonBody = JSONHandler.createJsonParameters(testConfig,apiParameters);
			header.put("Content-Type", "application/json");
			Response response = executeAndGetResponse( testConfig,  fullUrl,APIMethodsType.POST.getValue(), null,header, jsonBody,true);
			authorization.put("token", response.jsonPath().getString("token"));
			authorization.put("Cookie", response.getHeader("Set-Cookie"));
			testConfig.logComment("<<---------------Got Authorization userVal as:- " + authorization + "------------------->>");
			return authorization;
		}
		catch(Exception e)
		{
			testConfig.logException(e);
		}

		return null;
	}
	
	
	/**
	 * To call API with Parameter in API.
	 *
	 * @author i0465
	 * @param testConfig the test config
	 * @param fullUrl the full url
	 * @param methodType the method type
	 * @param apiParameters the api parameters
	 * @param apiHeaders the api headers
	 * @return the response
	 */
	public static Response executeAndGetResponse(Config testConfig, String fullUrl, String methodType, Map<String, String> apiParameters, Map<String, String> apiHeaders) {
		return executeAndGetResponse(testConfig,fullUrl,methodType,apiParameters,apiHeaders,null,false);
	}


	
	/**
	 * Gets the Authorization value for InAPI, which is required by the API's
	 * @param testConfig
	 * @param apiUrl	API request URL (baseUrl + command)
	 * @author i0465
	 * @param testConfig the test config
	 * @return the authorization header for in API
	 */
	public static HashMap<String, String> getAuthorizationHeaderForInAPI(Config testConfig)
	{
		HashMap<String, String> authorization = new HashMap<String, String>();
		HashMap<String,String> header = new HashMap<String,String>();
		JSONObject jsonObject;
		try
		{
			testConfig.logComment("<<---------------Need to Get Authorization for this API------------------->>");
			HashMap<String, String> apiParameters = new HashMap<String, String>();
			apiParameters.put(testConfig.getRunTimeProperty("userName").split("=")[0], testConfig.getRunTimeProperty("userName").split("=")[1]);
			apiParameters.put(testConfig.getRunTimeProperty("password").split("=")[0], testConfig.getRunTimeProperty("password").split("=")[1]);
			String fullUrl = testConfig.getRunTimeProperty("InAPIAuthorizationAPIBaseURL") + testConfig.getRunTimeProperty("InAPIAuthorizationAPIEndPoint");
			String jsonBody = JSONHandler.createJsonParameters(testConfig,apiParameters);
			header.put("Content-Type", "application/fhir+json; fhirVersion=4.0;");
			Response response = executeAndGetResponse( testConfig,  fullUrl,APIMethodsType.POST.getValue(), null,header, jsonBody,true);
			jsonObject = JSONHandler.parseResponseAsJSON(testConfig, response);
			authorization.put("token", jsonObject.getJSONObject("data").getString("token"));
			authorization.put("cookie", response.getHeader("Set-Cookie"));
			testConfig.logComment("<<---------------Got Authorization userVal as:- " + authorization + "------------------->>");
			return authorization;
		}
		catch(Exception e)
		{
			testConfig.logException(e);
		}

		return null;
	}
	
	/**
	 * Validate Request And Response Schema with Expected Schema Details.
	 * @author ranjeetkumar-i0803
	 * @param testConfig
	 * @param resourceName
	 * @param schemaFileName
	 * @param response
	 * @throws Exception
	 * @throws IOException
	 */
	public static void  validateSchema(Config testConfig, String resourceName, String schemaFileName , Response response) throws Exception{
		
		
	    
	    ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);

        try {
        		InputStream schemaStream = new FileInputStream(new File(schemaFileName));
        		InputStream jsonStream = new ByteArrayInputStream(response.asString().getBytes());
            
        
        	
        
            JsonNode json = objectMapper.readTree(jsonStream);
            //testConfig.logComment("json Node :"+json);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            //testConfig.logComment("Schema :"+schema);
            Set<ValidationMessage> validationResult = schema.validate(json);
            // print validation errors
            if (validationResult.isEmpty()) 
                testConfig.logPass("Schema Validation Pass For --> "+resourceName);
            else 
                validationResult.forEach(vm -> testConfig.logFail("Schema Validation Fail --> " +vm.getMessage()));
            
        }catch (Exception e) {
			testConfig.logException("Schema Validation Exception  : ", e , false);
		}
	}
	

	
	/**
	 *  For API request with Parameter request in Body and key parameter in map
	 * @param testConfig
	 * @param fullUrl
	 * @param methodType
	 * @param apiHeaders
	 * @param apiInBody
	 * @author i0465
	 * @return
	 */
	public static Response executeAndGetResponse(Config testConfig, String fullUrl, String methodType, Map<String, String> apiHeaders,Map<String,String> mapKeyParameter,String apiInBody) {
		return executeAndGetResponse(testConfig,fullUrl,methodType,mapKeyParameter,apiHeaders,apiInBody,true);
	}
	
	/**
	 * Decrypts given String value like Username/Password
	 * 
	 * @param Encrypted String
	 * @return Decrypted String
	 * @author i0465
	 */
	public static String decryptString(Config testConfig, String toBeDecrypt)  {
		PyString result = null;
		try {
			createEncryptCredsFile(testConfig);
			Properties properties = new Properties();
			String pythonConfigPath = System.getProperty("user.dir")+"/src/test/resources/";
			properties.setProperty("python.path", pythonConfigPath);
			PythonInterpreter.initialize(System.getProperties(), properties, new String[] { "" });
			PythonInterpreter pi = new PythonInterpreter();
			pi.exec("from EncryptCreds import to_java_decode_string");
			pi.set("string", new PyString(toBeDecrypt));
			pi.exec("result = to_java_decode_string(string)");
			pi.exec("print(result)");
			result = (PyString) pi.get("result");
		} catch (Exception e) {
			testConfig.logException("Exception in String decode : ", e, false);
		}
		return result.toString();
	}

	/**
	 * Generate Random UUID
	 * 
	 * @param email the email
	 * @return the string
	 */
	public static String GenerateRandomUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	/**
	 *  Encrypt user name and password 
	 * @param testConfig
	 * @param userName
	 * @param Password
	 * @author i0465-pramod.singh
	 * @return --> retyrn encrypted json string
	 */
	public static String encryptUserCred(Config testConfig,String userName,String Password) {
		JSONObject jsonobj = new JSONObject();		
		jsonobj.put("email",userName);
		jsonobj.put("password",Password);
	    return JSONHandler.encryptJson(testConfig,jsonobj.toString());
				
	}
	
	
	/**
	 * Execute and get response.
	 *
	 * @author i0465
	 * @param testConfig the test config
	 * @param fullUrl Complete API request URL (baseUrl + command + parameters)
	 * @param methodType the method type
	 * @param apiParameters API Query parameters, if it is null the excel parameters will be used
	 * @param apiHeaders API Headers
	 * @param jsonBody the json body
	 * @param paraminbody the paraminbody
	 * @return complete raw restassured Response
	 */
	public static Response executeAndGetResponseForMultiPartFormParams(Config testConfig, String fullUrl, String methodType,Map<String, Object> multipartFormParams, Map<String, String> apiHeaders){
		int responseCode;
		String requestUrl = fullUrl;
		RequestSpecification reqspec = RestAssured.given();
		if(testConfig.getRunTimeProperty("Disable_Encoding")!=null && testConfig.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
			reqspec.urlEncodingEnabled(false);
		}
		if (apiHeaders != null && apiHeaders.size() > 0)
		{   
			reqspec = reqspec.headers(apiHeaders);
		}
		for (Entry<String, Object> entry : multipartFormParams.entrySet()) 
			{
				String key = entry.getKey();
				Object value = entry.getValue();
					reqspec = reqspec.multiPart(key, value);
		}

		// Log the request details
		if(testConfig.getRunTimeProperty("logsDetailsMode") != null && testConfig.getRunTimeProperty("logsDetailsMode").equals("true")) {
			reqspec = reqspec.log().all();
		}

		// Execute API
		reqspec = reqspec
				.when();

		Response response = null;
		switch(methodType.toLowerCase())
		{
		case "post":
			response = reqspec
			.post(requestUrl);
			break;
		case "put":
			response = reqspec
			.put(requestUrl);
			break;
		case "patch":
			response = reqspec.patch(requestUrl);
			break;
		}
			
		
		if(testConfig.getRunTimeProperty("logsDetailsMode") != null && testConfig.getRunTimeProperty("logsDetailsMode").equals("true")) {
			response = response
				.then()
				.log().all()
				.extract()
				.response();
			testConfig.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			testConfig.logComment("Response Cookie :"+response.getCookies());
			testConfig.logComment("Response Time :"+response.getTime());
			testConfig.logComment("Response Headers :"+response.getHeaders());
		}
		else {
			testConfig.logComment("RequestUrl :"+requestUrl);
			testConfig.logComment("Request Headers :"+apiHeaders);
			responseCode=response.getStatusCode();
			if(responseCode != 200 && responseCode != 201) {
				testConfig.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			}
		}
		testConfig.logComment("Response Code :"+response.getStatusCode());
		Browser.wait(testConfig, 5);
		return response;
	}
	/**
	 * Create Encrypt Creds File from JAR
	 * @param testConfig
	 * @author i0465
	 */
	public static void createEncryptCredsFile(Config testConfig) {
		if(isCreatedEncryptedFile) 
			return;
		InputStream is = APIHelper.class.getClassLoader().getResourceAsStream("PythonFile/EncryptCreds.py");
		String pythonfilePath = System.getProperty("user.dir")+"/src/test/resources/EncryptCreds.py";
		File file =new File(pythonfilePath);	
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			IOUtils.copy(is, outputStream);	
			isCreatedEncryptedFile=true;
		} catch (Exception e) {
			testConfig.logException(e);
		}		
	}
}

