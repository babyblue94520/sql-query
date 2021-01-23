package pers.clare.demo.controller;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
