package pl.patrycjamecina.controller.impl;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pl.patrycjamecina.controller.ActionListenersController;
import pl.patrycjamecina.view.View;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@NoArgsConstructor
public class ActionListenersControllerImpl implements ActionListenersController {
    private View view;
    private SupplierDatabaseControllerImpl supplierDatabaseController;
    private ExcelFileAddControllerImpl excelFileAddControllerImpl;
    private ChooseFieldsCreationControllerImpl chooseFieldsCreationControllerImpl;
    private SQLQueriesControllerImpl sqlQueriesControllerImpl;
    private DatabaseControllerImpl databaseControllerImpl;
    private JComboBox[] comboBoxes;
    private List<XSSFRow> data;

    public ActionListenersControllerImpl(final View view) {
        ActionListener actionListener = e -> view.getTabbedPane1().setSelectedIndex(1);
        view.getAddDataButton().addActionListener(actionListener);
        ActionListener actionListener1 = e -> view.getTabbedPane1().setSelectedIndex(3);
        view.getDatabaseButton().addActionListener(actionListener1);
        view.getAddMoreDataButton().addActionListener(actionListener);
        view.getAdd_file().addActionListener(e -> {
            try {
                data = excelFileAddControllerImpl.addExcelFile();
                List<String> fieldNames = new ArrayList<>();
                Iterator cells = data.get(0).cellIterator();
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    fieldNames.add(cell.getStringCellValue());
                }
                databaseControllerImpl.connectToDatabaseExcel();
                chooseFieldsCreationControllerImpl.setComboBoxesExcel(fieldNames);
                view.getFieldsMappingForExcel().setVisible(true);
                excelFileAddControllerImpl.progressBarSetUp();
                view.getProgressBar().setVisible(true);
                view.getFileLoadedJLabel().setVisible(true);
            } catch (InvalidFormatException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        view.getDelete_file_button().addActionListener(e -> excelFileAddControllerImpl.deleteFile());
        view.getProgressBar().addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                excelFileAddControllerImpl.progressBarSetUp();
            }
        });
        view.getLoadDataButton1().addActionListener(e -> {
            if (!databaseControllerImpl.isValid) {
                JOptionPane.showMessageDialog(null, "Error! Field 'external_id' can not be empty", "Ok", JOptionPane.ERROR_MESSAGE);
            } else {
                databaseControllerImpl.saveDataToDatabase();
                databaseControllerImpl.showDatabaseTable();
                JOptionPane.showMessageDialog(null, "Data loaded successfully", "Ok", JOptionPane.DEFAULT_OPTION);
            }
        });
        view.getLoadDataExcel().addActionListener(e -> {
            try {
                if (excelFileAddControllerImpl.selectedFile == null && !databaseControllerImpl.isValid) {
                    JOptionPane.showMessageDialog(null, "Add file or enter the correct path first", "Ok", JOptionPane.DEFAULT_OPTION);
                } else {
                    databaseControllerImpl.saveDataToDatabaseFromExcel(data);
                    databaseControllerImpl.showDatabaseTable();
                    JOptionPane.showMessageDialog(null, "Data loaded successfully", "Ok", JOptionPane.DEFAULT_OPTION);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        view.getConn_button().addActionListener(e -> {
            databaseControllerImpl.databaseConnection();
            if (databaseControllerImpl.isValid) {
                view.getConnectedPanel().setVisible(true);
                view.getSupplier().setEnabled(false);
                view.getNewSupplierBtn().setEnabled(false);
                view.getFieldsMapping().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Access denied. Please enter a valid data or fill in all required fields", "Ok", JOptionPane.DEFAULT_OPTION);
            }
        });
        view.getDisconn_button().addActionListener(e -> {
            view.getDataSourceUrlField().setVisible(true);
            view.getLogin_panel().setVisible(true);
            view.getLogin_field().setText("");
            view.getPass_field().setText("");
            view.getDataSourceUrlField().setText("");
            view.getFieldsMapping().setVisible(false);
            view.getConnectedPanel().setVisible(false);
            view.getDisconn_button().setVisible(false);
            view.getSupplier().setEnabled(true);
            view.getNewSupplierBtn().setEnabled(true);
        });
        view.getGoToDatabaseTableButton().addActionListener(actionListener1);
        view.getLogin_field().addActionListener(e -> view.getLogin_field().setBorder(null));
        view.getNewSupplierBtn().addActionListener(e -> supplierDatabaseController.addNewSupplier());
        this.view = view;
        this.comboBoxes = view.getComboBoxes();
        databaseControllerImpl = new DatabaseControllerImpl(view);
        databaseControllerImpl.showDatabaseTable();
        supplierDatabaseController = new SupplierDatabaseControllerImpl(view);
        sqlQueriesControllerImpl = new SQLQueriesControllerImpl(view);
        excelFileAddControllerImpl = new ExcelFileAddControllerImpl(view);
        chooseFieldsCreationControllerImpl = new ChooseFieldsCreationControllerImpl(view);
        view.getComboBoxDatabaseName().addActionListener(e -> {
            try {
                Connection con = DriverManager.getConnection(databaseControllerImpl.getPath(), databaseControllerImpl.getLogin(), databaseControllerImpl.getPassword());
                chooseFieldsCreationControllerImpl.resetFieldMappingComboboxes(con, (String) ((JComboBox) e.getSource()).getSelectedItem(), comboBoxes);
                databaseControllerImpl.setDatabaseTableName((String) ((JComboBox) e.getSource()).getSelectedItem());
                con.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        view.getGoToDatabaseExcel().addActionListener(e -> view.getTabbedPane1().setSelectedIndex(3));
        view.getExtractDataToCSVButton().addActionListener(e -> {
            databaseControllerImpl.exportData();
            JOptionPane.showMessageDialog(null, "CSV File is created successfully.", "Ok", JOptionPane.DEFAULT_OPTION);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
