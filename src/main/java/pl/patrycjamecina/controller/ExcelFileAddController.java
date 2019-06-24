package pl.patrycjamecina.controller;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.IOException;
import java.util.List;
public interface ExcelFileAddController {
    List<XSSFRow> addExcelFile() throws IOException, InvalidFormatException;
    void progressBarSetUp();
    void deleteFile();
}
