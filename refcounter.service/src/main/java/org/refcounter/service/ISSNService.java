package org.refcounter.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.refcounter.persist.excel.Parser;
import org.refcounter.persist.excel.ParserFactory;
import org.refcounter.web.CheckerType;
import org.refcounter.web.CompositeChecker;

public class ISSNService {
	static Parser parser;

	/**
	 * Get all the worksheet names from an excel file
	 * 
	 * @param file
	 *            The file from which the method should get the worksheets
	 * @return The sheetnames as a list.
	 * @throws IOException
	 *             If the file cannot be opened
	 */
	public Set<String> getWorkSheets(File file) throws IOException {
		Set<String> result;
		parser = ParserFactory.getParser(FilenameUtils.getExtension(file.getName()));
		result = parser.getSheetNames(file);
		return result;
	}

	/**
	 * The method adds the values of a hashmap into an excel file as rows. The
	 * keys serve as the title for each column.
	 * 
	 * @param file
	 *            the target file into which the values should be uploaded.
	 * @param sheetName
	 *            The name of the sheet into which the data should be uploaded
	 * @param row
	 *            The starting row of the uploading
	 * @param column
	 *            The starting column of the uploading
	 * @param entries
	 *            The entries that should be uploaded
	 * @throws IOException IOException if thrown if the method cannot gain access to the file to write the data
	 */
	public void addResultsAsRow(File file, String sheetName, int row, int column,
			HashMap<String, List<String>> entries) throws IOException {
		if(file.exists()){
			try {
				FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
				FileLock lock=channel.lock();
				lock=channel.tryLock();
				lock.release();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		parser = ParserFactory.getParser(FilenameUtils.getExtension(file.getName()));
		Iterator mapIterator = entries.entrySet().iterator();
		int i = 0;
		while (mapIterator.hasNext()) {
			Entry<String, List<String>> actual = (Entry) mapIterator.next();

			try {
				parser.addRecordsToFileAsRow(actual.getValue(), file, sheetName, actual.getKey(), column,
						row + i);
				i++;
			} catch (IOException e) {
				e = new IOException("A fájlt nem lehet megnyitni, kérem zárja be, mielőtt folytatná a műveletet.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * The method adds the values of a hashmap into an excel file as columns.
	 * The keys serve as the title for each column.
	 * 
	 * @param file
	 *            the target file into which the values should be uploaded.
	 * @param sheetName
	 *            The name of the sheet into which the data should be uploaded
	 * @param row
	 *            The starting row of the uploading
	 * @param column
	 *            The starting column of the uploading
	 * @param entries
	 *            The entries that should be uploaded
	 * @throws IOException IOException if thrown if the method cannot gain access to the file to write the data
	 */
	public void addResultsAsColumn(File file, String sheetName, int row, int column,
			HashMap<String, List<String>> entries) throws IOException {
		if(file.exists()){
			try {
				FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
				FileLock lock=channel.lock();
				lock=channel.tryLock();
				lock.release();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		parser = ParserFactory.getParser(FilenameUtils.getExtension(file.getName()));
		Iterator mapIterator = entries.entrySet().iterator();
		int i = 0;
		while (mapIterator.hasNext()) {
			Entry<String, List<String>> actual = (Entry) mapIterator.next();

			try {
				parser.addRecordsToFileAsColumn(actual.getValue(), file, sheetName, actual.getKey(), column + i, row);
				i++;
			} catch (IOException e) {
				e = new IOException("A fájlt nem lehet megnyitni, kérem zárja be, mielőtt folytatná a műveletet.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method receives a list of ISSN numbers and the types of queries ot
	 * should run, then checkes the MTMT webpage for the requested values.
	 * 
	 * @param queries
	 *            The type of queries the method should run
	 * @param issns
	 *            The ISSN numbers which the method should check
	 * @return The received queries as a hashmap, where the keys are the query
	 *         types and each one has a list connected to them.
	 * @throws InterruptedException
	 *             Due to the server access, the query tends to be a bit slow.
	 *             If somehow it is interrupted during the process, it throws
	 *             the InterruptedException.
	 */
	public HashMap<String, List<String>> queryWebpage(List<CheckerType> queries, List<String> issns)
			throws InterruptedException {
		List<List<String>> lists = new ArrayList<>();
		//Create a sublist for each thread the method runs
		for (int i = 1; i < 9; i++) {
			lists.add(issns.subList(issns.size() / 9 * i - 1, issns.size() / 9 * i));
		}
		lists.add(issns.subList(issns.size() / 9 * 8, issns.size()));
		List<CompositeChecker> checkers = new ArrayList<>();
		//create a compositeChecker for each list, then run them as separate threads
		for (List<String> list : lists) {
			checkers.add(new CompositeChecker(list, queries));
		}

		List<Thread> threads = new ArrayList<Thread>();
		for (CompositeChecker checker : checkers) {
			threads.add(new Thread(checker));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		//Join the separate results together into a hashmap, then return with them.
		HashMap<String, List<String>> results = new HashMap<>();
		results.put("ISSN", issns);
		for (CheckerType query : queries) {
			List<String> result = new ArrayList<>();
			for (CompositeChecker checker : checkers) {
				result.addAll(checker.queryResults().get(query));
			}
			results.put(query.toString(), result);
		}

		return results;
	}

	/**
	 * Load a row of ISSN numbers for querying
	 * @param file The file from which the method should load the data
	 * @param sheet The worksheet from which the method should load the data
	 * @param row The row from which the data should be loaded
	 * @param column The starting column of the querying
	 * @return A list of ISSN numbers as string
	 * @throws InvalidFormatException If the file is not in the expected format.
	 * @throws IOException If the file cannot be accessed
	 */
	public List<String> loadFromRow(File file, String sheet, int row, int column)
			throws InvalidFormatException, IOException {
		List<String> result;
		parser = ParserFactory.getParser(FilenameUtils.getExtension(file.getName()));
		result = parser.getDataValues(file, sheet, true, column, row);
		return result;
	}

	/**
	 * Load a column of ISSN numbers for querying
	 * @param file The file from which the method should load the data
	 * @param sheet The worksheet from which the method should load the data
	 * @param row The starting row of the query
	 * @param column The column from which the data should be loaded
	 * @return A list of ISSN numbers as string
	 * @throws InvalidFormatException If the file is not in the expected format.
	 * @throws IOException If the file cannot be accessed
	 */
	public List<String> loadFromColumn(File file, String sheet, int row, int column)
			throws InvalidFormatException, IOException {
		List<String> result;
		parser = ParserFactory.getParser(FilenameUtils.getExtension(file.getName()));
		result = parser.getDataValues(file, sheet, false, column, row);
		return result;
	}
}
