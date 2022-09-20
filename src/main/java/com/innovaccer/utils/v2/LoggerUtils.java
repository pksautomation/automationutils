package com.innovaccer.utils.v2;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.Log;

public class LoggerUtils {

    private Config testConfig;

    public LoggerUtils(Config testConfig) {
        this.testConfig = testConfig;
    }
    public LoggerUtils() {
        this.testConfig = Config.getConfig();
    }

    public void logComment(String message) {
        testConfig.logComment(message);
    }

    public void logWarning(String message) {
        testConfig.logWarning(message);
    }

    public void logHighlight(String message) {
        testConfig.logHighLight(message);
    }

    public void logException(Exception exception) {
        testConfig.logException(exception);
    }

    public void logExceptionAndSkipFailure(String message, Throwable e, boolean... isTakeScreenShot) {
        testConfig.logExceptionSkipFailure(message, e, isTakeScreenShot);
    }

    public void logException(String message, Throwable e, boolean... isTakeScreenShot) {
        testConfig.logException(message, e, isTakeScreenShot);
    }

    public void logFailureException(Exception exception) {
        testConfig.logFailureException(exception);
    }
    
    public void embedStringAsHTMLInReport(String text) {
    	Log.embedMessageAsHTMLInReport(testConfig, text);
    }
	public void logFail(String string) {
		// TODO Auto-generated method stub
		
	}
	public void Fail(String string, Config configInstance) {
		// TODO Auto-generated method stub
		
	}
	public void logPass(String string, String actualURL) {
		// TODO Auto-generated method stub
		
	}
	public void logFail(String string, String expectedURL, String actualURL) {
		// TODO Auto-generated method stub
		
	}
	public void Warning(String string, Config configInstance) {
		// TODO Auto-generated method stub
		
	}

}