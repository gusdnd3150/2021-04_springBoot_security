package com.example.demo.test.user.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.test.user.vo.UserVO;


@Repository
public class TestDAO {

	
	@Autowired
	private SqlSession sqlsession;
	
	
	//유저에대한 권한만 쏙 빼오는 메서드
	public List<String> readAuthority(String username){
		return sqlsession.selectList("");
	}
	
	public UserVO selectUserById(String id){
		return  sqlsession.selectOne("userDB.checkUser",id);
	}
	
	//회원가입
	public void save(UserVO user) {
		user.setAutho("role_user");
		sqlsession.insert("userDB.saveUser",user);
	}
	
	// 회원가입 시 권한부여
	public void saveAuth() {
		sqlsession.insert("userDB.saveUserAuth");
	}
	
	public UserVO selectUsers(UserVO vo) {
		return sqlsession.selectOne("userDB.selectUser",vo);	
	}
}
