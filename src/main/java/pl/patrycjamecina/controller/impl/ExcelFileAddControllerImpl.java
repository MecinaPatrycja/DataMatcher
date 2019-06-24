package pl.patrycjamecina.controller.impl;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.patrycjamecina.controller.ExcelFileAddController;
import pl.patrycjamecina.view.View;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class ExcelFileAddControllerImpl implements ExcelFileAddController {
    private View view;
    public File selectedFile;

    public ExcelFileAddControllerImpl(final View view) {
        this.view = view;
    }

    @Override
    public List<XSSFRow> addExcelFile() throws IOException, InvalidFormatException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("xls, xlsx", "xls", "xlsx");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
            XSSFWorkbook wb = new XSSFWorkbook(selectedFile);
            XSSFSheet sheet = wb.getSheetAt(0);
            List<XSSFRow> sheetData = new ArrayList<>();
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();
//                Iterator cells = row.cellIterator();
//                List<Cell> data = new ArrayList<>();
//                while (cells.hasNext()) {
//                    Cell cell = (Cell) cells.next();
//                    data.add(cell);
//                }
//                sheetData.add(data);
                sheetData.add(row);
            }
            return sheetData;
        }
        System.out.println(selectedFile.getAbsolutePath());
        int timerDelay = 10;
        new Timer(timerDelay, new ActionListener() {
            private int index = 0;
            private int maxIndex = 100;

            public void actionPerformed(ActionEvent e) {
                if (index < maxIndex) {
                    view.getProgressBar().setValue(index);
                    index++;
                } else {
                    view.getProgressBar().setVisible(true);
                    view.getProgressBar().setStringPainted(true);
                    view.getProgressBar().setValue(0);
                    view.getProgressBar().setValue(maxIndex);
                    ((Timer) e.getSource()).stop();
                    view.getFileLoadedJLabel().setVisible(true);
                }
            }
        }).start();
        view.getProgressBar().setValue(view.getProgressBar().getMinimum());
        return null;
    }

    @Override
    public void progressBarSetUp() {
        view.getProgressBar().setStringPainted(true);
        view.getProgressBar().setValue(0);
        int timerDelay = 10;
        new Timer(timerDelay, new ActionListener() {
            private int index = 0;
            private int maxIndex = 100;

            public void actionPerformed(ActionEvent e) {
                if (index < maxIndex) {
                    view.getProgressBar().setValue(index);
                    index++;
                } else {
                    view.getProgressBar().setValue(maxIndex);
                    ((Timer) e.getSource()).stop(); // stop the timer
                }
            }
        }).start();
        view.getProgressBar().setValue(view.getProgressBar().getMinimum());
    }

    @Override
    public void deleteFile() {
        if (selectedFile.exists()) {
            if (selectedFile.delete()) {
                JOptionPane.showMessageDialog(null, "File deleted", "Ok", JOptionPane.CLOSED_OPTION);
                view.getFileLoadedJLabel().setVisible(false);
                view.getProgressBar().setVisible(false);
            }
        }
        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(null, "There is nothing to delete", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
