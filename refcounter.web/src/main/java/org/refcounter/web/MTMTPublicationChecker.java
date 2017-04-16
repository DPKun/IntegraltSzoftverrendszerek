package org.refcounter.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The implementation of the checker interface for a person's publications.
 * @author Felhasználó
 *
 */
public class MTMTPublicationChecker implements Checker{
	private List<String> results=new ArrayList<String>();

	public CheckerType getType() {
		return CheckerType.PUBLICATIONS;
	}

	public List<String> getResults() {
		return results;
	}

	public void getInfo(Document doc) {
		String result="Not Found";
		Elements entry = doc.getElementsByClass("td5_foosszesito2");
		
		for(Element element : entry){
			element.getElementsByAttributeValue("href", "#").toString();
		}
		
		results.add(result);
	}

}
