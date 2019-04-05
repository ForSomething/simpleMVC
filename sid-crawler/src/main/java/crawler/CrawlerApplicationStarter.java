package crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class CrawlerApplicationStarter {
    public static void main(String[] args){
        SpringApplication.run(CrawlerApplicationStarter.class, args);
    }

//    @RequestMapping(path = "/")
//    @ResponseBody
//    public String index(){
//        return "";
//    }
}
