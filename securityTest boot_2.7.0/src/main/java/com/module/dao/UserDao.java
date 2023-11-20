package com.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.module.util.UserMap;


@Mapper
public interface UserDao {

	public UserMap<String, Object> loadUser(String LOGIN_ID);

	public List<UserMap<String, Object>> loadAuthority(String lOGIN_ID);
	
	public int insertUser(UserMap<String, Object> userMap);
	
	public int updateUser(UserMap<String, Object> userMap);
	
	public int deleteUser(UserMap<String, Object> userMap);
	
	public int insertAuthority(UserMap<String, Object> userMap);
	
	public int updateAuthority(UserMap<String, Object> userMap);
	
	public int deleteAuthority(UserMap<String, Object> userMap);

	public List<UserMap<String, Object>> getAuthUrl();
	
}
