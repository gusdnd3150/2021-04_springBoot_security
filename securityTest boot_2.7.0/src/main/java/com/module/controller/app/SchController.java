package com.module.controller.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.service.CtrlService;
import com.module.service.ToolService;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Controller
public class SchController {
	private static final Logger logger = LoggerFactory.getLogger(SchController.class);
	
	@Autowired CtrlService service;
	
	@Autowired ToolService toolService;
	
	/**
	 * 태그 배터리 업데이트 로직.
	 * http://10.72.199.184/sensmapserver/api/anchors 앵커 주소
	 * @param map
	 * @return
	 */
	public CMap<String,Object> batteryStatSch(CMap<String,Object> map) {
		try {
			//List<SMap<String, Object>> batMapList = restGet("http://192.168.225.2/sensmapserver/api/tags", "X-ApiKey", "RkLHWV7hTUYT4lssKjWU3d8g");
			List<CMap<String, Object>> batMapList = restGet("http://10.72.199.183/sensmapserver/api/tags", "X-ApiKey", "XPtImWyXLyLpwDvSdYM4nAVK");
			for (int i = 0; i < batMapList.size(); i++) {
				CMap<String, Object> batMap = batMapList.get(i);
				service.mergeTagStat(batMap);
			}
			return map;
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "batteryStatSch", e);
			return null;
		}
	}
	
	public List<CMap<String, Object>> restGet(String strUrl, String propKey, String propValue) { //restGet("http://192.168.225.2/sensmapserver/api/tags", "X-ApiKey", "RkLHWV7hTUYT4lssKjWU3d8g");
		try {
			List<CMap<String, Object>> batMapList = new ArrayList<CMap<String,Object>>();	//소켓 정보
			URL url = new URL(strUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
			con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
			con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
			con.addRequestProperty(propKey, propValue); //key값 설정

			con.setRequestMethod("GET");
			
			con.setDoOutput(false); 

			StringBuilder sb = new StringBuilder();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//Stream을 처리해줘야 하는 귀찮음이 있음. 
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				br.close();
				JsonParser jsonParser = new JsonParser();
				JSONObject json = new JSONObject(sb.toString());
				JsonArray jsonArray = (JsonArray) jsonParser.parse(json.getString("results"));
				for (int i = 0; i < jsonArray.size(); i++) {
					CMap<String, Object> batMap = new CMap<String, Object>(); // 이안에 베터리 데이터 있습니다@@@@ {TAG_ID=12345, battery_voltage=3.93, battery_level=77%}
					JSONObject object = new JSONObject(jsonArray.get(i).toString());
					batMap.put("MAC_ADDR", object.getString("title"));
					JsonArray datastreamsJsonArray = (JsonArray) jsonParser.parse(object.getString("datastreams"));
					for (int j = 0; j < datastreamsJsonArray.size(); j++) {
						JsonObject datastreamsObject = (JsonObject)datastreamsJsonArray.get(j);
						String id = datastreamsObject.get("id").getAsString();
						if (null != id && ("battery_level".equals(id) || "battery_voltage".equals(id))) {
							batMap.put(id, datastreamsObject.get("current_value").getAsString());
						}
						
					}
					batMapList.add(batMap);
				}
				return batMapList;
			} else {
				logger.info(con.getResponseMessage());
			}

		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "restGet", e);
		}
		return null;
	}
	
	public CMap<String, Object> testSch(CMap<String, Object> cMap) {
		try {
			////logger.info("SchController testSch" + cMap);
			List<CMap<String, Object>> testList = toolService.selectTestData(cMap);
	        if (null == testList) {
				return null;
			}
	        
			for (int i = 0; i < testList.size(); i++) {
				CMap<String, Object> testMap = testList.get(i);
				
				ByteBuf byteBuf = Unpooled.buffer();
				byteBuf.writeByte(2);
				byteBuf.writeBytes("T".getBytes());
				byteBuf.writeBytes((testMap.getInt("SEND_COUNT")==0)? "T".getBytes() : "R".getBytes());
				byteBuf.writeBytes(testMap.getString("SEND_BYTES").getBytes());
				byteBuf.writeByte(3);
				
				testMap.put("SEND_COUNT", testMap.getInt("SEND_COUNT")+1);
				testMap.put("MSG_RSLT", "R");
				
				byte[] sendBytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(sendBytes);
				
				toolService.updateTestData(testMap);
				SendHandler.sendEventClientFixDviceId(testList.get(i).getString("SK_ID"), testMap.getString("RSLT_SEND_SEQ"), sendBytes);
			}

		} catch (Exception e) {
			////logger.info("SchController testSch Exception::" + e);
		}
		return cMap;
	}
}
