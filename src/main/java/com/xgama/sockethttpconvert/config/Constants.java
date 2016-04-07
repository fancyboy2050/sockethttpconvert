package com.xgama.sockethttpconvert.config;

import org.apache.commons.lang.math.NumberUtils;

import com.xgama.sockethttpconvert.util.PropertiesUtil;

public class Constants {

	public static PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("/platform.properties");
	/**
	 * LH用户验证新api
	 */
	public static String laohuNewUserValidateUrl = propertiesUtil.getValue("laohu.new.validate.url");
	/**
	 * 91用户验证api
	 */
	public static String user91ValidateUrl = propertiesUtil.getValue("91.validate.url");
	/**
	 * 当乐用户验证api
	 */
	public static String dlValidateUrl = propertiesUtil.getValue("dl.validate.url");
	/**
	 * 360用户验证api
	 */
	public static String qhValidateUrl = propertiesUtil.getValue("qh.validate.url");
	/**
	 * 360获取用户api
	 */
	public static String qhGetUserUrl = propertiesUtil.getValue("qh.getuser.url");
	/**
	 * UC验证用户API
	 */
	public static String ucValidateUrl = propertiesUtil.getValue("uc.validate.url");
	/**
	 * 小米验证用户api
	 */
	public static String xmValidateUrl = propertiesUtil.getValue("xm.validate.url");
	/**
	 * 豌豆荚验证用户api
	 */
	public static String wdjValidateUrl = propertiesUtil.getValue("wdj.validate.url");
	/**
	 * 同步推验证 用户api
	 */
	public static String tbtValidateUrl = propertiesUtil.getValue("tbt.validate.url");
	/**
	 * 百度多酷验证用户api
	 */
	public static String bddkValidateUrl = propertiesUtil.getValue("bddk.validate.url");
	/**
	 * 安智验证用户api
	 */
	public static String azValidateUrl = propertiesUtil.getValue("az.validate.url");
	/**
	 * 快用验证用户api
	 */
	public static String kyValidateUrl = propertiesUtil.getValue("ky.validate.url");
	/**
	 * iTools 验证用户url
	 */
	public static String iToolsValidateUrl = propertiesUtil.getValue("iTools.validate.url");
	
	/**
	 * onesdk 验证用户url
	 */
	public static String onesdkValidateUrl = propertiesUtil.getValue("onesdk.validate.url");
	
	public static String CHARGE_IP = propertiesUtil.getValue("charge.ip");
	public static String CHARGE_PORT = propertiesUtil.getValue("charge.port");
	public static String CHARGE_CLIENT_NUMS = propertiesUtil.getValue("charge.clientNums");

	/**
	 * 苹果验证地址和苹果沙盒验证地址
	 */
	public static String appleValidateUrl = propertiesUtil.getValue("apple.validate.url");
	public static String appleSandValidateUrl = propertiesUtil.getValue("sand.apple.validate.url");

	/**
	 * google pay public key
	 */
	public static String GOOGLE_PAY_PUBLIC_KEY = propertiesUtil.getValue("google.pay.publickey");

	/**
	 * 百度签名secretKey
	 */
	public static String BAIDU_SECRETKEY = propertiesUtil.getValue("baidu.secretKey");
	
	/**
	 * 91签名secretKey
	 */
	public static String JY_BAIDU_SECRETKEY = propertiesUtil.getValue("91.baidu.secretKey");
	
	/**
	 * 是否打印 渠道用户验证结果日志
	 */
	public static String LOG_CHANNEL_VALIDATE = propertiesUtil.getValue("log.channel.validate");
	
	/**
	 * http Client线程池大小
	 */
	public static final int HTTPCLIENT_THREAD_MAXTOTAL = NumberUtils.toInt(propertiesUtil.getValue("httpclient.thread.maxTotal"), 200);

	/**
	 * http Client Per Route 线程数量最大值
	 */
	public static final int HTTPCLIENT_THREAD_MAXPERROUTE = NumberUtils.toInt(propertiesUtil.getValue("httpclient.thread.maxPerRoute"), 20);
	
	/**
	 * 心跳包appOrder前缀
	 */
	public static final String HEARTBEAT_PACKET_PREFIX = "heartbeat_";

	/**
	 * 探测包appOrder前缀
	 */
	public static final String DETECT_PACKET_PREFIX = "detect_";
	
	/**
	 * 成功
	 */
	public static final String SUCCESS = "success";
	
	/**
	 * 失败
	 */
	public static final String FAIL = "fail";
}
