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

package org.osehra.vista.soa.rpc.codec;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import org.osehra.vista.soa.rpc.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcClientHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOG = LoggerFactory.getLogger(RpcClientHandler.class);

    private final BlockingQueue<RpcResponse> replies = new LinkedBlockingQueue<RpcResponse>();
    protected volatile Channel channel;
    
    public BlockingQueue<RpcResponse> getReplies() {
        return replies;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            LOG.debug(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        LOG.debug("Channel connected");
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOG.debug("Channel open");
        channel = e.getChannel();
        super.channelOpen(ctx, e);
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) {
        LOG.debug("message received '{}'", e.getMessage());
/*
        // Offer the answer after closing the connection.
        e.getChannel().close().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                storeResponse(e);
            }
        });
*/
        storeResponse(e);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        LOG.warn("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }

    private void storeResponse(final MessageEvent e) {
        replies.offer((RpcResponse)e.getMessage());
    }
}

