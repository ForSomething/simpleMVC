package event.annotation.handler;


import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventListenHandler implements AnnotationHandler.AnnotationItemHandler {

    @Override
    public void execute(Element element, AnnotationMirror annotation, ProcessingEnvironment environment) {
        try {
            //EventListen只能标记在方法上，因而传入的element只会是方法，我们在编译时根据这个注解，生成事件与方法订阅之间的映射

            FileObject fileObject = environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT,"","event.conf");
            Writer fileWriter = fileObject.openWriter();
            Map<String,Object> handlerInfoMap = new HashMap<>();
            handlerInfoMap.put("class",element.getEnclosingElement().asType().toString());
            handlerInfoMap.put("method",element.getSimpleName());
            handlerInfoMap.put("paramter",new LinkedList<String>());
            ExecutableElement method = (ExecutableElement)element;
            for(VariableElement paramter : method.getParameters()){
                ((List<String>)handlerInfoMap.get("paramter")).add(paramter.asType().toString());
            }
            //解析注解中标注的内容
            Map<? extends ExecutableElement, ? extends AnnotationValue> valueMap = annotation.getElementValues();
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> one : valueMap.entrySet()){
                switch (one.getKey().getSimpleName().toString()){
                    case "event":
                        String[] values = one.getValue().getValue().toString().split(",");
                        for (String item : values){
                            fileWriter.append(item + "->" + parse2json(handlerInfoMap) + "\n");
                        }
                }
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parse2json(Map<String,Object> map){
        StringBuilder jsonString = new StringBuilder("{");
        int index = 0;
        for (Map.Entry<String,Object> one : map.entrySet()){
            if(index != 0){
                jsonString.append(",");
            }
            jsonString.append("\"").append(one.getKey()).append("\":");
            if(one.getValue() instanceof List){
                jsonString.append("[");
                int listItemIndex = 0;
                for(Object listItem : ((List<Object>)one.getValue())){
                    if(listItemIndex != 0){
                        jsonString.append(",");
                    }
                    jsonString.append("\"").append(listItem.toString()).append("\"");
                    listItemIndex++;
                }
                jsonString.append("]");
            }else{
                jsonString.append("\"").append(one.getValue()).append("\"");
            }
            index++;
        }
        jsonString.append("}");
        return jsonString.toString();
    }
}
