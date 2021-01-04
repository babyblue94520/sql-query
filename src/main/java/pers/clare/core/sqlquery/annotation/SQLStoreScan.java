package pers.clare.core.sqlquery.annotation;

import org.springframework.context.annotation.Import;
import pers.clare.core.sqlquery.jpa.SQLEntityScanRegistrar;
import pers.clare.core.sqlquery.jpa.SQLEntityRepositoryFactoryBean;

import javax.sql.DataSource;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SQLEntityScanRegistrar.class})
public @interface SQLStoreScan {
    String[] value() default {};
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
    String sqlStoreServiceRef() default "sqlStoreService";
    Class<? extends SQLEntityRepositoryFactoryBean> factoryBean() default SQLEntityRepositoryFactoryBean.class;
    Class<? extends Annotation> annotationClass() default Annotation.class;
    Class<?> markerInterface() default Class.class;
}
