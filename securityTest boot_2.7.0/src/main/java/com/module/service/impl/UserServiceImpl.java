package com.module.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.module.dao.UserDao;
import com.module.service.UserService;
import com.module.util.UserMap;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired UserDao userDao;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public UserDetails loadUserByUsername(String LOGIN_ID) throws UsernameNotFoundException {
		////logger.info("UserServiceImpl loadUserByUsername 0000" + LOGIN_ID);
		UserMap<String, Object> userMap = userDao.loadUser(LOGIN_ID);
		if (userMap == null) {
			throw new UsernameNotFoundException(LOGIN_ID);
		}
		userMap.put("AUTHORITIES", getAuthorities(LOGIN_ID, "ROLE_"));
		return userMap;
	}
	
	public Collection<GrantedAuthority> getAuthorities(String LOGIN_ID, String rolePrefix) {
        List<UserMap<String, Object>> string_authorities = userDao.loadAuthority(LOGIN_ID);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (int i = 0; i < string_authorities.size(); i++) {
        	authorities.add(new SimpleGrantedAuthority(rolePrefix + string_authorities.get(i).getString("USER_AUTH").toUpperCase()));
		}
        return authorities;
   }
	
	@Override
	public PasswordEncoder passwordEncoder() {
		return passwordEncoder;
	}

	@Override
	public boolean insertUser(UserMap<String, Object> userMap) {
		String rawPassword = userMap.getString("LOGIN_PW");
		String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
		userMap.put("LOGIN_PW", encodedPassword);
		userDao.insertUser(userMap);
		userDao.insertAuthority(userMap);
		return false;
	}

	@Override
	public boolean updateUser(UserMap<String, Object> userMap) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUser(UserMap<String, Object> userMap) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<UserMap<String, Object>> getAuthUrl() {
		return userDao.getAuthUrl();
	}
	
}
