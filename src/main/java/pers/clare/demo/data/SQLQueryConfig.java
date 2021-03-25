package pers.clare.demo.data;

import org.springframework.beans.factory.annotation.Qualifier;
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
            @Qualifier(DemoDataSourceConfig.DataSourceName) DataSource write // 讀寫資料庫連線
            , @Qualifier(SlaveDataSourceConfig.DataSourceName) DataSource read // 唯讀資料庫連線，沒有則不用配置
    ) {
        return new SQLStoreService(write, read);
    }
}
