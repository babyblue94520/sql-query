package pers.clare.core.sqlquery.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.repository.util.ClassUtils;

import java.util.Arrays;
import java.util.Set;

@Log4j2
@Setter
@Getter
public class ClassPathSQLScanner extends ClassPathBeanDefinitionScanner {
    private AnnotationAttributes annotationAttributes;

    public ClassPathSQLScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void registerFilters() {
        // default include filter that accepts all classes
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            log.warn("No SQLStore was found in '{}' package. Please check your configuration.", Arrays.toString(basePackages));
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        if (beanDefinition.getMetadata().getInterfaceNames().length == 0) {
            return false;
        }
        for (String interfaceName : beanDefinition.getMetadata().getInterfaceNames()) {
            if (!(SQLRepository.class.getName().equals(interfaceName)
                    || SQLCrudRepository.class.getName().equals(interfaceName))) {
                return false;
            }
        }
        boolean isNonRepositoryInterface = !ClassUtils.isGenericRepositoryInterface(beanDefinition.getBeanClassName());
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();
        return isNonRepositoryInterface && isTopLevelType;
    }

    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
        }
        return candidates;
    }


    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        Class<? extends SQLRepositoryFactoryBean> factoryBeanClass = annotationAttributes.getClass("factoryBean");
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            log.debug("Creating SQLEntityRepositoryFactoryBean with name '{}' and '{}' interface", holder.getBeanName(), beanClassName);
            if (beanClassName == null) {
                log.warn("beanClassName is null.");
            } else {
                definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                definition.getConstructorArgumentValues().addGenericArgumentValue(annotationAttributes);
                definition.setBeanClass(factoryBeanClass);
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            }
        }
    }
}
