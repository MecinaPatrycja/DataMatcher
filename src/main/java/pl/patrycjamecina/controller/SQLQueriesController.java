package pl.patrycjamecina.controller;
import java.util.List;
public interface SQLQueriesController {
    String buildSqlQuery(List<String> fieldNames, String tableName);
    String buildSqlQuery(List<String> fieldNames, String tableName, String where);
    String buildSqlQuery(String tableName);
}
