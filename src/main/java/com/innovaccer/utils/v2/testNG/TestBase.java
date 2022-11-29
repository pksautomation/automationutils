package com.innovaccer.utils.v2.testNG;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.innovaccer.utils.v2.fileutils.ExcelUtils;
import org.testng.annotations.BeforeMethod;


@Listeners(com.innovaccer.utils.v2.testNG.TestListener.class)
public class TestBase{
	 protected static ThreadLocal<Config[]> threadLocalConfig = new ThreadLocal<Config[]>();
	 private LoggerUtils loggerUtils;
	 private String testName;
	 private ExcelUtils excel;
	 protected final static long DEFAULT_TEST_TIMEOUT = 600000;
	 private String log=null;
	 public Config scenarioContext;
	 private  UtilityObjectManager utilityObjectManager;
	//builds a new report using the html template 
	 ExtentHtmlReporter htmlReporter;
	    
	 ExtentReports extent;
	    //helps to generate the logs in test report.
	 ExtentTest test;

	    @DataProvider(name = "ScenariosRunner", parallel = true)
	    public Object[][] dataProviderMethod(Method method) throws IOException {
	        Map<String, Integer> requiredHeaders = new HashMap<>();
	        List<List<String>> testData = new ArrayList<>();
	        Config testConfig = (Config) GetTestConfig(method)[0][0];
	        excel= new ExcelUtils();
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
	            for (int i = 0; i < rowCount; i++) {
	                List<String> testRow = new ArrayList<>();
	                Row row = sheet.getRow(i + 1);
	                if (row.getCell(requiredHeaders.get("testEnabled")).toString().equalsIgnoreCase("Yes")) {
	                    testRow.add(row.getCell(requiredHeaders.get("testScenarioID")).toString());
	                    testRow.add(row.getCell(requiredHeaders.get("testScenarioName")).toString());
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
			threadLocalConfig.set(new Config[]{scenarioContext});
	        Config.threadLocalConfig.set(new Config[]{scenarioContext});
	        utilityObjectManager=new UtilityObjectManager(scenarioContext);
	        loggerUtils=new LoggerUtils(scenarioContext);
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
			String testcaseName = "NullConfig";			
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date startDate = new Date();
			scenarioContext.quitBrowser();
			scenarioContext.closeSQLDBConnection(scenarioContext.getDBConnection());
			//close driver
			scenarioContext.setTestEndTime(dateFormat.format(startDate));
			loggerUtils.logComment("<B>Test '\" + testcaseName + \"' Ended on '\" + dateFormat.format(startDate) + \"'</B>");
			System.out.println("<B>Test '" + testcaseName + "' Ended on '" + dateFormat.format(startDate) + "'</B>");
		}
		
//		@BeforeClass(alwaysRun = false)
//		@Parameters({ "browser", "environment", "testngOutputDir", "RemoteAddress", "BrowserVersion","ProjectName"})
//		public void InitializeParameters(@Optional String browser, @Optional String environment, @Optional String testngOutputDir, @Optional String RemoteAddress, @Optional String BrowserVersion, @Optional String ProjectName)
//		{
//			//initialize variable here before running scenarios
//		}
		
		//@Parameters({ "OS", "browser" })
	    @BeforeTest
	    public void startReport() {
	    	// initialize the HtmlReporter
	        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +"/test-output/testReport.html");
	        
	        //initialize ExtentReports and attach the HtmlReporter
	        extent = new ExtentReports();
	        extent.attachReporter(htmlReporter);
	         
	        //To add system or environment info by using the setSystemInfo method.
//	        extent.setSystemInfo("OS", OS);
//	        extent.setSystemInfo("Browser", browser);
	        
	        //configuration items to change the look and feel
	        //add content, manage tests etc
	        htmlReporter.config().setChartVisibilityOnOpen(true);
	        htmlReporter.config().setDocumentTitle("Extent Report Demo");
	        htmlReporter.config().setReportName("Test Report");
	        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
	        htmlReporter.config().setTheme(Theme.STANDARD);
	        htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
	    }
		
	    @AfterMethod
	    public void getResult(ITestResult result) {
	        if(result.getStatus() == ITestResult.FAILURE) {
	            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" FAILED ", ExtentColor.RED));
	            test.fail(result.getThrowable());
	        }
	        else if(result.getStatus() == ITestResult.SUCCESS) {
	            test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" PASSED ", ExtentColor.GREEN));
	        }
	        else {
	            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" SKIPPED ", ExtentColor.ORANGE));
	            test.skip(result.getThrowable());
	        }
	    }
	     
		@AfterTest
	    public void tearDown() {
	    	//to write or update test information to reporter
	        extent.flush();
	    }
		
		@BeforeMethod
		public void BeforeMethod(Method method, Object[] testData, ITestContext ctx) {
		   if (testData.length > 0) {
		     // this.setTestName(method.getName() + "_" + testData[0].toString());
		      ctx.setAttribute("testName", scenarioContext.getScenarioName());
		   } else
		      ctx.setAttribute("testName", method.getName());
		}
}
