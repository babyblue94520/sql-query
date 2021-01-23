package pers.clare.swagger;

import io.swagger.models.parameters.Parameter;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component("ServiceModelToSwagger2Mapper")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomModelToSwaggerMapper extends ServiceModelToSwagger2MapperImpl {

    /**
     * 排序參數順序
     * @param list
     * @return
     */
    @Override
    protected List<Parameter> parameterListToParameterList(List<springfox.documentation.service.Parameter> list) {
        if(list.size()>0){
            list = list.stream().sorted(Comparator.comparingInt(springfox.documentation.service.Parameter::getOrder)).collect(Collectors.toList());
        }
        return super.parameterListToParameterList(list);
    }
}
