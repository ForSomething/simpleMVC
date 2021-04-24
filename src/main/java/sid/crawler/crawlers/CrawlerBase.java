package sid.crawler.crawlers;

import sid.utils.pool.ObjectPool;
import sid.utils.communication.network.http.HttpWorker;
import sid.utils.communication.network.http.HttpWorkerFactory;
import sid.utils.communication.network.http.Request;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class CrawlerBase {
    private static ThreadPoolExecutor threadPoolExecutor;

    private static ObjectPool<HttpWorker> browserControllerWorkerPool;

    static String pooledWorkerName = "BrowserControllerWorker";

    static {
        threadPoolExecutor = new ThreadPoolExecutor(50,50,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());

        browserControllerWorkerPool = new ObjectPool<HttpWorker>(8) {
            @Override
            protected HttpWorker newInstance() {
                HttpWorker worker = HttpWorkerFactory.getWorker(pooledWorkerName);
                return (HttpWorker) Proxy.newProxyInstance(HttpWorker.class.getClassLoader(),new Class[]{HttpWorker.class},
                        (obj,method,args)->{
                            if(!"close".equals(method.getName())){
                                return method.invoke(worker, args);
                            }else{
                                browserControllerWorkerPool.returnInstance((HttpWorker)obj);
                                return null;
                            }
                        }
                );
            }
        };
    }

    protected HttpWorker getHttpWorker(String workerName){
        if(pooledWorkerName.equals(workerName)){
            return browserControllerWorkerPool.blockingGet();
        }
        return HttpWorkerFactory.getWorker(workerName);
    }

    protected void doRequestAsyn(test testfunc){

    }

    public abstract void start(Map<String,Object> param);
}

@FunctionalInterface
interface RequestFinishCallBack{
    void execute(HttpWorker worker,Object param);
}

interface test{
    void execute(Request request);
}
