package parser.tableparser.entity;

import java.util.LinkedList;
import java.util.List;

public class TableRow {
    List<TableCell> cells = new LinkedList<>();

    public void addCell(TableCell cell){
        cells.add(cell);
    }
}
