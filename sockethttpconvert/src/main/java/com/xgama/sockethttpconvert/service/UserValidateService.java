package com.xgama.sockethttpconvert.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.protobuf.MessageLite;
import com.xgama.sockethttpconvert.client.ChargeClient;
import com.xgama.sockethttpconvert.client.ChargeClientFactory;
import com.xgama.sockethttpconvert.config.ChargeConstants;
import com.xgama.sockethttpconvert.config.Constants;
import com.xgama.sockethttpconvert.controller.ChargeController;
import com.xgama.sockethttpconvert.pb.ChargeClass.GooglePayCharge;
import com.xgama.sockethttpconvert.pb.UserValidateClass.AZUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.BDDKUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.DLUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.GooglePay;
import com.xgama.sockethttpconvert.pb.UserValidateClass.IToolsUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.JYUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.KYUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.LaoHuUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.OnesdkUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.PPUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.QHGetUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.QHGetUserResult;
import com.xgama.sockethttpconvert.pb.UserValidateClass.QHRefreshToken;
import com.xgama.sockethttpconvert.pb.UserValidateClass.QHRefreshTokenResult;
import com.xgama.sockethttpconvert.pb.UserValidateClass.QHUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.TBTUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.UCUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.ValidateResult;
import com.xgama.sockethttpconvert.pb.UserValidateClass.WDJUser;
import com.xgama.sockethttpconvert.pb.UserValidateClass.XMUser;
import com.xgama.sockethttpconvert.util.GooglePaySecurity;
import com.xgama.sockethttpconvert.util.HttpClientUtil;
import com.xgama.sockethttpconvert.util.JsonUtil;
import com.xgama.sockethttpconvert.util.SecurityUtil;

public class UserValidateService {
	
	private final static int TIMEOUT = 5000;
	private final static Logger logger = LoggerFactory.getLogger(UserValidateService.class);
	
	public static MessageLite validate(Object object){
		
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		boolean valRes = false;
		int tempUserID = 0;
		ValidateResult result = builder.setTmpUserID(tempUserID).setResult(valRes).build();
		
		if(object instanceof LaoHuUser){
			LaoHuUser laohuUser = (LaoHuUser)object;
			valRes = laohuValidate(laohuUser);
			tempUserID = laohuUser.getTmpUserID();
			result = builder.setTmpUserID(tempUserID).setResult(valRes).build();
		}
		
		if(object instanceof JYUser){
			JYUser user91 = (JYUser)object;
			result = jyValidate(user91);
		}
		
		if(object instanceof DLUser){
			DLUser dlUser = (DLUser)object;
			result = dlValidate(dlUser);
		}
		
		if(object instanceof PPUser){
			PPUser ppUser = (PPUser)object;
			result = ppValidate(ppUser);
		} 
		
		if(object instanceof QHUser){
			QHUser qhUser = (QHUser)object;
			QHRefreshTokenResult qhRefreshTokenResult = qhValidate(qhUser);
			
			logChannelValidate(getChannelName(object), !StringUtils.isEmpty(qhRefreshTokenResult.getAccessToken()));
			return qhRefreshTokenResult;
		}
		
		if(object instanceof UCUser){
			UCUser ucUser = (UCUser)object;
			result = ucValidate(ucUser);
		}
		
		if(object instanceof XMUser){
			XMUser xmUser = (XMUser)object;
			result = xmValidate(xmUser);
		}
		
		if(object instanceof WDJUser){
			WDJUser wdjUser = (WDJUser)object;
			result = wdjValidate(wdjUser);
		}
		
		if(object instanceof TBTUser){
			TBTUser tbtUser = (TBTUser)object;
			result = tbtValidate(tbtUser);
		}
		
		if(object instanceof BDDKUser){
			BDDKUser bddkUser = (BDDKUser)object;
			result = bddkValidate(bddkUser);
		}
		
		if(object instanceof AZUser){
			AZUser azUser = (AZUser)object;
			result = azValidate(azUser);
		}
		
		if(object instanceof QHRefreshToken){
			QHRefreshToken qhRefreshToken = (QHRefreshToken)object;
			QHRefreshTokenResult qhRefreshTokenResult = qhRefreshToken(qhRefreshToken);
			
			logChannelValidate(getChannelName(object), !StringUtils.isEmpty(qhRefreshTokenResult.getAccessToken()));
			return qhRefreshTokenResult;
		}
		
		if(object instanceof QHGetUser){
			QHGetUser qhGetUser = (QHGetUser)object;
			QHGetUserResult qhGetUserResult = qhGetUser(qhGetUser);
			
			logChannelValidate(getChannelName(object), !StringUtils.isEmpty(qhGetUserResult.getId()));
			return qhGetUserResult;
		}
		
		if(object instanceof KYUser){
			KYUser kyUser = (KYUser) object;
			result = kyValidate(kyUser);
		}
		
		if(object instanceof RTSGUser){
			RTSGUser rtsgUser = (RTSGUser) object;
			result = rtsgValidate(rtsgUser);
		}
		
		if(object instanceof BMSGUser){
			BMSGUser bmsgUser = (BMSGUser) object;
			result = bmsgValidate(bmsgUser);
		}
		
		if (object instanceof ThailandGetToken) {
			ThailandGetToken thailandGetToken = (ThailandGetToken) object;
			ThailandGetTokenResult thailandGetTokenResult = thailandGetToken(thailandGetToken);
			
			logChannelValidate(getChannelName(object), thailandGetTokenResult.getResult());
			return thailandGetTokenResult;
		}
		
		if (object instanceof ThailandTokenDelay) {
			ThailandTokenDelay tokenDelay = (ThailandTokenDelay) object;
			ThailandTokenDelayResult thailandTokenDelayResult = thailandTokenDelay(tokenDelay);
			
			logChannelValidate(getChannelName(object), thailandTokenDelayResult.getResult());
			return thailandTokenDelayResult;
		}
		
		if(object instanceof ThailandUserInfo) {
			ThailandUserInfo thailandUserInfo = (ThailandUserInfo) object;
			ThailandUserInfoResult thailandUserInfoResult = thailandUserInfo(thailandUserInfo);
			
			logChannelValidate(getChannelName(object), thailandUserInfoResult.getResult());
			return thailandUserInfoResult;
		}
		
		if(object instanceof IToolsUser) {
			IToolsUser iToolsUser = (IToolsUser) object;
			result = iToolsValidate(iToolsUser);
		}
		
		if(object instanceof MalaysiaUser) {
			MalaysiaUser malaysiaUser = (MalaysiaUser) object;
			result = malaysiaValidate(malaysiaUser);
		}
		
		if(object instanceof MalaysiaEnUser){
			MalaysiaEnUser malaysiaEnUser = (MalaysiaEnUser) object;
			result = malaysiaEnValidate(malaysiaEnUser);
		}
		
		if(object instanceof OnesdkUser){
			OnesdkUser onesdkUser = (OnesdkUser) object;
			result = onesdkValidate(onesdkUser);
		}
		
		logChannelValidate(getChannelName(object), result.getResult());
		
		return result;
	}
	
	/**
	 * UC 用户登录验证
	 * @param ucUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static ValidateResult ucValidate(UCUser ucUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", ucUser.getId());
		params.put("service", ucUser.getService());
		params.put("data", JsonUtil.parse(ucUser.getData()));
		params.put("game", JsonUtil.parse(ucUser.getGame()));
		params.put("sign", ucUser.getSign());
		String result = HttpClientUtil.postJsonBody(Constants.ucValidateUrl, TIMEOUT, params, "UTF-8");
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(ucUser.getTmpUserID()).setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(ucUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> stateMap = (Map<String, Object>)resMap.get("state");
		if(stateMap != null){
			int code = (Integer)stateMap.get("code");
			if(code == 1){
				Map<String, Object> dataMap = (Map<String, Object>)resMap.get("data");
				int ucid = (Integer)dataMap.get("ucid");
				return builder.setTmpUserID(ucUser.getTmpUserID()).setResult(true).setUserId(ucid).build();
			}
		}
		return builder.setTmpUserID(ucUser.getTmpUserID()).setResult(false).build();
	}

	/**
	 * 小米用户登录验证
	 * @return
	 */
	private static ValidateResult xmValidate(XMUser xmUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("appId", xmUser.getAppId());
		params.put("session", xmUser.getSession());
		params.put("uid", xmUser.getUid());
		params.put("signature", xmUser.getSignature().toLowerCase());
		String result = HttpClientUtil.invokeGet(Constants.xmValidateUrl, params, "UTF-8", TIMEOUT, TIMEOUT);
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(xmUser.getTmpUserID()).setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(xmUser.getTmpUserID()).setResult(false).build();
		}
		
		int errcode = (Integer)resMap.get("errcode");
		if(errcode == 200){
			return builder.setTmpUserID(xmUser.getTmpUserID()).setResult(true).build();
		}

		return builder.setTmpUserID(xmUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 老虎登陆验证
	 * @param user
	 * @return
	 */
	private static boolean laohuValidate(LaoHuUser user) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(user.getUserId()));
		params.put("appId", String.valueOf(user.getAppId()));
		params.put("token", user.getToken());
		params.put("t", String.valueOf(user.getT()));
		params.put("sign", user.getSign());

		//version为1 是新token机制，否则为老token机制
		boolean newVersion = user.getVersion() == 1;

		String validateUrl = newVersion ? Constants.laohuNewUserValidateUrl : Constants.laohuUserValidateUrl;

		if (newVersion) {
			//params.put("version", String.valueOf(user.getVersion()));
			params.put("channelId", String.valueOf(user.getChannelId()));
		}

		logger.info("laohu validate user params : " + params + " --> version:" + user.getVersion());

		String result = HttpClientUtil.invokeGet(validateUrl, params, "UTF-8", TIMEOUT, TIMEOUT);

		logger.info("laohu validate user result : " + result);

		if (!StringUtils.hasText(result)) {
			return false;
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return false;
		}

		int code = (Integer) resMap.get("code");
		if (code == 0) {
			return true;
		}

		return false;
	}
	
	/**
	 * 91登陆验证
	 * @param user91
	 * @return
	 */
	private static ValidateResult jyValidate(JYUser user91){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("AppID", user91.getAppID());
		params.put("AccessToken", user91.getAccessToken());
		params.put("Sign", user91.getSign());
		String result = HttpClientUtil.invokeGet(Constants.user91ValidateUrl, params, "UTF-8", TIMEOUT, TIMEOUT);
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(user91.getTmpUserID()).setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(user91.getTmpUserID()).setResult(false).build();
		}
		
		Integer resultCode = (Integer)resMap.get("ResultCode");
		if(resultCode != null && 1 == resultCode){
			return builder.setTmpUserID(user91.getTmpUserID()).setResult(true).build();
		}
		
		return builder.setTmpUserID(user91.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 当乐登陆验证
	 * @param dlUser
	 * @return
	 */
	private static ValidateResult dlValidate(DLUser dlUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_id", String.valueOf(dlUser.getAppId()));
		params.put("mid", String.valueOf(dlUser.getMid()));
		params.put("token", dlUser.getToken());
		params.put("sig", dlUser.getSig());
		String result = HttpClientUtil.invokeGet(Constants.dlValidateUrl, params, "UTF-8", TIMEOUT, TIMEOUT);
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(dlUser.getTmpUserID()).setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(dlUser.getTmpUserID()).setResult(false).build();
		}
		
		Integer errorCode = (Integer)resMap.get("error_code");
		if(errorCode == 0){
			int memberId = (Integer)resMap.get("memberId");
			return builder.setTmpUserID(dlUser.getTmpUserID()).setResult(true).setUserId(memberId).build();
		}
		
		return builder.setTmpUserID(dlUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 豌豆荚登录验证
	 * @param wdjUser
	 * @return
	 */
	private static ValidateResult wdjValidate(WDJUser wdjUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", wdjUser.getUid());
		params.put("token", wdjUser.getToken());
		params.put("appkey_id", wdjUser.getAppkeyId());
		String result = HttpClientUtil.httpsGet(Constants.wdjValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("wdjUser result : "+result);
		if(result.equalsIgnoreCase("true")){
			return builder.setTmpUserID(wdjUser.getTmpUserID()).setResult(true).build();
		} else {
			return builder.setTmpUserID(wdjUser.getTmpUserID()).setResult(false).build();
		}
	}
	
	/**
	 * 同步推登录验证
	 * @param tbtUser
	 * @return
	 */
	private static ValidateResult tbtValidate(TBTUser tbtUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("k", tbtUser.getToken());
		String result = HttpClientUtil.httpsGet(Constants.tbtValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("tbtUser result : "+result);
		try {
			int parseRes = Integer.parseInt(result);
			if(parseRes > 1){
				return builder.setTmpUserID(tbtUser.getTmpUserID()).setResult(true).setUserId(parseRes).build();
			} 
		} catch (Exception e) {
			logger.error("tbtValidate failed", e);
		}
		return builder.setTmpUserID(tbtUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 百度多酷登录验证
	 * @param bddkUser
	 * @return
	 */
	private static ValidateResult bddkValidate(BDDKUser bddkUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("AppID", bddkUser.getAppID());
		params.put("AccessToken", bddkUser.getAccessToken());
		params.put("Sign", bddkUser.getSign());
		logger.info("bddk validate user params : "+params);
		String result = HttpClientUtil.httpsGet(Constants.bddkValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("bddk validate user result : "+result);
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(bddkUser.getTmpUserID()).setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(bddkUser.getTmpUserID()).setResult(false).build();
		}
		
		Integer resultCode = (Integer)resMap.get("ResultCode");
		if(resultCode != null && 1 == resultCode){
			return builder.setTmpUserID(bddkUser.getTmpUserID()).setResult(true).build();
		}
		return builder.setTmpUserID(bddkUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 安智登录验证
	 * @param bddkUser
	 * @return
	 */
	private static ValidateResult azValidate(AZUser azUser){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", azUser.getTime());
		params.put("appkey", azUser.getAppkey());
		params.put("sid", azUser.getSid());
		params.put("sign", azUser.getSign());
		logger.info("az validate user params : "+params);
		String result = HttpClientUtil.invokePost(Constants.azValidateUrl, params, TIMEOUT, Consts.UTF_8.name());
		logger.info("az validate user result : "+result);
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(azUser.getTmpUserID()).setResult(false).build();
		}
		
		AzUserValidateResponse azUserValidateResponse = JsonUtil.decodeJson(result, AzUserValidateResponse.class);
		if(azUserValidateResponse == null){
			return builder.setTmpUserID(azUser.getTmpUserID()).setResult(false).build();
		}
		
		String sc = azUserValidateResponse.getSc();
		if("1".equalsIgnoreCase(sc)){
			String msg = azUserValidateResponse.getMsg();
			if(StringUtils.isEmpty(msg)){
				return builder.setTmpUserID(azUser.getTmpUserID()).setResult(true).build();
			} else {
				String jsonMsg = SecurityUtil.base64Decode(msg);
				Map<String, Object> resMap = JsonUtil.parse(jsonMsg);
				return builder.setTmpUserID(azUser.getTmpUserID()).setUidStr((String)resMap.get("uid")).setResult(true).build();
			}
		}
		return builder.setTmpUserID(azUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 360用户验证
	 * @param qhUser
	 */
	private static QHRefreshTokenResult qhValidate(QHUser qhUser){
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", qhUser.getGrantType());
		params.put("code", qhUser.getCode());
		params.put("client_id", qhUser.getClientId());
		params.put("client_secret", qhUser.getClientSecret());
		params.put("redirect_uri", qhUser.getRedirectUri());
		String result = HttpClientUtil.connectPostHttps(Constants.qhValidateUrl, params);
		QHRefreshTokenResult.Builder builder = QHRefreshTokenResult.newBuilder();
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(qhUser.getTmpUserID()).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(qhUser.getTmpUserID()).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
		}
		
		String access_token = (String)resMap.get("access_token");
		if(!StringUtils.isEmpty(access_token)){
			String expires_in = (String)resMap.get("expires_in");
			String scope = (String)resMap.get("scope");
			String refresh_token = (String)resMap.get("refresh_token");
			return builder.setTmpUserID(qhUser.getTmpUserID()).setAccessToken(access_token).setExpiresIn(expires_in).setScope(scope).setRefreshToken(refresh_token).build();
		}
		
		return builder.setTmpUserID(qhUser.getTmpUserID()).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
	}
	
	/**
	 * 360刷新用户token
	 * @param qRefreshToken
	 * @return
	 */
	private static QHRefreshTokenResult qhRefreshToken(QHRefreshToken qRefreshToken){
		int tempUserID = qRefreshToken.getTmpUserID();
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", qRefreshToken.getGrantType());
		params.put("refresh_token", qRefreshToken.getRefreshToken());
		params.put("client_id", qRefreshToken.getClientId());
		params.put("client_secret", qRefreshToken.getClientSecret());
		params.put("scope", qRefreshToken.getScope());
		String result = HttpClientUtil.connectPostHttps(Constants.qhValidateUrl, params);
		QHRefreshTokenResult.Builder builder = QHRefreshTokenResult.newBuilder();
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(tempUserID).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(tempUserID).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
		}
		
		String access_token = (String)resMap.get("access_token");
		if(!StringUtils.isEmpty(access_token)){
			String expires_in = (String)resMap.get("expires_in");
			String scope = (String)resMap.get("scope");
			String refresh_token = (String)resMap.get("refresh_token");
			return builder.setTmpUserID(tempUserID).setAccessToken(access_token).setExpiresIn(expires_in).setScope(scope).setRefreshToken(refresh_token).build();
		}
		
		return builder.setTmpUserID(tempUserID).setAccessToken("").setExpiresIn("").setScope("").setRefreshToken("").build();
	}
	
	/**
	 * 360获取用户
	 * @param qGetUser
	 * @return
	 */
	private static QHGetUserResult qhGetUser(QHGetUser qGetUser){
		int tempUserID = qGetUser.getTmpUserID();
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", qGetUser.getAccessToken());
		params.put("fields", qGetUser.getFields());
		String result = HttpClientUtil.connectPostHttps(Constants.qhGetUserUrl, params);
		QHGetUserResult.Builder builder = QHGetUserResult.newBuilder();
		if(!StringUtils.hasText(result)){
			return builder.setTmpUserID(tempUserID).setId("").setName("").setAvatar("").build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setTmpUserID(tempUserID).setId("").setName("").setAvatar("").build();
		}
		
		String id = (String)resMap.get("id");
		if(!StringUtils.isEmpty(id)){
			String name = (String)resMap.get("name");
			String avatar = (String)resMap.get("avatar");
			return builder.setTmpUserID(tempUserID).setId(id).setName(name).setAvatar(avatar).build();
		}
		
		return builder.setTmpUserID(tempUserID).setId("").setName("").setAvatar("").build();
	}
	
	/**
	 * PP 登录验证
	 * @param uid
	 * @param session
	 * @return
	 */
	private static ValidateResult ppValidate(PPUser ppUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		String token_key = ppUser.getTokenKey();
		byte [] bytes = hexString2bytes(token_key);
		int length = bytes.length + 8;
		Socket client = null;
		try {
			InetAddress inStr = InetAddress.getByName("passport_i.25pp.com");	
			
			client = new Socket(inStr, 8080);
			client.setReuseAddress(false);
			
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			
			ByteBuffer inbf = ByteBuffer.allocate(length);
			inbf.order(ByteOrder.LITTLE_ENDIAN);
			inbf.putInt(length);
			inbf.putInt(0xAA000022);
			inbf.put(bytes);
			inbf.rewind();
			out.write(inbf.array());
			out.flush();
			
			byte[] read = readStream(in);
			if(read.length >= RecvPSData.MiniObjectSize)
			{
				RecvPSData recvPSData = new RecvPSData();
				ByteBuffer otbf = ByteBuffer.wrap(read);
				otbf.order(ByteOrder.LITTLE_ENDIAN);
				recvPSData.setLen(otbf.getInt());
				recvPSData.setCommand(otbf.getInt());
				recvPSData.setStatus(otbf.getInt());
//				logger.info("Recv from PP Server data length: " + recvPSData.getLen() + ", command: 0x" + Integer.toHexString(recvPSData.getCommand()) + ", status: 0x" + Integer.toHexString(recvPSData.getStatus()));
				if(recvPSData.getStatus() == 0){
					byte busername[] = new byte[recvPSData.getLen()-(3*4+8)];	//取 username 字节长度为 RecvPSData.getLen()-(3*4+8)
					otbf.get(busername, 0, recvPSData.getLen()-(3*4+8));
					String username = new String(busername, "UTF-8");
					recvPSData.setUserid(otbf.getLong());
					recvPSData.setUsername(username);
					return builder.setTmpUserID(ppUser.getTmpUserID()).setResult(true).setUserId(recvPSData.getUserid()).build();
				}
			}
			
		} catch (Exception e) {
			logger.error("PP validate failed, TmpUserID = "+ppUser.getTmpUserID()+", token_key = "+ppUser.getTokenKey(), e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				logger.error("PP validate failed, TmpUserID = "+ppUser.getTmpUserID()+", token_key = "+ppUser.getTokenKey(), e);
			}
		}
		return builder.setTmpUserID(ppUser.getTmpUserID()).setResult(false).build();
	}

	/**
	 * 快用  - 用户验证
	 * @param kyUser 
	 * @return ValidateResult
	 */
	@SuppressWarnings("unchecked")
	private static ValidateResult kyValidate(KYUser kyUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("tokenKey", kyUser.getTokenKey());
		params.put("sign", kyUser.getSign());
		logger.info("ky validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.kyValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("ky validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(kyUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resultMap = JsonUtil.parse(result);
		if (resultMap == null) {
			return builder.setTmpUserID(kyUser.getTmpUserID()).setResult(false).build();
		}

		String code = String.valueOf(resultMap.get("code"));
		if ("0".equalsIgnoreCase(code)) {
			builder.setTmpUserID(kyUser.getTmpUserID()).setResult(true);
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			if (dataMap != null && !StringUtils.isEmpty((String) dataMap.get("guid"))) {
				builder.setUidStr((String) dataMap.get("guid"));
			}
			return builder.build();
		}

		return builder.setTmpUserID(kyUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 日台神鬼 - 用户验证
	 * @param rtsgUser
	 * @return ValidateResult
	 */
	private static ValidateResult rtsgValidate(RTSGUser rtsgUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("gameid", rtsgUser.getGameid());
		params.put("playerid", rtsgUser.getPlayerid());
		params.put("token", rtsgUser.getToken());
		params.put("t", rtsgUser.getT());
		params.put("sign", rtsgUser.getSign());
		logger.info("RtSg validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.rtsgValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("RtSg validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(rtsgUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(rtsgUser.getTmpUserID()).setResult(false).build();
		}

		String error = String.valueOf(resMap.get("error"));
		if ("0".equalsIgnoreCase(error)) {
			return builder.setTmpUserID(rtsgUser.getTmpUserID()).setResult(true).build();
		}
		return builder.setTmpUserID(rtsgUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 北美-神鬼幻想 - 用户验证
	 * @param bmsgUser
	 * @return ValidateResult
	 */
	private static ValidateResult bmsgValidate(BMSGUser bmsgUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("gameUid", bmsgUser.getGameUid());
		params.put("gameId", bmsgUser.getGameId());
		params.put("gameToken", bmsgUser.getGameToken());
		params.put("t", bmsgUser.getT());
		params.put("sign", bmsgUser.getSign());

		logger.info("BmSg validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.bmsgValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("BmSg validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(bmsgUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(bmsgUser.getTmpUserID()).setResult(false).build();
		}

		String code = String.valueOf(resMap.get("code"));
		if ("0".equalsIgnoreCase(code)) {
			return builder.setTmpUserID(bmsgUser.getTmpUserID()).setUidStr(bmsgUser.getGameUid()).setResult(true)
					.build();
		}
		return builder.setTmpUserID(bmsgUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 泰国-神鬼幻想 - 获取token
	 * @param thailandUser
	 * @return ThailandGetTokenResult
	 */
	@SuppressWarnings("unchecked")
	private static ThailandGetTokenResult thailandGetToken(ThailandGetToken getToken) {
		ThailandGetTokenResult.Builder builder = ThailandGetTokenResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("authcode", getToken.getAuthcode());
		params.put("appkey", getToken.getAppkey());
		params.put("appSecret", getToken.getAppSecret());

		logger.info("Thailand get token params : " + params);
		String result = HttpClientUtil.connectPostHttps(Constants.thailandGetTokenUrl, params);
		logger.info("Thailand get token result : " + result);

		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(getToken.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(getToken.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resultMap = ((Map<String, Object>) resMap.get("result"));

		if (resultMap == null) {
			return builder.setTmpUserID(getToken.getTmpUserID()).setResult(false).build();
		}

		String status = String.valueOf(resultMap.get("status"));
		if ("1".equalsIgnoreCase(status)) {

			Map<String, Object> dataMap = (Map<String, Object>) resMap.get("data");

			if (dataMap != null) {
				builder.setTmpUserID(getToken.getTmpUserID()).setResult(true);

				String accessToken = (String) dataMap.get("access_token");
				String expiration = (String) dataMap.get("expiration");
				builder.setAccessToken(accessToken).setExpiration(expiration);
			} else {
				builder.setTmpUserID(getToken.getTmpUserID()).setResult(false);
			}
			return builder.build();
		}

		return builder.setTmpUserID(getToken.getTmpUserID()).setResult(false).build();
	}

	/**
	 * 泰国-神鬼幻想 - Access_token延时
	 * @param tokenDelay
	 * @return ThailandValidateResult
	 */
	@SuppressWarnings("unchecked")
	private static ThailandTokenDelayResult thailandTokenDelay(ThailandTokenDelay tokenDelay) {
		ThailandTokenDelayResult.Builder builder = ThailandTokenDelayResult.newBuilder();

		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", tokenDelay.getAccessToken());
		params.put("appkey", tokenDelay.getAppkey());
		params.put("appSecret", tokenDelay.getAppSecret());

		logger.info("Thailand access_token delay params : " + params);
		String result = HttpClientUtil.connectPostHttps(Constants.thailandTokenDelayUrl, params);
		logger.info("Thailand access_token delay result : " + result);

		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resultMap = ((Map<String, Object>) resMap.get("result"));

		if (resultMap == null) {
			return builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(false).build();
		}

		String status = String.valueOf(resultMap.get("status"));
		if ("1".equalsIgnoreCase(status)) {
			Map<String, Object> dataMap = (Map<String, Object>) resMap.get("data");

			if (dataMap != null) {
				builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(true);

				String accessToken = (String) dataMap.get("access_token");
				String expiration = (String) dataMap.get("expiration");
				builder.setAccessToken(accessToken).setExpiration(expiration);
			} else {
				builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(false);
			}
			return builder.build();
		}

		return builder.setTmpUserID(tokenDelay.getTmpUserID()).setResult(false).build();

	}
	
	/**
	 * 泰国-神鬼幻想 - 用户信息
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static ThailandUserInfoResult thailandUserInfo(ThailandUserInfo userInfo) {
		ThailandUserInfoResult.Builder builder = ThailandUserInfoResult.newBuilder();

		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", userInfo.getAccessToken());

		logger.info("Thailand user info params : " + params);
		String result = HttpClientUtil.connectPostHttps(Constants.thailandUserInfoUrl, params);
		logger.info("Thailand user info result : " + result);

		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(userInfo.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(userInfo.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resultMap = ((Map<String, Object>) resMap.get("result"));

		if (resultMap == null) {
			return builder.setTmpUserID(userInfo.getTmpUserID()).setResult(false).build();
		}

		String status = String.valueOf(resultMap.get("status"));
		if ("1".equalsIgnoreCase(status)) {
			Map<String, Object> dataMap = (Map<String, Object>) resMap.get("data");

			if (dataMap != null) {
				builder.setTmpUserID(userInfo.getTmpUserID()).setResult(true);

				String userId = (String) dataMap.get("userId");
				builder.setUserId(userId);
			} else {
				builder.setTmpUserID(userInfo.getTmpUserID()).setResult(false);
			}
			return builder.build();
		}

		return builder.setTmpUserID(userInfo.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * google Pay 请求转发
	 * @param googlePay
	 */
	public static void googlePayForward(GooglePay googlePay) {
		boolean verify = false;
		try {
			verify = GooglePaySecurity.verifyPurchase(Constants.GOOGLE_PAY_PUBLIC_KEY, googlePay.getPayInfo(),
					googlePay.getSign());
		} catch (Exception e) {
			logger.error("GooglePay verify exception", e);
		}

		logger.info("GooglePay data:" + googlePay + ", verify result:" + verify);

		if (!verify) {
			return;
		}

		Map<String, Object> payInfoMap = JsonUtil.parse(googlePay.getPayInfo());
		if (payInfoMap == null || payInfoMap.isEmpty()) {
			logger.error("GooglePay parse Json is empty");
			return;
		}

		String appOrder = (String) payInfoMap.get("developerPayload");

		GooglePayCharge.Builder builder = GooglePayCharge.newBuilder();
		builder.setAppOrder(appOrder).setOrderId((String) payInfoMap.get("orderId"))
				.setProductId((String) payInfoMap.get("productId"));
		GooglePayCharge charge = builder.build();

		logger.info("GooglePay charge:" + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(ChargeController.parseServer(appOrder)),
					Integer.parseInt(ChargeConstants.getPort(ChargeController.parseServer(appOrder))));

			client.invokeSend(builder.getAppOrder(), charge);

			logger.info("GooglePay notify charge, appOrder = " + appOrder);

		} catch (Exception e) {
			logger.error("GooglePay notify failed!", e);
		}

	} 
	
	
	/**
	 * iTools - 用户验证
	 * @param iToolsUser
	 * @return ValidateResult
	 */
	private static ValidateResult iToolsValidate(IToolsUser iToolsUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", iToolsUser.getAppid());
		params.put("sessionid", iToolsUser.getSessionid());
		params.put("sign", iToolsUser.getSign());

		logger.info("iTools validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.iToolsValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("iTools validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(iToolsUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(iToolsUser.getTmpUserID()).setResult(false).build();
		}

		String status = String.valueOf(resMap.get("status"));
		if ("success".equalsIgnoreCase(status)) {
			return builder.setTmpUserID(iToolsUser.getTmpUserID()).setResult(true).build();
		}
		return builder.setTmpUserID(iToolsUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 马来西亚神鬼 - 用户验证
	 * @param malaysiaUser
	 * @return ValidateResult
	 */
	private static ValidateResult malaysiaValidate(MalaysiaUser malaysiaUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("gameid", malaysiaUser.getGameid());
		params.put("playerid", malaysiaUser.getPlayerid());
		params.put("token", malaysiaUser.getToken());
		params.put("t", malaysiaUser.getT());
		params.put("sign", malaysiaUser.getSign());
		logger.info("Malaysia validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.rtsgValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("Malaysia validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(malaysiaUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(malaysiaUser.getTmpUserID()).setResult(false).build();
		}

		String error = String.valueOf(resMap.get("error"));
		if ("0".equalsIgnoreCase(error)) {
			return builder.setTmpUserID(malaysiaUser.getTmpUserID()).setResult(true).build();
		}
		return builder.setTmpUserID(malaysiaUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 马来西亚神鬼 -英文版 - 用户验证
	 * @param malaysiaUser
	 * @return ValidateResult
	 */
	private static ValidateResult malaysiaEnValidate(MalaysiaEnUser malaysiaEnUser) {
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("gameid", malaysiaEnUser.getGameid());
		params.put("playerid", malaysiaEnUser.getPlayerid());
		params.put("token", malaysiaEnUser.getToken());
		params.put("t", malaysiaEnUser.getT());
		params.put("sign", malaysiaEnUser.getSign());
		logger.info("Malaysia en validate user params : " + params);
		String result = HttpClientUtil.httpsGet(Constants.rtsgEnValidateUrl, params, TIMEOUT, TIMEOUT);
		logger.info("Malaysia en validate user result : " + result);
		if (!StringUtils.hasText(result)) {
			return builder.setTmpUserID(malaysiaEnUser.getTmpUserID()).setResult(false).build();
		}

		Map<String, Object> resMap = JsonUtil.parse(result);
		if (resMap == null) {
			return builder.setTmpUserID(malaysiaEnUser.getTmpUserID()).setResult(false).build();
		}

		String error = String.valueOf(resMap.get("error"));
		if ("0".equalsIgnoreCase(error)) {
			return builder.setTmpUserID(malaysiaEnUser.getTmpUserID()).setResult(true).build();
		}
		return builder.setTmpUserID(malaysiaEnUser.getTmpUserID()).setResult(false).build();
	}
	
	/**
	 * 马来西亚 google Pay 请求转发
	 * @param MalaysiaGooglePay
	 */
	public static void malaysiaGooglePayForward(MalaysiaGooglePay googlePay) {
		boolean verify = false;
		try {
			verify = GooglePaySecurity.verifyPurchase(Constants.GOOGLE_PAY_PUBLIC_KEY, googlePay.getPayInfo(),
					googlePay.getSign());
		} catch (Exception e) {
			logger.error("Malaysia GooglePay verify exception", e);
		}

		logger.info("Malaysia GooglePay data:" + googlePay + ", verify result:" + verify);

		if (!verify) {
			return;
		}

		Map<String, Object> payInfoMap = JsonUtil.parse(googlePay.getPayInfo());
		if (payInfoMap == null || payInfoMap.isEmpty()) {
			logger.error("Malaysia GooglePay parse Json is empty");
			return;
		}

		String appOrder = (String) payInfoMap.get("developerPayload");

		MalaysiaGooglePayCharge.Builder builder = MalaysiaGooglePayCharge.newBuilder();
		builder.setAppOrder(appOrder).setOrderId((String) payInfoMap.get("orderId"))
				.setProductId((String) payInfoMap.get("productId"));
		MalaysiaGooglePayCharge charge = builder.build();

		logger.info("Malaysia GooglePay charge:" + charge);
		try {
			ChargeClient client = ChargeClientFactory.getInstance().getClient(
					ChargeConstants.getIp(ChargeController.parseServer(appOrder)),
					Integer.parseInt(ChargeConstants.getPort(ChargeController.parseServer(appOrder))));

			client.invokeSend(builder.getAppOrder(), charge);

			logger.info("Malaysia GooglePay notify charge, appOrder = " + appOrder);

		} catch (Exception e) {
			logger.error("Malaysia GooglePay notify failed!", e);
		}

	} 
	
	private static ValidateResult onesdkValidate(OnesdkUser userOne){
		ValidateResult.Builder builder = ValidateResult.newBuilder();
		builder.setTmpUserID(userOne.getTmpUserID());
		Map<String, String> params = new HashMap<String, String>();
		params.put("userIdentity", userOne.getUserIdentity());
		params.put("token", userOne.getToken());
		params.put("appId", String.valueOf(userOne.getAppId()));
		params.put("sign", userOne.getSign());
		String result = HttpClientUtil.invokeGet(Constants.onesdkValidateUrl, params, "UTF-8", TIMEOUT, TIMEOUT);
		if(!StringUtils.hasText(result)){
			return builder.setResult(false).build();
		}
		
		Map<String, Object> resMap = JsonUtil.parse(result);
		if(resMap == null){
			return builder.setResult(false).build();
		}
		
		if("0".equals(String.valueOf(resMap.get("code")))){
			return builder.setResult(true).build();
		}
		
		return builder.setResult(false).build();
	}
	
	private static byte[] hexString2bytes(String hexString) {
	    if (hexString == null || hexString.length() % 2 != 0)
	        return null;
	    byte[] bytes = new byte[16];
	    int j = 0;
	    for (int i = 0; i < hexString.length(); i+=2) {
	    	String temp =  hexString.substring(i, i + 2);
	    	bytes[j] = (byte) Integer.parseInt(temp, 16);
	        j++;
	    }
	    return bytes;
	}
	
	/**
	 * 读取流
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		if ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		return outSteam.toByteArray();
	}
	
	/**
	 * 打印 渠道用户登录认证结果
	 * @param channel 渠道
	 * @param result 认证结果（true、false）
	 */
	private static void logChannelValidate(String channel, boolean result) {
		if(!openLogValidate()){
			return;
		}
		
		StringBuilder sb = new StringBuilder(50);
		sb.append("user validate res:").append(result ? Constants.SUCCESS : Constants.FAIL)
				.append(", channel:" + channel);
		logger.info(sb.toString());
	}
	
	private static String getChannelName(Object object) {
		if (object == null) {
			return "null object";
		}
		return object.getClass().getSimpleName();
	}
	
	/**
	 * 是否开启log
	 * @return boolean
	 */
	private static boolean openLogValidate(){
		return BooleanUtils.toBoolean(Constants.LOG_CHANNEL_VALIDATE);
	}
}

class RecvPSData
{
	private int len;
	private int command;
	private int status;
	private String username;
	private long userid;
	
	public static int MiniObjectSize = 3*4;
	

	public RecvPSData() {
		this.len = 0;
		this.command = 0;
		this.status = 0;
		this.username = null;
		this.userid = 0;
	}
	
	
	public RecvPSData(int len, int command, int status, String username,
			long userid) {
		super();
		this.len = len;
		this.command = command;
		this.status = status;
		this.username = username;
		this.userid = userid;
	}
	
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	
}
