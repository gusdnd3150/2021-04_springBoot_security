package com.module.service;

import java.util.List;

import com.spas.module.socket.utility.CMap;

public interface VccService {

	public int insertToolInOut(CMap<String, Object> cMap) throws Exception;
	
	public int insertTbRtPos(CMap<String, Object> cMap) throws Exception;

	public int insertTbRcMapping(CMap<String, Object> cMap) throws Exception;

	public CMap<String, Object> selectMaxMappingSeq(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTagTagId(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTagMacAddr(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectUseReport(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTagBat(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectUseTag(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectLineReportList(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectTagInOutList(CMap<String, Object> cMap) throws Exception;
	
	public CMap<String, Object> selectTbRtPos(CMap<String, Object> cMap) throws Exception;
	
	public int deleteTbRtPos(CMap<String, Object> cMap) throws Exception;

	public int updateTbRtPos(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRtPosProcCd(CMap<String, Object> cMap) throws Exception;
	
	public int updateTbRtToolPos(CMap<String, Object> cMap) throws Exception;
	
	public int insertTbRcProcInout(CMap<String, Object> cMap) throws Exception;
	
	public int mergeTbRtProcInout(CMap<String, Object> cMap) throws Exception;
	
	public int mergePbslTagInOut(CMap<String, Object> cMap) throws Exception;
	
	public int deletePbslTagInOut(CMap<String, Object> cMap) throws Exception;
	
	public int deletePbslTagIn(CMap<String, Object> cMap) throws Exception;

}
