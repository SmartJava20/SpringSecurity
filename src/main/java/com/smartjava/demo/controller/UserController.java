package com.smartjava.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartjava.demo.model.Token;
import com.smartjava.demo.model.User;
import com.smartjava.demo.response.ResultResponse;
import com.smartjava.demo.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/login")
	public ResultResponse login(@RequestBody User user) {
		String userName=user.getUserName();
		String pass=user.getPassword();
		return userService.login(userName,pass);
		
	}
	

	@PostMapping("/refreshToken")
	public ResultResponse refreshToken(@RequestBody Token token) {
			return userService.refreshToken(token);
		
	}
	
	@GetMapping("/list")
	public ResultResponse list() {
			return userService.list();
		
	}

}
