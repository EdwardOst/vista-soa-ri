/*
 * Copyright 2012-2013 The Open Source Electronic Health Record Agent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osehra.vista.camel.rpc.service;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.osehra.vista.soa.rpc.RpcConstants;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.VistaExecutor;
import org.osehra.vista.soa.rpc.codec.RpcClientHandler;
import org.osehra.vista.soa.rpc.codec.RpcClientPipelineFactory;
import org.osehra.vista.soa.rpc.service.VistaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VistaServiceTestSupport {
    protected final static Logger LOG = LoggerFactory.getLogger(VistaServiceTestSupport.class);
    protected int port = VistaServer.DEFAULT_PORT;
    protected VistaServer server;
    protected Channel client;

    @Before
    public void setUp() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        createServer();
        createClient();
    }
    @After
    public void tearDown() {
        if (server != null) {
            LOG.debug("Shutting down VistA test server");
            server.completed();
        }
    }

    protected abstract VistaExecutor getExecutor();

    protected void runServer(final VistaServer server) {
        if (server != null) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    server.run();
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    protected void createServer() {
        createServer(port);
    }

    protected void createServer(int port) {
        server = new VistaServer().setPort(port).setExecutor(getExecutor());
        LOG.debug("Starting VistA test server on port {}", port);
        runServer(server);
    }

    protected void createClient() {
        client = createClientChannel(RpcConstants.DEFAULT_HOST, RpcConstants.DEFAULT_PORT);
    }

    protected Channel createClientChannel(String host, int port) {
        LOG.debug("Creating VistA test client for {}:{}", host, port);
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new RpcClientPipelineFactory());
        return bootstrap.connect(new InetSocketAddress(host, port))
            .awaitUninterruptibly().getChannel();
    }

    protected RpcResponse call(RpcRequest request) throws Exception {
        return call(client, request);
    }

    protected RpcResponse call(Channel channel, RpcRequest request) throws Exception {
        ChannelFuture response = channel.write(request);
        Channel ch = response.awaitUninterruptibly().getChannel();
        RpcClientHandler handler= ch.getPipeline().get(RpcClientHandler.class);
        return handler.getReplies().take();
    }
}

