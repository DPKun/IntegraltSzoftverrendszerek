package org.refcounter.web;

import java.util.List;

import org.jsoup.nodes.Document;

/**
 * The interface of the information checkers found in the software.
 * A checker loads a webpage and tries to find a specific information on it.
 * @author Dániel Péter Kun
 *
 */
public interface Checker{

	/**
	 * A method that returns the type of the checker implementation as a value of the {@code CheckerType} enum.
	 * @return A value of {@code CheckerType}
	 */
	public CheckerType getType();
	
	/**
	 * A method that returns the results found by the checker
	 * @return The found entries as a list of Strings.
	 */
	public List<String> getResults();
	
	/**
	 * The method which gets the needed information from the webpage
	 * @param doc The JSoup document on which the method tries to find the requested value.
	 */
	public void getInfo(Document doc);
}
