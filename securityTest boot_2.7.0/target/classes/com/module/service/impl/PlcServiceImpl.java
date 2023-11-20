package com.module.service.impl;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.PlcDao;
import com.module.service.PbsService;
import com.module.service.PlcService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

@Service
public class PlcServiceImpl implements PlcService {
	private static final Logger logger = LoggerFactory.getLogger(PlcServiceImpl.class);
	
	@Autowired PlcDao plcDao;
	
	@Autowired PbsService pbsService;
	
	@Override
	public int mergeTbRtPlcData(CMap<String, Object> cMap) throws Exception {
		return plcDao.mergeTbRtPlcData(cMap);
	}

	@Override
	public int insertTbRcPlcData(CMap<String, Object> cMap) throws Exception {
		return plcDao.insertTbRcPlcData(cMap);
	}
	
	@Override
	public int insertPbsReportBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.insertPbsReportBody(cMap);
	}
	
	@Override
	public int deletePbsReportBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.deletePbsReportBody(cMap);
	}
	
	@Override
	public int deletePlcOutBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.deletePlcOutBody(cMap);
	}
	
	@Override
	public int mergePbsReportBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.mergePbsReportBody(cMap);
	}
	
	@Override
	public int updateEmpIdxCsProc(CMap<String, Object> cMap) throws Exception {
		return plcDao.updateEmpIdxCsProc(cMap);
	}
	
	@Override
	public int updateMesSeqCsProc(CMap<String, Object> cMap) throws Exception {
		return plcDao.updateMesSeqCsProc(cMap);
	}
	
	@Override
	public int updateCsReportBodyFlag(CMap<String, Object> cMap) throws Exception {
		return plcDao.updateCsReportBodyFlag(cMap);
	}
	
	@Override
	public int updateMesSeqReportBodyFlag(CMap<String, Object> cMap) throws Exception {
		return plcDao.updateMesSeqReportBodyFlag(cMap);
	}
	
	@Override
	public int updatePbsReportBodyFlag(CMap<String, Object> cMap) throws Exception {
		return plcDao.updatePbsReportBodyFlag(cMap);
	}
	
	@Override
	public int updateTbBiLineActiveYn(CMap<String, Object> cMap) throws Exception {
		return plcDao.updateTbBiLineActiveYn(cMap);
	}
	
	@Override
	public int insertTbRcPrcoReport(CMap<String, Object> cMap) throws Exception {
		return plcDao.insertTbRcPrcoReport(cMap);
	}
	
	@Override
	public CMap<String, Object> selectPbsReportBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectPbsReportBody(cMap);
	}
	
	@Override
	public CMap<String, Object> selectCsReportBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectCsReportBody(cMap);
	}
	
	@Override
	public CMap<String, Object> selectEmpIdx(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectEmpIdx(cMap);
	}
	
	@Override
	public CMap<String, Object> selectReportTargetBody(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectReportTargetBody(cMap);
	}
	
	@Override
	public CMap<String, Object> selectBodyReportMap(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectBodyReportMap(cMap);
	}
	
	@Override
	public CMap<String, Object> selectEmpIdxMap(CMap<String, Object> cMap) throws Exception {
		return plcDao.selectEmpIdxMap(cMap);
	}
	
	@Override
	public void csReportData(CMap<String, Object> cMap) {
		try {
			List<CMap<String, Object>> csReportList = pbsService.selectCsReport(cMap);
			JSONArray ja = new JSONArray();
			for (CMap<String, Object> csReportMap : csReportList) {
				if (csReportMap.getString("PROC_CD").equals("PLC_TTS")) {
					continue;
				}
				if (csReportMap.getString("PROC_CD_DT").equals("PBSL30") || csReportMap.getString("PROC_CD_DT").equals("PRE_OUT")) {
					csReportMap.put("PROC_CD", "PBSL30");
				} else if (csReportMap.getString("PROC_CD_DT").equals("BE_RP") || csReportMap.getString("PROC_CD_DT").equals("PBSL31")) {
					csReportMap.put("PROC_CD", "PBSL31");
				} else if (csReportMap.getString("PROC_CD_DT").equals("PBSLMP") || csReportMap.getString("PROC_CD_DT").equals("BE_OU")) {
					csReportMap.put("PROC_CD", "PBSLMP");
				}
				JSONObject jo = new JSONObject();
				jo.put("PROC_CD", csReportMap.getString("PROC_CD"));
				jo.put("PROC_CD_DT", csReportMap.getString("PROC_CD_DT"));
				jo.put("MES_PROD_SEQ", csReportMap.getString("MES_PROD_SEQ"));
				jo.put("TAG_ID", csReportMap.getString("TAG_ID"));
				jo.put("BODY_NO", csReportMap.getString("BODY_NO"));
				jo.put("VIN_NO", csReportMap.getString("VIN_NO"));
				jo.put("EMP_IDX", csReportMap.getString("EMP_IDX"));
				jo.put("BATTERY_LEVEL", csReportMap.getString("BATTERY_LEVEL"));
				jo.put("PLC_BODY_NO", csReportMap.getString("PLC_BODY_NO"));
				
				logger.info("csReportMap" + csReportMap);
				if (null != csReportMap.getString("REPORT_FLAG")&&(csReportMap.getString("REPORT_FLAG").equals("0")||csReportMap.getString("REPORT_FLAG").equals("2"))) {
					jo.put("OUT_RESULT", "N");
				} else if (null != csReportMap.getString("REPORT_FLAG")&&csReportMap.getString("REPORT_FLAG").equals("1")) {
					jo.put("OUT_RESULT", "Y");
				} else if (null != csReportMap.getString("REPORT_FLAG")&&csReportMap.getString("REPORT_FLAG").equals("3")) {
					jo.put("OUT_RESULT", "R");
				}
				
				ja.put(jo);
			}
			
			cMap.put("DATAS", ja);
			logger.info("~~~~~~~~~~~~~~~~~~~" + ja);
			SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_PBS_LINE_STATUS", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController csReportData Exception::", e);
		}
	}
	
	@Override
	public void csExpData(CMap<String, Object> cMap) {
		try {
			List<CMap<String, Object>> csExpList = pbsService.selectTbRcExp(cMap);
			JSONArray ja = new JSONArray();
			for (CMap<String, Object> csExpMap : csExpList) {
				JSONObject jo = new JSONObject();
				jo.put("MES_PROD_SEQ", csExpMap.getString("MES_PROD_SEQ"));
				jo.put("TAG_ID", csExpMap.getString("TAG_ID"));
				jo.put("BODY_NO", csExpMap.getString("BODY_NO"));
				jo.put("VIN_NO", csExpMap.getString("VIN_NO"));
				jo.put("DATE_TIME", csExpMap.getString("REG_DT"));
				
				logger.info("csExpMap" + csExpMap);
				
				ja.put(jo);
			}
			
			cMap.put("DATAS", ja);
			logger.info("~~~~~~~~~~~~~~~~~~~" + ja);
			SendHandler.sendSkId("TCPS_CS_TO_CORE", "CS_LIST_OUT_EXP", cMap);
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "PlcController csReportData Exception::", e);
		}
	}

}
