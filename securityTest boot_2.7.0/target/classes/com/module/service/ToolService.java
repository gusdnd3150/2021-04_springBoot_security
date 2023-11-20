package com.module.service;

import java.util.List;

import com.spas.module.socket.utility.CMap;

public interface ToolService {

	public int insertTbIfToolRslt(CMap<String, Object> cMap) throws Exception;
	
	public int insertMesSendData(CMap<String, Object> cMap) throws Exception;

	public CMap<String, Object> selectToolInOut(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectTestData(CMap<String, Object> cMap) throws Exception;
	
	public int updateTestData(CMap<String, Object> cMap) throws Exception;

	public int updateTestAckData(CMap<String, Object> cMap) throws Exception;
}
