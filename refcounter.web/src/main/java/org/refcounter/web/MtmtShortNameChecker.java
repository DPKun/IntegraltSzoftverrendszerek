package org.refcounter.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * An implementation of the checker interface. It searches for the short name of
 * journals by their ISSN numbers.
 * 
 * @author Dániel Péter Kun
 *
 */

public class MtmtShortNameChecker implements Checker {

	private List<String> results = new ArrayList<String>();

	public MtmtShortNameChecker() {
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> entries) {
		this.results = entries;
	}

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * short name data of the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The short name of the journal.
	 */
	public void getInfo(Document doc) {
		String result = "Not found";
		Elements journalEntry = doc
				.getElementsByClass("field field-name-field-rovid-nev field-type-text field-label-inline clearfix");
		for (Element entry : journalEntry) {
			result = entry.getElementsByClass("field-item even").get(0).text();
		}
		results.add(result);
	}

	public CheckerType getType() {
		return CheckerType.SHORTNAME;
	}

}
