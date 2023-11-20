package com.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spas.module.socket.utility.CMap;

@Mapper
public interface InitManagerDao {

	public List<CMap<String, Object>> selectTbBiTool(CMap<String, Object> cMap) throws Exception;
	
	public List<CMap<String, Object>> selectTbBiCar(CMap<String, Object> cMap) throws Exception;
	
}
