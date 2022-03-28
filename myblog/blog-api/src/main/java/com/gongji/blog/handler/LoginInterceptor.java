package com.gongji.blog.handler;

import com.alibaba.fastjson.JSON;
import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.service.LoginService;
import com.gongji.blog.service.SysUserService;
import com.gongji.blog.utils.UserThreadLocal;
import com.gongji.blog.vo.ErroCode;
import com.gongji.blog.vo.Result;
import com.google.gson.annotations.JsonAdapter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行controller方法之前执行
        /**
         * 判断 请求的端口路径是否为HAndlerNethod
         * 判断token是否为空
         * 如果不为空 登录验证
         * 验证成功 放行 即可
         */
        if (!(handler instanceof HandlerMethod)){
            //handler 可能是 requestResourcehandler springboot 程序 访问静态资源 默认去classpath下的static目录去寻找
            return true;
        }
        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}", requestURI);
        log.info("request method:{}", request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");
        if (StringUtils.isBlank(token)){
            Result result = Result.fail(ErroCode.NO_LOGIN.getCode() ,ErroCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser==null){
            Result result = Result.fail(ErroCode.NO_LOGIN.getCode() ,ErroCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //登录成功 放行
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果不删除ThreadLocal中用完的信息 会有内存泄露的风险
        UserThreadLocal.remove();
    }
}
