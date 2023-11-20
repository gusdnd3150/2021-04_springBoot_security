package com.module.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.module.util.UserMap;

public interface UserService extends UserDetailsService {
	
	public PasswordEncoder passwordEncoder();
	
	public boolean insertUser(UserMap<String, Object> userMap);
	
	public boolean updateUser(UserMap<String, Object> userMap);
	
	public boolean deleteUser(UserMap<String, Object> userMap);

	public List<UserMap<String, Object>> getAuthUrl();
	
}
