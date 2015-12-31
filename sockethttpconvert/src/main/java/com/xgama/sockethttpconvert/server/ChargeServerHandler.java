package com.xgama.sockethttpconvert.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.pb.ChargeClass.AppleCharge;
import com.xgama.sockethttpconvert.pb.ChargeClass.AppleChargeResult;
import com.xgama.sockethttpconvert.service.AppleCheckService;

public class ChargeServerHandler extends SimpleChannelHandler{
	
private final static Logger logger = LoggerFactory.getLogger(UserValidateServerHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		AppleCharge appleCharge = (AppleCharge)e.getMessage();
		Channel channel = e.getChannel();
		AppleChargeResult result = AppleCheckService.getAppleReceipt(appleCharge);
		channel.write(result);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.info("ChargeServerHandler ExceptionCaught", e.getCause());
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		logger.info("channelConnected : "+ctx.getChannel().getRemoteAddress().toString());
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		logger.info("channelDisconnected : "+ctx.getChannel().getRemoteAddress().toString());
	}

}
