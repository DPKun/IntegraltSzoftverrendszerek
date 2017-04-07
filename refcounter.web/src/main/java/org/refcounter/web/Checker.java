package org.refcounter.web;

import java.util.List;

import org.jsoup.nodes.Document;

public interface Checker{

	public CheckerType getType();
	
	public List<String> getResults();
	
	public void getInfo(Document doc);
}
