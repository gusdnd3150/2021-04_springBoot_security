package com.module.util.system.websocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.AbstractHeaderMapper;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.Application;
import com.spas.module.socket.ProtocolMain;
import com.spas.module.socket.protocol.ProtocolHandler;
import com.spas.module.socket.send.SendHandler;
import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

@ServerEndpoint(value = "/system", configurator = WebSocketConfig.class)
@Component
public class WebSocketServer {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	private static Set<Session> CLIENTS = new CopyOnWriteArraySet();

	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	ProtocolMain protocolMain;
	
	@Value("${package_id}") String pkgId;

	@OnOpen
	public void onOpen(Session session) throws Exception {
		try {
			
			if (!CLIENTS.contains(session)) {
				CLIENTS.add(session);
				JSONObject returnData = new JSONObject();
				JSONObject obj = new JSONObject();
				
				// 공데이터
				List<CMap<String, Object>> temp = new ArrayList<CMap<String,Object>>();
				
				// 초기 데이터 세팅
				List<CMap<String, Object>> socketList = ProtocolMain.socketList; // 
				List<CMap<String, Object>> msgInList = ProtocolMain.skInList; // 
				List<CMap<String, Object>> msgOutList = ProtocolMain.skOutList; //
				List<CMap<String, Object>> msgHeadList =temp; // 
				List<CMap<String, Object>> msgHeadDetailList = temp; // 
				List<CMap<String, Object>> msgBodyList = ProtocolMain.msgBodyList; // 
				List<CMap<String, Object>> msgBodyDetailList = temp; //
			
				List<CMap<String, Object>> socketBzList = ProtocolMain.skBzList; //
				List<CMap<String, Object>> scheduleList = ProtocolMain.skSchList; //
				List<CMap<String, Object>> channelList = ProtocolMain.channelList; //
				
				obj.put("WEB_SK_LIST", objectMapper.writeValueAsString(getSocketList(socketList)));
				obj.put("IN_LIST", objectMapper.writeValueAsString(msgInList));
				obj.put("CHANNEL_LIST", objectMapper.writeValueAsString(channelList));
				obj.put("OUT_LIST", objectMapper.writeValueAsString(msgOutList));
				obj.put("HD_LIST", objectMapper.writeValueAsString(msgHeadList));
				obj.put("HD_DT_LIST", objectMapper.writeValueAsString(msgHeadDetailList));
				obj.put("BODY_LIST", objectMapper.writeValueAsString(msgBodyList));
				obj.put("BODY_DT_LIST", objectMapper.writeValueAsString(msgBodyDetailList));
				obj.put("BZ_LIST", objectMapper.writeValueAsString(socketBzList));
				obj.put("SCH_LIST", objectMapper.writeValueAsString(scheduleList));
				obj.put("PROC_LIST", objectMapper.writeValueAsString(temp));
				obj.put("TOOL_LIST", objectMapper.writeValueAsString(temp));
				obj.put("LINE_LIST", objectMapper.writeValueAsString(temp));
				obj.put("POS_LIST", objectMapper.writeValueAsString(temp));
				
				returnData.put("FLAG", "INIT_INFO");
				returnData.put("DATA_LIST", obj);
				session.getBasicRemote().sendText(returnData.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@OnClose
	public void onClose(Session session) throws Exception {
		try {
			CLIENTS.remove(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		try {
			CLIENTS.remove(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) throws Exception {

		
		
		//CMap<String, Object> inmsgInfo = objectMapper.readValue(null, CMap.class);
		JSONObject returnData = new JSONObject();

		try {
			
			CMap<String, Object> reciveData = objectMapper.readValue(message, CMap.class);
			
			CMap<String, Object> map = new CMap<String, Object>();
			String command = reciveData.get("COMMAND").toString();
			String flag = reciveData.get("FLAG").toString();
			String bodyMsg = reciveData.get("DATA_LIST").toString();
			
			
			boolean allSendYn = false;

			returnData.put("COMMAND", "RESULT");
			returnData.put("FLAG", "SUCCESS");
			returnData.put("DATA_LIST", "SUCCESS");
			
			

			switch (flag) {
			case "RESTART": // 해당 소켓 재시작
				logger.info("RESTART socket::" + bodyMsg);
				map.put("FLAG", "RESTART_RESPONSE");
				
				CMap<String, Object> temp =	SocketUtility.listSelMap(ProtocolMain.socketList, "SK_ID", bodyMsg);
				ProtocolHandler protocolHandler = (ProtocolHandler) temp.get("SOCKET");
				protocolHandler.stopSocket();
				
				break;

			case "OUT_SEND_TEST": // 해당 소켓으로 송신 전문 테스트
				CMap<String, Object> msgInfo = objectMapper.readValue(bodyMsg, CMap.class);

				String skId = msgInfo.getString("SK_ID");
				String msgId = msgInfo.getString("MSG_ID");
				// CMap<String, Object> obj2 = new CMap<>();
				CMap<String,Object> obj2 = new CMap<>();
				

				List<Map<String, Object>> bodyList = (List<Map<String, Object>>) msgInfo.get("BODY");
				for (Map<String, Object> body : bodyList) {
					String valTy = body.get("type").toString();

					if ("STRING".equals(valTy)) {
						obj2.put(body.get("key").toString(), body.get("value").toString());
					} else if ("INT".equals(valTy)) {
						obj2.put(body.get("key").toString(), Integer.parseInt(body.get("value").toString()));
					} else if ("BYTE".equals(valTy)) {
						obj2.put(body.get("key").toString(), Byte.parseByte(body.get("value").toString()));
					} else if ("DOUBLE".equals(valTy)) {
						obj2.put(body.get("key").toString(), Double.parseDouble(body.get("value").toString()));
					} else if ("SHORT".equals(valTy)) {
						obj2.put(body.get("key").toString(), Short.parseShort(body.get("value").toString()));
					}
				}
				
				SendHandler.sendSkId(skId, msgId, obj2);
				break;
				
				
			case "IN_SEND_TEST": // 해당 메시지의 BZ_METHOD를 실행
				CMap<String, Object> inmsgInfo = objectMapper.readValue(bodyMsg, CMap.class);

				String bzMethod = inmsgInfo.getString("BZ_METHOD");
				String inskId = inmsgInfo.getString("SK_ID");
				String inmsgId = inmsgInfo.getString("MSG_ID");
				CMap<String,Object> obj = new CMap<>();

				List<Map<String, Object>> inbodyList = (List<Map<String, Object>>) inmsgInfo.get("BODY");
				for (Map<String, Object> body : inbodyList) {
					String valTy = body.get("type").toString();
					if ("STRING".equals(valTy)) {
						obj.put(body.get("key").toString(), body.get("value").toString());
					} else if ("INT".equals(valTy)) {
						obj.put(body.get("key").toString(), Integer.parseInt(body.get("value").toString()));
					} else if ("BYTE".equals(valTy)) {
						obj.put(body.get("key").toString(), Byte.parseByte(body.get("value").toString()));
					} else if ("DOUBLE".equals(valTy)) {
						obj.put(body.get("key").toString(), Double.parseDouble(body.get("value").toString()));
					} else if ("SHORT".equals(valTy)) {
						obj.put(body.get("key").toString(), Short.parseShort(body.get("value").toString()));
					}
				}
				obj.put("IN_MSG_ID", inmsgId);
				obj.put("IN_SK_ID", inskId);
				runInMethod(bzMethod, obj);
				break;
				
				
			case "REFRASH_DATA": // 인메모리 데이터 select polling 처리
				JSONObject returnData2 = new JSONObject();
				JSONObject obj3 = new JSONObject();

				if("LINE_LIST".equals(bodyMsg)) {
					//obj3.put("LINE_LIST", objectMapper.writeValueAsString(initManager.lineInitList));
				} else if("PROC_LIST".equals(bodyMsg)) {
					//obj3.put("PROC_LIST", objectMapper.writeValueAsString(initManager.procInitList));
				}else if("TOOL_LIST".equals(bodyMsg)) {
					//obj3.put("TOOL_LIST", objectMapper.writeValueAsString(initManager.toolInitList));
				}else if("POS_LIST".equals(bodyMsg)) {
					//obj3.put("POS_LIST", objectMapper.writeValueAsString(initManager.rtOffPosList));
				}
				returnData2.put("FLAG", "REFRASH_DATA");
				returnData2.put("DATA_LIST", obj3);
				session.getBasicRemote().sendText(returnData2.toString());
				break;
			
				
				
			case "INIT_RELOAD": // 인메모리 데이터 reload
				// 기준정보 리로드
				if("INIT_DATA".equals(bodyMsg)) {
					
					ConcurrentHashMap<String, Object> temp3 = new ConcurrentHashMap<String, Object>();
					
					
				// 소켓 정보 리로드
				}else if("SOCKET_DATA".equals(bodyMsg)) {
					//sendAllClientMsg(returnData.toString());
					logger.info("fffffffffffffffffffffffff   start reload Tcp");
					CMap<String, Object> cMap = new CMap<>();
			        cMap.put("PKG_ID", pkgId);
			        //ProtocolHandler tt = cMap.get("SOCKET");
			        		
			        protocolMain.reInit();
					//protocolMain.init(cMap);
					logger.info("fffffffffffffffffffffffff");
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			returnData.put("COMMAND", "RESULT");
			returnData.put("FLAG", "FAIL");
			returnData.put("DATA_LIST", e.toString());
			session.getBasicRemote().sendText(returnData.toString());
		}
		
		// 모든 클라이언트에 보낼지 여부
//		if(allSendYn) {
//			sendAllClientMsg(returnData.toString());
//		}
		session.getBasicRemote().sendText(returnData.toString());

	}
	
	
	
	public void sendAllClientMsg(String msg) {
		for(Session client : CLIENTS) {
			try {
				client.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void runInMethod(String bzMethod, CMap param)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != param) {
			String strClassName = bzMethod.substring(0, bzMethod.indexOf("."));
			String strMethodName = bzMethod.substring(bzMethod.indexOf(".") + 1);

			Object objClass = Application.getBean(strClassName);

			Method mtd = null;
			Method[] methods = objClass.getClass().getDeclaredMethods();

			for (int i = 0; i < methods.length; i++) {
				if (strMethodName.equals(methods[i].getName())) {
					mtd = methods[i];

					Object objRtn = mtd.invoke(objClass, param); // 실질적 함수 호출

					if (null == objRtn) {
						param.clear();
					}

				}
			}

		}
	}
	
	// 불필요 데이터 필터를 위한 가공
	public List<CMap<String, Object>> getSocketList(List<CMap<String, Object>> socketList) {
		List<CMap<String, Object>> returnDt = new ArrayList<CMap<String,Object>>();
		
		for(CMap<String, Object> data : socketList) {
			CMap<String, Object> newDt = new CMap<String, Object>();
			
			Set<String> keys = data.keySet();
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				if(//"HEAD_DETAIL_INTS".equals(key)||"HEAD_DETAIL_LIST".equals(key)||
						"SOCKET".equals(key)||"THREAD".equals(key)) {
				}else {
					newDt.put(key, data.get(key));
				}
			}
			returnDt.add(newDt);
		}
		return returnDt;
	}
	
}
