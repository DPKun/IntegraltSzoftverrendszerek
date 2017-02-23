package uni.iit.RefCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {
	private static File FIRST_FILE = new File("D://proba.xlsx");

	private static XSSFWorkbook openWorkBook(File file) throws IOException{
		XSSFWorkbook result;
		FileInputStream input = new FileInputStream(FIRST_FILE);
		result = new XSSFWorkbook(input);
		return result;
	}
	
	public static void main(String[] args) throws IOException{
		
        XSSFWorkbook workBook = openWorkBook(FIRST_FILE);
		
		XSSFSheet workSheet = workBook.getSheetAt(0);

		Iterator<Row> rowIterator = workSheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					System.out.println(cell.getStringCellValue() + " sor:" + cell.getRowIndex() + ", oszlop:"
							+ cell.getColumnIndex());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					System.out.println(cell.getNumericCellValue() + " sor:" + cell.getRowIndex() + ", oszlop:"
							+ cell.getColumnIndex());
					break;
				default:
					System.out.println("passz");

				}
			}

		}

	}
	
	
}
