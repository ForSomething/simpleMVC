package annotation;

import toolroom.FileUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes(value = {"annotation.EventListen"})
public class AnnotationHandler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            FileUtils.WriteLine("AnnotationHandler is working now","C:\\Users\\Administrator\\Desktop\\testlog.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("AnnotationHandler is working now");
        return true;
    }
}
