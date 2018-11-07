package subd;

import subd.entities.CellBase;
import subd.entities.Where;

import java.util.LinkedList;

public class Request {
    String requestType;

    String tableName;

    LinkedList<CellBase> columns = new LinkedList<>();
    LinkedList<Where> conditions = new LinkedList<>();
}
