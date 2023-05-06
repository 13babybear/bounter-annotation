package cn.bounter.annotation.trace;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogTraceSelector.class)
public @interface EnableLogTrace {

    /**
     * 接入系统的系统编码
     * @return
     */
    String systemCode();

}
