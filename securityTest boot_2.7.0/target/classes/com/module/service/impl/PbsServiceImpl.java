package com.module.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.PbsDao;
import com.module.service.PbsService;
import com.spas.module.socket.utility.CMap;

@Service
public class PbsServiceImpl implements PbsService {
	private static final Logger logger = LoggerFactory.getLogger(PbsServiceImpl.class);
	
	@Autowired PbsDao pbsDao;

	@Override
	public CMap<String, Object> selectSqMesProdPbs(CMap<String, Object> cMap) throws Exception {
		return pbsDao.selectSqMesProdPbs(cMap);
	}
	
	
	@Override
	public List<CMap<String, Object>> selectCsReport(CMap<String, Object> cMap) throws Exception {
		return pbsDao.selectCsReport(cMap);
	}
	
	@Override
	public CMap<String, Object> selectPbsPlcStat(CMap<String, Object> cMap) throws Exception {
		return pbsDao.selectPbsPlcStat(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectListLeaveList(CMap<String, Object> cMap) throws Exception {
		return pbsDao.selectListLeaveList(cMap);
	}
	
	@Override
	public int insertTbRcExp(CMap<String, Object> cMap) throws Exception {
		return pbsDao.insertTbRcExp(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectTbRcExp(CMap<String, Object> cMap) throws Exception {
		return pbsDao.selectTbRcExp(cMap);
	}
}
