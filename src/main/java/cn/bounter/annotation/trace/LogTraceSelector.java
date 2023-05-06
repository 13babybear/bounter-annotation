package cn.bounter.annotation.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class LogTraceSelector implements ImportSelector {

    public static String systemCode;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLogTrace.class.getName()));
        systemCode = attributes.getString("systemCode");
        return new String[] {LogTraceAspect.class.getName()};
    }
}
