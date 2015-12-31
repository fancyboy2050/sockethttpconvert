package com.xgama.sockethttpconvert.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	/**
	 * 解析json string为map对象
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		Map<String, Object> retMap = null;
		try {
			retMap = mapper.readValue(str, Map.class);
		} catch (Exception e) {
			logger.error("String to map failed!", e);
		}
		return retMap;
	}
	
	/**
	 * 解析json string为list对象
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> parseArray(String str){
		List<Map<String, Object>> array = null;
		try {
			array = mapper.readValue(str, List.class);
		} catch (Exception e) {
			logger.error("String to map failed!", e);
		}
		return array;
	}
	
	/**
	 * object对象转换给json string
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj){
		String retStr = "";
		if(obj == null){
			return retStr;
		}
		try {
			retStr = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("Object to json string failed!", e);
		}
		return retStr;
	}
	

	
	/**
	 * 使用对象进行json反序列化。
	 * @param <T>
	 * @param json json串
	 * @param pojoClass 类类型
	 * @return
	 * @throws Exception
	 */
	public static <T> T decodeJson(String json, Class<T> pojoClass) {
		try {
			return mapper.readValue(json, pojoClass);
		} catch (Exception e) {
			logger.error("DecodeJson to object failed! pojcClass : "+pojoClass, e);
			return null;
		}
	}
	
	public static void main(String[] args) {
		String data = "{'time':'20140117110859996','msg':'eyd1aWQnOicyMDE0MDExNzExMDg1N2EzTzI4dmR5c28nfQ==','sc':'1','st':'成功(sid有效)'}";
		
		try {
//			AzUserValidateResponse az = decodeJson(data, AzUserValidateResponse.class);
//
//			String jsonMsg = SecurityUtil.base64Decode(az.getMsg());
//			Map<String, Object> resMap = JsonUtil.parse(jsonMsg);
//			System.out.println(resMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
