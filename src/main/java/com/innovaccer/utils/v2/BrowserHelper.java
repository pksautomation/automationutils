package com.innovaccer.utils.v2;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.innovaccer.utils.Browser;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Log;

import java.io.File;

public class BrowserHelper {
	public Config configInstance;
	public BrowserHelper(Config testConfig) {
		configInstance=testConfig;
	}
	
	public void navigateToLoginPage() {
		String url = configInstance.getRunTimeProperty("EnvURL");
		Browser.navigateToURL(configInstance, url);
	}
	
	public void openBrowser()
	{
		int retryCnt = 10;
		while (configInstance.driver == null && retryCnt > 0)
		{
			try
			{
				configInstance.driver = Browser.openBrowser(configInstance);

			}
			catch (Exception e)
			{
				Log.Warning("Retrying the browser launch:-" + e.toString(), configInstance);
				System.out.println(e.toString());
			}
			if (configInstance.driver == null)
			{
				retryCnt--;
				if (retryCnt == 0)
				{
					configInstance.logFail("Browser could not be opened for : "+configInstance.getScenarioName());
					Assert.assertTrue(false);
				}
				Browser.wait(configInstance, 2);
			}

		}
		configInstance.endExecutionOnfailure = false;
	}
	
	/* Close the browser
	 * @author pramod.singh
	 */
	public void closeBrowser()
	{
		configInstance.logToStandardOut = true;
		Browser.quitBrowser(configInstance);
		configInstance.driver = null;
	}


	public void setImplicitWait(int timeInSeconds) {
		Browser.implicitWait(configInstance, timeInSeconds);
	}

	public void takeScreenshot() {
		Browser.takeScreenShoot(configInstance);
	}

	public void deleteCookies() {
		Browser.deleteCookies(configInstance);
	}

	public void waitForPageTitleToContain(String title) {
		Browser.waitForPageTitleToContain(configInstance, title);
	}

	public void downloadDesiredFile(String filePath, String fileName) {
		Browser.DesiredFileDownload(configInstance, filePath, fileName);
	}

	public void executeJavascript(String javascriptCode) {
		Browser.executeJavaScript(configInstance, javascriptCode);
	}

	public void quitBrowser() {
		Browser.closeBrowser(configInstance);
	}

	public void navigateBack() {
		Browser.goBack(configInstance);
	}

	public String getCookieValue(String cookieKey) {
		return Browser.getCookieValue(configInstance, cookieKey);
	}

	public File getPageHtmlFile() {
		return Browser.getPageHTMLFile(configInstance);
	}

	public File getScreenshotFile() {
		return Browser.getScreenShotFile(configInstance);
	}

	public File getLastModifiedFileInDirectory(String directoryPath) {
		return Browser.lastFileModified(configInstance, directoryPath);
	}

	public File getLastModifiedFileInDirectoryForAName(String directoryPath, String name) {
		return Browser.lastFileModifiedWithDesiredName(configInstance, directoryPath, name);
	}

	public void gotoUrl(String url) {
		Browser.navigateToURL(configInstance, url);
	}

	public void recordHtmlPage(File destination) {
		Browser.recordPageHTML(configInstance, destination);
	}

	public void switchToGivenWindow(String windowHandleName) {
		Browser.switchToGivenWindow(configInstance, windowHandleName);
	}

	public void switchToNewWindow() {
		Browser.switchToNewWindow(configInstance);
	}

	public void uploadFileUsingJavascript(String jsLocator, String filePath, WebElement element) {
		Browser.uploadFileWithJS(configInstance, jsLocator, filePath, element);
	}

	public boolean verifyUrl(String expectedUrl) {
		return Browser.verifyURL(configInstance, expectedUrl);
	}

	public void wait(int timeInSeconds) {
		Browser.wait(configInstance, timeInSeconds);
	}

	public void waitForUrlToMatch(String expectedUrl, int timeInSeconds) {
		Browser.waitForUrlToDisplay(configInstance, expectedUrl, timeInSeconds);
	}

	public void waitForPopup(int timeInSeconds) {
		Browser.waitForPopUp(configInstance, timeInSeconds);
	}

	public void launchWindowsApp() {
		Browser.launchWindowsApp(configInstance);
	}

	public void quitWindowsApp() {
		Browser.quitWindowsApp(configInstance);
	}

	public void getDelegateDriver(SelfHealingDriver driver) {
		Browser.getDelegateDriver(driver);
	}

	public String getDataFromConsoleInChromeBrowser() {
		return Browser.getConsoleDataOfChromeBrowser(configInstance);
	}

}
