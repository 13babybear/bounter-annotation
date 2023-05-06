package cn.bounter.annotation.trim;

import java.lang.annotation.*;


/**
 * 去空格字段注解
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrimField {

}
