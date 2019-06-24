package pl.patrycjamecina.controller;
import javax.swing.JComboBox;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
public interface ChooseFieldsCreationController {
    Map<String, String> getFieldTypeByNames(ResultSet rs) throws SQLException;
    void initFieldsToMap(Map<String, String> internalFieldTypeByNames);
    void initFieldsToMapExcel(Map<String, String> internalFieldTypeByNames);
    void setComboBoxes(Map<String, String> externalFieldTypeByNames);
    void setComboBoxesExcel(List<String> externalFieldNames);
    List<JComboBox> getSelectedComboBoxes();
    List<JComboBox> getSelectedComboBoxesExcel();
    void resetFieldMappingComboboxes(Connection con, String tableName, JComboBox[] comboBoxes) throws SQLException;
    Map<String, String> getFieldNamesMap(Connection con, String tableName) throws SQLException;
    void display(Map<String, String> fieldNamesMap);
}
