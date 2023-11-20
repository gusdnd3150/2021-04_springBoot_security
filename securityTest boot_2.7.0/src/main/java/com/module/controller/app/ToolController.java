package com.module.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.module.init.InitManager;
import com.module.service.ToolService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

@Controller
public class ToolController {
	private static final Logger logger = LoggerFactory.getLogger(ToolController.class);
	
	@Autowired ToolService toolService;
	
	@Autowired InitManager initManager;
	
	public CMap<String, Object> activeAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			SendHandler.sendChannel(channel, "ATL_ACTV_REQ_AND_REV", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> idleAllAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			channel.close();
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> idleReadAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			channel.close();
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> idleWriteAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			channel.close();
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> keepAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
			SendHandler.sendChannel(channel, "ATL_KEEPALIVE", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	
	public CMap<String, Object> reciveKeepAtlasTool(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("CHANNEL");
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveAtlasActiveRes(CMap<String, Object> cMap) {
		try {
			logger.info("ToolController reciveAtlasActiveRes" + cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			CMap<String, Object> toolMap = initManager.listSelMap(initManager.toolInitList, "TOOL_ID", cMap.getString("SK_ID"));
			if (null != toolMap) {
				if ("PF6000".equals(toolMap.getString("TOOL_MODL_CD")) || "PF4000".equals(toolMap.getString("TOOL_MODL_CD"))) {
					SendHandler.sendChannel(channel, "ATL_PF_SUB_RSLT_AND_REV", cMap);
				} else if ("PM4000".equals(toolMap.getString("TOOL_MODL_CD"))) {
					SendHandler.sendChannel(channel, "ATL_PM_SUB_RSLT_AND_REV", cMap);
				}
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveAtlasSetRes(CMap<String, Object> cMap) {
		try {
			String setResId = cMap.getString("ATL_MID");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			if ("0060".equals(setResId)) {
//				TcpSendHandler.sendChannel(channel, "ATL_SET_RES_AND_REV", cMap);
			} else if ("0034".equals(setResId)) {
//				TcpSendHandler.sendChannel(channel, "ATL_SET_RES_AND_REV", cMap);
			} else if ("0051".equals(setResId)) {
//				TcpSendHandler.sendChannel(channel, "ATL_SET_RES_AND_REV", cMap);
			}
		} catch (Exception e) {
			logger.info("ToolController reciveAtlasSetRes Exception::" + e);
		}
		return cMap;
	}
	
	public CMap<String, Object> recivePf6000Rslt(CMap<String, Object> cMap) {
		try {
			String setResId = cMap.getString("ATL_MID");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			
			cMap.put("TORQUE_MIN_LIMIT", SocketUtility.padRight(String.format("%.2f", (double)cMap.getInt("TORQUE_MIN_LIMIT")/100), 6, ' '));
			cMap.put("TORQUE_MAX_LIMIT", SocketUtility.padRight(String.format("%.2f", (double)cMap.getInt("TORQUE_MAX_LIMIT")/100), 6, ' '));
			cMap.put("TORQUE_FINAL_TARGET", SocketUtility.padRight(String.format("%.2f", (double)cMap.getInt("TORQUE_FINAL_TARGET")/100), 6, ' '));
			cMap.put("TORQUE", SocketUtility.padRight(String.format("%.2f", (double)cMap.getInt("TORQUE")/100), 6, ' '));
			
			cMap.put("TOOL_NM", cMap.getString("SK_ID"));
			cMap.put("TOOL_ID", cMap.getString("SK_ID"));
			cMap.put("PSET_NO", cMap.getString("PARAMETER_SET_ID"));
			cMap.put("JOB_NO", cMap.getString("JOB_ID"));
			cMap.put("BATCH_CNT", cMap.getString("BATCH_COUNTER"));
			String strTighteningStatus = "";
			if (null != cMap.getString("TIGHTENING_STATUS").trim()) {
				if ("1".equals(cMap.getString("TIGHTENING_STATUS"))) {
					strTighteningStatus = "OK";
				} else {
					strTighteningStatus = "NOK";
				}
			}
			cMap.put("TIGHTENING_STAT", strTighteningStatus);
			
			String strTorqueStatus = "";
			if (null != cMap.getString("TORQUE_STATUS").trim()) {
				if ("0".equals(cMap.getString("TORQUE_STATUS"))) {
					strTorqueStatus = "Low";
				} else if ("1".equals(cMap.getString("TORQUE_STATUS"))) {
					strTorqueStatus = "OK";
				} else if ("2".equals(cMap.getString("TORQUE_STATUS"))) {
					strTorqueStatus = "High";
				}
			}
			cMap.put("TORQUE_STAT", strTorqueStatus);
			
			
			String strAngleStatus = "";
			if (null != cMap.getString("ANGLE_STATUS").trim()) {
				if ("0".equals(cMap.getString("ANGLE_STATUS"))) {
					strAngleStatus = "Low";
				} else if ("1".equals(cMap.getString("ANGLE_STATUS"))) {
					strAngleStatus = "OK";
				} else if ("2".equals(cMap.getString("ANGLE_STATUS"))) {
					strAngleStatus = "High";
				}
			}
			cMap.put("ANGLE_STAT", strAngleStatus);
			cMap.put("TORQUE_MAX", cMap.getString("TORQUE_MAX_LIMIT"));
			cMap.put("TORQUE_MIN", cMap.getString("TORQUE_MIN_LIMIT"));
			cMap.put("BATCH_STAT", cMap.getString("BATCH_STATUS"));
			cMap.put("PSET_CHANGE_DT", cMap.getString("DT_OF_LAST_CHANGE"));
			
			String bodyNo = "XXX000000";
			if (null != cMap.getString("VIN_NO").trim() && !cMap.getString("VIN_NO").trim().equals("") && cMap.getString("VIN_NO").trim().length() >= 8) {
				String strBcrBodyNo = cMap.getString("VIN_NO").trim();
				CMap<String, Object> carMap = initManager.listSelMap(initManager.carInitList, "BCR_NO", strBcrBodyNo.substring(0, 2));
				String strBodyNo = (carMap.getString("CAR_CD") + " " + strBcrBodyNo.substring(strBcrBodyNo.length()-6));
				
				bodyNo = SocketUtility.padRight(strBodyNo.replace(" ", ""), 9, '0');
				cMap.put("BODY_NO", strBodyNo);
			} else {
				CMap<String, Object> toolInOutMap = toolService.selectToolInOut(cMap);
				if (null != toolInOutMap && null != toolInOutMap.get("BODY_NO")) {
					bodyNo = SocketUtility.padRight(toolInOutMap.getString("BODY_NO").replace(" ", ""), 9, '0');
					cMap.put("BODY_NO", toolInOutMap.getString("BODY_NO"));
				}
			}
			
			toolService.insertTbIfToolRslt(cMap);
			SendHandler.sendChannel(channel, "ATL_PF_RSLT_ACK_AND_REV", cMap);
			
			CMap<String, Object> toolMap = initManager.listSelMap(initManager.toolInitList, "TOOL_ID", cMap.getString("SK_ID"));
			if (null != toolMap && null != toolMap.getString("MES_SEND_YN") && toolMap.getString("MES_SEND_YN").equals("Y")) {
				String mappCd = toolMap.getString("MAPP_CD");
				String batchCounter = SocketUtility.padRight(cMap.getString("BATCH_COUNTER"), 4, '0');
				String tighteningId = SocketUtility.padRight(cMap.getString("TIGHTENING_ID"), 10, '0');
				
				String torque = (cMap.getString("TORQUE").trim().equals("")) ? SocketUtility.padLeft("NA", 6, ' '):SocketUtility.padRight(cMap.getString("TORQUE"), 6, ' ');
				String torqueStatus = (cMap.getString("TORQUE_STATUS").equals("1")) ? "O":"N";
				String angle = (cMap.getString("ANGLE").trim().equals("")) ? SocketUtility.padLeft("NA", 6, ' '):SocketUtility.padRight(cMap.getString("ANGLE"), 6, ' ');
				String angleStatus = (cMap.getString("ANGLE_STATUS").equals("1")) ? "O":"N";
				String parameterSetId = SocketUtility.padRight(cMap.getString("PARAMETER_SET_ID").trim(), 3, '0');
				String batchSize = SocketUtility.padRight(cMap.getString("BATCH_SIZE").trim(), 4, '0');
				String torqueMinLimit = SocketUtility.padRight(cMap.getString("TORQUE_MIN_LIMIT"), 6, ' ');
				String torqueMaxLimit = SocketUtility.padRight(cMap.getString("TORQUE_MAX_LIMIT"), 6, ' ');
				String torqueFinalTarget = SocketUtility.padRight(cMap.getString("TORQUE_FINAL_TARGET"), 6, ' ');
				String angleMin = SocketUtility.padRight(cMap.getString("ANGLE_MIN"), 6, ' ');
				String angleMax = SocketUtility.padRight(cMap.getString("ANGLE_MAX"), 6, ' ');
				String FinalAngleTarget = SocketUtility.padRight(cMap.getString("FINAL_ANGLE_TARGET"), 6, ' ');
				String dtOfLastChange = SocketUtility.padRight(cMap.getString("DT_OF_LAST_CHANGE"), 19, ' ');
				String timeStamp = SocketUtility.padRight(cMap.getString("TIME_STAMP"), 19, ' ');
				
				String strSendByte = mappCd+batchCounter+tighteningId+bodyNo+torque+torqueStatus+angle+angleStatus+parameterSetId+batchSize+torqueMinLimit+torqueMaxLimit+torqueFinalTarget+angleMin+angleMax+FinalAngleTarget+dtOfLastChange+timeStamp;
				CMap<String, Object> mesSendDataMap = new CMap<>();
				mesSendDataMap.put("SK_ID", "TCPC_MES_TOOL_RSLT");
				mesSendDataMap.put("SEND_BYTES", strSendByte);
				mesSendDataMap.put("BODY_NO", cMap.getString("BODY_NO"));
				toolService.insertMesSendData(mesSendDataMap);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "ToolController recivePf6000Rslt Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> recivePm4000_0106Rslt(CMap<String, Object> cMap) {//TODO DB 저장이랑 전송데이터 만드는 방식 협의 필요
		try {
			String setResId = cMap.getString("ATL_MID");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("TOOL_NM", cMap.getString("SK_ID"));
			cMap.put("TOOL_ID", cMap.getString("SK_ID"));
			cMap.put("PSET_NO", cMap.getString("PARAMETER_SET_ID"));
			cMap.put("JOB_NO", cMap.getString("JOB_ID"));
			cMap.put("BATCH_CNT", cMap.getString("BATCH_COUNTER"));
			cMap.put("TIGHTENING_STAT", cMap.getString("TIGHTENING_STATUS"));
			cMap.put("TORQUE_STAT", cMap.getString("TORQUE_STATUS"));
			cMap.put("ANGLE_STAT", cMap.getString("ANGLE_STATUS"));
			cMap.put("TORQUE_MAX", cMap.getString("TORQUE_MAX_LIMIT"));
			cMap.put("TORQUE_MIN", cMap.getString("TORQUE_MIN_LIMIT"));
			cMap.put("BATCH_STAT", cMap.getString("BATCH_STATUS"));
			cMap.put("PSET_CHANGE_DT", cMap.getString("DT_OF_LAST_CHANGE"));
			toolService.insertTbIfToolRslt(cMap);
			
			SendHandler.sendChannel(channel, "ATL_PM_RSLT_ACK_AND_REV", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> recivePm4000_0107Rslt(CMap<String, Object> cMap) {//TODO DB 저장이랑 전송데이터 만드는 방식 협의 필요
		try {
			String setResId = cMap.getString("ATL_MID");
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("TOOL_NM", cMap.getString("SK_ID"));
			cMap.put("TOOL_ID", cMap.getString("SK_ID"));
			cMap.put("PSET_NO", cMap.getString("PARAMETER_SET_ID"));
			cMap.put("JOB_NO", cMap.getString("JOB_ID"));
			cMap.put("BATCH_CNT", cMap.getString("BATCH_COUNTER"));
			cMap.put("TIGHTENING_STAT", cMap.getString("TIGHTENING_STATUS"));
			cMap.put("TORQUE_STAT", cMap.getString("TORQUE_STATUS"));
			cMap.put("ANGLE_STAT", cMap.getString("ANGLE_STATUS"));
			cMap.put("TORQUE_MAx", cMap.getString("TORQUE_MAX_LIMIT"));
			cMap.put("TORQUE_MIN", cMap.getString("TORQUE_MIN_LIMIT"));
			cMap.put("BATCH_STAT", cMap.getString("BATCH_STATUS"));
			cMap.put("PSET_CHANGE_DT", cMap.getString("DT_OF_LAST_CHANGE"));
			toolService.insertTbIfToolRslt(cMap);
			
			SendHandler.sendChannel(channel, "ATL_PM_RSLT_ACK_AND_REV", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveYocotaToolRslt(CMap<String, Object> cMap) {//TODO DB 저장이랑 전송데이터 만드는 방식 협의 필요
		try {
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			
			cMap.put("TOOL_NM", cMap.getString("SK_ID"));
			cMap.put("TOOL_ID", cMap.getString("SK_ID"));
			cMap.put("TORQUE", SocketUtility.padRight(cMap.getString("YOCOTA_TORQUE").trim(), 6, ' '));
			cMap.put("TOTAL_BYTES", new String(cMap.getBytes("TOTAL_BYTES")));
			
			String strTighteningStatus = "OK";
			if (null != cMap.get("YOCOTA_STATUS") && !"".equals(cMap.getString("YOCOTA_STATUS").trim())) {
				strTighteningStatus = "NOK";
			}
			cMap.put("TIGHTENING_STAT", strTighteningStatus);
			String bodyNo = "XXX000000";
			if (null != cMap.get("VIN_NO") && null != cMap.getString("VIN_NO").trim() && !cMap.getString("VIN_NO").trim().equals("") && cMap.getString("VIN_NO").trim().length() >= 8) {
				String strBcrBodyNo = cMap.getString("VIN_NO").trim();
				CMap<String, Object> carMap = initManager.listSelMap(initManager.carInitList, "BCR_NO", strBcrBodyNo.substring(0, 2));
				String strBodyNo = (carMap.getString("CAR_CD") + " " + strBcrBodyNo.substring(strBcrBodyNo.length()-6));

				bodyNo = SocketUtility.padRight(strBodyNo.replace(" ", ""), 9, '0');
				cMap.put("BODY_NO", strBodyNo);
			} else {
				CMap<String, Object> toolInOutMap = toolService.selectToolInOut(cMap);
				if (null != toolInOutMap && null != toolInOutMap.get("BODY_NO")) {
					bodyNo = SocketUtility.padRight(toolInOutMap.getString("BODY_NO").replace(" ", ""), 9, '0');
					cMap.put("BODY_NO", toolInOutMap.getString("BODY_NO"));
				} else {
					cMap.put("BODY_NO", "XXX 000000");
				}
			}
			
			toolService.insertTbIfToolRslt(cMap);
			
			CMap<String, Object> toolMap = initManager.listSelMap(initManager.toolInitList, "TOOL_ID", cMap.getString("SK_ID"));
			if (null != toolMap && null != toolMap.getString("MES_SEND_YN") && toolMap.getString("MES_SEND_YN").equals("Y")) {
				String mappCd = toolMap.getString("MAPP_CD");
				String batchCounter = SocketUtility.padRight("", 4, '0');
				String tighteningId = SocketUtility.padRight("", 10, '0');
				
				String torque = (cMap.getString("TORQUE").trim().equals("")) ? SocketUtility.padLeft("NA", 6, ' '):SocketUtility.padRight(cMap.getString("TORQUE"), 6, ' ');
				String torqueStatus = (cMap.getString("TIGHTENING_STAT").equals("OK")) ? "O":"N";
				String angle = SocketUtility.padRight("", 6, ' ');
				String angleStatus = (cMap.getString("TIGHTENING_STAT").equals("OK")) ? "O":"N";
				String parameterSetId = SocketUtility.padRight("", 3, '0');
				String batchSize = SocketUtility.padRight("", 4, '0');
				String torqueMinLimit = SocketUtility.padRight("", 6, ' ');
				String torqueMaxLimit = SocketUtility.padRight("", 6, ' ');
				String torqueFinalTarget = SocketUtility.padRight("", 6, ' ');
				String angleMin = SocketUtility.padRight("", 6, ' ');
				String angleMax = SocketUtility.padRight("", 6, ' ');
				String FinalAngleTarget = SocketUtility.padRight("", 6, ' ');
				String dtOfLastChange = SocketUtility.padRight("", 19, ' ');
				String timeStamp = SocketUtility.padRight(cMap.getString("YOCOTA_TIME"), 19, ' ');
				
				String strSendByte = mappCd + batchCounter+tighteningId+bodyNo+torque+torqueStatus+angle+angleStatus+parameterSetId+batchSize+torqueMinLimit+torqueMaxLimit+torqueFinalTarget+angleMin+angleMax+FinalAngleTarget+dtOfLastChange+timeStamp;
				CMap<String, Object> mesSendDataMap = new CMap<>();
				mesSendDataMap.put("SK_ID", "TCPC_MES_TOOL_RSLT");
				mesSendDataMap.put("SEND_BYTES", strSendByte);
				mesSendDataMap.put("BODY_NO", cMap.getString("BODY_NO"));
				toolService.insertMesSendData(mesSendDataMap);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "reciveYocotaToolRslt", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> receiveMesRsltAck(CMap<String, Object> cMap) {
		try {
			// logger.info("MesController receiveMesRsltAck" + cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");

			CMap<String, Object> channelMap = SocketUtility.channelGetChannelMap(channel);
			CMap<String, Object> testMap = new CMap<>();
			testMap.put("RSLT_SEND_SEQ", channelMap.getString("DEVICE_ID"));
			testMap.put("MSG_RSLT", cMap.getString("MES_RELT"));
			//// logger.info("@@@@@@@@@@" + channelMap);
			int a = toolService.updateTestAckData(testMap);
//			if (a > 0) {
//				channel.close();
//			}
		} catch (Exception e) {
			//// logger.info("MesController receiveMesRsltAck Exception::" + e);
		}
		return cMap;
	}
}
