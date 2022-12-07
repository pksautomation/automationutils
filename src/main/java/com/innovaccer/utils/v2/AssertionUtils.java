package com.innovaccer.utils.v2;

import org.testng.Assert;

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
    }

    /**
     * Assert Failure
     *
     * @param message -> Message to be printed
     */
    public void assertFail(String message) {
    	if (configInstance.isEndExecutionOnfailure()) {
            if (configInstance.isLogsMode()) {
            	Assert.fail(message);
            } else
            	Assert.fail(" --> [Fail] Something went wrong during Execution");
        }
    }

    /**
     * Assert whether the actual string contains an expected string
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertContains(String what, String expected, String actual, boolean... logPageInfo) {
        actual = actual.trim();
        loggerUtils = new LoggerUtils(configInstance);
        if (actual != null) {
            if (!actual.contains(expected.trim())) {
                loggerUtils.logFail(what, expected, actual, logPageInfo[0]);
            } else {
                loggerUtils.logPass(what, actual, logPageInfo[0]);
            }
        } else {
            loggerUtils.logFail(what, expected, actual, logPageInfo[0]);
        }
    }

    /**
     * Assert whether Expected and Actual Strings as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertEquals(String what, String expected, String actual, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if ((expected == null & actual == null) || (expected == null && actual.isEmpty())
                || (actual == null && expected.isEmpty())) {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
            return;
        }
        if (!actual.equals(expected)) {
            loggerUtils.logFail(what, expected, actual, logPageInfo[0]);
        } else {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
        }
    }

    /**
     * Assert whether Expected and Actual String as Same or Not and Log Warning if not Same
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected String
     * @param actual      -> Actual String
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertEqualsAndLogWarning(String what, String expected, String actual, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if (expected == null & actual == null) {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
            return;
        }
        String message = "[Warning]--> Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
        if (actual != null) {
            if (!actual.equals(expected)) {
                loggerUtils.logWarning(message);
            } else {
                loggerUtils.logPass(what, actual, logPageInfo[0]);
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
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertTrue(String what, boolean actual, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if (!actual) {
            loggerUtils.logFail(" Failed to verify " + what, logPageInfo[0]);
        } else {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
        }
    }

    /**
     * Assert whether Actual Boolean is False
     *
     * @param what        -> Object to be verified
     * @param actual      -> Actual Boolean Value
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertFalse(String what, boolean actual, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if (actual) {
            loggerUtils.logFail(" Failed to verify " + what, logPageInfo[0]);
        } else {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
        }
    }

    /**
     * Assert whether Expected and Actual Double Values as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected Double Value
     * @param actual      -> Actual Double Values
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertDoubleValues(String what, String expected, String actual, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if (expected == null & actual == null) {
            loggerUtils.logPass(what, actual, logPageInfo[0]);
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if (actual != null) {
            double expectedValue = Double.parseDouble(expected);
            expectedValue = Double.parseDouble(df.format(expectedValue));
            double actualValue = Double.parseDouble(actual);
            actualValue = Double.parseDouble(df.format(actualValue));

            if ((expectedValue == actualValue) || Math.abs(expectedValue - actualValue) <= 0.02) {
                loggerUtils.logPass(what, actual, logPageInfo[0]);
            } else {
                loggerUtils.logFail(what, expected, actual, logPageInfo[0]);
            }
        } else {
            loggerUtils.logFail(what, expected, actual, logPageInfo[0]);
        }
    }

    /**
     * Assert whether String 1 and String 2 are Different or Not
     *
     * @param what        -> Object to be verified
     * @param string1     -> First String
     * @param string2     -> Second String
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertDifferentStrings(String what, String firstStr, String secondStr, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        if (firstStr != null && secondStr != null) {
            if (!firstStr.equalsIgnoreCase(secondStr)) {
            	String message = "[" + configInstance.getUniqueId() + "]" + what + " values are different" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;
                loggerUtils.logPass(what, secondStr, logPageInfo[0]);
            } else {
            	
                loggerUtils.logFail(what, logPageInfo[0]);
            }
        } else {
			// Adding logs to check which value is null
        	loggerUtils.logComment("String 1 Value: " + firstStr);
        	loggerUtils.logComment("String 2 Value: " + secondStr);
            loggerUtils.logFail(what + " values are null", logPageInfo[0]);
        }
    }

    /**
     * Assert whether Expected and Actual Hash Maps as Same or Not
     *
     * @param what        -> Object to be verified
     * @param expected    -> Expected Map
     * @param actual      -> Actual Hash Map
     * @param logPageInfo -> Boolean to enable/disable logging page info
     */
    public void assertHashMaps(String what, Map<String, String> expected, Map<String, String> actual, boolean... logPageInfo) {
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue(), actual.get(entry.getKey()), logPageInfo[0]);
        }
    }

    /**
     * Assert whether Expected and Actual Hash Maps as Same or Not
     *
     * @param what         -> Object to be verified
     * @param expectedList -> Expected List
     * @param actualList   -> Actual List
     * @param logPageInfo  -> Boolean to enable/disable logging page info
     */
    public void assertLists(String what, List<String> expectedList, List<String> actualList, boolean... logPageInfo) {
    	loggerUtils = new LoggerUtils(configInstance);
        expectedList = expectedList.stream().map(String::toLowerCase).collect(Collectors.toList());
        actualList = actualList.stream().map(String::toLowerCase).collect(Collectors.toList());
        if (expectedList.equals(actualList)) {
            loggerUtils.logPass(what, expectedList, logPageInfo[0]);
        } else {
            loggerUtils.logFail(what, expectedList, actualList, logPageInfo[0]);
        }
    }
}