/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xgama.sockethttpconvert.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the
 * application on the device. For the sake of simplicity and clarity of this
 * example, this code is included here and is executed on the device. If you
 * must verify the purchases on the phone, you should obfuscate this code to
 * make it harder for an attacker to replace the code with stubs that treat all
 * purchases as verified.
 */
public class GooglePaySecurity {
	private static final Logger LOG = LoggerFactory.getLogger(GooglePaySecurity.class);

	private static final String TAG = "IABUtil/Security";

	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	/**
	 * google pay 公钥
	 */
	public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgLsFEhCO5tSbnjKlNVgCneK0Sp6hIuwlJC"
			+ "p8rYoqHiXJ2fPOe4KP3zjkXQNwDMN5XvloH4L3TpD6wPGKefcbZL2f0zFVgePlws1Nd3URDLFjRyvr8cq9X5TcLBoGv+HWRdL1HABz6xdQpWX"
			+ "UIg/v9Zt/0rpGSmFsCSio6CiGUqxiCw3wHJN50Qw3UpUOXN3Yzxgt6wxIQpZ95aK28HdEkVItDPbXZ46FvYk+9BNVQ45r4foGYwcsnjjISp8hp"
			+ "Y6GEdEyihYeLd/YGqG07o+ePt7osqCZwHj5JHL/KF/nqJJQbc8Uw5+CfJMI/rbqbZ3D7dOCEpPEbmSb6F81TJVgYwIDAQAB";

	/**
	 * Verifies that the data was signed with the given signature, and returns
	 * the verified purchase. The data is in JSON format and signed
	 * with a private key. The data also contains the {@link PurchaseState}
	 * and product ID of the purchase.
	 * @param base64PublicKey the base64-encoded public key to use for verifying.
	 * @param signedData the signed JSON string (signed, not encrypted)
	 * @param signature the signature for the data, signed with the private key
	 */
	public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
		if (signedData == null) {
			LOG.error(TAG, "data is null");
			return false;
		}

		boolean verified = false;
		if (!StringUtils.isEmpty(signature)) {
			PublicKey key = GooglePaySecurity.generatePublicKey(base64PublicKey);
			verified = GooglePaySecurity.verify(key, signedData, signature);
			if (!verified) {
				LOG.warn(TAG, "signature does not match data.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Generates a PublicKey instance from a string containing the
	 * Base64-encoded public key.
	 *
	 * @param encodedPublicKey Base64-encoded public key
	 * @throws IllegalArgumentException if encodedPublicKey is invalid
	 */
	public static PublicKey generatePublicKey(String encodedPublicKey) {
		try {
			byte[] decodedKey = Base64.decode(encodedPublicKey);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
			return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			LOG.error(TAG, "Invalid key specification.");
			throw new IllegalArgumentException(e);
		} catch (Base64DecodingException e) {
			LOG.error(TAG, "Base64 decoding failed.");
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Verifies that the signature from the server matches the computed
	 * signature on the data.  Returns true if the data is correctly signed.
	 *
	 * @param publicKey public key associated with the developer account
	 * @param signedData signed data from server
	 * @param signature server signature
	 * @return true if the data and signature match
	 */
	public static boolean verify(PublicKey publicKey, String signedData, String signature) {
		Signature sig;
		try {
			sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(signedData.getBytes());
			if (!sig.verify(Base64.decode(signature))) {
				LOG.error(TAG, "Signature verification failed.");
				return false;
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			LOG.error(TAG, "NoSuchAlgorithmException.");
		} catch (InvalidKeyException e) {
			LOG.error(TAG, "Invalid key specification.");
		} catch (SignatureException e) {
			LOG.error(TAG, "Signature exception.");
		} catch (Base64DecodingException e) {
			LOG.error(TAG, "Base64 decoding failed.");
		}
		return false;
	}
}
