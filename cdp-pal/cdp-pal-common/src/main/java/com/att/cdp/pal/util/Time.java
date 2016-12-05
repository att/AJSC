/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a general purpose helper class to augment standard Java time support.
 *
 * @since Sep 28, 2012
 * @version $Id$
 */

public final class Time {

    private static SimpleDateFormat dateformatter = null;

    private static final Logger LOG = LoggerFactory.getLogger(Time.class);

    @SuppressWarnings("nls")
    private static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");

    /**
     * The cached reference to the datatype factory
     */
    private static DatatypeFactory xmlDatatypeFactory = null;

    /**
     * Increments a date by the indicated months, days, hours, minutes, and seconds, and returns the updated date.
     *
     * @param date
     *            The date to be manipulated
     * @param months
     *            The number of months to be added to the date
     * @param days
     *            The number of days to be added to the date
     * @param hours
     *            The number of hours to be added to the date
     * @param minutes
     *            The number of minutes to be added to the date
     * @param seconds
     *            The number of seconds to be added to the date
     * @return The updated date.
     */
    public static Date addTime(final Date date, final int months, final int days, final int hours, final int minutes,
        final int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    /**
     * Clears the time components of a calendar to zero, leaving the date components unchanged.
     *
     * @param cal
     *            the calendar to be updated
     * @return The updated calendar object
     */
    public static Calendar dateOnly(final Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * This method returns the local time that corresponds to the end of the current day
     *
     * @return The time that corresponds to the end of the current day, expressed as local time
     */
    public static Date endOfDayLocal() {
        return endOfDayLocal(new Date());
    }

    /**
     * This method returns the last moment of the day for the supplied local time. This is defined as the millisecond
     * before midnight of the current date represented by the local time.
     *
     * @param localTime
     *            The local time for which the last moment of the day is desired.
     * @return The millisecond prior to midnight, local time.
     */
    public static Date endOfDayLocal(final Date localTime) {
        // @sonar:off
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(localTime);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        // @sonar:on

        return calendar.getTime();
    }

    /**
     * The end of the current day and in the current time zone expressed as a UTC time.
     *
     * @return The UTC time that corresponds to the end of the current day
     */
    public static Date endOfDayUTC() {
        return endOfDayUTC(new Date());
    }

    /**
     * Returns the UTC time that corresponds to the end of the day for the local time specified, using the current
     * (default) time zone.
     *
     * @param localTime
     *            The local time for which we are requesting the UTC time that corresponds to the end of the day
     * @return The UTC time that corresponds to the end of the local day specified by the local time.
     */
    public static Date endOfDayUTC(final Date localTime) {
        return endOfDayUTC(localTime, TimeZone.getDefault());
    }

    /**
     * Returns the time expressed in UTC time of the end of the day specified in local time and within the local time
     * zone.
     *
     * @param localTime
     *            The local time for which we will compute the end of the local day, and then convert to UTC time.
     * @param localTimeZone
     *            The time zone that the local time is within.
     * @return The UTC date that corresponds to the end of the day local time and in the local time zone.
     */
    public static Date endOfDayUTC(final Date localTime, final TimeZone localTimeZone) {
        Date endOfDay = endOfDayLocal(localTime);
        return utcDate(endOfDay, localTimeZone);
    }

    /**
     * returns current Date in 'UTC' Timezone
     *
     * @return The current date, expressed in the UTC timezone.
     */
    @SuppressWarnings("nls")
    public static Date getCurrentUTCDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(utcTime());
        return calendar.getTime();
    }

    /**
     * This method loads and caches the reference to the XML data type factory object.
     *
     * @return The XML Data Type Factory object
     */
    public static DatatypeFactory getDatatypeFactory() {
        if (xmlDatatypeFactory == null) {
            try {
                xmlDatatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }
        return xmlDatatypeFactory;
    }

    /**
     * Gives the date-time String based on given Locale and Timezone
     *
     * @param date
     *            The date to be formatted
     * @param locale
     *            The locale that we want to format the value for
     * @param timezone
     *            The time zone that the date is within
     * @return The formatted value
     */
    public static String getDateByLocaleAndTimeZone(final Date date, final Locale locale, final TimeZone timezone) {
        String strDate = null;
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        df.setTimeZone(timezone);
        synchronized (df) {
            strDate = df.format(date);
        }
        return strDate;
    }

    /**
     * Returns singleton UTC date formatter.
     *
     * @return The date formatter for UTC dates and times
     */
    @SuppressWarnings("nls")
    private static SimpleDateFormat getDateFormatter() {
        if (dateformatter == null) {
            dateformatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            dateformatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        }
        return dateformatter;
    }

    /**
     * This method returns the local time that corresponds to a given UTC time in the current time zone.
     *
     * @param utcTime
     *            The UTC time for which we desire the equivalent local time in the current time zone.
     * @return The local time that is equivalent to the given UTC time for the current time zone
     */
    public static long localTime(final long utcTime) {
        return localTime(utcTime, TimeZone.getDefault());
    }

    /**
     * This method can be used to get the local time that corresponds to a specific UTC time.
     * <p>
     * This method has a problem since the offset can only be determined by having a local time. So, we take the UTC
     * time and add the raw offset to it to come up with an approximation of the local time. This gives us a local time
     * that we can use to determine what the offset should be, which is what we actually add to the UTC time to get the
     * local time.
     * </p>
     *
     * @param utcTime
     *            The UTC time for which we want to obtain the equivalent local time
     * @param localTZ
     *            The time zone that we want the local time to be within
     * @return The local time for the specified time zone and the given UTC time
     */
    public static long localTime(final long utcTime, final TimeZone localTZ) {
        int offset = localTZ.getOffset(utcTime + localTZ.getRawOffset());
        long result = utcTime + offset;

        return result;
    }

    /**
     * Sets the date components of a calendar to the specified values, leaving the time components unchanged.
     *
     * @param cal
     *            The calendar to be updated
     * @param year
     *            The year to be set
     * @param month
     *            The month to be set
     * @param day
     *            The day to be set
     * @return The updated calendar object
     */
    public static Calendar setDate(final Calendar cal, final int year, final int month, final int day) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    /**
     * Returns the start of the day expressed in local time for the current local time.
     *
     * @return The start of the day
     */
    public static Date startOfDayLocal() {
        return startOfDayLocal(new Date());
    }

    /**
     * This method returns the date that corresponds to the start of the day local time. The date returned represents
     * midnight of the previous day represented in local time. If the UTC time is desired, use the methods
     * {@link #startOfDayUTC(Date, TimeZone)}, {@link #startOfDayUTC(Date)}, or {@link #startOfDayUTC()}
     *
     * @param localTime
     *            The local date that we wish to compute the start of day for.
     * @return The date that corresponds to the start of the local day
     */
    public static Date startOfDayLocal(final Date localTime) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(localTime);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * This method returns the UTC date that corresponds to the start of the local day based on the current time and the
     * default time zone (the time zone we are running in).
     *
     * @return The start of the local day expressed as a UTC time.
     */
    public static Date startOfDayUTC() {
        return startOfDayUTC(new Date());
    }

    /**
     * This method returns the UTC date that corresponds to the start of the local day specified in the current time
     * zone.
     *
     * @param localTime
     *            The local time to be used to compute the start of the day
     * @return The start of the local day expressed as a UTC time.
     */
    public static Date startOfDayUTC(final Date localTime) {
        return startOfDayUTC(localTime, TimeZone.getDefault());
    }

    /**
     * This method returns the UTC date that corresponds to the start of the local day specified in the local timezone.
     *
     * @param localTime
     *            The local time to be used to compute start of day
     * @param localTimeZone
     *            The time zone that the local time was recorded within
     * @return The corresponding UTC date
     */
    public static Date startOfDayUTC(final Date localTime, final TimeZone localTimeZone) {
        Date startOfDay = startOfDayLocal(localTime);
        return utcDate(startOfDay, localTimeZone);
    }

    /**
     * This method creates and returns an XML timestamp expressed as the current UTC value for the system. The caller
     * does not specify the time value or time zone using this method. This ensures that the timestamp value is always
     * expressed as UTC time.
     *
     * @return The XMLGregorianCalendar that can be used to record the timestamp
     */

    public static XMLGregorianCalendar timestamp() {
        getDatatypeFactory();
        XMLGregorianCalendar ts = xmlDatatypeFactory.newXMLGregorianCalendar();
        GregorianCalendar utc = new GregorianCalendar();
        utc.setTime(utcDate());
        ts.setTimezone(0);
        ts.setYear(utc.get(Calendar.YEAR));
        // Calendar Months are from 0-11 need to +1
        ts.setMonth(utc.get(Calendar.MONTH) + 1);
        ts.setDay(utc.get(Calendar.DAY_OF_MONTH));
        ts.setHour(utc.get(Calendar.HOUR_OF_DAY));
        ts.setMinute(utc.get(Calendar.MINUTE));
        ts.setSecond(utc.get(Calendar.SECOND));
        ts.setMillisecond(utc.get(Calendar.MILLISECOND));
        return ts;
    }

    /**
     * Converts XMLGregorianCalendar to java.util.Date in Java
     *
     * @param calendar
     *            the calendar object to be converted
     * @return The equivalent Date object
     */
    public static Date toDate(final XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

    /**
     * Converts java Date to XMLGregorianCalendar.
     *
     * @param date
     *            The date to convert
     * @return The XMLGregorianCalendar for the specified date
     */
    @SuppressWarnings("nls")
    public static XMLGregorianCalendar toXMLCalendar(final Date date) {
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
        cal.setTime(date);

        XMLGregorianCalendar xmlCal = null;
        try {
            xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOG.error("toXMLCalendar", e);
        }
        return xmlCal;
    }

    /**
     * Truncates the provided date so that only the date, hours, and minutes portions are significant. This method
     * returns the date with the seconds and milliseconds forced to zero.
     *
     * @param date
     *            The date to truncate
     * @return The date with only the year, month, day, hours, and minutes significant.
     */
    public static Date truncDate(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * The UTC date that corresponds to the current date in the local time zone.
     *
     * @return The UTC date for now.
     */
    public static Date utcDate() {
        return utcDate(new Date(), TimeZone.getDefault());
        // return new Date();
    }

    /**
     * The UTC date for the specified date in the current (default) time zone.
     *
     * @param date
     *            The local date for which the UTC date is desired.
     * @return The UTC date that corresponds to the date in the current time zone.
     */
    public static Date utcDate(final Date date) {
        TimeZone tz = TimeZone.getDefault();
        return utcDate(date, tz);
    }

    /**
     * Returns the UTC date for the specified date in the specified time zone.
     *
     * @param date
     *            The date for which the UTC date is desired in the specified zone
     * @param tz
     *            The time zone that corresponds to the date to be converted to UTC
     * @return The UTC date that corresponds to the local date in the local time zone.
     */
    public static Date utcDate(final Date date, final TimeZone tz) {
        return new Date(utcTime(date.getTime(), tz));
    }

    /**
     * Format incoming date as string in GMT or UTC.
     *
     * @param dt
     *            The source date to be formatted
     * @return The formatted string equivalent of the date
     */
    public static String utcFormat(final Date dt) {
        String strDate = null;
        DateFormat df = getDateFormatter();
        synchronized (df) {
            strDate = df.format(dt);
        }
        return strDate;
    }

    /**
     * Parse previously formated Date object back to a Date object.
     *
     * @param dateStr
     *            The representation of a UTC date as a string
     * @return The date object containing the parsed representation, or null if the representation cannot be parsed
     */
    @SuppressWarnings("nls")
    public static Date utcParse(final String dateStr) {
        String[] adtl = {
            "yyyy-MM-dd"
        };
        return utcParse(dateStr, adtl);
    }

    /**
     * Parse previously formated Date object back to a Date object.
     *
     * @param dateStr
     *            The representation of a UTC date as a string
     * @param adtlFormatStrings
     *            A list of strings that represent additional date format representations to try and parse.
     * @return The date object containing the parsed representation, or null if the representation cannot be parsed
     */
    @SuppressWarnings("nls")
    public static Date utcParse(final String dateStr, String... adtlFormatStrings) {
        if (dateStr != null) {
            // Build the list of formatters starting with the default defined in the class
            List<DateFormat> formats = new ArrayList<>();
            formats.add(getDateFormatter());

            if (adtlFormatStrings != null) {
                for (String s : adtlFormatStrings) {
                    formats.add(new SimpleDateFormat(s));
                }
            }

            // Return the first matching date formatter's result
            for (DateFormat df : formats) {
                df.setTimeZone(utcTZ);
                try {
                    return df.parse(dateStr);
                } catch (ParseException e) {
                    LOG.debug(String.format("IGNORE - Date string [%s] does not fit pattern [%s]", dateStr,
                        df.toString()));
                }
            }
        }
        return null;
    }

    /**
     * This method returns the current time for the UTC timezone
     *
     * @return The time in the UTC time zone that corresponds to the current local time.
     */
    public static long utcTime() {
        return utcTime(new Date().getTime());
    }

    /**
     * Get the UTC time that corresponds to the given time in the default time zone (current time zone for the system).
     *
     * @param localTime
     *            The time in the current time zone for which the UTC time is desired.
     * @return The UTC time
     */
    public static long utcTime(final long localTime) {
        TimeZone tz = TimeZone.getDefault();
        return utcTime(localTime, tz);
    }

    /**
     * Get the UTC time that corresponds to the given time in the specified timezone.
     * <p>
     * Note that the java <code>getOffset()</code> method works a little counter-intuitive. It returns the offset that
     * would be added to the current UTC time to get the LOCAL time represented by the local time zone. That means to
     * get the UTC time, we need to SUBTRACT this offset from the local time.
     * </p>
     *
     * @param localTime
     *            The time in the specified time zone for which the UTC time is desired.
     * @param localTZ
     *            The time zone which the local time is in.
     * @return The UTC time for the specified local time in the specified local time zone.
     */
    public static long utcTime(final long localTime, final TimeZone localTZ) {
        int offset = localTZ.getOffset(localTime);
        return localTime - offset;

    }

    /**
     * Creates a timestamp value from a time
     *
     * @param utcTime
     *            The UTC time to convert to a timestamp
     * @return The timestamp
     */
    public static Timestamp utcTimestamp(final long utcTime) {
        TimeZone tz = TimeZone.getDefault();
        return new Timestamp(utcTime(utcTime, tz));
    }

    private Time() {
        //
    }
}
