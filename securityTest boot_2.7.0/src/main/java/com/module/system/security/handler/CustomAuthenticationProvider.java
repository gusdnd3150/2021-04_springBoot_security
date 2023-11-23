package com.module.system.security.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.module.system.security.WebSecurityConfig;
import com.module.system.security.test.UserMap;
import com.module.system.security.test.UserService;
import com.module.system.security.test.UserVo;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import javax.annotation.Resource;


/**
 * 전달받은 사용자의 아이디와 비밀번호를 기반으로 비즈니스 로직을 처리하여 사용자의 ‘인증’에 대해서 검증을 수행하는 클래스입니다.
 * CustomAuthenticationFilter로 부터 생성한 토큰을 통하여 ‘UserDetailsService’를 통해 데이터베이스 내에서 정보를 조회합니다.
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	
    //@Resource
    //private UserDetailsService userDetailsService;
    
	@Autowired
    UserService userService;
    
    private BCryptPasswordEncoder passwordEncoder;
    
    
    public CustomAuthenticationProvider(BCryptPasswordEncoder passwordEncoder) {
    	this.passwordEncoder = passwordEncoder;
    }

    @SuppressWarnings("unused")
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	logger.info("2.CustomAuthenticationProvider");
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        // 'AuthenticaionFilter' 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        String userId = token.getName();
        String userPw = (String) token.getCredentials();

        // Spring Security - UserDetailsService를 통해 DB에서 아이디로 사용자 조회
        //UserVo userDetailsDto = (UserVo) userService.loadUserByUsername(userId);
        UserMap<String, Object> userDetailsDto = (UserMap<String, Object>) userService.loadUserByUsername(userId);
        //String testPw = "$2a$12$nbdnxhPtgAul5TMzXoL2m.9lqW7pGONwcYRh3KS4LzfGqeYdJiA/C"; // tess2
        //userDetailsDto.put("LOGIN_PW", testPw);
        
        if(passwordEncoder.matches(userPw, userDetailsDto.getPassword())) {
        	logger.info("비밀번호 매치");
        	logger.info("CustomAuthenticationProvider - login success :: "+ userId);
            return new UsernamePasswordAuthenticationToken(userDetailsDto, userDetailsDto.getPassword(), userDetailsDto.getAuthorities());
        }else if(userDetailsDto == null){
        	logger.info("CustomAuthenticationProvider - userDetailsDto is null ::");
        	throw new RuntimeException("User not Found");  // 커스텀 필요
        }else{
        	logger.info("CustomAuthenticationProvider - password unmatched ::");
            throw new BadCredentialsException(userDetailsDto.getUsername() + " Invalid password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    

}