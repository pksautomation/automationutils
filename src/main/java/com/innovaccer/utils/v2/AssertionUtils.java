package com.innovaccer.utils.v2;

import org.testng.Assert;

import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Log;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssertionUtils {

    private Config configInstance;
    private LoggerUtils loggerUtils;

    public AssertionUtils() {
        init(Config.getConfig());
    }

    public AssertionUtils(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils=new LoggerUtils(configInstance);
    }

    /**
     * Assert Failure
     *
     * @param message -> Message to be printed
     */
    public void assertFail(String message, boolean... logPageInfo) {
    	if (configInstance.isEndExecutionOnfailure()) {
    		message = "[Fail] Assert fail -> " + message;
            if (configInstance.isLogsMode()) {
            	loggerUtils.logFail(message, logPageInfo);
            	Assert.fail(message);
            } else {
            	message = "[Fail] Something went wrong during Execution";
            	loggerUtils.logFail(message, true);
            	Assert.fail("[Fail] Something went wrong during Execution");
            }
        }
    	else {
    		loggerUtils.logFail(message);
    	}
    }
    
    /**
     * Assert Pass
     *
     * @param message -> Message to be printed
     */
    public void assertPass(String message, boolean... logPageInfo) {
    		loggerUtils.logPass(message,logPageInfo);
    }

    /**
     * Assert whether the actual string contains an expected string
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertContains(String what, String expected, String actual, boolean... logPageInfo) {
        actual = actual.trim();
        
        boolean capturelogPageInfo=false;
    	if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
        if (actual != null) {
            if (!actual.contains(expected.trim())) {
                loggerUtils.logFail(what, expected, actual, capturelogPageInfo);
            } else {
                loggerUtils.logPass(what, actual, capturelogPageInfo);
            }
        } else {
            loggerUtils.logFail(what, expected, actual, capturelogPageInfo);
        }
    }

    /**
     * Assert whether Expected and Actual Strings as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertEquals(String what, String expected, String actual, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
    	if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
    	if ((expected == null & actual == null) || (expected == null && actual.isEmpty())
                || (actual == null && expected.isEmpty())) {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
            return;
        }
        if (!actual.equals(expected)) {
            loggerUtils.logFail(what, expected, actual, capturelogPageInfo);
        } else {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
        }
    }

    /**
     * Assert whether Expected and Actual String as Same or Not and Log Warning if not Same
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertEqualsAndLogWarning(String what, String expected, String actual, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
    	if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
    	if (expected == null & actual == null) {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
            return;
        }
        String message = "[Warning] Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
        if (actual != null) {
            if (!actual.equals(expected)) {
                loggerUtils.logWarning(message);
            } else {
                loggerUtils.logPass(what, actual, capturelogPageInfo);
            }
        } else {
            loggerUtils.logWarning(message);
        }
    }

    /**
     * Assert whether Actual Boolean is True
     *
     * @param what        -> Object to be verified
     * @param actual      -> Actual Boolean Value
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertTrue(String what, boolean actual, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
    	if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
        if (!actual) {
            loggerUtils.logFail(" Failed to verify " + what, capturelogPageInfo);
        } else {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
        }
    }

    /**
     * Assert whether Actual Boolean is False
     *
     * @param what        -> Object to be verified
     * @param actual      -> Actual Boolean Value
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertFalse(String what, boolean actual, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
        if (actual) {
            loggerUtils.logFail(" Failed to verify " + what, capturelogPageInfo);
        } else {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
        }
    }

    /**
     * Assert whether Expected and Actual Double Values as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected Double Value
     * @param actual      -> Actual Double Values
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertDoubleValues(String what, String expected, String actual, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
    	if (expected == null & actual == null) {
            loggerUtils.logPass(what, actual, capturelogPageInfo);
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if (actual != null) {
            double expectedValue = Double.parseDouble(expected);
            expectedValue = Double.parseDouble(df.format(expectedValue));
            double actualValue = Double.parseDouble(actual);
            actualValue = Double.parseDouble(df.format(actualValue));

            if ((expectedValue == actualValue) || Math.abs(expectedValue - actualValue) <= 0.02) {
                loggerUtils.logPass(what, actual, capturelogPageInfo);
            } else {
                loggerUtils.logFail(what, expected, actual, capturelogPageInfo);
            }
        } else {
            loggerUtils.logFail(what, expected, actual, capturelogPageInfo);
        }
    }

    /**
     * Assert whether String 1 and String 2 are Different or Not
     *
     * @param what        -> Object to be verified
     * @param string1     -> First String
     * @param string2     -> Second String
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     * @author i0465
     */
    public void assertDifferentStrings(String what, String firstStr, String secondStr, boolean... logPageInfo) {
    	
    	boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
    	if (firstStr != null && secondStr != null) {
            if (!firstStr.equalsIgnoreCase(secondStr)) {
                loggerUtils.logPass(what, secondStr, capturelogPageInfo);
            } else {
            	String message =  what + " values are different" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;
                loggerUtils.logFail(what, capturelogPageInfo);
            }
        } else {
			// Adding logs to check which value is null
        	loggerUtils.logComment("String 1 Value: " + firstStr);
        	loggerUtils.logComment("String 2 Value: " + secondStr);
            loggerUtils.logFail(what + " values are null", capturelogPageInfo);
        }
    }

    /**
     * Assert whether Expected and Actual Hash Maps as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected Map
     * @param actual      -> Actual Hash Map
     * @param loglogPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertHashMaps(String what, Map<String, String> expected, Map<String, String> actual, boolean... logPageInfo) {
    	boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
    	for (Map.Entry<String, String> entry : expected.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue(), actual.get(entry.getKey()), capturelogPageInfo);
        }
    }

    /**
     * Assert whether Expected and Actual Hash Maps as Same or Not
     *
     * @param what         -> Object to be verified
     * @param expectedList -> Expected List
     * @param actualList   -> Actual List
     * @param loglogPageInfo  -> Boolean to enable/disable logging page info
     */
    public void assertLists(String what, List<String> expectedList, List<String> actualList, boolean... logPageInfo) {
    	
        expectedList = expectedList.stream().map(String::toLowerCase).collect(Collectors.toList());
        actualList = actualList.stream().map(String::toLowerCase).collect(Collectors.toList());
        boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
        if (expectedList.equals(actualList)) {
            loggerUtils.logPass(what, expectedList, capturelogPageInfo);
        } else {
            loggerUtils.logFail(what, expectedList, actualList, capturelogPageInfo);
        }
    }
    
	/**
	 * compares values in first map with values in second map
	 * 
	 * @param testConfig
	 * @param expected
	 * @param actual
	 * @author i0465
	 */
	public  void compareEquals(Map<String, String> expected, Map<String, String> actual) {
		for (Map.Entry<String, String> entry : expected.entrySet()) {
			assertEquals(entry.getKey(), entry.getValue(), actual.get(entry.getKey()));
		}
	}
	
	/**
	 * This Method is used to compare two String for different Value
	 * 
	 * @param What     is to be tested
	 * @param Expected String to be tested
	 * @param Actual   String to be tested
	 * @author i0465
	 */
	public  void compareDifferent(String what, String firstStr, String secondStr,boolean...logPageInfo ) {
		
		boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
		if (firstStr != null && secondStr != null) {
			if (!firstStr.equalsIgnoreCase(secondStr)) {
				String message = "[" + loggerUtils.uniqueId + "]" + what + " values are different" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;
				assertPass(message,capturelogPageInfo);
			} else {
				String message = "[" + loggerUtils.uniqueId + "]" + what + " values are same" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;

				assertFail(message,capturelogPageInfo);
			}
		} else {
			// Adding logs to check which value is null
			loggerUtils.logComment("String 1 Value: " + firstStr);
			loggerUtils.logComment("String 2 Value: " + secondStr);
			assertFail(what + " values are null",capturelogPageInfo);
		}
	}
	
	/**
	 * 
	 * @param testConfig
	 * @param what
	 * @param actual
	 * @author i0465
	 */
	public void compareTrue( String what, boolean actual, boolean hardAssert,boolean ...logPageInfo) {
		boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
		if (!actual) {
			if(hardAssert) {
				configInstance.setEndExecutionOnfailure(true);
				String message = " Expected " + what + "  " + actual;
				assertFail(message,capturelogPageInfo);
			}
			else
				assertFail(" Failed to verify " + what,capturelogPageInfo);
		} else {
			assertPass(" Verified " + what,capturelogPageInfo);
		}
	}
	
	/**
	 * This method is used to compare a value to false. If the value is false, the
	 * test case passes else fails.
	 * 
	 * @param testConfig
	 * @param what
	 * @param actual
	 * @author i0465
	 */

	public void compareFalse(String what, boolean actual,boolean hardAssert,boolean... logPageInfo) {
		boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
		if (!actual) {
			loggerUtils.logPass(what, actual);
		} 
		else {
			if(hardAssert) {
				configInstance.setEndExecutionOnfailure(true);
				String message = what + "  " + actual;
				assertFail( message,capturelogPageInfo);
			}
			else
				assertFail(" Failed to verify " + what,capturelogPageInfo);
		}
	}
	
	/**
	 * Compare two string and log as warning if strings are not same
	 * 
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 * @author i0465
	 */
	public void compareEqualsWarning(String what, String expected, String actual) {
		if (expected == null & actual == null) {
			loggerUtils.logPass(what, actual);
			return;
		}

		if (actual != null) {
			if (!actual.equals(expected)) {
				loggerUtils.logWarning(what, expected, actual);
			} else {
				loggerUtils.logPass(what, actual);
			}
		} else {
			loggerUtils.logWarning(what, expected, actual);
		}
	}
	
	/**
	 * Compare two integer, double or float type values using a generic function.
	 * 
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 * @author i0465
	 */
	public <T> void compareEquals(String what, T expected, T actual, boolean hardAssert,boolean... logPageInfo  ) {
		boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
		if ((expected == null & actual == null) || (expected == null && actual.toString().isEmpty())
				|| (actual == null && expected.toString().isEmpty())) {
			loggerUtils.logPass(what, actual);
			return;
		}
		if (!actual.equals(expected)) {
			String message = "[Fail] Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";

						if( hardAssert) {
				configInstance.setEndExecutionOnfailure(true);
				assertFail(message,capturelogPageInfo);
			}
				
			else
				loggerUtils.logFail(what, expected, actual,capturelogPageInfo);
		} else {
			loggerUtils.logPass(what, actual);
		}
	}
	
	/**
	 * 
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 * @author i0465
	 */
	public  void compareContains(String what, String expected, String actual, boolean hardAssert,boolean... logPageInfo) {
		actual = actual.trim();
		String message = "[Fail]"  +what + " Expected : " + expected + " Actual : " + actual;
		boolean capturelogPageInfo=false;
		if(logPageInfo.length>0 && logPageInfo[0]) {
			capturelogPageInfo=true;
		}
		if (actual != null) {
			if (!actual.contains(expected.trim())) {
				if(hardAssert) {
					configInstance.setEndExecutionOnfailure(true);
					assertFail( message,capturelogPageInfo);
				}
				loggerUtils.logFail(what, expected, actual);
			} else {
				loggerUtils.logPass(what, actual);
			}
		} else {
			if(hardAssert) {
				configInstance.setEndExecutionOnfailure(true);
				assertFail(message,capturelogPageInfo);
			}
			loggerUtils.logFail(what, expected, actual);
		}
	}
 }