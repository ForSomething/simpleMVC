package crawlerfj.crawler.Impl.SingleStepCrawler;

import crawlerfj.crawler.ICrawlerfj;
import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleStepCrawler extends ICrawlerfj {
    private static SingleStepCrawler instance = new SingleStepCrawler();

    private SingleStepCrawler(){

    }

    public static SingleStepCrawler GetInstance(){
        return instance;
    }

    @Override
    public boolean CanHandle(Object _config) {
        return _config != null && SingleStepConfig.class.getCanonicalName().equals(_config.getClass().getCanonicalName());
    }

    @Override
    public void Crawling(Object _config) throws Exception {
        SingleStepConfig config = (SingleStepConfig)_config;
        super.executeThread(()->{
            try {
//                String uuid = UUID.randomUUID().toString().replace("-","");
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                String timeString = sdf.format(System.currentTimeMillis());
//                String logString = String.format("[%s] [%s]开始爬取，url为[%s]",timeString,uuid,singleStepConfig.getRequestEntity().getRequestURL());
//                FileUtils.GetInstance().WriteLines(new String[]{logString},"C:\\Users\\Administrator\\Desktop\\crawlerLog.txt",true);
                if(config.getBeforeCrawlingHandler() != null){
                    config.getBeforeCrawlingHandler().Excute(config);
                }
                config.getSingleStep().Execute(doRequest(config.getRequestEntity()),config);
                if(config.getAfterCrawlingHandler() != null){
                    config.getAfterCrawlingHandler().Excute(config);
                }
//                timeString = sdf.format(System.currentTimeMillis());
//                logString = String.format("[%s] [%s]爬取完成",timeString,uuid,singleStepConfig.getRequestEntity().getRequestURL());
//                FileUtils.GetInstance().WriteLines(new String[]{logString},"C:\\Users\\Administrator\\Desktop\\crawlerLog.txt",true);
            } catch (Exception e) {
                if(config.getExceptionHandler() != null){
                    config.setUserParam("exception",e);
                    config.getExceptionHandler().Excute(config);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private ResponseEntity doRequest(RequestEntity requestEntity) throws Exception {
        switch (requestEntity.getRequestMethod()){
            case GET: return HttpUtils.doGet(requestEntity);
            case POST: return HttpUtils.doPost(requestEntity);
            default: return null;
        }
    }
}
