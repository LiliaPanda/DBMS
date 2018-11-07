package subd;

import subd.entities.CellHead;
import subd.entities.CellSelect;
import subd.entities.CellValue;
import subd.entities.Where;

public class Parser {
    private Logs logs;
    private String query;
    private Request request;

    public Parser(Logs logs) {
        this.logs = logs;
    }

    public Request parse(String q) {
        this.query = new String(q);

        request = new Request();
        String requestType = "";

        requestType = nextPart(query, new String[]{" "}).toUpperCase();
        query = deleteFirstPart(query, new String[]{" "});

        switch (requestType) {
            case "CREATE": {
                CREATE(query);
                break;
            }
            case "INSERT": {
                INSERT(query);
                break;
            }
            case "SELECT": {
                SELECT(query);
                break;
            }
            case "UPDATE": {
                UPDATE(query);
                break;
            }
            case "DELETE": {
                DELETE(query);
                break;
            }
            case "DROP": {
                DROP(query);
                break;
            }
            default: {
                logs.add("Do not found command " + requestType);
                logs.error();
                break;
            }
        }

        return request;
    }

    private void CREATE(String query) {
        //CREATE TABLE table_name (column1 integer, column2 string, column3 real);
        request.requestType = "CREATE";

        String word = nextPart(query, new String[]{" "}).toUpperCase();
        if (word.equals("TABLE")) {
            query = deleteFirstPart(query, new String[]{" "});
            request.tableName = nextPart(query, new String[]{" "});
            query = deleteFirstPart(query, new String[]{"("});

            String row = "";
            int pos = 0;
            try {
                do {
                    row = nextPart(query, new String[]{",", ")"});

                    String colName = nextPart(row, new String[]{" "});
                    row = deleteFirstPart(row, new String[]{" "});
                    String colType = nextPart(row, new String[]{});

                    TypeCol typeCol = TypeCol.parseType(colType);
                    if (typeCol != TypeCol.NONE) {
                        request.columns.add(new CellHead(colName, pos, typeCol));
                    } else {
                        logs.error();
                        logs.add("Undefined type '" + colType + "'");
                    }

                    pos++;

                    query = deleteFirstPart(query, new String[]{",", ")"});
                } while (query.length() > 0 && !query.equals(";"));
            } catch (Exception e) {
                if (Logs.SHOW_EXCEPTION) {
                    e.printStackTrace();
                    logs.error();
                    logs.addBadRequest(query);
                }
            }
        } else {
            logs.error();
            logs.addBadRequest("TABLE");
        }
    }

    private void INSERT(String query) {
//        INSERT INTO table_name (column1, column2, column3) VALUES (10, value2, 1.2);
        request.requestType = "INSERT";

        String word = nextPart(query, new String[]{" "});
        if (word.toUpperCase().equals("INTO")) {
            try {
                query = deleteFirstPart(query, new String[]{" "});
                request.tableName = nextPart(query, new String[]{" "});
                query = deleteFirstPart(query, new String[]{"("});

                String columnsPack = "";
                String valuesPack = "";

                columnsPack = nextPart(query, new String[]{")"}) + ",";
                query = deleteFirstPart(query, new String[]{")"});

                String values = nextPart(query, new String[]{"("});
                query = deleteFirstPart(query, new String[]{"("});

                valuesPack = nextPart(query, new String[]{")"}) + ",";
                query = deleteFirstPart(query, new String[]{")"});

                if (!values.equals("VALUES")) {
                    logs.error();
                    logs.addBadRequest("VALUES");
                }

                String columnName = "";
                String columnValue = "";
                do {
                    columnName = nextPart(columnsPack, new String[]{","});
                    columnValue = nextPart(valuesPack, new String[]{","});

                    columnsPack = deleteFirstPart(columnsPack, new String[]{","});
                    valuesPack = deleteFirstPart(valuesPack, new String[]{","});

                    if (columnName.length() > 0 && columnValue.length() > 0) {
                        request.columns.add(new CellValue(columnName, clearColumnValue(columnValue)));
                    } else if (columnName.length() == 0 && columnValue.length() == 0) {
                        //end
                    } else {
                        logs.error();
                        logs.add("Count of columns does not equal count of values!!!");
                        return;
                    }
                } while ((columnsPack.length() > 0) && (valuesPack.length() > 0));
            } catch (Exception e) {
                if (Logs.SHOW_EXCEPTION) {
                    e.printStackTrace();
                    logs.error();
                    logs.addBadRequest(query);
                }
            }
        } else {
            logs.error();
            logs.addBadRequest("INTO");
        }
    }

    private void SELECT(String query) {
//        SELECT column1, column2 FROM table_name;
        request.requestType = "SELECT";

        try {
            String names = nextPart(query, new String[]{"FROM"});
            query = deleteFirstPart(query, new String[]{"FROM"});
            request.tableName = nextPart(query, new String[]{"WHERE", ";"});
            query = deleteFirstPart(query, new String[]{"WHERE", ";"});
            String wheres = nextPart(query, new String[]{";"});

            if (request.tableName.length() != 0) {
                if (names.length() != 0) {
                    int pos = 0;
                    do {
                        String columnName = nextPart(names, new String[]{","});
                        names = deleteFirstPart(names, new String[]{","});

                        if (columnName.length() > 0) {
                            request.columns.add(new CellSelect(columnName, pos));
                        } else if (columnName.length() == 0) {
                            //end
                        }
                        pos++;
                    } while ((names.length() > 0));

                } else {
                    logs.error();
                    logs.addBadRequest(query);
                    return;
                }

                //where
                parseWhereCondition(wheres);

            } else {
                logs.error();
                logs.add("Did non find table name");
            }

        } catch (Exception e) {
            if (Logs.SHOW_EXCEPTION) {
                e.printStackTrace();
            }
            logs.error();
            logs.addBadRequest(query);
        }
    }

    private void UPDATE(String query) {
        //UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;
        request.requestType = "UPDATE";
        request.tableName = nextPart(query, new String[]{"SET"});
        query = deleteFirstPart(query, new String[]{"SET"});
        if (request.tableName.length() > 0) {

            String sets = nextPart(query, new String[]{"WHERE", ";"});
            query = deleteFirstPart(query, new String[]{"WHERE", ";"});
            String wheres = nextPart(query, new String[]{";"});

            do {
                String oneSet = nextPart(sets, new String[]{",", ";"});
                sets = deleteFirstPart(sets, new String[]{",", ";"});

                String colName = nextPart(oneSet, new String[]{"="});
                oneSet = deleteFirstPart(oneSet, new String[]{"="});
                String value = nextPart(oneSet, new String[]{",", ";"});


                if (colName.length() > 0 && value.length() > 0) {
                    request.columns.add(new CellValue(colName, value));
                } else {
                    logs.error();
                    logs.addBadRequest(query);
                    return;
                }
            } while ((sets.length() > 0));

            parseWhereCondition(wheres);
        } else {
            logs.error();
            logs.add("No find table name");
        }
    }

    private void DELETE(String query) {
//        DELETE FROM table_name WHERE column1 = 10; (підтримуються тільки оператори: = AND)
        request.requestType = "DELETE";

        String word = nextPart(query, new String[]{" "});
        if (word.toUpperCase().equals("FROM")) {
            try {
                query = deleteFirstPart(query, new String[]{" "});
                request.tableName = nextPart(query, new String[]{"WHERE", ";"});
                query = deleteFirstPart(query, new String[]{"WHERE", ";"});
                String wheres = nextPart(query, new String[]{";"});

                if (request.tableName.length() != 0) {
                    //where
                    parseWhereCondition(wheres);
                } else {
                    logs.error();
                    logs.add("Did non find table name");
                }

            } catch (Exception e) {
                if (Logs.SHOW_EXCEPTION) {
                    e.printStackTrace();
                }
                logs.error();
                logs.addBadRequest(query);
            }
        } else {
            logs.error();
            logs.addBadRequest("FROM");
        }
    }

    private void DROP(String query) {
        //DROP TABLE table_name;
        request.requestType = "DROP";

        String word = nextPart(query, new String[]{" "}).toUpperCase();
        if (word.equals("TABLE")) {
            query = deleteFirstPart(query, new String[]{" "});
            request.tableName = nextPart(query, new String[]{" ", ";"});
        }
    }

    private void parseWhereCondition(String wheres) {
        if (wheres.length() != 0) {
            do {
                String where = nextPart(wheres, new String[]{"AND", "and"});
                wheres = deleteFirstPart(wheres, new String[]{"AND", "and"});

                String colName = nextPart(where, new String[]{"="});
                where = deleteFirstPart(where, new String[]{"="});
                String condition = "=";
//                String condition = nextPart(where, new String[]{" "});
//                where = deleteFirstPart(where, new String[]{" "});
                String value = clearColumnValue(nextPart(where, new String[]{" ", ";"}));

                if (colName.length() > 0 && condition.length() > 0 && value.length() > 0) {
                    request.conditions.add(new Where(colName, condition, value));
                } else {
                    logs.error();
                    logs.add("Bad value in WHERE condition");
                    return;
                }
            } while ((wheres.length() > 0));
        }
    }

    private String nextPart(String query, String[] separators) {
        return query.substring(0, firstIndex(query, separators)[0]).trim();
    }

    private String deleteFirstPart(String query, String[] separators) {
        int[] resp = firstIndex(query, separators);
        return query.substring(resp[0] + resp[1]).trim();
    }

    private int[] firstIndex(String query, String[] separators) {
        int min = query.length();
        int lengthSeparator = 0;
        if (separators.length > 0) {
            for (int i = 0; i < separators.length; i++) {
                int curIndex = query.indexOf(separators[i]);
                if (curIndex < min && curIndex != -1) {
                    min = curIndex;
                    lengthSeparator = separators[i].length();
                }
            }
        } else {
            min = query.length();
        }
        return new int[]{min, lengthSeparator};
    }

    private String clearColumnValue(String value) {
        if (value.indexOf("'") == 0) {
            value = value.substring(1);
        }
        if (value.indexOf("'") == value.length() - 1) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

}
