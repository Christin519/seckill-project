package com.czw.controller;

import javax.servlet.http.HttpServletResponse;

import com.czw.dto.ResultDTO;
import com.czw.dto.UserDTO;
import com.czw.service.RedisService;
import com.czw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
    UserService userService;
	
	@Autowired
    RedisService redisService;
	
    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }
    
    @RequestMapping("/do_login")
    @ResponseBody
    public ResultDTO<String> doLogin(HttpServletResponse response, UserDTO user) {
    	log.info(user.toString());
    	//登录
        String token = userService.login(response, user);
        return ResultDTO.success(token);
    }
}
