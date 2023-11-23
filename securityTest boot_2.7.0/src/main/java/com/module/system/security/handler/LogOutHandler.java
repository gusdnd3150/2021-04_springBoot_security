/**
 * 
 */
package com.module.system.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.module.system.security.test.UserService;
import com.module.system.security.test.UserVo;

/**
 * @author gusdn
 *
 */
public class LogOutHandler implements LogoutHandler {

	private static final Logger logger = LoggerFactory.getLogger(LogOutHandler.class);

	@Autowired
	UserService userService;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter printWriter =  null;
		HashMap<String, Object> responseMap = new HashMap<>();
		JSONObject returnData = null;

		try {
			// 'AuthenticaionFilter' 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
			String userId = token.getName();
			// String userPw = (String) token.getCredentials();
			UserVo userDetailsDto = (UserVo) userService.loadUserByUsername(userId);

			SecurityContextHolder.clearContext();

			
			logger.info("AccessDeniedHandler");

			responseMap.put("message", "access denied");

			returnData = new JSONObject(responseMap);
			response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printWriter.print(returnData); // 최종 저장된 '사용자 정보', '사이트 정보' Front 전달
		printWriter.flush();
		printWriter.close();

	}

}
