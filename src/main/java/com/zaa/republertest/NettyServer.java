package com.zaa.republertest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap server;
    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;
    private Channel serverChannel;
    @PostConstruct
    public void start() throws Exception {
        serverChannel = server.bind(tcpPort).sync().channel().closeFuture().sync().channel();
    }
    @PreDestroy
    public void stop() {
        serverChannel.close();
    }
        
    public ServerBootstrap getServer() {
        return server;
    }

    public void setServer(ServerBootstrap server) {
        this.server = server;
    }

    public InetSocketAddress getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(InetSocketAddress tcpPort) {
        this.tcpPort = tcpPort;
    }        
        
}
