package pers.clare.core.sqlquery.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import pers.clare.core.sqlquery.SQLCrudRepositoryImpl;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.method.SQLMethodFactory;
import pers.clare.core.sqlquery.method.SQLMethodInterceptor;

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
            target = new SQLCrudRepositoryImpl(repositoryInterface, sqlStoreService);
            result.setInterfaces(repositoryInterface, SQLCrudRepository.class);
        } else {
            target = new SQLRepository() {
            };
            result.setInterfaces(repositoryInterface, SQLRepository.class);
        }
        result.setTarget(target);
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
        result.addAdvice(new SQLMethodInterceptor(repositoryInterface, target, SQLMethodFactory.create(repositoryInterface, sqlStoreService)));
        T repository = (T) result.getProxy(classLoader);

        if (log.isDebugEnabled()) {
            log.debug("Finished creation of repository instance for {}.", repositoryInterface.getName());
        }

        return repository;
    }
}
