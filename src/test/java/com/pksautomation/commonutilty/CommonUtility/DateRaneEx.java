package com.pksautomation.commonutilty.CommonUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateRaneEx {
 public static void main(String str[]) {
		        String date1 = "31-01-2020";
		        String date2 = "01-12-2020";
		        List<String> dateList= new ArrayList<String>();

		        DateFormat formater = new SimpleDateFormat("dd-MM-yyyy");

		        Calendar beginCalendar = Calendar.getInstance();
		        Calendar finishCalendar = Calendar.getInstance();

		        try {
		            beginCalendar.setTime(formater.parse(date1));
		            finishCalendar.setTime(formater.parse(date2));
		            System.out.println("Bigin Calender : " + beginCalendar);
		            System.out.println("End Calender : " + finishCalendar);
		            int numOfDaysInMonth = beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		            //beginCalendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth-1);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }

		        DateFormat formaterYd = new SimpleDateFormat("dd-MM-yyyy");

		        while (beginCalendar.before(finishCalendar)) {
		        	int numOfDaysInMonth = beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		        	//System.out.println(numOfDaysInMonth);
		            beginCalendar.set(Calendar.DAY_OF_MONTH, numOfDaysInMonth);
		            String date =     formaterYd.format(beginCalendar.getTime()).toUpperCase();
		            System.out.println(date);
		            dateList.add(date);
		            beginCalendar.add(Calendar.MONTH,1);
		        }
		    }
}
