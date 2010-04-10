package edu.bsu.cs.jive.util;

import java.util.Iterator;

/**
 * Processes collections and provides stringified versions of them.
 *
 * @author pvg
 */
public class StringSeparator {

	private static final String DEFAULT_SEPARATOR = ", ";
	
	/**
	 * Return a string version of the collection using comma-separation.
	 * @param i an iterable collection
	 * @return string version of the list
	 */
	public static String toString(Iterable<?> i) {
		return toString(i,DEFAULT_SEPARATOR);
	}
	
	/**
	 * Return a string version of the list the specified separator.
	 * @param i an iterable collection
	 * @param separator
	 * @return string version of the list
	 */
	public static String toString(Iterable<?> i, String separator) {
		StringBuffer sb = new StringBuffer();
		Iterator<?> iter = i.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().toString());
			if (iter.hasNext())
				sb.append(separator);
		}
		return sb.toString();
	}
	
}
