package com.xgama.sockethttpconvert.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;
import com.xgama.sockethttpconvert.codec.ProtobufCustomEncoder;
import com.xgama.sockethttpconvert.codec.ProtobufFrameDecoder;
import com.xgama.sockethttpconvert.pb.ChargeClass.Result;
import com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat;
import com.xgama.sockethttpconvert.util.SecurityUtil;

public class ChargeClientFactory {

	private final static Logger logger = LoggerFactory.getLogger(ChargeClientFactory.class);
	private static ChargeClientFactory _self = new ChargeClientFactory();
	private static Map<String, ChargeClient> chargeClientMap = new HashMap<String, ChargeClient>();
	private static final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());
	private static final ExecutorService executor_service = Executors.newCachedThreadPool();
	private final static long connectTimeout = 5000;
	// 触发读空闲时间（秒）
	private final static int READER_IDLE_TIME_SECONDS = 45;
	// 抛出ReadTimeoutException 时间（秒）
	private final static int TIMEOUT_SECONDS = 55;

	private Timer timer = null;
	// 空闲状态Handler
	private IdleStateHandler idleStateHandler = null;
	// ReadTimeoutHandler
	private ChannelHandler timeoutHandler = null;

	private ChargeClientFactory() {
		timer = new HashedWheelTimer();
		timeoutHandler = new ReadTimeoutHandler(timer, TIMEOUT_SECONDS, TimeUnit.SECONDS);
		idleStateHandler = new IdleStateHandler(timer, READER_IDLE_TIME_SECONDS, 0, 0);
	}

	public synchronized ChargeClient getClient(String targetIP, int targetPort) throws Exception {
		String key = buildkey(targetIP, targetPort);
		ChargeClient cc = chargeClientMap.get(key);
		if (cc == null) {
			cc = createClient(targetIP, targetPort);
			chargeClientMap.put(key, cc);
		}
		return cc;

	}

	public synchronized void removeClient(String key) {
		logger.info("remove client from map, key is : " + key);
		chargeClientMap.remove(key);
	}
	
	private ChargeClient createClient(String targetIP, int targetPort) throws Exception {
		String key = buildkey(targetIP, targetPort);
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		final ChargeClientHandler chargeClientHandler = new ChargeClientHandler();
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() {
				return Channels.pipeline(
						new ProtobufFrameDecoder(), 
						new ProtobufCustomEncoder(),
						chargeClientHandler);
			}
		});
		
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("connectTimeoutMillis", connectTimeout);
		ChannelFuture cf = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));
		cf.awaitUninterruptibly(connectTimeout);
		if (!cf.isDone()) {
			logger.error("Create connection to " + targetIP + ":" + targetPort + " timeout!");
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " timeout!");
		}
		if (cf.isCancelled()) {
			logger.error("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
		}
		if (!cf.isSuccess()) {
			logger.error("Create connection to " + targetIP + ":" + targetPort + " error", cf.getCause());
			throw new Exception("Create connection to " + targetIP + ":" + targetPort + " error", cf.getCause());
		}
		ChargeClient client = new ChargeClient(cf, key);
		chargeClientHandler.setClient(client);
		chargeClientHandler.setKey(key);
		
		//创建Task，探测是否支持心跳
		executor_service.execute(new DetectHeartbeatTask(client));
		return client;
	}

	private String buildkey(String targetIP, int targetPort) {
		return targetIP + ":" + targetPort;
	}

	
	public IdleStateHandler getIdleStateHandler() {
		return idleStateHandler;
	}

	public ChannelHandler getTimeoutHandler() {
		return timeoutHandler;
	}

	public static IdleStateHandler getDefaultIdelStateHandler() {
		return getInstance().getIdleStateHandler();
	}

	public static ChannelHandler getDefaultReadtimeoutHandler() {
		return getInstance().getTimeoutHandler();
	}
	
	public static ChargeClientFactory getInstance() {
		return _self;
	}
	
	public static void shutdown(){
		executor_service.shutdown();
		logger.info("shutdown ExecutorService sucess...");
	}
	

	/**
	 * 探测服务器是否支持心跳包
	 * @author xujiahui
	 *
	 */
	public static class DetectHeartbeatTask implements Runnable {

		private ChargeClient chargeClient;

		public DetectHeartbeatTask(ChargeClient chargeClient) {
			this.chargeClient = chargeClient;
		}

		@Override
		public void run() {
			String appOrder = SecurityUtil.getDetectID();
			MessageLite hearbeat = Heartbeat.newBuilder().setAppOrder(appOrder).build();
			try {
				Result result = chargeClient.invokeSyn(appOrder, hearbeat, connectTimeout);

				//支持心跳包
				if (result != null) {
					ChannelFuture channelFuture = chargeClient.getCf();
					ChannelPipeline pipeline = channelFuture.getChannel().getPipeline();
					pipeline.addBefore("2", "readTimeout", getDefaultReadtimeoutHandler());
					pipeline.addBefore("2", "idelStaeHandler", getDefaultIdelStateHandler());
					pipeline.addBefore("2", "hearbeat", new HeartbeatHandler());

					logger.debug(pipeline.toString());

					logger.info("detect heartbeat success --> " + chargeClient.getServerIP() + ":"
							+ chargeClient.getServerPort());

				}
			} catch (Exception e) {
				logger.error("detect heartbeat fail , maybe not support heartbeat --> " + chargeClient.getServerIP()
						+ ":" + chargeClient.getServerPort(), e);
			}
		}

	}
}
