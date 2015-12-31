package com.xgama.sockethttpconvert.client;

import java.io.IOException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.pb.ChargeClass.Result;

public class ChargeClientHandler extends SimpleChannelHandler{
	
	private final static Logger logger = LoggerFactory.getLogger(ChargeClientHandler.class);
	
	private ChargeClient client;
	
	private String key;
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if(e.getMessage() instanceof Result){
			Result result = (Result)e.getMessage();
			client.putResponse(result);
		} else {
			logger.error("receive message error,only support com.projectx.web.pb.ChargeClass.Result");
			throw new Exception("receive message error,only support com.projectx.web.pb.ChargeClass.Result");
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		if(!(e.getCause() instanceof IOException)){
			logger.error("catch some exception not IOException",e.getCause());
			
			//ReadTimeout异常
			if(e.getCause() instanceof ReadTimeoutException){
				logger.info("catch ReadTimeoutException , connection closed: " + ctx.getChannel().getRemoteAddress());
				ChargeClientFactory.getInstance().removeClient(key);
				
				//关闭channel
				safeCloseChannel(ctx.getChannel());
			}
		}
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		logger.info("connection connected remote address : "+ctx.getChannel().getRemoteAddress());
		logger.info("connection connected local address : "+ctx.getChannel().getLocalAddress());
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		logger.warn("connection closed: "+ctx.getChannel().getRemoteAddress());
		ChargeClientFactory.getInstance().removeClient(key);
	}

	public static void safeCloseChannel(Channel channel) {
		if (null == channel) {
			return;
		}
		try {
			channel.close();
		} catch (Exception e) {
			logger.error("safeCloseChannel error", e.getCause());
		}
	}
	
	public ChargeClient getClient() {
		return client;
	}

	public void setClient(ChargeClient client) {
		this.client = client;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
