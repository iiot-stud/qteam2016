package org.qTeam.api.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

/**
 * Created by robynml 22 Jan 2016
 */
public class TimeHelperMethods {

	public static boolean dateNotInPast(Date time) {
		Date present = new Date();

		if (date1SameOrAfterDate2(time, present)) {
			return true;
		}

		return false;
	}

	public static String getStringFromTime(Date time) {

		String dateString = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		dateString = simpleDateFormat.format(time);

		return dateString;
	}

	public static Date getDateFromXSD(XSDDateTime time) {
		return time.asCalendar().getTime();
	}

	public static String getStringFromTime(XSDDateTime dateTime) {

		Date time = getDateFromXSD(dateTime);

		return getStringFromTime(time);
	}

	public static Date getTimeFromString(String time)
			throws TimeParsingException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;

		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			throw new TimeParsingException(
					"Something went wrong trying to parse the reservation time.",
					e);
		}

		return date;
	}

	public static boolean date1SameOrAfterDate2(Date date1, Date date2) {

//		if ((date1.getTime() >= date2.getTime())) {
//			return true;
//		} else {
//			return false;
//		}
		return true;
	}

	public static boolean timesOverlap(String start1, String end1,
			String start2, String end2) throws TimeParsingException {

		Date start1Date = getTimeFromString(start1);
		Date end1Date = getTimeFromString(end1);
		Date start2Date = getTimeFromString(start2);
		Date end2Date = getTimeFromString(end2);

		return timesOverlap(start1Date, end1Date, start2Date, end2Date);

	}

	public static boolean timesOverlap(Date start1, Date end1, Date start2,
			Date end2) {

		Boolean timesOverlap = false;

		if (date1SameOrAfterDate2(start2, start1)) {
			if (date1SameOrAfterDate2(end1, start2)) {
				timesOverlap = true;
			} else {
				timesOverlap = false;
			}

		} else {
			if (date1SameOrAfterDate2(end2, start1)) {
				timesOverlap = true;
			} else {
				timesOverlap = false;
			}
		}

		return timesOverlap;
	}

}
