package pers.clare.core.sqlquery.annotation;

import org.springframework.context.annotation.Import;
import pers.clare.core.sqlquery.jpa.SQLScanRegistrar;
import pers.clare.core.sqlquery.jpa.SQLRepositoryFactoryBean;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SQLScanRegistrar.class})
public @interface SQLScan {
    String[] value() default {};
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
    String sqlStoreServiceRef() default "sqlStoreService";
    Class<? extends SQLRepositoryFactoryBean> factoryBean() default SQLRepositoryFactoryBean.class;
    Class<? extends Annotation> annotationClass() default Annotation.class;
    Class<?> markerInterface() default Class.class;
}
