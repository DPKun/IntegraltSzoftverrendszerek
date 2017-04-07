package org.refcounter.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * An implementation of the checker interface. It searches for the name of
 * journals by their ISSN numbers.
 * 
 * @author Dániel Péter Kun
 *
 */
public class MtmtNameChecker implements Checker{

	private List<String> results = new ArrayList<String>();

	public MtmtNameChecker() {
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> entries) {
		this.results = entries;
	}

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * name data of the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The name of the journal.
	 */
	public void getInfo(Document doc){
		String result = "Not found";
		Elements journalEntry = doc.getElementsByClass("node node-journal node-teaser nem-surgos clearfix");
		Elements titles = journalEntry.select("h2");
		Elements titleLink = titles.select("a");
		for (Element title : titleLink) {
			result = title.text();
		}
		results.add(result);
	}

	public CheckerType getType() {
		return CheckerType.NAME;
	}
}
