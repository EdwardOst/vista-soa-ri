/*
 * Copyright 2012-2014 The Open Source Electronic Health Record Alliance
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

package org.osehra.vista.soa.rpc.service;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osehra.vista.soa.rpc.VistaExecutor;
import org.osehra.vista.soa.rpc.codec.RpcServerPipelineFactory;


public final class VistaServer extends VistaServerSupport {
    public static final int DEFAULT_PORT = 9200;
    private int port = DEFAULT_PORT;
    private VistaExecutor executor;

    private static String usage() {
        return "usage: java VistaServer [port]\n";
    }

    public static void main(String... args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            int p = 0;
            String a = args[0];
            if ("--help".equals(a) || args.length > 1) {
                // will print the help message and exit
            } else {
                try {
                    p = Integer.parseInt(a);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port value: " + a);
                }
            }
            if (p == 0) {
                System.out.println(usage());
                return;
            }
            port = p;
        }
        // TODO: add options for VistaExecutor flavors and configuration
        new VistaServer().setPort(port).run();
    }

    public VistaServer setExecutor(VistaExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    protected void startInternal() throws RuntimeException {
        super.startInternal();

        try {
            startRpcListener();
        } catch (Exception e) {
            LOG.error("Failed to start RPC server", e);
            throw new RuntimeException("Failed to start RPC server", e);
        }
    }
    
    public VistaServer setPort (int port) {
        this.port = port;
        return this;
    }

    protected void startRpcListener() throws Exception {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

        ServerBootstrap bootstrap = new ServerBootstrap(
            new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(createServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        // Start listener to accept incoming connections.
        LOG.info("VistA RPC Broker starting on port {}", port);
        bootstrap.bind(new InetSocketAddress(port));
    }

    private ChannelPipelineFactory createServerPipelineFactory() {
        // TODO: maybe set other options (e.g logging)
        return new RpcServerPipelineFactory(executor);
    }
}
