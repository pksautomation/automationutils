package com.pksautomation.utils.dbconnection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pksautomation.utils.APIHelper;
import com.pksautomation.utils.Browser;
import com.pksautomation.utils.Config;
import com.pksautomation.utils.Helper;
import com.pksautomation.utils.TestDataReader;
import com.jayway.restassured.response.Response;

import enums.APIMethodType.APIMethodsType;

/**
 * 
 * @author pksautomation
 *
 */
public class ElasticConnection {

	/**
	 * 
	 * @param testConfig
	 * @param query
	 * @return JSON String
	 * @author pksautomation
	 */
	private static String convertSQLQueryToJSONStrinPayLoad(Config testConfig, String selectQuery) {
		String jsonString=null;
		Response response=null;
		String apiFullURL=null;
		Map<String,String> header = new HashMap<String,String>();
		//header.put("Content-Type", ContentType.TEXT_XML.getMimeType());
		header.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		apiFullURL = testConfig.getRunTimeProperty("ElasticDBConnectionURL")+testConfig.getRunTimeProperty("EndpointForPayloadConverterAPI");
		response=APIHelper.executeAndGetResponse(testConfig, apiFullURL, APIMethodsType.POST.getValue(), null, header, selectQuery, true);
		jsonString=APIHelper.parseResponseAsJSON(testConfig, response).toString();
		//testConfig.logComment("Payload :" + jsonString.toString());
		return jsonString;
	}
	
	/**
	 * Execute select Query on elastic DB
	 * @param testConfig
	 * @param sqlSelectQuery
	 * @param indexName
	 * @return JSONObject
	 * @author pksautomation
	 */
	public static JSONArray executeSelectQuery(Config testConfig,String sqlSelectQuery,String indexName) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArrayResponse=null;
		Response response=null;
		String apiFullURL=null;
		Map<String,String> header = new HashMap<String,String>();

		header.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
		try {
			String domainurl=testConfig.getRunTimeProperty("ElasticDBConnectionURL").toString();
			apiFullURL = domainurl + "/"+ indexName + "/" + testConfig.getRunTimeProperty("EndPointForSearchQuery");
			sqlSelectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, sqlSelectQuery);
			testConfig.logComment("Running Select Query : " + sqlSelectQuery);
			testConfig.logComment("API Full URL :" + apiFullURL);
			String payLoad = convertSQLQueryToJSONStrinPayLoad(testConfig, sqlSelectQuery);
			response = APIHelper.executeAndGetResponse(testConfig, apiFullURL, APIMethodsType.POST.getValue(), null,
					header, payLoad, true);
			jsonObject = APIHelper.parseResponseAsJSON(testConfig, response);

			JSONObject jsonHits = jsonObject.getJSONObject("hits");
			jsonArrayResponse = jsonHits.getJSONArray("hits");
			// System.out.println("Query Result" + response.toString());
		} catch (Exception e) {
			testConfig.logException(e);
		}
		return jsonArrayResponse;
	}
	/**
	 * Execute elastic query and return result in JSON Array Form
	 * @param testConfig
	 * @param elasticsqlRowNum
	 * @param indexName
	 * @return
	 * @author pksautomation
	 */
	public static JSONArray executeSelectQuery(Config testConfig,int elasticsqlRowNum,String indexName) {
		String excelFilePath = System.getProperty("user.dir") +File.separator
				+ testConfig.getRunTimeProperty("QueryExcelSheetPath");	
		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("ElasticQuery", excelFilePath);
		String elasticSelectQuery = testDataReader.GetData(elasticsqlRowNum, "Query", true);		
		return ElasticConnection.executeSelectQuery(testConfig, elasticSelectQuery,indexName);
	}
	/**
	 *  Execute delete query on elastic on basis of elastic query
	 * @param testConfig
	 * @param sqlSelectQuery
	 * @param indexName
	 * @return
	 * @author pksautomation
	 */
	public static int executeDeleteQuery(Config testConfig,String sqlSelectQuery,String indexName) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArrayResponse;
		Response response=null;
		String apiFullURL=null;
		Map<String,String> header = new HashMap<String,String>();
		testConfig.logComment("Run delete query on index " + indexName);
		header.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
		String baseUrl = testConfig.getRunTimeProperty("ElasticDBConnectionURL").toString();
		apiFullURL = baseUrl+ "/" + indexName+ "/" +testConfig.getRunTimeProperty("EndPointForDeleteQuery");
		sqlSelectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, sqlSelectQuery);
		testConfig.logComment("Query : " + sqlSelectQuery);
		String payLoad = convertSQLQueryToJSONStrinPayLoad(testConfig,sqlSelectQuery);
		if(indexName == null || indexName.isEmpty())
			return -1;
		
		response=APIHelper.executeAndGetResponse(testConfig, apiFullURL, APIMethodsType.DELETE.getValue(), null, header, payLoad, true);
		jsonObject=APIHelper.parseResponseAsJSON(testConfig, response);
		JSONArray jsonArray = jsonObject.getJSONArray("failures");
		JSONObject jsonIndecesObject = jsonObject.getJSONObject("_indices");
		JSONObject jsonAllObject = jsonIndecesObject.getJSONObject("_all");
		if ((jsonAllObject.get("found")== jsonAllObject.get("deleted"))&& (jsonArray ==null || jsonArray.length() == 0))
		 return 0;
		else
			return -1;
	}
	
	/**
	 * execute Delete Query from elastic database
	 * @param testConfig
	 * @param elasticsqlRowNum
	 * @param indexName
	 * @author pksautomation
	 * @return
	 */
	public static int executeDeleteQuery(Config testConfig,int elasticsqlRowNum,String indexName) {
		int status=-1;
		int count=0;
		String excelFilePath = System.getProperty("user.dir") +File.separator
				+ testConfig.getRunTimeProperty("QueryExcelSheetPath");
		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("ElasticQuery", excelFilePath);
		String elasticSelectQuery = testDataReader.GetData(elasticsqlRowNum, "Query", true);
		Browser.wait(testConfig, 2);
		while(status !=-1 || count<2) {
			status=ElasticConnection.executeDeleteQuery(testConfig, elasticSelectQuery,indexName);
			if(status != -1)
				break;
			count++;
		}
		return status;
	}
}
