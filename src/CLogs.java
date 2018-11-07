public class CLogs {

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    public void testColors() {
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 10; i++) {
                System.out.println("\033["+j+";"+i+"m "+j+" OPOP Thext " + i);
            }
            for (int i = 30; i < 50; i++) {
                System.out.println("\033["+j+";"+i+"m "+j+" OPOP Thext " + i);
            }
            for (int i = 90; i < 110; i++) {
                System.out.println("\033["+j+";"+i+"m "+j+" OPOP Thext " + i);
            }
        }
    }

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

    private static void print(String text, StyleText styleText, NormColorText normColorText) {
        System.out.print(String.format("\033[%s;%s%s%s", styleText.value, normColorText.value, text, RESET));
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


}
