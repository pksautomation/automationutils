package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Element;
import com.innovaccer.utils.Helper;

import java.util.List;
import java.util.Map;

public class AssertionHelper {

    public Config testConfig;

    public AssertionHelper(Config testConfig) {
        this.testConfig = testConfig;
    }

    public void assertContains(String what, String expected, String actual) {
        Helper.compareContains(testConfig, what, expected, actual);
    }

    public void assertEquals(String what, String expected, String actual, boolean isHardAssert) {
        Helper.compareEquals(testConfig, what, expected, actual, isHardAssert);
    }

    public void assertEqualsAndLogWarning(String what, String expected, String actual) {
        Helper.compareEqualsWarning(testConfig, what, expected, actual);
    }

    public void assertTrue(String what, boolean actual, boolean isHardAssert) {
        Helper.compareTrue(testConfig, what, actual, isHardAssert);
    }

    public void assertFalse(String what, boolean actual) {
        Helper.compareFalse(testConfig, what, actual);
    }

    public void assertDoubleValues(String what, String expected, String actual) {
        Helper.compareValues(testConfig, what, expected, actual);
    }

    public void assertDifferentStrings(String what, String string1, String string2) {
        Helper.compareDifferent(testConfig, what, string1, string2);
    }

    public void assertHashMaps(Map<String, String> expected, Map<String, String> actual) {
        Helper.compareEquals(testConfig, expected, actual);
    }

    public void assertLists(String what, List<String> actualList, List<String> expectedList) {
        Helper.compareLists(testConfig, what, actualList, expectedList, true);
    }

}