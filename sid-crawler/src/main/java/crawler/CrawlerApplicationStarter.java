package crawler;

import crawler.crawlerfj.environment.IEnvironmentCore;
import crawler.crawlerfj.environment.impl.BrowserEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

//    @Bean
//    public IEnvironmentCore stringBean(){
//        return new BrowserEnvironment();
//    }
}
