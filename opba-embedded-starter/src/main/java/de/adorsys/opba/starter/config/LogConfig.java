package de.adorsys.opba.starter.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.multibanking.domain.spi.StrongCustomerAuthorisable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class LogConfig {

    @Aspect
    @Component
    public static class LoggingAspect {

        @SneakyThrows
        @Around("execution(public * de.adorsys.multibanking.domain.spi.OnlineBankingService.*(..))")
        public Object doLog(ProceedingJoinPoint joinPoint) {
            var res = joinPoint.proceed();
            var mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).findAndRegisterModules().setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if (!joinPoint.getSignature().getName().equals("getStrongCustomerAuthorisation")) {
                log.info("OBS: {}", mapper.writeValueAsString(buildMap(res, joinPoint.getSignature().getName(), joinPoint.getArgs())));
            }
            if (res instanceof StrongCustomerAuthorisable) {
                return Proxy.newProxyInstance(
                    LoggingAspect.class.getClassLoader(),
                    new Class[]{StrongCustomerAuthorisable.class},
                    new LoggingDynamicInvocationHandler(res)
                );
            }
            return res;
        }

        private static Map<String, Object> buildMap(Object res, String name, Object[] args) {
            // Guava and java11 maps don't allow null values.
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("method", name);
            map.put("return", res);
            map.put("args", args);
            return map;
        }

        @Slf4j
        public static class LoggingDynamicInvocationHandler implements InvocationHandler {

            private final Map<String, Method> methods = new HashMap<>();

            private final Object target;

            public LoggingDynamicInvocationHandler(Object target) {
                this.target = target;

                for (Method method : target.getClass().getDeclaredMethods()) {
                    this.methods.put(method.getName(), method);
                }
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = methods.get(method.getName()).invoke(target, args);
                var mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).findAndRegisterModules().setSerializationInclusion(JsonInclude.Include.NON_NULL);
                log.info("OBS.SCA: {}", mapper.writeValueAsString(buildMap(result, method.getName(), args)));
                return result;
            }
        }
    }
}
