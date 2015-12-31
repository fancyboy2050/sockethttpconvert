package com.xgama.sockethttpconvert.controller;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.util.JsonUtil;

public class PPRsaCheck {
	
//    public static Map<String, String> ID_KEY_MAP = new HashMap<String, String>();
    private static final Logger logger = LoggerFactory.getLogger(WDJRsaCheck.class);

	/**
	 * 默认公钥(openssl)
	 */
    public static final String DEFAULT_PUBLIC_KEY= "";
    
	/** 
	 * 字节数据转字符串专用集合 
	 */  
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};  
    
//    static {
//    	ID_KEY_MAP.put("1263", "a6136fad8962b77c2941a78592bdc70a");
//    	ID_KEY_MAP.put("1717", "819951ade161ec1a65ba66371f209a6e");
//    	ID_KEY_MAP.put("1893", "8e7091880ab5454245bdae269271de54");
//    }
    
    public static boolean doCheck(String appId, String orderId, String billno, String account, String amount, String status, String sign){
    	try {
			byte[] dcDataStr = Base64.decodeBase64(sign);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(DEFAULT_PUBLIC_KEY);
            RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			byte[] plainData = decrypt(pubKey, dcDataStr);
			String content = new String(plainData);
			logger.info("plainData content : "+content);
			Map<String, Object> parseMap = JsonUtil.parse(content);
			if(parseMap != null){
				String _orderId = (String)parseMap.get("order_id");
				String _billno = (String)parseMap.get("billno");
				String _account = (String)parseMap.get("account");
				String _amount = (String)parseMap.get("amount");
				String _status = String.valueOf(parseMap.get("status"));
				String _appId = (String)parseMap.get("app_id");
				
				if(StringUtils.equalsIgnoreCase(_orderId, orderId) && 
					StringUtils.equalsIgnoreCase(_billno, billno) &&
					StringUtils.equalsIgnoreCase(_account, account) &&
					StringUtils.equalsIgnoreCase(_amount, amount) &&
					StringUtils.equalsIgnoreCase(_status, status) &&
					StringUtils.equalsIgnoreCase(_appId, appId)){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("PPRsaCheck failed!", e);
		}
    	return false;
    }
    
    /** 
     * 字节数据转十六进制字符串 
     * @param data 输入数据 
     * @return 十六进制内容 
     */  
    public static String byteArrayToString(byte[] data){  
        StringBuilder stringBuilder= new StringBuilder();  
        for (int i=0; i<data.length; i++){  
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移   
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);  
            //取出字节的低四位 作为索引得到相应的十六进制标识符   
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i<data.length-1){  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }  
    
    /** 
     * 公钥解密过程 
     * @param publicKey 公钥 
     * @param cipherData 密文数据 
     * @return 明文 
     * @throws Exception 解密过程中的异常信息 
     */  
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception{  
        if (publicKey== null){  
            throw new Exception("解密公钥为空, 请设置");  
        }  
        Cipher cipher= null;  
        try {  
        	//使用默认RSA
            cipher= Cipher.getInstance("RSA");
            //cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            byte[] output= cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("解密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {
        	logger.error("BadPaddingException", e);
            throw new Exception("密文数据已损坏");  
        }         
    }  
    
    public static void main(String[] args) {
    	PPRsaCheck rsaEncrypt= new PPRsaCheck();  
        //rsaEncrypt.genKeyPair();   

        //加载公钥   
        try {  
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(DEFAULT_PUBLIC_KEY);
            RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            System.out.println("加载公钥成功");  
            
            
            //文档测试数据 
//            String testDataStr = "d7C8ph77SaqWsSk+T2KpHXKuhplBdZOosP9a7XnQAziC4A0aO8yQG0RdyMz/Ya2G77V0ufOq0QyHdv25dONOwuCGrq+fUMrn+l8D5fdIsGI0mIvbVVum2A3arxuG0toMhqIlxKD88CIs2hyEMit6exRRMnFgHFjcDh1KVajHC7DecfmhRunQctPFX9Z2JxIpLMGYsqb6qKqSaO0sdfamnFpl2ozwSKBTijAECj7Xx354SiLJTqbsERWx1b5dLR/iuZpODSY9IY3RHdEJ60e+ggk1q+n5MHEdL+M9tnbqw7kYsiLYSVvFJ7YTyqSR4qGC/GyGUAJdNiiNjB8MOGsUBQ==";
            
            String testDataStr = "fwhsOKIjZxPHtVP8KExo+H7VIYOr6GgdYgITeAA1t/2yRprocrY5o+zjOdnEW6aT+RoWsGSPKEElINMal2pwu/8kGVkdD8Vv6e1akfXPh5kVbbrG/VfKGStT7e4FxbODa3eXvfS+KR4bWVbnAHA6lGNr7iKdkOEVcTKqzDdGGjCvCCxhufuqmgVBne9yGC0woDvwEsoUc+qDTyxE69/9fykzQ74oHZROT3/EFL2HRSIluIZoD6Qbz4FP5Ail45IZ3H0WnVNfIwBGBwQ3hB/AbzAY7Q2hnpRzUZ0a7rR/k3UOFFbMW+FKGrnr147RtIOrf29owGLkjkzUxhlPq8j9pg==";
      
            try {
                byte[] dcDataStr = Base64.decodeBase64(testDataStr);
                byte[] plainData = rsaEncrypt.decrypt(pubKey, dcDataStr);  
                System.out.println("文档测试数据明文长度:" + plainData.length);  
                System.out.println(PPRsaCheck.byteArrayToString(plainData));  
                System.out.println(new String(plainData));
                
            } catch (Exception e) {  
                System.err.println(e.getMessage());  
            } 
            
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载公钥失败");  
        }  
	}

}
