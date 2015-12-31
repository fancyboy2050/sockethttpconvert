package com.xgama.sockethttpconvert.util;

import java.util.HashMap;
import java.util.Map;

import com.xgama.sockethttpconvert.config.Constants;

public class BeanHelper {

	public static Map<String, Object> createBaiduResult(Integer appID, Integer code, String msg) {
		return createMapResult(appID, code, msg, _createBaiduSign(appID, code));
	}

	public static Map<String, Object> createJYResult(Integer appID, Integer code, String msg) {
		return createMapResult(appID, code, msg, _createJYSign(appID, code));
	}

	public static Map<String, Object> createMapResult(Integer appID, Integer code, String msg, String sign) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("AppID", appID);
		map.put("ResultCode", code);
		map.put("ResultMsg", msg);
		map.put("Sign", sign);
		map.put("Content", "");
		return map;
	}

	/**
	 * 生成百度多酷 签名
	 * @param appID
	 * @param resultCode
	 * @return
	 */
	private static String _createBaiduSign(Integer appID, Integer resultCode) {
		return SecurityUtil.md5(appID + resultCode + Constants.BAIDU_SECRETKEY);
	}

	/**
	 * 生成91平台 签名
	 * @param appID
	 * @param resultCode
	 * @return
	 */
	private static String _createJYSign(Integer appID, Integer resultCode) {
		return SecurityUtil.md5(appID + resultCode + Constants.JY_BAIDU_SECRETKEY);
	}

	public static void main(String[] args) {
		Map<String, Object> result = createBaiduResult(11, 1, "success");
		System.out.println(JsonUtil.objectToJson(result));
	}

}
