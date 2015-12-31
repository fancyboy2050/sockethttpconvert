package com.xgama.sockethttpconvert.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.google.protobuf.MessageLite;

public class ProtobufCustomEncoder extends OneToOneEncoder{

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		
		if (msg instanceof MessageLite) {
			byte[] array = ((MessageLite)msg).toByteArray();
			ChannelBuffer buf = ChannelBuffers.directBuffer(4+2+array.length);
			buf.writeInt(array.length+6);
			buf.writeShort(DataType.getType((MessageLite)msg));
			buf.writeBytes(array);
			return buf;
		}
		
		return msg;
	}

}
