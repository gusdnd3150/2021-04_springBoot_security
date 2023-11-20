
package com.module.util.system.security.handler;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.module.util.system.security.WebSecurityConfig;
import com.module.util.system.security.test.UserVo;
import com.module.util.system.security.utils.TokenUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * 사용자의 ‘인증’에 대해 성공하였을 경우 수행되는 Handler로 성공에 대한 사용자에게 반환값을 구성하여 전달합니다
 */
@Configuration
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		logger.debug("3.1. CustomLoginSuccessHandler");

		// [STEP1] 사용자와 관련된 정보를 모두 조회합니다.
		UserVo userDto = ((UserVo) authentication.getPrincipal());

		// [STEP2] 조회한 데이터를 JSONObject 형태로 파싱을 수행합니다.
		HashMap<String, Object> responseMap = new HashMap<>();
		
		responseMap.put("token", TokenUtils.createToken(authentication));
		responseMap.put("userName",userDto.getUserName());
		responseMap.put("auth", userDto.getAuthListAsString());
		
		JSONObject returnData = new JSONObject(responseMap);

		// [STEP1] 사용자의 상태가 '휴면 상태' 인 경우 응답 값으로 전달 할 데이터
//        if (userDto.get) {
//            responseMap.put("userInfo", userVoObj);
//            responseMap.put("resultCode", 9001);
//            responseMap.put("token", null);
//            responseMap.put("failMsg", "휴면 계정입니다.");
//            jsonObject = new JSONObject(responseMap);
//        }

		// [STEP2] 사용자의 상태가 '휴면 상태'가 아닌 경우 응답 값으로 전달 할 데이터
//        else {
//            // 1. 일반 계정일 경우 데이터 세팅
//            responseMap.put("userInfo", userVoObj);
//            responseMap.put("resultCode", 200);
//            responseMap.put("failMsg", null);
//            jsonObject = new JSONObject(responseMap);
//
//            // TODO: 추후 JWT 발급에 사용 할 예정
//            // String token = TokenUtils.generateJwtToken(userVo);
//            // jsonObject.put("token", token);
//            // response.addHeader(AuthConstants.AUTH_HEADER, AuthConstants.TOKEN_TYPE + " " + token);
//        }

		// [STEP3] 구성한 응답 값을 전달합니다.
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter printWriter = response.getWriter();
		printWriter.print(returnData); // 최종 저장된 '사용자 정보', '사이트 정보' Front 전달
		printWriter.flush();
		printWriter.close();
	}
}
