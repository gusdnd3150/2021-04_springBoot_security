package com.module.util.system.security.handler;


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

import com.module.util.system.security.WebSecurityConfig;
import com.module.util.system.security.test.UserService;
import com.module.util.system.security.test.UserVo;

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

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	logger.info("2.CustomAuthenticationProvider");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // 'AuthenticaionFilter' 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        String userId = token.getName();
        String userPw = (String) token.getCredentials();

        // Spring Security - UserDetailsService를 통해 DB에서 아이디로 사용자 조회
        UserVo userDetailsDto = (UserVo) userService.loadUserByUsername(userId);

        logger.info("CustomAuthenticationProvider - authenticate ::"+userDetailsDto);
        if (!(userDetailsDto.getPassword().equalsIgnoreCase(userPw) && userDetailsDto.getPassword().equalsIgnoreCase(userPw))) {
        	logger.info("CustomAuthenticationProvider - authenticate mimated ::");
            throw new BadCredentialsException(userDetailsDto.getUsername() + "Invalid password");
        }
        logger.info("CustomAuthenticationProvider - authenticate success ::");
        return new UsernamePasswordAuthenticationToken(userDetailsDto, userPw, userDetailsDto.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    

}