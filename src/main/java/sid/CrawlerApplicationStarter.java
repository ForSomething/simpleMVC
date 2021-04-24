package sid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CrawlerApplicationStarter {
    public static void main(String[] args){
//        try {
//            new BisiCrawler().start(new HashMap<>());
////            PersonalCreditReportTableParser.parse(FileUtils.readFileToString(new File("E:/Temp/getQueryCreditReport.html"),"gbk"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SpringApplication.run(CrawlerApplicationStarter.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CrawlerApplicationStarter.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);
    }

//    @RequestMapping(path = "/")
//    @ResponseBody
//    public String index(){
//        return "";
//    }

//    @Bean
//    public IEnvironmentCore stringBean(){
//        return new BrowserEnvironment();
//    }
}
