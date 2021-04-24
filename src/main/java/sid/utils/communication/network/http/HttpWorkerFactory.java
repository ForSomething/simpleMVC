package sid.utils.communication.network.http;

import java.util.HashMap;
import java.util.Map;

public class HttpWorkerFactory {
    private static Map<String,HttpWorker> workerMap;

    static {
        workerMap = new HashMap<>();
//        workerMap.put("BrowserControllerWorker",new BrowserControllerWorker());
        workerMap.put("CustomWorker",new CustomWorker());
    }

    public static HttpWorker getWorker(String workerName){
        return workerMap.get(workerName);
    }
}
