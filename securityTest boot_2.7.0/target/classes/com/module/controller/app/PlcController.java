package com.module.controller.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collections;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.module.service.MesService;
import com.module.service.PbsService;
import com.module.service.PlcService;
import com.module.service.VccService;
import com.module.util.RestUtility;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Controller
public class PlcController {
	private static final Logger logger = LoggerFactory.getLogger(PlcController.class);

	@Autowired
	PlcService plcService;

	@Autowired
	VccService vccService;

	@Autowired
	MesService mesService;
	
	@Autowired 
	PbsService pbsService;

	@Value("${chk_profile}") String chkProfile;

	public static List<CMap<String, Object>> plcDataList = Collections.synchronizedList(new ArrayList<>());// 실시간 참조 동시성
//																											// 예외 방지
//	public CMap<String, Object> sendSchPlcReadAllData(CMap<String, Object> cMap) {
//		try {
//			cMap.put("PLCALIAS", "CP43");
//			SendHandler.sendSkId("WEBSK_PLC", "READALL", cMap);
//		} catch (Exception e) {
//			SocketUtility.exceptionLogger(logger, "PlcController recivePlcReadAllData Exception::", e);
//		}
//		return cMap;
//	}
//	
//	public CMap<String, Object> recivePlcReadAllData(CMap<String, Object> cMap) {
//		try {
//			logger.info("recivePlcReadAllData" + cMap);
//		} catch (Exception e) {
//			SocketUtility.exceptionLogger(logger, "PlcController recivePlcReadAllData Exception::", e);
//		}
//		return cMap;
//	}
//	
	
	/**
	 * 밀리초 단위 스케줄 CP43 PBS_OUT 쪽 PLC 데이터 수신부
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> read_cp43_cts(CMap<String, Object> cMap) {
		try {
			String strUrl = "http://10.72.199.187:500/";
			if ("local".equals(chkProfile)) {
				strUrl = "http://127.0.0.1:500/";
			}
			String readData = RestUtility.callRestApi(strUrl, "POST",
					"application/x-www-form-urlencoded", "COMMAND=READ&PLCALIAS=CP43&RANGEALIAS=CTS-1&POS=0&LENGTH=2").toString().replace("[DONE]", "");
			
			cMap.put("READARRAY", readData);
			logicCts_1(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController readPlcSch Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 밀리초 단위 스케줄 의장 PLC 데이터 수신부
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> readLineData(CMap<String, Object> cMap) {
		try {
			String strUrl = "http://10.72.199.187:500/";
			if ("local".equals(chkProfile)) {
				strUrl = "http://127.0.0.1:500/";
			}
			String readData = RestUtility.callRestApi("http://10.72.199.187:500/", "POST",
					"application/x-www-form-urlencoded", "COMMAND=READALL&PLCALIAS=" + cMap.getString("SCH_ID")).toString().replace("[DONE]", "");
			cMap.put("TARGET_LINE", cMap.getString("SCH_ID"));
			cMap.put("READ_DATA", readData);
			logicAbPlc(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController readPlcSch Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 밀리초 단위 스케줄 PBS_OUT 쪽 PLC 바디 데이터 수신부
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> readPreBodyData(CMap<String, Object> cMap) {
		try {
			String strUrl = "http://10.72.199.187:500/";
			if ("local".equals(chkProfile)) {
				strUrl = "http://127.0.0.1:500/";
			}
			String readData = RestUtility.callRestApi(strUrl, "POST",
					"application/x-www-form-urlencoded", "COMMAND=READ&PLCALIAS=CP41A&RANGEALIAS=PRE_BODY&POS=0&LENGTH=4").toString().replace("[DONE]", "");
			
			cMap.put("READARRAY", readData);
			logicPreBody(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController readPlcSch Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 밀리초 단위 스케줄 PBS 드랍리프트 바디넘버 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> logicPreBody(CMap<String, Object> cMap) {
		try {
			Decoder decoder = Base64.getDecoder();
			byte[] decodedBytes = decoder.decode(cMap.getString("READARRAY"));
			String strBody = new String(decodedBytes).trim();
			if (null == strBody || "".equals(strBody)) {
				cMap.put("BODY_NO", SocketUtility.rPad(strBody, 10, " "));
				int intChk = updatePlcData(cMap.getString("SCH_ID")+"_BODY_NO", cMap.getString("BODY_NO"));
			} else {
				cMap.put("CAR_CD_TOO", strBody.substring(0,2));
				cMap.put("BODY_NO_SIX", strBody.substring(2));
				CMap<String, Object> dataMap = mesService.selectTbBiCarCd(cMap);
				if (null == dataMap) {
					return null;
				}
				cMap.putAll(dataMap);
				cMap.put("BODY_NO", SocketUtility.rPad(cMap.getString("CAR_CD"), 4, " ")+cMap.getString("BODY_NO_SIX"));
				int intChk = updatePlcData(cMap.getString("SCH_ID")+"_BODY_NO", cMap.getString("BODY_NO"));
				cMap.put("TAG_ID", "BE_PRE");
				cMap.put("PROC_CD", "PLC_BE_PRE");
				cMap.put("LINE_CD", "PBSL");
				cMap.put("PLC_BODY_NO", cMap.getString("BODY_NO"));
				if (0 < intChk) {
					logger.info("logicPreBody" + cMap);
					plcService.mergePbsReportBody(cMap);
				}
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPreBody Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 밀리초 단위 스케줄 A-B PLC 데이터 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> logicAbPlc(CMap<String, Object> cMap) {
		try {
			JSONArray jsonArr = new JSONArray(cMap.getString("READ_DATA"));
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonData = jsonArr.getJSONObject(i);
				Decoder decoder = Base64.getDecoder();
				byte[] decodedBytes = decoder.decode(jsonData.getString("READARRAY"));
//				updatePlcData(cMap.getString("SCH_ID")+"_"+jsonData.getString("RANGEALIAS"), Byte.toString(decodedBytes[0]));
				String txt = cMap.getString("SCH_ID")+"_"+jsonData.getString("RANGEALIAS");
				String binaryString = Byte.toString(decodedBytes[0]);
				
				int intChk = updatePlcData(txt, binaryString);
				if (null != jsonData.getString("RANGEALIAS") && jsonData.getString("RANGEALIAS").equals("CAR_IN") && 0 < intChk) {
					CMap<String, Object> sendMap = new CMap<>();
					sendMap.put("LINE_CD", cMap.getString("SCH_ID"));
					sendMap.put("BOOLEAN", Byte.toString(decodedBytes[0]));
					SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CAR_IN", sendMap);
					
					if ("TR01".equals(sendMap.getString("LINE_CD")) ||
							"TR02".equals(sendMap.getString("LINE_CD")) ||
							"TR03".equals(sendMap.getString("LINE_CD")) ||
							"CS02".equals(sendMap.getString("LINE_CD")) ||
							"FN03".equals(sendMap.getString("LINE_CD")) ||
							"FN04".equals(sendMap.getString("LINE_CD"))) {
						CMap<String, Object> detectMap = SocketUtility.listSelMap(plcDataList, sendMap.getString("LINE_CD")+"_CAR_DETECT", "1");
						if (null != detectMap && "1".equals(sendMap.getString("BOOLEAN"))) {
							logger.info("@@@@@@@ 공대차 생성 @@@@@@@" + sendMap + "감지" + detectMap);
//							SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CREATE_EMPTY_CAR", sendMap);
						}
					}
				} else if (null != jsonData.getString("RANGEALIAS") && jsonData.getString("RANGEALIAS").equals("CAR_DETECT") && 0 < intChk) {
					CMap<String, Object> sendMap = new CMap<>();
					sendMap.put("LINE_CD", cMap.getString("SCH_ID"));
					sendMap.put("BOOLEAN", Byte.toString(decodedBytes[0]));
					SendHandler.sendSkId("TCPS_VCC_TO_CORE", "CAR_DETECT", sendMap);
				} else if (null != jsonData.getString("RANGEALIAS") && jsonData.getString("RANGEALIAS").equals("LINE_SIG") && 0 < intChk) {
					CMap<String, Object> sendMap = new CMap<>();
					sendMap.put("LINE_CD", cMap.getString("SCH_ID"));
					sendMap.put("LINE_SIGN", Byte.toString(decodedBytes[0]));
					sendMap.put("ACTIVE_YN", Byte.toString(decodedBytes[0]).equals("0")?"N":"Y");
					plcService.updateTbBiLineActiveYn(sendMap);
					SendHandler.sendSkId("TCPS_VCC_TO_CORE", "LINE_SIGNAL", sendMap);
				}
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicAbPlc Exception::", e);
		}
		return cMap;
	}

	/**
	 * CP43 Alias 데이터의 CTS_1 Alias 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> logicCts_1(CMap<String, Object> cMap) {
		try {
			Decoder decoder = Base64.getDecoder(); // 베이스 64디코더객체
			byte[] decodedBytes = decoder.decode(cMap.getString("READARRAY")); // base64 인코딩데이터
			ByteBuf buf = Unpooled.buffer();// 파싱할 버퍼 객체
			buf.writeBytes(decodedBytes);// 디코딩한 데이터 버퍼 입력
			short shortData = buf.readShortLE();// 엔디안 처리 or 처리 필요없을 시 LE가 없는 함수 호출
			buf.release();// 메모리 lake 방지 무조건 초기화 해야함.
			String binaryString = SocketUtility.shortToBinary(shortData);
			String[] arrBinaryString = binaryString.split("");
			logger.info("전체 ::" + binaryString + " 하트비트 ::" + arrBinaryString[0] + " PRE_IN ::" + arrBinaryString[13]);
			for (int i = 0; i < arrBinaryString.length; i++) {
				String txt = "";
				if (i == 0) {
					txt = "HEART_BIT";
				} else if (i == 1) {// PBS 라인기동(체인기동이라 정지와 관계 없음)
					txt = "PBS_OUT_LINE_SIG";
				} else if (i == 2) {// PBSLOU 스톱바 상태
					txt = "STOP_2";
				} else if (i == 3) {// PBSLMP 진출 LS
					txt = "LS_OUT_2";
				} else if (i == 4) {// PBSLMP 진입 LS
					txt = "LS_IN_2";
				} else if (i == 5) {// PBSL31 스톱바 상태
					txt = "STOP_1";
				} else if (i == 6) {// PBSL31 진출 LS
					txt = "LS_OUT_1";
				} else if (i == 7) {// PBSL31 진입 LS
					txt = "LS_IN_1";
				} else if (i == 8) {// 미사용
					txt = "NONE_1";
				} else if (i == 9) {// 미사용
					txt = "NONE_2";
				} else if (i == 10) {// 진출..?
					txt = "PRE_OUT";
				} else if (i == 11) {// 스톱바
					txt = "PRE_STOP";
				} else if (i == 12) {// 포토센서 감지
					txt = "POTO";
				} else if (i == 13) {// PRE_PBS 포크 안착
					txt = "PRE_IN";
				} else if (i == 14) {
					txt = "RIGHT_POSITION";
				} else if (i == 15) {
					txt = "FORK_MOVE";
				}
				int intChk = updatePlcData(txt, arrBinaryString[i]);
				
				if (0 < intChk && txt.equals("PRE_IN") && "1".equals(arrBinaryString[i])) {
					logicPrePbsIn(cMap, txt, arrBinaryString[i]);
				} else if (0 < intChk && txt.equals("PRE_OUT") && "1".equals(arrBinaryString[i])) {
					logicPrePbsOut(cMap, txt, arrBinaryString[i]);
				} else if (0 < intChk && txt.equals("LS_IN_1") && "1".equals(arrBinaryString[i])) {
					logicPbsl31In(cMap, txt, arrBinaryString[i]);
				} else if (0 < intChk && txt.equals("LS_OUT_1") && "1".equals(arrBinaryString[i])) {
					logicPbsl31OutReport(cMap, txt, arrBinaryString[i]);
				} else if (0 < intChk && txt.equals("LS_IN_2") && "1".equals(arrBinaryString[i])) {
					logicPbslMpIn(cMap, txt, arrBinaryString[i]);
				} else if (0 < intChk && txt.equals("LS_OUT_2") && "1".equals(arrBinaryString[i])) {
					logicPbslMpOutReport(cMap, txt, arrBinaryString[i]);
				}
				// else if (0 < intChk && txt.equals("LS_OUT_1") && "1".equals(arrBinaryString[i])) {
//					logicPreOut(cMap, txt, arrBinaryString[i]);
//				}
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicCts_1 Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public CMap<String, Object> logicReportProc(CMap<String, Object> cMap, String txt, String binaryString, String beProcCd, String afProcCd) {
		try {
			CMap<String, Object> reportMap = new CMap<>();
			reportMap.put("PROC_CD", beProcCd);
			CMap<String, Object> bodyMap = plcService.selectPbsReportBody(reportMap);
			logger.info(txt + " bodyMap 확인" + bodyMap);
			if (null == bodyMap) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				reportMap.put("PROC_CD", afProcCd);
				CMap<String, Object> procBodyMap = plcService.selectPbsReportBody(reportMap);
				logger.info(txt + " procBodyMap 확인" + procBodyMap);
				if (null != procBodyMap) {
					LocalDateTime currentTime = LocalDateTime.now();
					LocalDateTime targetTime = LocalDateTime.parse(procBodyMap.getString("REG_DT"), formatter);
					long timeDifferenceInSeconds = ChronoUnit.SECONDS.between(targetTime, currentTime);
					
					if (timeDifferenceInSeconds >= 10) {
						reportMap.put("PROC_CD", afProcCd);
						reportMap.put("TAG_ID", "ERROR");
						reportMap.put("BODY_NO", "          ");
						reportMap.put("ERROR_FLAG", "1");
						reportMap.putAll(plcService.selectEmpIdx(cMap));
						logger.info(txt + " @@@@@@@@ 0000" + reportMap);
						plcService.mergePbsReportBody(reportMap);
						return reportMap;
					} else {
						return null;
					}
				} else {
					reportMap.put("PROC_CD", afProcCd);
					reportMap.put("TAG_ID", "ERROR");
					reportMap.put("BODY_NO", "          ");
					reportMap.put("ERROR_FLAG", "1");
					reportMap.putAll(plcService.selectEmpIdx(cMap));
					logger.info(txt + " @@@@@@@@ 1111" + reportMap);
					plcService.mergePbsReportBody(reportMap);
					return reportMap;
				}

			}
			reportMap.putAll(bodyMap);
			plcService.deletePbsReportBody(reportMap);
			reportMap.put("PROC_CD", afProcCd);
			CMap<String, Object> reportTargetMap = null;
			logger.info(txt + " @@@@" + reportMap);
			if ("PBSLMP".equals(afProcCd)) {
				reportMap.put("PROC_CD", "PBSL31");
			}
			reportTargetMap = plcService.selectReportTargetBody(reportMap);//
			if ("PBSLMP".equals(afProcCd)) {
				reportMap.put("PROC_CD", "PBSLMP");
				if (null != reportTargetMap) {
					reportTargetMap.put("PROC_CD", "PBSLMP");
				}
			}
			logger.info("reportTargetMap 확인" + reportTargetMap);
			if (null != reportTargetMap) {
				logger.info("logicPrePbsIn " + reportMap);
				reportMap.putAll(reportTargetMap);
				plcService.mergePbsReportBody(reportMap);
				logger.info("정상 저장");
				return reportMap;
			} else {
				CMap<String, Object> procCdMap = new CMap<>();
				procCdMap.put("PROC_CD", "P400");
				CMap<String, Object> ttsChkMap = plcService.selectPbsReportBody(procCdMap);
				logger.info("PLC 바디가 안맞아서 TTS 케이스 0000 " + ttsChkMap);
				logger.info("PLC 바디가 안맞아서 TTS 케이스 1111 " + reportMap);
				if (null != ttsChkMap && null != ttsChkMap.get("BODY_NO") && ttsChkMap.getString("BODY_NO").equals(reportMap.getString("BODY_NO"))) {
					logger.info("PLC 바디가 안맞아서 TTS 케이스 2222 " + reportMap);
					reportMap.putAll(reportTargetMap);
					plcService.mergePbsReportBody(reportMap);
					logger.info("정상 저장");
					return reportMap;
				} else {
					if (null == reportMap.get("EMP_IDX")) {
						reportMap.putAll(plcService.selectEmpIdx(cMap));
						logger.info(txt + " @@@@@@@@ 2222" + reportMap);
					}
					reportMap.put("PROC_CD", afProcCd);
					reportMap.put("TAG_ID", "ERROR");
					reportMap.put("BODY_NO", "          ");
					reportMap.put("ERROR_FLAG", "1");
					logger.info(txt + " @@@@@@@@ 4444" + reportMap);
					plcService.mergePbsReportBody(reportMap);
					return reportMap;
				}
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicReportProc Exception::", e);
		}
		return null;
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicNotReportProc(CMap<String, Object> cMap, String txt, String binaryString, String beProcCd, String afProcCd) {
		try {
			CMap<String, Object> reportMap = new CMap<>();
			reportMap.put("PROC_CD", beProcCd);
			CMap<String, Object> bodyMap = plcService.selectPbsReportBody(reportMap);
			logger.info(txt + " bodyMap확인" + bodyMap);
			
			//TODO 로컬 테스트 로직
//			if (null == bodyMap && "local".equals(chkProfile) && "PBSLMP".equals(beProcCd)) {
//				logger.info("로컬 테스트 로직");
//				reportMap.put("PROC_CD", "PBSL31");
//				bodyMap = plcService.selectPbsReportBody(reportMap);
//			}
			//TODO 로컬 테스트 로직 여기까지
			
			if (null == bodyMap) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				reportMap.put("PROC_CD", afProcCd);
				CMap<String, Object> procBodyMap = plcService.selectPbsReportBody(reportMap);
				logger.info(txt + " procBodyMap확인" + procBodyMap);
				if (null != procBodyMap) {
					LocalDateTime currentTime = LocalDateTime.now();
					LocalDateTime targetTime = LocalDateTime.parse(procBodyMap.getString("REG_DT"), formatter);
					long timeDifferenceInSeconds = ChronoUnit.SECONDS.between(targetTime, currentTime);
					
					if (timeDifferenceInSeconds >= 10) {
						if ("PBSLOU".equals(afProcCd)) {
							return;
						}
						reportMap.put("PROC_CD", afProcCd);
						reportMap.put("TAG_ID", "ERROR");
						reportMap.put("BODY_NO", "          ");
						reportMap.put("ERROR_FLAG", "1");
						reportMap.putAll(plcService.selectEmpIdx(cMap));
						logger.info(txt + " @@@@@@@@@@ 0000" + reportMap);
						plcService.mergePbsReportBody(reportMap);
						return;
					} else {
						return;
					}
				} else {
					reportMap.put("PROC_CD", afProcCd);
					reportMap.put("TAG_ID", "ERROR");
					reportMap.put("BODY_NO", "          ");
					reportMap.put("ERROR_FLAG", "1");
					reportMap.putAll(plcService.selectEmpIdx(cMap));
					logger.info(txt + " @@@@@@@@@@ 1111" + reportMap);
					plcService.mergePbsReportBody(reportMap);
					return;
				}

			}
			reportMap.putAll(bodyMap);
			plcService.deletePbsReportBody(reportMap);
			reportMap.put("PROC_CD", afProcCd);
			logger.info(txt + " @@@@@@@@@@ 2222" + reportMap);
			if ("PRE_OUT".equals(afProcCd) || "BE_RP".equals(afProcCd)) {
				reportMap.put("REPORT_FLAG", "");
			}
			try {
				logger.info("@@@@@@@@@@@@@@@@ 테스트 체크 @@@@@@@@@@@@@@@@" + reportMap.get("REPORT_FLAG"));
				if ("PBSLOU".equals(afProcCd) && null != reportMap.get("REPORT_FLAG") && "3".equals(reportMap.getString("REPORT_FLAG"))) {
					return;
				}
			} catch (Exception e) {
				SocketUtility.exceptionLogger(logger, "새로 추가 된 로직 예외 확인.", e);
			}
			
			plcService.mergePbsReportBody(reportMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicNotReportProc Exception::", e);
		}
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPrePbsIn(CMap<String, Object> cMap, String txt, String binaryString) {//TODO
		try {
			CMap<String, Object> reportMap = logicReportProc(cMap, txt, binaryString, "PLC_BE_PRE", "PBSL30");
			reportMap.put("PROC_CD", "P390");
			plcService.mergePbsReportBody(reportMap);
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPrePbsIn Exception::", e);
		}
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPrePbsOut(CMap<String, Object> cMap, String txt, String binaryString) {
		try {
			logicNotReportProc(cMap, txt, binaryString, "PBSL30", "PRE_OUT");
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPrePbsIn Exception::", e);
		}
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPbsl31In(CMap<String, Object> cMap, String txt, String binaryString) {
		try {
			logicNotReportProc(cMap, txt, binaryString, "PRE_OUT", "BE_RP");
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPbsl31In Exception::", e);
		}
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPbsl31OutReport(CMap<String, Object> cMap, String txt, String binaryString) { //TODO
		try {
			CMap<String, Object> reportMap = logicReportProc(cMap, txt, binaryString, "BE_RP", "PBSLMP");
			reportMap.put("PROC_CD", "T000");
			plcService.mergePbsReportBody(reportMap);
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPbsl31OutReport Exception::", e);
		}
	}

	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPbslMpIn(CMap<String, Object> cMap, String txt, String binaryString) {
		try {
			logicNotReportProc(cMap, txt, binaryString, "PBSLMP", "BE_OU");
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPbslMpIn Exception::", e);
		}
	}
	
	/**
	 * LS_OUT_1 데이터 처리 로직
	 * 
	 * @param cMap
	 * @return cMap
	 */
	public void logicPbslMpOutReport(CMap<String, Object> cMap, String txt, String binaryString) {
		try {
			logicNotReportProc(cMap, txt, binaryString, "BE_OU", "PBSLOU");
			plcService.csReportData(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController logicPbslMpOutReport Exception::", e);
		}
	}
	
	/**
	 * 값이 변경 시 메모리, 데이터베이스 PLC 데이터 업데이트
	 * 
	 * @return void
	 */
	public int updatePlcData(String txt, String binaryString) {
		int retrunInt = 0;
		try {
			CMap<String, Object> plcDataMap = SocketUtility.listSelMap(plcDataList, "PLC_DIV", txt);
			if (null != plcDataMap && !plcDataMap.getString("PLC_DATA").equals(binaryString)) { // 값이 변경 되어 업데이트
				plcDataMap.put("PLC_DATA", binaryString);
				if (!plcDataMap.getString("PLC_DIV").contains("NONE")&&!plcDataMap.getString("PLC_DIV").contains("CPU_STOP")) {
					retrunInt = plcService.mergeTbRtPlcData(plcDataMap);
				}
				if (!plcDataMap.getString("PLC_DIV").contains("NONE")&&!plcDataMap.getString("PLC_DIV").contains("HEART_BIT")&&!plcDataMap.getString("PLC_DIV").contains("CPU_STOP")) {
					plcService.insertTbRcPlcData(plcDataMap);
					logger.info("UPDATE DATA :: " + txt + " :: " + binaryString);
				}
			} else if (null == plcDataMap) { // 재기동 되어서 업데이트
				CMap<String, Object> listInsertMap = new CMap<>();
				listInsertMap.put("PLC_DIV", txt);
				listInsertMap.put("PLC_DATA", binaryString);
				plcDataList.add(listInsertMap);
				retrunInt = plcService.mergeTbRtPlcData(listInsertMap);
				plcService.insertTbRcPlcData(listInsertMap);
				logger.info("INSERT DATA :: " + txt + " :: " + binaryString);
			}
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController updatePlcData Exception::", e);
		}
		return retrunInt;
	}
}
