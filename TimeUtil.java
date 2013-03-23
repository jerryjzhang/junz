package com.junz.hibernate.time;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {
  public static Timestamp createTimeRepresentation(long unixTime, String timeZoneID){
		//1. The Unix Time number
		//   --> 
		//   The representation of time in specified TimeZone(stored in Calendar)
		Calendar calUserTz = Calendar.getInstance(TimeZone.getTimeZone(timeZoneID));
		calUserTz.setTimeInMillis(unixTime);
		
		//2. The representation of time in specified TimeZone(stored in Calendar)
		//	 --->
		//   The relative Unix Time Number to local TimeZone
		Calendar calLocalTz = Calendar.getInstance();
		calLocalTz.set(calUserTz.get(Calendar.YEAR), calUserTz.get(Calendar.MONTH), 
				calUserTz.get(Calendar.DAY_OF_MONTH), calUserTz.get(Calendar.HOUR_OF_DAY), 
				calUserTz.get(Calendar.MINUTE), calUserTz.get(Calendar.SECOND));
		calLocalTz.set(Calendar.MILLISECOND, calUserTz.get(Calendar.MILLISECOND));		
		
		//3. The relative Unix Time Number to local TimeZone
		//   -->
		//   The representation of time in specified TimeZone(stored in Timestamp)
		Timestamp time = new Timestamp(calLocalTz.getTimeInMillis());
		
		return time;
	}
	
	public static long createUnixTimeNumber(Timestamp time, String timeZoneID){
		//1. The relative Unix Time Number to local TimeZone
		//   -->
		//   The representation of time in specified TimeZone(stored in Calendar)
		Calendar calLocalTz = Calendar.getInstance();
		calLocalTz.setTimeInMillis(time.getTime());
		
		//2. The representation of time in specified TimeZone(stored in Calendar)
		//   -->
		//   The Unix Time Number
		Calendar calUserTz = Calendar.getInstance(TimeZone.getTimeZone(timeZoneID));
		calUserTz.set(calLocalTz.get(Calendar.YEAR), calLocalTz.get(Calendar.MONTH), 
				calLocalTz.get(Calendar.DAY_OF_MONTH), calLocalTz.get(Calendar.HOUR_OF_DAY), 
				calLocalTz.get(Calendar.MINUTE), calLocalTz.get(Calendar.SECOND));
		calUserTz.set(Calendar.MILLISECOND, calLocalTz.get(Calendar.MILLISECOND));	
		
		return calUserTz.getTimeInMillis();
	}
}
