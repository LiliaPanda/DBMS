package subd.entities;

public class CellSelect extends CellBase {
    public String celHeadName;
    public int position;

    public CellSelect(String celHeadName, int position) {
        this.celHeadName = celHeadName;
        this.position = position;
    }
}
