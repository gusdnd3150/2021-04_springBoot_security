package com.module.service;

import java.util.List;

import com.spas.module.socket.utility.CMap;

public interface PbsService {

	public CMap<String, Object> selectSqMesProdPbs(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectCsReport(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectPbsPlcStat(CMap<String, Object> cMap) throws Exception;

	public List<CMap<String, Object>> selectListLeaveList(CMap<String, Object> cMap) throws Exception;

	public int insertTbRcExp(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectTbRcExp(CMap<String, Object> cMap) throws Exception;
}
