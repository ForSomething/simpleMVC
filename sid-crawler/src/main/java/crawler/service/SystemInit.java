package crawler.service;

import crawler.crawlers.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Component
public class SystemInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        testFunction();
    }


    private void testFunction(){
        try{
//            int fetchRight = 1;
//            String item = "<dt></dt>";
//            Date dfsdf = new Date(){
//                String text = "text";
//
//                public String getText() {
//                    return text;
//                }
//
//                public void setText(String text) {
//                    this.text = text;
//                }
//            };
//            HashMap<String,Object> env = new HashMap<>();
//            env.put("dfsdf",dfsdf);
//            Object sdfsdfsdfsdf = ScriptUtils.execute("dfsdf.getText",env);
//            Document doc = Jsoup.parse("","utf-8");
//            Element element = doc.selectFirst("");
//            element.parent();
//            String sdfsd = "";
//            sdfsd += "44";
//            sdfsd += "44";
//            sdfsd += "44";
//            sdfsd += "44";
//
//            StringBuilder sb = new StringBuilder();
//            sb.insert(0,"sdf").append("tyty");
//            sb.append("sdf");
//            sb.append("sdf");
//            sb.append("sdf");
//            sb.append("sdf");
//            sb.append("sdf");sb.append("sdf");
//            sb.append("sdf");
//            sb.append("sdf");
//            sb.append("sdf");
//            FileUtils.ReadLinesFromFile(sb.toString());
//            ArrayList<String> list = new ArrayList<>();
//            String[] stringArr = {"111","222"};
//            Object[] objArr = Arrays.copyOf(stringArr, stringArr.length, Object[].class);
//            String sdfsdf = "";
            BisiCrawler.start();
            System.out.print("crawling finish");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
