package com.module.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spas.module.socket.utility.CMap;
import com.spas.module.socket.utility.SocketUtility;

public class RestUtility {
	private static final Logger logger = LoggerFactory.getLogger(RestUtility.class);
	
	public static StringBuffer callRestApi(String strUrl, String requestMethod, String contentType, String requestParam) throws Exception {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(250);
			connection.setReadTimeout(250);
			connection.setRequestProperty("content-type", contentType);
			connection.setDoOutput(true);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			bw.write(requestParam);
			bw.flush();
			bw.close();
			
			StringBuffer stringBuffer = new StringBuffer();
	        String inputLine;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((inputLine = bufferedReader.readLine()) != null)  {
	            stringBuffer.append(inputLine);
	        }
	        bufferedReader.close();
	        return stringBuffer;
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "RestUtility callRestApi Exception::", e);
		}
		return null;
	}
	
	public static StringBuffer callSewioRestApi(String strUrl, String requestMethod, String contentType, String requestParam) throws Exception {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(1000);
			connection.setRequestProperty("content-type", contentType);
			connection.setDoOutput(true);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			bw.write(requestParam);
			bw.flush();
			bw.close();
			
			StringBuffer stringBuffer = new StringBuffer();
	        String inputLine;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((inputLine = bufferedReader.readLine()) != null)  {
	            stringBuffer.append(inputLine);
	        }
	        bufferedReader.close();
	        return stringBuffer;
		} catch (Exception e) {
			SocketUtility.exceptionLogger(logger, "RestUtility callRestApi Exception::", e);
		}
		return null;
	}
}
