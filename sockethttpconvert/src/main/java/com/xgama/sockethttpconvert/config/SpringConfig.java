package com.xgama.sockethttpconvert.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.xgama.sockethttpconvert.server.ChargePipelineFactory;
import com.xgama.sockethttpconvert.server.UserValidatePipelineFactory;

@Configuration
@PropertySource("classpath:netty-server.properties")
public class SpringConfig {
	
	@Value("${tcp.port}")
	private int tcpPort;
	
	@Value("${tcp.charge.port}")
	private int tcpChargePort;
	
	@Value("${boss.thread.count}")
	private int bossThreadCount;
	
	@Value("${worker.thread.count}")
	private int workerThreadCount;

	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap() {
		MemoryAwareThreadPoolExecutor eventExecutor = new MemoryAwareThreadPoolExecutor(200, 1048576, 1048576);
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(bossThreadCount), Executors.newFixedThreadPool(workerThreadCount));
		ServerBootstrap b = new ServerBootstrap(factory);
		b.setPipelineFactory(new UserValidatePipelineFactory(eventExecutor));
		b.setOption("child.tcpNoDelay", true);
		b.setOption("child.keepAlive", true);
		b.setOption("reuseAddress", true);
		return b;
	}

	@Bean(name = "chargeServerBootstrap")
	public ServerBootstrap chargeBootstrap() {
		MemoryAwareThreadPoolExecutor eventExecutor = new MemoryAwareThreadPoolExecutor(100, 1048576, 1048576);
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		ServerBootstrap b = new ServerBootstrap(factory);
		b.setPipelineFactory(new ChargePipelineFactory(eventExecutor));
		b.setOption("child.tcpNoDelay", true);
		b.setOption("child.keepAlive", true);
		b.setOption("reuseAddress", true);
		return b;
	}
	
	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}
	
	@Bean(name = "chargeSocketAddress")
	public InetSocketAddress tcpChargePort() {
		return new InetSocketAddress(tcpChargePort);
	}

	/**
	 * Necessary to make the Value annotations work.
	 * 
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	


}
