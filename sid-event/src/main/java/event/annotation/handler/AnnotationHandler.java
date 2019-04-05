package event.annotation.handler;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value = {"annotation.EventListen"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class AnnotationHandler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> classSet = roundEnv.getRootElements();
            String packageName = AnnotationHandler.class.getCanonicalName().replace(AnnotationHandler.class.getSimpleName(),"");
            for(Element classItem : classSet){
                List<? extends Element> enclosedElems = classItem.getEnclosedElements();
                for(Element fieldAndMethod : enclosedElems){
                    //获取类成员上的注解
                    List<? extends AnnotationMirror> annotationMirrors = fieldAndMethod.getAnnotationMirrors();
                    for(AnnotationMirror annotation : annotationMirrors){
                        Class handleClass = null;
                        try {
                            handleClass = Class.forName(packageName +
                                    annotation.getAnnotationType().asElement().getSimpleName() + "Handler");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if(handleClass != null){
                            ((AnnotationHandler.AnnotationItemHandler)handleClass.newInstance()).execute(fieldAndMethod,annotation,processingEnv);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    interface AnnotationItemHandler{
        void execute(Element element,AnnotationMirror annotation, ProcessingEnvironment environment);
    }
}
