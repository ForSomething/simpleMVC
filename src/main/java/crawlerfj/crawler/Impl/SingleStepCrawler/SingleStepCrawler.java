package crawlerfj.crawler.Impl.SingleStepCrawler;

import crawlerfj.crawler.ICrawlerfj;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleStepCrawler extends ICrawlerfj {
    private ThreadPoolExecutor threadPoolExecutor;

    private static SingleStepCrawler instance = new SingleStepCrawler();

    private SingleStepCrawler(){
        threadPoolExecutor = new ThreadPoolExecutor(50,50,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
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
        threadPoolExecutor.execute(new StepExecuteRunnable(config,this));
    }

    private ResponseEntity DoRequestBySuper(RequestEntity requestEntity) throws Exception {
        return super.DoRequest(requestEntity);
    }

    private class StepExecuteRunnable implements Runnable{
        SingleStepConfig singleStepConfig;

        SingleStepCrawler crawler;

        public StepExecuteRunnable(SingleStepConfig singleStepConfig,SingleStepCrawler crawler){
            this.crawler = crawler;
            this.singleStepConfig = singleStepConfig;
        }

        @Override
        public void run() {
            try {
//                String uuid = UUID.randomUUID().toString().replace("-","");
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                String timeString = sdf.format(System.currentTimeMillis());
//                String logString = String.format("[%s] [%s]开始爬取，url为[%s]",timeString,uuid,singleStepConfig.getRequestEntity().getRequestURL());
//                FileUtils.GetInstance().WriteLines(new String[]{logString},"C:\\Users\\Administrator\\Desktop\\crawlerLog.txt",true);
                if(singleStepConfig.getBeforeCrawlingHandler() != null){
                    singleStepConfig.getBeforeCrawlingHandler().Excute(singleStepConfig);
                }
                singleStepConfig.getSingleStep().Execute(crawler.DoRequestBySuper(singleStepConfig.getRequestEntity()),singleStepConfig);
                if(singleStepConfig.getAfterCrawlingHandler() != null){
                    singleStepConfig.getAfterCrawlingHandler().Excute(singleStepConfig);
                }
//                timeString = sdf.format(System.currentTimeMillis());
//                logString = String.format("[%s] [%s]爬取完成",timeString,uuid,singleStepConfig.getRequestEntity().getRequestURL());
//                FileUtils.GetInstance().WriteLines(new String[]{logString},"C:\\Users\\Administrator\\Desktop\\crawlerLog.txt",true);
            } catch (Exception e) {
                if(singleStepConfig.getExceptionHandler() != null){
                    singleStepConfig.setUserParam("exception",e);
                    singleStepConfig.getExceptionHandler().Excute(singleStepConfig);
                }else{
                    e.printStackTrace();
                }
            }
        }
    }
}
