package com.gongji.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gongji.blog.dao.mapper.SysUserMapper;
import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.service.LoginService;
import com.gongji.blog.service.SysUserService;
import com.gongji.blog.vo.ErroCode;
import com.gongji.blog.vo.LoginUserVo;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private LoginService loginService;


    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getAccount, account);
        sysUserLambdaQueryWrapper.eq(SysUser::getPassword, password);
        sysUserLambdaQueryWrapper.select(SysUser::getAccount, SysUser::getId, SysUser::getAvatar
                , SysUser::getNickname);
        sysUserLambdaQueryWrapper.last("limit 1");
        return sysUserMapper.selectOne(sysUserLambdaQueryWrapper);
    }

    public SysUser findUserById(long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if ((sysUser == null)) {
            sysUser.setNickname("gongji");
        }
        return sysUser;
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1.token 的合法性 是否为空 解析是否成功 redis是否存在
         * 2. 如果校验失败则返回错误
         *      如果成功 则返回对应的结果 LoginUserVo
         */
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null) {

            return Result.fail(ErroCode.TOKEN_ERROR.getCode(), ErroCode.TOKEN_ERROR.getMsg());

        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        return Result.success(loginUserVo);
    }

    @Override
    public void save(SysUser sysUser) {
        //保存用户这个id会自动生成
        //这个地方 默认生成的id是 分布式id 雪花算法
        //mybatis-plus
        sysUserMapper.insert(sysUser);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }


    @Override
    public UserVo findUserVoByAuthorId(Long authorId) {
        SysUser sysUser = sysUserMapper.selectById(authorId);
        if ((sysUser == null)) {
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("gongji");
        }
        UserVo userVo = new UserVo();
        userVo.setAvatar(sysUser.getAvatar());
        userVo.setNickname(sysUser.getNickname());
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
    }


}
