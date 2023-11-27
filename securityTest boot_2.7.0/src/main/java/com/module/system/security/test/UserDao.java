/**
 * 
 */
package com.module.system.security.test;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author gusdn
 *
 */
@Repository
public class UserDao {
	
	@Autowired
	SqlSession sqlSession;
	
	public UserMap<String, Object> selectUserInfo(String userName) {
		UserMap<String, Object> user = sqlSession.selectOne("UserDao.selectUserInfo", userName);
		return user;
	};
	
	public List<UserMap<String, Object>> selectUserMenu(String userName) {
		List<UserMap<String, Object>> menu = sqlSession.selectList("UserDao.selectUserMenu", userName);
		return menu;
	};
}
