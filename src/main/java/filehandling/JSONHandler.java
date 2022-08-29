package filehandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.innovaccer.utils.APIHelper;
import com.innovaccer.utils.Browser;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.ISO8601DateFormat;
import com.jayway.restassured.response.Response;

public class JSONHandler {
	
	private static String REGEXP_ISO8061 = "^([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(.([0-9]){3})?(Z|[\\+\\-]([0-9]{2}):([0-9]{2}))$";
    private static Pattern matcherISO8601 = Pattern.compile(REGEXP_ISO8061);
    public static boolean autoConvertISO8601 = true;
	
	//Gets the value from JSONResponse
	public static String getValueFromJson(Config testConfig, Response response, String nodePath) {
		String value = null;
		try {
			value =response.jsonPath().getString(nodePath);
		}catch (Exception e) {
			testConfig.logException("Exception in getValueFromJson :", e , false);
		}
		return value; 
	}
	
	//Gets the values (list of values) from JSONResponse
	public static List<String> getValuesFromJson(Config testConfig, Response response, String nodePath) {
		List<String> value = null;
		try{
			value =response.jsonPath().getList(nodePath);
		}catch (Exception e) {
			testConfig.logException("Exception in getValuesFromJson : ", e, false);
		}
		
		return value; 
	}
	
	//Gets the value (HashMap) from JSONResponse
	public static Map<String, Object> getValueInHashMapFromJson(Config testConfig, Response response, String nodePath) {
		Map<String, Object> valueMap = null;
		try {
			valueMap = response.jsonPath().getMap(nodePath);
		}catch (Exception e) {
			testConfig.logException("Exception in getValueInHashMapFromJson : ", e, false);
		}
		return valueMap; 
	}
	
	//Extracts the Json Body response from the raw Rest Assured Response.
	public static JSONObject parseResponseAsJSON(Config testConfig, Response response)
	{
		String responseAsString = response.asString();
		JSONObject jObject = null;

		switch (response.getStatusCode()) 
		{
		case 504:
			Browser.wait(testConfig, 90);
			break;
		case 505:
			Browser.wait(testConfig, 30);
			break;
		default:
			responseAsString = response.asString();
			break;
		}

		try 
		{
			jObject = new JSONObject(responseAsString);
			return jObject;
		} 
		catch (JSONException e) {
			testConfig.logException(e);
		} 

		return null;
	}
	
	//Extracts the Json Body response from the raw Rest Assured Response.
	public static JSONArray parseResponseAsJSONArray(Config testConfig, Response response)
	{
		String responseAsString = response.asString();
		JSONArray jarray = null;

		switch (response.getStatusCode()) 
		{
		case 504:
			Browser.wait(testConfig, 90);
			break;
		case 505:
			Browser.wait(testConfig, 30);
			break;
		default:
			responseAsString = response.asString();
			break;
		}

		try 
		{
			jarray = new JSONArray(responseAsString);
			return jarray;
		} 
		catch (JSONException e) {
			testConfig.logException(e);
		} 

		return null;
	}
	
	//Convert Map of parameters
	public static String createJsonParameters(Config testConfig, HashMap<String, String> parameters){
		JSONObject jsonPostParameters = new JSONObject();
		for (Entry<String, String> entry : parameters.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			try {
				 jsonPostParameters.put(key, value);
				
			} catch (JSONException e) {
				testConfig.logException(e);
			}
		}
    		return jsonPostParameters.toString();
	}
	
	//Function to convert JSON file to JSON object.
	public static JSONObject parseJSONFileInJSONObject(String fileLocationURL) {
		JSONObject jo = null;
		InputStream  is;
		try {
			is = new FileInputStream(fileLocationURL);
	        JSONTokener tokener = new JSONTokener(is);
	        jo = new JSONObject(tokener);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        // typecasting obj to JSONObject
		
		return jo;
	}
	
	public static JSONArray parseJSONFileInJSONArray(String fileLocationURL) {
		JSONArray ja = null;
		InputStream  is;
		try {
			is = new FileInputStream(fileLocationURL);
	        JSONTokener tokener = new JSONTokener(is);
	        ja = new JSONArray(tokener);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return ja;
	}
	
	//Encrypts the JSON body
	public static String encryptJson(Config testConfig, String toBeEncrupt) {
		PyString result = null;
		try {
			APIHelper.createEncryptCredsFile(testConfig);
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
			testConfig.logException("Exception in JsonEncryption : ", e, false);
		}
		return result.toString();

	}
	
	//Convert LinkedHashMap of parameters to JSONString, maintain insertion order of data in map .
	public static String createJsonParameters(Config testConfig, Map<String, String> parameters){
		LinkedHashMap<String,String> hasMap = new LinkedHashMap<String,String>(parameters);
		JSONObject jsonPostParameters = new JSONObject(hasMap);
		try {
		      Field changeMap = jsonPostParameters.getClass().getDeclaredField("map");
		      changeMap.setAccessible(true);
		      changeMap.set(jsonPostParameters, new LinkedHashMap<>());
		      changeMap.setAccessible(false);
		    } catch (IllegalAccessException | NoSuchFieldException e) {
		      testConfig.logException(e);
		    }
		for (Entry<String, String> entry : parameters.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			try {
				 jsonPostParameters.put(key, value);
				
			} catch (JSONException e) {
				testConfig.logException(e);
			}
		}
    		return jsonPostParameters.toString();
	}
	
	 public static Map<String, Object> convertJSONObjectToMap(JSONObject jo) throws JSONException
	    {
	        Map<String, Object> model = new HashMap<String, Object>();
	        
	        Iterator<String> itr = (Iterator<String>)jo.keys();
	        while (itr.hasNext())
	        {
	            String key = (String)itr.next();
	            
	            Object o = jo.get(key);
	            if (o instanceof JSONObject)
	            {
	                model.put(key, convertJSONObjectToMap((JSONObject)o));
	            }
	            else if (o instanceof JSONArray)
	            {
	                model.put(key, convertJSONArrayToList((JSONArray)o));
	            }
	            else if (o == JSONObject.NULL)
	            {
	                model.put(key, null); // note: http://freemarker.org/docs/dgui_template_exp.html#dgui_template_exp_missing
	            }
	            else
	            {
	                if ((o instanceof String) && autoConvertISO8601 && (matcherISO8601.matcher((String)o).matches()))
	                {
	                    o = ISO8601DateFormat.parse((String)o);
	                }
	                model.put(key, o);
	            }
	        }
	       return model;
	    }
	 
	 public static List<Object> convertJSONArrayToList(JSONArray ja) throws JSONException
	    {
	        List<Object> model = new ArrayList<Object>();
	       
	        for (int i = 0; i < ja.length(); i++)
	        {
	            Object o = ja.get(i);
	            
	            if (o instanceof JSONArray)
	            {
	                model.add(convertJSONArrayToList((JSONArray)o));
	            }
	            else if (o instanceof JSONObject)
	            {
	                model.add(convertJSONObjectToMap((JSONObject)o));
	            }
	            else if (o == JSONObject.NULL)
	            {
	                model.add(null);
	            }
	            else
	            {
	                if ((o instanceof String) && autoConvertISO8601 && (matcherISO8601.matcher((String)o).matches()))
	                {
	                    o = ISO8601DateFormat.parse((String)o);
	                }
	                model.add(o);
	            }
	        }
	       return model;
	    }
	   
	 public static HashMap<String, HashMap<String, String>> readTestData(String filePath, String fileName) throws Throwable {
			JSONArray orgArray;
			JSONObject parentObj,childObj,orgObj;
			HashMap<String,HashMap<String,String>> jsonMapofMap = new HashMap<String,HashMap<String,String>>();
			HashMap<String,String> jsonMap = new HashMap<String,String> ();
			String value="",key="";
			filePath=filePath+fileName+".json";
			InputStream is = new FileInputStream(new File(filePath));
	        JSONTokener tokener = new JSONTokener(is); 
	        try {
				parentObj = new JSONObject(tokener);
				Iterator<String> iterator = parentObj.keys();
				while (iterator.hasNext()) {
					String tagName = iterator.next().toString();
					childObj = parentObj.getJSONObject(tagName);
					Iterator<String> childit = childObj.keys();
					while (childit.hasNext()) {
						key = childit.next().toString();
						if(childObj.get(key) instanceof JSONObject && childObj.get(key).toString().contains("selection") && childObj.get(key).toString().contains("values")) {
							orgObj = childObj.getJSONObject(key);
							orgArray = orgObj.getJSONArray("values");
							value = (String)orgArray.get(orgObj.getInt("selection") - 1);
						}
						else if (childObj.get(key) instanceof JSONObject) {
							value = Helper.generateDynamicValue((JSONObject)childObj.get(key));
							
						}
						else 
							value=childObj.get(key).toString();
					
						jsonMap.put(key,value);
				}
				jsonMapofMap.put(tagName, jsonMap);
				jsonMap = new HashMap<String,String> ();
				}
	        }catch (Exception e) {
	        	e.printStackTrace();
	        }
			
			return jsonMapofMap;
		}
	 
	 
	 public static JSONObject convertStringToJSONObject (String jsonString) throws Throwable {
		 JSONObject jo = null;
		 JSONParser parser = new JSONParser(); 
		 try {
			 jo = (JSONObject) parser.parse(jsonString);  
			 return jo;
		 } catch (Exception e) {
			 e.printStackTrace();
		 } 
		 return jo;
	 }
	 
	 //placeholder
	 public static JSONObject validateJSONObject (JSONObject jo) {
		 return jo;
	 }
	 
	 
}
