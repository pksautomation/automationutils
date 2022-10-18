package com.innovaccer.utils.v2;

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

/**
 * 
 * @author pramod.singh
 *
 */
public class Helper {


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
	
	/**
	 *  get first index of matching regex in string
	 * @param regex
	 * @param str
	 * @return 
	 * @author i0465
	 */
	public static int getFirstMatchingPoint(Pattern regex, String str) {
		Matcher m = regex.matcher(str);
		if (m.find()) {
			return m.start();
		} else {
			return -1;
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
	public static void replaceStringInFile(String filePath, String search, String replacement) {
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
			e.printStackTrace();
		}
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
	public static String getSystemTimeZoneId() {
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
	 * 
	 * @param listOfSteps
	 * @return
	 */
	public static String getTextReportForFailureTestSteps(List<String> listOfSteps) {
		StringBuilder finalReport = new StringBuilder();
		finalReport.append("===================================================================================================\n");
		finalReport.append("<h2> Following list of Steps are failed due to exceptions or failure of Soft Assertion check </h2>");
		for (int i = 0; i < listOfSteps.size(); i++) {
			finalReport.append("<h4>" + listOfSteps.get(i) + " is failed due to some handled exception or Soft Assertion </h4> </br>");
		}
		finalReport.append("\n ---------------------------------------------------------------------------------------------------- \n");	
		finalReport.append("</br> <h3> Attention Please  -> Please follow any one instructions to get details of failure reason of above mention steps </h3></br> ");
		finalReport.append("<li> a) Follow attached html reports where step wise failure reasons are fiven </li> </br>");
		finalReport.append("<li> b) For particular step sequence number, Follow after hook of that step or Last html file attached </li> </br>");
		finalReport.append("---------------------------------------------------------------------------------------------------- \n");	
		finalReport.append("<p> Please Find Failure reason in details Step wise </p> ");
		for (int i = 0; i < listOfSteps.size(); i++) {
			finalReport.append("/</br></br> <h3> Failure reason details for  " + listOfSteps.get(i) + "</h3>");
			finalReport.append("</br> ------------------------------- Step Failure reason in details-------------------- </br>");
			finalReport.append(listOfSteps.get(i));
		}
		return finalReport.toString();
		
	}
}
