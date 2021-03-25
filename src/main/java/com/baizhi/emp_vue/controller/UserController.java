package com.baizhi.emp_vue.controller;

import com.baizhi.emp_vue.entity.User;
import com.baizhi.emp_vue.service.UserService;
import com.baizhi.emp_vue.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	// 获得二维码
	@GetMapping("getImage")
	public String getImageCode(HttpServletRequest request) throws IOException {

		String code = VerifyCodeUtils.generateVerifyCode(4);
		
		request.getServletContext().setAttribute("code", code);

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		VerifyCodeUtils.outputImage(200, 60, byteArrayOutputStream, code);

		return "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());

	}

	// 用户信息注册

	@PostMapping("/registerUser")
	public Map<String, Object> register(@RequestBody User user, String code, HttpServletRequest request) {
		log.info("用户信息:[{}]", user.toString());
		log.info("用户输入的验证码信息:[{}]", code);

		Map<String, Object> map = new HashMap<>();
		try {
			String key = (String) request.getServletContext().getAttribute("code");
			if (key.equalsIgnoreCase(code)) {
				userService.register(user);
				map.put("status", true);
				map.put("msg", "注册成功");
			} else {
				throw new RuntimeException("验证码错误");
			}
		} catch (Exception e) {

			e.printStackTrace();
			
			map.put("status", false);
			map.put("msg", e.getMessage());
		}

		return map;
	}
	
	
	
//用户登录实现
@PostMapping("/loginUser")	
public Map<String, Object> loginUser(@RequestBody User user){
	
	Map<String, Object> map = new HashMap<>();
	
	try {
	   User userDB = userService.loginUser(user);
	   
	   map.put("status", true);
	   map.put("msg", "登录成功");
	   map.put("user", userDB);
		
	} catch (Exception e) {
		
	map.put("status", false);
	map.put("msg",e.getMessage());
	 }
	
	return map;
 

  }	
	
	
}
