package pl.patrycjamecina.controller;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
public interface DatabaseController {
    String getPath();
    String getLogin();
    String getPassword();
    void databaseConnection();
    List<String> getTableNames(Connection con) throws SQLException;
    void saveDataToDatabase();
    void saveDataToDatabaseFromExcel(List<XSSFRow> data) throws SQLException;
    void showDatabaseTable();
    void connectToDatabaseExcel() throws SQLException;
    void exportData();
    void setDatabaseTableName(String databaseTableName);
}
