package com.module.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.VccDao;
import com.module.service.VccService;
import com.spas.module.socket.utility.CMap;

@Service
public class VccServiceImpl implements VccService {
	private static final Logger logger = LoggerFactory.getLogger(VccServiceImpl.class);
	
	@Autowired VccDao vccDao;
	
	@Override
	public int insertToolInOut(CMap<String, Object> cMap) throws Exception {
		return vccDao.insertToolInOut(cMap);
	}
	
	@Override
	public int insertTbRtPos(CMap<String, Object> cMap) throws Exception {
		return vccDao.insertTbRtPos(cMap);
	}

	@Override
	public int insertTbRcMapping(CMap<String, Object> cMap) throws Exception {
		return vccDao.insertTbRcMapping(cMap);
	}

	@Override
	public CMap<String, Object> selectMaxMappingSeq(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectMaxMappingSeq(cMap);
	}
	
	@Override
	public CMap<String, Object> selectTagTagId(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectTagTagId(cMap);
	}
	
	@Override
	public CMap<String, Object> selectTagMacAddr(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectTagMacAddr(cMap);
	}
	
	@Override
	public CMap<String, Object> selectUseReport(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectUseReport(cMap);
	}
	
	@Override
	public CMap<String, Object> selectTagBat(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectTagBat(cMap);
	}
	
	@Override
	public CMap<String, Object> selectUseTag(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectUseTag(cMap);
	}
	
	@Override
	public CMap<String, Object> selectTbRtPos(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectTbRtPos(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectLineReportList(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectLineReportList(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectTagInOutList(CMap<String, Object> cMap) throws Exception {
		return vccDao.selectTagInOutList(cMap);
	}

	@Override
	public int deleteTbRtPos(CMap<String, Object> cMap) throws Exception {
		return vccDao.deleteTbRtPos(cMap);
	}

	@Override
	public int updateTbRtPos(CMap<String, Object> cMap) throws Exception {
		return vccDao.updateTbRtPos(cMap);
	}
	
	@Override
	public int updateTbRtPosProcCd(CMap<String, Object> cMap) throws Exception {
		return vccDao.updateTbRtPosProcCd(cMap);
	}
	
	@Override
	public int updateTbRtToolPos(CMap<String, Object> cMap) throws Exception {
		return vccDao.updateTbRtToolPos(cMap);
	}
	
	@Override
	public int insertTbRcProcInout(CMap<String, Object> cMap) throws Exception {
		return vccDao.insertTbRcProcInout(cMap);
	}
	
	@Override
	public int mergeTbRtProcInout(CMap<String, Object> cMap) throws Exception {
		return vccDao.mergeTbRtProcInout(cMap);
	}
	
	@Override
	public int mergePbslTagInOut(CMap<String, Object> cMap) throws Exception {
		return vccDao.mergePbslTagInOut(cMap);
	}
	
	@Override
	public int deletePbslTagInOut(CMap<String, Object> cMap) throws Exception {
		return vccDao.deletePbslTagInOut(cMap);
	}
	
	@Override
	public int deletePbslTagIn(CMap<String, Object> cMap) throws Exception {
		return vccDao.deletePbslTagIn(cMap);
	}

}
