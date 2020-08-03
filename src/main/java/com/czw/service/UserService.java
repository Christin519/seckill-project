package com.czw.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.bean.User;
import com.czw.dto.UserDTO;
import com.czw.enums.CodeMsg;
import com.czw.exception.GlobalException;
import com.czw.mapper.UserMapper;
import com.czw.redis.UserKey;
import com.czw.util.MD5Util;
import com.czw.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: ChengZiwang
 * @date: 2020/7/21
 **/
@Service
public class UserService extends ServiceImpl<UserMapper,User> {

    public static final String COOKI_NAME_TOKEN = "token";

    @Resource
    UserMapper userMapper;

    @Autowired
    RedisService redisService;

    public User getById(long id) {
        return ((User) userMapper.selectById(id));
    }

    public User getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //延长有效期
        if(user != null) {
            addCookie(response, token, user);
        }
        return user;
    }


    public String login(HttpServletResponse response, UserDTO user) {
        if(user == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = user.getId();
        String formPass = user.getPassword();
        //判断手机号是否存在
        User newUser = getById(Long.valueOf(mobile));
        if(newUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = newUser.getPassword();
        String saltDB = newUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, newUser);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
