package com.xgama.sockethttpconvert.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;
import com.xgama.sockethttpconvert.config.Constants;
import com.xgama.sockethttpconvert.controller.DataCache;
import com.xgama.sockethttpconvert.pb.ChargeClass.Result;

public class ChargeClient{
	
	private final static Logger logger = LoggerFactory.getLogger(ChargeClient.class);
	
	private ChannelFuture cf;
	private String key;

	protected static ConcurrentHashMap<String, ArrayBlockingQueue<Object>> responses = 
			new ConcurrentHashMap<String, ArrayBlockingQueue<Object>>();
	
	public ChargeClient(ChannelFuture cf, String key){
		this.cf = cf;
		this.key = key;
	}

	/**
	 * 执行请求
	 * @param req
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public Result invokeSyn(final String appOrder, final MessageLite message, final long timeout) throws Exception{

		long beginTime = System.currentTimeMillis();
		ArrayBlockingQueue<Object> responseQueue = new ArrayBlockingQueue<Object>(1);
		responses.put(appOrder, responseQueue);
		try {
			ChannelFuture channelFuture = cf.getChannel().write(message);
			
			channelFuture.addListener(new ChannelFutureListener(){
				
				 public void operationComplete(ChannelFuture future) throws Exception {
					 if(future.isSuccess()){
						 logger.info("charge invoke success, app order is : "+appOrder);
						 return;
					 } else {
						 if (cf.getChannel().isConnected()) {
							 cf.getChannel().close();
							 logger.info("close connect, app order is : "+appOrder);
						 } 
						 responses.remove(appOrder); 
					 }
				 }
				 
			});
			if(!cf.getChannel().isConnected()){
				logger.info("connect is closed, app order is : "+appOrder);
				ChargeClientFactory.getInstance().removeClient(key);
			}
		} catch (Exception e) {
			responses.remove(appOrder);
			responseQueue = null;
			logger.error("send request to os sendbuffer error", e);
			throw e;
		}
		Object result = null;
		try {
			result = responseQueue.poll(timeout - (System.currentTimeMillis() - beginTime), TimeUnit.MILLISECONDS);
		} catch(Exception e){
			responses.remove(appOrder);
			logger.error("Get response error", e);
			throw new Exception("Get response error", e);
		}
		
		responses.remove(appOrder);
		
		if (result == null) {
			String errorMsg = "receive response timeout(" + timeout + " ms),server is: " + getServerIP() + ":" + getServerPort() + " request app order is:" + appOrder;
			throw new Exception(errorMsg);
		}
		
		return (Result) result;
	}

	/**
	 * 执行请求
	 * @param req
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public void invokeSend(final String appOrder, final MessageLite message) throws Exception {

		try {
			ChannelFuture channelFuture = cf.getChannel().write(message);

			channelFuture.addListener(new ChannelFutureListener() {

				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						logger.info("charge send success, app order is : " + appOrder);
						return;
					} else {
						if (cf.getChannel().isConnected()) {
							cf.getChannel().close();
							logger.info("charge send maybe error, close connect, app order is : " + appOrder);
						}
					}
				}

			});

			if (!cf.getChannel().isConnected()) {
				logger.info("connect is closed, app order is : " + appOrder);
				ChargeClientFactory.getInstance().removeClient(key);
			}
		} catch (Exception e) {
			logger.error("send request to os sendbuffer error", e);
			throw e;
		}

	}
	
	
	/**
	 * 处理响应结果
	 * @param res
	 * @throws Exception
	 */
	public void putResponse(Result res) throws Exception{
		String appOrder = res.getAppOrder();
		//忽略心跳包结果，不放入responses中
		if(appOrder != null && appOrder.startsWith(Constants.HEARTBEAT_PACKET_PREFIX)){
			logger.info("receive heartbeat packet,request appOrder is:" + appOrder);
			return;
		}
		if (!responses.containsKey(appOrder)) {
			logger.warn("give up the response,request appOrder is:" + appOrder + ",maybe because timeout!");
			DataCache.add(res);
			logger.info("Put result to local cache,request appOrder is:" + appOrder +", cache obj : "+res);
			return;
		}
		try {
			ArrayBlockingQueue<Object> queue = responses.get(res.getAppOrder());
			if (queue != null) {
				queue.put(res);
			} else {
				logger.warn("give up the response,request appOrder is:" + appOrder + ",because queue is null");
			}
		} catch (InterruptedException e) {
			logger.error("put response error,request appOrder is:" + appOrder, e);
		}
	}

	public ChannelFuture getCf() {
		return cf;
	}

	public void setCf(ChannelFuture cf) {
		this.cf = cf;
	}

	public String getServerIP() {
		return ((InetSocketAddress) cf.getChannel().getRemoteAddress())
				.getHostName();
	}

	public int getServerPort() {
		return ((InetSocketAddress) cf.getChannel().getRemoteAddress())
				.getPort();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
