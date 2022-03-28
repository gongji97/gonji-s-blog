package com.gongji.blog.controller;

import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.utils.UserThreadLocal;
import com.gongji.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestContoller {
    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        return Result.success(sysUser);
    }
}
