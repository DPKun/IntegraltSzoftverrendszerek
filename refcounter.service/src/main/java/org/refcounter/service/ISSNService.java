package org.refcounter.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.refcounter.persist.excel.XlsxParser;
import org.refcounter.web.Checker;
import org.refcounter.web.MtmtEntriesChecker;

public class ISSNService {
	static XlsxParser parser;

	public static void main(String[] args) throws IOException, InvalidFormatException, InterruptedException {
		parser = new XlsxParser();
		File file = new File("D:\\DOÁB.xlsx");
		List<String> iSSNs = parser.getDataValues(file, "Nemzetközi", false, 0, 2);
		List<List<String>> lists = new ArrayList<>();
		for (int i = 1; i < 9; i++) {
			lists.add(iSSNs.subList(iSSNs.size() / 9 * i - 1, iSSNs.size() / 9 * i));
		}
		lists.add(iSSNs.subList(iSSNs.size() / 9 * 8, iSSNs.size()));
		List<Checker> checkers = new ArrayList<>();
		for (List<String> list : lists) {
			System.out.println(list.get(0));
			checkers.add(new MtmtEntriesChecker(list));
		}
		List<Thread> threads = new ArrayList<Thread>();
		for (Checker checker : checkers) {
			threads.add(new Thread(checker));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		List<String> resultEntries = new ArrayList<>();
		for (Checker checker : checkers) {
			resultEntries.addAll(checker.getResults());
		}
		parser.addRecordsToFileAsColumn(iSSNs, file, "result", 0, 0);
		parser.addRecordsToFileAsColumn(resultEntries, file, "result", 1, 0);
	}

}
