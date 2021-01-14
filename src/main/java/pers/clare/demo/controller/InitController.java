package pers.clare.demo.controller;

import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pers.clare.demo.bo.Test2;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;
import pers.clare.demo.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequestMapping("")
@RestController
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class InitController {
    @Bean
    public TomcatContextCustomizer sameSiteCookiesConfig() {
        return context -> {
            final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            // 设置Cookie的SameSite
            cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());

            context.setCookieProcessor(cookieProcessor);
        };
    }


    @GetMapping("init")
    public void init(
            HttpServletRequest request
            , HttpServletResponse response
            ) {
        request.getSession(true);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD,POST,PUT,DELETE");
    }

    @GetMapping("get")
    public void get(
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD,POST,PUT,DELETE");
    }
}
