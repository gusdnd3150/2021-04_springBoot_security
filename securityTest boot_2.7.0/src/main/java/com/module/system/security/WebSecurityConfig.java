package com.module.system.security;

import com.module.system.security.filter.CustomAuthenticationFilter;
import com.module.system.security.filter.JwtAuthorizationFilter;
import com.module.system.security.handler.AccessDeniedHandlerImpl;
import com.module.system.security.handler.AuthenticationEntryPointImpl;
import com.module.system.security.handler.CustomAuthFailureHandler;
import com.module.system.security.handler.CustomAuthSuccessHandler;
import com.module.system.security.handler.CustomAuthenticationProvider;
import com.module.system.security.handler.LogOutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	
	// GET 예외 처리
	final String[] GET_WHITELIST = new String[]{
            "/loginProcess",
            "/login",
            "/user/login-id/**",
            "/user/email/**",
            "/affiliate",
            "/socket",
            "/system",
    };

	// POST 예외 처리
    final String[] POST_WHITELIST = new String[]{
            "/client-user",
            "/loginProcess"
    };
    
    /**
    정적 자원(Resource)에 대해서 인증된 사용자가  정적 자원의 접근에 대해 ‘인가’에 대한 설정을 담당하는 메서드이다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 자원에 대해서 Security를 적용하지 않음으로 설정
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * HTTP에 대해서 ‘인증’과 ‘인가’ 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("[+] WebSecurityConfig Start !!! ");
        http
        .cors()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPointHandler()) // 인증 실패
        .accessDeniedHandler(accessDeniedHandler()) // 인가 실패
        .and().authorizeRequests()
        .antMatchers(HttpMethod.GET, GET_WHITELIST).permitAll() // 해당 GET URL은 모두 허용
        .antMatchers(HttpMethod.POST, POST_WHITELIST).permitAll() // 해당 POST URL은 모두 허용
        .antMatchers("/client-user/**").hasAnyRole("ADMIN") // 권한 적용
        .antMatchers("/api/**").permitAll() // 코어 시스템 프리픽스 적용, 웹에서 접근 url 설정
        .anyRequest().authenticated() // 나머지 요청에 대해서는 인증을 요구
        .and() 
        .formLogin().disable() // 로그인 페이지 사용 안함
//        //.loginPage("/login") // 로그인 성공 URL을 설정함
//        .successForwardUrl("/index") // 로그인 실패 URL을 설정함
//        .failureForwardUrl("/index").permitAll()
//        .and()
        .logout()
            .logoutUrl("/logout")
            .addLogoutHandler(logOutHandler())
            //.logoutSuccessHandler(logOutHandler())
            //.invalidateHttpSession(true)
            //.deleteCookies("JSESSIONID")
        .and()
        .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
  
    // 시큐리티 인증을 커스텀 인증으로 대체하여 사용
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    // 실제 유저 정보를 바탕으로 인증처리를 진행 
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder());
    }

    // 비밀번호를 암호화하기 위한 BCrypt 인코딩을 통하여 비밀번호에 대한 암호화를 수행합니다.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    
    // 로그인 url 설정과 인증 핸들러 매핑
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/loginProcess");     // 로그인 처리 url
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());    // '인증' 성공 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler());    // '인증' 실패 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }
    
    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080"); // 허용할 도메인을 추가
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    

     // Spring Security 기반의 사용자의 정보가 맞을 경우 수행이 되며 결과값을 리턴해주는 Handler
    @Bean
    public CustomAuthSuccessHandler customLoginSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }

    // Spring Security 기반의 사용자의 정보가 맞지 않을 경우 수행이 되며 결과값을 리턴해주는 Handler
    @Bean
    public CustomAuthFailureHandler customLoginFailureHandler() {
        return new CustomAuthFailureHandler();
    }


 
     // JWT 토큰을 통하여서 사용자를 인증
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }
    
    
    // 로그인 성공한 사용자의 유저 권한 체크
    @Bean
    public AccessDeniedHandlerImpl accessDeniedHandler(){
    	return new  AccessDeniedHandlerImpl();
    }
    @Bean
    public AuthenticationEntryPointImpl authenticationEntryPointHandler(){
    	return new  AuthenticationEntryPointImpl();
    }
    
    
    @Bean
    public LogOutHandler logOutHandler(){
    	return new LogOutHandler();
    }


}
