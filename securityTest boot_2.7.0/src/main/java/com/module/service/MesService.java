package com.module.service;

import com.spas.module.socket.utility.CMap;

public interface MesService {

	public CMap<String, Object> selectIfMesProd(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectIfMesProdSeq(CMap<String, Object> cMap) throws Exception;

	public int insertTbIfMesProd(CMap<String, Object> cMap) throws Exception;
	
	public int deleteRcProcReportBody(CMap<String, Object> cMap) throws Exception;
	
	public int deleteRcProcProcInoutBody(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectRcReportData(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectMaxRcReportData(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTbBiCarCd(CMap<String, Object> cMap) throws Exception;
	
}
