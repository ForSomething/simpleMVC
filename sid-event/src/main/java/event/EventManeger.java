package event;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class EventManeger {
    private static HashMap<Events, LinkedList<Method>> eventListenerMap = new HashMap<>();

    private EventManeger(){}

    public static void on(Events event){
        LinkedList<Method> listenerList = eventListenerMap.get(event);
        if(listenerList == null){
            return;
        }
        for(Method method : listenerList){
            method.setAccessible(true);
            Class[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length > 1){
                //目前事件监听器只允许最多有一个参数，就是事件类型参数，因为其他的参数在调用的时候也不懂传什么过去
                continue;
            }
            ArrayList<Object> invokeParameters = new ArrayList<>();
            for(Class parameterType : parameterTypes){
                invokeParameters.add(event);
            }
            try{
                method.invoke(null,invokeParameters.toArray());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void registerListener(Events event,Method method){
        LinkedList<Method> listenerList = eventListenerMap.get(event);
        if(listenerList == null){
            listenerList = new LinkedList<>();
            eventListenerMap.put(event,listenerList);
        }
        listenerList.add(method);
    }
}
