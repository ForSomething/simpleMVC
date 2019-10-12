package crawler.crawlers;

import pool.ObjectPool;
import utils.communication.network.http.*;

import java.lang.reflect.Proxy;
import java.util.Map;

public abstract class CrawlerBase {
    private static ObjectPool<HttpWorker> browserControllerWorkerPool;

    static String pooledWorkerName = "BrowserControllerWorker";

    static {
        browserControllerWorkerPool = new ObjectPool<HttpWorker>(8) {
            @Override
            protected HttpWorker newInstance() {
                HttpWorker worker = HttpWorkerFactory.getWorker(pooledWorkerName);
                return (HttpWorker) Proxy.newProxyInstance(HttpWorker.class.getClassLoader(),worker.getClass().getInterfaces(),
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

    public HttpWorker getHttpWorker(String workerName){
        if(pooledWorkerName.equals(workerName)){
            return browserControllerWorkerPool.blockingGet();
        }
        return HttpWorkerFactory.getWorker(workerName);
    }

    abstract void start(Map<String,Object> param);
}

@FunctionalInterface
interface RequestFinishCallBack{
    void execute(Response response);
}
