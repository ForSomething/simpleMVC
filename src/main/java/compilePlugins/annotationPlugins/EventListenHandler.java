package compilePlugins.annotationPlugins;

import annotation.EventListen;
import common.constvaslue.Events;
import toolroom.FileUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.io.IOException;

public class EventListenHandler implements AnnotationHandler.AnnotationItemHandler {

    @Override
    public void execute(Element element,AnnotationMirror annotation) {
        try {
            EventListen eventListen = annotation.getAnnotationType().getAnnotation(EventListen.class);
            Events[] events = eventListen.event();
            for (Events event : events){
                FileUtils.WriteLine(event.toString() + "#" + element.toString(),
                        "C:\\Users\\Administrator\\Desktop\\testlog.txt",true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
