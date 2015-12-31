package com.xgama.sockethttpconvert.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

/**
 * 数字签名
 * @author devuser
 *
 */
public class RSAEncrypt {
	
	/** 
     * 数字签名   MD5withRSA
     * */  
    public static final String SIGN_ALGORITHM_MD5WITHRSA = "MD5withRSA";
    
    /**
     * 数字签名   SHA1WithRSA
     */
    public static final String SIGN_ALGORITHM_SHA1WITHRSA = "SHA1WithRSA";
    
    /**
     * 数字签名   SHA1WithDSA
     */
    public static final String SIGN_ALGORITHM_SHA1WITHDSA = "SHA1WithDSA";
    
    private static final String PUBLIC_KEY="RSAPublicKey";
    private static final String PRIVATE_KEY="RSAPrivateKey";
    
    /**
     * 生产密钥对
     * @return
     * @throws Exception
     */
    public static Map<String, String> genKeyPair() throws Exception{
        KeyPairGenerator keyPairGen= KeyPairGenerator.getInstance("RSA");  
        keyPairGen.initialize(1024, new SecureRandom());  
        KeyPair keyPair= keyPairGen.generateKeyPair();  
        RSAPrivateKey privateKey= (RSAPrivateKey) keyPair.getPrivate();  
        RSAPublicKey publicKey= (RSAPublicKey) keyPair.getPublic();
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put(PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
        return keyMap;
    }
    
    /**
     * 从字符串中加载公钥 
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadRSAPublicKey(String publicKeyStr) throws Exception {
		try {
//			BASE64Decoder base64Decoder= new BASE64Decoder();
//			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			byte[] buffer = Base64.decodeBase64(publicKeyStr);
	        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
	        X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
	        return (RSAPublicKey)keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法", e);
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法", e);
		}
    }
    
    /**
     * 从字符串中加载私钥
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadRSAPrivateKey(String privateKeyStr) throws Exception {
		try {
//			BASE64Decoder base64Decoder= new BASE64Decoder();
//			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			byte[] buffer = Base64.decodeBase64(privateKeyStr);
	        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
	        PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
	        return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法", e);
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法", e);
		}
    }
  
    /**
     * 私钥签名
     * @param content
     * @param privateKey
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String sign(String content, RSAPrivateKey privateKey, String signAlgorithm) throws Exception{
        //实例化Signature  
        Signature signature = Signature.getInstance(signAlgorithm);
        //初始化Signature  
        signature.initSign(privateKey);  
        //更新  
        signature.update(content.getBytes("UTF-8"));  
        return Base64.encodeBase64String(signature.sign()); 
    }
    
    /**
     * 公钥验证
     * @param content
     * @param publicKey
     * @param sign
     * @param signAlgorithm
     * @return
     * @throws Exception
     */
    public static boolean verify(String content, String sign, RSAPublicKey publicKey, String signAlgorithm) throws Exception {
        //实例化Signature  
        Signature signature = Signature.getInstance(signAlgorithm);
        //初始化Signature  
        signature.initVerify(publicKey);
        //更新  
        signature.update(content.getBytes("UTF-8")); 
        return signature.verify(Base64.decodeBase64(sign));
    }
    
    /**
     * main test
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
		Map<String, String> keyPaidMap = RSAEncrypt.genKeyPair();
		String publicKeyStr = keyPaidMap.get(PUBLIC_KEY);
		String privateKeyStr = keyPaidMap.get(PRIVATE_KEY);
		
		RSAPublicKey rsaPublicKey = RSAEncrypt.loadRSAPublicKey(publicKeyStr);
		RSAPrivateKey rsaPrivateKey = RSAEncrypt.loadRSAPrivateKey(privateKeyStr);
		
		String content = "encryp=encryp&testtcontent=testContent";
		String sign = RSAEncrypt.sign(content, rsaPrivateKey, SIGN_ALGORITHM_MD5WITHRSA);
		System.out.println("sign : "+sign);
		
		boolean boo = RSAEncrypt.verify(content, sign, rsaPublicKey, SIGN_ALGORITHM_MD5WITHRSA);
		System.out.println("boo : "+boo);
		
	}

}
