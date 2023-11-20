package com.module.service;

import com.spas.module.socket.utility.CMap;

public interface CtrlService {

	public CMap<String, Object> selectCtrlInfo(CMap<String, Object> cMap) throws Exception;

	public int mergeTagStat(CMap<String, Object> cMap) throws Exception;
	
}
