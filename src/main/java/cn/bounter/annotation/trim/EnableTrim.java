package cn.bounter.annotation.trim;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TrimAspect.class)
public @interface EnableTrim {

}
