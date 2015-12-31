package com.xgama.sockethttpconvert.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.xgama.sockethttpconvert.config.Constants;
import com.xgama.sockethttpconvert.pb.ChargeClass.AppleCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.AppleChargeResult;
import com.xgama.sockethttpconvert.util.HttpClientUtil;
import com.xgama.sockethttpconvert.util.JsonUtil;

public class AppleCheckService {
		
		private static final Logger logger = LoggerFactory.getLogger(AppleCheckService.class);
		
		public static AppleChargeResult getAppleReceipt(AppleCharge appleCharge) {
			Assert.notNull(appleCharge);
			Map<String, String> postParams = new HashMap<String, String>();
			postParams.put("receipt-data", appleCharge.getReceiptData());
    		ReceiptCheckResponse appleReceiptCheckResponse = null;
	        try {
	        	String responseString = HttpClientUtil.iOSPostHttps(Constants.appleValidateUrl, postParams);
	        	appleReceiptCheckResponse = JsonUtil.decodeJson(responseString, ReceiptCheckResponse.class);
	        	logger.info("responseMap:" + appleReceiptCheckResponse);
	        } catch(Exception e) {
	        	logger.error("connect to apple server network error!", e);
	        	return AppleChargeResult.newBuilder().setStatus(2).build();
	        }
	        if(appleReceiptCheckResponse != null) {
	        	int status = appleReceiptCheckResponse.getStatus();
	        	if(status == 0) {
	        		Map<String, String> receiptJson = appleReceiptCheckResponse.getReceipt();
	        		return AppleChargeResult.newBuilder()
	        				.setStatus(0)
	        				.setProductId(receiptJson.get("product_id"))
	        				.setTransactionId(receiptJson.get("transaction_id"))
	        				.setBid(receiptJson.get("bid"))
	        				.setQuantity(receiptJson.get("quantity"))
	        				.setItemId(receiptJson.get("item_id"))
	        				.build();
	        	
	        	} else if (status == 21007) {//沙盒测试
	        		ReceiptCheckResponse sandReceiptCheckResponse = null;
	        		 try {
	        			 String sandResponseString = HttpClientUtil.iOSPostHttps(Constants.appleSandValidateUrl, postParams);
	        			 logger.info("sandResponseString:"+sandResponseString);
	        			 sandReceiptCheckResponse = JsonUtil.decodeJson(sandResponseString, ReceiptCheckResponse.class);
	        		 } catch(Exception e) {
	    	        	 logger.error("sand connect to apple server network error!", e);
	    	        	 return AppleChargeResult.newBuilder().setStatus(2).build();
	        	     }
	        	      if(sandReceiptCheckResponse != null) {
	        	        	int sandStatus = sandReceiptCheckResponse.getStatus();
	        	        	if(sandStatus == 0) {
	        	        		Map<String, String> sendReceiptJson = sandReceiptCheckResponse.getReceipt();
	        	        		return AppleChargeResult.newBuilder()
	        	        				.setStatus(0)
	        	        				.setProductId(sendReceiptJson.get("product_id"))
	        	        				.setTransactionId(sendReceiptJson.get("transaction_id"))
	        	        				.setBid(sendReceiptJson.get("bid"))
	        	        				.setQuantity(sendReceiptJson.get("quantity"))
	        	        				.setItemId(sendReceiptJson.get("item_id"))
	        	        				.build();
	        	        	}
	        	      }
	        	}
	        }
			return AppleChargeResult.newBuilder().setStatus(2).build();
		}
}
