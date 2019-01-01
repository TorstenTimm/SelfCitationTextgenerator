/**
 * Copyright (C) modular design GmbH.
 * All rights reserved.
 */
package de.voynich.text.util;

import de.voynich.text.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * helper class providing methods for manipulating strings
 */
public final class StringUtil {

	/**
	 * Default constructor (empty), private because this class has only
	 * static methods.
	 */
	private StringUtil() {
		// nope
	}

	/**
	 * check if a string contains a number
	 * @param value
	 * @return
	 */
	public static boolean isParsableAsNumber(final String value) {
		return validateRegex(value, "[+-]?[0-9]+", false);
	}

	/**
	 * formats the actual date as a string
	 * @param saveDatePattern the pattern to format the date
	 * @return the formatted date as a string
	 */
	public static String dateAsString(String saveDatePattern) {
		DateFormat df;
		try {
			df = new SimpleDateFormat(saveDatePattern);
		} catch (IllegalArgumentException e) {
			df = new SimpleDateFormat(Constants.SAVE_DATE_PATTERN_DEFAULT);
		}
		df.setLenient(false);

		return df.format(new Date());
	}

	/**
	 * Substitutes <code>searchString</code> in the given source String with <code>replaceString</code>.
	 *
	 * This is a high-performance implementation which should be used as a replacement for
	 * <code>{@link String#replaceAll(String, String)}</code> in case no
	 * regular expression evaluation is required.
	 *
	 * @param source the content which is scanned
	 * @param searchString the String which is searched in content
	 * @param replaceString the String which replaces <code>searchString</code>
	 *
	 * @return the substituted String
	 */
	public static String substitute(final String source, final String searchString, String replaceString) {
		if (source == null) {
			return null;
		}

		if (isEmpty(searchString)) {
			return source;
		}

		if (replaceString == null) {
			replaceString = "";
		}

		int len = source.length();
		int sl = searchString.length();
		int rl = replaceString.length();
		int length;
		if (sl == rl) {
			length = len;
		} else {
			int c = 0;
			int s = 0;
			int e;
			while ((e = source.indexOf(searchString, s)) != -1) {
				c++;
				s = e + sl;
			}
			if (c == 0) {
				return source;
			}
			length = len - (c * (sl - rl));
		}

		int s = 0;
		int e = source.indexOf(searchString, s);
		if (e == -1) {
			return source;
		}
		StringBuilder sb = new StringBuilder(length);
		while (e != -1) {
			sb.append(source.substring(s, e));
			sb.append(replaceString);
			s = e + sl;
			e = source.indexOf(searchString, s);
		}
		e = len;
		sb.append(source.substring(s, e));
		return sb.toString();
	}

	/**
	 * Returns <code>true</code> if the provided String is either <code>null</code>
	 * or the empty String <code>""</code>.
	 *
	 * @param value the value to check
	 *
	 * @return true, if the provided value is null or the empty String, false otherwise
	 */
	public static boolean isEmpty(final String value) {

		return value == null || value.isEmpty();
	}

	/**
	 * Returns <code>true</code> if the provided String is either <code>null</code>
	 * or contains only white spaces.
	 *
	 * @param value the value to check
	 *
	 * @return true, if the provided value is null or contains only white spaces, false otherwise
	 */
	public static boolean isEmptyOrWhitespaceOnly(final String value) {
		return isEmpty(value) || (value.trim().length() == 0);
	}

	/**
	 * Validates a value against a regular expression.
	 *
	 * @param value the value to vms
	 * @param regex the regular expression
	 * @param allowEmpty if an empty value is allowed
	 *
	 * @return <code>true</code> if the value satisfies the validation
	 */
	public static boolean validateRegex(final String value, final String regex, final boolean allowEmpty) {

		if (isEmptyOrWhitespaceOnly(value)) {
			return allowEmpty;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	/**
	 * Splits a String into substrings along the provided char delimiter and returns
	 * the result as an Array of Substrings.
	 *
	 * @param source the String to split
	 * @param delimiter the delimiter to split at
	 *
	 * @return the Array of splitted Substrings
	 */
	public static String[] splitAsArray(final String source, final String delimiter) {

		List<String> result;
		if (source.contains(delimiter)) {
			result = splitAsList(source, delimiter);
		} else {
			result = new ArrayList<>();
			result.add(source);
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Splits a String into substrings along the provided char delimiter and returns
	 * the result as a List of Substrings.
	 *
	 * @param source the String to split
	 * @param delimiter the delimiter to split at
	 *
	 * @return the List of splitted Substrings
	 */
	public static List<String> splitAsList(final String source, final String delimiter) {

		return splitAsList(source, delimiter, false /* trim */, true /* zeroTokens */);
	}

	/**
	 * Splits a String into substrings along the provided char delimiter and returns
	 * the result as a List of Substrings.
	 *
	 * @param source the String to split
	 * @param delimiter the delimiter to split at
	 * @param trim flag to indicate if leading and trailing white spaces should be omitted
	 *
	 * @return the List of splitted Substrings
	 */
	public static List<String> splitAsList(final String source, final String delimiter, final boolean trim, final boolean zeroTokens) {

		List<String> result = new ArrayList<String>();
		int i = 0;
		int l = source.length();
		int n = source.indexOf(delimiter);
		if (n < 0)
			n = l;
		while (i <= l) {
			// zero - length items are not seen as tokens at start or end
			if (zeroTokens || (i < n) || ((i > 0) && (i < l))) {
				result.add(trim ? source.substring(i, n).trim() : source.substring(i, n));
			}
			i = n + 1;
			n = source.indexOf(delimiter, i);
			if (n < 0)
				n = l;
		}

		return result;
	}
}