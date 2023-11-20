package com.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface BrakeDao {

	public int insertTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTbRcBrakeModuleTotalStatus(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRcBrakeModuleBodyNo(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectMesSendData(CMap<String, Object> cMap) throws Exception;
	
}
