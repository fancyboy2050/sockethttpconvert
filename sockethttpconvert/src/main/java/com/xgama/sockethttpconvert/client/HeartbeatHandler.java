package com.xgama.sockethttpconvert.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat;
import com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.Builder;
import com.xgama.sockethttpconvert.util.SecurityUtil;

/**
 * 心跳包Handler
 */
public class HeartbeatHandler extends IdleStateAwareChannelHandler {

	private final static Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {

		// 读 操作空闲-发送心跳包
		if (e.getState() == IdleState.READER_IDLE) {

			Builder builder = Heartbeat.newBuilder();
			Heartbeat heartbeat = builder.setAppOrder(SecurityUtil.getHeartbeatID()).build();
			e.getChannel().write(heartbeat);

			logger.info("send heart beat message :" + heartbeat);
			return;
		}

		super.channelIdle(ctx, e);
	}

}
