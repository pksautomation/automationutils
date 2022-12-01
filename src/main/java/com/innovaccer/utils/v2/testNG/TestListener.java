package com.innovaccer.utils.v2.testNG;

	import java.text.DateFormat;
	import java.text.SimpleDateFormat;
	import java.util.Date;
	import java.util.List;

	import org.testng.IInvokedMethod;
	import org.testng.IInvokedMethodListener;
	import org.testng.ITestContext;
	import org.testng.ITestListener;
	import org.testng.ITestResult;
	import org.testng.Reporter;
	import org.testng.internal.TestResult;

	import com.innovaccer.utils.v2.Config;
	import com.innovaccer.utils.v2.Helper;
	import com.innovaccer.utils.v2.LoggerUtils;

	public class TestListener implements ITestListener, IInvokedMethodListener
	{
		@Override
		public void onTestFailure(ITestResult result)
		{
			boolean executeOnce = true;
			Config[] testConfigs = Config.threadLocalConfig.get();
			if (testConfigs != null)
				for (Config testConf : testConfigs)
				{
					if (testConf != null)
					{
						LoggerUtils logger = new LoggerUtils(testConf);
						DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
						Date startDate = new Date();
						logger.logComment("***************EXECUTION OF TESTCASE ENDS HERE at : "+dateFormat.format(startDate)+" ***************");
						testConf.setTestResult(false);
						testConf.setLogToStandardOut(true);
						
						Reporter.setCurrentTestResult(result);
						// Log the unhandled exception thrown by the test cases. This
						// screenshot will appear in Log output, and not in inline
						// testcase logs
						if (result.getThrowable() != null && executeOnce)
						{
							executeOnce = false;
							
							String exceptionMessage = result.getThrowable().getMessage();
							if(exceptionMessage == null)
							{
								logger.logComment("Unable to get the Failure Reason of testcase:"+testConf.getScenarioName());
							}
							
							
							if (exceptionMessage == null || !exceptionMessage.equalsIgnoreCase("Ending execution in the middle!"))
								{
								logger.logFailureException(result.getThrowable());
								}
						}
						// else nothing to log, as test case has caught the exception if any
						testConf.endTest(result);
						testConf.attachLogs(testConf.getScenarioName());
					}
					else
					{
						System.out.println("testConfig object not found in onTestFailure");
					}
				}
		}
		
		
		@Override
		public void afterInvocation(IInvokedMethod method, ITestResult testResult)
		{
			//Run this after running a test case which failed with soft asserts (Log.Fail) i.e. status as success, 
			//to do assertAll, and mark the test case as fail
			if (method.isTestMethod() && testResult.getStatus() == TestResult.SUCCESS)
			{
				String errorMessage = "";
				Config[] testConfigs = Config.threadLocalConfig.get();
				if (testConfigs != null)
				{
					for (Config testConf : testConfigs)
					{
						if (testConf != null)
						{
							try
							{
								testConf.getSoftAssert().assertAll();
							}
							catch (AssertionError e)
							{
								errorMessage = errorMessage + e.getMessage();						
							}
						}
					}
				}
				if (errorMessage != "")
				{
					testResult.setStatus(TestResult.FAILURE);
					testResult.setThrowable(new AssertionError(errorMessage));
					System.out.println("<------ Exiting afterInvocation with errorMessage = " + errorMessage + "------>");
				}
			}
		}
		
		@Override
		public void onTestSkipped(ITestResult result)
		{
			Config[] testConfigs = Config.threadLocalConfig.get();
			if (testConfigs != null)
				for (Config testConf : testConfigs)
				{
					if (testConf != null)
					{
						String message = "Test case skipped " + testConf.getScenarioName();
						
						System.out.println(message);
						message = "<font color='Orange'>" + message + "</font></br>";
						Reporter.log(message);
					}
					else
					{
						System.out.println("testConfig object not found in onTestSkipped");
					}
				}
		}
		
		@Override
		public void onTestSuccess(ITestResult result)
		{
			Config[] testConfigs = Config.threadLocalConfig.get();
			if (testConfigs != null)
				for (Config testConf : testConfigs)
				{
					if (testConf != null)
					{
						testConf.endTest(result);
						testConf.attachLogs(testConf.getScenarioName());
					}
					else
					{
						System.out.println("testConfig object not found in onTestSuccess");
					}
				}
		}
		
		@Override
		public void beforeInvocation(IInvokedMethod method, ITestResult testResult)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTestStart(ITestResult result)
		{
			Config[] testConfigs = Config.threadLocalConfig.get();
			if (testConfigs != null)
				for (Config testConf : testConfigs)
				{
					if (testConf != null)
					{
						LoggerUtils logger = new LoggerUtils(testConf);
						Reporter.setCurrentTestResult(result);
						List<String> reporterOutput = Reporter.getOutput(result);
						if(!Helper.listContainsString(reporterOutput, "<B>Test '" + testConf.getScenarioName() + "' Started on '"))
							System.out.println("<B>Test '" + testConf.getScenarioName() + "' Started on '" + testConf.getTestStartTime() + "'</B>");
					}
					else
					{
						System.out.println("testConfig object not found in onTestStart");
					}
				}
		}
			
		@Override
		public void onFinish(ITestContext context)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onStart(ITestContext context)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result)
		{
			// TODO Auto-generated method stub
		}
		
}
