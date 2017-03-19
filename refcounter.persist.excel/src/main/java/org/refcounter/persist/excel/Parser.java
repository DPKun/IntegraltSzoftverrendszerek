package org.refcounter.persist.excel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * An Interface representing the excel parser implemetations.
 * Used to access the specific parsers throught the factory.
 * @author Dániel Péter Kun
 *
 */

public interface Parser {

	public List<String> getDataValues(File file, String sheetName, boolean direction, int column, int row)
			throws InvalidFormatException, IOException;

	public void addRecordsToFile(Collection<String> entries, File file, String sheetName, String columnName)
			throws IOException;

	public void addRecordsToFileAsRow(List<String> entries, File file, String sheetName, String rowName,
			int columnNumber, int rowNumber) throws IOException;

	public void addRecordsToFileAsRow(List<String> entries, File file, String sheetName, int columnNumber,
			int rowNumber) throws IOException;

	public void addRecordsToFileAsColumn(List<String> entries, File file, String sheetName, int columnNumber,
			int rowNumber) throws IOException;

	public void addRecordsToFileAsColumn(List<String> entries, File file, String sheetName, String columnName,
			int columnNumber, int rowNumber) throws IOException;
}
