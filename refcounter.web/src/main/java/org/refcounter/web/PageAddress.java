package org.refcounter.web;
/**
 * An enum containing various base pageAddresses on which the software accesses information
 * @author Dániel Péter Kun
 *
 */
public enum PageAddress {
	
	MTMTJOURNAL("https://www.mtmt.hu/talalatok?egyszeru_kereses="),MTMTPERSON("https://vm.mtmt.hu/search/tmtosztaly.php?AuthorID=");
	
	private String address;
	
	PageAddress(String webpage){
		this.address=webpage;
	}
	
	public String getAddress(){
		return this.address;
	}

}
