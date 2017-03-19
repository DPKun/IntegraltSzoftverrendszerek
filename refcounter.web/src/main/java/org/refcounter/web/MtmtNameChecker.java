package org.refcounter.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
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
public class MtmtNameChecker implements Checker, Runnable {

	private List<String> iSSNs;
	private List<String> entries = new ArrayList<String>();

	public MtmtNameChecker(List<String> iSSNs) {
		super();
		this.iSSNs = iSSNs;
	}

	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	public List<String> getiSSNs() {
		return iSSNs;
	}

	public void setiSSNs(List<String> iSSNs) {
		this.iSSNs = iSSNs;
	}

	/**
	 * A method which accesses a url of the mtmt search engine and queries the
	 * name data of the journal.
	 * 
	 * @param ISSN
	 *            The ISSN number of the journal.
	 * @return The name of the journal.
	 */
	public String getInfo(String ISSN) throws IOException {
		String result = "Not found";
		Document doc = Jsoup.connect("https://www.mtmt.hu/talalatok?egyszeru_kereses=" + ISSN).get();
		Elements journalEntry = doc.getElementsByClass("node node-journal node-teaser nem-surgos clearfix");
		Elements titles = journalEntry.select("h2");
		Elements titleLink = titles.select("a");
		for (Element title : titleLink) {
			result = title.text();
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
