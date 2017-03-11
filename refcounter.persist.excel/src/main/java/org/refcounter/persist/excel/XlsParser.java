package org.refcounter.persist.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFRow.CellIterator;

/**
 * This Class serves as a parser for XLS excel document. The methods of the
 * class should be capable of querying the data and upload it into a file
 *
 */
public class XlsParser {

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
	public static boolean isCellEmpty(final HSSFCell cell) {
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
	public HSSFWorkbook loadWorkBook(File file) throws IOException {
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
	public Collection<String> getDataValues(HSSFWorkbook workBook, String sheetName, boolean direction, int column,
			int row) throws InvalidFormatException, IOException {
		HSSFSheet table = workBook.getSheet(sheetName);
		Set<String> result = new HashSet<String>();
		if (direction) {
			HSSFRow workRow = table.getRow(row);
			CellIterator iterator = (CellIterator) workRow.cellIterator();
			while (iterator.hasNext()) {
				HSSFCell workCell = (HSSFCell) iterator.next();
				if (workCell.getColumnIndex() >= column) {
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
	public void addRecordsToFile(Collection<String> entries, File file, HSSFWorkbook workBook, String sheetName,
			String columnName) throws IOException {
		// open the worksheet
		HSSFSheet workSheet;
		if (workBook.getSheet(sheetName) == null) {
			workSheet = workBook.createSheet(sheetName);
			HSSFCell cell = workSheet.createRow(0).createCell(0, CellType.STRING);
			cell.setCellValue(columnName);

		} else {
			workSheet = workBook.getSheet(sheetName);
		}
		// select the first empt line or create one, then upload the data
		HSSFRow row = workSheet.getRow(0);
		int index = -1;
		for (Cell cell : row) {
			if (isCellEmpty((HSSFCell) cell)) {
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
				HSSFCell cell = (HSSFCell) row.createCell(row.getLastCellNum() + 1);
				cell.setCellValue(entry);
			} else {
				if (row.getCell(index) == null) {
					row.createCell(index);
				}
				HSSFCell cell = (HSSFCell) row.getCell(index);
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
