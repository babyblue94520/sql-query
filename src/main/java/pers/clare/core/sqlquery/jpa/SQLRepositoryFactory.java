package pers.clare.core.sqlquery.jpa;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.repository.core.support.*;
import pers.clare.core.sqlquery.*;

@Log4j2
public class SQLRepositoryFactory implements BeanClassLoaderAware, BeanFactoryAware {
    protected ClassLoader classLoader;
    protected BeanFactory beanFactory;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public <T> T getRepository(
            Class<T> repositoryInterface
            , SQLStoreService sqlStoreService
    ) {
        if (!SQLRepository.class.isAssignableFrom(repositoryInterface)) {
            throw new Error(String.format("%s must inherit %s interface", repositoryInterface, SQLRepository.class.getSimpleName()));
        }
        ProxyFactory result = new ProxyFactory();
        Object target;
        if (SQLCrudRepository.class.isAssignableFrom(repositoryInterface)) {
            target = new SQLCrudCrudRepositoryImpl(repositoryInterface, sqlStoreService);
            result.setInterfaces(repositoryInterface, SQLCrudRepository.class);
        } else {
            target = new Object();
            result.setInterfaces(repositoryInterface, SQLRepository.class);
        }
        result.setTarget(target);
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
        if (MethodInvocationValidator.supports(repositoryInterface)) {
            result.addAdvice(new MethodInvocationValidator());
        }
        result.addAdvice(new SQLMethodInterceptor(repositoryInterface, target, SQLMethodFactory.create(repositoryInterface, sqlStoreService)));
        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(repositoryInterface)) {
            result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
        }
        T repository = (T) result.getProxy(classLoader);

        if (log.isDebugEnabled()) {
            log.debug("Finished creation of repository instance for {}.", repositoryInterface.getName());
        }

        return repository;
    }
}
