package pers.clare.core.sqlquery.annotation;

import java.lang.annotation.*;

/**
 * 載入SQL標籤
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sql {
    String name() default "";

    String query() default "";
}
