package subd.entities;

import subd.TypeCol;
import subd.annotation.JsonFieldName;

public class CellHead extends CellBase {
    @JsonFieldName(fieldName = "name")
    public String name;
    @JsonFieldName(fieldName = "position")
    public int position;
    @JsonFieldName(fieldName = "type")
    public String type;
    public TypeCol typeT;

    public CellHead(String name, int position, TypeCol type) {
        this.name = name;
        this.position = position;
        this.typeT = type;
        this.type = type.toString();
    }
}
