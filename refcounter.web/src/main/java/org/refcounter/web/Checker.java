package org.refcounter.web;

import java.io.IOException;

public interface Checker {

	public String getInfo(String ISSN) throws IOException;
}
