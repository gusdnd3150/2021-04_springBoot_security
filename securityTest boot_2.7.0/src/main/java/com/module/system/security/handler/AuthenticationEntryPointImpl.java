/**
 * 
 */
package com.module.system.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


/**
 * @author gusdn
 *
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPointImpl.class);
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		logger.info("AuthenticationEntryPointImpl");;
		
	}

}