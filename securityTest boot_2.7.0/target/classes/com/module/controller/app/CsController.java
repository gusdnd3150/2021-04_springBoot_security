package com.module.controller.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
public class CsController {
	private static final Logger logger = LoggerFactory.getLogger(CsController.class);
	
	@Autowired VccService vccService;
	
	@Autowired PlcService plcService;
	
	@Autowired PbsService pbsService;
	
	public CMap<String, Object> schSendLeaveList(CMap<String, Object> cMap) {// 탈하 목록
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			List<CMap<String, Object>> LeaveList = pbsService.selectListLeaveList(cMap);
			JSONArray ja = new JSONArray();
			for (CMap<String, Object> LeaveMap : LeaveList) {
				JSONObject jo = new JSONObject();
				jo.put("BODY_NO", LeaveMap.getString("BODY_NO"));
				jo.put("DATE_TIME", LeaveMap.getString("REG_DT"));
				
				ja.put(jo);
			}
			cMap.put("DATAS", ja);
			SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_LIST_RID", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 킵어라이브 전송
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> activeCs(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			plcService.csReportData(cMap);
			schSendLeaveList(cMap);
			plcService.csExpData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 킵어라이브 전송
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> keepAlive(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			CMap<String, Object> plcChkMap = pbsService.selectPbsPlcStat(cMap);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime currentTime = LocalDateTime.now();
			LocalDateTime heartBeatTime = LocalDateTime.parse(plcChkMap.getString("HEART_BIT"), formatter);
			LocalDateTime preBodyNoTime = LocalDateTime.parse(plcChkMap.getString("PRE_BODY_NO"), formatter);
			long heartBeatSeconds = ChronoUnit.SECONDS.between(heartBeatTime, currentTime);
			long preBodyNoSeconds = ChronoUnit.SECONDS.between(preBodyNoTime, currentTime);// 이친구는 애매해서 보류.. ( && preBodyNoSeconds <= 70)
			if (heartBeatSeconds <= 10) {
				cMap.put("PLC", "Y");
			} else {
				cMap.put("PLC", "N");
			}
			SendHandler.sendChannel(channel, "CS_KEEP_ALIVE", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 킵어라이브 수신
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> receiveKeepAlive(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * TTS 요청 수신
	 * REQ_TTS전문 수신
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> receiveTtsReq(CMap<String, Object> cMap) {
		Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		try {
			logger.info("receiveTtsReq" + cMap);
			CMap<String, Object> tagMap = vccService.selectTagTagId(cMap);
			if (null == tagMap) {
				cMap.put("RESULT", "FAIL");
				SendHandler.sendChannel(channel, "CS_RES_TTS", cMap);
				return cMap;
			}
			logger.info("태그 정보 있고,");
			
			CMap<String, Object> vccSendMap = new CMap<>();
			vccSendMap.putAll(cMap);
			vccSendMap.put("TAG_ID", tagMap.getString("MAC_ADDR"));
			
			CMap<String, Object> posMap = vccService.selectUseTag(vccSendMap);
			logger.info("#############" + posMap);
			if (null != posMap && null != vccSendMap.get("BODY_NO") && null != posMap.get("BODY_NO") && vccSendMap.get("BODY_NO") != posMap.get("BODY_NO") && !"PBSL".equals(posMap.getString("LINE_CD")) && !"1".equals(posMap.getString("MES_PROD_SEQ").substring(0, 1))) {
				cMap.put("RESULT", "FAIL");
				SendHandler.sendChannel(channel, "CS_RES_TTS", cMap);
				return cMap;
			}
			logger.info("vccSendMap 확인 ::" + vccSendMap);
			vccSendMap.put("PROC_CD", "P400");
			if (null == vccSendMap.get("MES_PROD_SEQ") || "".equals(vccSendMap.getString("MES_PROD_SEQ").trim())) {
				vccSendMap.putAll(pbsService.selectSqMesProdPbs(cMap));
			}
			vccSendMap.put("ERROR_FLAG", "");
			vccSendMap.put("REPORT_FLAG", "");
			cMap.put("RESULT", "OK");
			plcService.mergePbsReportBody(vccSendMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController receiveTtsReq Exception::", e);
			cMap.put("RESULT", "FAIL");
			SendHandler.sendChannel(channel, "CS_RES_TTS", cMap);
			return cMap;
		}
		return cMap;
	}
	
	/**
	 * 공정보고 요청 수신
	 * REQ_OUT전문 수신
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> receiveProcReport(CMap<String, Object> cMap) {
		try {
			logger.info("receiveProcReport" + cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			CMap<String, Object> saveMap = new CMap<>();
			saveMap.putAll(cMap);
			saveMap.put("PROC_CD", "T000");
			if (null == cMap.get("TAG_ID") || "".equals(cMap.getString("TAG_ID"))) {
				cMap.put("RESULT", "FAIL");
				SendHandler.sendChannel(channel, "CS_RES_OUT", cMap);
				return null;
			}
			
			CMap<String, Object> tagMap = vccService.selectTagTagId(cMap);
			if (null == tagMap) {
				cMap.put("RESULT", "FAIL");
				SendHandler.sendChannel(channel, "CS_RES_OUT", cMap);
				return null;
			}
//			tagMap.putAll(cMap);
//			tagMap.put("TAG_ID", tagMap.getString("MAC_ADDR"));
//			
//			CMap<String, Object> posMap = vccService.selectUseTag(tagMap);
//			if (null != posMap && null != tagMap.get("BODY_NO") && null != posMap.get("BODY_NO") && tagMap.get("BODY_NO") != posMap.get("BODY_NO")) {
//				cMap.put("RESULT", "FAIL");
//				SendHandler.sendChannel(channel, "CS_RES_OUT", cMap);
//				return cMap;
//			}
			
			saveMap.put("TAG_ID", tagMap.getString("MAC_ADDR"));
			plcService.mergePbsReportBody(saveMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 강제 공정보고 요청 수신
	 * REQ_OUT_FORCE전문 수신
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> receiveProcReportForce(CMap<String, Object> cMap) {
		try {
			logger.info("receiveProcReportForce" + cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			CMap<String, Object> saveMap = new CMap<>();
			saveMap.putAll(cMap);
			saveMap.put("PROC_CD", "T000");
			saveMap.put("TAG_ID", vccService.selectTagTagId(cMap).getString("MAC_ADDR"));
			plcService.mergePbsReportBody(saveMap);
			
			SendHandler.sendSkId("TCPS_VCC_TO_CORE", "PBS_OUT_TTS_CAR", saveMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController keepAlive Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 강제 공정보고 요청 수신
	 * REQ_OUT_FORCE전문 수신
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> receiveProcReportException(CMap<String, Object> cMap) {
		try {
			logger.info("receiveProcReportException" + cMap);
			cMap.put("PROC_CD", "BE_RP");
			cMap.put("REPORT_FLAG", "3");
			int intUpdateCount = 0;
			CMap<String, Object> saveMap = new CMap<>();
			saveMap.putAll(cMap);
			saveMap.put("TAG_ID", vccService.selectTagTagId(cMap).getString("MAC_ADDR"));
			intUpdateCount = plcService.mergePbsReportBody(saveMap);
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@ " + intUpdateCount);
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@ " + cMap);
			if (intUpdateCount <= 0) {
				cMap.put("RESULT", "FAIL");
			} else {
				cMap.put("RESULT", "OK");
			}
			pbsService.insertTbRcExp(cMap);
			SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_OUT_EXP", cMap);
			plcService.csReportData(cMap);
			plcService.csExpData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "CsController receiveProcReportException Exception::", e);
		}
		return cMap;
	}
}