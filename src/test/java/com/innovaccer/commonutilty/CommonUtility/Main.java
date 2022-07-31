package com.innovaccer.commonutilty.CommonUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.innovaccer.utils.AesEncrypter;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.TestDataReader;
import com.innovaccer.utils.dbconnection.ColumnMappingOfTables;
import com.innovaccer.utils.dbconnection.ElasticConnection;
import com.innovaccer.utils.dbconnection.GreenplumConnection;
import com.innovaccer.utils.dbconnection.MongoConnection;

import cucumber.api.Scenario;

/**
 * 
 * @author pramod.singh
 *
 */
public class Main {
 public static void main(String [] str) {
//	 String localConfigPath = System.getProperty("user.dir") + File.separator + "src/test/resources/Config/"
//				+ "Config.properties";
//	 Config testConfig = new Config(localConfigPath);
	 //String excelFilePath =  System.getProperty("user.dir") + File.separator + testConfig.getRunTimeProperty("ColumnMappingSheetOfTable");
	// TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("pd_risk_output", excelFilePath);
	 //String data = testDataReader.GetData(Integer.valueOf(testConfig.getRunTimeProperty("risk_output_ColumnMapping")), "TableName");
//	 ColumnMappingOfTables columnMapping = new ColumnMappingOfTables();
//	 Map<String,String> riskMapping = columnMapping.getRiskOutputColumnMapping(testConfig, 1, "pd_risk_output");
//	 System.out.println(riskMapping);
			 //	 String url = System.getProperty("user.dir")+ "/src/test/resources/APIJSONFile/testAPI.json";
//	 System.out.println(url);
//	 JSONObject obj = APIService.parseJSONFileInJSONObject(url);
//	 obj.put("firstName", "pramod");
//	 System.out.println("First Name : " + obj.get("firstName") + "  Last Name : "+ obj.get("lastName"));
	//String excelFilePathForTestData =  System.getProperty("user.dir") + File.separator + testConfig.getRunTimeProperty("ColumnMappingSheetOfTable");
	 //MongoConnection.getMongoDBConnection(testConfig);
//	 TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("pd_risk_output", excelFilePath);
	 //ElasticConnection.executeSelectQuery(testConfig, "select * from pd_patients_sbr where empi = 'P161046306' limit 1", "pd_patients_sbr");
//	 String abc="Ram";
//	 StringBuilder abc1 = new StringBuilder(abc);
//	 String test=abc;
//	 test="abc";
//	 String date1 = "2014-12-02";
//	 String date2 = "2016-12-01";
//	// String str1 = Helper.getDateDifferenceInFormatyyyy_mm_dd(date1, date2);
//	 String str1=Helper.getDateBeforeOrAfterYearsFromGiveDate(-1, "yyyy-MM-dd", date2);
//	 System.out.println(str1);	
//	 ArrayList<JSONObject> arrayList = MongoConnection.executeMongoQueryAndReturnArrayOfJSON(testConfig, 3);
//	 System.out.println(arrayList.get(0));
	 String strq  = "fffrf{} ffds212121fv : " ;
	 strq=strq.replaceAll("[^a-zA-Z0-9]+","");
	 System.out.println(strq);
	 List<String> list = new ArrayList<String>();
	 System.out.println(list.get(0));
 }
}
