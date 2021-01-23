package pers.clare.demo.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.annotation.SQLScan;

import javax.sql.DataSource;

@Configuration
@SQLScan(
        sqlStoreServiceRef = SQLQueryConfig.SQLStoreServiceName
        , basePackages = {"pers.clare.demo.data.sql"}
)
public class SQLQueryConfig {
    public static final String Prefix = "demo";
    public static final String SQLStoreServiceName = Prefix + "SQLStoreService";

    @Primary
    @Bean(name = SQLStoreServiceName)
    public SQLStoreService sqlStoreService(
//            @Qualifier(DemoDataSourceConfig.DataSourceName)
                    DataSource write
            ,
//            @Qualifier(SlaveDataSourceConfig.DataSourceName)
                    DataSource read
    ) {
        return new SQLStoreService(write, read);
    }
}
