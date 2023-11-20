package com.module;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

public class Test {
//	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) {
		try {
			CMap<String, Object> testMap = new CMap<>();
			testMap.put("MES_PROD_SEQ", "12345678901234567");
			
			long testLong = testMap.getLong("MES_PROD_SEQ");
			testMap.put("MES_PROD_SEQ", testLong);
			
			String a = testMap.getString("MES_PROD_SEQ").substring(5, 17);
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
