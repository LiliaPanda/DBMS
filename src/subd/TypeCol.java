package subd;

public enum TypeCol {
    INTEGER("integer"), STRING("string"), REAL("real"), NONE("none");

    private String name;

    TypeCol(String name) {
        this.name = name;
    }

    public static TypeCol parseType(String type) {
        switch (type.toUpperCase()) {
            case "INTEGER":
                return INTEGER;
            case "STRING":
                return STRING;
            case "REAL":
                return REAL;
            default:
                return NONE;
        }
    }

    public static boolean validate(TypeCol typeCol, String value) {
        boolean typeValid = false;
        switch (typeCol) {
            case INTEGER: {
                try {
                    Integer.parseInt(value);
                    typeValid = true;
                } catch (Exception e) {
                    if (Logs.SHOW_EXCEPTION) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case REAL: {
                try {
                    Float.parseFloat(value);
                    typeValid = true;
                } catch (Exception e) {
                    if (Logs.SHOW_EXCEPTION) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case STRING: {
                typeValid = true;
                break;
            }
            default: {
                typeValid = false;
                break;
            }
        }
        return typeValid;
    }

    @Override
    public String toString() {
        return name;
    }
}
