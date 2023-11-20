package com.module.controller.app;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

@Controller
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Autowired PlcController plcController;
	@Autowired VccController vccController;
	@Autowired PbsController pbsController;
	
	public CMap<String, Object> testFn(CMap<String, Object> cMap) {
		logger.info("testFn ::" + cMap);
		try {
			if (cMap.getString("TEST_DIV").trim().equals("0")) {//plc 클리어
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {0, 0, 0, 0};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			}else if (cMap.getString("TEST_DIV").trim().equals("1")) { //공정진입
				cMap.put("PROC_CD", "PBSL30");
				vccController.reciveProcIn(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("2")) {//공정진입
				cMap.put("PROC_CD", "PBSL31");
				vccController.reciveProcIn(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("3")) {//포크 바디
				Encoder encoder = Base64.getEncoder();
				cMap.put("READARRAY", new String(encoder.encode(cMap.getString("BODY_NO").getBytes())));
				cMap.put("SCH_ID", "PRE");
				plcController.logicPreBody(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("4")) {//포크안착
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {4, 0, 4, 0};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("5")) {//PRE_OUT
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {32, 0, 32, 0};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("6")) {//BE_RP
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {0, 1, 0, 1};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("7")) {//PBSL31
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {0, 2, 0, 2};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("8")) {//BE_OU
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {0, 8, 0, 8};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			} else if (cMap.getString("TEST_DIV").trim().equals("9")) {//PBSLOU
				Encoder encoder = Base64.getEncoder();
				byte[] byteSh = {0, 16, 0, 16};
				cMap.put("READARRAY", new String(encoder.encode(byteSh)));
				plcController.logicCts_1(cMap);
			}else if (cMap.getString("TEST_DIV").trim().equals("10")) { //공정진입
				cMap.put("PROC_CD", "PBSL32");
				vccController.reciveProcIn(cMap);
			}
//			else if (cMap.getString("TEST_DIV").trim().equals("8")) {//공정보고 요청 30
//				cMap.put("SIG_DIV", "RQ");
//				cMap.put("PBS_DIV", ";");
//				cMap.put("STN_CD", "P390");
//				pbsController.reciveProcBodyReq(cMap);
//			} else if (cMap.getString("TEST_DIV").trim().equals("9")) {//공정보고 31 
//				cMap.put("SIG_DIV", "RQ");
//				cMap.put("PBS_DIV", ";");
//				cMap.put("STN_CD", "T000");
//				pbsController.reciveProcBodyReq(cMap);
//			} else if (cMap.getString("TEST_DIV").trim().equals("10")) {//공정보고 요청 TTS
//				cMap.put("SIG_DIV", "RQ");
//				cMap.put("PBS_DIV", ";");
//				cMap.put("STN_CD", "P400");
//				pbsController.reciveProcBodyReq(cMap);
//			}

		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "TestController testFn Exception::", e);
		}
		return cMap;
	}
}