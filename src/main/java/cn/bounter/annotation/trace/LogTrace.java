package cn.bounter.annotation.trace;

import java.lang.annotation.*;

/**
 * 日志追踪注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogTrace {

    /**
     * 是否批量
     */
    boolean multi() default false;

    /**
     * 日志备注
     */
    String remark() default "";

    /**
     * 操作人
     */
    String creatorId() default "";

    /**
     * 操作人姓名
     */
    String creatorName() default "";
}