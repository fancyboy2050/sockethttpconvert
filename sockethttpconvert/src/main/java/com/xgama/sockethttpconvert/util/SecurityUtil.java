package com.xgama.sockethttpconvert.util;

import java.security.MessageDigest;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.config.Constants;

public class SecurityUtil {

	private static final String MAC_NAME = "HmacSHA1";
	private static final String ENCODING = "UTF-8";

	private final static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

	/**
	 * 唯一识别码
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(uuid.substring(0, 8));
		sb.append(uuid.substring(9, 13));
		sb.append(uuid.substring(14, 18));
		sb.append(uuid.substring(19, 23));
		sb.append(uuid.substring(24));
		return sb.toString();
	}
	
	/**
	 * 返回探测包唯一ID
	 * @return
	 */
	public static String getDetectID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.DETECT_PACKET_PREFIX);
		sb.append(uuid.substring(0, 8));
		sb.append(uuid.substring(9, 13));
		sb.append(uuid.substring(14, 18));
		sb.append(uuid.substring(19, 23));
		sb.append(uuid.substring(24));
		return sb.toString();
	}
	
	/**
	 * 返回心跳包唯一ID
	 * @return
	 */
	public static String getHeartbeatID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.HEARTBEAT_PACKET_PREFIX);
		sb.append(uuid.substring(0, 8));
		sb.append(uuid.substring(9, 13));
		sb.append(uuid.substring(14, 18));
		sb.append(uuid.substring(19, 23));
		sb.append(uuid.substring(24));
		return sb.toString();
	}

	/**
	 * MD5加密
	 * @param source
	 * @return
	 */
	public static String md5(String source) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes("UTF-8"));
			byte bytes[] = md.digest();
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			logger.error("md5 Exceptoion", e);
		}
		return sb.toString();
	}
	
	/**
	 * 安全哈希签名
	 * @param source
	 * @return
	 */
	public static String sha1(String source){
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = messageDigest.digest(source.getBytes("UTF-8"));
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			logger.error("sha1 Exceptoion", e);
		}
		return sb.toString();
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对encryptText 进行签名
	 * @param encryptText 被签名的字符串
	 * @param encryptKey 密钥
	 * @return 返回被加密后的字符串
	 * @throws Exception
	 */
	public static String HmacSHA1Encrypt(String encryptText, String encryptKey)
			throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		// 根据给定的字节数组构造一个密钥, 第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(MAC_NAME);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);
		byte[] text = encryptText.getBytes(ENCODING);
		// 完成 Mac 操作
		byte[] digest = mac.doFinal(text);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
	}

	/**
	 * 转换成Hex
	 * 
	 * @param bytesArray
	 */
	public static StringBuilder bytesToHexString(byte[] bytesArray) {
		if (bytesArray == null) {
			return null;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (byte b : bytesArray) {
			String hv = String.format("%02x", b);
			sBuilder.append(hv);
		}
		return sBuilder;
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对encryptText 进行签名
	 * @param encryptData 被签名的字符串
	 * @param encryptKey 密钥
	 * @return 返回被加密后的字符串
	 * @throws Exception
	 */
	public static String HmacSHA1Encrypt(byte[] encryptData, String encryptKey)
			throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		// 根据给定的字节数组构造一个密钥, 第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(MAC_NAME);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);
		// 完成 Mac 操作
		byte[] digest = mac.doFinal(encryptData);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
	}

	
	/**
	 * base64编码
	 * @param inStr
	 * @return
	 */
	public static String base64Encode(String inStr) {
		byte[] buf = encodeBase64(inStr);
		if(null == buf){
			return null;
		}
		
		try {
			return new String(buf,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * base64编码
	 * @param inStr 
	 * @return byte[]
	 */
	public static byte[] encodeBase64(String inStr) {
		if (null == inStr) {
			return null;
		}
		Base64 base64 = new Base64();
		try {
			return base64.encode(inStr.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * base64解码
	 * @param inStr
	 * @return
	 */
	public static String base64Decode(String inStr) {
		byte[] buf = decodeBase64(inStr);
		if(null == buf){
			return null;
		}
		
		try {
			return new String(buf, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
	
	/**
	 * base64解码
	 * @param inStr
	 * @return byte[]
	 */
	public static byte[] decodeBase64(String inStr) {
		if (null == inStr) {
			return null;
		}
		Base64 base64 = new Base64();
		try {
			return base64.decode(inStr.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(base64Encode("abc"));
		System.out.println(base64Encode(""));
		System.out.println(base64Encode(null));
	}
}
