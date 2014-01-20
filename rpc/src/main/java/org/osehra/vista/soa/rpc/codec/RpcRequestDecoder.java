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

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.osehra.vista.soa.rpc.Parameter;
import org.osehra.vista.soa.rpc.RpcConstants;
import org.osehra.vista.soa.rpc.RpcRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcRequestDecoder extends ReplayingDecoder<RpcRequestDecoder.State> {

    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(RpcRequestDecoder.class);

    private String namespace;
    private String code;
    private String version;
    private String name;
    private List<Parameter> params = new ArrayList<Parameter>();

    public RpcRequestDecoder() {
        super(State.READ_NS);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {

        // TODO: improve logging so every byte received is traced
        switch (state) {
        case READ_NS: {
            byte b = buffer.readByte();
            if (b != RpcConstants.NS_START) {
                return readNotification(buffer, RpcConstants.DEF_FRAME_LEN);
            }
            int ridx = buffer.readerIndex();
            int rbytes = actualReadableBytes();
            int pos = buffer.indexOf(ridx, ridx + rbytes, (byte)RpcConstants.NS_STOP);
            if (pos == -1) {
                // Namespace delimiter (']') not found
                if (rbytes > RpcConstants.MAX_NS_LEN) {
                    throw new TooLongFrameException();
                } else {
                    // Wait until more data is received
                    return null;
                }
            }
            int len = pos - ridx;
            if (len > RpcConstants.MAX_NS_LEN) {
                throw new TooLongFrameException();
            }
            namespace = buffer.readBytes(len).toString(RpcCodecUtils.DEF_CHARSET);
            buffer.skipBytes(1);

            checkpoint(State.READ_CODE);
        }
        case READ_CODE: {
            // Code has a fixed size of 5 chars
            code = buffer.readBytes(RpcConstants.CODE_LEN).toString(RpcCodecUtils.DEF_CHARSET);
            checkpoint(State.READ_NAME);
        }
        case READ_NAME: {
            byte b = '\0';
            while (name == null) {
                b = buffer.readByte();
                if (b == '\0') {
                    throw new CorruptedFrameException();
                }
                String s = buffer.readBytes((int)b).toString(RpcCodecUtils.DEF_CHARSET);
                if (version == null && s.charAt(0) >= '0' && s.charAt(0) <= '9') {
                    // TODO: when is version mandatory and when is it optional, if ever?
                    version = s;
                } else {
                    name = s;
                }
            }
            
            checkpoint(State.READ_PARAMS);
        }
        case READ_PARAMS: {
            byte b = buffer.readByte();
            if (b == RpcConstants.FRAME_STOP) {
                // no parameters
                break;
            }
            if (b != RpcConstants.PARAMS_START) {
                throw new CorruptedFrameException("Expected either parameters or end of frame.");
            }

            boolean eot = false;
            while (!eot) {
                Parameter param = RpcCodecUtils.decodeParameter(buffer);
                eot = param == null;
                if (!eot) {
                    params.add(param);
                }
            }
            RpcRequest request = new RpcRequest().namespace(namespace).code(code).name(name).version(version);
            request.getParmeters().addAll(params);

            reset();
            return request;
        }
        default:
            // Should not get here, all cases are handled
            throw new CorruptedFrameException();
        }

        return null;
    }

    private void reset() {
        namespace = null;
        code = null;
        name = null;
        version = null;
        params.clear();
        checkpoint(State.READ_NS);
    }

    private static String readNotification(ChannelBuffer buffer, int maxLength) throws TooLongFrameException {
        StringBuilder sb = new StringBuilder(RpcConstants.DEF_FRAME_LEN);
        for (int i = 0; i < maxLength; i++) {
            byte nextByte = buffer.readByte();
            if (nextByte == '\n') {
                return sb.toString();
            }
            sb.append((char)nextByte);
        }
        throw new TooLongFrameException("VistA rpc frame larger than " + maxLength + " bytes.");
    }

    enum State {
        READ_NS,
        READ_CODE,
        READ_NAME,
        READ_PARAMS
    }

}

