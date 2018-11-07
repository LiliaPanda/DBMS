package subd.entities;

import subd.annotation.JsonFieldName;

public class CellValue extends CellBase {
//    @JsonFieldName(fieldName = "celHeadName")
    public String celHeadName;
    @JsonFieldName(fieldName = "value")
    public String value;

    public CellValue(String celHeadName, String value) {
        this.celHeadName = celHeadName;
        this.value = value;
    }

    public CellValue(String value) {
        this.value = value;
    }
}
