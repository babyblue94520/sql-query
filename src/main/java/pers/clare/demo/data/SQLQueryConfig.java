package pers.clare.demo.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.annotation.SQLStoreScan;

import javax.sql.DataSource;

@Profile("mysql")
@Configuration
@SQLStoreScan(
        sqlStoreServiceRef = SQLQueryConfig.SQLStoreServiceName
        , basePackages = {"pers.clare.demo.data.sql"}
)
public class SQLQueryConfig {
    public static final String Prefix = "demo";
    public static final String SQLQueryServiceName = Prefix + "SQLQueryService";
    public static final String SQLEntityServiceName = Prefix + "SQLEntityService";
    public static final String SQLStoreServiceName = Prefix + "SQLStoreService";

    @Primary
    @Bean(name = SQLStoreServiceName)
    public SQLStoreService sqlStoreService(
            @Qualifier(DemoDataSourceConfig.DataSourceName) DataSource write
            , @Qualifier(SlaveDataSourceConfig.DataSourceName) DataSource read
    ) {
        return new SQLStoreService(write, read);
    }

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
