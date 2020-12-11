package pers.clare.core.sqlquery.jpa;

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
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.repository.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

@Log4j2
@Setter
@Getter
public class ClassPathSQLEntityScanner extends ClassPathBeanDefinitionScanner {
    private AnnotationAttributes annotationAttributes;

    public ClassPathSQLEntityScanner(BeanDefinitionRegistry registry) {
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
            log.warn(() -> "No MyBatis mapper was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isNonRepositoryInterface = !ClassUtils.isGenericRepositoryInterface(beanDefinition.getBeanClassName());
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();

        return isNonRepositoryInterface && isTopLevelType;
    }

    /**
     * Customizes the repository interface detection and triggers annotation detection on them.
     */
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
        Class<? extends SQLEntityRepositoryFactoryBean> factoryBeanClass = annotationAttributes.getClass("factoryBean");
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            log.debug(() -> "Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName
                    + "' mapperInterface");

            // the mapper interface is the original class of the bean
            // but, the actual class of the bean is MapperFactoryBean
            definition.getConstructorArgumentValues()
                    .addGenericArgumentValue(beanClassName); // issue #59
            definition.getConstructorArgumentValues()
                    .addGenericArgumentValue(annotationAttributes);
            definition.setBeanClass(factoryBeanClass);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }
}
