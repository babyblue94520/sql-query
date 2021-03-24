package pers.clare.core.sqlquery.annotation;


import java.lang.annotation.*;

/**
 * 載入SQL標籤
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sql {
    /**
     * sql
     */
    String value() default "";

    /**
     * Use name to get SQL from XML
     */
    String name() default "";
}
