package com.module.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

@Component
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	public CMap<String, Object> receiveData(CMap<String, Object> cMap) {
		try {
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "BrakeController receiveData Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> receiveSecData(CMap<String, Object> cMap) {
		try {
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "BrakeController receiveData Exception::", e);
		}
		return cMap;
	}
}
