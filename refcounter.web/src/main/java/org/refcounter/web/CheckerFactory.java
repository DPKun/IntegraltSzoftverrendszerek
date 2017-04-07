package org.refcounter.web;

public class CheckerFactory {

	/**
	 * The method for the instantiation of the specified checker.
	 * @param type
	 *            The type of the needed checker
	 * @return a checker implementation.
	 */
	public static Checker getChecker(CheckerType type) {
		if (type == null) {
			return null;
		}

		if (type.equals(CheckerType.ENTRY)) {
			return new MtmtEntriesChecker();
		} else if (type.equals(CheckerType.SHORTNAME)) {
			return new MtmtShortNameChecker();
		} else if (type.equals(CheckerType.NAME)) {
			return new MtmtNameChecker();
		}

		return null;

	}
	
}
