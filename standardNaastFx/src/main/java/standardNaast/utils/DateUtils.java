/* Copyright 2011 BuyWay-Services */
package standardNaast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import standardNaast.constants.DateFormat;
import standardNaast.constants.DefaultValues;
import standardNaast.exception.TechnicalException;

/**
 * Description : Date utilities.
 *
 * @author BuyWay-Services: DWW<BR>
 *         Created on 4 oct. 2011
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	/**
	 * Format a date according to the date format and a language.
	 *
	 * @param date
	 *            the date to format
	 * @param format
	 *            the {@link #DateFormat}
	 * @param language
	 *            the language
	 * @return the formatted date
	 */
	public static final String formatDate(final Date date, final String format) {
		return date != null ? new SimpleDateFormat(format, new Locale("fr")).format(date)
				: DefaultValues.Literals.NOT_AVAILABLE;
	}

	/**
	 * Format a date using the business pattern dd/mm/yyyy.
	 *
	 * @param date
	 *            the date to format
	 * @return the formatted date
	 */
	public static final String formatDate(final Date date) {
		return DateUtils.formatDate(date, DateFormat.DDSMMSYYYY);
	}

	/**
	 * Parses a {@link Date} in string according to the date format.
	 *
	 * @param date
	 *            the string to parse.
	 * @param format
	 *            the {@link #DateFormat}
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
			} catch (final ParseException e) {
				throw new TechnicalException("Invalid date [" + date + "] or format [" + format + "].", e);
			}
		}

		return parsedDate;
	}

	/**
	 * Parses a {@link DateTime} in string according to the date format.
	 *
	 * @param dateTime
	 *            the string to parse.
	 * @param format
	 *            the {@link #DateFormat}
	 * @return a DateTime.
	 */
	public static final LocalDateTime parseDateTime(final String dateTime, final String format) {
		try {
			return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
		} catch (final RuntimeException e) { // TechnicalException is also a
												// runtime but it logs !
			throw new TechnicalException("Invalid datetime [" + dateTime + "] or format [" + format + "].", e);
		}
	}

	/**
	 * Test if the given date is within the range. Equal to the start or end is
	 * considered within as well.
	 *
	 * @param testDate
	 *            the date to test
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return true if within, false otherwise
	 */
	public static boolean isWithinRange(final Date testDate, final Date startDate, final Date endDate) {
		return !(testDate.before(startDate) || testDate.after(endDate));
	}

	/**
	 * @param date
	 *            The date to truncate
	 * @return The date truncated to DDMMYYYY
	 */
	public static Date toDateMidnight(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
	}

	public static LocalDate toLocalDate(final Date date) {
		if (date != null) {
			return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
		} else {
			return null;
		}
	}

	public static Date toDate(final LocalDate localDate) {
		if (localDate != null) {
			final Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
			return Date.from(instant);
		} else {
			return null;
		}
	}

}
