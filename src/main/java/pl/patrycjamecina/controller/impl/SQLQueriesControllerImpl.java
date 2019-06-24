package pl.patrycjamecina.controller.impl;
import pl.patrycjamecina.controller.SQLQueriesController;
import pl.patrycjamecina.view.View;

import java.util.List;
public class SQLQueriesControllerImpl implements SQLQueriesController {
    private View view;

    public SQLQueriesControllerImpl(final View view) {
        this.view = view;
    }

    @Override
    public String buildSqlQuery(List<String> fieldNames, String tableName) {
        return "SELECT " + String.join(",", fieldNames) + " FROM " + tableName;
    }

    @Override
    public String buildSqlQuery(List<String> fieldNames, String tableName, String where) {
        return "SELECT " + String.join(",", fieldNames) + " FROM " + tableName + " WHERE " + where;
    }

    @Override
    public String buildSqlQuery(String tableName) {
        return "SELECT * FROM " + tableName;
    }
}
