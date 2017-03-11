package org.refcounter.persist.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFRow.CellIterator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This Class serves as a parser for XLSX excel document. The methods of the
 * class should be capable of querying the data and upload it into a file
 *
 */
public class XlsxParser {

	/**
	 * Check if the given String is a ISSN number
	 * 
	 * @param ISSN
	 *            The string that should be checked
	 * @return true, if the string is a possible ISSN number, false if it does
	 *         not match the format of the ISSN number
	 */
	public boolean checkISSN(String ISSN) {
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

	/**
	 * Checks if the value of a given {@link XSSFCell} is empty.
	 * 
	 * @param cell
	 *            The {@link XSSFCell}.
	 * @return {@code true} if the {@link XSSFCell} is empty. {@code false}
	 *         otherwise.
	 */
	public static boolean isCellEmpty(final XSSFCell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return true;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * A workbook loading method to avoid multiple file access
	 * 
	 * @param file
	 *            The file from which the workbook should be loaded
	 * @return The workBook from the file
	 * @throws IOException
	 *             if the filepath is wrong
	 */
	public XSSFWorkbook loadWorkBook(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		XSSFWorkbook workBook = new XSSFWorkbook(stream);
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
	public Collection<String> getDataValues(XSSFWorkbook workBook, String sheetName, boolean direction, int column,
			int row) throws InvalidFormatException, IOException {
		XSSFSheet table = workBook.getSheet(sheetName);
		Set<String> result = new HashSet<String>();
		if (direction) {
			XSSFRow workRow = table.getRow(row);
			CellIterator iterator = (CellIterator) workRow.cellIterator();
			while (iterator.hasNext()) {
				XSSFCell workCell = (XSSFCell) iterator.next();
				if (workCell.getColumnIndex() >= column) {
					result.add(workCell.getStringCellValue());
				}
			}
		} else {
			for (int i = row; i <= table.getLastRowNum(); i++) {
				XSSFRow workRow = table.getRow(i);
				XSSFCell cell = workRow.getCell(column);
				if (cell == null) {
					break;
				}
				String content = cell.getStringCellValue();
				if (checkISSN(content)) {
					result.add(content);
				}
			}
		}
		return result;
	}

	/**
	 * The method inserts the given records into a existing file
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
	public void addRecordsToFile(Collection<String> entries, File file, XSSFWorkbook workBook, String sheetName,
			String columnName) throws IOException {
		// open the worksheet
		XSSFSheet workSheet;
		if (workBook.getSheet(sheetName) == null) {
			workSheet = workBook.createSheet(sheetName);
			XSSFCell cell = workSheet.createRow(0).createCell(0, CellType.STRING);
			cell.setCellValue(columnName);

		} else {
			workSheet = workBook.getSheet(sheetName);
		}
		// select the first empty line or create one, then upload the data
		XSSFRow row = workSheet.getRow(0);
		int index = -1;
		for (Cell cell : row) {
			if (isCellEmpty((XSSFCell) cell)) {
				index = cell.getColumnIndex();
				cell.setCellValue(columnName);
				break;
			}
		}
		if (index == -1) {
			row.createCell(row.getLastCellNum() + 1).setCellValue(columnName);
		}
		int insertIndex = 0;
		for (String entry : entries) {
			insertIndex++;
			if (workSheet.getRow(insertIndex) == null) {
				workSheet.createRow(insertIndex);
			}
			row = workSheet.getRow(insertIndex);
			if (index == -1) {
				XSSFCell cell = (XSSFCell) row.createCell(row.getLastCellNum() + 1);
				cell.setCellValue(entry);
			} else {
				if (row.getCell(index) == null) {
					row.createCell(index);
				}
				XSSFCell cell = (XSSFCell) row.getCell(index);
				cell.setCellValue(entry);

			}
		}
		// save the edited file
		// TODO add file creation if file does not exist
		FileOutputStream output = new FileOutputStream(file);
		workBook.write(output);
		output.close();
	}

}
