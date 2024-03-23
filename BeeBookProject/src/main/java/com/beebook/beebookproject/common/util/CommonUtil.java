package com.beebook.beebookproject.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * This class is used to implement common functions for project
 * 
 * @author Red Devil
 * @since 2020-10-10
 */
public class CommonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 * Convert date to string with input format
	 * 
	 * @param inputDate
	 * @param dateFormat
	 * @return outputDateStr
	 */
	public static String cvDateToString(Date inputDate, String dateFormat) {

		String outputDate = StringUtils.EMPTY;
		if (inputDate != null) {
			SimpleDateFormat dateFormatOutput = new SimpleDateFormat(dateFormat);
			outputDate = dateFormatOutput.format(inputDate);
		}
		return outputDate;
	}

	/**
	 * Convert date to string with input format and locale
	 * 
	 * @param inputDate
	 * @param dateFormat
	 * @return outputDateStr
	 */
	public static String cvDateToString(Date inputDate, String dateFormat, Locale locale) {

		String outputDate = StringUtils.EMPTY;
		if (inputDate != null) {
			SimpleDateFormat dateFormatOutput = new SimpleDateFormat(dateFormat, locale);
			outputDate = dateFormatOutput.format(inputDate);
		}
		return outputDate;
	}

	/**
	 * Convert string to date
	 * 
	 * @param dateStr
	 * @param dateFormat
	 * @return Date
	 */
	public static Date cvStringToDate(String dateStr, String dateFormat) {

		Date outputDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		try {
			outputDate = simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			LOGGER.error("Error when converting data: {}", e.getMessage());
		}
		return outputDate;
	}

	/**
	 * Read Properties with key and file name
	 * 
	 * @param key
	 * @param fileName
	 * @return value
	 */
	public static String readProperties(String key, String fileName) {

		InputStream inputStream = CommonUtil.class.getClassLoader().getResourceAsStream(fileName);
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
			return prop.getProperty(key);
		} catch (IOException e) {
			LOGGER.error("Error when reading property from file {}: {}", fileName, e.getMessage());
		}
		return null;
	}
}