import subd.DB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        DB db_ = new DB("C:\\IdeaProjects\\Lili_db\\db\\db.bin");

        printInfo();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        input = br.readLine();
        while (!input.equals("exit")) {
            if (input.length() > 0) {
                db_.execute(input);
                db_.printLogs();
            }
            input = br.readLine();
        }
        br.close();
    }

    private static void printInfo() {
        System.out.println("Allowed command:");
        System.out.println("CREATE TABLE table_name (column1 integer, column2 string, column3 real);");
        System.out.println("INSERT INTO table_name (column1, column2, column3) VALUES (10, value2, 1.2);");
        System.out.println("SELECT column1, * FROM table_name WHERE column1 = 10; (підтримуються тільки оператори: = AND)");
        System.out.println("UPDATE table_name SET column1 = 11 WHERE column3 = 1.2; (підтримуються тільки оператори: = AND)");
        System.out.println("DELETE FROM table_name WHERE column1 = 10; (підтримуються тільки оператори: = AND)");
        System.out.println("DROP TABLE table_name;");
        System.out.println("exit");
        System.out.println("");
        System.out.println("PRINT COMMAND:");
    }
}


