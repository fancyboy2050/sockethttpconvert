package com.xgama.sockethttpconvert.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private Properties properties = new Properties();
	private String fileName;
	
	public static PropertiesUtil newInstance(String fileName){
		return new PropertiesUtil(fileName);
	}
	
	public PropertiesUtil(String fileName) {
		this.fileName = fileName;
		reload();
	}
	
	public static String readFile(String fileName) {
		try {
			InputStream is = PropertiesUtil.class.getResourceAsStream(fileName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer content = new StringBuffer();
			String lineTXT = null;
			while ((lineTXT = bufferedReader.readLine()) != null) {
				content.append(lineTXT.toString());
			}
			bufferedReader.close();
			is.close();
			return content.toString();
		} catch (Exception e) {
			logger.error("系统配置文件读取失败：" + e.getMessage());
		}
		return null;
	}
	
	public String getValue(String key) {
		String value = properties.getProperty(key);
		return value == null ? value : value.trim();
	}
	
	public String getValue(String key, String defaultValue) {
		String value = properties.getProperty(key, defaultValue);
		return value == null ? value : value.trim();
	}
	
	public void reload(){
		try {
			InputStream is = PropertiesUtil.class.getResourceAsStream(fileName);
			properties.load(is);
		} catch (Exception e) {
			logger.error("系统配置文件读取失败，文件："+fileName , e);
		}
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
