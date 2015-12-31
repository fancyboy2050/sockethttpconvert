package com.xgama.sockethttpconvert.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.util.PropertiesUtil;

public class ChargeConstants {
	
	private final static Logger logger = LoggerFactory.getLogger(ChargeConstants.class);
	public static PropertiesUtil chargeProperties = PropertiesUtil.newInstance("/charge.properties");
	
	public static String getIp(String server){
		String key = new StringBuilder("SERVER_").append(server).append("_IP").toString();
		String value = chargeProperties.getValue(key);
		if(StringUtils.isEmpty(value)){
			chargeProperties.reload();
			value = chargeProperties.getValue(key);
		}
		logger.info(String.format("Get ip from properties, key : %s, value : %s", new Object[]{key, value}));
		return value;
	}
	
	public static String getPort(String server){
		String key = new StringBuilder("SERVER_").append(server).append("_PORT").toString();
		String value = chargeProperties.getValue(key);
		if(StringUtils.isEmpty(value)){
			chargeProperties.reload();
			value = chargeProperties.getValue(key);
		}
		logger.info(String.format("Get port from properties, key : %s, value : %s", new Object[]{key, value}));
		return value;
	}

}
