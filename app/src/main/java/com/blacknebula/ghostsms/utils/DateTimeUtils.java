package com.blacknebula.ghostsms.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Util class used to manipulate date and time
 *
 * @author ayassinov
 */
public class DateTimeUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DEFAULT_LOCALE = "FR-fr";


    /**
     * Get current date with seconds and milliseconds,
     *
     * @return a current date time object with seconds and milliseconds
     */
    public static Date currentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * Get current date without seconds and milliseconds,
     * used when comparing date and ignoring seconds and milliseconds
     *
     * @return a current date time object without seconds and milliseconds
     */
    public static Date currentDateWithoutSeconds() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get a current time
     *
     * @return an instance of Date reprenting the current date time on the system timezone
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }


    public static Date getDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * truncate a given date and remove time information
     *
     * @param date the date that will be truncated
     * @return an instance of date without time in the system timezone
     */
    public static Date getDateWithoutTime(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * A minimum date is a date at 01/01/1970 00:00. We can use it to avoid testing with null
     *
     * @return a minimum date.
     */
    public static Date getMinDate() {
        return new Date(0L);
    }


    /**
     * Get the hour from a date
     *
     * @param now an instance of date
     * @return an number representing the hour of a date
     */
    public static int getHours(Date now) {
        return getCalendar(now).get(Calendar.HOUR_OF_DAY);
    }

    /**
     * return a future date on the system time zone with the number of defined hours
     *
     * @param hours number of hours to add
     * @return the future date without seconds and milliseconds
     */
    public static Date addHoursFromNow(int hours) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Set a minutes to a date
     *
     * @param date    an instance of a date
     * @param minutes number of minutes to set
     * @return an instance of a date with the minutes added
     */
    public static Date addMinutes(Date date, int minutes) {
        final Calendar c = getCalendar(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /**
     * Set a seconds to a date
     *
     * @param date    an instance of a date
     * @param seconds number of seconds to set
     * @return an instance of a date with the seconds added
     */
    public static Date addSeconds(Date date, int seconds) {
        final Calendar c = getCalendar(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    /**
     * Set minutes to a current date/time
     *
     * @param minutes number of seconds to set
     * @return an instance of a date with the minutes added
     */
    public static Date addMinutesFromNow(int minutes) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /**
     * Set a seconds to a current date/time
     *
     * @param seconds number of seconds to set
     * @return an instance of a date with the seconds added
     */
    public static Date addSecondsFromNow(int seconds) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    public static Date addDaysFromNow(int days) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    public static Date addMonthsNow(int months) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    public static Date addMonths(Date date, int months) {
        final Calendar c = getCalendar(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    /**
     * Set days to a date
     *
     * @param date an instance of a date
     * @param days number of days to set
     * @return an instance of a date with the days added
     */
    public static Date addDays(Date date, int days) {
        final Calendar c = getCalendar(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    public static Integer getYear(Date date) {
        final Calendar cal = getCalendar(date);
        return cal.get(Calendar.YEAR);
    }

    public static Integer getMonth(Date date) {
        final Calendar cal = getCalendar(date);
        return cal.get(Calendar.MONTH);

    }

    public static Integer getDayOfMonth(Date date) {
        final Calendar cal = getCalendar(date);
        return cal.get(Calendar.DAY_OF_MONTH);

    }

    public static Date getDate(int year, int month, int day) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }


    /**
     * Set a date to a Calender instance and return it
     *
     * @param date the date to set on a calender
     * @return an instance of a calender with the given date
     */
    private static Calendar getCalendar(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


}
