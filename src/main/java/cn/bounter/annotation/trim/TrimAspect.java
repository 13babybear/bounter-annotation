package cn.bounter.annotation.trim;

import cn.bounter.annotation.trim.Trim;
import cn.bounter.annotation.trim.TrimField;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 去空格注解解析器
 */
@Aspect
@Component
@Slf4j
public class TrimAspect {

    @Around("@annotation(trim)")
    public Object trimAroud(ProceedingJoinPoint point, Trim trim) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] params = method.getParameters();
        boolean allParamTrim = Stream.of(params).allMatch(parameter -> parameter.getAnnotation(TrimField.class) == null);
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            for (int i=0; i<args.length; i++) {
                try {
                    boolean paramTrim = params[i].getAnnotation(TrimField.class) != null;
                    log.info("去除空格前：{}", JSON.toJSONString(args[i]));
                    args[i] = doTrim(args[i], allParamTrim, paramTrim);
                } catch (Exception e) {
                    log.warn("去除空格异常，异常信息：{}", e.getMessage(), e);
                }
            };
        }
        return point.proceed(args);
    }

    private Object doTrim(Object arg, Boolean allParamTrim, Boolean paramTrim) {
        try {
            if (arg == null) {
                return null;
            }
            Class argClass = arg.getClass();
            if (argClass == String.class) {
                if (allParamTrim || paramTrim) {
                    return String.valueOf(arg).trim();
                }
            }
            //通过反射修改对象字段
            Field[] fields = argClass.getDeclaredFields();
            if (fields == null || fields.length == 0) {
                return arg;
            }
            boolean trimAll = Stream.of(fields).allMatch(field -> field.getAnnotation(TrimField.class) == null);
            for (Field field : fields) {
                //去除private权限，变为可更改
                field.setAccessible(true);
                if (field.getType() == List.class) {
                    List<Object> childList = (List)field.get(arg);
                    if (CollectionUtils.isEmpty(childList)) {
                        continue;
                    }
                    if (trimAll || field.getAnnotation(TrimField.class) != null) {
                        List<Object> newList = childList.stream().map(obj -> doTrim(obj, true, true)).collect(Collectors.toList());
                        //特殊处理List<String>类型
                        if (newList.get(0).getClass() == String.class) {
                            //重新设置去除空格后的值
                            field.set(arg, newList);
                        }
                    }
                }
                if (field.getType() != String.class) {
                    continue;
                }
                //字段有TrimField注解只对加了该注解的字段去空格，否则对所有字符串类型字段去空格
                if (trimAll || field.getAnnotation(TrimField.class) != null) {
                    //返回参数的值
                    Object fieldValue = field.get(arg);
                    if (fieldValue != null) {
                        //重新设置去除空格后的值
                        field.set(arg, String.valueOf(fieldValue).trim());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("去除空格异常，异常信息：{}", e.getMessage(), e);
        }
        return arg;
    }

}
