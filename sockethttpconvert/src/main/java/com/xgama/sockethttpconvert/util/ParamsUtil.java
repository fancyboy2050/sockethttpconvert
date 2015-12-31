package com.xgama.sockethttpconvert.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.net.URLEncoder;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;

public class ParamsUtil {

	public static String encode(String s, String charset) {
        try {
            return URLEncoder.encode(s, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static String decode(String s, String charset) {
        try {
            return URLDecoder.decode(s, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String joinParamsValue(Map<String, String> params){
		StringBuilder sb = new StringBuilder();
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsMap.put(entry.getKey(), entry.getValue());
        }
		for (Map.Entry<String, String> entry : paramsMap.entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return sb.toString();
	}
    
	/**
	 * 解析get形式参数串
	 * @param sParam
	 * @return Map<String, String>
	 */
	public static Map<String, String> unJoinParamsValue(String sParam) {
		if (StringUtils.isEmpty(sParam)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		String[] array = sParam.split("&");
		for (int i = 0, len = array.length; i < len; i++) {
			String[] pvArr = array[i].split("=");
			map.put(pvArr[0], pvArr[1]);
		}
		return map;
	}
    
    public static String joinValueOnly(Map<String, String> params){
    	StringBuilder sb = new StringBuilder();
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
		for (Map.Entry<String, String> entry : paramsMap.entrySet()){
            sb.append(entry.getValue());
		}
		return sb.toString();
    }
}
