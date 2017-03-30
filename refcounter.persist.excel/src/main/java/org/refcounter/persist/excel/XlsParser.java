package org.refcounter.persist.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.streaming.SXSSFRow.CellIterator;

/**
 * This Class serves as a parser for XLS excel document. The methods of the
 * class should be capable of querying the data and upload it into a file
 *
 */
public class XlsParser implements Parser {
	/**
	 * Check if the given String is a ISSN number
	 * 
	 * @param ISSN
	 *            The string that should be checked
	 * @return true, if the string is a possible ISSN number, false if it does
	 *         not match the format of the ISSN number
	 */
	private boolean checkISSN(String ISSN) {
		if (ISSN.length() == 9) {
			if ("-".equalsIgnoreCase(ISSN.substring(4, 5))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// TODO Create matcher for ISSN handling
	/*
	 * public List<String> getISSNs(String cellValue) { List<String> result =
	 * new ArrayList<String>(); if (cellValue.length() < 13) { return result; }
	 * else if(cellValue.length()==13) { String ISSN = cellValue.substring(5,
	 * 9); ISSN = ISSN.concat("-"); ISSN = ISSN.concat(cellValue.substring(9,
	 * 13)); result.add(ISSN); } else{ String ISSN = cellValue.substring(5, 9);
	 * ISSN= ISSN.concat("-"); ISSN=ISSN.concat(cellValue.substring(9,13));
	 * result.add(ISSN); ISSN=cellValue.substring(15, 19);
	 * ISSN=ISSN.concat("-"); ISSN=ISSN.concat(cellValue.substring(19,23));
	 * result.add(ISSN); }
	 * 
	 * return result; }
	 */

	/**
	 * Checks if the value of a given {@link HSSFCell} is empty.
	 * 
	 * @param cell
	 *            The {@link HSSFCell}.
	 * @return {@code true} if the {@link HSSFCell} is empty. {@code false}
	 *         otherwise.
	 */
	private boolean isCellEmpty(final HSSFCell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return true;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * A convenience method to load the workbook from a file
	 * 
	 * @param file
	 *            The file from which the workbook should be loaded
	 * @return The workBook from the file
	 * @throws IOException
	 *             if the filepath is wrong
	 */
	private HSSFWorkbook loadWorkBook(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		HSSFWorkbook workBook = new HSSFWorkbook(stream);
		return workBook;
	}

	/**
	 * This method gets the Data from the requested row or column. Currently
	 * works with ISSN codes
	 * 
	 * @param workBook
	 *            The workBook of the query
	 * @param sheetName
	 *            The name of the target worksheet
	 * @param direction
	 *            if true, the method queries from a row, if false, the method
	 *            queries from a column
	 * @param column
	 *            The starting column of the querying
	 * @param row
	 *            The starting row of the querying
	 * @return The queried data as a collection of String values
	 * @throws InvalidFormatException
	 *             if the file is not a XLS
	 * @throws IOException
	 *             if the filepath is not correct
	 */
	public List<String> getDataValues(File file, String sheetName, boolean direction, int column, int row)
			throws InvalidFormatException, IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		HSSFSheet table = workBook.getSheet(sheetName);
		List<String> result = new ArrayList<String>();
		if (direction) {
			HSSFRow workRow = table.getRow(row);
			CellIterator iterator = (CellIterator) workRow.cellIterator();
			while (iterator.hasNext()) {
				HSSFCell workCell = (HSSFCell) iterator.next();
				if (workCell.getColumnIndex() >= column) {
					if (workCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
						workCell.setCellType(CellType.STRING);
					}
					result.add(workCell.getStringCellValue());
				}
			}
		} else {
			for (int i = row; i <= table.getLastRowNum(); i++) {
				HSSFRow workRow = table.getRow(i);
				HSSFCell cell = workRow.getCell(column);
				if (cell == null) {
					break;
				}
				if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
					cell.setCellType(CellType.STRING);
				}
				String content = cell.getStringCellValue();
				if (checkISSN(content)) {
					result.add(content);
				}
				// result.addAll(getISSNs(content));
			}
		}
		return result;
	}

	/**
	 * The method inserts the given records into a existing file. Instead of the
	 * given coordinates in the worktable, it looks for the first empty line.
	 * 
	 * @param entries
	 *            the records that should be uploaded
	 * @param file
	 *            The target file into which the data should be uploaded
	 * @param workBook
	 *            The workbook that should be saved
	 * @param sheetName
	 *            The name of the sheet into which the data will be uploaded
	 * @param columnName
	 *            The name of the created column
	 * @throws IOException
	 *             in case of bad filepath
	 */
	public void addRecordsToFile(Collection<String> entries, File file, String sheetName, String columnName)
			throws IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		// open the worksheet
		HSSFSheet workSheet = getSheet(workBook, sheetName);
		// select the first empty line or create one, then upload the data
		HSSFRow row = workSheet.getRow(0);
		int index = -1;
		for (Cell cell : row) {
			if (isCellEmpty((HSSFCell) cell)) {
				index = cell.getColumnIndex();
				cell.setCellValue(columnName);
				break;
			}
		}
		if (index < 0) {
			row.createCell(row.getLastCellNum()).setCellValue(columnName);
			index = row.getLastCellNum();
		}
		int insertIndex = 0;
		for (String entry : entries) {
			insertIndex++;
			row = getRow(workSheet, insertIndex);
			HSSFCell cell = row.getCell(index, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(entry);
		}
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();
	}

	/**
	 * Adds the given records to the file as a row.
	 * 
	 * @param entries
	 *            The entries that should be added
	 * @param file
	 *            The file into which the user wants to upload the data.
	 * @param sheetName
	 *            The name of the into which the user wants to upload the data.
	 * @param rowName
	 *            The name of row, which will be shown in the first cell.
	 * @param columnNumber
	 *            The starting column of the insertion.
	 * @param rowNumber
	 *            The starting row of the insertion
	 * @throws IOException
	 */
	public void addRecordsToFileAsRow(List<String> entries, File file, String sheetName, String rowName,
			int columnNumber, int rowNumber) throws IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		HSSFSheet workSheet = getSheet(workBook, sheetName);
		HSSFRow row = getRow(workSheet, rowNumber);
		HSSFCell cell = row.getCell(columnNumber, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(rowName);
		for (int i = 0; i < entries.size(); i++) {
			cell = row.getCell(columnNumber + 1 + i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(entries.get(i));
		}
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();

	}

	/**
	 * Adds the given records to the file as a row. Used if the row does not
	 * have a title
	 * 
	 * @param entries
	 *            The entries that should be added
	 * @param file
	 *            The file into which the user wants to upload the data.
	 * @param sheetName
	 *            The name of the into which the user wants to upload the data.
	 * @param columnNumber
	 *            The starting column of the insertion.
	 * @param rowNumber
	 *            The starting row of the insertion
	 * @throws IOException
	 */
	public void addRecordsToFileAsRow(List<String> entries, File file, String sheetName, int columnNumber,
			int rowNumber) throws IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		HSSFSheet workSheet = getSheet(workBook, sheetName);
		HSSFRow row = getRow(workSheet, rowNumber);
		HSSFCell cell;
		for (int i = 0; i < entries.size(); i++) {
			cell = row.getCell(columnNumber + i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(entries.get(i));
		}
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();
	}

	/**
	 * Adds the given records to the file as a column.
	 * 
	 * @param entries
	 *            The entries that should be added
	 * @param file
	 *            The file into which the user wants to upload the data.
	 * @param sheetName
	 *            The name of the into which the user wants to upload the data.
	 * @param columnName
	 *            The name of column, which will be shown in the first cell.
	 * @param columnNumber
	 *            The starting column of the insertion.
	 * @param rowNumber
	 *            The starting row of the insertion
	 * @throws IOException
	 */
	public void addRecordsToFileAsColumn(List<String> entries, File file, String sheetName, int columnNumber,
			int rowNumber) throws IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		HSSFSheet workSheet = getSheet(workBook, sheetName);
		HSSFRow row;
		HSSFCell cell;
		for (int i = 0; i < entries.size(); i++) {
			row = getRow(workSheet, rowNumber + i);
			cell = row.getCell(columnNumber, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(entries.get(i));
		}
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();
	}

	/**
	 * Adds the given records to the file as a column. Used if the column does
	 * not have a title.
	 * 
	 * @param entries
	 *            The entries that should be added
	 * @param file
	 *            The file into which the user wants to upload the data.
	 * @param sheetName
	 *            The name of the into which the user wants to upload the data.
	 * @param columnNumber
	 *            The starting column of the insertion.
	 * @param rowNumber
	 *            The starting row of the insertion
	 * @throws IOException
	 */
	public void addRecordsToFileAsColumn(List<String> entries, File file, String sheetName, String columnName,
			int columnNumber, int rowNumber) throws IOException {
		HSSFWorkbook workBook = loadWorkBook(file);
		HSSFSheet workSheet = getSheet(workBook, sheetName);
		HSSFRow row = getRow(workSheet, rowNumber);
		HSSFCell cell = row.getCell(columnNumber, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(columnName);
		for (int i = 0; i < entries.size(); i++) {
			row = getRow(workSheet, rowNumber + 1 + i);
			cell = row.getCell(columnNumber, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(entries.get(i));
		}
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();
	}

	/**
	 * A method which tries to get a sheet in the workbook. If the sheet does
	 * not exist, it is created.
	 * 
	 * @param workBook
	 *            The workbook in which the sheet should be found
	 * @param sheetName
	 *            The name of the sheet.
	 * @return An object representing the worksheet.
	 */
	private HSSFSheet getSheet(HSSFWorkbook workBook, String sheetName) {
		HSSFSheet workSheet;
		if (workBook.getSheet(sheetName) == null) {
			workSheet = workBook.createSheet(sheetName);
			workSheet.createRow(0).createCell(0, CellType.STRING);
		} else {
			workSheet = workBook.getSheet(sheetName);
		}
		return workSheet;
	}

	/**
	 * A method which tries to get a row in the worksheet. If the row does not
	 * exist, it is created.
	 * 
	 * @param sheet
	 *            The sheet in which the row should be found.
	 * @param rowNumber
	 *            The index of the row.
	 * @return an object representing the row.
	 */
	private HSSFRow getRow(HSSFSheet sheet, int rowNumber) {
		HSSFRow row;
		if (sheet.getRow(rowNumber) == null) {
			row = sheet.createRow(rowNumber);
		} else {
			row = sheet.getRow(rowNumber);
		}
		return row;
	}

	/**
	 * A method which gets all sheet names found in a file
	 * @param file The file in which the method should search
	 * @return the sheet names as a set
	 */
	public Set<String> getSheetNames(File file) throws IOException {
		HSSFWorkbook workBook= loadWorkBook(file);
		Set<String> sheetNames = new HashSet<String>();
		for(int i=0;i<workBook.getNumberOfSheets();i++){
			sheetNames.add(workBook.getSheetName(i));
		}
		return sheetNames;
	}

}
