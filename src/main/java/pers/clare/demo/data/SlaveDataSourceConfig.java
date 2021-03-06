package pers.clare.demo.data;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(SlaveDataSourceConfig.BeanName)
public class SlaveDataSourceConfig {
    public static final String Prefix = "slave";

    public static final String BeanName = Prefix + "DataSourceConfig";
    public static final String DataSourcePropertiesName = "spring.datasource." + Prefix;
    public static final String DataSourceName = Prefix + "DataSource";
    public static final String JdbcTemplateName = Prefix + "JdbcTemplate";

    @Bean(name = DataSourceName)
    @ConfigurationProperties(prefix = DataSourcePropertiesName)
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
