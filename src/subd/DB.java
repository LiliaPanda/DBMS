package subd;

import org.json.JSONArray;
import org.json.JSONObject;
import subd.annotation.JsonFieldName;
import subd.annotation.analyzers.JsonFieldNameAnalyzer;
import subd.entities.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class DB {
    private String dbFile;
    private Logs logs;
    private Parser parser;
    @JsonFieldName(fieldName = "tables")
    public HashMap<String, Table> tables;

    public DB(String file) {
        logs = new Logs();
        parser = new Parser(logs);
        tables = new HashMap<>();

        dbFile = file;
        try {
            File f = new File(dbFile);
            if (f.exists() || !f.isDirectory()) {
                File ff = new File(dbFile);
                ff.getParentFile().mkdirs();
                ff.createNewFile();
            }

            byte[] encoded = Files.readAllBytes(Paths.get(file));
            parseDBFromFile(new String(encoded));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDBFromFile(String db) {
        if (db.length() > 0) {
            JSONObject joDb = new JSONObject(db);

            JSONObject joTables = joDb.getJSONObject("tables");
            Iterator<String> keys = joTables.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (joTables.get(key) instanceof JSONObject) {
                    JSONObject joTable = joTables.getJSONObject(key);
                    String tableName = joTable.getString("tableName");
                    JSONObject joCelHead = joTable.getJSONObject("celHead");
                    JSONArray tableRows = joTable.getJSONArray("tableRows");
                    Table table = new Table(tableName);

                    Iterator<String> celNames = joCelHead.keys();
                    while (celNames.hasNext()) {
                        String cName = celNames.next();
                        JSONObject joCell = joCelHead.getJSONObject(cName);

                        String name = joCell.getString("name");
                        int position = joCell.getInt("position");
                        String type = joCell.getString("type");
                        CellHead cellHead = new CellHead(name, position, TypeCol.parseType(type));
                        table.celHeads.put(name, cellHead);
                    }

                    for (int i = 0; i < tableRows.length(); i++) {
                        TableRow tableRow = new TableRow();
                        JSONObject joRow = tableRows.getJSONObject(i);
                        JSONObject joCelValues = joRow.getJSONObject("celValues");

                        Iterator<String> celDatas = joCelValues.keys();
                        while (celDatas.hasNext()) {
                            String cName = celDatas.next();
                            JSONObject joCelValue = joCelValues.getJSONObject(cName);
//                            String celHeadName = joCelValue.getString("celHeadName");
                            String value = joCelValue.getString("value");
                            tableRow.celValues.put(cName, new CellValue(value));
                        }
                        table.tableRows.add(tableRow);
                    }

                    tables.put(tableName, table);
                }
            }
        }
    }

    public void execute(String query) {
        logs.clear();
        Request request = parser.parse(query);
        if (!logs.isError()) return;
        exequteRequest(request);
        if (!logs.isError()) return;
        saveToFile();
    }

    private void exequteRequest(Request request) {
        try {
            switch (request.requestType) {
                case "CREATE": {
                    CREATE(request.tableName, (LinkedList<CellHead>) (LinkedList<?>) request.columns);
                    break;
                }
                case "INSERT": {
                    INSERT(request.tableName, (LinkedList<CellValue>) (LinkedList<?>) request.columns);
                    break;
                }
                case "SELECT": {
                    SELECT(request.tableName, (LinkedList<CellSelect>) (LinkedList<?>) request.columns, request.conditions);
                    break;
                }
                case "UPDATE": {
                    UPDATE(request.tableName, (LinkedList<CellValue>) (LinkedList<?>) request.columns, request.conditions);
                    break;
                }
                case "DELETE": {
                    DELETE(request.tableName, request.conditions);
                    break;
                }
                case "DROP": {
                    DROP(request.tableName);
                    break;
                }
            }
        } catch (Exception e) {
            if (Logs.SHOW_EXCEPTION) {
                e.printStackTrace();
            }
        }
    }

    private void CREATE(String tableName, LinkedList<CellHead> parameters) {
        if (tables.containsKey(tableName)) {
            logs.error();
            logs.add("Table with that name already created!!!");
            return;
        }

        Table newTable = new Table(tableName);
        for (CellHead cellHead : parameters) {
            newTable.celHeads.put(cellHead.name, cellHead);
        }

        tables.put(tableName, newTable);
    }

    private void INSERT(String tableName, LinkedList<CellValue> parameters) {
        Table table = tables.get(tableName);
        if (table == null) {
            logs.error();
            logs.add("Not found table with that name.");
            return;
        }

        TableRow row = new TableRow();
        CellHead cellHead = null;
        for (int i = 0; i < parameters.size(); i++) {
            CellValue celValue = parameters.get(i);
            cellHead = table.celHeads.get(celValue.celHeadName);
            if (cellHead != null) {
                if (TypeCol.validate(cellHead.typeT, celValue.value)) {
                    row.addCel(cellHead.name, new CellValue(celValue.value));
                } else {
                    logs.error();
                    logs.add("Value is not valid '" + celValue.value + "'");
                    return;
                }
            } else {
                logs.error();
                logs.add("Do not find column with name '" + celValue.celHeadName + "'");
                return;
            }
        }
        table.tableRows.add(row);
    }

    private void SELECT(String tableName, LinkedList<CellSelect> parameters, LinkedList<Where> conditions) {
        Table table = tables.get(tableName);
        if (table == null) {
            logs.error();
            logs.add("Not found table with that name.");
            return;
        }

        LinkedList<CellSelect> paramNew = new LinkedList<>();
        for (CellSelect celReq : parameters) {
            if (celReq.celHeadName.equals("*")) {
                int lastNumber = -1;
                String curKey = "";
                for (Map.Entry<String, CellHead> e : table.celHeads.entrySet()) {
                    int min = 9999;

                    for (Map.Entry<String, CellHead> entry : table.celHeads.entrySet()) {
                        String key = entry.getKey();
                        CellHead value = entry.getValue();
                        if (value.position < min && value.position > lastNumber) {
                            curKey = key;
                            min = value.position;
                        }
                    }
                    lastNumber = min;
                    paramNew.add(new CellSelect(curKey, 0));
                }
            } else {
                paramNew.add(celReq);
            }
        }

        //print header
        StringBuilder sRows = new StringBuilder();
        logs.add(printLine(paramNew.size()));
        for (CellSelect celReq : paramNew) {
            sRows.append(String.format("|%20s ", celReq.celHeadName));
        }
        sRows.append("|");
        logs.add(sRows.toString());
        logs.add(printLine(paramNew.size()));

        //print data
        for (TableRow row : table.tableRows) {
            if (checkConditions(table, row, conditions)) {
                sRows = new StringBuilder();
                for (CellSelect celReq : paramNew) {
                    sRows.append(String.format("|%20s ", row.celValues.get(celReq.celHeadName).value));
                }
                sRows.append("|");
                logs.add(sRows.toString());
                logs.add(printLine(paramNew.size()));
            }
        }
    }

    private void UPDATE(String tableName, LinkedList<CellValue> parameters, LinkedList<Where> conditions) {
        Table table = tables.get(tableName);
        if (table == null) {
            logs.error();
            logs.add("Not found table with that name.");
            return;
        }

        for(TableRow tableRow : table.tableRows) {
            if (checkConditions(table, tableRow, conditions)) {
                for(CellValue param : parameters) {
                    tableRow.celValues.put(param.celHeadName, param);
                }
            }
        }
    }

    private void DELETE(String tableName, LinkedList<Where> conditions) {
        Table table = tables.get(tableName);
        if (table == null) {
            logs.error();
            logs.add("Not found table with that name.");
            return;
        }

        for (Iterator<TableRow> it = table.tableRows.iterator(); it.hasNext();) {
            if (checkConditions(table, it.next(), conditions)) {
                it.remove();
            }
        }
    }

    private void DROP(String tableName) {
        tables.remove(tableName);
    }

    private boolean checkConditions(Table table, TableRow tableRow, LinkedList<Where> conditions) {
        boolean allIsOk = true;
        for (Where where : conditions) {
            if (where.condition.equals("=")) {
                if (tableRow.celValues.get(where.celHead).value.equals(where.value)) {
                    allIsOk = true;
                } else {
                    allIsOk = false;
                    break;
                }
            }
        }
        return allIsOk;
    }

    private String printLine(int countColumns) {
        StringBuilder respData = new StringBuilder();
        respData.append("+");
        for (int i = 0; i < countColumns; i++) {
            respData.append(new String(new char[21]).replace('\0', '-'));
            respData.append("+");
        }
        return respData.toString();
    }

    private boolean saveToFile() {
        try {
            JsonFieldNameAnalyzer analyzer = new JsonFieldNameAnalyzer();
            JSONObject table = analyzer.toJson(this);
            BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile));
            writer.write(table.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void printLogs() {
        logs.print();
    }

}
