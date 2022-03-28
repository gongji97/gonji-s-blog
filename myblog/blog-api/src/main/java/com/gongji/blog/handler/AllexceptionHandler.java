package com.gongji.blog.handler;

import com.gongji.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//对加了controller注解方法进行拦截处理AOP的实现
@ControllerAdvice
public class AllexceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
