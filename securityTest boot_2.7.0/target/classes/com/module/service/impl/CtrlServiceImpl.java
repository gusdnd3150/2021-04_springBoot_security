package com.module.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.CtrlDao;
import com.module.service.CtrlService;
import com.spas.module.socket.utility.CMap;

@Service
public class CtrlServiceImpl implements CtrlService {
	private static final Logger logger = LoggerFactory.getLogger(CtrlServiceImpl.class);
	
	@Autowired CtrlDao ctrlDao;

	@Override
	public CMap<String, Object> selectCtrlInfo(CMap<String, Object> cMap) throws Exception {
		return ctrlDao.selectCtrlInfo(cMap);
	}
	
	
	@Override
	public int mergeTagStat(CMap<String, Object> cMap) throws Exception {
		return ctrlDao.mergeTagStat(cMap);
	}

	

}
