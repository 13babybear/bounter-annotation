package cn.bounter.annotation.trim;

import java.lang.annotation.*;


/**
 * 去空格注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trim {

}
