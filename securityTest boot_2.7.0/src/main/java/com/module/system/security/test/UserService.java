/**
 * 
 */
package com.module.system.security.test;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

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

import com.module.system.security.utils.TokenUtils;


/**
 * @author gusdn
 *
 */
@Service
public class UserService implements UserDetailsService{
	
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	
	@Autowired
	UserDao userDao;
	
	@Autowired
	BCryptPasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username)  {
		logger.info("loadUserByUsername==="+ username);
		//UserVo  user  = new UserVo(username,username,auth);
		UserMap<String, Object> user = userDao.selectUserInfo(username);
		if(user == null) {
			throw new UsernameNotFoundException(username);
		}
		logger.info("select user :: "+ user);
		return user;
	}
	
	
	public List<UserMap<String, Object>> selectUserMenu(String username){
		return userDao.selectUserMenu(username);
	}
	
}
