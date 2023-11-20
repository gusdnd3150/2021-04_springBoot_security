package com.module.util.system.security;

import com.module.util.system.security.filter.CustomAuthenticationFilter;

import com.module.util.system.security.filter.JwtAuthorizationFilter;
import com.module.util.system.security.handler.AccessDeniedHandlerImpl;
import com.module.util.system.security.handler.AuthenticationEntryPointImpl;
import com.module.util.system.security.handler.CustomAuthFailureHandler;
import com.module.util.system.security.handler.CustomAuthSuccessHandler;
import com.module.util.system.security.handler.CustomAuthenticationProvider;
import com.module.util.system.security.test.UserService;
import com.module.util.system.security.utils.TokenUtils;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 환경 설정을 구성하기 위한 클래스입니다.
 * 웹 서비스가 로드 될때 Spring Container 의해 관리가 되는 클래스이며 사용자에 대한 ‘인증’과 ‘인가’에 대한 구성을 Bean 메서드로 주입을 한다.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	
	final String[] GET_WHITELIST = new String[]{
            "/loginProcess",
            "/login",
            "/user/login-id/**",
            "/user/email/**",
            "/affiliate"
    };

    final String[] POST_WHITELIST = new String[]{
            "/client-user",
            "/loginProcess"
    };
    
    /**
     * 1. 정적 자원(Resource)에 대해서 인증된 사용자가  정적 자원의 접근에 대해 ‘인가’에 대한 설정을 담당하는 메서드이다.
     *
     * @return WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 자원에 대해서 Security를 적용하지 않음으로 설정
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * 2. HTTP에 대해서 ‘인증’과 ‘인가’를 담당하는 메서드이며 필터를 통해 인증 방식과 인증 절차에 대해서 등록하며 설정을 담당하는 메서드이다.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
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
        .anyRequest().authenticated() // 나머지 요청에 대해서는 인증을 요구
        .and() // 로그인하는 경우에 대해 설정함
        .formLogin().disable() // 로그인 페이지 사용 안함
//        //.loginPage("/login") // 로그인 성공 URL을 설정함
//        .successForwardUrl("/index") // 로그인 실패 URL을 설정함
//        .failureForwardUrl("/index").permitAll()
//        .and()
        .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }


    /**
     * 3. authenticate 의 인증 메서드를 제공하는 매니져로'Provider'의 인터페이스를 의미합니다.
     * - 과정: CustomAuthenticationFilter → AuthenticationManager(interface) → CustomAuthenticationProvider(implements)
     *
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    /**
     * 4. '인증' 제공자로 사용자의 이름과 비밀번호가 요구됩니다.
     * - 과정: CustomAuthenticationFilter → AuthenticationManager(interface) → CustomAuthenticationProvider(implements)
     *
     * @return CustomAuthenticationProvider
     */
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder());
    }

    /**
     * 5. 비밀번호를 암호화하기 위한 BCrypt 인코딩을 통하여 비밀번호에 대한 암호화를 수행합니다.
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 6. 커스텀을 수행한 '인증' 필터로 접근 URL, 데이터 전달방식(form) 등 인증 과정 및 인증 후 처리에 대한 설정을 구성하는 메서드입니다.
     *
     * @return CustomAuthenticationFilter
     */
    
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/loginProcess");     // 로그인 처리 url
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());    // '인증' 성공 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler());    // '인증' 실패 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }
    
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
    

    /**
     * 7. Spring Security 기반의 사용자의 정보가 맞을 경우 수행이 되며 결과값을 리턴해주는 Handler
     *
     * @return CustomLoginSuccessHandler
     */
    @Bean
    public CustomAuthSuccessHandler customLoginSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }

    /**
     * 8. Spring Security 기반의 사용자의 정보가 맞지 않을 경우 수행이 되며 결과값을 리턴해주는 Handler
     *
     * @return CustomAuthFailureHandler
     */
    @Bean
    public CustomAuthFailureHandler customLoginFailureHandler() {
        return new CustomAuthFailureHandler();
    }


    /**
     * 9. JWT 토큰을 통하여서 사용자를 인증합니다.
     *
     * @return JwtAuthorizationFilter
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }
    
    
    @Bean
    public AccessDeniedHandlerImpl accessDeniedHandler(){
    	return new  AccessDeniedHandlerImpl();
    }
    
    @Bean
    public AuthenticationEntryPointImpl authenticationEntryPointHandler(){
    	return new  AuthenticationEntryPointImpl();
    }


}
