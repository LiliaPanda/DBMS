package subd.entities;

import subd.annotation.JsonFieldName;

import java.util.ArrayList;
import java.util.HashMap;

public class Table {
    @JsonFieldName(fieldName = "tableName")
    public String tableName;
    @JsonFieldName(fieldName = "celHead")
    public HashMap<String, CellHead> celHeads;
    @JsonFieldName(fieldName = "tableRows")
    public ArrayList<TableRow> tableRows;

    public Table(String tableName) {
        this.tableName = tableName;
        celHeads = new HashMap<>();
        tableRows = new ArrayList<>();
    }


}
