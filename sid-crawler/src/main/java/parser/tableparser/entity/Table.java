package parser.tableparser.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Table {
    List<TableRow> rows = new LinkedList<>();

    public void addRow(TableRow row){
        rows.add(row);
    }
}