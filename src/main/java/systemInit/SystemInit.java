package systemInit;

import common.constvaslue.Events;
import eventManegement.EventManeger;
import toolroom.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
                String[] eventParam = line.split(":");
                String[] methodParam = eventParam[1].split("->");
                String[] methodParamArray = methodParam[1].substring(methodParam[1].indexOf('(') + 1,methodParam[1].indexOf(')')).split(",");
                Class[] methodParamClassesArray = new Class[methodParamArray.length];
                for(int index = 0;index < methodParamArray.length;index++){
                    methodParamClassesArray[index] = Class.forName(methodParamArray[index]);
                }
                Events events = Events.valueOf(eventParam[0].substring(eventParam[0].lastIndexOf('.') + 1));
                Class theClassMethodBelongTo = Class.forName(methodParam[0]);
                EventManeger.registerListener(events,
                        theClassMethodBelongTo.getDeclaredMethod(methodParam[1].substring(0,methodParam[1].indexOf('(')),methodParamClassesArray));
            }
            EventManeger.on(Events.NOTHING);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
