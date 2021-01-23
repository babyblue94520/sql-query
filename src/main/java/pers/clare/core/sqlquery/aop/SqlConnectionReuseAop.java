package pers.clare.core.sqlquery.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.clare.core.sqlquery.support.ConnectionReuse;
import pers.clare.core.sqlquery.support.ConnectionReuseHolder;

/**
 * 依權限身分覆寫主站和子站
 */
@Aspect
@Order(Integer.MIN_VALUE)
@Component
public class SqlConnectionReuseAop {

    @Around("@annotation(pers.clare.core.sqlquery.aop.SqlConnectionReuse)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {

        SqlConnectionReuse sqlConnectionReuse = ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaredAnnotation(SqlConnectionReuse.class);
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        connectionReuse.setReuse(true);
        connectionReuse.setTransaction(sqlConnectionReuse.transaction());
        try {
            Object result = joinPoint.proceed();
            connectionReuse.commit();
            return result;
        } catch (Exception e) {
            connectionReuse.rollback();
            throw e;
        } finally {
            connectionReuse.close();
            connectionReuse.setReuse(false);
            connectionReuse.setTransaction(false);
        }
    }
}
