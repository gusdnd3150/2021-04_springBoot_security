package com.module.init;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.module.dao.InitManagerDao;
import com.spas.module.socket.utility.CMap;


@Component
public class InitManager {
	private static final Logger logger = LoggerFactory.getLogger(InitManager.class);
	
	@Autowired InitManagerDao initDao;
	
	public List<CMap<String, Object>> toolInitList;	//차종 정보
	public List<CMap<String, Object>> carInitList;	//차종 정보
	
	/**
	 * DB 에서 설정 데이터를 읽어서 저장합니다
	 */
	public void loadDb(CMap<String, Object> cMap) {
		////logger.info("loadDb");
		
		toolInitList = new ArrayList<CMap<String,Object>>();	//차종 정보
		carInitList = new ArrayList<CMap<String,Object>>();	//차종 정보
		
		loadToolInitList(cMap);
		loadCarInitList(cMap);
	}


	public void loadToolInitList(CMap<String, Object> cMap)  {
		try {
			toolInitList = initDao.selectTbBiTool(cMap);
			logger.info("loadToolInitList size:" + toolInitList.size());
		} catch (Exception e) {
			logger.info("loadToolInitList Exception:" + e);
		}
		
	}
	
	
	public void loadCarInitList(CMap<String, Object> cMap)  {
		try {
			carInitList = initDao.selectTbBiCar(cMap);
			logger.info("loadCarInitList size:" + carInitList.size());
		} catch (Exception e) {
			logger.info("loadCarInitList Exception:" + e);
		}
		
	}
	
	public CMap<String, Object> listSelMap(List<CMap<String, Object>> list, String key, String findValue) {
		for (int i = 0; i < list.size(); i++) {
			CMap<String, Object> returnMap = list.get(i);
			if (findValue.equals(returnMap.getString(key))) {
				return returnMap;
			}
		}
		return null;
	}
}
