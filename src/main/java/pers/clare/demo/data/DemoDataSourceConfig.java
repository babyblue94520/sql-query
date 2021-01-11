package pers.clare.demo.data;


import org.springframework.context.annotation.Profile;
import pers.clare.core.data.repository.ExtendedRepositoryImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration(DemoDataSourceConfig.BeanName)
@EnableJpaRepositories(
        entityManagerFactoryRef = DemoDataSourceConfig.EntityManagerFactoryName
        , transactionManagerRef = DemoDataSourceConfig.TransactionManagerName
        , basePackages = {DemoDataSourceConfig.BasePackages}
        , repositoryBaseClass = ExtendedRepositoryImpl.class
)
public class DemoDataSourceConfig {
    public static final String Prefix = "demo";
    public static final String BasePackages = "pers.clare." + Prefix + ".data.repository";
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

    @Primary
    @Bean(name = EntityManagerFactoryName)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder
            , @Qualifier(DataSourceName) DataSource dataSource
    ) {
        return entityManagerFactoryBuilder.dataSource(dataSource)
                .packages(EntityPackages)
                .persistenceUnit(PersistenceUnitName)
                .build();
    }

    @Primary
    @Bean(name = TransactionManagerName)
    @Autowired
    public PlatformTransactionManager transactionManager(
            @Qualifier(EntityManagerFactoryName) FactoryBean<EntityManagerFactory> entityManagerFactory
    ) throws Exception {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
