package com.module.controller.app;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.module.service.MesService;
import com.module.service.PbsService;
import com.module.service.PlcService;
import com.module.service.VccService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

/**
 * @author user
 *
 */
@Component
public class PbsController {
	private static final Logger logger = LoggerFactory.getLogger(PbsController.class);
	
	@Autowired PbsService pbsService;
	
	@Autowired PlcService plcService;
	
	@Autowired	VccService vccService;
	
	@Autowired MesService mesService;
	
	/**
	 * PBS-IN 도장완료 바디넘버 처리 로직 이때 10000000000000000 대로 MES_PROD_SEQ 사용 임시 맵핑
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> recivePbsBody(CMap<String, Object> cMap) {
		try {
			logger.info("PbsController recivePbsBody 0000:: " + cMap.getString("BODY_NO"));
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			String strSendData = "CO;"+cMap.getString("TOTAL_BYTES_STR");
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
			
			cMap.putAll(pbsService.selectSqMesProdPbs(cMap));
			SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CREATE_CAR_PBS", cMap);
			vccService.insertTbRcMapping(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * 테스트 목적이었음..
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> recivePrePbsBody(CMap<String, Object> cMap) {
		try {
			logger.info("PbsController recivePrePbsBody 1111:: " + cMap.getString("BODY_NO"));
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			String strSendData = "CO;"+cMap.getString("TOTAL_BYTES_STR");
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * tts쪽 로직 혼용기간동안 사용.. 테스트 용도였음.
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> reciveTtsBody(CMap<String, Object> cMap) {
		try {
			logger.info("PbsController reciveTtsBody 2222:: " + cMap.getString("BODY_NO"));
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			String strSendData = "CO;"+cMap.getString("TOTAL_BYTES_STR");
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
			
			if ("BLANK_DATA".equals(cMap.getString("BODY_NO"))) {
				return null;
			}
			
//			TODO 혼용기간 로직
			CMap<String, Object> rtPosMap = vccService.selectTbRtPos(cMap);
			if (null == rtPosMap) {
				cMap.putAll(pbsService.selectSqMesProdPbs(cMap));
			} else {
				cMap.put("MES_PROD_SEQ", rtPosMap.get("MES_PROD_SEQ"));
			}
			
			cMap.put("BODY_NO", cMap.getString("BODY_NO"));
			SendHandler.sendSkId("TCPS_VCC_TO_CORE", "PBS_OUT_TTS_CAR", cMap);
			
			cMap.put("TAG_ID", "TTS");
			cMap.put("PROC_CD", "P400");
			cMap.put("ERROR_FLAG", "");
			cMap.put("REPORT_FLAG", "");
			plcService.mergePbsReportBody(cMap);
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * 단건 공정보고 요청
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> reciveProcBodyReq(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			
			if ("P390".equals(cMap.getString("STN_CD"))) {// PRE
				cMap.put("PROC_CD", "P390");
				int updateCount = logicProcReport(cMap, channel);
				if (0 < updateCount) { // TODO 데이터 셀렉트 부분 해야함.
					logicCsReport(cMap, channel);
				}
			} else if ("P400".equals(cMap.getString("STN_CD"))) {// TTS TODO
				cMap.put("PROC_CD", "P400");
				int updateCount = logicProcReport(cMap, channel);
				if (0 < updateCount) { // TODO 데이터 셀렉트 부분 해야함.
					logicCsReport(cMap, channel);
				}
			} else if ("T000".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "T000");
				int updateCount = logicProcReportT000(cMap, channel);
				if (0 < updateCount) { // TODO 데이터 셀렉트 부분 해야함.
					logicCsReport(cMap, channel);
				}
			} else if ("T100".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0101");
				logicProcReport(cMap, channel);
			} else if ("T110".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0201");
				logicProcReport(cMap, channel);
			} else if ("T120".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0301");
				logicProcReport(cMap, channel);
			} else if ("T130".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "CS0101");
				logicProcReport(cMap, channel);
			} else if ("T140".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "CS0201");
				logicProcReport(cMap, channel);
			} else if ("T150".equals(cMap.getString("STN_CD"))) {// 파이널 1라인 좌표 안나옴..
				cMap.put("PROC_CD", "FN0101");
				logicProcReport(cMap, channel);
			} else if ("T160".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0201");
				logicProcReport(cMap, channel);
			} else if ("T170".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0301");
				logicProcReport(cMap, channel);
			} else if ("T180".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0401");
				logicProcReport(cMap, channel);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * 공정보고 로직 함수
	 * 
	 */
	public void logicCsReport(CMap<String, Object> cMap, Channel channel) {
		try {
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
	}
	
	/**
	 * 공정보고 로직 함수
	 * 
	 */
	public int logicProcReport(CMap<String, Object> cMap, Channel channel) {
		int returnInt = 0;
		try {
			CMap<String, Object> rePortBody = plcService.selectPbsReportBody(cMap);
			logger.info("@@@@ rePortBody ::" + rePortBody);
			if (null == rePortBody) {
				String strSendData = "CN";
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
				return returnInt;
			}
			
			if (null == rePortBody.get("REPORT_FLAG")) {
				rePortBody.put("REPORT_FLAG", "0");
				cMap.putAll(rePortBody);
				logger.info("############# 공정보고 타냐???? " + rePortBody);
				returnInt = plcService.updatePbsReportBodyFlag(rePortBody);
				plcService.insertTbRcPrcoReport(rePortBody);
				if ("".equals(rePortBody.getString("BODY_NO").trim())) {
					String strSendData = "CP";
					SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
				} else {
					String strSendData = "RR;"+cMap.getString("STN_CD")+";"+rePortBody.getString("BODY_NO");
					SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
				}
			} else if (null == rePortBody.get("REPORT_FLAG") || 1 >= rePortBody.getInt("REPORT_FLAG")) {
				String strSendData = "CN";
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
			}
			
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return returnInt;
	}
	
	/**
	 * 공정보고 로직 함수
	 * 
	 */
	public int logicProcReportT000(CMap<String, Object> cMap, Channel channel) {
		int returnInt = 0;
		try {
			CMap<String, Object> rePortBody = plcService.selectPbsReportBody(cMap);
			logger.info("@@@@ rePortBody logicProcReportT000 ::" + rePortBody);
			if (null == rePortBody) {
				String strSendData = "CN";
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
				return returnInt;
			}
			
			if (null == rePortBody.get("REPORT_FLAG")) {
				rePortBody.put("REPORT_FLAG", "0");
				cMap.putAll(rePortBody);
				logger.info("############# 공정보고 타냐???? " + rePortBody);
				int intFlag = 0;
				if ("".equals(rePortBody.getString("BODY_NO").trim())) {
					String strSendData = "CP";
					SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
					intFlag = 1;
				} else {
					try {
						CMap<String, Object> csChkMap = plcService.selectBodyReportMap(cMap);
						logger.info("플레그 데이터 확인" + csChkMap);
						String manualFlag = "A";
						if (null != csChkMap && "PBSLMP".equals(csChkMap.getString("PROC_CD"))) {
							manualFlag = "A";
						} else {
							manualFlag = "M";
						}
						String strSendData = "RR;"+cMap.getString("STN_CD")+";"+rePortBody.getString("BODY_NO")+";"+manualFlag;
						SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
						intFlag = 1;
					} catch (Exception e) {
						SocketUtility.exceptionLogger(logger, "공정보고 전송부분", e);
						return 0;
					}
				}
				logger.info("############# 공정보고 업데이트 하냐????? " + rePortBody);
				returnInt = plcService.updatePbsReportBodyFlag(rePortBody);
				plcService.insertTbRcPrcoReport(rePortBody);
			} else if (null == rePortBody.get("REPORT_FLAG") || 1 >= rePortBody.getInt("REPORT_FLAG")) {
				String strSendData = "CN";
				SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
			}
			
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return returnInt;
	}
	/**
	 * 라인 데이터 보고
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> reciveLineBodyReq(CMap<String, Object> cMap) {
		try {
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			
			if ("T100".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0101");
				cMap.put("LINE_CD", "TR01");
				logicLineReport(cMap, channel);
			} else if ("T110".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0201");
				cMap.put("LINE_CD", "TR02");
				logicLineReport(cMap, channel);
			} else if ("T120".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "TR0301");
				cMap.put("LINE_CD", "TR03");
				logicLineReport(cMap, channel);
			} else if ("T130".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "CS0101");
				cMap.put("LINE_CD", "CS01");
				logicLineReport(cMap, channel);
			} else if ("T140".equals(cMap.getString("STN_CD"))) {// TODO 위치랑 확인 필요.
				cMap.put("PROC_CD", "CS0201");
				cMap.put("LINE_CD", "CS02");
				logicLineReport(cMap, channel);
			} else if ("T150".equals(cMap.getString("STN_CD"))) {// 파이널 1라인 좌표 안나옴.. // TODO 위치랑 확인 필요.
				cMap.put("PROC_CD", "FN0101");
				cMap.put("LINE_CD", "FN01");
				logicLineReport(cMap, channel);
			} else if ("T160".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0201");
				cMap.put("LINE_CD", "FN02");
				logicLineReport(cMap, channel);
			} else if ("T170".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0301");
				cMap.put("LINE_CD", "FN03");
				logicLineReport(cMap, channel);
			} else if ("T180".equals(cMap.getString("STN_CD"))) {// OUT
				cMap.put("PROC_CD", "FN0401");
				cMap.put("LINE_CD", "FN04");
				logicLineReport(cMap, channel);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * 라인데이터 전송 로직함수
	 * 
	 */
	public void logicLineReport(CMap<String, Object> cMap, Channel channel) {
		try {
			List<CMap<String, Object>> rePortLineList = null;
			rePortLineList = vccService.selectLineReportList(cMap);
			if (0 >= rePortLineList.size()) {
				String strProcCd = cMap.getString("PROC_CD");
				int lastProcCd = Integer.parseInt(strProcCd.substring(strProcCd.length()-1))+1;
				cMap.put("PROC_CD", strProcCd.substring(0, strProcCd.length()-1) + lastProcCd);
				rePortLineList = vccService.selectLineReportList(cMap);
				if (0 >= rePortLineList.size()) {
					return;
				}
			}
			String strLineData = "";
			for (CMap<String, Object> reportLineMap : rePortLineList) {
				strLineData+=reportLineMap.getString("BODY_NO").substring(1,10).replace(" ", "");
			}
			
			String strSendData = "RR;"+cMap.getString("STN_CD")+";"+strLineData;
			SendHandler.sendChannel(channel, SocketUtility.bytesAddDelimiter(strSendData.getBytes(), 0));
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
	}
	
	/**
	 * 응답 수신부분
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> reciveBodyRes(CMap<String, Object> cMap) {
		try {
			logger.info("@@@@ 4444" + cMap);
			Channel channel = (Channel) cMap.get("RECIVE_CHANNEL");
			String strBytes = cMap.getString("TOTAL_BYTES_STR");
			String[] arrBytes = strBytes.split(";");
			int port = SocketUtility.socketAddressToPort(channel.localAddress());
			int dataLen = arrBytes.length;
			if (dataLen <= 1) {
				return null;
			} else if (dataLen == 2) {
				logger.info(port+"길이가 2 @@@@@ " + strBytes);
				return null;
			} else if (dataLen == 3) {
				logger.info(port+"길이가 3 @@@@@ " + strBytes);
				return null;
			} else if (dataLen >= 4) {
				logger.info(port+"길이가 4 @@@@@ " + strBytes);
			}
			
			
			if (port == 7013) {//tts
				cMap.put("PROC_CD", "P400");
				cMap.put("ACK_FLAG", arrBytes[0]);
				cMap.put("BODY_NO", arrBytes[3]);
				logger.info(cMap.getString("PROC_CD") + "응답 들어왔다 맞냐?" + cMap);
				logicBodyRes(cMap);
				
			} else if (port == 7010) {// 공정보고
				cMap.put("PROC_CD", "T000");
				cMap.put("ACK_FLAG", arrBytes[0]);
				cMap.put("BODY_NO", arrBytes[3]);
				logger.info(cMap.getString("PROC_CD") + "응답 들어왔다 맞냐?" + cMap);
				logicBodyRes(cMap);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
		return cMap;
	}
	
	/**
	 * 응답 수신부분
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicBodyRes(CMap<String, Object> cMap) {
		try {
			CMap<String, Object> ttsMap = plcService.selectPbsReportBody(cMap);
			if (null != ttsMap) {
				ttsMap.put("MAC_ADDR", ttsMap.getString("TAG_ID"));
			}
			CMap<String, Object> csMap = plcService.selectCsReportBody(ttsMap);
			
			if ("NN".equals(cMap.getString("ACK_FLAG"))) {
				if (ttsMap.getString("BODY_NO").equals(cMap.getString("BODY_NO"))) {
					ttsMap.put("RESULT", "MES_FAIL");
					ttsMap.put("REPORT_FLAG", "2");
					if ("P400".equals(cMap.getString("PROC_CD"))) {
						CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
						logger.info("여기 로그 0000" + tagMap);
						if (null != tagMap && null != tagMap.get("TAG_ID")) {
							ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
						}
						SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_TTS", ttsMap);
						
						ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
						if (null == csMap) {
							plcService.updateCsReportBodyFlag(ttsMap);
						} else {
							plcService.updateMesSeqReportBodyFlag(ttsMap);
						}
					} else if ("T000".equals(cMap.getString("PROC_CD"))) {
						CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
						logger.info("여기 로그 1111" + tagMap);
						if (null != tagMap && null != tagMap.get("TAG_ID")) {
							ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
						}
						SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_OUT", ttsMap);
						
						ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
						plcService.updateCsReportBodyFlag(ttsMap);
					}
					
					plcService.updatePbsReportBodyFlag(ttsMap);
					plcService.insertTbRcPrcoReport(ttsMap);
				}
			} else if ("CO".equals(cMap.getString("ACK_FLAG"))) {
				if (ttsMap.getString("BODY_NO").equals(cMap.getString("BODY_NO"))) {
					ttsMap.put("RESULT", "OK");
					
					if ("P400".equals(cMap.getString("PROC_CD"))) {
						if (null == csMap) {
							ttsMap.put("REPORT_FLAG", "");
							SendHandler.sendSkId("TCPS_VCC_TO_CORE", "PBS_OUT_TTS_CAR", ttsMap);
							CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
							logger.info("여기 로그 2222" + tagMap);
							if (null != tagMap && null != tagMap.get("TAG_ID")) {
								ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
							}
							SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_TTS", ttsMap);
							ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
							plcService.updateCsReportBodyFlag(ttsMap);
							plcService.updateEmpIdxCsProc(ttsMap);// 이거 문제있다.
						} else {
							ttsMap.put("REPORT_FLAG", "");
							SendHandler.sendSkId("TCPS_VCC_TO_CORE", "PBS_OUT_TTS_CAR", ttsMap);
							CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
							logger.info("여기 로그 3333" + tagMap);
							if (null != tagMap && null != tagMap.get("TAG_ID")) {
								ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
							}
							SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_TTS", ttsMap);
							ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
							plcService.updateCsReportBodyFlag(ttsMap);
							plcService.updateMesSeqCsProc(ttsMap);// 이거 문제있다.
						}
					} else if ("T000".equals(cMap.getString("PROC_CD"))) {
						ttsMap.put("REPORT_FLAG", "1");
						CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
						if (null != tagMap && null != tagMap.get("TAG_ID")) {
							ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
						}
						SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_OUT", ttsMap);
						ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
						plcService.updateCsReportBodyFlag(ttsMap);
//						ttsMap.put("REPORT_FLAG", "1");
//						ttsMap.put("EMP_IDX", "");
//						plcService.updateEmpIdxCsProc(ttsMap);// 이거 문제있다.
					}
					ttsMap.put("REPORT_FLAG", "1");
					plcService.updatePbsReportBodyFlag(ttsMap);
					plcService.insertTbRcPrcoReport(ttsMap);
				}
			} else if ("CP".equals(cMap.getString("ACK_FLAG"))) {
				if (ttsMap.getString("BODY_NO").equals(cMap.getString("BODY_NO"))) {
					ttsMap.put("RESULT", "MES_FAIL");
					ttsMap.put("REPORT_FLAG", "2");
					
					if ("T000".equals(cMap.getString("PROC_CD"))) {
						CMap<String, Object> tagMap = vccService.selectTagMacAddr(ttsMap);
						logger.info("여기 로그 4444" + tagMap);
						if (null != tagMap && null != tagMap.get("TAG_ID")) {
							ttsMap.put("TAG_ID", tagMap.getString("TAG_ID"));
						}
						SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_RES_OUT", ttsMap);
						ttsMap.put("TAG_ID", ttsMap.getString("MAC_ADDR"));
						plcService.updateCsReportBodyFlag(ttsMap);
					}
					
					plcService.updatePbsReportBodyFlag(ttsMap);
					plcService.insertTbRcPrcoReport(ttsMap);
				}
			}
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, null, e);
		}
	}

}
