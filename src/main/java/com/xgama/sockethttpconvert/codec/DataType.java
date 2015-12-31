package com.xgama.sockethttpconvert.codec;

import java.util.HashMap;

import com.google.protobuf.MessageLite;
import com.xgama.sockethttpconvert.pb.ChargeClass;
import com.xgama.sockethttpconvert.pb.HeartbeatClass;
import com.xgama.sockethttpconvert.pb.TypeClass.Type;
import com.xgama.sockethttpconvert.pb.UserValidateClass;

public class DataType {

	private static HashMap<Integer, ProtobufCommonDecoder> decoderMap = new HashMap<Integer, ProtobufCommonDecoder>();

	static {
		decoderMap.put(Type.LAOHU_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.LaoHuUser.getDefaultInstance()));
		decoderMap.put(Type.JY_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.JYUser.getDefaultInstance()));
		decoderMap.put(Type.DL_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.DLUser.getDefaultInstance()));
		decoderMap.put(Type.QH_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.QHUser.getDefaultInstance()));
		decoderMap.put(Type.PP_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.PPUser.getDefaultInstance()));
		decoderMap.put(Type.UC_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.UCUser.getDefaultInstance()));
		decoderMap.put(Type.XM_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.XMUser.getDefaultInstance()));
		decoderMap.put(Type.WDJ_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.WDJUser.getDefaultInstance()));
		decoderMap.put(Type.TBT_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.TBTUser.getDefaultInstance()));
		decoderMap.put(Type.BDDK_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.BDDKUser.getDefaultInstance()));
		decoderMap.put(Type.AZ_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.AZUser.getDefaultInstance()));
		decoderMap.put(Type.KY_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.KYUser.getDefaultInstance()));
		decoderMap.put(Type.ONESDK_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.OnesdkUser.getDefaultInstance()));
		decoderMap.put(Type.ITOOLS_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.IToolsUser.getDefaultInstance()));
		decoderMap.put(Type.USER_VALIDATE_RESULT.getNumber(), new ProtobufCommonDecoder(UserValidateClass.ValidateResult.getDefaultInstance()));
		// 充值type，对象关联
		decoderMap.put(Type.LAOHU_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.LaoHuCharge.getDefaultInstance()));
		decoderMap.put(Type.JY_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.JYCharge.getDefaultInstance()));
		decoderMap.put(Type.DL_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.DLCharge.getDefaultInstance()));
		decoderMap.put(Type.PP_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.PPCharge.getDefaultInstance()));
		decoderMap.put(Type.UC_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.UCCharge.getDefaultInstance()));
		decoderMap.put(Type.XM_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.XMCharge.getDefaultInstance()));
		decoderMap.put(Type.QH_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.QHCharge.getDefaultInstance()));
		decoderMap.put(Type.WDJ_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.WDJCharge.getDefaultInstance()));
		decoderMap.put(Type.TBT_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.TBTCharge.getDefaultInstance()));
		decoderMap.put(Type.BDDK_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.BDDKCharge.getDefaultInstance()));
		decoderMap.put(Type.AZ_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.AZCharge.getDefaultInstance()));
		decoderMap.put(Type.TM_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.TMCharge.getDefaultInstance()));
		decoderMap.put(Type.KY_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.KYCharge.getDefaultInstance()));
		decoderMap.put(Type.ITOOLS_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.IToolsCharge.getDefaultInstance()));
		decoderMap.put(Type.YY_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.YYCharge.getDefaultInstance()));
		decoderMap.put(Type.ONESDK_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.OnesdkCharge.getDefaultInstance()));
		decoderMap.put(Type.CHARGE_RESULT.getNumber(), new ProtobufCommonDecoder(ChargeClass.Result.getDefaultInstance()));
		decoderMap.put(Type.QH_GET_USER.getNumber(), new ProtobufCommonDecoder(UserValidateClass.QHGetUser.getDefaultInstance()));
		decoderMap.put(Type.QH_REFRESH_TOKEN.getNumber(), new ProtobufCommonDecoder(UserValidateClass.QHRefreshToken.getDefaultInstance()));
		decoderMap.put(Type.QH_GET_USER_RESULT.getNumber(), new ProtobufCommonDecoder(UserValidateClass.QHGetUserResult.getDefaultInstance()));
		decoderMap.put(Type.QH_REFRESH_TOKEN_RESULT.getNumber(), new ProtobufCommonDecoder(UserValidateClass.QHRefreshTokenResult.getDefaultInstance()));
		decoderMap.put(Type.GOOGLE_PAY.getNumber(), new ProtobufCommonDecoder(UserValidateClass.GooglePay.getDefaultInstance()));
		decoderMap.put(Type.GOOGLE_PAY_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.GooglePayCharge.getDefaultInstance()));
		decoderMap.put(Type.APPLE_CHARGE.getNumber(), new ProtobufCommonDecoder(ChargeClass.AppleCharge.getDefaultInstance()));
		decoderMap.put(Type.APPLE_CHARGE_RESULT.getNumber(), new ProtobufCommonDecoder(ChargeClass.AppleChargeResult.getDefaultInstance()));
		//心跳
		decoderMap.put(Type.HEARTBEAT.getNumber(), new ProtobufCommonDecoder(HeartbeatClass.Heartbeat.getDefaultInstance()));

	}

	public static ProtobufCommonDecoder getDecoder(int type) {
		return decoderMap.get(type);
	}

	public static int getType(MessageLite messageLite) {

		if (messageLite instanceof UserValidateClass.LaoHuUser) {
			return Type.LAOHU_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.JYUser) {
			return Type.JY_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.DLUser) {
			return Type.DL_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.QHUser) {
			return Type.QH_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.PPUser) {
			return Type.PP_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.UCUser) {
			return Type.UC_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.XMUser) {
			return Type.XM_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.WDJUser) {
			return Type.WDJ_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.TBTUser) {
			return Type.TBT_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.BDDKUser) {
			return Type.BDDK_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.AZUser) {
			return Type.AZ_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.KYUser) {
			return Type.KY_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.IToolsUser) {
			return Type.ITOOLS_USER.getNumber();
		}

		if (messageLite instanceof UserValidateClass.QHRefreshTokenResult) {
			return Type.QH_REFRESH_TOKEN_RESULT.getNumber();
		}

		if (messageLite instanceof UserValidateClass.QHGetUserResult) {
			return Type.QH_GET_USER_RESULT.getNumber();
		}

		if (messageLite instanceof UserValidateClass.ValidateResult) {
			return Type.USER_VALIDATE_RESULT.getNumber();
		}

		if(messageLite instanceof UserValidateClass.OnesdkUser){
			return Type.ONESDK_USER.getNumber();
		}
		
		if (messageLite instanceof ChargeClass.LaoHuCharge) {
			return Type.LAOHU_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.JYCharge) {
			return Type.JY_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.DLCharge) {
			return Type.DL_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.PPCharge) {
			return Type.PP_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.UCCharge) {
			return Type.UC_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.XMCharge) {
			return Type.XM_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.QHCharge) {
			return Type.QH_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.WDJCharge) {
			return Type.WDJ_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.TBTCharge) {
			return Type.TBT_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.BDDKCharge) {
			return Type.BDDK_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.AZCharge) {
			return Type.AZ_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.TMCharge) {
			return Type.TM_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.KYCharge) {
			return Type.KY_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.IToolsCharge) {
			return Type.ITOOLS_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.YYCharge) {
			return Type.YY_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.Result) {
			return Type.CHARGE_RESULT.getNumber();
		}

		if (messageLite instanceof ChargeClass.AppleCharge) {
			return Type.APPLE_CHARGE.getNumber();
		}

		if (messageLite instanceof ChargeClass.AppleChargeResult) {
			return Type.APPLE_CHARGE_RESULT.getNumber();
		}

		if (messageLite instanceof UserValidateClass.GooglePay) {
			return Type.GOOGLE_PAY.getNumber();
		}

		if (messageLite instanceof ChargeClass.GooglePayCharge) {
			return Type.GOOGLE_PAY_CHARGE.getNumber();
		}
		
		if (messageLite instanceof HeartbeatClass.Heartbeat) {
			return Type.HEARTBEAT.getNumber();
		}

		if(messageLite instanceof ChargeClass.OnesdkCharge){
			return Type.ONESDK_CHARGE.getNumber();
		}
		return 0;
	}

}
