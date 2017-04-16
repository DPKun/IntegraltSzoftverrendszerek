package org.refcounter.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * An implementation of the checker interface. It searches for the number of
 * entries in a journal by its ISSN number.
 * For further documentation see {@docRoot Checker}.
 * @author Dániel Péter Kun
 *
 */

public class MtmtEntriesChecker implements Checker {
	private List<String> results = new ArrayList<String>();

	public MtmtEntriesChecker(){
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> entries) {
		this.results = entries;
	}

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * number of entries found in the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The number of entries in the journal.
	 */
	public void getInfo(Document doc){
		String result = "Not found";
		Elements journalEntry = doc.getElementsByClass(
				"field field-name-field-kozlemenyek-szama field-type-number-integer field-label-inline clearfix");
		for (Element entry : journalEntry) {
			result = entry.getElementsByClass("field-item even").get(0).text();
		}
		results.add(result);
	}

	public CheckerType getType() {
		return CheckerType.ENTRY;
	}

}
