/**
 * 
 */
package com.module.util.system.security.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author gusdn
 *
 */
@Component
public class  UserVo implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String userPassword;
	private Collection<? extends GrantedAuthority> authorities;
	
	public UserVo() {}
	
	public UserVo(Map<String, Object> user, Collection<? extends GrantedAuthority> authorities) {
		this.userName = user.get("userName").toString();
		this.userPassword = user.get("userPassword").toString();
		this.authorities = authorities;
	}
	
	public UserVo(UserVo user, Collection<? extends GrantedAuthority> authorities) {
		this.userName = user.getUsername();
		this.userPassword = user.getPassword();
		this.authorities = authorities;
	}
	
	public UserVo(String userName , String userPassword, Collection<? extends GrantedAuthority> authorities) {
		this.userName = userName;
		this.userPassword = userPassword;
		this.authorities = authorities;
	}
	
	public UserVo(String userName ,  Collection<? extends GrantedAuthority> authorities) {
		this.userName = userName;
		this.authorities = authorities;
	}
	
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}


	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return userPassword;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}
	
	
	public List<String> getAuthListAsString() {
		// TODO Auto-generated method stub
		List<String> roles = new ArrayList<String>();
		for(GrantedAuthority e : authorities) {
			roles.add(e.getAuthority());
		}
		return roles;
	}


	///////////////////// 자격증명 관련 함수
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
