package subd.entities;

import subd.annotation.JsonFieldName;

import java.util.HashMap;

public class TableRow {
    @JsonFieldName(fieldName = "celValues")
    public HashMap<String, CellValue> celValues;

    public TableRow() {
        this.celValues = new HashMap<>();
    }

    public void addCel(String colName, CellValue celValue) {
        celValues.put(colName, celValue);
    }
}
