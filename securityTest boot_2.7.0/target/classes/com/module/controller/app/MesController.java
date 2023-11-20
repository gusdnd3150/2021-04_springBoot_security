package com.module.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.module.service.MesService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

@Controller
public class MesController {
	private static final Logger logger = LoggerFactory.getLogger(MesController.class);
	
	@Autowired MesService mesService;
	
	public CMap<String, Object> receiveMesProdIf(CMap<String, Object> cMap) {
		try {
			logger.info("MesController receiveMesProdIf" + cMap);

			cMap.put("DP_CMT", SocketUtility.padRight(cMap.getString("DP_CMT").trim(), 4, '0'));
			cMap.put("BODY_NO", cMap.getString("CAR_CD") + cMap.getString("BODY_NO_6"));
			// cMap.put("U_PART", new String(byteUpart));
			// cMap.put("C_PART", new String(byteCpart));
			// cMap.put("P_PART", new String(bytePpart));
			// cMap.put("Q_PART", new String(byteQpart));

			mesService.insertTbIfMesProd(cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			
			CMap<String, Object> mesProdMap = mesService.selectIfMesProd(cMap);
			if (null == mesProdMap) {
				return null;
			}
			cMap.put("MES_PROD_SEQ", mesProdMap.get("MES_PROD_SEQ"));
			cMap.put("PROC_CD", "T000");
			CMap<String, Object> mappingTagMap = mesService.selectMaxRcReportData(cMap);
			if (null != mappingTagMap) {
				cMap.putAll(mappingTagMap);
			}
			
			byte[] returnBytes = cMap.getBytes("TOTAL_BYTES");
			SendHandler.sendChannel(channel, returnBytes); // TODO 김범주 책임님쪽 로직 완료되면 주석해제 
			if ("NORMAL".equals(cMap.getString("LEAVE_DIV").trim())) {
				SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CREATE_CAR", cMap);
			} else {
				SendHandler.sendSkId("TCPS_VCC_TO_CORE", "DELETE_CAR", cMap);
				mesService.deleteRcProcReportBody(cMap);
				mesService.deleteRcProcProcInoutBody(cMap);
				// TODO 공정보고, 공정진입. 전체 이력 삭제
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
}
