package org.refcounter.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.refcounter.persist.excel.XlsxParser;
import org.refcounter.web.MtmtEntriesChecker;

public class ISSNService {
	static XlsxParser parser;
	
	public static void main(String[] args) throws IOException, InvalidFormatException, InterruptedException{	
		parser = new XlsxParser();
		File file=new File("D:\\elsoResz.xlsx");
		List<String> iSSNs =parser.getDataValues(file, "Munka1", false, 0, 0);
		List<String> iSSN1 = iSSNs.subList(0, iSSNs.size()/3);
		List<String> iSSN2 = iSSNs.subList(iSSNs.size()/3, iSSNs.size()/3*2);
		List<String> iSSN3 = iSSNs.subList(iSSNs.size()/3*2, iSSNs.size());
		MtmtEntriesChecker checker1 = new MtmtEntriesChecker(iSSN1);
		MtmtEntriesChecker checker2 = new MtmtEntriesChecker(iSSN2);
		MtmtEntriesChecker checker3 = new MtmtEntriesChecker(iSSN3);
		if(iSSN1.get(iSSN1.size()-1).equals(iSSN2.get(0))){
			iSSN1.remove(iSSN1.size()-1);
		}
		if(iSSN2.get(iSSN2.size()-1).equals(iSSN3.get(0))){
			iSSN2.remove(iSSN2.size()-1);
		}
		Thread thread1 = new Thread(checker1, "thread1");
		Thread thread2 = new Thread(checker2, "thread2");
		Thread thread3 = new Thread(checker3, "thread3");
		List<Thread> threads = new ArrayList<Thread>();
		threads.add(thread1);
		threads.add(thread2);
		threads.add(thread3);
		for(Thread thread : threads){
			thread.start();
		}
		for(Thread thread : threads){
			thread.join();
		}
		List<String> resultEntries = checker1.getEntries();
		for(int i =0; i<checker2.getEntries().size();i++){
			resultEntries.add(checker2.getEntries().get(i));
		}
		for(int i =0; i<checker3.getEntries().size();i++){
			resultEntries.add(checker3.getEntries().get(i));
		}
		parser.addRecordsToFile(iSSNs, file, "eredmeny4", "ISSN");
		parser.addRecordsToFile(resultEntries, file, "eredmeny4", "bejegyzes");
	}

}
