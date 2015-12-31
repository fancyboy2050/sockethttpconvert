package com.xgama.sockethttpconvert.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iTools 加密验证工具类
 *
 */
public class IToolsRSASignature {
	private static final Logger LOG = LoggerFactory.getLogger(IToolsRSASignature.class);

	public static final String RSA_PUBLIC = "";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	* 得到公钥
	*/
	public static PublicKey getPublicKey() throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = Base64.decodeBase64(RSA_PUBLIC);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		return pubKey;
	}

	/**
	* RSA 公钥解密
	*/
	public static String decrypt(String content) throws Exception {
		PublicKey pubKey = getPublicKey();

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pubKey);

		InputStream ins = new ByteArrayInputStream(Base64.decodeBase64(content));
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		//rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
		byte[] buf = new byte[128];
		int bufl;

		while ((bufl = ins.read(buf)) != -1) {
			byte[] block = null;

			if (buf.length == bufl) {
				block = buf;
			} else {
				block = new byte[bufl];
				for (int i = 0; i < bufl; i++) {
					block[i] = buf[i];
				}
			}

			writer.write(cipher.doFinal(block));
		}

		return new String(writer.toByteArray(), "utf-8");
	}

	/**
	* RSA 签名检查
	*/
	public static boolean verify(String content, String sign) throws Exception {
		PublicKey pubKey = getPublicKey();

		try {
			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes("utf-8"));

			boolean result = signature.verify(Base64.decodeBase64(sign));

			return result;
		} catch (Exception e) {
			LOG.error("iTools verify error", e);
		}

		return false;
	}

}
