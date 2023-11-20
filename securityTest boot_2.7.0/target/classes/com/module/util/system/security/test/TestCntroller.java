/**
 * 
 */
package com.module.util.system.security.test;

import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.module.util.system.security.utils.TokenUtils;

/**
 * @author gusdn
 *
 */
@CrossOrigin
@RestController
public class TestCntroller {
	
	
	@Autowired
	UserService userService;
	
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	
	
	public TestCntroller(AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}
    
    private static final Logger logger = LoggerFactory.getLogger(TestCntroller.class);
    
    
    /**
     * Description : 로그인 처리 
     * @author : Hyeon woong
     * @since : 2023. 11. 10
     */
    @PostMapping("/loginProcess")
    public String login(@RequestBody UserVo user) {
    	logger.info("ttttttttttttttt"+user.getPassword());
    	logger.info("ttttttttttttttt"+user.getUsername());
    	//String token =userService.login(user);

    	UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        String token  = TokenUtils.createToken(authentication);

        
    	return token;
    }
    
}
