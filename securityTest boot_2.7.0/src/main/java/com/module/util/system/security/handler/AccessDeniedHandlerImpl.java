/**
 * 
 */
package com.module.util.system.security.handler;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.module.util.system.security.utils.TokenUtils;

/**
 * @author gusdn
 *
 */
@Component
public class AccessDeniedHandlerImpl  implements AccessDeniedHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);
	
	
	@Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		HashMap<String, Object> responseMap = new HashMap<>();
		logger.info("AccessDeniedHandler");
		
		responseMap.put("message", "access denied");
		
		JSONObject returnData = new JSONObject(responseMap);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter printWriter = response.getWriter();
		printWriter.print(returnData); // 최종 저장된 '사용자 정보', '사이트 정보' Front 전달
		printWriter.flush();
		printWriter.close();
    }
}
