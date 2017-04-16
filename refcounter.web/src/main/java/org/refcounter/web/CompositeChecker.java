package org.refcounter.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * A Composite class for getting various implementations of the checker interface to work with the same connection.
 * 
 * @author Dániel Péter Kun
 *
 */

public class CompositeChecker implements Runnable, Checker{
	private PageAddress webpage;
	
	private List<Checker> checkers = new ArrayList<Checker>();
	
	private List<String> ISSNs;
	
	public CompositeChecker(List<String> issns,List<CheckerType> checkers,PageAddress webpage){
		this.webpage=webpage;
		this.ISSNs=issns;
		for(CheckerType c : checkers){
			this.checkers.add(CheckerFactory.getChecker(c));
		}
	}
/**
 * A method which gets the document of a webpage
 * @param ISSN The ISSN which the software appends to the base webpage
 * @return The document of the webpage
 * @throws IOException if the connection cannot be established
 */
	public Document connect(String ISSN) throws IOException {
		Document doc = Jsoup.connect(webpage.getAddress() + ISSN).get();
		return doc;
	}
	
	public void addChecker(Checker checker){
		checkers.add(checker);
	}
	
/**
 * The runnable implementation
 */
	public void run() {
		for(String issn : ISSNs){
			try {
				getInfo(connect(issn));				
			} catch (IOException e) {
				e=new IOException("Nem elérhető a weblap, kérem ellenőrizze az internetkapcsolatot.");
				e.printStackTrace();
			}
		}
	}

/**
 * A method which returns the results of the various checkers added to the composite
 * @return A hashmap where the type of the checker serves as the key and its results are stored as a list
 */
	public HashMap<CheckerType,List<String>> queryResults() {
		HashMap<CheckerType,List<String>> results=new HashMap<CheckerType, List<String>>();
		for(Checker c : checkers){
			results.put(c.getType(), c.getResults());
		}
		return results;
	}


	public void getInfo(Document doc){
		for(Checker c : checkers){
			c.getInfo(doc);
		}
	}

	public CheckerType getType() {
		return null;
	}

	public List<String> getResults() {
		return null;
	}
}
