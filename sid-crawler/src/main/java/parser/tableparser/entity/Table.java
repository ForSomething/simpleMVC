package parser.tableparser.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Table {

    protected class Cell<T>{
        Map<String,Cell> around = new HashMap<>();

        private T content;

        protected T getCellContent(){
            return content;
        }
    }
}