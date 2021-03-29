package pers.clare.demo.data;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration(DemoDataSourceConfig.BeanName)
public class DemoDataSourceConfig {
    public static final String Prefix = "demo";
    public static final String BeanName = Prefix + "DataSourceConfig";
    public static final String DataSourcePropertiesName = "spring.datasource." + Prefix;
    public static final String DataSourceName = Prefix + "DataSource";


    @Primary
    @Bean(name = DataSourceName)
    @ConfigurationProperties(prefix = DataSourcePropertiesName)
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

}
