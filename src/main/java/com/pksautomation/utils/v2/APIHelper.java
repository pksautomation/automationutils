package com.pksautomation.utils.v2;

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
import com.pksautomation.utils.v2.fileutils.*;
//import io.restassured.response.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class APIHelper.
 *
 * @author pramod.singh
 */
public class APIHelper {

	boolean isCreatedEncryptedFile=false;
	private Config configInstance;
    private LoggerUtils loggerUtils;
    private BrowserUtils browserUtils;
    private YamlUtils YamlUtils;
    private JSONUtils jsonUtils;
    
    public APIHelper() {
        init(Config.getConfig());
    }

    public APIHelper(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
        browserUtils = new BrowserUtils(configInstance);
        YamlUtils = new YamlUtils(configInstance);
        jsonUtils =  new JSONUtils(configInstance);
        
    }
    
	/**
	 *  Execute and Get response on the Basis of Request Method type.
	 * @implNote If logsDetailsMode=true from config file, it will Print response details
	 * @param fullUrl
	 * @param methodType
	 * @param apiHeaders
	 * @param apiInBody
	 * @author i0465
	 * @return Response object
	 */
	public Response executeAndGetResponse(String fullUrl, String methodType, Map<String, String> apiHeaders,String jsonStringBody) {
		return executeAndGetResponse(fullUrl,methodType,null,apiHeaders,jsonStringBody,true);
	}
	
	/**
	 * Get Authorization Token From Yaml Config with Proxy Option 
	 * It is optional.
	 * @implNote if it is true then (proxyHost , proxyPort ) will be considered from Yaml.
	 * @author ranjeetkumar-i0803
	 * @param isProxyEnable
	 * @return
	 */
	public Response getAuthorizationTokenFromYamlConfig(boolean ...isProxyEnable) {
		HashMap<String, String> header = new HashMap<String, String>();
		HashMap<String, String> apiParameters = new HashMap<String, String>();
		try {
			loggerUtils.logComment("<<---------------Need to Get Authorization for this API------------------->>");
   		
			apiParameters.put(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".userName").split("=")[0], YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".userName").split("=")[1]);
			apiParameters.put(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".passWord").split("=")[0], YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".passWord").split("=")[1]);
			
			// Enable when proxy is On
			if(isProxyEnable.length > 0 && isProxyEnable[0]) {
				System.setProperty(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".proxyHost").split("=")[0],YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".proxyHost").split("=")[1]);
				System.setProperty(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".proxyPort").split("=")[0],YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".proxyPort").split("=")[1]);
			}
			
			String fullUrl = YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".baseURL") + YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName").toLowerCase()+".authorizationAPIEndPoint");
			String jsonBody = jsonUtils.createJsonParameters(apiParameters);
			header.put("Content-Type", "application/json");
			
			Response response = executeAndGetResponse(fullUrl, APIMethodsType.POST.getValue(), header, jsonBody);
  
			return response;
		} catch (Exception e) {
			loggerUtils.logException("Exception in get Authorization Token From YamlConfig :", e, false);

		}

		return null;
	}
	
	public Response getAuthorizationTokenFromYamlConfig(boolean withEncryption, boolean isProxyEnable) {
		HashMap<String, String> header = new HashMap<String, String>();
		HashMap<String, String> apiParameters = new HashMap<String, String>();
		String loginReq = null;
		try {
			loggerUtils.logComment("<<---------------Need to Get Authorization for this API------------------->>");
   		    if(withEncryption) {
   		    	apiParameters.put("email", YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".userName"));
   		    	apiParameters.put("password", YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".passWord"));
   		    	String jsonBody = jsonUtils.createJsonParameters(apiParameters);
   		    	loginReq = encryptJson(jsonBody);

   		    }else {
   		    	apiParameters.put(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".userName").split("=")[0], YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".userName").split("=")[1]);
   				apiParameters.put(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".passWord").split("=")[0], YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".passWord").split("=")[1]);
   			    loginReq = jsonUtils.createJsonParameters(apiParameters);
   		    }
			// Enable when proxy is On
			if(isProxyEnable) {
				System.setProperty(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".proxyHost").split("=")[0],YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".proxyHost").split("=")[1]);
				System.setProperty(YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".proxyPort").split("=")[0],YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".proxyPort").split("=")[1]);
			}
			
			String fullUrl = YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".baseURL") + YamlUtils.getYamlValue(configInstance.getRunTimeProperty("projectName")+".authorizationAPIEndPoint");
			header.put("Content-Type", "application/json");
			Response response = executeAndGetResponse(fullUrl, APIMethodsType.POST.getValue(), header, loginReq);
  
			return response;
		} catch (Exception e) {
			loggerUtils.logException("Exception in get Authorization Token From YamlConfig :", e, false);

		}

		return null;
				
	}
	
	/**
	 * Execute and get response.
	 * @author i0465
	 * @param fullUrl Complete API request URL (baseUrl + command + parameters)
	 * @param methodType the method type
	 * @param apiParameters API Query parameters, if it is null the excel parameters will be used
	 * @param apiHeaders API Headers
	 * @param jsonBody the json body
	 * @param paraminbody the paraminbody
	 * @return complete raw restassured Response
	 */
	public Response executeAndGetResponse(String fullUrl, String methodType, Map<String, String> apiParameters, Map<String, String> apiHeaders,String jsonBody,Boolean paraminbody){
		boolean disablecharsetflag=false;
		int responseCode;
		//remove the query params from full url
		String[] cmd = fullUrl.split("\\?");
		String requestUrl = cmd[0];
		
		// Prepare request
		RequestSpecification reqspec = RestAssured.given();
		if(configInstance.getRunTimeProperty("Disable_Encoding")!=null && configInstance.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
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
		
		
		
		if(configInstance.getRunTimeProperty("Disable_Encoding")!=null && configInstance.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
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
		if(configInstance.getRunTimeProperty("logsDetailsMode") != null && configInstance.getRunTimeProperty("logsDetailsMode").equals("true")) {
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
			
		
		if(configInstance.getRunTimeProperty("logsDetailsMode") != null && configInstance.getRunTimeProperty("logsDetailsMode").equals("true")) {
			response = response
				.then()
				.log().all()
				.extract()
				.response();
			loggerUtils.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			loggerUtils.logComment("Response Cookie :"+response.getCookies());
			loggerUtils.logComment("Response Time :"+response.getTime());
			loggerUtils.logComment("Response Headers :"+response.getHeaders());
		}
		else {
			loggerUtils.logComment("RequestUrl :"+requestUrl);
			loggerUtils.logComment("Request Headers :"+apiHeaders);
			
			if(paraminbody)
				loggerUtils.logComment("Request Body :"+jsonBody);
			if(cmd.length > 1)
				loggerUtils.logComment("Request Parameters :"+Arrays.toString(cmd[1].split("&")));
			responseCode=response.getStatusCode();
			if(responseCode != 200 && responseCode != 201) {
				loggerUtils.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			}
		}
		loggerUtils.logComment("Response Code :"+response.getStatusCode());
		return response;
	}


	/**
	 * Gets the Authorization value, which is required by the API's.
	 * @return the authorization header from login API
	 */
	public HashMap<String, String> getAuthorizationHeaderFromLoginAPI()
	{
		HashMap<String, String> authorization = new HashMap<String, String>();
		HashMap<String,String> header = new HashMap<String,String>();
		try
		{
			loggerUtils.logComment("<<---------------Need to Get Authorization for this API------------------->>");
			HashMap<String, String> apiParameters = new HashMap<String, String>();
			apiParameters.put(configInstance.getRunTimeProperty("userName").split("=")[0], configInstance.getRunTimeProperty("userName").split("=")[1]);
			apiParameters.put(configInstance.getRunTimeProperty("password").split("=")[0], configInstance.getRunTimeProperty("password").split("=")[1]);
			String fullUrl = configInstance.getRunTimeProperty("AuthorizationAPIBaseURL") + configInstance.getRunTimeProperty("AuthorizationAPIEndPoint");
			String jsonBody = jsonUtils.createJsonParameters(apiParameters);
			header.put("Content-Type", "application/json");
			Response response = executeAndGetResponse(fullUrl,APIMethodsType.POST.getValue(), null,header, jsonBody,true);
			authorization.put("token", response.jsonPath().getString("token"));
			authorization.put("Cookie", response.getHeader("Set-Cookie"));
			loggerUtils.logComment("<<---------------Got Authorization userVal as:- " + authorization + "------------------->>");
			return authorization;
		}
		catch(Exception e)
		{
			loggerUtils.logFailureException(e);
		}

		return null;
	}
	
	
	/**
	 * To call API with Parameter in API.
	 *
	 * @author i0465
	 * @param fullUrl the full url
	 * @param methodType the method type
	 * @param apiParameters the api parameters
	 * @param apiHeaders the api headers
	 * @return the response
	 */
	public Response executeAndGetResponse(String fullUrl, String methodType, Map<String, String> apiParameters, Map<String, String> apiHeaders) {
		return executeAndGetResponse(fullUrl,methodType,apiParameters,apiHeaders,null,false);
	}


	
	/**
	 * Gets the Authorization value for InAPI, which is required by the API's
	 * @param apiUrl	API request URL (baseUrl + command)
	 * @author i0465
	 * @return the authorization header for in API
	 */
	public HashMap<String, String> getAuthorizationHeaderForInAPI()
	{
		HashMap<String, String> authorization = new HashMap<String, String>();
		HashMap<String,String> header = new HashMap<String,String>();
		JSONObject jsonObject;
		try
		{
			loggerUtils.logComment("<<---------------Need to Get Authorization for this API------------------->>");
			HashMap<String, String> apiParameters = new HashMap<String, String>();
			apiParameters.put(configInstance.getRunTimeProperty("userName").split("=")[0], configInstance.getRunTimeProperty("userName").split("=")[1]);
			apiParameters.put(configInstance.getRunTimeProperty("password").split("=")[0], configInstance.getRunTimeProperty("password").split("=")[1]);
			String fullUrl = configInstance.getRunTimeProperty("InAPIAuthorizationAPIBaseURL") + configInstance.getRunTimeProperty("InAPIAuthorizationAPIEndPoint");
			String jsonBody = jsonUtils.createJsonParameters(apiParameters);
			header.put("Content-Type", "application/fhir+json; fhirVersion=4.0;");
			Response response = executeAndGetResponse(fullUrl,APIMethodsType.POST.getValue(), null,header, jsonBody,true);
			jsonObject = jsonUtils.parseResponseAsJSON(response);
			authorization.put("token", jsonObject.getJSONObject("data").getString("token"));
			authorization.put("cookie", response.getHeader("Set-Cookie"));
			loggerUtils.logComment("<<---------------Got Authorization userVal as:- " + authorization + "------------------->>");
			return authorization;
		}
		catch(Exception e)
		{
			loggerUtils.logFailureException(e);
		}

		return null;
	}
	
	/**
	 * Validate Request And Response Schema with Expected Schema Details.
	 * @author ranjeetkumar-i0803
	 * @param resourceName
	 * @param schemaFileName
	 * @param response
	 * @throws Exception
	 * @throws IOException
	 */
	public void validateSchema(String resourceName, String schemaFileName , Response response) throws Exception{
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
            	loggerUtils.logPass("Schema Validation Pass For --> "+resourceName);
            else 
                validationResult.forEach(vm -> loggerUtils.logFail("Schema Validation Fail --> " +vm.getMessage()));
            
        }catch (Exception e) {
			loggerUtils.logException("Schema Validation Exception  : ", e , false);
		}
	}
	

	/**
	 *  For API request with Parameter request in Body and key parameter in map
	 * @param fullUrl
	 * @param methodType
	 * @param apiHeaders
	 * @param apiInBody
	 * @author i0465
	 * @return
	 */
	public Response executeAndGetResponse(String fullUrl, String methodType, Map<String, String> apiHeaders,Map<String,String> mapKeyParameter,String apiInBody) {
		return executeAndGetResponse(fullUrl,methodType,mapKeyParameter,apiHeaders,apiInBody,true);
	}
	
	
	/**
	 * Encrypts given Json Body
	 * @param Jason body String
	 * @return Encrypted json String
	 */
	public String encryptJson(String toBeEncrupt) {
		PyString result = null;
		try {
			createEncryptCredsFile();
			Properties properties = new Properties();
			String pythonConfigPath = System.getProperty("user.dir")+"/src/test/resources/";
			properties.setProperty("python.path", pythonConfigPath);
			PythonInterpreter.initialize(System.getProperties(), properties, new String[] { "" });
			PythonInterpreter pi = new PythonInterpreter();
			pi.exec("from EncryptCreds import to_java_encode_json");
			pi.set("string", new PyString(toBeEncrupt));
			pi.exec("result = to_java_encode_json(string)");
			pi.exec("print(result)");
			result = (PyString) pi.get("result");
		} catch (Exception e) {
			loggerUtils.logException("Exception in JsonEncryption : ", e, false);
		}
		return result.toString();

	}

	/**
	 * Decrypts given String value like Username/Password
	 * @param Encrypted String
	 * @return Decrypted String
	 * @author i0465
	 */
	public String decryptString(String toBeDecrypt)  {
		PyString result = null;
		try {
			createEncryptCredsFile();
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
			loggerUtils.logException("Exception in String decode : ", e, false);
		}
		return result.toString();
	}

	/**
	 * Generate Random UUID
	 * 
	 * @param email the email
	 * @return the string
	 */
	public String GenerateRandomUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	/**
	 *  Encrypt user name and password
	 * @param userName
	 * @param Password
	 * @author i0465-pramod.singh
	 * @return --> retyrn encrypted json string
	 */
	public  String encryptUserCred(String userName,String Password) {
		JSONObject jsonobj = new JSONObject();		
		jsonobj.put("email",userName);
		jsonobj.put("password",Password);
	    return encryptJson(jsonobj.toString());
				
	}
	
	/**
	 * Execute and get response.
	 *
	 * @author i0465
	 * @param fullUrl Complete API request URL (baseUrl + command + parameters)
	 * @param methodType the method type
	 * @param apiParameters API Query parameters, if it is null the excel parameters will be used
	 * @param apiHeaders API Headers
	 * @param jsonBody the json body
	 * @param paraminbody the paraminbody
	 * @return complete raw restassured Response
	 */
	public Response executeAndGetResponseForMultiPartFormParams(String fullUrl, String methodType,Map<String, Object> multipartFormParams, Map<String, String> apiHeaders){
		int responseCode;
		String requestUrl = fullUrl;
		RequestSpecification reqspec = RestAssured.given();
		if(configInstance.getRunTimeProperty("Disable_Encoding")!=null && configInstance.getRunTimeProperty("Disable_Encoding").equalsIgnoreCase("true")) {
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
		if(configInstance.getRunTimeProperty("logsDetailsMode") != null && configInstance.getRunTimeProperty("logsDetailsMode").equals("true")) {
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
			
		
		if(configInstance.getRunTimeProperty("logsDetailsMode") != null && configInstance.getRunTimeProperty("logsDetailsMode").equals("true")) {
			response = response
				.then()
				.log().all()
				.extract()
				.response();
			loggerUtils.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			loggerUtils.logComment("Response Cookie :"+response.getCookies());
			loggerUtils.logComment("Response Time :"+response.getTime());
			loggerUtils.logComment("Response Headers :"+response.getHeaders());
		}
		else {
			loggerUtils.logComment("RequestUrl :"+requestUrl);
			loggerUtils.logComment("Request Headers :"+apiHeaders);
			responseCode=response.getStatusCode();
			if(responseCode != 200 && responseCode != 201) {
				loggerUtils.logComment("API Response for " + requestUrl + " :- "+ response.asString());
			}
		}
		loggerUtils.logComment("Response Code :"+response.getStatusCode());
		return response;
	}
	
	/**
	 * Create Encrypt Creds File from JAR
	 * @param testConfig
	 * @author i0465
	 */
	public void createEncryptCredsFile() {
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
			loggerUtils.logException(e);
		}		
	}
}

