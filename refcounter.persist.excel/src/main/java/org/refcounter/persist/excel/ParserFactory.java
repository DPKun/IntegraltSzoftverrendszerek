package org.refcounter.persist.excel;

/**
 * The factory of the excel parser implementations.
 * @author Dániel Péter Kun
 *
 */
public class ParserFactory {

	/**
	 * The factory method for creating the appropiate type of parser
	 * @param Extension The extension of the input file
	 * @return A parser implementation for the given file extension.
	 */
	public static Parser getParser(String Extension) {
		if (Extension == null) {
			return null;
		}

		if (Extension.equalsIgnoreCase("xlsx")) {
			return new XlsxParser();
		} else if (Extension.equalsIgnoreCase("xls")) {
			return new XlsParser();
		}
		return null;

	}

}
