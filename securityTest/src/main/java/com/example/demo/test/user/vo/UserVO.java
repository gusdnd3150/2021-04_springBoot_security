package com.example.demo.test.user.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserVO  {
	
	private int id;
	private String userId;
	private String username;
	private String email;
	private String password;
	private String autho;

    

}
