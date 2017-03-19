package org.refcounter.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * An implementation of the checker interface. It searches for the number of
 * entries in a journal by its ISSN number.
 * 
 * @author Dániel Péter Kun
 *
 */

public class MtmtEntriesChecker implements Runnable, Checker {

	private List<String> iSSNs;
	private List<String> entries = new ArrayList<String>();

	public MtmtEntriesChecker(List<String> iSSNs) {
		super();
		this.iSSNs = iSSNs;
	}

	public List<String> getiSSNs() {
		return iSSNs;
	}

	public void setiSSNs(List<String> iSSNs) {
		this.iSSNs = iSSNs;
	}

	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * number of entries found in the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The number of entries in the journal.
	 */
	public String getInfo(String ISSN) throws IOException {
		String result = "Not found";
		Document doc = Jsoup.connect("https://www.mtmt.hu/talalatok?egyszeru_kereses=" + ISSN).get();
		Elements journalEntry = doc.getElementsByClass(
				"field field-name-field-kozlemenyek-szama field-type-number-integer field-label-inline clearfix");
		for (Element entry : journalEntry) {
			result = entry.getElementsByClass("field-item even").get(0).text();
		}
		System.out.println(ISSN + " : " + result + " found.");
		return result;
	}

	public void run() {
		for (String iSSN : iSSNs) {
			try {
				entries.add(getInfo(iSSN));
			} catch (IOException e) {
				System.out.println("Thread crashed at: " + iSSN);
				e.printStackTrace();
			}
		}
	}
}
