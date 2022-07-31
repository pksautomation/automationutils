package com.innovaccer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.CharMatcher;

/**
 * 
 * @author pramod.singh
 *
 */
public class Helper {

	/**
	 * Function to change the given date value from given old Date Format to desired
	 * new Date Format.
	 * 
	 * @param testConfig
	 * @param date         -> date which needs to be converted ex: 26/11/2014
	 * @param oldDateFomat -> is the format which we are giving as input ex:
	 *                     yyyy-mm-dd
	 * @param newDateFomat -> is the format which we get after converting ex: dd MMM
	 *                     yy
	 * @author i0465
	 * @return
	 */
	public static String changeDateFormat(Config testConfig, String date, String oldDateFomat, String newDateFomat) {

		String desiredDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(newDateFomat);
		SimpleDateFormat oldFormat = new SimpleDateFormat(oldDateFomat);

		try {
			desiredDate = dateFormat.format(oldFormat.parse(date)).toString();

		} catch (ParseException e) {
			testConfig.logComment(e.getMessage());
		}
		return desiredDate;
	}

	public static String changeDateFormat(String oldDateString) {
		String OLD_FORMAT = "dd/MM/yyyy";
		String NEW_FORMAT = "yyyy/MM/dd";
		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = null;
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		return sdf.format(d);
	}

	/**
	 * Method used to change dd/mm/yyyy to yyyy-mm-dd
	 * 
	 * @param date               which needs to be converted ex: 26/11/2014
	 * @param initDateFormat     is the format which we are giving as input ex:
	 *                           dd/mm/yyyy
	 * @param expectedDateFormat is the format which we get after converting ex:
	 *                           yyyy-mm-dd
	 *                           @author i0465
	 * @return
	 * @throws ParseException
	 */
	public static String changeDateFormat(String date, String initialDateFormat, String expectedDateFormat)
			throws ParseException {
		
		Date initDate = new SimpleDateFormat(initialDateFormat).parse(date);
		SimpleDateFormat formatter = new SimpleDateFormat(expectedDateFormat);
		String parsedDate = formatter.format(initDate);

		return parsedDate;
	}

	/**
	 * Function to change Date from 20/12/15 to 20-12-15
	 * 
	 * @param date
	 * @author i0465
	 * @return
	 */
	public static String changeDateFormatSeperator(String date) {
		String dateOnly = "";
		dateOnly = date.replaceAll("/", "-");
		return dateOnly;
	}

	/**
	 * Function to change Date from 20/12/15 to 20-12-15 and to merge Date & Time to
	 * make 1 field.
	 * 
	 * @param date
	 * @param time
	 * @author i0465
	 * @return
	 */
	public static String changeDateTimeFormat(String date, String time) {
		String dateTime = "";
		dateTime = changeDateFormatSeperator(date);
		dateTime = dateTime.concat(" ");
		dateTime = dateTime.concat(time);
		return dateTime;
	}

	/**
	 * Check given String is in given date format
	 * 
	 * @param date
	 * @return boolean value
	 * @author i0465
	 */
	public static boolean verifyDateFormat(String date, String format) {

		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			df.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}

	}

	/**
	 * To change the filePath containing \\ to /
	 * 
	 * @param existingFilePath
	 * @return new FilePath
	 */
	public static String changeFilePath(String existingFilePath) {
		// format filePath
		StringBuffer newText = new StringBuffer();
		for (int i = 0; i < existingFilePath.length(); i++) {
			boolean flag = false;
			// newText.append(filePath.charAt(i));
			if (existingFilePath.charAt(i) == '/') {
				if (existingFilePath.charAt(i + 1) == '/') {
					flag = true;
					newText.append('\\');
					i++;
				} else
					newText.append(existingFilePath.charAt(i));
			}
			if (!flag)
				newText.append(existingFilePath.charAt(i));

		}
		String newFilePath = newText.toString();

		return newFilePath;
	}
	
	public static int getFirstMatchingPoint(Pattern regex, String str) {
		Matcher m = regex.matcher(str);
		if (m.find()) {
			return m.start();
		} else {
			return -1;
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
	public static void compareContains(Config testConfig, String what, String expected, String actual) {
		actual = actual.trim();
		if (actual != null) {
			if (!actual.contains(expected.trim())) {
				testConfig.logFail(what, expected, actual);
			} else {
				testConfig.logPass(what, actual);
			}
		} else {
			testConfig.logFail(what, expected, actual);
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
	public static <T> void compareEquals(Config testConfig, String what, T expected, T actual, boolean... hardAssert ) {
		if ((expected == null & actual == null) || (expected == null && actual.toString().isEmpty())
				|| (actual == null && expected.toString().isEmpty())) {
			testConfig.logPass(what, actual);
			return;
		}

		if (!actual.equals(expected)) {
			String message = "[" + testConfig.uniqueId + "] [Fail] -->  Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
			if( hardAssert.length > 0 && hardAssert[0])
				Log.Failfinal(message, testConfig);
			else
				testConfig.logFail(what, expected, actual);
		} else {
			testConfig.logPass(what, actual);
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
	public static void compareEqualsWarning(Config testConfig, String what, String expected, String actual) {
		if (expected == null & actual == null) {
			testConfig.logPass(what, actual);
			return;
		}

		if (actual != null) {
			if (!actual.equals(expected)) {
				testConfig.logWarning(what, expected, actual);
			} else {
				testConfig.logPass(what, actual);
			}
		} else {
			testConfig.logWarning(what, expected, actual);
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
	public static void compareExcelEquals(Config testConfig, String what, String expected, String actual) {
		if (actual != null) {
			if (!actual.equals("{skip}")) {
				if (!actual.equals(expected)) {
					if (expected.equals("")) {
						testConfig.logPass(what, actual);
					} else {
						testConfig.logFail(what, expected, actual);
					}
				} else {
					testConfig.logPass(what, actual);
				}
			} else {
				testConfig.logWarning("Skipping Verification of " + what + " as " + actual);
			}
		} else

		{
			testConfig.logFail(what, expected, actual);
		}
	}
	/**
	 * 
	 * @param testConfig
	 * @param what
	 * @param actual
	 * @author i0465
	 */
	public static void compareTrue(Config testConfig, String what, boolean actual, boolean... hardAssert) {
		if (!actual) {
			if(hardAssert.length > 0 && hardAssert[0])
				Log.Failfinal(" Failed to verify " + what, testConfig);
			else
				testConfig.logFail(" Failed to verify " + what);
		} else {
			testConfig.logPass(" Verified " + what);
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

	public static void compareFalse(Config testConfig, String what, boolean actual) {
		if (!actual) {
			testConfig.logPass("Verified " + what);
		} else {
			testConfig.logFail("Failed to verify " + what);
		}
	}

	/**
	 * @param testConfig
	 * @param what
	 * @param expected   This value must be value having more than 2 digits after
	 *                   decimal
	 * @param actual
	 * @author i0465
	 */
	public static void compareValues(Config testConfig, String what, String expected, String actual) {
		if (expected == null & actual == null) {
			testConfig.logPass(what, actual);
			return;
		}
		DecimalFormat df = new DecimalFormat("0.00");

		if (actual != null) {
			double expectedValue = Double.valueOf(expected);
			expectedValue = Double.valueOf(df.format(expectedValue));
			double actualValue = Double.valueOf(actual);
			actualValue = Double.valueOf(df.format(actualValue));

			if ((expectedValue == actualValue) || Math.abs(expectedValue - actualValue) <= 0.02) {
				testConfig.logPass(what, actual);
			} else {
				testConfig.logFail(what, expected, actual);
			}
		} else {
			testConfig.logFail(what, expected, actual);
		}
	}

	/**
	 * Generate a random Alphabets string of given length
	 * 
	 * @param length Length of string to be generated
	 * @author i0465
	 */
	public static String generateRandomAlphabetsString(int length) {
		Random rd = new Random();
		String aphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(aphaNumericString.charAt(rd.nextInt(aphaNumericString.length())));
		}

		return sb.toString();
	}

	/**
	 * Generate a random Alpha-Numeric string of given length
	 * 
	 * @param length Length of string to be generated
	 * @author i0465
	 */
	public static String generateRandomAlphaNumericString(int length) {
		Random rd = new Random();
		String aphaNumericString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(aphaNumericString.charAt(rd.nextInt(aphaNumericString.length())));
		}

		return sb.toString();
	}

	/**
	 * Generate a random Special Character string of given length
	 * 
	 * @param length Length of string to be generated
	 * @author i0465
	 */

	public static String generateRandomSpecialCharacterString(int length) {
		Random rd = new Random();
		String specialCharString = "~!@#$%^*()_<>?/{}[]|\";";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(specialCharString.charAt(rd.nextInt(specialCharString.length())));
		}

		return sb.toString();
	}

	/**
	 * Generate a random number of given length
	 * 
	 * @param length Length of number to be generated
	 * @author i0465
	 * @return
	 */
	public static long generateRandomNumber(int length) {
		long randomNumber = 1;
		int retryCount = 1;

		// retryCount added for generating specified length's number
		while (retryCount > 0) {
			String strNum = Double.toString(Math.random());
			strNum = strNum.replace(".", "");

			if (strNum.length() > length) {
				strNum = strNum.substring(0, length);
			} else {
				int remainingLength = length - strNum.length() + 1;
				randomNumber = generateRandomNumber(remainingLength);
				strNum = strNum.concat(Long.toString(randomNumber));
			}

			randomNumber = Long.parseLong(strNum);

			if (String.valueOf(randomNumber).length() < length) {
				retryCount++;
			} else {
				retryCount = 0;
			}

		}

		return randomNumber;
	}

	private static byte[] getByteArray(String pathToFile) {
		Path path = Paths.get(pathToFile);
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static String getCurrentDate(String format) {
		// get current date
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Get current  Date
	 * @param format
	 * @author i0465
	 * @return
	 */
	public static String getCurrentDateTime(String format) {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	/**
	 * Get current  Time
	 * @param format
	 * @author i0465
	 * @return
	 */
	public static String getCurrentTime(String format) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = formatter.format(cal.getTime());

		return currentTime;
	}
/**
 * 
 * @param dd
 * @param mm
 * @param yyyy
 * @param format
 * @author i0465
 * @return
 */
	public static String getDate(int dd, int mm, int yyyy, String format) {
		Calendar date = new GregorianCalendar(yyyy, mm - 1, dd);
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date.getTime());
	}

	/**
	 * This utility method returns a future or past date after/before number of
	 * days.
	 * 
	 * @param days
	 * @param format sample format yyyy-MM-dd
	 * @author i0465
	 * @return
	 */
	public static String getDateBeforeOrAfterDays(int days, String format) {
		Date tomorrow = new Date();
		DateFormat dateFormat = new SimpleDateFormat(format);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		tomorrow = cal.getTime();

		return dateFormat.format(tomorrow);
	}

	/**
	 * This method converts input to the NEW_FORMAT input should be in dd/MM/yyyy
	 * 
	 * @param days
	 * @param NEW_FORMAT
	 * @param date
	 * @author i0465
	 * @return
	 */
	public static String getDateBeforeOrAfterDays(int days, String NEW_FORMAT, String date) {

		String OLD_FORMAT = "dd/MM/yyyy";
		String newDateString = null;
		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(newDateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.add(Calendar.DATE, days); // number of days to add
		return sdf.format(c.getTime()); // dt is now the new date

	}
	/**
	 * 
	 * @param years
	 * @param format
	 * @author i0465
	 * @return
	 */
	public static String getDateBeforeOrAfterYears(int years, String format) {
		Date tomorrow = new Date();
		DateFormat dateFormat = new SimpleDateFormat(format);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, years);
		tomorrow = cal.getTime();

		return dateFormat.format(tomorrow);
	}
	/**
	 * 
	 * @param dd
	 * @param mm
	 * @param yyyy
	 * @param format
	 * @author i0465
	 * @return
	 */
	public static String getDatePreviousTo(int dd, int mm, int yyyy, String format) {
		Calendar date = new GregorianCalendar(yyyy, mm - 1, dd);
		date.add(Calendar.DAY_OF_YEAR, -1);
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date.getTime());
	}

	/**
	 * Replaces the arguments like {$someArg} present in input string with its value
	 * from RuntimeProperties
	 * 
	 * @param input string in which some Argument is present
	 * @author i0465
	 * @return replaced string
	 */
	public static String replaceArgumentsWithRunTimeProperties(Config testConfig, String input) {
		String value = null;
		if(input.contains("{$set:"))
			return input;
		if (input.contains("{$")) {
			int index = input.indexOf("{$");
			String key = input.substring(index + 2, input.indexOf("}", index + 2));
			value = testConfig.getRunTimeProperty(key);
			input = input.replace("{$" + key + "}", value);
			return replaceArgumentsWithRunTimeProperties(testConfig, input);
		} else {
			return input;
		}

	}

	/**
	 * Get the roundOff value to desired minimum fraction of digits.
	 * 
	 * @param roundOffValue
	 * @param minimumFractionDigits
	 * @author i0465
	 * @return
	 */
	public static String roundOff(double roundOffValue, int minimumFractionDigits) {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(minimumFractionDigits);
		//df.setMinimumFractionDigits(minimumFractionDigits);
		df.setRoundingMode(RoundingMode.HALF_UP);
		String strRoundOffValue = df.format(roundOffValue);
		return strRoundOffValue;
	}

	/**
	 * Returns a JSON key from JSON object
	 * @author i0465
	 */
	public static String getJSONKeyValue(Config testConfig, JSONObject jObject, String key) {
		String value = null;
		if (jObject != null) {
			try {
				if (key != null)
					value = jObject.get(key).toString();
			} catch (JSONException e) {
				testConfig.logException(e);
			}
		}
		return value;
	}

	/**
	 * This Method is used to create a file with given format
	 * 
	 * @param extension
	 * @author i0465
	 * @return -- File Path
	 */
	public static String createFileWithGivenFormat(Config testConfig, String extension) {
		String datetime = Helper.getCurrentDateTime("yyyy-MM-dd HH:mm:ss.SSS");
		testConfig.logComment("datetime=" + datetime);
		datetime = CharMatcher.is(':').removeFrom(datetime);
		String newFilePath = testConfig.downloadPath;
		File file = new File(newFilePath, datetime + extension);
		try {
			file.createNewFile();
			newFilePath = newFilePath + datetime + extension;
		} catch (IOException e) {
			newFilePath = null;
			e.printStackTrace();
		}
		return newFilePath;
	}

	/**
	 * This Method is used to create folder at given path
	 * 
	 * @param path
	 * @author i0465
	 * @return
	 */
	public static boolean createFolder(String path) {
		File newdir = new File(path);
		boolean result = false;
		if (!newdir.exists()) {
			// System.out.println("Creating Directory : " + path);
			try {
				Files.createDirectories(Paths.get(path));
				System.out.println("Directory created successfully : " + path);
				result = true;
			} catch (Exception se) {
				System.out.println("========>>Exception while creating Directory : " + path);
				se.printStackTrace();
			}
		} else {
			System.out.println("Directory: " + path + " already Exist");
			result = true;
		}
		return result;
	}

	/**
	 * This function is used to update in an existing text file. (If file is not
	 * present then will create new file also)
	 * 
	 * @param testConfig
	 * @param location
	 * @param textToUpdate
	 * @author i0465
	 */
	public static void updateTextFile(Config testConfig, String location, String textToUpdate) {
		try {
			Path pathToFile = Paths.get(location);
			Files.createDirectories(pathToFile.getParent());

			File file = new File(location);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fstream = new FileWriter(location, true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(textToUpdate + ",");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	public static void compareDifferent(Config testConfig, String what, String firstStr, String secondStr) {
		if (firstStr != null && secondStr != null) {
			if (!firstStr.equalsIgnoreCase(secondStr)) {
				String message = "[" + testConfig.uniqueId + "]" + what + " values are different" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;
				Log.Pass(message, testConfig);
			} else {
				String message = "[" + testConfig.uniqueId + "]" + what + " values are same" + " Expected is : "
						+ firstStr + " and Actual is: " + secondStr;

				Log.Fail(message, testConfig);
			}
		} else {
			// Adding logs to check which value is null
			testConfig.logComment("String 1 Value: " + firstStr);
			testConfig.logComment("String 2 Value: " + secondStr);
			Log.Fail(what + " values are null", testConfig);
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
	public static void compareEquals(Config testConfig, Map<String, String> expected, Map<String, String> actual) {
		for (Map.Entry<String, String> entry : expected.entrySet()) {
			Helper.compareEquals(testConfig, entry.getKey(), entry.getValue(), actual.get(entry.getKey()));
		}
	}

	/**
	 * Update given dateTime string
	 * 
	 * @param dateTime -> to be updated
	 * @param hour     -> to be updated with
	 * @author i0465
	 * @return dateTime String
	 * 
	 */
	public static String getDateTimeWithHourDifference(String dateTime, int hour) {
		String[] actualdateTime = dateTime.split("\\."); // split dateTime string if passed as 2015-09-12 23:45:78.0
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = formatter.parse(actualdateTime[0]); // parse actualDateTime given
			formatter.format(date); // change format of actualdateTime string to date
		} catch (Exception e) {
			System.out.println(e);
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date); // set dateTime into calendar
		cal.add(Calendar.HOUR, hour); // update dateTime with specified hours
		Date requireddateTime = cal.getTime(); // get dateTime after updating
		String updatedDateTime = formatter.format(requireddateTime); // change format of dateTime to dateTime string

		return updatedDateTime;
	}

	/**
	 * Check List Contains Given String
	 * 
	 * @param list
	 * @param stringToMatch
	 * @author i0465
	 * @return true/false
	 * 
	 */
	public static boolean listContainsString(List<String> list, String stringToMatch) {
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			String tempString = iter.next();
			if (tempString.contains(stringToMatch))
				return true;
		}
		return false;
	}

	/**
	 * Replace String in File
	 * 
	 * @param testConfig
	 * @param filePath
	 * @param search
	 * @param replacement
	 * @author i0465
	 */
	public static void replaceStringInFile(Config testConfig, String filePath, String search, String replacement) {
		File htmlFile = new File(filePath);
		try {

			FileReader fr = new FileReader(htmlFile);
			String s;
			String totalStr = "";
			try (BufferedReader br = new BufferedReader(fr)) {

				while ((s = br.readLine()) != null) {
					totalStr += s;
				}
				totalStr = totalStr.replaceAll(search, replacement);
				FileWriter fw = new FileWriter(htmlFile);
				fw.write(totalStr);
				fw.close();

			}
		} catch (Exception e) {
			testConfig.logException(e);
		}
	}

	/**
	 * return Bson Document
	 * 
	 * @param jsonString
	 * @author i0465
	 * @return
	 */
	public static BsonDocument convertJSONStringIntoBsonDocument(Config testConfig,String jsonString) {
		BsonDocument document=null;
		try {
			document = BsonDocument.parse(jsonString.trim());
		}
		catch(Exception e) {
			testConfig.logException(e);
		}
		return document;
	}

	/**
	 * Parse from Document Object into JSONObject
	 * 
	 * @param doc
	 * @author i0465
	 * @return JSONObject
	 */
	public static JSONObject pareseDocumentIntoJSONObject(Document doc) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(doc.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Fail test scenarios
	 * 
	 * @param testConfig
	 * @author i0465
	 * @param message
	 */
	public static void failTestScenario(Config testConfig, String message) {
		testConfig.endExecutionOnfailure = true;
		Log.failure(message, testConfig);
	}

	/**
	 * Convert Epoch time to human readable form according to given format
	 * 
	 * @param dataFormat
	 * @param epoch
	 * @author i0465
	 * @return
	 */
	public static String convertEpochTimeToHumanReadable(String dataFormat, long epoch) {
		String convertedDate;
		Date date = new Date(epoch);
		SimpleDateFormat formatter = new SimpleDateFormat(dataFormat);
		convertedDate = formatter.format(date);
		return convertedDate;
	}

	/**
	 * get Date difference in days
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDateDifferenceInFormaty_m_d(String startDate, String endDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String yyyy_mm_dd = null;
		Date d1 = null;
		Date d2 = null;
		try {
			startDate = format.format(format.parse(startDate));
			endDate = format.format(format.parse(endDate));
			LocalDate startD = LocalDate.parse(startDate);
			LocalDate startD1 = LocalDate.parse(endDate);
			Period diff = Period.between(startD, startD1);
			yyyy_mm_dd = diff.getYears() + "_" + diff.getMonths() + "_" + diff.getDays();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return yyyy_mm_dd;
	}

	/**
	 * Get Date Before or After Years From Give Date
	 * 
	 * @param years  ---> years to get before or after,years in positive means after
	 *               given date and in negative means before date
	 * @param format
	 * @param date
	 * @return
	 * * @author pramod.singh
	 */
	public static String getDateBeforeOrAfterYearsFromGiveDate(int years, String format, String date) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		LocalDate localDate = LocalDate.parse(date);
		if (years > 0)
			localDate = localDate.plusYears(years);
		else
			localDate = localDate.minusYears(Math.abs(years));
		Date resultDate = null;
		try {
			resultDate = dateFormat.parse(localDate.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateFormat.format(resultDate);
	}
	
	/**
	 * Get the roundOff value to desired minimum fraction of digits.
	 * 
	 * @param roundOffValue
	 * @param minimumFractionDigits
	 * @return
	 * @author pramod.singh
	 */
	public static String roundOff(double roundOffValue, int minimumFractionDigits,int maximumFractionDigit) {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(maximumFractionDigit);
		df.setMinimumFractionDigits(minimumFractionDigits);
		df.setRoundingMode(RoundingMode.HALF_UP);
		String strRoundOffValue = df.format(roundOffValue);
		return strRoundOffValue;
	}
	
	/**
	 * get current time zone id
	 * @param testConfig
	 * @return
	 */
	public static String getSystemTimeZoneId(Config testConfig) {
		testConfig.logComment("System Time zone name is : " + TimeZone.getDefault().getDisplayName());
		testConfig.logComment("System timezone id is : " + TimeZone.getDefault().getID());
		return TimeZone.getDefault().getID();
	}
	
	/**
	 * Get total nos of days between two date
	 * @param firstDate
	 * @param secondDate
	 * @param format
	 * @return
	 */
	public static long getDaysBetweenTwoDate(String firstDate,String secondDate,String format) {
		SimpleDateFormat myFormat = new SimpleDateFormat(format);
		long days=0;

		try {
		    Date date1 = myFormat.parse(firstDate);
		    Date date2 = myFormat.parse(secondDate);
		    long diff = date2.getTime() - date1.getTime();
		    days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return days;
	}
	
	/**
	 * Convert JSON file data into JSONObject
	 * @param testConfig
	 * @param filePath
	 * @return
	 * @author i0465
	 */
	public static JSONObject convertJSONFileToJSONObject(Config testConfig, String filePath) {
		JSONObject jsonObj = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line+"\n");
			}

			jsonObj = new JSONObject(builder.toString());
			//testConfig.logComment(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObj;

	}
	/**
	 * Convert JSON file data into JSON Array
	 * @param testConfig
	 * @param filePath
	 * @return
	 * @author i0465
	 */
	public static JSONArray convertJSONFileToJSONArrayObject(Config testConfig, String filePath) {
		JSONArray jsonarray = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line+"\n");
			}

			jsonarray = new JSONArray(builder.toString());
			//testConfig.logComment(obj.toString());
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return jsonarray;
	}
	/**
	 * Comparing two list value after converting all values to lower case
	 * @param testConfig
	 * @param what
	 * @param actualList
	 * @param expectedList
	 * @param hardAssert
	 */
	public static void compareLists(Config testConfig, String what, List<String> actualList, List<String> expectedList,boolean... hardAssert) {
		actualList = actualList.stream().map(String::toLowerCase).collect(Collectors.toList());
		expectedList = expectedList.stream().map(String::toLowerCase).collect(Collectors.toList());
		if(expectedList.equals(actualList)) {
			testConfig.logPass("expected " + what, expectedList);
		}
		else {
			testConfig.logFail(what + "", expectedList, actualList);
		}
	}
	
	public static HashMap<String, HashMap<String, String>> readTestData(String filePath, String fileName) throws Throwable {
		JSONArray orgArray;
		JSONObject parentObj,childObj,orgObj;
		HashMap<String,HashMap<String,String>> jsonMapofMap = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String> jsonMap = new HashMap<String,String> ();
		String value="",key="";
		filePath=filePath+fileName+".json";
		InputStream is = new FileInputStream(new File(filePath));
        JSONTokener tokener = new JSONTokener(is); 
        try {
			parentObj = new JSONObject(tokener);
			Iterator<String> iterator = parentObj.keys();
			while (iterator.hasNext()) {
				String tagName = iterator.next().toString();
				childObj = parentObj.getJSONObject(tagName);
				Iterator<String> childit = childObj.keys();
				while (childit.hasNext()) {
					key = childit.next().toString();
					if(childObj.get(key) instanceof JSONObject && childObj.get(key).toString().contains("selection") && childObj.get(key).toString().contains("values")) {
						orgObj = childObj.getJSONObject(key);
						orgArray = orgObj.getJSONArray("values");
						value = (String)orgArray.get(orgObj.getInt("selection") - 1);
					}
					else if (childObj.get(key) instanceof JSONObject) {
						value = generateDynamicValue((JSONObject)childObj.get(key));
						
					}
					else 
						value=childObj.get(key).toString();
				
					jsonMap.put(key,value);
			}
			jsonMapofMap.put(tagName, jsonMap);
			jsonMap = new HashMap<String,String> ();
			}
        }catch (Exception e) {
        	e.printStackTrace();
        }
		
		return jsonMapofMap;
	}
	
	public static String generateDynamicValue(JSONObject dynamicObje) throws Throwable{
    	int length=dynamicObje.getInt("length");
    	String email="",prefix="",suffix="",dynamicValue="";
		try { 
			switch(dynamicObje.getString("type")) {
				case "NUMERIC":
					dynamicValue=(String.valueOf(Helper.generateRandomNumber(length)));
					break;
				case "ALPHABETIC":
					dynamicValue=(String.valueOf(Helper.generateRandomAlphabetsString(length)));
					break;
				case "ALPHA_NUMERIC":
					dynamicValue=(String.valueOf(Helper.generateRandomAlphaNumericString(length)));
					break;
			}
			if(dynamicObje.has("prefix") && !dynamicObje.isNull("prefix")) {
				prefix=dynamicObje.getString("prefix");
			}
			if(dynamicObje.has("suffix") && !dynamicObje.isNull("suffix")) {
				suffix=dynamicObje.getString("suffix");
			}
		dynamicValue=prefix+dynamicValue+suffix;						
		
    	}catch (Exception e) {
    		e.printStackTrace();
    		
    	}
    	return dynamicValue;
    }
}
