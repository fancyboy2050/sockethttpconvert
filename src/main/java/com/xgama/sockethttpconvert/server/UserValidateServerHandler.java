package com.xgama.sockethttpconvert.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;
import com.xgama.sockethttpconvert.pb.UserValidateClass.GooglePay;
import com.xgama.sockethttpconvert.service.UserValidateService;

public class UserValidateServerHandler extends SimpleChannelHandler{
	
	private final static Logger logger = LoggerFactory.getLogger(UserValidateServerHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		logger.info("message received:" + e.getMessage());
		Channel channel = e.getChannel();
		
		//GooglePay 请求转发 做特殊处理，无返回值
		if(e.getMessage() instanceof GooglePay){
			UserValidateService.googlePayForward((GooglePay)e.getMessage());
			return;
		}
		
		MessageLite messageLite = UserValidateService.validate(e.getMessage());
		channel.write(messageLite);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.error("ExceptionCaught", e.getCause());
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		logger.info("channelConnected : "+ctx.getChannel().getRemoteAddress().toString());
	}
}
