package compilePlugins.annotationPlugins;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.Map;

public class EventListenHandler implements AnnotationHandler.AnnotationItemHandler {

    @Override
    public void execute(Element element, AnnotationMirror annotation, ProcessingEnvironment environment) {
        try {
            //EventListen只能标记在方法上，因而传入的element只会是方法，我们在编译时根据这个注解，生成事件与方法订阅之间的映射
            FileObject fileObject = environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT,"","event.conf");
            Writer fileWriter = fileObject.openWriter();
            //获取方法的全限定名
            String fullMethodName = element.getEnclosingElement().asType().toString() + "->" + element.toString();
            //解析注解中标注的内容
            Map<? extends ExecutableElement, ? extends AnnotationValue> valueMap = annotation.getElementValues();
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> one : valueMap.entrySet()){
                switch (one.getKey().getSimpleName().toString()){
                    case "event":
                        String[] values = one.getValue().getValue().toString().split(",");
                        for (String item : values){
                            fileWriter.append(item + ":" + fullMethodName + "\n");
                        }
                }
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
