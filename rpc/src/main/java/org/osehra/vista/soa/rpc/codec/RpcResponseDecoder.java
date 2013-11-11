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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.osehra.vista.soa.rpc.RpcConstants;
import org.osehra.vista.soa.rpc.RpcResponse;


public class RpcResponseDecoder extends ReplayingDecoder<RpcResponseDecoder.State> {

    RpcResponse message = new RpcResponse();

    public RpcResponseDecoder() {
        super(State.READ_PREFIX);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {
        switch (state) {
        case READ_PREFIX: {
            message = new RpcResponse();
            message.setCode(buffer.readByte());
            int start = buffer.readerIndex();
            int stop = start + actualReadableBytes();
            for (int i = start; i < stop; i++) {
                if (buffer.getByte(i) != RpcConstants.FRAME_START) {
                    buffer.skipBytes(i - start);
                    break;
                }
            }
            checkpoint(State.READ_CONTENT);
        }
        case READ_CONTENT: {
            boolean eot = false;
            while (!eot) {
                RpcResponse.Line line = new RpcResponse.Line();
                eot = readLine(buffer, line, RpcConstants.MAX_FRAME_LEN);
                message.appendLine(line);
                checkpoint();
            }
            checkpoint(State.READ_PREFIX);
            break;
        }
        default:
            // Should not get here, all cases are handled
            throw new CorruptedFrameException();
        }

        return message;
    }

    private static boolean readLine(ChannelBuffer buffer, RpcResponse.Line line, int maxLength) throws TooLongFrameException {
        boolean quote = false;
        StringBuilder sb = new StringBuilder(RpcConstants.DEF_FRAME_LEN);

        int ll = 0;
        while (true) {
            byte nextByte = buffer.readByte();
            if (nextByte == RpcConstants.FRAME_STOP) {
                flushField(sb, line);
                return true;
            } else if (nextByte == '\r') { // CRLF
                nextByte = buffer.readByte();
                if (nextByte == '\n') {
                    flushField(sb, line);
                    return false;
                }
            } else if (nextByte == RpcConstants.FIELD_DELIM && !quote) {
                flushField(sb, line);
            } else {
                if (ll >= maxLength) {
                    throw new TooLongFrameException(
                        "VistA rpc frame larger than " + maxLength + " bytes.");
                }
                if (nextByte == '\'') {
                    quote = !quote;
                }
                ll++;
                sb.append((char)nextByte);
            }
        }
    }
    
    private static void flushField(StringBuilder sb, RpcResponse.Line line) {
        line.add(sb.toString());
        sb.setLength(0);
    }

    enum State {
        READ_PREFIX,
        READ_CONTENT
    }

}

