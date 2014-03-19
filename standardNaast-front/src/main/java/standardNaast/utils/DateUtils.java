/* Copyright 2011 BuyWay-Services */
package standardNaast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import standardNaast.constants.DateFormat;
import standardNaast.constants.DefaultValues;
import standardNaast.exception.TechnicalException;

/**
 * Description : Date utilities.
 *
 * @author BuyWay-Services: DWW<BR> Created on 4 oct. 2011
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * Format a date according to the date format and a language.
     *
     * @param date the date to format
     * @param format the {@link #DateFormat}
     * @param language the language
     * @return the formatted date
     */
    public static final String formatDate(final Date date, final String format) {
        return date != null ? new SimpleDateFormat(format, new Locale("fr")).format(date)
                : DefaultValues.Literals.NOT_AVAILABLE;
    }

    /**
     * Format a date using the business pattern dd/mm/yyyy.
     *
     * @param date the date to format
     * @return the formatted date
     */
    public static final String formatDate(final Date date) {
        return DateUtils.formatDate(date, DateFormat.DDSMMSYYYY);
    }

    /**
     * Parses a {@link Date} in string according to the date format.
     *
     * @param date the string to parse.
     * @param format the {@link #DateFormat}
     * @return a Date or null if the date string is empty
     */
    public static final Date parseDate(final String date, final String format) {
        Date parsedDate = null;

        if (StringUtils.isNotBlank(date)) {
            try {
                if (format == null) {
                    throw new ParseException(StringUtils.EMPTY, -1);
                }

                parsedDate = new SimpleDateFormat(format).parse(date);
            } catch (ParseException e) {
                throw new TechnicalException("Invalid date [" + date + "] or format [" + format + "].", e);
            }
        }

        return parsedDate;
    }

    /**
     * Parses a {@link DateTime} in string according to the date format.
     *
     * @param dateTime the string to parse.
     * @param format the {@link #DateFormat}
     * @return a DateTime.
     */
    public static final DateTime parseDateTime(final String dateTime, final String format) {
        try {
            return DateTime.parse(dateTime, DateTimeFormat.forPattern(format));
        } catch (RuntimeException e) { // TechnicalException is also a runtime but it logs !
            throw new TechnicalException("Invalid datetime [" + dateTime + "] or format [" + format + "].", e);
        }
    }

    /**
     * Add a given number of days to a given date.
     *
     * @param date the date to change.
     * @param numberOfDays the number of days to add
     * @return the new date
     */
    public static final Date addDays(final Date date, final int numberOfDays) {
        return new LocalDate(date).plusDays(numberOfDays).toDate();
    }

    /**
     * Checks if a given date is older than a limit.
     *
     * @param date the date to check.
     * @param age the limit.
     * @return true if the age is more than or equal to the given date.
     */
    public static final boolean isOlderThan(final Date date, final int age) {
        return Years.yearsBetween(new DateTime(date), DateTime.now()).getYears() > age;
    }

    /**
     * @param start Beginning of the slice
     * @param end Ending of the slice
     * @param yearsOnly Flag specifiying the computing method, using years only
     * or not
     * @return Computed value
     */
    public static int computeTimeSlice(final Date start, final Date end, final boolean yearsOnly) {
        int result = -1;
        if (start != null) {
            // Date value
            DateTime startTime = new DateTime(start);
            // Date of the day
            DateTime endTime = new DateTime(end);

            if (yearsOnly) {
                result = endTime.getYear() - startTime.getYear();
            } else {
                // Number of years in date value
                Period valPeriod = new Period(startTime, endTime);
                // Age
                result = valPeriod.getYears();
            }
        }
        return result;
    }

    /**
     * Checks if a date is today.
     *
     * @param date the date
     * @return true if the date is today
     */
    public static boolean isToday(final Date date) {
        return new LocalDate(date).isEqual(new LocalDate());
    }

    /**
     * Checks if a calendar date is today.
     *
     * @param cal the calendar
     * @return true if cal date is today
     */
    public static boolean isToday(final Calendar cal) {
        return new LocalDate(cal).isEqual(new LocalDate());
    }

    /**
     * Test if the given date is within the range. Equal to the start or end is
     * considered within as well.
     *
     * @param testDate the date to test
     * @param startDate the start date
     * @param endDate the end date
     * @return true if within, false otherwise
     */
    public static boolean isWithinRange(final Date testDate, final Date startDate, final Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    /**
     * @param date The date to truncate
     * @return The date truncated to DDMMYYYY
     */
    public static Date toDateMidnight(final Date date) {
        return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
    }

    /**
     * @param birthdate The date for which we want the number of years compared
     * to now.
     * @return The number of years.
     */
    public static int getYearsBetween(final Date birthdate) {
        if (birthdate == null) {
            throw new TechnicalException("We don't accept null here!");
        }

        Date birthdateMidnight = DateUtils.toDateMidnight(birthdate);
        DateTime birthdateTime = new DateTime(birthdateMidnight.getTime());
        DateTime now = new DateTime();
        Years age = Years.yearsBetween(birthdateTime, now);
        return age.getYears();
    }
}
