package pers.clare.core.sqlquery.jpa;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.*;
import pers.clare.core.sqlquery.SQLEntityRepositoryImpl;

import javax.sql.DataSource;

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

    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    public <T> T getRepository(
            Class<T> repositoryInterface
            , DataSource writeDataSource
            , DataSource readDataSource
    ) {
        SQLEntityRepositoryImpl target;
        if (readDataSource != null && readDataSource != writeDataSource) {
            target = new SQLEntityRepositoryImpl(repositoryInterface, writeDataSource, readDataSource);
        } else {
            target = new SQLEntityRepositoryImpl(repositoryInterface, writeDataSource);
        }

        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(repositoryInterface, SQLEntityRepository.class);
//        if (MethodInvocationValidator.supports(repositoryInterface)) {
//            result.addAdvice(new MethodInvocationValidator());
//        }
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
        result.addAdvice(new SQLQueryMethodInterceptor(SQLQueryMethodFactory.create(repositoryInterface)));
//        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(repositoryInterface)) {
//            result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
//        }


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
