package com.module.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface MainDao {

	public Map<String, Object> TestSelect(Map<String, Object> hMap);

}
