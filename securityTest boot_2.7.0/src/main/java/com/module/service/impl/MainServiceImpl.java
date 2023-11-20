package com.module.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.dao.MainDao;
import com.module.service.MainService;

@Service
public class MainServiceImpl implements MainService {
	private static final Logger logger = LoggerFactory.getLogger(MainServiceImpl.class);
	
	@Autowired MainDao mainDao;
	
	@Override
	public Map<String, Object> TestSelect(Map<String, Object> map) throws Exception {
		return mainDao.TestSelect(map);
	}

}
