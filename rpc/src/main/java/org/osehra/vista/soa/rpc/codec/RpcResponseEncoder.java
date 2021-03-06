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


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.osehra.vista.soa.rpc.RpcConstants;
import org.osehra.vista.soa.rpc.RpcResponse;


public class RpcResponseEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse response = (RpcResponse)msg;

            ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
            cb.writeByte((byte)RpcConstants.FRAME_START);
            cb.writeByte((byte)RpcConstants.FRAME_START);
            String content = response.getContent().toString();
            cb.writeBytes(content.getBytes(RpcCodecUtils.DEF_CHARSET));
            cb.writeByte((byte)RpcConstants.FRAME_STOP);
            return cb;
        }
        return msg;
    }

}

