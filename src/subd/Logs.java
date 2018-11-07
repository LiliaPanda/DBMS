package subd;

import java.util.LinkedList;

public class Logs {
    public static boolean SHOW_EXCEPTION = true;

    private boolean done = true;
    LinkedList<String> logs = new LinkedList<>();

    public boolean isError() {
        return done;
    }

    public void error() {
        done = false;
    }

    public void clear() {
        logs.clear();
        done = true;
    }

    public void addBadRequest(String part) {
        logs.add("Bad request '" + part + "'");
    }

    public void add(String log) {
        logs.add(log);
    }

    public void print() {
        if (done) {
            printLnG("Success");
            for (int i = 0; i < logs.size(); i++) {
                printLnN(logs.get(i));
            }
        } else {
            printLnR("Error");
            for (int i = 0; i < logs.size(); i++) {
                printLnN(logs.get(i));
            }
        }
    }

    //--N
    public static void printLnN(String text) {
        printN(text + "\r\n");
    }

    public static void printN(String text) {
        print(text, StyleText.REGULAR, NormColorText.GRAY);
    }

    //--G
    public static void printLnG(String text) {
        printG(text + "\r\n");
    }

    public static void printG(String text) {
        print(text, StyleText.REGULAR, NormColorText.GREEN);
    }

    //--R
    public static void printLnR(String text) {
        printR(text + "\r\n");
    }

    public static void printR(String text) {
        print(text, StyleText.BOLD, NormColorText.RED);
    }

    private static void print(String text, StyleText styleText, NormColorText normColorText) {
        System.out.print(String.format("\033[%s;%s%s%s", styleText.value, normColorText.value, text, RESET));
    }

    public static final String RESET = "\033[0m";  // Text Reset

    private static enum NormColorText {
        GRAY(      0, "Gray",    "0m"),
        BLACK(     0, "Black",   "30m"),
        RED(       0, "Red",     "31m"),
        GREEN(     0, "Green",   "32m"),
        YELLOW(    0, "Yellow",  "33m"),
        BLUE(      0, "Blue",    "34m"),
        PURPLE(    0, "Purple",  "35m"),
        CYAN(      0, "Cyan",    "36m"),
        WHITE(     0, "White",   "37m");

        int id;
        String name;
        String value;

        NormColorText(int id, String name, String value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }

    private static enum StyleText {
        REGULAR(1, "Regular", "0"),
        BOLD(1, "Bold", "1"),
        UNDERLINE(1, "Underline", "4");

        int id;
        String name;
        String value;

        StyleText(int id, String name, String value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }

}
