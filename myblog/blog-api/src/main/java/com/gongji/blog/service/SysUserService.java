package com.gongji.blog.service;

import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.UserVo;

public interface SysUserService {
    SysUser findUser(String account, String password);

    SysUser findUserById(long id);

    /**
     * 根据token查询当前用户的信息
     *
     * @param token
     * @return
     */
    Result findUserByToken(String token);


    SysUser findUserByAccount(String account);


    UserVo findUserVoByAuthorId(Long authorId);

    void save(SysUser sysUser);
}
