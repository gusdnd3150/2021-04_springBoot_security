package com.example.demo.test.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.test.user.dao.TestDAO;
import com.example.demo.test.user.vo.UserDetailsVO;
import com.example.demo.test.user.vo.UserVO;


@Service
public class UserService implements UserDetailsService {
	
	
	@Autowired
	TestDAO dao;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	UserDetailsService userDetailsService;

	//회원가입
		public String checkJoinUser(UserVO vo) {
			String result=null;
			
			try {
				UserVO list= dao.selectUserById(vo.getUserId());
				if(list==null) {
							vo.setPassword(encoder.encode(vo.getPassword()));
							dao.save(vo);
							//dao.saveAuth();
							result="success";
				}else {
					result="already";
				}
			} catch (Exception e) {
				e.printStackTrace();
				result="fail";
			}
			return result;
		}
		
		
		
		public String checkLogin(UserVO vo,HttpServletRequest request) {
			String result =null;
			HttpSession session =request.getSession();
			try {
				UserVO user = dao.selectUsers(vo);
				if(user==null) {
					result="not exist";
				}else if(!encoder.matches(vo.getPassword(), user.getPassword())) {
					result="not matched";
				}else {
					
					userDetailsService.loadUserByUsername(user.getUserId());
					//session.setAttribute("LOGIN", user);
					result="success";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				result="fail";
			}
			
			return result;
		}



		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			UserDetailsVO user = new UserDetailsVO();
			System.out.println("유저 파람정보:"+username);
			user.setUsername("user");
			user.setPassword("123");
			List<String> authoList = new ArrayList<String>();
			authoList.add("USER");
			System.out.println("검사한다~");
			return user;
		}
	
}
