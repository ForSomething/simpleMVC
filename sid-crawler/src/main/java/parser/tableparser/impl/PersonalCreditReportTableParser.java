package parser.tableparser.impl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.tableparser.entity.Table;
import parser.tableparser.entity.TableCell;
import parser.tableparser.entity.TableRow;
import utils.JsonUtils;

import java.io.File;
import java.util.ListIterator;


public class PersonalCreditReportTableParser {
    public static Table parse(String html)throws Exception{
        Document doc = Jsoup.parse(html);
        Elements allTrs = doc.select("tr");
        Table rootTable = new Table();
        Table currentTable = rootTable;
        for(int trIndex = 0;trIndex < allTrs.size();trIndex++){
            Element tr = allTrs.get(trIndex);
            if(tr.select("table").size() > 0){//这个判断条件需不需要是这样，还不得而知
                //如果这一行里面存在table元素，在根据目前已知的征信报告格式，我们就认为这一行是做排版用的
                //目前想到的排版行的相关操作有如下
                //1、排版行一定是涉及新table的，在此处就需要新建一个table对象，然后下面的行，一定是在这个新table中的(排版行的子元素中，只会有一个table)
                TableRow positionRow = new TableRow();
                currentTable.addRow(positionRow);
                TableCell cell = new TableCell();
                positionRow.addCell(cell);
                currentTable = new Table();
                cell.setContent(currentTable);
                continue;
            }
            //如果不是排版行则按列入表
            Elements tds = tr.select("td");
            TableRow row = new TableRow();
            currentTable.addRow(row);
            for(int tdIndex = 0;tdIndex < tds.size();tdIndex++){
                Element td = tds.get(tdIndex);
                TableCell cell = new TableCell();
                row.addCell(cell);
                cell.setContent(td.text());
            }
            if(tr.lastElementSibling() == tr){
                //如果自己是最后一个节点，那么说明子表格已经遍历结束，要切换回根表格
                currentTable = rootTable;
            }
        }
        explain(rootTable);
        String sdfsdf = JsonUtils.parse2Json(rootTable);
        FileUtils.write(new File("E:\\work\\zhixiaoyinhang\\文档\\人行\\征信中心爬虫\\parsedJson.json"),sdfsdf);
        return rootTable;
    }

    public static void explain(Table table){

    }
}
