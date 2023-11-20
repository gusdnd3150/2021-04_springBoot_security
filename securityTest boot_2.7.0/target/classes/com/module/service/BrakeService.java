package com.module.service;

import java.util.List;

import com.spas.module.socket.utility.CMap;

public interface BrakeService {
	
	public int insertTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTbRcBrakeModuleTotalStatus(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRcBrakeModuleBodyNo(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectMesSendData(CMap<String, Object> cMap) throws Exception;
}
