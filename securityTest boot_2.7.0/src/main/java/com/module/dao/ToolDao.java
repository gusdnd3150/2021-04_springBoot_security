package com.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface ToolDao {

	public int insertTbIfToolRslt(CMap<String, Object> cMap) throws Exception;
	
	public int insertMesSendData(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectToolInOut(CMap<String, Object> cMap) throws Exception;

	public List<CMap<String, Object>> selectTestData(CMap<String, Object> cMap) throws Exception;
	
	public int updateTestData(CMap<String, Object> cMap) throws Exception;
	
	public int updateTestAckData(CMap<String, Object> cMap) throws Exception;
}
