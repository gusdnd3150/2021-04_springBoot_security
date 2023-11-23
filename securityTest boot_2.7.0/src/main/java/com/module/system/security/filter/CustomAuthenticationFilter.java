package com.module.system.security.filter;



import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.system.security.WebSecurityConfig;
import com.module.system.security.test.UserMap;
import com.module.system.security.test.UserVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 아이디와 비밀번호 기반의 데이터를 Form 데이터로 전송을 받아 '인증'을 담당하는 필터입니다.
 */
//@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
	
	
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final UsernamePasswordAuthenticationToken authRequest;
        
        logger.info("[STEP1]:CustomAuthenticationFilter"+request);
        UserVo loginDTO;
        try {
            loginDTO = new ObjectMapper().readValue(request.getInputStream(), UserVo.class);
            authRequest = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        } catch (IOException e) {
        	e.printStackTrace();
        	throw new UsernameNotFoundException(e.getMessage());
        }
        setDetails(request, authRequest);

        // AuthenticationManager에게 전달 -> AuthenticationProvider의 인증 메서드 실행
        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
  

//    /**
//     * 지정된 URL로 form 전송을 하였을 경우 파라미터 정보를 가져온다.
//     *
//     * @param request  from which to extract parameters and perform the authentication
//     * @param response the response, which may be needed if the implementation has to do a
//     *                 redirect as part of a multi-stage authentication process (such as OpenID).
//     * @return Authentication {}
//     * @throws AuthenticationException {}
//     */
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        UsernamePasswordAuthenticationToken authRequest;
//        try {
//            authRequest = getAuthRequest(request);
//            setDetails(request, authRequest);
//            return this.getAuthenticationManager().authenticate(authRequest);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        	return null;
//            //throw new BusinessExceptionHandler(e.getMessage(), ErrorCode.BUSINESS_EXCEPTION_ERROR);
//        }
//
//    }
//
//
//    /**
//     * Request로 받은 ID와 패스워드 기반으로 토큰을 발급한다.
//     *
//     * @param request HttpServletRequest
//     * @return UsernamePasswordAuthenticationToken
//     * @throws Exception e
//     */
//    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) throws Exception {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
//            UserVo user = objectMapper.readValue(request.getInputStream(), UserVo.class);
//            logger.info("1.CustomAuthenticationFilter :: userId:" + user.getUsername() + " userPw:" + user.getPassword());
//
//            // ID와 패스워드를 기반으로 토큰 발급
//            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
//        } catch (UsernameNotFoundException ae) {
//            throw new UsernameNotFoundException(ae.getMessage());
//        } catch (Exception e) {
//        	throw new UsernameNotFoundException(e.getMessage());
//            //throw new BusinessExceptionHandler(e.getMessage(), ErrorCode.IO_ERROR);
//        }
//
//    }
//    */
}
