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
	
	private List<Checker> checkers = new ArrayList<Checker>();
	
	private List<String> ISSNs;
	
	public CompositeChecker(List<String> issns,List<CheckerType> checkers){
		this.ISSNs=issns;
		for(CheckerType c : checkers){
			this.checkers.add(CheckerFactory.getChecker(c));
		}
	}

	public Document connect(String ISSN) throws IOException {
		Document doc = Jsoup.connect("https://www.mtmt.hu/talalatok?egyszeru_kereses=" + ISSN).get();
		return doc;
	}
	
	public void addChecker(Checker checker){
		checkers.add(checker);
	}
	

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
