package pers.clare.core.sqlquery.repository;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import pers.clare.core.sqlquery.SQLStoreService;

public class SQLRepositoryFactoryBean<T> implements InitializingBean, FactoryBean<T>, BeanClassLoaderAware,
        BeanFactoryAware, ApplicationEventPublisherAware {
    protected ClassLoader classLoader;
    protected BeanFactory beanFactory;

    private final Class<? extends T> repositoryInterface;

    private SQLRepositoryFactory factory;

    private AnnotationAttributes annotationAttributes;

    private T repository;

    public SQLRepositoryFactoryBean(
            Class<? extends T> repositoryInterface
            , AnnotationAttributes annotationAttributes
    ) {
        Assert.notNull(repositoryInterface, "Repository interface must not be null!");
        Assert.notNull(annotationAttributes, "Repository annotationAttributes must not be null!");
        this.repositoryInterface = repositoryInterface;
        this.annotationAttributes = annotationAttributes;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public T getObject() throws Exception {
        return this.repository;
    }

    @Override
    public Class<?> getObjectType() {
        return this.repositoryInterface;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SQLStoreService sqlStoreService = (SQLStoreService) beanFactory.getBean(this.annotationAttributes.getString("sqlStoreServiceRef"));
        this.factory = new SQLRepositoryFactory();
        this.factory.setBeanClassLoader(classLoader);
        this.factory.setBeanFactory(beanFactory);
        this.repository = this.factory.getRepository(repositoryInterface, sqlStoreService);
    }

}
