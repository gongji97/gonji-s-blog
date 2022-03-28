package com.gongji.blog.config;

import com.gongji.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域问题
        registry.addMapping("/**").allowedOrigins("120.79.72.69").allowedOrigins("http://www.gongji97.cn/")
        .allowedOrigins("localhost:8080").allowedMethods("POST","GET","PUT","OPTIONS","DELETE")
        .maxAge(3600).allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截test接口 后续遇到需要拦截的接口时 再配置需要拦截的接口
        registry.addInterceptor(loginInterceptor).addPathPatterns("/test")
                .addPathPatterns("/comments/create/change").addPathPatterns("/articles/publish");
    }
}
