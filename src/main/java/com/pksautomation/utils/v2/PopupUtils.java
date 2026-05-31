package com.pksautomation.utils.v2;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author pksautomation
 */
public class PopupUtils {

    private Config testConfig;
    private LoggerUtils loggerUtils;
    private AssertionUtils assertionUtils;


    public PopupUtils(Config testConfig) {
        init(testConfig);
    }

    public PopupUtils() {
        init(Config.getConfig());
    }

    private void init(Config config) {
        this.testConfig = config;
        loggerUtils = new LoggerUtils(this.testConfig);
        assertionUtils = new AssertionUtils(this.testConfig);
    }

    public void cancel() {
        Alert alert = getPopup();
        if (alert != null) {
            alert.accept();
            testConfig.getDriver().switchTo().defaultContent();
            loggerUtils.logComment("Dismissed the Pop-up.");
        }

    }

    public void confirmNoPopup(String txt) {
        try {
            Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
            WebDriverWait wait = new WebDriverWait(testConfig.getDriver(), 10);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                boolean result = true;
                if (alert.getText().equals(txt)) {
                    result = false;
                    testConfig.getDriver().switchTo().alert();
                    assertionUtils.assertTrue("Absence of Alert with text as:" + txt, result);
                    alert.accept();
                    return;
                }
                assertionUtils.assertTrue("Absence of Alert with text as: " + txt, result);
            } else {
                assertionUtils.assertTrue("Absence of Alert with text as: " + txt, true);
            }
        } catch (Exception e) {
            assertionUtils.assertTrue("Absence of Alert with text as: " + txt, true);
        }
    }

    public Alert getPopup() {
        try {
            Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
            WebDriverWait wait = new WebDriverWait(testConfig.getDriver(), ObjectWaitTime);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert = testConfig.getDriver().switchTo().alert();
            loggerUtils.logComment("Got the Alert with text '" + alert.getText() + "'");
            return alert;
        } catch (Exception e) {
            loggerUtils.logComment("Exception while getting the PopUp...");
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAlertPresent() {
        return isAlertPresent(true);
    }

    public boolean isAlertPresent(boolean doLogging) {
        try {
            Alert alert = testConfig.getDriver().switchTo().alert();
            loggerUtils.logComment("Got the Alert with text '" + alert.getText() + "'");
            return true;
        } catch (Exception e) {
            if (doLogging)
                loggerUtils.logComment("Checked alert is not present");
            return false;
        }
    }

    public void ok() {
        Alert alert = getPopup();

        if (alert != null) {
            alert.accept();
            testConfig.getDriver().switchTo().defaultContent();
            loggerUtils.logComment("Accepted the Pop-up.");
        }

    }

    public String text(Config testConfig) {
        Alert alert = getPopup();
        return alert.getText();
    }

    public void type(String characters) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            loggerUtils.logFailureException(e);
        }
        robot.delay(9000);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        robot.delay(5000);
        StringBuffer newText = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {
            newText.append(characters.charAt(i));
            if (characters.charAt(i) == '\\') {
                if (characters.charAt(i + 1) == '\\') {
                    i++;
                }
            }
        }
        StringSelection stringSelection = new StringSelection(newText.toString());
        clipboard.setContents(stringSelection, stringSelection);
        robot.delay(5000);
        robot.keyPress(KeyEvent.VK_CLEAR);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(5000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(5000);
    }

    public void clearClipBoard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("");
        clipboard.setContents(stringSelection, stringSelection);
    }

    public String getClipboardContents(WebElement el) {
        String returnText = null;
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipboardContents = systemClipboard.getContents(el);
        try {
            if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException exception) {
            loggerUtils.logFailureException(exception);
        }
        return returnText;
    }
}