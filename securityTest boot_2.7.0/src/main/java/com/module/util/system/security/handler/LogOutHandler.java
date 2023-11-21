/**
 * 
 */
package com.module.util.system.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.module.util.system.security.test.UserService;
import com.module.util.system.security.test.UserVo;

/**
 * @author gusdn
 *
 */
public class LogOutHandler  implements LogoutSuccessHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(LogOutHandler.class);
	
	@Autowired
	UserService userService;
	
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		logger.info("token::" + token);
		//logger.info("token::" + token.getCredentials());
		userService.loadUserByUsername("logout test");
		//SecurityContextHolder.clearContext();
		logger.info("onLogoutSuccess");
	}

}
