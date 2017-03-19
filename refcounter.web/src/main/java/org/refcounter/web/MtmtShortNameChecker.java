package org.refcounter.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
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

public class MtmtShortNameChecker implements Checker, Runnable {

	private List<String> iSSNs;
	private List<String> entries = new ArrayList<String>();

	public MtmtShortNameChecker(List<String> iSSNs) {
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

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * short name data of the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The short name of the journal.
	 */
	public String getInfo(String ISSN) throws IOException {
		String result = "Not found";
		Document doc = Jsoup.connect("https://www.mtmt.hu/talalatok?egyszeru_kereses=" + ISSN).get();
		Elements journalEntry = doc
				.getElementsByClass("field field-name-field-rovid-nev field-type-text field-label-inline clearfix");
		for (Element entry : journalEntry) {
			result = entry.getElementsByClass("field-item even").get(0).text();
		}
		System.out.println(ISSN + " : " + result + " found.");
		return result;
	}

}
