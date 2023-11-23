package com.module.system.security.test;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserMap<K, V> extends HashMap<K, V> implements UserDetails {
	private static final long serialVersionUID = 6723434363565852261L;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>(); // 권한 정보
		String auths = (String) super.get("AUTHS");
		if(auths != null && !"".contentEquals(auths)) {
			String[] roles = auths.split("/");
			for(String role : roles) {
				auth.add(new SimpleGrantedAuthority("ROLE_"+role));
			}
		}
		return auth;
	}


	@Override
	public String getPassword() {
		return (String) this.get("LOGIN_PW");
	}


	@Override
	public String getUsername() {
		return (String) this.get("LOGIN_ID");
	}

	@Override
	public boolean isAccountNonExpired() {
		return Boolean.parseBoolean((String) this.get("ISACCOUNTNONEXPIRED"));
	}


	@Override
	public boolean isAccountNonLocked() {
		return Boolean.parseBoolean((String) this.get("ISACCOUNTNONLOCKED"));
	}


	@Override
	public boolean isCredentialsNonExpired() {
		return Boolean.parseBoolean((String) this.get("ISCREDENTIALSNONEXPIRED"));
	}


	@Override
	public boolean isEnabled() {
		return Boolean.parseBoolean((String) this.get("ISENABLED"));
	}

	/**
	 * default null
	 * @param key
	 * @return
	 */
	public K getString(K key){
		K rt = null;
		 
		try{
			V obj = super.get(key);
			
			if(null != obj && (obj instanceof Integer || obj instanceof BigDecimal )){ 
				rt = (K) ("" + get(key));
			}else{
				rt = (K)obj;
			}
		}catch(Exception e){}
		
		return rt;
	}
 
	
	/**
	 * default -1
	 * @param key
	 * @return
	 */
	public int getInt(K key){
		V obj = super.get(key);
		
		if(null == obj) {
			obj = (V) "-1";
		}
		
		return Integer.parseInt("" + obj);
	}
	
	
	/**
	 * exception null
	 * @param key
	 * @return
	 */
	public byte[] getBytes(K key){
		try {
			return (byte[])super.get(key);
		}catch(Exception e) {
			return null;
		}
	}
	
	
	public byte getByte(K key){
		return (Byte)super.get(key);
	}
	
	
	public double getDouble(K key){
		return (Double)super.get(key);
	}
	
	
	public float getFloat(K key){
		return (Float)super.get(key);
	}
	
	public short getShort(K key){
		return (Short)super.get(key);
	}
	
	
	public char getChar(K key){
		return (Character)super.get(key);
	}
	
	public boolean getBoolean(K key){
		boolean isTrue = false;
		
		if(null != super.get(key)) {
			isTrue = (Boolean)super.get(key);
		}
		
		return isTrue;
	}
	
	public long getLong(K key){
		return (Long)super.get(key);
	}
	
	public JSONObject getJson(K key){
		return (JSONObject)super.get(key);
	}
	
	public JSONArray getJsonArray(K key){
		return (JSONArray)super.get(key);
	}
		
	public UserMap<K, V> getHMap(K key){
		UserMap<K, V> rtn = null;
		
		try {
			rtn = (UserMap<K, V>) super.get(key);
		}catch(Exception e) {
			rtn = new UserMap();
		}
		
		return rtn;
	}
	
	public List<UserMap<K, V>> getListHMap(K key){
		List<UserMap<K, V>> rtn = null;
		
		try {
			rtn = (List<UserMap<K, V>>) super.get(key);
		}catch(Exception e) {
			rtn = new ArrayList();
		}
		
		return rtn;
	}
	
	public List getList(K key){
		List rtn = null;
		
		try {
			rtn = (List) super.get(key);
		}catch(Exception e) {
			rtn = new ArrayList();
		}
		
		return rtn;
	}
	
	public boolean isExist(K key) {
		boolean isHave = true;
		
		try {
			V obj = super.get(key);
			
			if(null == obj) {
				isHave = false;
			}
		}catch(Exception e) {
			isHave = false;
		}
		
		return isHave;
	}


	


}
