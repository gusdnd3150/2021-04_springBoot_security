package com.module.dao;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface CtrlDao {

	public CMap<String, Object> selectCtrlInfo(CMap<String, Object> cMap) throws Exception;

	public int mergeTagStat(CMap<String, Object> cMap) throws Exception;
	
}
