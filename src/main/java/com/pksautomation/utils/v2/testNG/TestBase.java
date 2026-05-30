package com.innovaccer.utils.v2.testNG;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import org.testng.internal.TestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.Helper;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.UtilityObjectManager;
import com.innovaccer.utils.v2.cucumber.TestContext;
import com.innovaccer.utils.v2.customexception.CustomRuntimeException;
import com.innovaccer.utils.v2.fileutils.ExcelUtils;
import com.jayway.restassured.path.json.JsonPath;

import org.testng.annotations.BeforeMethod;

/**
 * 
 * @author i0465
 *
 */
@Listeners(com.innovaccer.utils.v2.testNG.TestListener.class)
public class TestBase implements ITest{
	 public static ThreadLocal<String> testName = new ThreadLocal<>();
	 private LoggerUtils loggerUtils;
	 private ExcelUtils excel;
	 protected final static long DEFAULT_TEST_TIMEOUT = 600000;
	 private String log=null;
	 public Config scenarioContext;
	 private  UtilityObjectManager utilityObjectManager;
	 
	//builds a new report using the html template 
	 public static ExtentHtmlReporter htmlExtendReporter;  
	 public static ExtentReports extentReport;

	    @DataProvider(name = "ScenariosRunner")
	    public Object[][] dataProviderMethod(Method method) throws IOException {
	        Map<String, Integer> requiredHeaders = new HashMap<>();
	        List<List<String>> testData = new ArrayList<>();
	        Object[][] obj = new Object[][] {{}};
	        Config testConfig = new Config(method);
	        excel= new ExcelUtils(testConfig);
	        String excelPath = System.getProperty("user.dir") + File.separator + "src/test/resources/TestData/ScenarioDetails.xlsx";
	        FileInputStream file = null;
	        try {
	            file = new FileInputStream(new File(excelPath));
	            Workbook workbook = new XSSFWorkbook(file);
	            Sheet sheet = workbook.getSheet("ScenarioData");
	            int rowCount = excel.getRowCountInWorkSheet(excelPath, sheet.getSheetName());
	            for (Cell cell : sheet.getRow(0)) {
	                requiredHeaders.put(cell.getStringCellValue(), cell.getColumnIndex());
	            }
	            int count=0;
	            for (int i = 0; i < rowCount; i++) {
	                List<String> testRow = new ArrayList<>();
	                Row row = sheet.getRow(i + 1);
	                if (row.getCell(requiredHeaders.get("testEnabled")).toString().equalsIgnoreCase("Yes")) {
	                    testRow.add(row.getCell(requiredHeaders.get("testScenarioID")).toString());
	                    testRow.add(row.getCell(requiredHeaders.get("testScenarioName")).toString());
	                }
	                if (testRow.size() != 0) {
	                    testData.add(testRow);
	                    //obj[count++]=testRow.toArray();
	                    testRow = null;
	                }
	                
	            }
	            //return obj;
	            return testData.stream().map(List::toArray).toArray(Object[][]::new);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	         finally {
	        	if(file != null)
	        		file.close();
	        }
	        return null;
	    }
	    
	    @DataProvider(name = "testData", parallel = true)
	    public Object[][] scenariosDataProvider(Method method) throws IOException {
	        Map<String, Integer> requiredHeaders = new HashMap<>();
	        List<List<String>> testData = new ArrayList<>();
	        boolean flag=false;
	        Config testConfig = new Config(method);
	        excel= new ExcelUtils(testConfig);
	        String excelPath = System.getProperty("user.dir") + File.separator + testConfig.getRunTimeProperty("ScenarioSheet");
	        FileInputStream file = null;
	        try {
	            file = new FileInputStream(new File(excelPath));
	            Workbook workbook = new XSSFWorkbook(file);
	            Sheet sheet = workbook.getSheet("ScenarioData");
	            String featureName = testConfig.getRunTimeProperty("FeatureName");
	            List<String> suiteType = new ArrayList<>();
	            suiteType = Stream.of(testConfig.getRunTimeProperty("SuiteType").split("\\s*,\\s*"))
	            	     .map(String::trim)
	            	     .collect(Collectors.toList());
	            int rowCount = excel.getRowCountInWorkSheet(excelPath, sheet.getSheetName());
	            for (Cell cell : sheet.getRow(0)) {
	                requiredHeaders.put(cell.getStringCellValue(), cell.getColumnIndex());
	            }
	            for (int i = 0; i < rowCount; i++) {
	                List<String> testRow = new ArrayList<>();
	                Row row = sheet.getRow(i + 1);
	                if (row.getCell(requiredHeaders.get("FeatureName")).toString().equalsIgnoreCase(featureName)) {
	                	for (int k = 0 ; k < suiteType.size() ; k++) {
	                		if(row.getCell(requiredHeaders.get(suiteType.get(k)+"Suite")).toString().equalsIgnoreCase("Yes"))
	                			flag=true;
	                		else if(row.getCell(requiredHeaders.get(suiteType.get(k)+"Suite")).toString().equalsIgnoreCase("No")) {
	                			flag = false;
	                			break;
	                		}
	                	}
	                    if(flag==true && row.getCell(requiredHeaders.get("ParallelMode")).toString().equalsIgnoreCase(testConfig.getRunTimeProperty("ParallelMode")) && row.getCell(requiredHeaders.get("RunTest")).toString().equalsIgnoreCase("Yes")) {
	                    	testRow.add(row.getCell(requiredHeaders.get("ScenarioID")).toString());
	                    	testRow.add(row.getCell(requiredHeaders.get("ScenarioName")).toString());
	                  }
	                }
	                if (testRow.size() != 0) {
	                    testData.add(testRow);
	                    testRow = null;
	                }
	            }
	            return testData.stream().map(List::toArray).toArray(Object[][]::new);
	        } catch (FileNotFoundException e) {
	            loggerUtils.logException(e);
	        }
	         finally {
	        	if(file != null)
	        		file.close();
	        }
	        return null;
	    }
	    
		@DataProvider(name = "GetTestConfig")
		public Object[][] GetTestConfig(Method method)
		{
			scenarioContext = new Config(method);
			String testName = method.getDeclaringClass().getName() + "." + method.getName();
			String testStartTime = Helper.getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
			scenarioContext.setTestStartTime(testStartTime);
			scenarioContext.setScenarioName(testName);	
	        Config.threadLocalConfig.set(new Config[] {scenarioContext});
	        utilityObjectManager=new UtilityObjectManager(scenarioContext);
	        loggerUtils=new LoggerUtils();
	        scenarioContext.setUtilityObjectManager(utilityObjectManager);
			return new Object[][] { { scenarioContext } };
		}
		

		@AfterMethod(alwaysRun = true)
		public void tearDown(ITestResult result)
		{
			tearDownHelper(result);
		}

		protected void tearDownHelper(ITestResult result)
		{
			String testcaseName = result.getTestName();		
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date startDate = new Date();
			String logsData="";
			try {
		        if(result.getStatus() == ITestResult.FAILURE) {
		            Config.getConfig().getExtentTestLog().log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" FAILED ", ExtentColor.RED));
		            File screenshotFilepath=Config.getConfig().getUtilityObjectManager().getBrowserUtils().getScreenshotFile();
					Config.getConfig().getUtilityObjectManager().getBrowserUtils().captureScreenShoot(screenshotFilepath);
					Config.getConfig().getLoggerUtils().attachScreenShot(screenshotFilepath.getAbsolutePath(), screenshotFilepath.getName());
					//Config.getConfig().getExtentTestLog().log(Status.INFO,MarkupHelper.createLabel(" Capture Screen Shot" + Config.getConfig().getExtentTestLog().addScreenCaptureFromPath(screenshotPath),ExtentColor.WHITE));

		        }
		        else if(result.getStatus() == ITestResult.SUCCESS) {
		        	Config.getConfig().getExtentTestLog().log(Status.PASS, MarkupHelper.createLabel(" PASSED "+result.getName(), ExtentColor.GREEN));						
		        }
		        else {
		        	Config.getConfig().getExtentTestLog().log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" SKIPPED ", ExtentColor.ORANGE));
		        	Config.getConfig().getExtentTestLog().skip(result.getThrowable());
		        }
		    	}catch(Exception e) {
		    		e.printStackTrace();
		    	}
			logsData=Config.getConfig().getTestLog();
			Config.getConfig().getUtilityObjectManager().getBrowserUtils().quitBrowser();
			File screenshotFilepath=Config.getConfig().getUtilityObjectManager().getBrowserUtils().getTestLogsFile();
			Config.getConfig().getUtilityObjectManager().getTestDataHelper().getTextUtils().writeTextFile(screenshotFilepath.getAbsolutePath(), logsData, false);
			Config.getConfig().getLoggerUtils().attachTestLogs(screenshotFilepath.getAbsolutePath(), screenshotFilepath.getName());
			System.out.println("<B>Test '" + testcaseName + "' Ended on '" + dateFormat.format(startDate) + "'</B>");
		}
		
	    @BeforeTest
	    public void startReport() {
	        HashMap<String, HashMap<String, String>> jsonMapofMap;
			String fileName = "ScenarioTestData.json", filePath = "";
	    	// initialize the HtmlReporter
	    	try {
	        htmlExtendReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +"/ExtendReport/testReport.html");        
	        //initialize ExtentReports and attach the HtmlReporter
	        extentReport = new ExtentReports();
	        extentReport.attachReporter(htmlExtendReporter);
	        htmlExtendReporter.config().setChartVisibilityOnOpen(true);
	        htmlExtendReporter.config().setDocumentTitle("Extent Report Demo");
	        htmlExtendReporter.config().setReportName("Automation Report Details");
	        htmlExtendReporter.config().setTestViewChartLocation(ChartLocation.TOP);
	        htmlExtendReporter.config().setTheme(Theme.DARK);
	        htmlExtendReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
	        extentReport.setSystemInfo("Project Name", "TRM Automation Suit");
	        extentReport.setSystemInfo("Branch", "master");
	        extentReport.setSystemInfo("Environemnt", "Nucleous Dev");		
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		   
		   @AfterMethod
		    public void getResult(Method method, Object[] testData, ITestContext ctx,ITestResult result) {
		       ///
		    }
			
			@BeforeMethod
			public void BeforeMethod(Method method, Object[] testData, ITestContext ctx) {
				String startedOn;
			   if (testData!=null && testData.length > 0) {
				   String [] testInfo = (String[]) testData[0];
				   Config scenarioContext = new Config(method);
				   scenarioContext.setScenarioName(testInfo[1]);
				   ExtentTest extentTestLog=extentReport.createTest(testInfo[1], "Validate Login Flow");
				   //extentTestLog.assignCategory("Smoke");
				   scenarioContext.setExtentTestLog(extentTestLog);
				   testName.set(method.getName() + "_" + testInfo[1]);
				   Config.threadLocalConfig.set(new Config[]{scenarioContext});
				   LoggerUtils logger = new LoggerUtils();
				   String testcaseName = testInfo[1];	
				   DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				   Date startDate = new Date();
				   startedOn = dateFormat.format(startDate);
				   scenarioContext.setTestStartTime(startedOn);
				   logger.logComment("<B>Test '\" + testcaseName : " + testcaseName + " Started on " + startedOn + "'</B>");
				   ctx.setAttribute("testName", scenarioContext.getScenarioName());
			   } else
				   ctx.setAttribute("testName", method.getName());
			}
			
		
	     
		@AfterTest
	    public void tearDown() {
	    	//to write or update test information to reporter
	        extentReport.flush();
	    }
		
		@Override
		public String getTestName() {
			return testName.get();
		}

//	@BeforeTest
//	public void BeforeTest(Method method, Object[] testData, ITestContext ctx) {
//		HashMap<String, HashMap<String, String>> jsonMapofMap;
//		String fileName = "ScenarioTestData.json", filePath = "";
//		Config testConfig = new Config();
//		if (fileName != null && !fileName.trim().isEmpty()) {
//			filePath = System.getProperty("user.dir") + File.separator
//					+ scenarioContext.getRunTimeProperty("TestDataJSONPath");
//			try {
//				jsonMapofMap = testConfig.getUtilityObjectManager().getTestDataHelper().getJSONUtils()
//						.readTestData(filePath, fileName);
//				scenarioContext.getTestData().putAll(jsonMapofMap);
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
//	}
		       
		
}
