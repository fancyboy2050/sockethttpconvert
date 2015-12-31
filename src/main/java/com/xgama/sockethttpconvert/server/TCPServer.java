package com.xgama.sockethttpconvert.server;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TCPServer {
	
	private final static Logger logger = LoggerFactory.getLogger(TCPServer.class);

	@Autowired
	@Qualifier("serverBootstrap")
	private ServerBootstrap b;
	
	@Autowired
	@Qualifier("tcpSocketAddress")
	private InetSocketAddress tcpPort;

	@Autowired
	@Qualifier("chargeServerBootstrap")
	private ServerBootstrap chargeServer;
	
	@Autowired
	@Qualifier("chargeSocketAddress")
	private InetSocketAddress chargePort;

	private Channel serverChannel;
	
	private Channel chargeServerChannel;

	@PostConstruct
	public void start() throws Exception {
		logger.info("Starting server at " + tcpPort);
		logger.info("Starting charge server at " + chargePort);
		serverChannel = b.bind(tcpPort);
		chargeServerChannel = chargeServer.bind(chargePort);
	}

	@PreDestroy
	public void stop() {
		logger.info("Stop server at " + tcpPort);
		logger.info("Stop charge server at " + chargePort);
		serverChannel.close();
		chargeServerChannel.close();
	}
	
}
