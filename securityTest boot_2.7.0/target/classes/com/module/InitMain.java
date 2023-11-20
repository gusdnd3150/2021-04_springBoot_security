package com.module;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.module.init.InitManager;
import com.spas.module.socket.ProtocolMain;
import com.spas.module.socket.utility.CMap;


/**
 * 시스템 bean 생성이후 실제적으로 
 *  시스템 프로세서가 시작되는 클래스
 * @author Administrator
 *
 */
@Component
public class InitMain {
	private static final Logger logger = LoggerFactory.getLogger(InitMain.class);
	
	@Value("${server.port}") String webPort;
	@Value("${spring.datasource.driverClassName}") String dbDriver;
	@Value("${spring.datasource.url}") String dbUrl;
	@Value("${spring.datasource.username}") String dbUserId;
	@Value("${spring.datasource.password}") String dbPassWord;
	@Value("${package_id}") String packageId;
	
	@Autowired InitManager initManager;
	
	public void init() {
		try {
			CMap<String, Object> cMap = new CMap<>();
			cMap.put("PKG_ID", packageId);
			initManager.loadDb(cMap);
		} catch (Exception e) {
			////logger.info("runProcess Exception " + e);
		}
	}

	
}
