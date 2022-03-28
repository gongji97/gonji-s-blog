package com.gongji.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.service.LoginService;
import com.gongji.blog.service.SysUserService;
import com.gongji.blog.utils.JWTUtils;
import com.gongji.blog.vo.ErroCode;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.LoginParam;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
//事务注解
@Transactional
public class LoginServiceImpl  implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //加密言
    private static final String slat = "mszlu!@#";

    /**
     * 1.检查参数是否合法
     * 2.根据用户名和密码去user表中查询 是否存在
     * 3.若不存在 登录失败
     *    若存在 使用jwt 生成token 返回给前端
     * 4.token放入redis中 redis token user信息设置过期时间
     *  登录认证的时候 先认证token字符串是否合法 去使用redis验证是否存在
     * @param loginParam
     */
    @Override
    public Result login(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if(StringUtils.isEmpty(account)||StringUtils.isEmpty(password)){
            return Result.fail(ErroCode.PARAMS_ERROR.getCode(),ErroCode.PARAMS_ERROR.getMsg() );
        }
        password = DigestUtils.md5Hex(password + slat);
        SysUser sysUser = sysUserService.findUser(account,password);
        if (sysUser==null){
            return Result.fail(ErroCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErroCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSON(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        Map<String, Object> checkToken = JWTUtils.checkToken(token);
        if (checkToken == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token).toString();
        if (StringUtils.isEmpty(userJson)){
            return null;
        }
        return JSON.parseObject(userJson, SysUser.class);
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_token");
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1.判断参数是否合法
         * 2.判断账号是否存在
         *     不存在 注册 存在 返回已被注册
         * 3.生成token
         * 4.存入redis 并返回
         * 5. 注意加上事务 一旦中间任何过程出现问题 需要进行回归
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (org.apache.commons.lang3.StringUtils.isBlank(account)
                || org.apache.commons.lang3.StringUtils.isBlank(password)
                || org.apache.commons.lang3.StringUtils.isBlank(nickname)
        ) {
            return Result.fail(ErroCode.PARAMS_ERROR.getCode(), ErroCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if (sysUser != null){
            return Result.fail(ErroCode.ACCOUNT_EXIST.getCode(), ErroCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser =new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password + slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/user.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);
        return Result.success(token);
    }
}
