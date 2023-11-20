package com.module.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.MesDao;
import com.module.service.MesService;
import com.spas.module.socket.utility.CMap;

@Service
public class MesServiceImpl implements MesService {
	private static final Logger logger = LoggerFactory.getLogger(MesServiceImpl.class);
	
	@Autowired MesDao mesDao;

	@Override
	public CMap<String, Object> selectIfMesProd(CMap<String, Object> cMap) throws Exception {
		return mesDao.selectIfMesProd(cMap);
	}
	
	@Override
	public CMap<String, Object> selectIfMesProdSeq(CMap<String, Object> cMap) throws Exception {
		return mesDao.selectIfMesProdSeq(cMap);
	}
	
	@Override
	public CMap<String, Object> selectRcReportData(CMap<String, Object> cMap) throws Exception {
		return mesDao.selectRcReportData(cMap);
	}
	
	@Override
	public CMap<String, Object> selectMaxRcReportData(CMap<String, Object> cMap) throws Exception {
		return mesDao.selectMaxRcReportData(cMap);
	}
	
	@Override
	public int insertTbIfMesProd(CMap<String, Object> cMap) throws Exception {
		return mesDao.insertTbIfMesProd(cMap);
	}
	
	@Override
	public int deleteRcProcReportBody(CMap<String, Object> cMap) throws Exception {
		return mesDao.deleteRcProcReportBody(cMap);
	}
	
	@Override
	public int deleteRcProcProcInoutBody(CMap<String, Object> cMap) throws Exception {
		return mesDao.deleteRcProcProcInoutBody(cMap);
	}

	@Override
	public CMap<String, Object> selectTbBiCarCd(CMap<String, Object> cMap) throws Exception {
		return mesDao.selectTbBiCarCd(cMap);
	}

}
