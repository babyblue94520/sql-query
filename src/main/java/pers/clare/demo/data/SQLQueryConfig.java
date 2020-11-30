package pers.clare.demo.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pers.clare.core.sqlquery.SQLEntityService;
import pers.clare.core.sqlquery.SQLQueryService;

import javax.sql.DataSource;

@Configuration
public class SQLQueryConfig {
    public static final String Prefix = "demo";
    public static final String SQLQueryServiceName = Prefix + "SQLQueryService";
    public static final String SQLEntityServiceName = Prefix + "SQLEntityService";

    @Primary
    @Bean(name = SQLQueryServiceName)
    public SQLQueryService sqlQueryService(
            @Qualifier(DemoDataSourceConfig.DataSourceName) DataSource write
            ,
            @Qualifier(SlaveDataSourceConfig.DataSourceName) DataSource read
    ) {
        return new SQLQueryService(write, read);
    }

    @Primary
    @Bean(name = SQLEntityServiceName)
    public SQLEntityService sqlEntityService(
            @Qualifier(DemoDataSourceConfig.DataSourceName) DataSource write
            , @Qualifier(SlaveDataSourceConfig.DataSourceName) DataSource read
    ) {
        return new SQLEntityService(write, read);
    }
}
