package com.gongji.blog.service;

import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.LoginParam;
import org.springframework.stereotype.Service;


public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    /**
     * 注册功能
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}
