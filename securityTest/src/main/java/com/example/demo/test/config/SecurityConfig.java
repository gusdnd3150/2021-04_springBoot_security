package com.example.demo.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.test.user.service.UserService;

import lombok.extern.java.Log;

@Log
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Override
    public void configure(WebSecurity web) {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	 http.authorizeRequests()
         .antMatchers("/user/update", "/user/logout").authenticated()
         .antMatchers("/board/write", "/board/delte", "/board/modify", "/board/list").authenticated()
         .antMatchers("/admin/**").hasAuthority("role_admin")
         .anyRequest().permitAll();

     http.csrf().disable();

     // 2. 로그인 설정
     http
         .formLogin()
         .loginPage("/loginForm.do") 	// 로그인 페이지 url
         .loginProcessingUrl("loginForm")  // view form의 action과 맞아야함
         .failureUrl("/user/login?error=fail") // 로그인 실패시 redirect
         .defaultSuccessUrl("/main.do", true) // 로그인 성공시
        // .successHandler(MySimpleUrlAuthenticationSuccessHandler())
         .usernameParameter("username")  // 로그인 요청시 id용 파라미터 (메소드 이름이 usernameParameter로 무조건 써야하지만, 파라미터는 email이든 id이든 name이든 상관없다.)
         .passwordParameter("password");	// 로그인 요청시 password용 파라미터

     // 3. 로그아웃 설정
     http
         .logout()
         .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
         .logoutSuccessUrl("/") // 로그아웃 성공시
         .invalidateHttpSession(true);
    }

  
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
         auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    
	/*
	 * @Bean public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
	 * return new MySimpleUrlAuthenticationSuccessHandler(); }
	 */
    
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }
}
