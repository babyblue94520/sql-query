package pers.clare.core.sqlquery.jpa;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;

public class SQLEntityRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> implements InitializingBean, RepositoryFactoryInformation<S, ID>, FactoryBean<T>, BeanClassLoaderAware,
        BeanFactoryAware, ApplicationEventPublisherAware {
    protected ClassLoader classLoader;
    protected BeanFactory beanFactory;

    private final Class<? extends T> repositoryInterface;

    private SQLEntityRepositoryFactory factory;

    private RepositoryMetadata repositoryMetadata;

    private AnnotationAttributes annotationAttributes;

    private T repository;

    public SQLEntityRepositoryFactoryBean(
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
    public EntityInformation<S, ID> getEntityInformation() {
        return null;
    }

    @Override
    public RepositoryInformation getRepositoryInformation() {
        return null;
    }

    @Override
    public PersistentEntity<?, ?> getPersistentEntity() {
        return null;
    }

    @Override
    public List<QueryMethod> getQueryMethods() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        DataSource writeDataSource = (DataSource) beanFactory.getBean(this.annotationAttributes.getString("writeDataSourceRef"));
        DataSource readDataSource = (DataSource) beanFactory.getBean(this.annotationAttributes.getString("readDataSourceRef"));
        this.factory = new SQLEntityRepositoryFactory();
        this.factory.setBeanClassLoader(classLoader);
        this.factory.setBeanFactory(beanFactory);
        this.repositoryMetadata = this.factory.getRepositoryMetadata(repositoryInterface);
        this.repository = this.factory.getRepository(repositoryInterface, writeDataSource, readDataSource);
    }

}
