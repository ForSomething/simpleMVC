package systemInit;

import common.constvaslue.Events;
import eventManegement.EventManeger;
import toolroom.FileUtils;
import toolroom.JsonUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SystemInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try{
            InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("event.conf");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));
            String line;
            while (true){
                if((line = reader.readLine()) == null){
                    break;
                }
                String[] eventParam = line.split("->");
                Map<String,Object> mathodInfoMap = JsonUtils.parseJsonString2Map(eventParam[1]);
                List<String> paramterList = (List<String>)mathodInfoMap.get("paramter");
                Class[] methodParamClassesArray = new Class[paramterList.size()];
                for(int index = 0;index < paramterList.size();index++){
                    methodParamClassesArray[index] = Class.forName(Events.class.getCanonicalName());
                }
                Events events = Events.valueOf(eventParam[0].substring(eventParam[0].lastIndexOf('.') + 1));
                Class theClassMethodBelongTo = Class.forName(mathodInfoMap.get("class").toString());
                EventManeger.registerListener(events,
                        theClassMethodBelongTo.getDeclaredMethod(mathodInfoMap.get("method").toString(),methodParamClassesArray));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
