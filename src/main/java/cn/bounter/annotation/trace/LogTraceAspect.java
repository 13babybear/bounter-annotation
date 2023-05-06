package cn.bounter.annotation.trace;

import com.alibaba.fastjson.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Setter
@Component
public class LogTraceAspect {

    @Around("@annotation(logTrace)")
    public Object traceLog(ProceedingJoinPoint point, LogTrace logTrace) throws Throwable {
        Object retVal = null;
        String errorMsg = null;
        try {
            retVal = point.proceed();
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw e;
        } finally {
            List<OperateLogMsg> logList = null;
            boolean isMulti = logTrace.multi();
            try {
                if (isMulti) {
                    //从context获取待发送的日志列表
                    logList = TraceContext.get();
                } else {
                    logList = Arrays.asList(mapToLog(logTrace, point));
                }
                if (!CollectionUtils.isEmpty(logList)) {
                    //循环发送日志MQ
                    for (OperateLogMsg logMsg : logList) {
                        log.info("发送操作日志成功，日志内容：{}", JSON.toJSONString(logMsg));
                    }
                }
            } catch (Exception e) {
                log.error("发送操作日志失败，日志内容：{}", JSON.toJSONString(logList), e);
            } finally {
                //清理上下文
                TraceContext.clear();
            }
        }
        return retVal;
    }

    private OperateLogMsg mapToLog(LogTrace logTrace, ProceedingJoinPoint joinPoint) {
        return new OperateLogMsg()
                .setRemark(TraceContext.getRemark() == null ? parseSpEL(logTrace.remark(), joinPoint) : TraceContext.getRemark())
                .setCreatorId(TraceContext.getCreateId() == null ? parseSpEL(logTrace.creatorId(), joinPoint) : TraceContext.getCreateId())
                .setCreatorName(TraceContext.getCreateName() == null ? parseSpEL(logTrace.creatorName(), joinPoint) : TraceContext.getCreateName());
    }

    /**
     * 解析SpEL表达式
     *
     * @param spELStr
     * @param joinPoint
     * @return
     */
    private String parseSpEL(String spELStr, ProceedingJoinPoint joinPoint) {
        if (spELStr.indexOf("#") == -1) {
            return spELStr;
        }
        SpelExpressionParser parser = new SpelExpressionParser();
        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spELStr);
        // spring的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        // 通过joinPoint获取被注解方法的形参
        Object[] args = joinPoint.getArgs();
        // 给上下文赋值
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 表达式从上下文中计算出实际参数值
        /*如:
            @annotation(key="#student.name")
             method(Student student)
             那么就可以解析出方法形参的某属性值，return “xiaoming”;
          */
        return String.valueOf(expression.getValue(context));
    }

}
