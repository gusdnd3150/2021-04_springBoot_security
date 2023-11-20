package com.module.controller.web.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.module.service.UserService;
import com.module.util.UserMap;

@Controller
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired UserService userservice;
	
//	@RequestMapping("/login")
//	public String loginForm() {
//		return "/user/login-form";
//	}
	
	@RequestMapping("/createuser")
	public void createuser() {
		UserMap<String, Object> userMap = new UserMap<String, Object>();
		userMap.put("LOGIN_ID", "admin");
		userMap.put("LOGIN_PW", "test");
		userMap.put("USER_KO_FAM_NM", "한");
		userMap.put("USER_KO_NM", "수빈");
		userMap.put("USER_EN_FAM_NM", "HAN");
		userMap.put("USER_EN_NM", "SOOBIN");
		userMap.put("EMAIL_ADDR", "gkstnqls68@naver.com");
		userMap.put("MOBILE_NO", "010-7610-2158");
		userMap.put("USER_STAT", "1");
		userMap.put("LANG_CD", "ko");
		userMap.put("DEL_YN", "N");
		userMap.put("LOGIN_DT", "");
		userMap.put("PW_CHG_DT", "");
		userMap.put("LOGN_FAIL_CNT", "");
		userMap.put("ISACCOUNTNONEXPIRED", "true");
		userMap.put("ISACCOUNTNONLOCKED", "true");
		userMap.put("ISCREDENTIALSNONEXPIRED", "true");
		userMap.put("ISENABLED", "true");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ADMIN"));
		authorities.add(new SimpleGrantedAuthority("USER"));
		userMap.put("AUTHORITIES", authorities);
		userservice.insertUser(userMap);
	}
}
