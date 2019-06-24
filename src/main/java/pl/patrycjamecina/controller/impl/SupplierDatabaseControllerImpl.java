package pl.patrycjamecina.controller.impl;
import pl.patrycjamecina.controller.SupplierDatabaseController;
import pl.patrycjamecina.view.View;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
public class SupplierDatabaseControllerImpl implements SupplierDatabaseController {
    private View view;
    private ChooseFieldsCreationControllerImpl chooseFieldsCreationControllerImpl;
    private SQLQueriesControllerImpl sqlQueriesControllerImpl;
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/medicines";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static final String BASE_TABLE = "DATABASE";
    private static final String SUPPLIER_TABLE = "SUPPLIER";
    private static final String[] SUPPLIER_FIELDS = new String[]{"id", "name"};
    private int supplierId;

    public SupplierDatabaseControllerImpl(final View view) {
        this.view = view;
        this.chooseFieldsCreationControllerImpl = new ChooseFieldsCreationControllerImpl(view);
        this.sqlQueriesControllerImpl = new SQLQueriesControllerImpl(view);
    }

    @Override
    public void addNewSupplier() {
        try {
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Map<String, String> fieldTypeByNames = chooseFieldsCreationControllerImpl.getFieldNamesMap(con, SUPPLIER_TABLE);
            chooseFieldsCreationControllerImpl.display(fieldTypeByNames);
            Map<String, JTextField> textFieldsByName = view.getTextFieldsByName();
            String sql = "INSERT INTO " + SUPPLIER_TABLE + " (" + String.join(",", textFieldsByName.keySet()) + ") values (" + String.join(",", Collections.nCopies(textFieldsByName.size(), "?")) + ")";
            PreparedStatement pstmt = con.prepareStatement(sql);
            int i = 1;
            for (String key : textFieldsByName.keySet()) {
                pstmt.setObject(i, textFieldsByName.get(key).getText());
                i++;
            }
            pstmt.executeUpdate();
            getSuppliers(con);
            con.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void getSupplierId() {
        try {
            Connection baseDb = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = baseDb.prepareStatement(sqlQueriesControllerImpl.buildSqlQuery(Arrays.asList("id"), SUPPLIER_TABLE, "name = " + "'" + view.getSupplier().getSelectedItem() + "'"));
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            supplierId = rs.getInt("id");
            baseDb.close();
        } catch (Exception e) {
            System.out.println("ActionListenersControllerImpl.databaseConnection(): " + e.getMessage());
        }
    }

    @Override
    public int supplierId() {
        getSupplierId();
        return supplierId;
    }

    @Override
    public void getSuppliers(Connection con) throws SQLException {
        List<String> supplierNames = new ArrayList<>();
        supplierNames.add("");
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = statement.executeQuery(sqlQueriesControllerImpl.buildSqlQuery(Arrays.asList(SUPPLIER_FIELDS), SUPPLIER_TABLE));
        while (rs.next()) {
            supplierNames.add(rs.getString("name"));
        }
        view.getSupplier().setModel(new DefaultComboBoxModel(supplierNames.toArray()));
    }
}
