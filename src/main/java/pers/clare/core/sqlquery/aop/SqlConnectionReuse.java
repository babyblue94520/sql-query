package pers.clare.core.sqlquery.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依權限身分覆寫主站和子站
 * 當前使用者子站不為 null，則subProductId取代成當使用者subProductId
 * 當前使用者主站不為 null，則productId取代成當使用者productId
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlConnectionReuse {
    boolean transaction() default false;
    boolean readonly() default true;
}
