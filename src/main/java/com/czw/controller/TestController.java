package com.czw.controller;

import com.czw.mapper.UserMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: ChengZiwang
 * @date: 2020/7/21
 **/
@RestController
public class TestController {

    @Resource
    UserMapper userMapper;

    @RequestMapping("/test")
    public String test(){
        Long id=13365852567L;
        return userMapper.selectById(id).toString();
    }
}
