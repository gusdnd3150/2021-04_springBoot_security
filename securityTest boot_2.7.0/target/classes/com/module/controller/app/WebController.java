package com.module.controller.app;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.module.service.PbsService;
import com.module.service.PlcService;
import com.module.service.VccService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

@Controller
public class WebController {
	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	public CMap<String, Object> keepAlive(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			SendHandler.sendChannel(channel, "WEB_KEEP_ALIVE", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "WebController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> receiveKeepAlive(CMap<String, Object> cMap) {
		try {
			logger.info("킵어라이브 응답 수신");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "WebController receiveKeepAlive Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> receiveMakeBeEmpCar(CMap<String, Object> cMap) {
		try {
			logger.info("대상 이전 공대차 생성");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("MES_PROD_SEQ", cMap.getLong("MES_PROD_SEQ")-1);
			cMap.put("BODY_NO", "99"+cMap.getString("MES_PROD_SEQ").substring(9, 17));
			cMap.put("TAG_ID", "99"+cMap.getString("MES_PROD_SEQ").substring(5, 17));
			SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CREATE_EMPTY_CAR", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "WebController receiveMakeBeEmpCar Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> receiveMakeAfEmpCar(CMap<String, Object> cMap) {
		try {
			logger.info("대상 다음 공대차 생성");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("MES_PROD_SEQ", cMap.getLong("MES_PROD_SEQ")+1);
			cMap.put("BODY_NO", "99"+cMap.getString("MES_PROD_SEQ").substring(9, 17));
			cMap.put("TAG_ID", "99"+cMap.getString("MES_PROD_SEQ").substring(5, 17));
			SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CREATE_EMPTY_CAR", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "WebController receiveMakeAfEmpCar Exception::", e);
		}
		return cMap;
	}
}