package sid.bo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface Persistence {
    String column() default "";

    String table() default "";

    String sortColumns() default "";

    ColumnNameRule columnNameRule() default ColumnNameRule.EQUAL;

    enum ColumnNameRule{
        EQUAL,//属性和字段同名
        UNDERLINE_SEPARATOR //下划线分隔符
    }
}
