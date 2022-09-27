package com.innovaccer.utils.v2;

import com.epam.healenium.SelfHealingDriver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.Augmentable;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.innovaccer.utils.Popup;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BrowserUtils {

	private Config configInstance;
	private LoggerUtils loggerUtils;
	private WebDriver driver;
	private AssertionUtils assertionUtils;

	public BrowserUtils(Config testConfig) {
		init(testConfig);
	}

	private void init(Config testConfig) {
		this.configInstance=testConfig;
		loggerUtils = new LoggerUtils(configInstance);
		assertionUtils = new AssertionUtils(configInstance);
		driver=configInstance.driver;
	}

	public BrowserUtils() {
		init(Config.getConfig());
	}

	public void navigateToLoginPage() {
		String url = configInstance.getRunTimeProperty("Environment");
		navigateToURL(url);
	}

	public void navigateToURL(String url)
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date startDate = new Date();
		startDate = new Date();
		loggerUtils.logComment("Starting Navigation to web page- '" + url + "' at:- "+dateFormat.format(startDate) + " for : " +configInstance.scenarioName);
		try
		{
			configInstance.driver.navigate().to(url);
		}
		catch(UnhandledAlertException ua)
		{
			loggerUtils.logWarning("Alert appeared during navigation");
			if(Popup.isAlertPresent(configInstance))
				Popup.ok(configInstance);
		}
		catch(Exception e) {
			loggerUtils.logException(e);
		}

		loggerUtils.logComment("Navigated to web page- '" + url + "' for : " +configInstance.scenarioName);
	}

	public void openBrowser()
	{
		int retryCnt = 5;
		while (configInstance.getDriver() == null && retryCnt > 0)
		{
			try
			{
				configInstance.driver = openBrowser(configInstance);

			}
			catch (Exception e)
			{
				loggerUtils.logWarning("Retrying the browser launch:-" + e.toString());
			}
			if (configInstance.getDriver() == null)
			{
				retryCnt--;
				if (retryCnt == 0)
				{
					loggerUtils.logFail("Browser could not be opened for : "+configInstance.getScenarioName());
					assertionUtils.assertTrue("Opening Browser", false);
				}
			}

		}
	}


	/* Close the browser
	 * @author pramod.singh
	 */


	public void setImplicitWait(int timeInSeconds) {
			driver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
	}

	public void takeScreenshot() {
		try
		{
			if (driver != null)
			{
				byte[] screenshot = null;

				try
				{
					screenshot = captureScreenshot();
				}
				catch (NullPointerException ne)
				{
					loggerUtils.logWarning("NullPointerException:Screenshot can't be taken. Probably browser is not reachable");
					driver = null;
				}

				if (screenshot != null)
				{
					configInstance.testScenario.embed(screenshot, "image/png");
					loggerUtils.logComment("Refer Screenshot In Attachement");
				}
			}
		}
		catch (Exception e)
		{
			configInstance.enableScreenshot = false;
			loggerUtils.logWarning("Unable to take screenshot2:- " + ExceptionUtils.getStackTrace(e));
			loggerUtils.logFailureException(e);
		}
	}

	private byte[] captureScreenshot()
	{
		byte[] screenshot = null;

		try
		{
			if(Popup.isAlertPresent(configInstance, false))
			{
				Popup.ok(configInstance);
			}

			if (driver.getClass().isAnnotationPresent(Augmentable.class) || driver.getClass().getName().startsWith("org.openqa.selenium.remote.RemoteWebDriver$$EnhancerByCGLIB"))
			{
				WebDriver augumentedDriver = new Augmenter().augment(driver);
				screenshot = ((TakesScreenshot) augumentedDriver).getScreenshotAs(OutputType.BYTES);
			}
			else
			{
				screenshot = ((TakesScreenshot)((SelfHealingDriver) driver).getDelegate()).getScreenshotAs(OutputType.BYTES);
			}

		}
		catch (UnhandledAlertException alert)
		{
			Popup.ok(configInstance);
			loggerUtils.logWarning(ExceptionUtils.getFullStackTrace(alert));
		}
		catch (NoSuchWindowException NoSuchWindowExp)
		{
			loggerUtils.logWarning("NoSuchWindowException:Screenshot can't be taken. Probably browser is not reachable");
			//test case will end, setting this as null will prevent taking screenshot again in cleanup
			driver = null;
		}
		catch (WebDriverException webdriverExp)
		{
			loggerUtils.logWarning("Unable to take screenshot1:- " + ExceptionUtils.getFullStackTrace(webdriverExp));
		}
		catch(Exception e)
		{
			loggerUtils.logComment("****************************Exception in Browser.java***********************");
			e.printStackTrace();
		}
		return screenshot;
	}


	public void deleteCookies() {
		if (driver != null)
		{
			loggerUtils.logComment("Delete all cookies!!");
			driver.manage().deleteAllCookies();
		}
	}

	/**
	 * @author nikitagatagat
	 * @param title
	 */
	public void waitForPageTitleToContain(String title) {
		loggerUtils.logComment("Wait for page title to contain '" + title + "'.");
		Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));
		WebDriverWait wait = new WebDriverWait(driver, ObjectWaitTime);
		wait.until(ExpectedConditions.titleContains(title));
		//Browser.waitForPageTitleToContain(configInstance, title);
	}

	public void downloadDesiredFile(String filePath, String fileName) {
		DesiredFileDownload(filePath, fileName);
	}

	public File DesiredFileDownload(String path, String name)
	{
		File fl = new File(path);
		File choise = null;
		for (int retry = 0; retry <= 15; retry++)
		{
			// storing all file names in files array
			File[] files = fl.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return file.isFile();
				}
			});

			// Finding the desired file
			for (File file : files)
			{
				// System.out.println(file);
				if (file.getName().contains(name) && (!file.getName().contains("part")))
				{
					choise = file;
					break;
				}
			}
			// checking if file with required name has been found out
			if (choise == null && retry == 15)
			{
				loggerUtils.logFail("File with name " + name + " does not exist in " + path);
				break;
			}
			else
				if (choise == null)
					try {
						wait(1);
						} catch (InterruptedException e) {
							loggerUtils.logWarning(ExceptionUtils.getFullStackTrace(e));
					}
				else
					break;
		}
		return choise;
	}


	public Object executeJavascript(String javascriptCode) {
		loggerUtils.logComment("Execute javascript:-" + javascriptCode);
		JavascriptExecutor javaScript = (JavascriptExecutor)driver;
		return javaScript.executeScript(javascriptCode);
	}

	/**
	 * @author nikitagatagat
	 * @exception UnreachableBrowserException
	 */
	public void quitBrowser() {
		try
		{
			if (driver != null)
			{
				loggerUtils.logComment("Close the browser window with URL:- " + driver.getCurrentUrl() + ". And title as :- " + driver.getTitle());
				driver.close();
			}
		}
		catch (UnreachableBrowserException e)
		{
			loggerUtils.logWarning(ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * Function for going back to the previous page
	 */
	public void navigateBack() {
		goBack();
	}

	public void goBack()
	{
		loggerUtils.logComment("Clicking on back button on browser");
		driver.navigate().back();
	}


	public String getCookieValue(String cookieKey) {
		String value = null;
		if (driver != null)
		{
			Cookie cookie = driver.manage().getCookieNamed(cookieKey);
			if (cookie == null)
			{
				loggerUtils.logFail("Cookie " + cookieKey + " Not found");
				return null;
			}
			value = cookie.getValue();
			loggerUtils.logComment("Read the cookie named '" + cookieKey + "' value as '" + value + "'");
		}
		return value;
	}

	public File getPageHtmlFile() {
		File dest = getScreenShotDirectory();
		return new File(dest.getPath() + File.separator + getPageHTMLFileName());
	}

	private File getScreenShotDirectory()
	{
		File dest = new File(configInstance.getRunTimeProperty("ResultsDir") + File.separator );
		return dest;
	}

	private  String getPageHTMLFileName()
	{
		String nameScreenshot = configInstance.featureName + "." + configInstance.scenarioName;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date) + "_" + nameScreenshot + ".html";
	}

	public File getScreenshotFile() {
		File dest = getScreenShotDirectory();
		return new File(dest.getPath() + File.separator + getScreenshotFileName());
	}

	public File getLastModifiedFileInDirectory(String directoryPath) {
		return lastFileModified(directoryPath);
	}

	/**
	 * Get last update file name from specific directory
	 * @param dir
	 * @author pramod.singh
	 * @return
	 */
	public File lastFileModified(String dir)
	{
		File fl = new File(dir);
		File[] files = fl.listFiles();
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		return files[0];
	}

	public File getLastModifiedFileInDirectoryForAName(String directoryPath, String name)  {
		return lastFileModifiedWithDesiredName(directoryPath, name);
	}

	public  File lastFileModifiedWithDesiredName(String path, String name)
	{
		File fl = new File(path);
		File choise = null;
		List<File> arrayOfSortedFiles = new ArrayList<File>();
		long lastMod = Long.MIN_VALUE;
		for (int retry = 0; retry <= 5; retry++)
		{
			// making a list of files in download folder
			System.out.println("Wait for file to download");
			File[] files = fl.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return file.isFile();
				}
			});
			// Matching names of desired file
			for (File file : files)
			{
				if (file.getName().contains(name))
					arrayOfSortedFiles.add(file);
			}
			if (arrayOfSortedFiles.size() > 0)
				break;
			else
				continue;
		}
		// Finding matching file which has been last modified
		for (File matchingfile : arrayOfSortedFiles)
		{
			if (matchingfile.lastModified() > lastMod)
			{
				choise = matchingfile;
				lastMod = matchingfile.lastModified();
			}
		}
		if (choise == null)
			loggerUtils.logFail("No File found with name" + name);
		else
			System.out.println("The file chosen is as: " + choise.getName());
		return choise;
	}

	private String getScreenshotFileName()
	{
		String nameScreenshot =((List<String>) configInstance.testScenario.getSourceTagNames()).get(0);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date) + "_" + nameScreenshot + ".png";
	}

	/*
	 * public void navigateToURL(String url)
	 *  { Browser.navigateToURL(configInstance,url); }
	 */

	public void recordHtmlPage(File destination) {
		try
		{
			if (driver != null)
			{
				String pageHTML;
				Boolean remoteExecution = (configInstance.getRunTimeProperty("RemoteExecution").toLowerCase().equals("true")) ? true : false;

				if (remoteExecution)
				{
					WebDriver augumentedDriver = new Augmenter().augment(driver);
					pageHTML = (augumentedDriver).getPageSource();
				}
				else
				{
					pageHTML = driver.getPageSource();
				}

				try
				{
					FileUtils.writeStringToFile(destination, pageHTML);
				}
				catch (IOException e)
				{
					// We are not using testConfig.logException(e) to print
					// exception here
					// testConfig.logException(e) creates a infinite loop and
					// breaks all test cases
					e.printStackTrace();
				}

				loggerUtils.logComment("<B>Page HTML</B>:- <a href=" + destination.getName() + " target='_blank' >" + destination.getName() + "</a>");
			}
		}
		catch (Exception e)
		{
			loggerUtils.logWarning("Unable to record Page HTML:- " + ExceptionUtils.getFullStackTrace(e));
			configInstance.recordPageHTMLOnFailure = false;
			throw e;
		}
	}
	/**
	 * @author nikitagatagat
	 * @param windowHandleName
	 */
	public void switchToGivenWindow(String windowHandleName) {
		if (configInstance.driver != null)
		{
			loggerUtils.logComment("Switching to the given window handle:- " + windowHandleName);
			configInstance.driver.switchTo().window(windowHandleName);
			loggerUtils.logComment("Switched to window with URL:- " + driver.getCurrentUrl() + ". And title as :- " + driver.getTitle());
		}
	}



	public void uploadFileUsingJavascript(String jsLocator, String filePath, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(jsLocator + ".style.display = \"block\";");
		js.executeScript(jsLocator + ".style.visibility = 'visible';");
		js.executeScript(jsLocator + ".style.opacity = 1;");
		element.sendKeys(filePath);
	}

	/*public boolean verifyUrl(String expectedURL) {
		try
		{
			int retries = 30;
			String actualURL = driver.getCurrentUrl().toLowerCase();
			expectedURL = expectedURL.toLowerCase();

			while (retries > 0)
			{
				if (actualURL.contains(expectedURL))
				{
					LoggerUtils.logPass("Browser URL", actualURL);
			
					actualURL = driver.getCurrentUrl().toLowerCase();
					if (!actualURL.contains(expectedURL))
					{
						LoggerUtils.logFail("Browser URL", expectedURL, actualURL);
						return false;
					}

					return true;
				}
				actualURL = driver.getCurrentUrl().toLowerCase();
				retries--;
			}
			LoggerUtils.logFail("Browser URL", expectedURL, actualURL);
			return false;
		}
		catch (UnreachableBrowserException e)
		{
			LoggerUtils.logException(e);
			return false;
		}
	}*/



	public SelfHealingDriver launchWindowsApp() {
		WebDriver delegate = null;
		SelfHealingDriver driver=null;
		String winApp = configInstance.getRunTimeProperty("projectName");
		try {
			if (System.getProperty("os.name").toLowerCase().contains("window")) {
				quitWindowsApp();
				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + File.separator+"src/test/resources/drivers/windowdriver/chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--disable-popup-blocking");
				Map<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("profile.default_content_settings.popups", 0);
				options.setBinary(configInstance.getRunTimeProperty("AppPath").toString());
				delegate=new ChromeDriver(options);
				driver=  SelfHealingDriver.create(delegate);
				Set<String> handles = driver.getWindowHandles();
				Iterator<String> handlesIterator = handles.iterator();
				String mainWindow = handlesIterator.next();
				driver.switchTo().window(mainWindow);
			}

		loggerUtils.logComment("Windows application '" + winApp + "' launched successfully for testcase:- "+configInstance.scenarioName);
		}
		catch(Exception e) {
			loggerUtils.logComment(e.toString());
		}
		return driver;
	}

	/**
	 * @author nikitagatagat
	 */
	public void quitWindowsApp() {

		Process process;
		try
		{
			if (driver != null) {
				loggerUtils.logComment("Quit the application");
				driver.quit();
			}
			process = Runtime.getRuntime().exec("taskkill /F /IM InNote.exe");
			process.waitFor();
			process.destroy();
		}
		catch (UnreachableBrowserException | IOException | InterruptedException e)
		{
			loggerUtils.logWarning(ExceptionUtils.getFullStackTrace(e));
		}
		//Browser.quitWindowsApp(configInstance);
	}

	public WebDriver getDelegateDriver(SelfHealingDriver driver) {
		return driver.getDelegate();
	}

	public String getConsoleDataOfChromeBrowser() {
			 String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
			 String netData = ((JavascriptExecutor)configInstance.driver).executeScript(scriptToExecute).toString();
			 return netData;
	}

	/**
	 * @return new browser instance
	 * @throws IOException
	 * @author pramod.singh
	 */
	public SelfHealingDriver openBrowser(Config configInstance)
	{
		SelfHealingDriver driver=null;
		WebDriver deligate_driver = null;
		String browser = configInstance.getRunTimeProperty("Browser");
		String browserVersion = configInstance.getRunTimeProperty("BrowserVersion");

		if(configInstance.BrowserName !=null && !configInstance.BrowserName.isEmpty()) {
			browser  = configInstance.BrowserName;
			configInstance.putRunTimeProperty("Browser", browser);
		}

		try {
			if (!configInstance.remoteExecution) {
				loggerUtils.logComment("Launching '" + browser + "' browser in local machine");
				System.out.println("Launching '" + browser + "' browser in local machine");
				switch (browser.toLowerCase()) {
				case "firefox":
					System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
					if(browserVersion != null)
						WebDriverManager.firefoxdriver().driverVersion(browserVersion).setup();
					else
						WebDriverManager.firefoxdriver().setup();
					deligate_driver = new FirefoxDriver();
					break;

				case "htmlunit":// for headless browser execution , used for running api test cases
					deligate_driver = new HtmlUnitDriver(true);
					break;

				case "chrome":
					if(browserVersion != null)
						WebDriverManager.chromedriver().driverVersion(browserVersion).setup();
					else
						WebDriverManager.chromedriver().setup();
					ChromeOptions options = new ChromeOptions();
					//To run same browser - will update
					Map<String, Object> prefs = new HashMap<String, Object>();
					if (configInstance.scenario != null)
						prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator);
					else
						prefs.put("download.default_directory", configInstance.downloadPath);

					options.setExperimentalOption("prefs", prefs);
					options.addArguments("disable-infobars");
					options.addArguments("--start-maximized");
					options.addArguments("--disable-extensions");
					options.addArguments("--disable-popup-blocking");
					if (configInstance.isBrowserInHeadlessMode) {
						System.out.println("In Headless");
						options.addArguments("--headless");
						options.addArguments("window-size=1366x768");
						options.addArguments("--disable-extensions");
						options.addArguments("enable-automation");
						options.addArguments("--window-size=1364,786");
						options.addArguments("--no-sandbox");
						options.addArguments("--dns-prefetch-disable");
						options.addArguments("--disable-gpu");
						options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
						options.addArguments("--disable-infobars");
					    options.addArguments("--disable-dev-shm-usage");
					    options.addArguments("--disable-browser-side-navigation");
					    options.addArguments("--disable-gpu");

					}
					//options.setHeadless(false);
					deligate_driver = new ChromeDriver(options);
					//driver = new ChromeDriver(options);
					break;
				case "ie":
					if(browserVersion != null)
						WebDriverManager.iedriver().driverVersion(browserVersion).setup();
					else
						WebDriverManager.iedriver().setup();
					InternetExplorerOptions ieoptions = new InternetExplorerOptions();
					deligate_driver = new InternetExplorerDriver(ieoptions);
					deligate_driver.manage().window().maximize();
					TimeUnit tu = TimeUnit.SECONDS;
					deligate_driver.manage().timeouts().implicitlyWait(5, tu);
					break;

				case "opera":
//					DesiredCapabilities operaCapability = DesiredCapabilities.opera();
//					operaCapability.setCapability("opera.port", -1);
					//deligate_driver = new OperaDriver(operaCapability);
					break;

				default:
					Assert.fail(browser + "- is not supported");
				}
			}
			else {
				loggerUtils.logComment("Launching '" + browser + "' browser on remote machine " + configInstance.getRunTimeProperty("RemoteBrowserLocation"));
				System.out.println("Launching '" + browser + "' browser on remote machine " + configInstance.getRunTimeProperty("RemoteBrowserLocation"));
				switch (browser.toLowerCase()) {
				case "firefox":
					//Write a code for it
					break;

				case "htmlunit":// for headless browser execution , used for running api test cases
					//Write a code for it
					break;

				case "chrome":
					HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
					if (configInstance.scenario != null)
						chromePrefs.put("download.default_directory", System.getProperty("user.dir") + File.separator);
					else
						chromePrefs.put("download.default_directory", configInstance.downloadPath);
					if(browserVersion != null)
						WebDriverManager.chromedriver().driverVersion(browserVersion).setup();
					else
						WebDriverManager.chromedriver().setup();
			        ChromeOptions options = new ChromeOptions();
			        options.setExperimentalOption("prefs", chromePrefs);
			        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
			        options.addArguments("disable-infobars");
					options.addArguments("--start-maximized");
					options.addArguments("--disable-extensions");
			        DesiredCapabilities cap = DesiredCapabilities.chrome();
			        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			        cap.setCapability(ChromeOptions.CAPABILITY, options);
			        cap.setBrowserName("chrome");
			        if(configInstance.isBrowserInHeadlessMode) {
			        	options.addArguments("--headless");
			        	options.addArguments("window-size=1364x768");
			        }
			        //System.out.println(" Remote browser location " + testConfig.getRunTimeProperty("RemoteBrowserLocation"));
			        if(configInstance.getRunTimeProperty("RemoteBrowserLocation") == null)
			        	deligate_driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			        else {
			        	deligate_driver = new RemoteWebDriver(new URL(configInstance.getRunTimeProperty("RemoteBrowserLocation")), cap);
			        }
					break;
				case "ie":
					//Write a code for it
					break;

				case "opera":
					//Write a code for it

					break;

				default:
					Assert.fail(browser + "- is not supported");
				}
			}

		if (deligate_driver != null)
		{
			//Close the browser incase time taken to load a page exceed 2 min
			Long ObjectWaitTime = Long.parseLong(configInstance.getRunTimeProperty("ObjectWaitTime"));
			driver = SelfHealingDriver.create(deligate_driver);
			driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(ObjectWaitTime*2, TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(ObjectWaitTime*2, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
		//System.out.println("Browser '" + browser + "' launched successfully for testcase:- "+testConfig.scenarioName);
		loggerUtils.logComment("Browser '" + browser + "' launched successfully for testcase:- "+configInstance.scenarioName);
		}
		catch(Exception e) {
			loggerUtils.logComment(e.toString());
			loggerUtils.logFailureException(e);
		}
		return driver;
	}

}
	
	

