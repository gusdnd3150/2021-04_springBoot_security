package com.example.demo.test.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.test.user.dao.TestDAO;
import com.example.demo.test.user.vo.UserVO;

@Component
public class UserDetailServiceImpl implements  UserDetailsService {

	@Autowired
	private TestDAO dao;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
   /*
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserVO user = dao.readUser(userId);
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(user.getAutho()));
		
		user.setAuthorities(authorities);
        //user.setAuthorities(getAuthorities(userId));
        return user;
	}
	
	@Override
	public UserDetails loadUserByUsername(String autho) throws UsernameNotFoundException {
		UserVO user= new UserVO();
		System.out.println(autho);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(autho));
		
		user.setAuthorities(authorities);
        return user;
	}*/

	/*
	@Override
	public Collection<GrantedAuthority> getAuthorities(String userId) {
		//문자열로 스트링 값 즉 role에 대해 정보만 넣어주면 된다 로그인시 이 두메서드를 진행한다?
		List<String> string_authorities = dao.readAuthority(userId);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String authority : string_authorities) {
             authorities.add(new SimpleGrantedAuthority(authority));
        }	
        return authorities;
	}
	 */
	
	@Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserVO userVo = dao.selectUserById(userId);

        SecurityUser securityUser = new SecurityUser();

        if ( userVo != null ) {
            securityUser.setName(userVo.getUsername());         
            securityUser.setUsername(userVo.getUserId());     // principal
            securityUser.setPassword(userVo.getPassword());  // credetial

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(userVo.getAutho()));

            securityUser.setAuthorities(authorities);
            System.out.println("ddddd"+userVo.toString());
        }
        
        return securityUser; // 여기서 return된 UserDetails는 SecurityContext의 Authentication에 등록되어 인증 정보를 갖고 있는다.
    }
	
	

	
}
