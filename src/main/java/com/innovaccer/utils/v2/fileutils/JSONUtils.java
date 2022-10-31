package com.innovaccer.utils.v2.fileutils;

import com.innovaccer.utils.APIHelper;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.ISO8601DateFormat;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.WaitHelper;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import com.google.gson.JsonSyntaxException;
import com.innovaccer.utils.APIHelper;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.ISO8601DateFormat;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.WaitHelper;

//import io.restassured.response.Response;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import com.jayway.restassured.response.Response;

public class JSONUtils {

    boolean autoConvertISO8601 = true;
    private final String REGEXP_ISO8061 = "^([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(.([0-9]){3})?(Z|[\\+\\-]([0-9]{2}):([0-9]{2}))$";
    private final Pattern matcherISO8601 = Pattern.compile(REGEXP_ISO8061);
    private Config configInstance;
    private LoggerUtils loggerUtils;
    private WaitHelper waitUtils;

    public JSONUtils(Config configInstance) {
        init(configInstance);
    }

    public JSONUtils() {
        init(Config.getConfig());
    }

    private void init(Config configInstance) {
        this.configInstance = configInstance;
        loggerUtils = new LoggerUtils(configInstance);
        waitUtils = new WaitHelper(configInstance);
    }

    /**
     * Gets the value from json.
     * @param response the response
     * @param nodePath the node path
     * @return the value from json
     * @author pramod.singh
     */
    public String getValueFromJson(Response response, String nodePath) {
        String value = null;
        try {
            value = response.jsonPath().getString(nodePath);
        } catch (Exception e) {
            loggerUtils.logException("Exception in getValueFromJson :", e, false);
        }
        return value;
    }

    /**
     * Gets the values from json.
     *
     * @param response the response
     * @param nodePath the node path
     * @return the values from json
     * @author ranjeetkumar-i0803
     */
    public List<String> getValuesFromJson(Response response, String nodePath) {
        List<String> value = null;
        try {
            value = response.jsonPath().getList(nodePath);
        } catch (Exception e) {
            loggerUtils.logException("Exception in getValuesFromJson : ", e, false);
        }

        return value;
    }

    /**
     * Gets the value (HashMap) from JSONResponse.
     *
     * @param response the response
     * @param nodePath the node path
     * @return the hashmap of values from json
     * @author ranjeetkumar-i0803
     */
    public Map<String, Object> getValueInHashMapFromJson(Response response, String nodePath) {
        Map<String, Object> valueMap = null;
        try {
            valueMap = response.jsonPath().getMap(nodePath);
        } catch (Exception e) {
            loggerUtils.logException("Exception in getValueInHashMapFromJson : ", e, false);
        }
        return valueMap;
    }

    /**
     * Extracts the Json Body response from the raw restassured Response.
     * @param response   complete raw restassured Response
     * @return Json response body
     */
    public JSONObject parseResponseAsJSON(Response response) {
        String responseAsString = response.asString();
        JSONObject jObject = null;

        switch (response.getStatusCode()) {
            case 504:
                waitUtils.wait(90);
                break;
            case 505:
                waitUtils.wait(30);
                break;
            default:
                responseAsString = response.asString();
                break;
        }

        try {
            jObject = new JSONObject(responseAsString);
            return jObject;
        } catch (JSONException e) {
            loggerUtils.logFailureException(e);
        }

        return null;
    }

    /**
     * Extracts the Json Body response from the raw restassured Response.
     * @param response   complete raw restassured Response
     * @return Json response Array
     */
    public JSONArray parseResponseAsJSONArray(Response response) {
        String responseAsString = response.asString();
        JSONArray jarray = null;

        switch (response.getStatusCode()) {
            case 504:
                waitUtils.wait(90);
                break;
            case 505:
                waitUtils.wait(30);
                break;
            default:
                responseAsString = response.asString();
                break;
        }

        try {
            jarray = new JSONArray(responseAsString);
            return jarray;
        } catch (JSONException e) {
            loggerUtils.logFailureException(e);
        }

        return null;
    }

    /**
     * Convert Map of parameters.
     *
     * @param testConfig the test config
     * @param parameters the parameters
     * @return paramaters in JSON format
     */
    public String createJsonParameters(HashMap<String, String> parameters) {
        JSONObject jsonPostParameters = new JSONObject();
        for (Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                jsonPostParameters.put(key, value);

            } catch (JSONException e) {
                loggerUtils.logFailureException(e);
            }
        }
        return jsonPostParameters.toString();
    }

    /**
     * Function to convert JSON file to JSON object.
     *
     * @param fileLocationURL the file location URL
     * @return the JSON object
     */
    public JSONObject parseJSONFileInJSONObject(String fileLocationURL) {
        JSONObject jo = null;
        InputStream is;
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

    /**
     * Extracts the Json Body response from the raw restassured Response
     *
     * @param testConfig
     * @param response   complete raw restassured Response
     * @return Json response body
     */
    public JSONArray parseJSONFileInJSONArray(String fileLocationURL) {
        JSONArray ja = null;
        InputStream is;
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


    /**
     * Convert LinkedHashMap of parameters to JSONString, maintain insertion order of data in map.
     *
     * @param testConfig the test config
     * @param parameters the parameters --> map of values
     * @return paramaters in JSON format
     * @author i0465 (pramod.singh)
     */
    public String createJsonParameters(Map<String, String> parameters) {
        LinkedHashMap<String, String> hasMap = new LinkedHashMap<String, String>(parameters);
        JSONObject jsonPostParameters = new JSONObject(hasMap);
        try {
            Field changeMap = jsonPostParameters.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(jsonPostParameters, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            loggerUtils.logFailureException(e);
        }
        for (Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                jsonPostParameters.put(key, value);

            } catch (JSONException e) {
                loggerUtils.logFailureException(e);
            }
        }
        return jsonPostParameters.toString();
    }

    /**
     * JSONObject is an unordered collection of name/value pairs -> convert to Map (equivalent to Freemarker "hash")
     *
     * @param jo
     * @return
     * @throws JSONException
     * @author pramod.singh
     */
    public Map<String, Object> convertJSONObjectToMap(JSONObject jo) throws JSONException {
        Map<String, Object> model = new HashMap<String, Object>();
        Iterator<String> itr = jo.keys();
        while (itr.hasNext()) {
            String key = itr.next();

            Object o = jo.get(key);
            if (o instanceof JSONObject) {
                model.put(key, convertJSONObjectToMap((JSONObject) o));
            } else if (o instanceof JSONArray) {
                model.put(key, convertJSONArrayToList((JSONArray) o));
            } else if (o == JSONObject.NULL) {
                model.put(key, null); // note: http://freemarker.org/docs/dgui_template_exp.html#dgui_template_exp_missing
            } else {
                if ((o instanceof String) && autoConvertISO8601 && (matcherISO8601.matcher((String) o).matches())) {
                    o = ISO8601DateFormat.parse((String) o);
                }
                model.put(key, o);
            }
        }
        return model;
    }

    /**
     * JSONArray is an ordered sequence of values -> convert to List (equivalent to Freemarker "sequence")
     *
     * @author pramod.singh
     */
    public List<Object> convertJSONArrayToList(JSONArray ja) throws JSONException {
        List<Object> model = new ArrayList<Object>();

        for (int i = 0; i < ja.length(); i++) {
            Object o = ja.get(i);

            if (o instanceof JSONArray) {
                model.add(convertJSONArrayToList((JSONArray) o));
            } else if (o instanceof JSONObject) {
                model.add(convertJSONObjectToMap((JSONObject) o));
            } else if (o == JSONObject.NULL) {
                model.add(null);
            } else {
                if ((o instanceof String) && autoConvertISO8601 && (matcherISO8601.matcher((String) o).matches())) {
                    o = ISO8601DateFormat.parse((String) o);
                }
                model.add(o);
            }
        }
        return model;
    }

    /* Read data from JSON File and return Map of Map with all the details
     * @return Map of Map of String
     * @author nikita.gatagat
     */
    public HashMap<String, HashMap<String, String>> readTestData(String filePath, String fileName) throws Throwable {
        JSONArray orgArray;
        JSONObject parentObj, childObj, orgObj;
        HashMap<String, HashMap<String, String>> jsonMapofMap = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> jsonMap = new HashMap<String, String>();
        String value = "", key = "";
        filePath = filePath + fileName + ".json";
        InputStream is = new FileInputStream(new File(filePath));
        JSONTokener tokener = new JSONTokener(is);
        try {
            parentObj = new JSONObject(tokener);
            Iterator<String> iterator = parentObj.keys();
            while (iterator.hasNext()) {
                String tagName = iterator.next();
                childObj = parentObj.getJSONObject(tagName);
                Iterator<String> childit = childObj.keys();
                while (childit.hasNext()) {
                    key = childit.next();
                    if (childObj.get(key) instanceof JSONObject && childObj.get(key).toString().contains("selection") && childObj.get(key).toString().contains("values")) {
                        orgObj = childObj.getJSONObject(key);
                        orgArray = orgObj.getJSONArray("values");
                        value = (String) orgArray.get(orgObj.getInt("selection") - 1);
                    } else if (childObj.get(key) instanceof JSONObject) {
                        value = generateDynamicValue((JSONObject) childObj.get(key));

                    } else
                        value = childObj.get(key).toString();

                    jsonMap.put(key, value);
                }
                jsonMapofMap.put(tagName, jsonMap);
                jsonMap = new HashMap<String, String>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonMapofMap;
    }


    /* Convert String Object to JSON Object
     * @param String jsonString
     * @return JSONObject
     * @throws JSONException
     * @author nikita.gatagat
     */
    public JSONObject convertStringToJSONObject(String jsonString) throws Throwable {
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

    //placeholder to validate the JSON File and return the correct JSONObject
    public JSONObject validateJSONObject(String filePathName) {
        JSONObject jo = null;
        return jo;
    }
    
    /**
	 * 
	 * @param dynamicObje
	 * @return
	 * @throws Throwable
	 */
	public  String generateDynamicValue(JSONObject dynamicObje) throws Throwable{
    	int length=dynamicObje.getInt("length");
    	String email="",prefix="",suffix="",dynamicValue="";
		try { 
			switch(dynamicObje.getString("type")) {
				case "NUMERIC":
					dynamicValue=(String.valueOf(Helper.generateRandomNumber(length)));
					break;
				case "ALPHABETIC":
					dynamicValue=(String.valueOf(Helper.generateRandomAlphabetsString(length)));
					break;
				case "ALPHA_NUMERIC":
					dynamicValue=(String.valueOf(Helper.generateRandomAlphaNumericString(length)));
					break;
			}
			if(dynamicObje.has("prefix") && !dynamicObje.isNull("prefix")) {
				prefix=dynamicObje.getString("prefix");
			}
			if(dynamicObje.has("suffix") && !dynamicObje.isNull("suffix")) {
				suffix=dynamicObje.getString("suffix");
			}
		dynamicValue=prefix+dynamicValue+suffix;						
		
    	}catch (Exception e) {
    		loggerUtils.logException(e);
    		
    	}
    	return dynamicValue;
    }
	
	/**
	 * Parse from Document Object into JSONObject
	 * 
	 * @param doc
	 * @author i0465
	 * @return JSONObject
	 */
	public static JSONObject pareseDocumentIntoJSONObject(Document doc) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(doc.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * Returns a JSON key from JSON object
	 * @author i0465
	 */
	public String getJSONKeyValue(Config testConfig, JSONObject jObject, String key) {
		String value = null;
		if (jObject != null) {
			try {
				if (key != null)
					value = jObject.get(key).toString();
			} catch (JSONException e) {
				loggerUtils.logException(e);
			}
		}
		return value;
	}
}
