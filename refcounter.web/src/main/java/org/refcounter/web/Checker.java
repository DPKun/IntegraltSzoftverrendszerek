package org.refcounter.web;

import java.io.IOException;
import java.util.List;

public interface Checker extends Runnable{

	public List<String> getResults();
	
	public String getInfo(String ISSN) throws IOException;
}
