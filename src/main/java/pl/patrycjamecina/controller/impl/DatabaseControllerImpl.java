package pl.patrycjamecina.controller.impl;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pl.patrycjamecina.controller.DatabaseController;
import pl.patrycjamecina.view.View;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class DatabaseControllerImpl implements DatabaseController {
    private View view;
    private SupplierDatabaseControllerImpl supplierDatabaseController;
    private SQLQueriesControllerImpl sqlQueriesControllerImpl;
    private ChooseFieldsCreationControllerImpl chooseFieldsCreationControllerImpl;
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/medicines";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static final String BASE_TABLE = "DATABASE";
    private String databaseTableName = "";
    public boolean isValid;

    public DatabaseControllerImpl(final View view) {
        this.view = view;
        supplierDatabaseController = new SupplierDatabaseControllerImpl(view);
        sqlQueriesControllerImpl = new SQLQueriesControllerImpl(view);
        chooseFieldsCreationControllerImpl = new ChooseFieldsCreationControllerImpl(view);
    }

    @Override
    public String getPath() {
        String fieldText = view.getDataSourceUrlField().getText().trim();
        return fieldText;
    }

    @Override
    public String getLogin() {
        String loginText = view.getLogin_field().getText().trim();
        return loginText;
    }

    @Override
    public String getPassword() {
        String passText = view.getPass_field().getText().trim();
        return passText;
    }

    @Override
    public void databaseConnection() {
        if (getPath() == null || getPath().equals("") || view.getSupplier().getSelectedItem().equals("")) {
            isValid = false;
            JOptionPane.showMessageDialog(null, "Please enter a valid data or fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Connection con = DriverManager.getConnection(getPath(), getLogin(), getPassword());
                Connection con1 = DriverManager.getConnection(DB_URL, USER, PASS);
                if (con.isValid(10000)) {
                    JOptionPane.showConfirmDialog(null, "Connection success!", "Ok", JOptionPane.DEFAULT_OPTION);
                    view.getLogin_panel().setVisible(false);
                    view.getDisconn_button().setVisible(true);
                    isValid = true;
                    List<String> tableNames = getTableNames(con);
                    tableNames.add(0, "");
                    view.getComboBoxDatabaseName().setModel(new DefaultComboBoxModel(tableNames.toArray()));
                    Map<String, String> fieldTypeByNames = chooseFieldsCreationControllerImpl.getFieldNamesMap(con1, BASE_TABLE);
                    Map<String, String> comboboxesTypeByNames = chooseFieldsCreationControllerImpl.getFieldNamesMap(con, (String) view.getComboBoxDatabaseName().getSelectedItem());
                    fieldTypeByNames.remove("id");
                    fieldTypeByNames.remove("supplier_id");
                    chooseFieldsCreationControllerImpl.initFieldsToMap(fieldTypeByNames);
                    chooseFieldsCreationControllerImpl.setComboBoxes(comboboxesTypeByNames);
                }
                supplierDatabaseController.getSupplierId();
                con.close();
            } catch (Exception e) {
                System.out.println("ActionListenersControllerImpl.databaseConnection(): " + e.getMessage());
            }
        }
    }

    @Override
    public List<String> getTableNames(Connection con) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        ResultSet rs = con.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
        while (rs.next()) {
            String name = rs.getString(3);
            tableNames.add(name);
        }
        return tableNames;
    }

    @Override
    public void saveDataToDatabase() {
        Connection con = null;
        Connection con1 = null;
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            con1 = DriverManager.getConnection(getPath(), getLogin(), getPassword());
            List<JComboBox> comboBoxes = chooseFieldsCreationControllerImpl.getSelectedComboBoxes();
            Map<String, String> fieldMap = new HashMap<>();
            for (JComboBox cb : comboBoxes) {
                fieldMap.put(cb.getName(), ((String) cb.getSelectedItem()).split(" ")[0]);
            }
            fieldMap.put("supplier_id", ("" + supplierDatabaseController.supplierId()));
            String sql = "INSERT INTO " + BASE_TABLE + " (" + String.join(",", fieldMap.keySet()) + ") values (" + String.join(",", Collections.nCopies(fieldMap.size(), "?")) + ")";
            Statement statement = con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sqlQueriesControllerImpl.buildSqlQuery(new ArrayList<>(fieldMap.values()), databaseTableName));
            while (rs.next()) {
                int i = 1;
                for (String key : fieldMap.keySet()) {
                    pstmt.setObject(i, rs.getObject(fieldMap.get(key)));
                    i++;
                    if (fieldMap.get("external_id") == null) {
                        isValid = false;
                    }
                }
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            isValid = false;
            System.out.println("Could not get JDBC connection: " + e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                con1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveDataToDatabaseFromExcel(List<XSSFRow> data) throws SQLException {
        Connection con = null;
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            List<JComboBox> comboBoxes = chooseFieldsCreationControllerImpl.getSelectedComboBoxesExcel();
            Map<String, String> fieldMap = new HashMap<>();
            for (JComboBox cb : comboBoxes) {
                fieldMap.put(cb.getName(), ((String) cb.getSelectedItem()));
            }
            fieldMap.put("supplier_id", "99"/*("" + supplierDatabaseController.supplierId())*/);
            String sql = "INSERT INTO " + BASE_TABLE + " (" + String.join(",", fieldMap.keySet()) + ") values (" + String.join(",", Collections.nCopies(fieldMap.size(), "?")) + ")";
            PreparedStatement pstmt = con.prepareStatement(sql);
            Map<String, Integer> indexByName = new HashMap<>();
            Iterator cells = data.get(0).cellIterator();
            while (cells.hasNext()) {
                XSSFCell cell = (XSSFCell) cells.next();
                indexByName.put(cell.getStringCellValue(), cell.getAddress().getColumn());
            }
            for (int j = 1; j < data.size(); j++) {
                int i = 1;
                for (String key : fieldMap.keySet()) {
                    String value;
                    if (!key.equals("supplier_id")) {
                        XSSFCell cell = data.get(j).getCell(indexByName.get(fieldMap.get(key)));
                        cell.setCellType(CellType.STRING);
                        value = cell == null ? "" : cell.getStringCellValue();
                    } else {
                        value = fieldMap.get("supplier_id");
                    }
                    value = value.replace(",", "");
                    pstmt.setObject(i, value);
                    i++;
                }
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Could not get JDBC connection: " + e);
            JOptionPane.showMessageDialog(null, "Error! Data conversion error", "Ok", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showDatabaseTable() {
        try {
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Map<String, String> fieldTypeByNames = chooseFieldsCreationControllerImpl.getFieldNamesMap(con, BASE_TABLE);
            fieldTypeByNames.remove("id");
            fieldTypeByNames.remove("supplier_id");
            supplierDatabaseController.getSuppliers(con);
            Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(sqlQueriesControllerImpl.buildSqlQuery(BASE_TABLE));
            ResultSetMetaData rsmd = rs.getMetaData();
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                model.addColumn(rsmd.getColumnName(i));
            }
            while (rs.next()) {
                int s1 = rs.getInt(1);
                String s2 = rs.getString(2);
                String s3 = rs.getString(3);
                String s4 = rs.getString(4);
                String s5 = rs.getString(5);
                String s6 = rs.getString(6);
                String s7 = rs.getString(7);
                int s8 = rs.getInt(8);
                int s9 = rs.getInt(9);
                int s10 = rs.getInt(10);
                int s11 = rs.getInt(11);
                int s12 = rs.getInt(12);
                model.addRow(new Object[]{s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12});
            }
            view.getTable1().setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectToDatabaseExcel() throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
        Map<String, String> fieldNamesMap = chooseFieldsCreationControllerImpl.getFieldNamesMap(con, BASE_TABLE);
        fieldNamesMap.remove("id");
        fieldNamesMap.remove("supplier_id");
        chooseFieldsCreationControllerImpl.initFieldsToMapExcel(fieldNamesMap);
        con.close();
    }

    @Override
    public void exportData() {
        Statement stmt;
        String query;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formatDateTime = now.format(formatter);
        String filename = "C:\\Users\\mpatr\\OneDrive\\Desktop\\exports\\DataMatcherExport" + formatDateTime + ".csv";
        try {
            FileWriter fw = new FileWriter(filename);
            LocalDateTime today = LocalDateTime.now();
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sqlQueriesControllerImpl.buildSqlQuery(BASE_TABLE));
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= 12; i++) {
                String name = rsmd.getColumnName(i);
                fw.write(name);
                fw.append(',');
            }
            fw.write("\n");
            while (rs.next()) {
                fw.append(rs.getString(1));
                fw.append(',');
                fw.append(rs.getString(2));
                fw.append(',');
                fw.append(rs.getString(3));
                fw.append(',');
                fw.append(rs.getString(4));
                fw.append(',');
                fw.append(rs.getString(5));
                fw.append(',');
                fw.append(rs.getString(6));
                fw.append(',');
                fw.append(rs.getString(7));
                fw.append(',');
                fw.append(rs.getString(8));
                fw.append(',');
                fw.append(rs.getString(9));
                fw.append(',');
                fw.append(rs.getString(10));
                fw.append('\n');
                fw.flush();
            }
            System.out.println("CSV File is created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            stmt = null;
        }
    }
}
