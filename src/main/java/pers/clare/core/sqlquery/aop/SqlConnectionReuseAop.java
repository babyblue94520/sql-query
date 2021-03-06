package pers.clare.core.sqlquery.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.clare.core.sqlquery.support.ConnectionReuseHolder;
import pers.clare.core.sqlquery.support.ConnectionReuseManager;

@Aspect
@Order(Integer.MIN_VALUE)
@Component
public class SqlConnectionReuseAop {

    @Around("@annotation(pers.clare.core.sqlquery.aop.SqlConnectionReuse)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        SqlConnectionReuse sqlConnectionReuse = ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaredAnnotation(SqlConnectionReuse.class);
        ConnectionReuseManager manager = ConnectionReuseHolder.init(
                sqlConnectionReuse.transaction()
                , sqlConnectionReuse.isolation()
                , sqlConnectionReuse.readonly()
        );
        try {
            Object result = joinPoint.proceed();
            manager.commit();
            return result;
        } catch (Exception e) {
            manager.rollback();
            throw e;
        } finally {
            manager.close();
        }
    }
}
