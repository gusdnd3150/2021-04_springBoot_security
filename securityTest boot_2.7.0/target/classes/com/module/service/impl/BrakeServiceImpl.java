package com.module.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.BrakeDao;
import com.module.dao.CtrlDao;
import com.module.service.BrakeService;
import com.module.service.CtrlService;
import com.spas.module.socket.utility.CMap;

@Service
public class BrakeServiceImpl implements BrakeService {
	private static final Logger logger = LoggerFactory.getLogger(BrakeServiceImpl.class);
	
	@Autowired BrakeDao brakeDao;

	@Override
	public int insertTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception {
		return brakeDao.insertTbRcBrakeModuleRslt(cMap);
	}
	
	@Override
	public CMap<String, Object> selectTbRcBrakeModuleTotalStatus(CMap<String, Object> cMap) throws Exception {
		return brakeDao.selectTbRcBrakeModuleTotalStatus(cMap);
	}
	
	@Override
	public int updateTbRcBrakeModuleRslt(CMap<String, Object> cMap) throws Exception {
		return brakeDao.updateTbRcBrakeModuleRslt(cMap);
	}
	
	@Override
	public int updateTbRcBrakeModuleBodyNo(CMap<String, Object> cMap) throws Exception {
		return brakeDao.updateTbRcBrakeModuleBodyNo(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectMesSendData(CMap<String, Object> cMap) throws Exception {
		return brakeDao.selectMesSendData(cMap);
	}
}
