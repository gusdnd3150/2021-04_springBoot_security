/**
 * 
 */
package com.module.system.security.test;

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

}
