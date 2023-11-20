package com.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface PbsDao {

	public CMap<String, Object> selectSqMesProdPbs(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectCsReport(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectPbsPlcStat(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectListLeaveList(CMap<String, Object> cMap) throws Exception;
	
	public int insertTbRcExp(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectTbRcExp(CMap<String, Object> cMap) throws Exception;
}
