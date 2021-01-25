package com.example.demo.test.user.vo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AuthorityVO {
	
	   private int authority_id;  //pk
	   private int id;   //fk
	   private String authority_name;   // 'role_admin'  'role_user'

}
