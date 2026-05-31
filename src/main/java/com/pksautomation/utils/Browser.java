package com.pksautomation.utils;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.JsonParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Augmentable;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.epam.healenium.SelfHealingDriver;
import com.google.common.base.Function;
import com.opera.core.systems.OperaDriver;

import cucumber.api.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;

import com.pksautomation.utils.Element.How;
/**
 * 
 * @author pksautomation
 *
 */
public class Browser
{

	// This class overrides the setCompressionQuality() method to workaround
	// a problem in compressing JPEG images using the javax.imageio package.
	public static class MyImageWriteParam extends JPEGImageWriteParam
	{
		public MyImageWriteParam()
		{
			super(Locale.getDefault());
		}

		// This method accepts quality levels between 0 (lowest) and 1 (highest)
		// and simply converts
		// it to a range between 0 and 256; this is not a correct conversion
		// algorithm.
		// However, a proper alternative is a lot more complicated.
		// This should do until the bug is fixed.
		@Override
		public void setCompressionQuality(float quality)
		{
			if (quality < 0.0F || quality > 1.0F)
			{
				throw new IllegalArgumentException("Quality out-of-bounds!");
			}
			this.compressionQuality = 256 - (quality * 256);
		}
	}

	/**
	 * Refresh browser once
	 * @author pksautomation
	 */
	public static void browserRefresh(Config testConfig)
	{
		//testConfig.driver.navigate().refresh();
		executeJavaScript(testConfig, "location.reload();");
		testConfig.logComment("Refreshing the browser...");
	}


	//@Attachment(value = "Screenshot", type = "image/png")
	private static byte[] captureScreenshot(Config testConfig)
	{
		byte[] screenshot = null;

		try
		{
			if(Popup.isAlertPresent(testConfig, false))
			{
				Popup.ok(testConfig);
			}

			if (testConfig.driver.getClass().isAnnotationPresent(Augmentable.class) || testConfig.driver.getClass().getName().startsWith("org.openqa.selenium.remote.RemoteWebDriver$$EnhancerByCGLIB")) 
			{
				WebDriver augumentedDriver = new Augmenter().augment(testConfig.driver);
				screenshot = ((TakesScreenshot) augumentedDriver).getScreenshotAs(OutputType.BYTES);					
			}
			else
			{
				screenshot = ((TakesScreenshot) testConfig.driver.getDelegate()).getScreenshotAs(OutputType.BYTES);
			}

		}
		catch (UnhandledAlertException alert)
		{
			Popup.ok(testConfig);
			testConfig.logWarning(ExceptionUtils.getFullStackTrace(alert));
		}
		catch (NoSuchWindowException NoSuchWindowExp)
		{
			testConfig.logWarning("NoSuchWindowException:Screenshot can't be taken. Probably browser is not reachable");
			//test case will end, setting this as null will prevent taking screenshot again in cleanup
			testConfig.driver = null;
		}
		catch (WebDriverException webdriverExp)
		{
			testConfig.logWarning("Unable to take screenshot1:- " + ExceptionUtils.getFullStackTrace(webdriverExp));
		}
		catch(Exception e)
		{
			testConfig.logComment("****************************Exception in Browser.java***********************");
			e.printStackTrace();
		}
		return screenshot;
	}

	/**
	 * Close the current window, quitting the browser if it's the last window
	 * currently open.
	 * 
	 * @param Config
	 *            test config instance for the browser to be closed
	 * @author pksautomation
	 */
	public static void closeBrowser(Config testConfig)
	{
		try
		{
			if (testConfig.driver != null)
			{
				testConfig.logComment("Close the browser window with URL:- " + testConfig.driver.getCurrentUrl() + ". And title as :- " + testConfig.driver.getTitle());
				testConfig.driver.close();
			}
		}
		catch (UnreachableBrowserException e)
		{
			testConfig.logWarning(ExceptionUtils.getFullStackTrace(e));
		}
	}

	// Reads the jpeg image in infile, compresses the image,
	// and writes it back out to outfile.
	// compressionQuality ranges between 0 and 1,
	// 0-lowest, 1-highest.
	private static void compressJpegFile(File infile, File outfile, float compressionQuality)
	{
		try
		{
			// Retrieve jpg image to be compressed
			RenderedImage rendImage = ImageIO.read(infile);

			// Find a jpeg writer
			ImageWriter writer = null;
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("png");
			if (iter.hasNext())
			{
				writer = iter.next();
			}

			// Prepare output file
			ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
			writer.setOutput(ios);

			// Set the compression quality
			ImageWriteParam iwparam = new MyImageWriteParam();
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(compressionQuality);

			// Write the image
			writer.write(new IIOImage(rendImage, null, null));

			// Cleanup
			ios.flush();
			writer.dispose();
			ios.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (OutOfMemoryError outOfMemoryError)
		{
			outOfMemoryError.printStackTrace();
		}
	}

	/**
	 * Delete the cookies of the given browser instance
	 * 
	 * @param Config
	 *            test config instance
	 *  @author pksautomation
	 */
	public static void deleteCookies(Config testConfig)
	{
		if (testConfig.driver != null)
		{
			testConfig.logComment("Delete all cookies!!");
			testConfig.driver.manage().deleteAllCookies();
		}
	}
	/**
	 * Wait for given browser's  title to display
	 * @param testConfig
	 * @param title
	 * @author pksautomation
	 */
	public static void waitForPageTitleToContain(Config testConfig, String title)
	{
		testConfig.logComment("Wait for page title to contain '" + title + "'.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
		wait.until(ExpectedConditions.titleContains(title));
	}

	/**
	 * @param testConfig
	 *            - element of Config
	 * @param path
	 *            - path of the folder where file is present
	 * @param name
	 *            - some text that is present in file name
	 * @author pksautomation
	 * @return - file name with matching text
	 */
	public static File DesiredFileDownload(Config testConfig, String path, String name)
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
				Log.Fail("File with name " + name + " does not exist in " + path, testConfig);
				break;
			}
			else
				if (choise == null)
					Browser.wait(testConfig, 1);
				else
					break;
		}
		return choise;
	}

	/**
	 * Executes JavaScript in the context of the currently selected frame or
	 * window in the Config driver instance.
	 * 
	 * @param javaScriptToExecute
	 *            Java Script To Execute
	 * @return If the script has a return value (i.e. if the script contains a
	 *         return statement), then the following steps will be taken: For an
	 *         HTML element, this method returns a WebElement For a decimal, a
	 *         Double is returned For a non-decimal number, a Long is returned
	 *         For a boolean, a Boolean is returned For all other cases, a
	 *         String is returned. For an array, return a List<Object> with each
	 *         object following the rules above. We support nested lists. Unless
	 *         the value is null or there is no return value, in which null is
	 *         returned
	 */
	public static Object executeJavaScript(Config testConfig, String javaScriptToExecute)
	{
		testConfig.logComment("Execute javascript:-" + javaScriptToExecute);
		JavascriptExecutor javaScript = (JavascriptExecutor) testConfig.driver;
		return javaScript.executeScript(javaScriptToExecute);
	}
	/**
	 * Get name of Caller Class Name of current function
	 * @author pksautomation
	 * @return
	 */
	private static String getCallerClassName()
	{
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++)
		{
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(Browser.class.getName()) && !ste.getClassName().contains("Helper") && ste.getClassName().indexOf("java.lang.Thread") != 0)
			{
				return ste.getClassName();
			}
		}
		return null;
	}
	/**
	 * get cookies value for current browser
	 * @param testConfig
	 * @param cookieName
	 * @author pksautomation
	 * @return
	 */
	public static String getCookieValue(Config testConfig, String cookieName)
	{
		String value = null;
		if (testConfig.driver != null)
		{
			Cookie cookie = testConfig.driver.manage().getCookieNamed(cookieName);
			if (cookie == null)
			{
				testConfig.logFail("Cookie " + cookieName + " Not found");
				return null;
			}
			value = cookie.getValue();
			testConfig.logComment("Read the cookie named '" + cookieName + "' value as '" + value + "'");
		}
		return value;
	}

	/**
	 * Uses the specified method name to generate a destination file name where
	 * PageHTML can be saved
	 * 
	 * @param Config
	 *            test config instance
	 * @return file using which we can save PageHTML
	 */
	public static File getPageHTMLFile(Config testConfig)
	{
		File dest = getScreenShotDirectory(testConfig);
		return new File(dest.getPath() + File.separator + getPageHTMLFileName(testConfig));
	}
	
	

	private static String getPageHTMLFileName(Config testConfig)
	{
		String nameScreenshot = testConfig.featureName + "." + testConfig.scenarioName;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date) + "_" + nameScreenshot + ".html";
	}

	private static File getScreenShotDirectory(Config testConfig)
	{
		File dest = new File(testConfig.getRunTimeProperty("ResultsDir") + File.separator );
		return dest;
	}

	/**
	 * Uses the specified method name to generate a destination file name where
	 * screenshot can be saved
	 * 
	 * @param Config
	 *            test config instance
	 *            @author pksautomation
	 * @return file using which we can call takescreenshot
	 */
	public static File getScreenShotFile(Config testConfig)
	{
		File dest = getScreenShotDirectory(testConfig);
		return new File(dest.getPath() + File.separator + getScreenshotFileName(testConfig));
	}
	/**
	 * 
	 * @param testConfig
	 * @author pksautomation
	 * @return
	 */
	private static String getScreenshotFileName(Config testConfig)
	{
		String nameScreenshot =((List<String>) testConfig.testScenario.getSourceTagNames()).get(0);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date) + "_" + nameScreenshot + ".png";
	}

	/**
	 * To return back to previous page
	 * 
	 * @param testConfig
	 * @param url
	 * @author pksautomation
	 */
	public static void goBack(Config testConfig)
	{
		testConfig.logComment("Clicking on back button on browser");
		testConfig.driver.navigate().back();
	}

	/**
	 * Get last update file name from specific directory
	 * @param testConfig
	 * @param dir
	 * @author pksautomation
	 * @return
	 */
	public static File lastFileModified(Config testConfig, String dir)
	{
		File fl = new File(dir);
		File[] files = fl.listFiles();
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

		return files[0];
	}

	/**
	 * @param testConfig
	 *            - element of Config
	 * @param path
	 *            - path of the folder where file is present
	 * @param name
	 *            - some text that is present in file name
	 * @author pksautomation
	 * @return - file name of the last modified file with matching text
	 */
	public static File lastFileModifiedWithDesiredName(Config testConfig, String path, String name)
	{
		File fl = new File(path);
		File choise = null;
		List<File> arrayOfSortedFiles = new ArrayList<File>();
		long lastMod = Long.MIN_VALUE;
		for (int retry = 0; retry <= 5; retry++)
		{
			// making a list of files in download folder
			System.out.println("Wait for file to download");
			Browser.wait(testConfig, 5);
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
			Log.Fail("No File found with name" + name, testConfig);
		else
			System.out.println("The file chosen is as: " + choise.getName());
		return choise;
	}

	/**
	 * Navigate to driver the URL specified
	 * 
	 * @param Config
	 *            test config instance
	 * @param url
	 *            URL to be navigated
	 *  @author pksautomation
	 */
	public static void navigateToURL(Config testConfig, String url)
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date startDate = new Date();
		startDate = new Date();
		testConfig.logComment("Navigate to web page- '" + url + "' at:- "+dateFormat.format(startDate) + " for : " +testConfig.scenarioName);
		try
		{
			//testConfig.driver.get(url);
			testConfig.driver.navigate().to(url);
		}
		catch(UnhandledAlertException ua)
		{
			testConfig.logWarning("Alert appeared during navigation");
			if(Popup.isAlertPresent(testConfig))
				Popup.ok(testConfig);
		}
		catch(Exception e) {
			testConfig.logException(e);
		}

		testConfig.logComment("Navigated to web page- '" + url + "' for : " +testConfig.scenarioName);
	}

	/**
	 * Opens the new browser instance using the given config
	 * 
	 * @return new browser instance
	 * @throws IOException
	 * @author pksautomation
	 */
	public static SelfHealingDriver openBrowser(Config testConfig)
	{
		SelfHealingDriver driver=null;
		WebDriver deligate_driver = null;
		String browser = testConfig.getRunTimeProperty("Browser");
		String browserVersion = testConfig.getRunTimeProperty("BrowserVersion");
		
		if(testConfig.BrowserName !=null && !testConfig.BrowserName.isEmpty()) {
			browser  = testConfig.BrowserName;
			testConfig.putRunTimeProperty("Browser", browser);
		}
		
		try {
			if (!testConfig.remoteExecution) {
				testConfig.logComment("Launching '" + browser + "' browser in local machine");
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
					if (testConfig.scenario != null)
						prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator);
					else
						prefs.put("download.default_directory", testConfig.downloadPath);
					
					options.setExperimentalOption("prefs", prefs);
					options.addArguments("disable-infobars");
					options.addArguments("--start-maximized");
					options.addArguments("--disable-extensions");
					options.addArguments("--disable-popup-blocking");
					if (testConfig.isBrowserInHeadlessMode) {
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
				testConfig.logComment("Launching '" + browser + "' browser on remote machine " + testConfig.getRunTimeProperty("RemoteBrowserLocation"));				
				System.out.println("Launching '" + browser + "' browser on remote machine " + testConfig.getRunTimeProperty("RemoteBrowserLocation"));
				switch (browser.toLowerCase()) {
				case "firefox":
					//Write a code for it
					break;

				case "htmlunit":// for headless browser execution , used for running api test cases
					//Write a code for it
					break;

				case "chrome":
					HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
					if (testConfig.scenario != null)
						chromePrefs.put("download.default_directory", System.getProperty("user.dir") + File.separator);
					else
						chromePrefs.put("download.default_directory", testConfig.downloadPath);
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
			        if(testConfig.isBrowserInHeadlessMode) {
			        	options.addArguments("--headless");
			        	options.addArguments("window-size=1364x768");
			        }
			        //System.out.println(" Remote browser location " + testConfig.getRunTimeProperty("RemoteBrowserLocation"));
			        if(testConfig.getRunTimeProperty("RemoteBrowserLocation") == null)
			        	deligate_driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			        else {
			        	deligate_driver = new RemoteWebDriver(new URL(testConfig.getRunTimeProperty("RemoteBrowserLocation")), cap);
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
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			driver = SelfHealingDriver.create(deligate_driver);
			driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(ObjectWaitTime*2, TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(ObjectWaitTime*2, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
		//System.out.println("Browser '" + browser + "' launched successfully for testcase:- "+testConfig.scenarioName);
		testConfig.logComment("Browser '" + browser + "' launched successfully for testcase:- "+testConfig.scenarioName);
		}
		catch(Exception e) {
			testConfig.logComment(e.toString());
			testConfig.logException(e);
		}
		return driver;
	}	

	/**
	 * Quits this driver, closing every associated window.
	 * 
	 * @param Config
	 *            test config instance for the browser to be quit
	 * @author pksautomation
	 */
	public static void quitBrowser(Config testConfig)
	{
		try
		{
			if (testConfig.driver != null)
			{
				testConfig.logComment("Quit the browser");
				testConfig.driver.quit();
			}
		}
		catch (UnreachableBrowserException e)
		{
			testConfig.logWarning(ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * Record PageHTML of the current active browser window
	 * 
	 * @param Config
	 *            test config instance
	 * @param destination
	 *            file to which Page HTML is to be saved
	 */
	public static void recordPageHTML(Config testConfig, File destination)
	{
		try
		{
			if (testConfig.driver != null)
			{
				String pageHTML;
				Boolean remoteExecution = (testConfig.getRunTimeProperty("RemoteExecution").toLowerCase().equals("true")) ? true : false;

				if (remoteExecution)
				{
					WebDriver augumentedDriver = new Augmenter().augment(testConfig.driver);
					pageHTML = (augumentedDriver).getPageSource();
				}
				else
				{
					pageHTML = testConfig.driver.getPageSource();
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

				testConfig.logComment("<B>Page HTML</B>:- <a href=" + destination.getName() + " target='_blank' >" + destination.getName() + "</a>");
			}
		}
		catch (Exception e)
		{
			testConfig.logWarning("Unable to record Page HTML:- " + ExceptionUtils.getFullStackTrace(e));
			testConfig.recordPageHTMLOnFailure = false;
			throw e;
		}
	}

	/**
	 * Switch the driver to the specified window
	 * 
	 * @param Config
	 *            test config instance
	 * @param windowHandle
	 *            Name of the window to be switched to
	 * @author pksautomation
	 */
	public static void switchToGivenWindow(Config testConfig, String windowHandle)
	{
		if (testConfig.driver != null)
		{
			testConfig.logComment("Switching to the given window handle:- " + windowHandle);
			testConfig.driver.switchTo().window(windowHandle);
			testConfig.logComment("Switched to window with URL:- " + testConfig.driver.getCurrentUrl() + ". And title as :- " + testConfig.driver.getTitle());
		}
	}

	/**
	 * Switch the driver to the new window
	 * 
	 * @param Config
	 *            test config instance
	 * @return window handle of the old window, so that it can be switched back
	 *         later
	 * @author pksautomation
	 */
	public static String switchToNewWindow(Config testConfig)
	{
		if (testConfig.driver != null)
		{
			testConfig.logComment("Switching to the new window");
			String oldWindow = testConfig.driver.getWindowHandle();

			if (testConfig.driver.getWindowHandles().size() < 2)
			{
				testConfig.logFail("No new window appeared, windows count available :-" + testConfig.driver.getWindowHandles().size());
			}

			for (String winHandle : testConfig.driver.getWindowHandles())
			{
				if (!winHandle.equals(oldWindow))
				{
					testConfig.driver.switchTo().window(winHandle);
					testConfig.logComment("Switched to window with URL:- " + testConfig.driver.getCurrentUrl() + ". And title as :- " + testConfig.driver.getTitle());
				}
			}

			return oldWindow;
		}
		return null;
	}

	public static void takeScreenShoot(Config testConfig) {
		takeScreenShoot(testConfig, new File(""));
	}

	/**
	 * Takes the screenshot of the current active browser window
	 * 
	 * @param Config
	 *            test config instance
	 * @param destination
	 *            file to which screenshot is to be saved
	 * @author pksautomation
	 */
	public static void takeScreenShoot(Config testConfig, File destination)
	{
		try
		{
			if (testConfig.driver != null)
			{
				byte[] screenshot = null;

				try
				{
					screenshot = captureScreenshot(testConfig);
				}
				catch (NullPointerException ne)
				{
					testConfig.logWarning("NullPointerException:Screenshot can't be taken. Probably browser is not reachable");
					testConfig.driver = null;
				}

				if (screenshot != null)
				{
					/* try
					{
						FileUtils.writeByteArrayToFile(destination, screenshot);

						float compressionQuality = (float) 0.5;
						try
						{
							compressionQuality = Float.parseFloat(testConfig.getRunTimeProperty("ScreenshotCompressionQuality"));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						compressJpegFile(destination, destination, compressionQuality);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				BufferedImage bImage = ImageIO.read(destination);
			    ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    ImageIO.write(bImage, "png", bos );
			    byte [] data = bos.toByteArray();
				String href = destination.getPath();
				testConfig.logComment("<B>Screenshot</B>:- <a href=" + href + " target='_blank' >" + destination.getName() + "</a>");
				 */
					testConfig.testScenario.embed(screenshot, "image/png");
					testConfig.logComment("Refer Screenshot In Attachement");
				}
			}
		}
		catch (Exception e)
		{
			testConfig.enableScreenshot = false;
			testConfig.logWarning("Unable to take screenshot2:- " + ExceptionUtils.getStackTrace(e));
			testConfig.logException(e);
		}
	}

	public static void uploadFileWithJS(Config testConfig, String strJSLocater, String strFilePath, WebElement element)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript(strJSLocater + ".style.display = \"block\";");
		js.executeScript(strJSLocater + ".style.visibility = 'visible';");
		js.executeScript(strJSLocater + ".style.opacity = 1;");
		//js.executeScript(strJSLocater + ".style.width = '1px';");
		//js.executeScript(strJSLocater + ".style.height = '1px';");
		element.sendKeys(strFilePath);
	}

	/**
	 * Verify Page URL
	 * 
	 * @param Config
	 *            test config instance
	 * @param expectedURL
	 * @return true if actual URL contains the expected URL
	 * @author pksautomation
	 */
	public static boolean verifyURL(Config testConfig, String expectedURL)
	{
		try
		{
			int retries = 30;
			String actualURL = testConfig.driver.getCurrentUrl().toLowerCase();
			expectedURL = expectedURL.toLowerCase();

			while (retries > 0)
			{
				if (actualURL.contains(expectedURL))
				{
					testConfig.logPass("Browser URL", actualURL);

					// Verify that page stays on same page (no internal
					// redirect)
					Browser.wait(testConfig, 5);
					actualURL = testConfig.driver.getCurrentUrl().toLowerCase();
					if (!actualURL.contains(expectedURL))
					{
						testConfig.logFail("Browser URL", expectedURL, actualURL);
						return false;
					}

					return true;
				}
				Browser.wait(testConfig, 1);
				actualURL = testConfig.driver.getCurrentUrl().toLowerCase();
				retries--;
			}
			testConfig.logFail("Browser URL", expectedURL, actualURL);
			return false;
		}
		catch (UnreachableBrowserException e)
		{
			// testConfig.endExecutionOnfailure = true;
			testConfig.logException(e);
			return false;
		}
	}

	/**
	 * Method use to wait until given url visible
	 * @param url
	 * @param maxTimeToWaitInSec
	 * @author pksautomation
	 */
	public static void waitForUrlToDisplay(Config testConfig, String url,int maxTimeToWaitInSec)
	{
		int count = 0;
		while(!testConfig.driver.getCurrentUrl().equals(url) && count < maxTimeToWaitInSec)
		{
			count +=1;
			Browser.wait(testConfig,1);
		}
	}

	/**
	 * Waits and accepts POP Up
	 * 
	 * @param testConfig
	 * @param pollTime
	 *            - Intervals in which Browser should be polled for alert
	 * @author pksautomation
	 */
	public static void waitForPopUp(Config testConfig, int pollTime)
	{

		// Time to poll for every 5 seconds whether popup is present or not
		int threshold = 5;

		for (int i = 0; i < pollTime; i++)
		{

			// Time to poll for every 5 seconds whether popup is present or not
			if (Popup.isAlertPresent(testConfig))
			{
				Popup.ok(testConfig);
				testConfig.logComment("Alert closed successfully");
				break;
			}

			Browser.wait(testConfig, threshold);
		}
	}

	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author pksautomation
	 */
	public static WebElement waitForVisibility(Config testConfig,WebDriver driver, By by, int maxWaitTimeInSecond,
			String description) {
		return Element.waitForVisibility(testConfig, by, description);
	}
	
	/**
	 * Launches the windows application using the given config
	 * 
	 * @return new browser instance
	 * @throws IOException
	 */
	public static SelfHealingDriver launchWindowsApp(Config testConfig)
	{
		WebDriver delegate = null;
		SelfHealingDriver driver=null;
		String winApp = testConfig.getRunTimeProperty("projectName");
		try {
			if (System.getProperty("os.name").toLowerCase().contains("window")) {
				quitWindowsApp(testConfig);
				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + File.separator+"src/test/resources/drivers/windowdriver/chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--disable-popup-blocking");
				Map<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("profile.default_content_settings.popups", 0);
				options.setBinary(testConfig.getRunTimeProperty("AppPath").toString());
				
				delegate=new ChromeDriver(options);
				driver=  SelfHealingDriver.create(delegate);
				Set<String> handles = driver.getWindowHandles();
				Iterator<String> handlesIterator = handles.iterator();
				String mainWindow = handlesIterator.next();
				driver.switchTo().window(mainWindow);
			}
			
		testConfig.logComment("Windows application '" + winApp + "' launched successfully for testcase:- "+testConfig.scenarioName);
		}
		catch(Exception e) {
			testConfig.logComment(e.toString());
		}
		return driver;
	}	

	/**
	 *  Quits the windows application instance and kills instance from task manager
	 * 
	 * @param Config :test config instance for the application to be quit
	 */
	public static void quitWindowsApp(Config testConfig)
	{
		Process process;
		try
		{
			if (testConfig.driver != null) {
				testConfig.logComment("Quit the application");
				testConfig.driver.quit();	
			}
			process = Runtime.getRuntime().exec("taskkill /F /IM InNote.exe");
			process.waitFor();
			process.destroy();	
		}
		catch (UnreachableBrowserException | IOException | InterruptedException e)
		{
			testConfig.logWarning(ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * Implicit time out
	 * 
	 * @param testConfig
	 * @param maxtimeoutInSecond
	 * @author pksautomation
	 */
	public static void implicitWait(Config testConfig, int maxtimeoutInSecond) {
		testConfig.driver.manage().timeouts().implicitlyWait(maxtimeoutInSecond, TimeUnit.SECONDS);
	}
/**
 * Wait till Element is clickable
 * @param testConfig
 * @param by
 * @param maxWaitTimeInSecond
 * @param description
 * @return --> WebElement
 * @author pksautomation
 */
	public static WebElement waitForElementToBeClickable(Config testConfig, By by, int maxWaitTimeInSecond,String description) {
		WebElement element = null;
		try {
		    WebDriverWait wait = new WebDriverWait(testConfig.driver, maxWaitTimeInSecond);
		    element = wait.until(ExpectedConditions.elementToBeClickable(by));
		}catch (Exception e) {
			testConfig.logExceptionSkipFailure(description, e, true);
		}
		return element;
		
	}
	/**
	 * wait for js to load 
	 * @param testConfig
	 * @author pksautomation
	 * @return
	 */
	public static boolean waitForJStoLoad(Config testConfig) {
		JavascriptExecutor javaScript = (JavascriptExecutor) testConfig.driver;
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Integer.parseInt(testConfig.getRunTimeProperty("ObjectWaitTime")));
		//System.out.println("Wait for jquery to load");
		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long) javaScript.executeScript("return jQuery.active") == 0);
				} catch (Exception e) {
					return true;
				}
			}
		};

		// wait for Javascript to load
		//System.out.println("Wait for JS to load");
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return javaScript.executeScript("return document.readyState").toString().equals("complete");
			}
		};

		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author pksautomation
	 */
	public static WebElement waitForVisibility(Config testConfig, By by, int maxWaitTimeInSecond,String description) {
		Long waitTime = Long.valueOf(maxWaitTimeInSecond);
		return Element.waitForVisibility(testConfig, by, description,waitTime);
	}
	
	/**
	 * overloaded method - Pause the execution for given less than one seconds
	 * 
	 * @param seconds
	 * @author pksautomation
	 */
	public static void wait(Config testConfig, double seconds)
	{
		int milliseconds = (int) (seconds * 1000);
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e)
		{
			testConfig.logException(e);
		}
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author pksautomation
	 */
	public static WebElement waitForVisibility(Config testConfig, How how, String what, String description)
	{
		return Element.waitForVisibility(testConfig, how, what, description);
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author pksautomation
	 */
	public static boolean waitForElementToLoad(Config testConfig, By by, String description)
	{
		return Element.waitForElementToLoad(testConfig, by,description);
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author pksautomation
	 */
	public static boolean waitForElementToLoad(Config testConfig, How how, String what, String description,int objectWaitTime)
	{
		return Element.waitForElementToLoad(testConfig, how,what,description,objectWaitTime);
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author pksautomation
	 */
	public static boolean waitForElementToLoad(Config testConfig, How how, String what, String description)
	{
		return Element.waitForElementToLoad(testConfig, how,what,description);
	}
	
	/**
	 * Wait for visibility of element
	 * @param testConfig
	 * @param how
	 * @param what
	 * @param description
	 * @author pksautomation
	 */
	public static boolean waitForElementToLoad(Config testConfig, By by,int maxWaitTime, String description)
	{
		return Element.waitForElementToLoad(testConfig, by, maxWaitTime,description);
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @author pksautomation
	 */
	public static WebElement waitForVisibility(Config testConfig, By by,String description) {
		return Element.waitForVisibility(testConfig, by, description);
	}
	/**
	 * 
	 * @param driver
	 * @return
	 */
	public static WebDriver getDelegateDriver(SelfHealingDriver driver) {
		return driver.getDelegate();
	}
	
	public static String getConsoleDataOfChromeBrowser(Config testConfig) {
		 String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
		 String netData = ((JavascriptExecutor)testConfig.driver).executeScript(scriptToExecute).toString();
		 return netData;
	}
}