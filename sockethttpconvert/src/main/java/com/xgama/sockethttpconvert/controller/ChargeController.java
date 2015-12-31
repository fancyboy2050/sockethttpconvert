package com.xgama.sockethttpconvert.controller;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Decoder;

import com.xgama.sockethttpconvert.client.ChargeClient;
import com.xgama.sockethttpconvert.client.ChargeClientFactory;
import com.xgama.sockethttpconvert.config.ChargeConstants;
import com.xgama.sockethttpconvert.controller.UCChargeModel.Data;
import com.xgama.sockethttpconvert.pb.ChargeClass.AZCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.BDDKCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.DLCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.IToolsCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.JYCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.KYCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.LaoHuCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.OnesdkCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.PPCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.QHCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.Result;
import com.xgama.sockethttpconvert.pb.ChargeClass.TBTCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.TMCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.UCCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.WDJCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.XMCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.YYCharge;
import com.xgama.sockethttpconvert.util.BeanHelper;
import com.xgama.sockethttpconvert.util.IToolsRSASignature;
import com.xgama.sockethttpconvert.util.JsonUtil;
import com.xgama.sockethttpconvert.util.ParamsUtil;
import com.xgama.sockethttpconvert.util.RSAEncrypt;
import com.xgama.sockethttpconvert.util.SecurityUtil;

@Controller
@RequestMapping("/charge")
public class ChargeController {
	
	private final static Logger logger = LoggerFactory.getLogger(ChargeController.class);
	private final static int TIME_OUT = 10000;
	private final static String publicKeyStr = "";
	private static RSAPublicKey rsaPublicKey;
	
	static {
		try {
			rsaPublicKey = RSAEncrypt.loadRSAPublicKey(publicKeyStr);
		} catch (Exception e) {
			logger.error("Load RSA public key failed!", e);
		}
	}
	
	@RequestMapping("/notify/tm")
	public @ResponseBody Map<String, Object> tmCharge(@RequestParam(required=true, value="orderNo")Long orderNo, 
			@RequestParam(required=true, value="roleId")String roleId,
			@RequestParam(required=true, value="goodId")String goodId,
			@RequestParam(required=true, value="goodNum")Long goodNum,
			@RequestParam(required=true, value="serverId")String serverId,
			@RequestParam(required=true, value="money")Long money,
			@RequestParam(required=true, value="sign")String sign){
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("orderNo", String.valueOf(orderNo));
		treeMap.put("roleId", roleId);
		treeMap.put("goodId", goodId);
		treeMap.put("goodNum", String.valueOf(goodNum));
		treeMap.put("serverId", serverId);
		treeMap.put("money", String.valueOf(money));
		logger.info("TM charge, orderNo : "+orderNo+", params : "+treeMap);
		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : treeMap.entrySet()){
			sb.append(entry.getValue());
		}
		boolean verify = false;
		try {
			logger.info("TM charge, orderNo : "+orderNo+",verify content : "+sb.toString()+", sign : "+sign);
			verify = RSAEncrypt.verify(sb.toString(), sign, rsaPublicKey, RSAEncrypt.SIGN_ALGORITHM_MD5WITHRSA);
			logger.info("TM charge, orderNo : "+orderNo+", verify result : "+verify);
		} catch (Exception e) {
			logger.error("RSAEncrypt.verify exception!", e);
			retMap.put("code", 4);
			return retMap;
		}

		if(verify){
			try {
				String appOrder = SecurityUtil.getUUID();
				TMCharge.Builder builder = TMCharge.newBuilder().setAppOrder(appOrder).setOrderNo(String.valueOf(orderNo))
						.setRoleId(roleId).setGoodNum(String.valueOf(goodNum)).setGoodId(goodId).setServerId(serverId).setMoney(String.valueOf(money));
				TMCharge charge = builder.build();
				logger.info("TM charge, orderNo : "+orderNo+", charge content : "+charge); 
				ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(serverId)), Integer.parseInt(ChargeConstants.getPort(parseServer(serverId))));
				Result result = client.invokeSyn(appOrder, charge, TIME_OUT);
				logger.info("TM charge, orderNo : "+orderNo+", appOrder = "+ appOrder + ", result = "+result.getResult());
				if(result.getResult() == 1){
					retMap.put("code", 0);
					retMap.put("msg", "OK!");
					retMap.put("appOrder", result.getGameOrder());
				} else {
					if(result.getResult() != 0){
						retMap.put("code", result.getResult());
					} else {
						retMap.put("code", 6);
					}
					retMap.put("msg", "failed!");
				}
			} catch (Exception e) {
				logger.error("Invoke exception, orderNo : "+orderNo, e);
				retMap.put("code", 99);
			}
		} else {
			retMap.put("code", 4);
		}
		return retMap;
	}
	
	@RequestMapping("/notify/laohu")
	public @ResponseBody Map<String, Object> laohuNotify(@RequestParam(required=true, value="userId")Long userId, 
			@RequestParam(required=true, value="appId")Integer appId, 
			@RequestParam(required=true, value="pOrder")Long pOrder, 
			@RequestParam(required=true, value="appOrder")String appOrder,
			@RequestParam(required=true, value="t")Long t, 
			@RequestParam(required=true, value="amount")Integer amount,
			@RequestParam(required=true, value="payStatus")String payStatus,
			@RequestParam(required=true, value="sign")String sign,
			@RequestParam(required=false, value="serverId")Integer serverId,
			@RequestParam(required=false, value="ext")String ext){
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("code", 1);
		
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(appOrder)){
			retMap.put("code", 0);
			retMap.put("msg", "OK!");
			logger.info("Laohu notify result get from cache , appOrder : "+appOrder);
			return retMap;
		}
		
		LaoHuCharge.Builder builder = LaoHuCharge.newBuilder().setAppOrder(appOrder).setAmount(amount)
							  .setUserId(userId).setAppId(appId).setPOrder(pOrder)
							  .setT(t).setPayStatus(payStatus).setSign(sign);
		if(serverId != null && serverId > 0){
			builder = builder.setServerId(serverId);
		}
		
		if(!StringUtils.isEmpty(ext)){
			builder = builder.setExt(ext);
		}
		
		LaoHuCharge charge = builder.build();
		
		logger.info("laohu charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(appOrder)), Integer.parseInt(ChargeConstants.getPort(parseServer(appOrder))));
			Result result = client.invokeSyn(appOrder, charge, TIME_OUT);
			logger.info("laohu notify charge, appOrder = "+ appOrder + ", result = "+result.getResult());
			if(result.getResult() == 1){
				retMap.put("code", 0);
				retMap.put("msg", "OK!");
				return retMap;
			}
		} catch (Exception e) {
			logger.error("laohu notify failed!", e);
		}
		retMap.put("msg", "失败！");
		return retMap;
	}
	
	public static String parseServer(String appOrder){
		if(StringUtils.isEmpty(appOrder) || appOrder.length() <= 3){
			return "";
		}
		return appOrder.substring(appOrder.length()-3);
	}
	
	@RequestMapping("/notify/91")
	public @ResponseBody Map<String, Object> jyNotify(@RequestParam(required=true, value="AppID") Integer appID,
			@RequestParam(required=true, value="OrderSerial")String orderSerial,
			@RequestParam(required=true, value="CooperatorOrderSerial")String cooperatorOrderSerial,
			@RequestParam(required=true, value="Sign")String sign,
			@RequestParam(required = true, value = "Content") String content) {

		// 先检测缓存是否已经有回复结果
		if (DataCache.checkCache(cooperatorOrderSerial)) {
			logger.info("91 notify result get from cache , appOrder : " + cooperatorOrderSerial);
			return BeanHelper.createJYResult(appID, 1, "success");
		}
		
		String decodecontent = new String(Base64.decodeBase64(content));
		Map<String, Object> contentMap = JsonUtil.parse(decodecontent);
		logger.info("91 notify contentMap : "+contentMap.toString());
		String OrderMoney = String.valueOf((int) (Double.parseDouble(String.valueOf(contentMap.get("OrderMoney"))) * 100));
		String OrderStatus = String.valueOf(contentMap.get("OrderStatus"));
		String StartDateTime = String.valueOf(contentMap.get("StartDateTime"));

		JYCharge.Builder builder  = JYCharge.newBuilder().setAppOrder(cooperatorOrderSerial).setAppID(appID)
				.setOrderSerial(orderSerial).setCooperatorOrderSerial(cooperatorOrderSerial).setSign(sign)
				.setContent(content);
		
		if(!StringUtils.isBlank(OrderMoney)){
			builder.setOrderMoney(OrderMoney);
		}
		
		if(!StringUtils.isBlank(OrderStatus)){
			builder.setOrderStatus(OrderStatus);
		}

		if(!StringUtils.isBlank(StartDateTime)){
			builder.setStartDateTime(StartDateTime);
		}
		
		JYCharge charge = builder.build();
		logger.info("91 notify charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(parseServer(cooperatorOrderSerial)),
					Integer.parseInt(ChargeConstants.getPort(parseServer(cooperatorOrderSerial))));
			Result result = client.invokeSyn(cooperatorOrderSerial, charge, TIME_OUT);
			logger.info("91 notify charge, appOrder = " + cooperatorOrderSerial + ", result = " + result.getResult());
			
			if (result.getResult() == 1) {
				return BeanHelper.createJYResult(appID, 1, "success");
			}
		} catch (Exception e) {
			logger.error("91 notify failed!", e);
		}
		return BeanHelper.createJYResult(appID, 0, "error");
	}
	
	@RequestMapping("/notify/dl")
	public @ResponseBody String dlNotify(@RequestParam(required=true, value="result")String result, 
			@RequestParam(required=true, value="money")String money, 
			@RequestParam(required=true, value="order")String order, 
			@RequestParam(required=true, value="mid")Long mid,
			@RequestParam(required=true, value="time")String time, 
			@RequestParam(required=true, value="signature")String signature, 
			@RequestParam(required=true, value="ext")String ext){
		
		String[] exts = ext.split("\\|");
		if(exts == null || exts.length < 2){
			return "fail";
		}
		String appOrder = exts[0];
		logger.info("dl appOrder : "+appOrder);
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(appOrder)){
			logger.info("dl notify result get from cache , appOrder : "+appOrder);
			return "success";
		}
		
		DLCharge charge = DLCharge.newBuilder().setAppOrder(appOrder).setExt(ext)
				.setMid(mid).setMoney(money).setOrder(order)
				.setResult(result).setTime(time)
				.setSignature(signature).build();
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(appOrder)), Integer.parseInt(ChargeConstants.getPort(parseServer(appOrder))));
			Result res = client.invokeSyn(appOrder, charge, TIME_OUT);
			logger.info("dl notify charge, appOrder = "+ appOrder + ", result = "+res.getResult());
			if(res.getResult() == 1){
				return "success";
			}
		} catch (Exception e) {
			logger.error("dl notify failed!", e);
		}
		return "fail";
	}
	
	@RequestMapping(value="/notify/pp", method=RequestMethod.POST)
	public @ResponseBody String ppNotify(@RequestParam(required=true, value="order_id")String order_id, 
			@RequestParam(required=true, value="billno")String billno, 
			@RequestParam(required=true, value="account")String account, 
			@RequestParam(required=true, value="amount")String amount,
			@RequestParam(required=true, value="status")String status, 
			@RequestParam(required=true, value="app_id")String app_id, 
			@RequestParam(required=true, value="sign")String sign){
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(billno)){
			logger.info("PP notify result get from cache , appOrder : "+billno);
			return "success";
		}
		
		if(!PPRsaCheck.doCheck(app_id, order_id, billno, account, amount, status, sign)){
			logger.info("PP sign check failed!");
			return "fail";
		}
		
		PPCharge charge = PPCharge.newBuilder().setAppOrder(billno).setAccount(account).setAmount(amount).setAppId(app_id)
				.setBillno(billno).setOrderId(order_id).setSign(sign).setStatus(status).build();
		logger.info("pp notify charge, charge = "+ charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(billno)), Integer.parseInt(ChargeConstants.getPort(parseServer(billno))));
			Result res = client.invokeSyn(billno, charge, TIME_OUT);
			logger.info("pp notify charge, appOrder = "+ billno + ", result = "+res.getResult());
			if(res.getResult() == 1){
				return "success";
			}
		} catch (Exception e) {
			logger.error("pp notify failed!", e);
		}
		return "fail";
	}
	
	@RequestMapping(value="/notify/uc", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String ucNotify(@RequestBody UCChargeModel ucCharge){
		try {
			if(ucCharge != null){
				Data data = ucCharge.getData();
				
				// 先检测缓存是否已经有回复结果
				if(DataCache.checkCache(data.getCallbackInfo())){
					logger.info("uc notify result get from cache , appOrder : "+data.getCallbackInfo());
					return "SUCCESS";
				}
				
				UCCharge charge = UCCharge.newBuilder().setAppOrder(data.getCallbackInfo()).setOrderId(data.getOrderId()).setAmount(data.getAmount())
						.setGameId(String.valueOf(data.getGameId())).setServerId(String.valueOf(data.getServerId()))
						.setUcid(String.valueOf(data.getUcid())).setPayWay(String.valueOf(data.getPayWay()))
						.setCallbackInfo(data.getCallbackInfo()).setOrderStatus(data.getOrderStatus())
						.setFailedDesc(data.getFailedDesc()).setSign(ucCharge.getSign()).build();
				logger.info("UC notify charge, charge = "+ charge);
				ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(data.getCallbackInfo())), Integer.parseInt(ChargeConstants.getPort(parseServer(data.getCallbackInfo()))));
				Result res = client.invokeSyn(data.getCallbackInfo(), charge, TIME_OUT);
				logger.info("uc notify charge, appOrder = "+ data.getCallbackInfo() + ", result = "+res.getResult());
				if(res.getResult() == 1){
					return "SUCCESS";
				}
			}
		} catch (Exception e) {
			logger.error("uc notify failed!", e);
		}
		return "FAILURE";
	}
	
	@RequestMapping("/notify/xm")
	public @ResponseBody Map<String, Object> xmNotify(@RequestParam(required=true, value="appId")String appId,
			@RequestParam(required=false, value="cpUserInfo")String cpUserInfo,
			@RequestParam(required=true, value="cpOrderId")String cpOrderId, 
			@RequestParam(required=true, value="uid")String uid, 
			@RequestParam(required=true, value="orderId")String orderId,
			@RequestParam(required=true, value="orderStatus")String orderStatus, 
			@RequestParam(required=true, value="payFee")String payFee, 
			@RequestParam(required=true, value="productCode")String productCode,
			@RequestParam(required=true, value="productName")String productName, 
			@RequestParam(required=true, value="productCount")String productCount, 
			@RequestParam(required=true, value="payTime")String payTime,
			@RequestParam(required=true, value="signature")String signature){
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("errcode", 3515);
		
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(cpOrderId)){
			retMap.put("errcode", 200);
			logger.info("xm notify result get from cache , appOrder : "+cpOrderId);
			return retMap;
		}
		
		XMCharge.Builder builder = XMCharge.newBuilder();
		builder.setAppOrder(cpOrderId).setCpOrderId(cpOrderId).setAppId(appId).setUid(uid)
				.setOrderId(orderId).setOrderStatus(orderStatus).setPayFee(payFee)
				.setProductCode(productCode).setProductName(productName).setProductCount(productCount)
				.setPayTime(payTime).setSignature(signature);
		if(!StringUtils.isEmpty(cpUserInfo)){
			builder.setCpUserInfo(cpUserInfo);
		}
		XMCharge charge = builder.build();
		logger.info("xm charge = "+ charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(cpOrderId)), Integer.parseInt(ChargeConstants.getPort(parseServer(cpOrderId))));
			Result res = client.invokeSyn(cpOrderId, charge, TIME_OUT);
			logger.info("xm notify charge, appOrder = "+ cpOrderId + ", result = "+res.getResult());
			if(res.getResult() == 1){
				retMap.put("errcode", 200);
				return retMap;
			}
		} catch (Exception e) {
			logger.error("xm notify failed!", e);
		}
		
		return retMap;
	}
	
	@RequestMapping("/notify/qh")
	public @ResponseBody String qhNotify(@RequestParam(required=true, value="app_key")String app_key, 
			@RequestParam(required=true, value="product_id")String product_id, 
			@RequestParam(required=true, value="amount")String amount, 
			@RequestParam(required=true, value="app_uid")String app_uid,
			@RequestParam(required=true, value="order_id")String order_id, 
			@RequestParam(required=true, value="gateway_flag")String gateway_flag,
			@RequestParam(required=true, value="user_id")String user_id, 
			@RequestParam(required=true, value="sign_type")String sign_type,
			@RequestParam(required=true, value="app_order_id")String app_order_id, 
			@RequestParam(required=true, value="sign_return")String sign_return, 
			@RequestParam(required=true, value="sign")String sign){
		
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(app_order_id)){
			logger.info("qh notify result get from cache , appOrder : "+app_order_id);
			return "ok";
		}
		
		QHCharge charge = QHCharge.newBuilder().setAppOrder(app_order_id).setAppOrderId(app_order_id).setAppKey(app_key).setProductId(product_id)
				.setAmount(amount).setAppUid(app_uid).setOrderId(order_id).setGatewayFlag(gateway_flag).setUserId(user_id)
				.setSignType(sign_type).setSignReturn(sign_return).setSign(sign).build();
		logger.info("charge : "+charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(app_order_id)), Integer.parseInt(ChargeConstants.getPort(parseServer(app_order_id))));
			Result res = client.invokeSyn(app_order_id, charge, TIME_OUT);
			logger.info("qh notify charge, appOrder = "+ app_order_id + ", result = "+res.getResult());
			if(res.getResult() == 1){
				return "ok";
			}
		} catch (Exception e) {
			logger.error("qh notify failed!", e);
		}
		
		return "fail";
	}
	
	@RequestMapping("/notify/wdj")
	public @ResponseBody String wdjNotify(@RequestParam(required=true, value="content")String content, 
			@RequestParam(required=true, value="signType")String signType, 
			@RequestParam(required=true, value="sign")String sign){
		try {
			WDJContent wdjContent = JsonUtil.decodeJson(content, WDJContent.class);
			if(wdjContent == null){
				logger.error("Request content not right , content is : "+content);
				return "fail";
			}
			boolean checkBoo = WDJRsaCheck.doCheck(content, sign);
			if(!checkBoo){
				logger.error("WDJRsaCheck.doCheck failed!");
				return "fail";
			}
			String appOrder = wdjContent.getOut_trade_no();
			// 先检测缓存是否已经有回复结果
			if(DataCache.checkCache(appOrder)){
				logger.info("WDJ notify result get from cache , appOrder : "+appOrder);
				return "success";
			}
			
			WDJCharge.Builder builder = WDJCharge.newBuilder();
			builder.setAppOrder(wdjContent.getOut_trade_no()).setSignType(signType)
				.setSign(sign).setTimeStamp(wdjContent.getTimeStamp()).setOrderId(wdjContent.getOrderId())
				.setMoney(wdjContent.getMoney()).setChargeType(wdjContent.getChargeType()).setAppKeyId(wdjContent.getAppKeyId())
				.setBuyerId(wdjContent.getBuyerId()).setOutTradeNo(wdjContent.getOut_trade_no());
			if(!StringUtils.isBlank(wdjContent.getCardNo())){
				builder.setCardNo(wdjContent.getCardNo());
			}
			WDJCharge wdjCharge = builder.build();
			logger.info("wdjNotify charge : "+wdjCharge);
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(appOrder)), Integer.parseInt(ChargeConstants.getPort(parseServer(appOrder))));
			Result res = client.invokeSyn(appOrder, wdjCharge, TIME_OUT);
			logger.info("wdj notify charge, appOrder = "+ appOrder + ", result = "+res.getResult());
			if(res.getResult() == 1){
				return "success";
			}
		} catch (Exception e) {
			logger.error("wdj notify failed!", e);
		}
		return "fail";
	}
	
	@RequestMapping("/notify/tbt")
	public @ResponseBody Map<String, Object> tbtNotify(@RequestParam(required=true, value="source")String source,
			@RequestParam(required=true, value="trade_no")String trade_no,
			@RequestParam(required=true, value="amount")String amount,
			@RequestParam(required=true, value="partner")String partner,
			@RequestParam(required=true, value="paydes")String paydes,
			@RequestParam(required=true, value="debug")String debug,
			@RequestParam(required=true, value="sign")String sign,
			@RequestParam(required=false,value="tborder") String tborder){
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(trade_no)){
			logger.info("WDJ notify result get from cache , appOrder : "+trade_no);
			resMap.put("status", "success");
			return resMap;
		}
		
		TBTCharge.Builder builder = TBTCharge.newBuilder();
		builder.setAppOrder(trade_no).setSource(source).setTradeNo(trade_no)
			.setAmount(amount).setPartner(partner).setPaydes(paydes)
			.setDebug(debug).setSign(sign);
		//可选参数torder
		if (StringUtils.isNotEmpty(tborder)) {
			builder.setTorder(tborder);
		}
		TBTCharge charge = builder.build();
		logger.info("tbtNotify charge : "+charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(trade_no)), Integer.parseInt(ChargeConstants.getPort(parseServer(trade_no))));
			Result res = client.invokeSyn(trade_no, charge, TIME_OUT);
			logger.info("tbt notify charge, appOrder = "+ trade_no + ", result = "+res.getResult());
			if(res.getResult() == 1){
				resMap.put("status", "success");
				return resMap;
			}
		} catch (Exception e) {
			logger.error("tbt notify failed!", e);
		}
		
		resMap.put("status", "fail");
		return resMap;
	}
	
	@RequestMapping("/notify/bddk")
	public @ResponseBody Map<String, Object> bddkNotify(@RequestParam(required=true, value="AppID") Integer appID,
			@RequestParam(required=true, value="OrderSerial")String orderSerial,
			@RequestParam(required=true, value="CooperatorOrderSerial")String cooperatorOrderSerial,
			@RequestParam(required=true, value="Sign")String sign,
			@RequestParam(required = true, value = "Content") String content) {
		// 先检测缓存是否已经有回复结果
		if (DataCache.checkCache(cooperatorOrderSerial)) {
			logger.info("BDDK notify result get from cache , appOrder : " + cooperatorOrderSerial);
			return BeanHelper.createBaiduResult(appID, 1, "success");
		}

		String decodecontent = new String(Base64.decodeBase64(content));
		Map<String, Object> contentMap = JsonUtil.parse(decodecontent);
		logger.info("BDDK notify contentMap : "+contentMap.toString());
		String OrderMoney = String.valueOf((int) (Double.parseDouble(String.valueOf(contentMap.get("OrderMoney"))) * 100));
		String OrderStatus = String.valueOf(contentMap.get("OrderStatus"));
		String StartDateTime = String.valueOf(contentMap.get("StartDateTime"));
		
		BDDKCharge.Builder builder = BDDKCharge.newBuilder();
		builder.setAppOrder(cooperatorOrderSerial).setAppID(appID).setOrderSerial(orderSerial)
				.setCooperatorOrderSerial(cooperatorOrderSerial).setSign(sign).setContent(content);
		
		if(!StringUtils.isBlank(OrderMoney)){
			builder.setOrderMoney(OrderMoney);
		}
		
		if(!StringUtils.isBlank(OrderStatus)){
			builder.setOrderStatus(OrderStatus);
		}

		if(!StringUtils.isBlank(StartDateTime)){
			builder.setStartDateTime(StartDateTime);
		}
		BDDKCharge charge = builder.build();
		logger.info("bddkNotify charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(parseServer(cooperatorOrderSerial)),
					Integer.parseInt(ChargeConstants.getPort(parseServer(cooperatorOrderSerial))));
			Result res = client.invokeSyn(cooperatorOrderSerial, charge, TIME_OUT);
			logger.info("bddk notify charge, appOrder = " + cooperatorOrderSerial + ", result = " + res.getResult());
			if (res.getResult() == 1) {
				return BeanHelper.createBaiduResult(appID, 1, "success");
			}
		} catch (Exception e) {
			logger.error("bddk notify failed!", e);
		}
		return BeanHelper.createBaiduResult(appID, 0, "error");
	}
	
	@SuppressWarnings("unused")
	private final static String azAppKey = "1387795767Y2bc1yNgOopj70a9XdxG";
	private final static String azAppSecret = "GY7YGJKSQR9J8Q1GVp27W8pU";
	@RequestMapping("/notify/az")
	public @ResponseBody String azNotify(@RequestParam(required=true, value="data")String data){
		
		String decodeData = AZDes3Util.decrypt(data, azAppSecret);
		logger.info("AZ decodeData : "+decodeData);
		if(StringUtils.isEmpty(decodeData)){
			return "fail";
		}
		Map<String, Object> resMap = JsonUtil.parse(decodeData);
		if(resMap != null){
			String cpInfo = (String)resMap.get("cpInfo");
			// 先检测缓存是否已经有回复结果
			if(DataCache.checkCache(cpInfo)){
				logger.info("AZ notify result get from cache , appOrder : "+cpInfo);
				return "success";
			}
			String payAmount = parseObj(resMap.get("payAmount"));
			String uid = parseObj(resMap.get("uid"));
			String notifyTime = parseObj(resMap.get("notifyTime"));
			String memo = parseObj(resMap.get("memo"));
			String orderAmount = parseObj(resMap.get("orderAmount"));
			String orderAccount = parseObj(resMap.get("orderAccount"));
			String code = parseObj(resMap.get("code"));
			String orderTime = parseObj(resMap.get("orderTime"));
			String msg = parseObj(resMap.get("msg"));
			String orderId = parseObj(resMap.get("orderId"));
			AZCharge.Builder builder = AZCharge.newBuilder();
			builder.setAppOrder(cpInfo).setPayAmount(payAmount).setUid(uid).setNotifyTime(notifyTime)
			.setOrderAmount(orderAmount).setOrderAccount(orderAccount).setCode(code)
			.setOrderTime(orderTime).setMsg(msg).setOrderId(orderId).setCpInfo(cpInfo);
			if(!StringUtils.isBlank(memo)){
				builder.setMemo(memo);
			}
			AZCharge charge = builder.build();
			logger.info("AZNotify charge : "+charge);
			try {
				ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(cpInfo)), Integer.parseInt(ChargeConstants.getPort(parseServer(cpInfo))));
				Result res = client.invokeSyn(cpInfo, charge, TIME_OUT);
				logger.info("AZ notify charge, appOrder = "+ cpInfo + ", result = "+res.getResult());
				if(res.getResult() == 1){
					return "success";
				}
			} catch (Exception e) {
				logger.error("AZ notify failed!", e);
			}
		}

		return "fail";
	}
	
	//快用-RSA加密公钥
	private static final String kyPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdrE2x7Z5ORYO"
			+ "6k+asGU+immKxN3BYdywFp0fESBVRPyL4XPjDdsl3BqFScEVxNI4DA15yJFdbEJwjU1xGeBcc9lswsVZkw"
			+ "ZwohipzNWegiilKbZB0whasweKxo36DIyPjKFS4+qRQEVS2dntxrqD7ClTnm6Sszpv6m1jkOyqfaQIDAQAB";

	/**
	 * 快用支付通知
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = "/notify/ky")
	public @ResponseBody
	String kyNotify(@RequestParam(required = true, value = "notify_data") String notify_data,
			@RequestParam(required = true, value = "orderid") String orderid,
			@RequestParam(required = true, value = "dealseq") String dealseq,
			@RequestParam(required = true, value = "uid") String uid,
			@RequestParam(required = true, value = "subject") String subject,
			@RequestParam(required = true, value = "v") String v,
			@RequestParam(required = true, value = "sign") String sign) {

		//获得通知签名
		Map<String, String> transformedMap = new HashMap<String, String>();
		transformedMap.put("notify_data", notify_data);
		transformedMap.put("orderid", orderid);
		transformedMap.put("sign", sign);
		transformedMap.put("dealseq", dealseq);
		transformedMap.put("uid", uid);
		transformedMap.put("subject", subject);
		transformedMap.put("v", v);
		//rsa签名验签
		String verify = getVerifyData(transformedMap);
		logger.info("verfiy data:" + verify);
		logger.info("sign is:" + sign);

		if (!KYRSASignature.doCheck(verify, sign, kyPubKey, "UTF-8")) {
			logger.warn("KY RSA sign doCheck failed,verify:" + verify);
			return "failed";
		}

		KYRSAEncrypt rsaEncrypt = new KYRSAEncrypt();

		//加载公钥   
		try {
			rsaEncrypt.loadPublicKey(kyPubKey);
		} catch (Exception e) {
			logger.error("KY load RSA public key failed, 快用加载公钥失败", e);
			return "failed";
		}

		//公钥解密通告加密数据
		String notifyData = null;
		try {
			byte[] dcDataStr = SecurityUtil.decodeBase64(notify_data);
			byte[] plainData = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), dcDataStr);
			notifyData = new String(plainData, "UTF-8");
			logger.info("KuaiYong Notify Data:" + notifyData);
		} catch (Exception e) {
			logger.error("KY decrypt failed", e);
			return "failed";
		}

		//比对收到的订单和本地订单，通过dealseq，然后做相应处理
		Map<String, String> notifyMap = ParamsUtil.unJoinParamsValue(notifyData);

		if (StringUtils.isEmpty(dealseq) || notifyMap == null || !dealseq.equals(notifyMap.get("dealseq"))
				|| StringUtils.isEmpty(notifyMap.get("payresult")) || StringUtils.isEmpty(notifyMap.get("fee"))) {
			logger.error("KY verify notifyData failed,Notify Data:" + notifyMap.toString());
			return "failed";
		}

		//先检测缓存是否已经有回复结果
		if (DataCache.checkCache(dealseq)) {
			logger.info("KY notify result get from cache , appOrder : " + dealseq);
			return "success";
		}

		KYCharge.Builder builder = KYCharge.newBuilder();
		builder.setAppOrder(dealseq).setNotifyData(notify_data).setDealseq(dealseq).setOrderid(orderid).setUid(uid)
				.setSubject(subject).setV(v).setSign(sign).setFee(notifyMap.get("fee")).setPayresult(
						notifyMap.get("payresult"));

		KYCharge charge = builder.build();
		logger.info("KYNotify charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(parseServer(dealseq)),
					Integer.parseInt(ChargeConstants.getPort(parseServer(dealseq))));

			Result res = client.invokeSyn(dealseq, charge, TIME_OUT);
			logger.info("KY notify charge, appOrder = " + dealseq + ", result = " + res.getResult());

			return res.getResult() == 1 ? "success" : "failed";
		} catch (Exception e) {
			logger.error("KY notify failed!", e);
		}

		return "failed";
	}
	
	/**
	 * iTools 支付通知
	 */
	@RequestMapping(method = { RequestMethod.GET,RequestMethod.POST }, value = "/notify/iTools")
	public @ResponseBody
	String iToolsNotify(@RequestParam(required = true, value = "sign") String sign,
			@RequestParam(required = true, value = "notify_data") String notify_data) {

		boolean verified = false;
		String notifyJson = null;
		try {
			notifyJson = IToolsRSASignature.decrypt(notify_data);

			logger.info("iTools notifyJson:" + notifyJson);

			//公钥对数据进行RSA签名校验
			verified = IToolsRSASignature.verify(notifyJson, sign);
		} catch (Exception e) {
			logger.error("iTools decrypt error, notify_data:" + notify_data, e);
			return "fail";
		}

		if (!verified) {
			logger.error("iTools verify fail, sign:" + sign);
			return "fail";
		}

		Map<String, Object> mapParam = JsonUtil.parse(notifyJson);
		if (mapParam == null || mapParam.isEmpty()) {
			return "fail";
		}

		//参数列表
		String order_id_com = parseObj(mapParam.get("order_id_com"));
		String user_id = parseObj(mapParam.get("user_id"));
		String amount = parseObj(mapParam.get("amount"));
		String account = parseObj(mapParam.get("account"));
		String order_id = parseObj(mapParam.get("order_id"));
		String result = parseObj(mapParam.get("result"));

		// 先检测缓存是否已经有回复结果
		if (DataCache.checkCache(order_id_com)) {
			logger.info("iTools notify result get from cache , appOrder : " + order_id_com);
			return "success";
		}

		IToolsCharge.Builder builder = IToolsCharge.newBuilder();
		builder.setAppOrder(order_id_com).setOrderIdCom(order_id_com).setUserId(user_id).setAmount(amount)
				.setAccount(account).setOrderId(order_id).setResult(result);

		IToolsCharge charge = builder.build();

		logger.info("iTools charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(parseServer(order_id_com)),
					Integer.parseInt(ChargeConstants.getPort(parseServer(order_id_com))));

			Result res = client.invokeSyn(order_id_com, charge, TIME_OUT);
			logger.info("iTools notify charge, appOrder = " + order_id_com + ", result = " + res.getResult());

			return res.getResult() == 1 ? "success" : "fail";
		} catch (Exception e) {
			logger.error("iTools notify failed!", e);
		}
		return "fail";
	}
	
	/**
	 * YY-支付通知
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET,RequestMethod.POST }, value = "/notify/yy")
	public @ResponseBody
	String yyNotify(@RequestParam(required = true, value = "account") Long account,
			@RequestParam(required = true, value = "orderid") String orderid,
			@RequestParam(required = true, value = "rmb") String rmb,
			@RequestParam(required = true, value = "num") Integer num,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = true, value = "time") Long time,
			@RequestParam(required = true, value = "game") String game,
			@RequestParam(required = false, value = "server") String server,
			@RequestParam(required = false, value = "role") String role,
			@RequestParam(required = true, value = "itemid") String itemid,
			@RequestParam(required = false, value = "price") String price,
			@RequestParam(required = false, value = "cparam") String cparam,
			@RequestParam(required = true, value = "sign") String sign) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 先检测缓存是否已经有回复结果
		if (DataCache.checkCache(cparam)) {
			logger.info("YY notify result get from cache , appOrder : " + cparam);
			Map<String, Object> dataMap = this.buildYYDataMap(account, orderid, rmb);
			resultMap.put("code", 1);
			resultMap.put("data", dataMap);
			return JsonUtil.objectToJson(resultMap);
		}

		YYCharge.Builder builder = YYCharge.newBuilder();

		builder.setAppOrder(cparam).setAccount(account).setOrderid(orderid).setRmb(rmb).setNum(num).setType(type)
				.setTime(time).setGame(game).setServer(server).setRole(role).setItemid(itemid).setPrice(price)
				.setCparam(cparam).setSign(sign);

		YYCharge charge = builder.build();
		logger.info("YY charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(parseServer(cparam)),
					Integer.parseInt(ChargeConstants.getPort(parseServer(cparam))));

			Result res = client.invokeSyn(cparam, charge, TIME_OUT);
			logger.info("YY notify charge, appOrder = " + cparam + ", result = " + res.getResult());
			if (res.getResult() == 1) {
				Map<String, Object> dataMap = this.buildYYDataMap(account, orderid, rmb);
				resultMap.put("code", 1);
				resultMap.put("data", dataMap);
				return JsonUtil.objectToJson(resultMap);
			}
		} catch (Exception e) {
			logger.error("YY notify failed!", e);
		}
		resultMap.put("code", -100);
		resultMap.put("data", null);
		return JsonUtil.objectToJson(resultMap);
	}
	
	private Map<String, Object> buildYYDataMap(Long account, String orderid, String rmb) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("orderid", orderid);
		dataMap.put("rmb", Float.valueOf(NumberUtils.toFloat(rmb)).intValue());
		dataMap.put("account", account);
		return dataMap;
	}
	
	/**
     * 获得验签名的数据
     * @param map
     * @return
     */
    private String getVerifyData(Map<String,String> map) {
        String signData = getSignData(map);
        return signData;
    }
	
	/**
	 * 获得MAP中的参数串；
	 * 
	 * @param params
	 * @return
	 */
	public static String getSignData(Map<String, String> params) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);

			if ("sign".equals(key)) {
				continue;
			}
			String value = (String) params.get(key);
			if (value != null) {
				content.append((i == 0 ? "" : "&") + key + "=" + value);
			} else {
				content.append((i == 0 ? "" : "&") + key + "=");
			}
		}
		return content.toString();
	}
	
	private String parseObj(Object obj){
		return obj == null? "" : obj.toString();
	}
	
	
	@RequestMapping("/notify/onesdk")
	public @ResponseBody Map<String, Object> onesdkNotify(
			@RequestParam(required=true, value="appOrder")String appOrder, 
			@RequestParam(required=true, value="userIdentity")String userIdentity, 
			@RequestParam(required=true, value="appId")Long appId, 
			@RequestParam(required=true, value="generalOrder")Long generalOrder, 
			@RequestParam(required=true, value="t")Long t,
			@RequestParam(required=true, value="amount")Integer amount,
			@RequestParam(required=true, value="payStatus")String payStatus,
			@RequestParam(required=true, value="agent")String agent,
			@RequestParam(required=true, value="agentId")String agentId,
			@RequestParam(required=false, value="agentOrder")String agentOrder,
			@RequestParam(required=true, value="bookAmount")Integer bookAmount,
			@RequestParam(required=false, value="serverId")Integer serverId,
			@RequestParam(required=false, value="ext")String ext,
			@RequestParam(required=true, value="sign")String sign,
			@RequestParam(required=false, value="supplement")String supplement
			){
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("code", 1);
		
		// 先检测缓存是否已经有回复结果
		if(DataCache.checkCache(appOrder)){
			retMap.put("code", 0);
			retMap.put("msg", "OK!");
			logger.info("onesdk notify result get from cache , appOrder : "+appOrder);
			return retMap;
		}
		
		OnesdkCharge.Builder builder = OnesdkCharge.newBuilder().setAppOrder(appOrder).setUserIdentity(userIdentity).setAppId(appId)
				.setGeneralOrder(generalOrder).setT(t).setAmount(amount).setPayStatus(payStatus).setAgent(agent).setAgentId(agentId)
				.setBookAmount(bookAmount).setSign(sign);

		if(!StringUtils.isEmpty(agentOrder)){
			builder = builder.setAgentOrder(agentOrder);
		}
				
		if(serverId != null && serverId > 0){
			builder = builder.setServerId(serverId);
		}
		
		if(!StringUtils.isEmpty(ext)){
			builder = builder.setExt(ext);
		}
		
		if(!StringUtils.isEmpty(supplement)){
			builder = builder.setSupplement(supplement);
		}
		
		OnesdkCharge charge = builder.build();
		
		logger.info("onesdk charge : " + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(ChargeConstants.getIp(parseServer(appOrder)), Integer.parseInt(ChargeConstants.getPort(parseServer(appOrder))));
			Result result = client.invokeSyn(appOrder, charge, TIME_OUT);
			logger.info("onesdk notify charge, appOrder = "+ appOrder + ", result = "+result.getResult());
			if(result.getResult() == 1){
				retMap.put("code", 0);
				retMap.put("msg", "OK!");
				return retMap;
			}
		} catch (Exception e) {
			logger.error("onesdk notify failed!", e);
		}
		retMap.put("msg", "失败！");
		return retMap;
	}
}
