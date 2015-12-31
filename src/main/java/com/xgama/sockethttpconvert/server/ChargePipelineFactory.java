package com.xgama.sockethttpconvert.server;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.xgama.sockethttpconvert.codec.ProtobufCustomEncoder;
import com.xgama.sockethttpconvert.codec.ProtobufFrameDecoder;

public class ChargePipelineFactory  implements ChannelPipelineFactory{
	
	private final ExecutionHandler executionHandler;
	
	public ChargePipelineFactory(Executor eventExecutor) {
        executionHandler = new ExecutionHandler(eventExecutor);
    }

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(new ProtobufFrameDecoder(), 
				new ProtobufCustomEncoder(),
				executionHandler,
				new ChargeServerHandler());
	}

}
