package com.module;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spas.module.MainProcess;

@SpringBootApplication
@EnableScheduling
@ComponentScan(value={"com.module", "com.spas.module"})
@MapperScan({"com.spas.module.dao", "com.module.dao"})
public class Application extends SpringBootServletInitializer {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
    	SpringApplicationBuilder sab = new SpringApplicationBuilder(Application.class);
    	ctx = sab.headless(false).run(args);
    	com.spas.module.Application.ctx = sab.headless(true).run(args);
    	
    	InitMain initMain = ctx.getBean(InitMain.class); 
    	initMain.init();
		
		MainProcess mainProcess = ctx.getBean(MainProcess.class); 
    	mainProcess.runProcess();
	}

    /**
     * 어플리케이션 컨텐츠의 bean 객체를 반환합니다
     * @param obj
     * @return
     */
    public static Object getBean(Object obj){
    	String objFullName = obj.toString();
    	int iLet = objFullName.lastIndexOf(".");
    	String objName = objFullName.substring(iLet + 1);
    	objName = objName.substring(0, 1).toLowerCase() + objName.substring(1);
    	return ctx.getBean(objName);
    }
    
    public static Object getBean(String strBean){
    	strBean = strBean.substring(0, 1).toLowerCase() + strBean.substring(1);
    	return ctx.getBean(strBean);
    }
}
