package com.pksautomation.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601DateFormat {
	
	public static Object parse(String strDate) {
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	    String string1 = "2001-07-04T12:08:56.235-0700";
	    Date date;
		try {
			date = df1.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			date=null;
			e.printStackTrace();
		}
	    return date;
	}
}
