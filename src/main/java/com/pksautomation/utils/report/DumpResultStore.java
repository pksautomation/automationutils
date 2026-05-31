package com.pksautomation.utils.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.codehaus.jackson.map.ObjectMapper;
import com.pksautomation.utils.Config;
import cucumber.api.Scenario;

public class DumpResultStore {
	
	private static String ticketId = null;
	private static String product = null;
	private static String env = null;
	private static String productType = null;
	private static String execution_time = null;
	private static File fl;
	private static Boolean suite_start_pointer = false ;
	private static ResultStore  resultStore;
	

	public static void resultFileConfigure(String resultStorefile){
		
	     fl = new File(resultStorefile);
		if(fl.exists()) {
			fl.delete();
		}
}
	
	public static void storeExecutionResultQuality(Config testConfig, String resultStorefile,Scenario scenario, long duration, String start_at_time, String end_at_time,boolean ...isScenarioFailed) throws Exception{

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
		long executionDuration = sdf.parse(end_at_time).getTime() - sdf.parse(start_at_time).getTime();
		executionDuration = TimeUnit.SECONDS.convert((long) executionDuration, TimeUnit.MILLISECONDS);
		
		 BufferedWriter bw = new BufferedWriter(new FileWriter(new File(resultStorefile) , true));
		 env = testConfig.getRunTimeProperty("projectName").toUpperCase();
		 
	     String scenarioStatus;
	     if(isScenarioFailed.length>0) {
	    	 if(isScenarioFailed[0])
	    		 scenarioStatus="failed";
	    	 else
	    		 scenarioStatus="passed";
	     }
	     else {
	    	 scenarioStatus=scenario.getStatus().toString();
	     }
		 if(scenarioStatus.equalsIgnoreCase("passed")) {
			 scenarioStatus = "PASS";	
		 } 
		 else if (scenarioStatus.equalsIgnoreCase("failed")) {
			 scenarioStatus = "FAIL";
		 }
		  String scenarioName = scenario.getName().replaceAll("'", "");
		 List<String> tags = new ArrayList<String>(scenario.getSourceTagNames());
	     for(int i = 0; i < tags.size(); i++) {
	    	if(tags.get(i).contains("@Product")) {
	    		product = tags.get(i).split("_")[1];
	    		productType = tags.get(i).split("_")[2];
	    		
	    	}
	    	if(tags.get(i).contains("@TestCaseKey")) {
	    		 ticketId = tags.get(i).split("=")[1];
	    	}
	     }
		 String rawFeatureName = scenario.getId();
		 String featureName = rawFeatureName.split(";")[0];
		 if(!suite_start_pointer) {
			 execution_time = env.toUpperCase()+"_"+start_at_time.split("T")[0]+"_"+start_at_time.split("T")[1].split(":")[0];
			 suite_start_pointer = true;
		 }
		 
		 resultStore = new ResultStore();
		 resultStore.setStatus(scenarioStatus);
		 resultStore.setDuration_sec(executionDuration);
		 resultStore.setStarted_at(start_at_time);
		 resultStore.setFinished_at(end_at_time);
		 resultStore.setFeature_name(featureName);
		 resultStore.setScenario_name(scenarioName);
		 resultStore.setProduct(product);
		 resultStore.setEnvironment(env);
		 resultStore.setProductType(productType);
		 resultStore.setTicketId(ticketId);
		 resultStore.setsuite_start_pointer(execution_time);
		 resultStore.setTestType("Sanity");

		 String result = new ObjectMapper().writeValueAsString(resultStore);
		 //System.out.println("resultStoreQuery:"+result);
		
		 bw.write(result);
		 bw.newLine();
		 bw.close();

	}
}
