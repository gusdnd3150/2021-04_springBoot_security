package com.example.demo.test.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.test.user.service.UserService;
import com.example.demo.test.user.vo.UserVO;

@Controller
public class TestController {
	
	@Autowired
	private UserService service;
	
	
	//메인창
	@RequestMapping("/main.do")
	public String test() {
		return "main";
	}
	
	//회원가입창 이동
	@RequestMapping("/joinUser.do")
	public String join() {
		return "joinForm";
	}
	
	//로그인창 이동
	@RequestMapping("/loginForm.do")
	public String loginForm() {
		
		return "loginForm";
	}
	
	//회원가입
	@ResponseBody
    @RequestMapping(value = "/join.do", method={RequestMethod.GET,RequestMethod.POST})
	public String jogin(UserVO user) {
		String result=null;
		try {
			result=service.checkJoinUser(user);
			
		} catch (Exception e) {
			e.printStackTrace();
			result="fail";
		}
		return result;
	}
	
	
	//로그인
	@ResponseBody
	@RequestMapping(value = "/login.do", method={RequestMethod.GET,RequestMethod.POST})
	public String login(UserVO user,HttpServletRequest request,HttpServletResponse respones) {
		String result=service.checkLogin(user,request);
		return  result;
	}
	
	@RequestMapping("/board/list")
	public String boardlist() {
		
		System.out.println("제발");
		
		return "boardList";
		
	}

}
