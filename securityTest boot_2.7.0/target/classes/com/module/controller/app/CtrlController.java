package com.module.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.module.service.CtrlService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

@Component
public class CtrlController {
	private static final Logger logger = LoggerFactory.getLogger(CtrlController.class);
	
	@Autowired CtrlService ctrlService;
	
	/**
	 * 
	 */
	public CMap<String, Object> activeCtrl(CMap<String, Object> cMap) {
		try {
			logger.info("CtrlController activeCtrl" + cMap);
			Channel channel = (Channel) cMap.get("CHANNEL");
			cMap.put("IP_ADDR", SocketUtility.socketAddressToIp(channel.remoteAddress()));
			cMap.putAll(ctrlService.selectCtrlInfo(cMap));
			SendHandler.sendChannel(channel, "CTRL_INFO", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CtrlController activeCtrl Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 
	 */
	public CMap<String, Object> reciveLsSig(CMap<String, Object> cMap) {
		try {
//			logger.info("CtrlController reciveLsSig:: " + cMap);
			logger.info("LS_NO:: " + cMap.getString("LS_NO"));
			Channel reciveChannel = (Channel) cMap.get("RECIVE_CHANNEL");
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CtrlController reciveLsSig Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 
	 */
	public CMap<String, Object> reciveKeepAlive(CMap<String, Object> cMap) {
		try {
			logger.info("CtrlController reciveKeepAlive:: " + cMap.getString("CTRL_ID"));
			Channel reciveChannel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("CTRL_RETURN_MID", cMap.getString("MSG_ID_REL"));
			SendHandler.sendChannel(reciveChannel, "CTRL_KEEP_ALIVE_ACK", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CtrlController reciveKeepAlive Exception::", e);
		}
		return cMap;
	}
	
}
