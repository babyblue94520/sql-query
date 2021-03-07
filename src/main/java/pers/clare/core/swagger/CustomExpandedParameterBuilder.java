package pers.clare.core.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Enums;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.transform;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Types.typeNameFor;

@Profile("dev")
@Primary
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExpandedParameterBuilder extends ExpandedParameterBuilder {
    private final TypeResolver resolver;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired
    public CustomExpandedParameterBuilder(
            TypeResolver resolver,
            EnumTypeDeterminer enumTypeDeterminer) {
        super(resolver,enumTypeDeterminer);
        this.resolver = resolver;
        this.enumTypeDeterminer = enumTypeDeterminer;
    }

    @Override
    public void apply(ParameterExpansionContext context) {
        AllowableValues allowable = allowableValues(context.getFieldType().getErasedType());

        String name = isNullOrEmpty(context.getParentName())
                ? context.getFieldName()
                : String.format("%s.%s", context.getParentName(), context.getFieldName());

        String typeName = context.getDataTypeName();
        ModelReference itemModel = null;
        ResolvedType resolved = resolver.resolve(context.getFieldType());
        if (isContainerType(resolved)) {
            resolved = fieldType(context).or(resolved);
            ResolvedType elementType = collectionElementType(resolved);
            String itemTypeName = typeNameFor(elementType.getErasedType());
            AllowableValues itemAllowables = null;
            if (enumTypeDeterminer.isEnum(elementType.getErasedType())) {
                itemAllowables = Enums.allowableValues(elementType.getErasedType());
                itemTypeName = "string";
            }
            typeName = containerType(resolved);
            itemModel = new ModelRef(itemTypeName, itemAllowables);
        } else if (enumTypeDeterminer.isEnum(resolved.getErasedType())) {
            typeName = "string";
        }
        context.getParameterBuilder()
                .name(name)
                .description(null)
                .defaultValue(null)
                .required(Boolean.FALSE)
                .allowMultiple(isContainerType(resolved))
                .type(resolved)
                .modelRef(new ModelRef(typeName, itemModel))
                .allowableValues(allowable)
                .parameterType(context.getParameterType())
                .parameterAccess(null)
//                .order(DEFAULT_PRECEDENCE) // 移除固定的排序
        ;
    }

    private Optional<ResolvedType> fieldType(ParameterExpansionContext context) {
        return Optional.of(context.getFieldType());
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private AllowableValues allowableValues(Class<?> fieldType) {

        AllowableValues allowable = null;
        if (enumTypeDeterminer.isEnum(fieldType)) {
            allowable = new AllowableListValues(getEnumValues(fieldType), "LIST");
        }

        return allowable;
    }

    private List<String> getEnumValues(final Class<?> subject) {
        return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
            @Override
            public String apply(final Object input) {
                return input.toString();
            }
        });
    }
}
