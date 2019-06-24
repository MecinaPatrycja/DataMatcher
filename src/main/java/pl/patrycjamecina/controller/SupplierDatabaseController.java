package pl.patrycjamecina.controller;
import java.sql.Connection;
import java.sql.SQLException;
public interface SupplierDatabaseController {
    void addNewSupplier();
    void getSupplierId();
    int supplierId();
    void getSuppliers(Connection con) throws SQLException;
}
