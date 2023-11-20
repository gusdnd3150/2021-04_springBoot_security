/**
 * 
 */
package com.module.util.system.security.test;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.module.util.system.security.utils.TokenUtils;


/**
 * @author gusdn
 *
 */
@Service
public class UserService implements UserDetailsService{
	
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	
	@Autowired
	BCryptPasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("loadUserByUsername==="+ username);
		
		List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>(); // 권한 정보
		auth.add(new SimpleGrantedAuthority("ROLE_USER"));
		// 테스트를 위해 아이디 비번 통일
		UserVo  user  = new UserVo(username,username,auth);
		return user;
	}
	
	public String login(UserVo user) {
		//UserVo dbUser = repository.selectUserById(user.getUsername());
		UserVo dbUser = user;
		String token = "";
		List<String> autho = new ArrayList<String>();
		
//		if(!encoder.matches(user.getPassword(), dbUser.getPassword())) {
//			throw new IllegalArgumentException();
//		}else if(dbUser == null){
//			throw new IllegalArgumentException();
//		}
		autho.add("ROLE_USER");
		
		// version 111
		//token =TokenUtils.generateJwtToken(user);
		
		
		// version 222
				
		return token;
	}

}
