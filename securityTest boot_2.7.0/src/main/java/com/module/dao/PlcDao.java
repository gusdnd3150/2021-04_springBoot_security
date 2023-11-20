package com.module.dao;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface PlcDao {

	public int mergeTbRtPlcData(CMap<String, Object> cMap) throws Exception;
	
	public int insertTbRcPlcData(CMap<String, Object> cMap) throws Exception;
	
	public int insertPbsReportBody(CMap<String, Object> cMap) throws Exception;
	
	public int deletePbsReportBody(CMap<String, Object> cMap) throws Exception;
	
	public int deletePlcOutBody(CMap<String, Object> cMap) throws Exception;
	
	public int mergePbsReportBody(CMap<String, Object> cMap) throws Exception;
	
	public int updateEmpIdxCsProc(CMap<String, Object> cMap) throws Exception;
	
	public int updateMesSeqCsProc(CMap<String, Object> cMap) throws Exception;
	
	public int updateCsReportBodyFlag(CMap<String, Object> cMap) throws Exception;
	
	public int updateMesSeqReportBodyFlag(CMap<String, Object> cMap) throws Exception;

	public int updatePbsReportBodyFlag(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbBiLineActiveYn(CMap<String, Object> cMap) throws Exception;
	
	public int insertTbRcPrcoReport(CMap<String, Object> cMap) throws Exception;

	public CMap<String, Object> selectPbsReportBody(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectCsReportBody(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectEmpIdx(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectReportTargetBody(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectBodyReportMap(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectEmpIdxMap(CMap<String, Object> cMap) throws Exception;
	
}
