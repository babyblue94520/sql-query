package pers.clare.core.sqlquery.jpa;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.*;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.interceptor.TransactionalProxy;
import pers.clare.core.sqlquery.SQLEntityRepositoryImpl;

import javax.sql.DataSource;
import java.util.Optional;

@Log4j2
public class SQLEntityRepositoryFactory implements BeanClassLoaderAware, BeanFactoryAware {
    protected ClassLoader classLoader;
    protected BeanFactory beanFactory;
    protected DataSource writeDataSource;
    protected DataSource readDataSource;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public <T> T getRepository(Class<T> repositoryInterface) {
        SQLEntityRepositoryImpl target ;
        if (readDataSource != null && readDataSource != writeDataSource) {
            target = new SQLEntityRepositoryImpl(repositoryInterface, writeDataSource, readDataSource);
        } else {
            target = new SQLEntityRepositoryImpl(repositoryInterface, writeDataSource);
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(repositoryInterface, SQLEntityRepository.class);
        if (MethodInvocationValidator.supports(repositoryInterface)) {
            result.addAdvice(new MethodInvocationValidator());
        }
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);

        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(repositoryInterface)) {
            result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
        }


        T repository = (T) result.getProxy(classLoader);

        if (log.isDebugEnabled()) {
            log.debug("Finished creation of repository instance for {}.", repositoryInterface.getName());
        }

        return repository;
    }

    protected RepositoryMetadata getRepositoryMetadata(Class<?> repositoryInterface) {
        return AbstractRepositoryMetadata.getMetadata(repositoryInterface);
    }
}
