package com.module.controller.app;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.module.service.BrakeService;
import com.module.service.ToolService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

/**
 * @author user
 *
 */
@Component
public class BrakeController {
	private static final Logger logger = LoggerFactory.getLogger(BrakeController.class);
	
	@Autowired BrakeService brakeService;
	
	@Autowired ToolService toolService;
	
	public CMap<String, Object> receiveData(CMap<String, Object> cMap) {
		try {
			logger.info("@@@@@@@@@" + cMap);
			String[] receiveData = cMap.getString("TOTAL_BYTES_STR").split(";");
			cMap.put("SPLIT_DATA", receiveData);
			if (receiveData.length == 3) { // 바디넘버 수신
				logicBrakeModuleBodyNo(cMap);
			} else if (receiveData.length >= 20) { // 채결 수신
				logicBrakeModuleRslt(cMap);
			} else if ("RQ".equals(receiveData[0])) { // s/n alc 판정 요청
				logicRqTotalStatus(cMap);
			} else if ("OK".equals(receiveData[0])||"NG".equals(receiveData[0])) { // s/n alc 판정 요청
				logicRqTotalStatusAck(cMap);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "BrakeController receiveData Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> logicBrakeModuleRslt(CMap<String, Object> cMap) {
		Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		try {
			logger.info("@@@@@@@@@ logicBrakeModuleRslt" + cMap);
			
			String[] receiveData = (String[]) cMap.get("SPLIT_DATA");
			String[] arrSnAlc = Arrays.copyOfRange(receiveData, 0, 4);
			String[] toolRsltData = Arrays.copyOfRange(receiveData, 4, receiveData.length);
			cMap.put("SERIAL_NUMBER", arrSnAlc[0]);
			cMap.put("ALC_CODE", arrSnAlc[1]);
			cMap.put("TOTAL_STATUS", arrSnAlc[2]);
			cMap.put("STATUS_DT", arrSnAlc[3]);
			
			StringBuilder stringBuilder = new StringBuilder();
			
			for (String element : toolRsltData) {
			    stringBuilder.append(element);
			    stringBuilder.append(";"); // 각 요소를 세미콜론으로 구분
			}
			
			// 마지막 세미콜론 제거
			if (stringBuilder.length() > 0) {
			    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			}
			
			String result = stringBuilder.toString();
			cMap.put("RECEIVE_BYTES", result);
			
			brakeService.insertTbRcBrakeModuleRslt(cMap);
			
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(("OK;"+cMap.getString("TOTAL_BYTES_STR")).getBytes(), 0));
		} catch (Exception e) {
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(("NG;"+cMap.getString("TOTAL_BYTES_STR")).getBytes(), 0));
			SocketUtility.exceptionLogger(logger, "BrakeController logicBrakeModuleRslt Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> logicRqTotalStatus(CMap<String, Object> cMap) {
		Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		try {
			logger.info("@@@@@@@@@ logicRqTotalStatus" + cMap);
			CMap<String, Object> totalStatusMap = brakeService.selectTbRcBrakeModuleTotalStatus(cMap);
			if (null == totalStatusMap) {
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(("CN").getBytes(), 0));
			} else {
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter((totalStatusMap.getString("SERIAL_NUMBER")+";"+totalStatusMap.getString("ALC_CODE")+";"+totalStatusMap.getString("TOTAL_STATUS")+";"+totalStatusMap.getString("STATUS_DT")).getBytes(), 0));
				totalStatusMap.put("MSG_RSLT", "0");
				brakeService.updateTbRcBrakeModuleRslt(totalStatusMap);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "BrakeController logicRqTotalStatus Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> logicRqTotalStatusAck(CMap<String, Object> cMap) {
		Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		try {
			logger.info("@@@@@@@@@ logicRqTotalStatusAck" + cMap);
			String[] receiveData = (String[]) cMap.get("SPLIT_DATA");
			String[] arrSnAlc = Arrays.copyOfRange(receiveData, 1, 5);
			cMap.put("SERIAL_NUMBER", arrSnAlc[0]);
			cMap.put("ALC_CODE", arrSnAlc[1]);
			cMap.put("TOTAL_STATUS", arrSnAlc[2]);
			cMap.put("STATUS_DT", arrSnAlc[3]);
			
			if ("OK".equals(receiveData[0])) {
				cMap.put("MSG_RSLT", "1");
			} else {
				cMap.put("MSG_RSLT", "2");
			}
			
			brakeService.updateTbRcBrakeModuleRslt(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "BrakeController logicRqTotalStatusAck Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> logicBrakeModuleBodyNo(CMap<String, Object> cMap) {
		Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
		try {
			logger.info("@@@@@@@@@ logicBrakeModuleBodyNo" + cMap);
			String[] receiveData = (String[]) cMap.get("SPLIT_DATA");
			
			cMap.put("SERIAL_NUMBER", receiveData[0]);
			cMap.put("ALC_CODE", receiveData[1]);
			cMap.put("BODY_NO", receiveData[2]);
			String bodyNo = receiveData[2].replace(" ", "");
			
			brakeService.updateTbRcBrakeModuleBodyNo(cMap);
			
			List<CMap<String, Object>> mesSendList = brakeService.selectMesSendData(cMap);
			
			for (CMap<String, Object> mesSendMap: mesSendList) {
				String strTotalBytes = mesSendMap.getString("RECEIVE_BYTES");
				String[] toolRsltData = strTotalBytes.split(";");
				
				
				
				
				do {
					String mappCd = "BSBT01";
					String batchCounter = SocketUtility.padRight(toolRsltData[0], 4, '0');
					String tighteningId = SocketUtility.padRight(toolRsltData[1], 10, '0');
					
					cMap.put("TORQUE", SocketUtility.padRight(String.format("%.2f", (double)Integer.parseInt(toolRsltData[2])/100), 6, ' '));
					String torque = (cMap.getString("TORQUE").trim().equals("")) ? SocketUtility.padLeft("NA", 6, ' '):SocketUtility.padRight(cMap.getString("TORQUE"), 6, ' ');
					String torqueStatus = (toolRsltData[3].equals("1")) ? "O":"N";
					String angle = (toolRsltData[4].trim().equals("")) ? SocketUtility.padLeft("NA", 6, ' '):SocketUtility.padRight(toolRsltData[4], 6, ' ');
					String angleStatus = (toolRsltData[5].equals("1")) ? "O":"N";
					String parameterSetId = SocketUtility.padRight(toolRsltData[6].trim(), 3, '0');
					String batchSize = SocketUtility.padRight(toolRsltData[7].trim(), 4, '0');
					cMap.put("TORQUE_MIN_LIMIT", SocketUtility.padRight(String.format("%.2f", (double)Integer.parseInt(toolRsltData[8])/100), 6, ' '));
					String torqueMinLimit = SocketUtility.padRight(cMap.getString("TORQUE_MIN_LIMIT"), 6, ' ');
					cMap.put("TORQUE_MAX_LIMIT", SocketUtility.padRight(String.format("%.2f", (double)Integer.parseInt(toolRsltData[9])/100), 6, ' '));
					String torqueMaxLimit = SocketUtility.padRight(cMap.getString("TORQUE_MAX_LIMIT"), 6, ' ');
					cMap.put("TORQUE_FINAL_TARGET", SocketUtility.padRight(String.format("%.2f", (double)Integer.parseInt(toolRsltData[10])/100), 6, ' '));
					String torqueFinalTarget = SocketUtility.padRight(cMap.getString("TORQUE_FINAL_TARGET"), 6, ' ');
					String angleMin = SocketUtility.padRight(toolRsltData[11], 6, ' ');
					String angleMax = SocketUtility.padRight(toolRsltData[12], 6, ' ');
					String FinalAngleTarget = SocketUtility.padRight(toolRsltData[13], 6, ' ');
					String dtOfLastChange = SocketUtility.padRight(toolRsltData[14], 19, ' ');
					String timeStamp = SocketUtility.padRight(toolRsltData[15], 19, ' ');
					
					String strSendByte = mappCd+batchCounter+tighteningId+bodyNo+torque+torqueStatus+angle+angleStatus+parameterSetId+batchSize+torqueMinLimit+torqueMaxLimit+torqueFinalTarget+angleMin+angleMax+FinalAngleTarget+dtOfLastChange+timeStamp;
					CMap<String, Object> mesSendDataMap = new CMap<>();
					mesSendDataMap.put("SK_ID", "TCPC_MES_TOOL_RSLT");
					mesSendDataMap.put("SEND_BYTES", strSendByte);
					mesSendDataMap.put("BODY_NO", cMap.getString("BODY_NO"));
					toolService.insertMesSendData(mesSendDataMap);
					toolRsltData = Arrays.copyOfRange(toolRsltData, 16, toolRsltData.length);
				} while (toolRsltData.length <= 0);
			}
			
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(("OK;"+cMap.getString("TOTAL_BYTES_STR")).getBytes(), 0));
		} catch (Exception e) {
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(("NG;"+cMap.getString("TOTAL_BYTES_STR")).getBytes(), 0));
			SocketUtility.exceptionLogger(logger, "BrakeController logicBrakeModuleBodyNo Exception::", e);
		}
		return cMap;
	}
}
