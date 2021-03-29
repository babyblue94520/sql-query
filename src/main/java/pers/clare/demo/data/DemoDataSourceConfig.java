package pers.clare.demo.data;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration(DemoDataSourceConfig.BeanName)
public class DemoDataSourceConfig {
    public static final String Prefix = "demo";
    public static final String BasePackages = "pers.clare." + Prefix + ".data.jpa";
    public static final String[] EntityPackages = {
            "pers.clare.demo.data.entity"
    };

    public static final String BeanName = Prefix + "DataSourceConfig";
    public static final String DataSourcePropertiesName = "spring.datasource." + Prefix;
    public static final String DataSourceName = Prefix + "DataSource";
    public static final String EntityManagerFactoryName = Prefix + "EntityManagerFactory";
    public static final String TransactionManagerName = Prefix + "TransactionManager";
    public static final String JdbcTemplateName = Prefix + "JdbcTemplate";
    public static final String PersistenceUnitName = Prefix + "PersistenceUnit";


    @Primary
    @Bean(name = DataSourceName)
    @ConfigurationProperties(prefix = DataSourcePropertiesName)
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = JdbcTemplateName)
    public JdbcTemplate jdbcTemplate(@Qualifier(DataSourceName) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
