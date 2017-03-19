package org.refcounter.web;

import java.util.List;

/**
 * A Factory class for getting various implementations of the checker interface.
 * 
 * @author Dániel Péter Kun
 *
 */

public class CheckerFactory {

	/**
	 * The method for the instantiation of the specified checker.
	 * 
	 * @param type
	 *            The type of the needed checker
	 * @param ISSNs
	 *            The List of ISSN numbers for the constructor of the checker
	 * @return a checker implementation.
	 */
	public static Checker getChecker(CheckerType type, List<String> ISSNs) {
		if (type == null) {
			return null;
		}

		if (type.equals(CheckerType.ENTRY)) {
			return new MtmtEntriesChecker(ISSNs);
		} else if (type.equals(CheckerType.SHORTNAME)) {
			return new MtmtShortNameChecker(ISSNs);
		} else if (type.equals(CheckerType.NAME)) {
			return new MtmtNameChecker(ISSNs);
		}

		return null;

	}
}
