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

package org.osehra.vista.soa.rpc.codec;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.VistaExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcServerHandler extends SimpleChannelUpstreamHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RpcServerHandler.class);
    private VistaExecutor executor;

    public void setExecutor(VistaExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            LOG.debug(e.toString());
        }
        super.handleUpstream(ctx, e);
    }
/*
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOG.debug("Channel {} connected", ctx.toString());
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOG.debug("Channel {} disconnected", ctx.toString());
        super.channelDisconnected(ctx, e);
    }
*/

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Object request = e.getMessage();
        if (request instanceof RpcRequest) {
            LOG.info("RECEIVED request");
            RpcResponse reply = executor.execute((RpcRequest) request);
            e.getChannel().write(reply);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        LOG.warn("Unexpected exception from downstream", e.getCause());
        e.getChannel().close();
    }
}

