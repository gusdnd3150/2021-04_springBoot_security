package com.module.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.ToolDao;
import com.module.service.ToolService;
import com.spas.module.socket.utility.CMap;

@Service
public class ToolServiceImpl implements ToolService {
	private static final Logger logger = LoggerFactory.getLogger(ToolServiceImpl.class);
	
	@Autowired ToolDao toolDao;

	@Override
	public int insertTbIfToolRslt(CMap<String, Object> cMap) throws Exception {
		return toolDao.insertTbIfToolRslt(cMap);
	}
	
	@Override
	public int insertMesSendData(CMap<String, Object> cMap) throws Exception {
		return toolDao.insertMesSendData(cMap);
	}

	@Override
	public CMap<String, Object> selectToolInOut(CMap<String, Object> cMap) throws Exception {
		return toolDao.selectToolInOut(cMap);
	}
	
	@Override
	public List<CMap<String, Object>> selectTestData(CMap<String, Object> cMap) throws Exception {
		return toolDao.selectTestData(cMap);
	}
	
	@Override
	public int updateTestData(CMap<String, Object> cMap) throws Exception {
		return toolDao.updateTestData(cMap);
	}
	
	@Override
	public int updateTestAckData(CMap<String, Object> cMap) throws Exception {
		return toolDao.updateTestAckData(cMap);
	}
}
