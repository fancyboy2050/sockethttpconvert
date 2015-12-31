package com.xgama.sockethttpconvert.controller;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WDJRsaCheck {
    // 豌豆荚公钥
    public final static String WandouPublicKey = "";
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final Logger logger = LoggerFactory.getLogger(WDJRsaCheck.class);
    
	public static boolean doCheck(String content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = WDJBase64.decode(WandouPublicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));
            boolean bverify = signature.verify(WDJBase64.decode(sign));
            return bverify;
        } catch (Exception e) {
            logger.error("WDJRsaCheck doCheck failed!", e);
        }
        return false;
    }

}
