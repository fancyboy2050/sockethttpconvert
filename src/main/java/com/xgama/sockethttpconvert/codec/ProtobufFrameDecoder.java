package com.xgama.sockethttpconvert.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;

public class ProtobufFrameDecoder extends FrameDecoder{
	private final static Logger logger = LoggerFactory.getLogger(ProtobufFrameDecoder.class);

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
			throws Exception {
		logger.info("decode receive data size:" + buffer.readableBytes());
		
        if (buffer.readableBytes() < 7) {
            return null;
        }

		int dataLength = buffer.getInt(buffer.readerIndex());
	    if (dataLength < 0) {
		      throw new CorruptedFrameException("negative length: " + dataLength);
		}
	    
        if (buffer.readableBytes() < dataLength) {
            return null;
        }
        
        buffer.skipBytes(4);
        int dataType = buffer.readShort();
		ProtobufCommonDecoder decoder = DataType.getDecoder(dataType);
		
		if(decoder == null){
			throw new DecodeException("ProtobufCommonDecoder not exists , data type is " + dataType);
		}
		try{
			MessageLite messageLite = decoder.invokeDecode(ctx, channel, buffer.readBytes(dataLength-6));
			return messageLite;
		}catch(Exception e){
			throw new DecodeException("ProtobufCommonDecoder invokeDecode error ", e);
		}
	}

}
