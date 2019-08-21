package parser.tableparser.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.tableparser.entity.Table;


public class PersonalCreditReportTableParser {
    public Table parse(String html){
        Document doc = Jsoup.parse(html);
        Elements tabels = doc.select("table");
        return null;
    }
}
