package com.xgama.sockethttpconvert.controller;

import java.util.concurrent.CopyOnWriteArraySet;

import com.xgama.sockethttpconvert.pb.ChargeClass.Result;

public class DataCache {
	
	public final static  CopyOnWriteArraySet<String> COPY_ON_WRITE_ARRAY_SET = new CopyOnWriteArraySet<String>();
	
	/**
	 * 处理成功订单进入本地缓存
	 * @param res
	 */
	public static void add(Result res){
		if(res != null && res.getResult() == 1){
			COPY_ON_WRITE_ARRAY_SET.add(res.getAppOrder());
		}
	}
	
	/**
	 * 检查本地缓存是否已经保存有处理结果
	 * @param appOrder
	 * @return
	 */
	public static boolean checkCache(String appOrder){
		if(COPY_ON_WRITE_ARRAY_SET.contains(appOrder)){
			COPY_ON_WRITE_ARRAY_SET.remove(appOrder);
			return true;
		} else {
			return false;
		}
	}

}
