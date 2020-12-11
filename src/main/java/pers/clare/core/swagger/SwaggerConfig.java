package pers.clare.core.swagger;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private Set<String> headers = new HashSet<>();
    private List<ResponseMessage> responseMessages = new ArrayList<>();

    {
        headers.add("application/x-www-form-urlencoded");
        headers.add("application/json");
    }

    @Bean
    public Docket docket(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("pers.clare.demo"))
                .paths(PathSelectors.any())
                .build()
                .consumes(headers)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages)
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("demo api")
                .description("restful api")
                .version("1.0")
                .build();
    }

    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Arrays.asList(
                        newRule(resolver.resolve(Pageable.class), resolver.resolve(PageableApiModel.class))
                        , newRule(resolver.resolve(Page.class, WildcardType.class), resolver.resolve(PageApiModel.class, WildcardType.class))
                );
            }
        };
    }

    @ApiModel
    @Data
    static class PageableApiModel {
        @ApiModelProperty(value = "頁數", example = "0")
        private Integer page;

        @ApiModelProperty(value = "每頁筆數", example = "20")
        private Integer size;

        @ApiModelProperty(value = "排序(\"name asc|desc\")", example = "")
        private List<String> sort;
    }

    @ApiModel
    @Data
    static class PageApiModel<T> {
        @ApiModelProperty("當前頁碼")
        private Integer number;
        @ApiModelProperty("每頁筆數")
        private Integer size;
        @ApiModelProperty("總筆數")
        private Integer totalElements;
        @ApiModelProperty("總頁數")
        private Integer totalPages;
        @ApiModelProperty("資料筆數")
        private Integer numberOfElements;
        @ApiModelProperty("資料")
        private List<T> content;
        @ApiModelProperty("排序")
        private List<PageSortApiModel> sort;

        @ApiModelProperty("是否第一筆")
        private Boolean first;
        @ApiModelProperty("是否最後一筆")
        private Boolean last;
    }

    @ApiModel
    @Data
    static class PageSortApiModel {
        @ApiModelProperty("排序方式(ASC|DESC)")
        private String direction;

        @ApiModelProperty("排序欄位")
        private String property;
    }
}
