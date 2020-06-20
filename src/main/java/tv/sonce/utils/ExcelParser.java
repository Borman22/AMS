package tv.sonce.utils;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import tv.sonce.exceptions.ClosingFileExceptionRT;
import tv.sonce.exceptions.FileNotFoundExceptionRT;
import tv.sonce.exceptions.ReadingFileExceptionRT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser {

    private List<List<String>> sheet;

    public ExcelParser(String file, int sheetNumber) {
        HSSFWorkbook myExcelBook;
        try {
            myExcelBook = new HSSFWorkbook(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundExceptionRT(e.getMessage());
        } catch (IOException e) {
            throw new ReadingFileExceptionRT(e.getMessage());
        }
        feelSheet(myExcelBook.getSheetAt(sheetNumber));
        try {
            myExcelBook.close();
        } catch (IOException e) {
            throw new ClosingFileExceptionRT(e.getMessage());
        }
    }

    public List<List<String>> getSheet() {
        return sheet;
    }

    private void feelSheet(HSSFSheet hssfSheet) {
        sheet = new ArrayList<>(hssfSheet.getLastRowNum() + 1);
        for (int i = hssfSheet.getFirstRowNum(); i <= hssfSheet.getLastRowNum(); i++) {
            List<String> row = new ArrayList<>(hssfSheet.getRow(0).getLastCellNum() + 1);
            for (Cell cell : hssfSheet.getRow(i))
                row.add(cell.toString());
            sheet.add(row);
        }
    }
}
