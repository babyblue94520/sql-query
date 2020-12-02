package pers.clare.demo.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pers.clare.core.data.repository.ExtendedRepositoryImpl;
import pers.clare.core.sqlquery.SQLEntityService;
import pers.clare.core.sqlquery.SQLQueryService;

import javax.sql.DataSource;

@Profile("h2")
@Configuration
@EnableJpaRepositories(
         repositoryBaseClass = ExtendedRepositoryImpl.class
)
public class H2SQLQueryConfig {
    public static final String Prefix = "demo";
    public static final String SQLQueryServiceName = Prefix + "SQLQueryService";
    public static final String SQLEntityServiceName = Prefix + "SQLEntityService";


    @Primary
    @Bean(name = SQLQueryServiceName)
    public SQLQueryService sqlQueryService(
            DataSource write
    ) {
        return new SQLQueryService(write);
    }

    @Primary
    @Bean(name = SQLEntityServiceName)
    public SQLEntityService sqlEntityService(
            DataSource write
    ) {
        return new SQLEntityService(write);
    }
}
