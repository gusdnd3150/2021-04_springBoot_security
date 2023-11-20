package com.module.controller.app;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.module.service.MesService;
import com.module.service.PbsService;
import com.module.service.PlcService;
import com.module.service.VccService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.channel.Channel;

@Controller
public class VccController {
	private static final Logger logger = LoggerFactory.getLogger(VccController.class);
	
	@Autowired VccService vccService;
	
	@Autowired PlcService plcService;
	
	@Autowired MesService mesService;
	
	@Autowired PbsService pbsService;
	
	public CMap<String, Object> activeSendLineSig(CMap<String, Object> cMap) {
		try {
			List<CMap<String, Object>> plcDataList = PlcController.plcDataList;
			for (int i = 0; i < plcDataList.size(); i++) {
				CMap<String, Object> plcMap = plcDataList.get(i);
				if (plcMap.getString("PLC_DIV").contains("LINE_SIG")) {
					String[] splLineCd = plcMap.getString("PLC_DIV").split("_");
					if (splLineCd.length == 3) {
						CMap<String, Object> sendMap = new CMap<>();
						sendMap.put("LINE_CD", splLineCd[0]);
						sendMap.put("LINE_SIGN", plcMap.getString("PLC_DATA"));
						SendHandler.sendSkId("TCPS_VCC_TO_CORE", "LINE_SIGNAL", sendMap);
					}
				}
			}
			
			vccService.deletePbslTagInOut(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController activeSendLineSig Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 현재 공구 진입 진출 로직만 되어있음.
	 */
	public CMap<String, Object> reciveCreateCarPbs(CMap<String, Object> cMap) {
		try {
			logger.info("VccController reciveCreateCarPbs" + cMap);
			Channel reciveChannel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("LINE_CD", "PBSL");
//			vccService.insertTbRtPos(cMap);
			vccService.insertTbRcMapping(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveCreateCarPbs Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 현재 공구 진입 진출 로직만 되어있음.
	 */
	public CMap<String, Object> reciveProcIn(CMap<String, Object> cMap) {
		try {
			logger.info("VccController reciveProcIn" + cMap);
//			Channel reciveChannel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("INOUT_TY", "IN");
			vccService.insertTbRcProcInout(cMap);
			vccService.mergeTbRtProcInout(cMap);
			vccService.updateTbRtPosProcCd(cMap);
			
			if ("PBSL30".equals(cMap.getString("PROC_CD"))||"PBSL31".equals(cMap.getString("PROC_CD"))) {
				CMap<String, Object> selProcMap = new CMap<>();
				selProcMap.putAll(cMap);
				if ("PBSL30".equals(cMap.getString("PROC_CD"))) {
					selProcMap.put("PROC_CD", "P390");
				} else if ("PBSL31".equals(cMap.getString("PROC_CD"))) {
					selProcMap.put("PROC_CD", "T000");
				}
				CMap<String, Object> rcReortMap = mesService.selectRcReportData(selProcMap);// 공정보고 이력조회
				if ("PBSL30".equals(cMap.getString("PROC_CD"))) {
					selProcMap.put("PROC_CD", "PBSL30");
				} else if ("PBSL31".equals(cMap.getString("PROC_CD"))) {
					selProcMap.put("PROC_CD", "PBSLMP");
				}
				CMap<String, Object> rtReortMap = plcService.selectPbsReportBody(selProcMap);//현재 공정 보고 있는지 조회
				
				if (null == rcReortMap && null != rtReortMap && "".equals(rtReortMap.getString("BODY_NO").trim()) && cMap.getString("BODY_NO").equals(rtReortMap.getString("PLC_BODY_NO"))) {
					logger.info("========== 보정로직 ========== " + selProcMap + "\n==========공정보고이력 태그ID==========" + rcReortMap + "\n==========다음LS전까지 유지데이터==========" + rtReortMap);
					if ("PBSL30".equals(cMap.getString("PROC_CD"))) {
						selProcMap.put("PROC_CD", "P390");
					} else if ("PBSL31".equals(cMap.getString("PROC_CD"))) {
						selProcMap.put("PROC_CD", "T000");
					}
					plcService.mergePbsReportBody(selProcMap);
					if ("PBSL30".equals(cMap.getString("PROC_CD"))) {
						selProcMap.put("PROC_CD", "PBSL30");
					} else if ("PBSL31".equals(cMap.getString("PROC_CD"))) {
						selProcMap.put("PROC_CD", "PBSLMP");
					}
					plcService.mergePbsReportBody(selProcMap);
				}
			}
			
			if ("TR0101".equals(cMap.getString("PROC_CD"))
					||"TR0201".equals(cMap.getString("PROC_CD"))
					||"TR0301".equals(cMap.getString("PROC_CD"))
					||"CS0101".equals(cMap.getString("PROC_CD"))
					||"CS0201".equals(cMap.getString("PROC_CD"))
					||"FN0101".equals(cMap.getString("PROC_CD"))
					||"FN0201".equals(cMap.getString("PROC_CD"))
					||"FN0301".equals(cMap.getString("PROC_CD"))
					||"FN0401".equals(cMap.getString("PROC_CD"))) {
				plcService.mergePbsReportBody(cMap);
			}
			
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveProcIn Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 현재 공구 진입 진출 로직만 되어있음.
	 */
	public CMap<String, Object> reciveProcOut(CMap<String, Object> cMap) {
		try {
			logger.info("VccController reciveProcOut" + cMap);
//			Channel reciveChannel = (Channel) cMap.get("RECIVE_CHANNEL");
			cMap.put("INOUT_TY", "OUT");
			vccService.insertTbRcProcInout(cMap);
			vccService.mergeTbRtProcInout(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveProcOut Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 현재 공구 진입 진출 로직만 되어있음.
	 */
	public CMap<String, Object> reciveToolIn(CMap<String, Object> cMap) {
		try {
//			logger.info("VccController reciveToolIn" + cMap);
			cMap.put("INOUT_TY", "IN");
			vccService.insertToolInOut(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolIn Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveToolOut(CMap<String, Object> cMap) {
		try {
			//logger.info("VccController reciveToolOut" + cMap);
			cMap.put("INOUT_TY", "OUT");
			vccService.insertToolInOut(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolOut Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveCreateCarAck(CMap<String, Object> cMap) {
		try {
			//logger.info("VccController reciveCreateCarAck" + cMap);
//			vccService.insertTbRtPos(cMap);
			vccService.insertTbRcMapping(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveCreateCarAck Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> recivePbsOutTtsCarAck(CMap<String, Object> cMap) {
		try {
			//logger.info("VccController reciveCarMappingAck" + cMap);
			vccService.insertTbRcMapping(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveCarMappingAck Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveCarMappingAck(CMap<String, Object> cMap) {
		try {
			//logger.info("VccController reciveCarMappingAck" + cMap);
			vccService.insertTbRcMapping(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveCarMappingAck Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveSignOff(CMap<String, Object> cMap) {
		try {
			logger.info("VccController reciveSignOff" + cMap);
			vccService.deleteTbRtPos(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveSignOff Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveLocInfo(CMap<String, Object> cMap) {// TODO
		try {
			//logger.info("VccController reciveLocInfo" + cMap);
			vccService.updateTbRtPos(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveLocInfo Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> reciveToolLocInfo(CMap<String, Object> cMap) {// TODO
		try {
			//logger.info("VccController reciveToolLocInfo" + cMap);
			vccService.updateTbRtToolPos(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolLocInfo Exception::", e);
		}
		return cMap;
	}
	
	/**
	 * 
	 */
	public CMap<String, Object> recivePbslTagIn(CMap<String, Object> cMap) {
		try {
			vccService.mergePbslTagInOut(cMap);
			logicTagInfoSend(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolIn Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> recivePbslTagOut(CMap<String, Object> cMap) {
		try {
			vccService.deletePbslTagIn(cMap);
			logicTagInfoSend(cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolOut Exception::", e);
		}
		return cMap;
	}
	
	public CMap<String, Object> logicTagInfoSend(CMap<String, Object> cMap) { //TODO
		try {
			List<CMap<String, Object>> tagInOutList = vccService.selectTagInOutList(cMap);
			JSONArray ja = new JSONArray();
			for (CMap<String, Object> tagInOutMap : tagInOutList) {
				tagInOutMap.put("MAC_ADDR", tagInOutMap.getString("TAG_ID"));
				
				CMap<String, Object> tagMap = vccService.selectTagMacAddr(tagInOutMap);
				
				if (null != tagMap) {
					tagInOutMap.putAll(tagMap);
				}
				logger.info("tagInOutMap" + tagInOutMap);
				logger.info("tagMap" + tagMap);
				JSONObject jo = new JSONObject();
				jo.put("TAG_ID", tagInOutMap.getString("TAG_ID"));
				jo.put("MAC_ADDR", tagInOutMap.getString("MAC_ADDR"));
				jo.put("BATTERY_LEVEL", tagInOutMap.getString("BATTERY_LEVEL"));
				jo.put("PROC_CD", tagInOutMap.getString("PROC_CD"));
				ja.put(jo);
			}
			cMap.put("DATAS", ja);
			SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_TAG_INFO", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "VccController reciveToolOut Exception::", e);
		}
		return cMap;
	}
}
