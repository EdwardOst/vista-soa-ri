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

package org.osehra.vista.camel.rpc.util;


import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;

import org.junit.Assert;
import org.junit.Test;

import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.codec.RpcRequestDecoder;
import org.osehra.vista.soa.rpc.codec.RpcResponseDecoder;
import org.osehra.vista.soa.rpc.util.NettyLogLineParser;
import org.osehra.vista.soa.rpc.util.TextParser;


public class NettyBufferParseTest {

    @Test
    public void testParseRequest() throws Exception {
        String content = ""
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|\n"
            + "|00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|\n"
            + "|00000020| 38 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |8.1.100f00010f00|\n"
            + "|00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|\n"
            + "|00000040| 6f 72 67 66 04                                  |orgf.           |\n"
            + "+--------+-------------------------------------------------+----------------+\n";

        commonRequestParseTest(content);
    }

    @Test
    public void testParseRequestNoHeader() throws Exception {
        String content = ""
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|\n"
            + "|00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|\n"
            + "|00000020| 38 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |8.1.100f00010f00|\n"
            + "|00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|\n"
            + "|00000040| 6f 72 67 66 04                                  |orgf.           |\n"
            + "+--------+-------------------------------------------------+----------------+\n";

        commonRequestParseTest(content);
    }

    public void commonRequestParseTest(String content) throws Exception {
        NettyLogLineParser logParser = new NettyLogLineParser();
        new TextParser(logParser).parse(new ByteArrayInputStream(content.getBytes()));

        Assert.assertEquals(1, logParser.getEntries().size());
        byte[] buffer = logParser.getEntries().get(0);
        Assert.assertEquals(69, buffer.length);

        DecoderEmbedder<RpcRequest> e = new DecoderEmbedder<RpcRequest>(new RpcRequestDecoder());
        e.offer(ChannelBuffers.copiedBuffer(buffer));
        RpcRequest result = e.poll();
        Assert.assertNotNull(result);
    }

    @Test
    public void testParseRequests() throws Exception {
        String content = ""
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|\n"
            + "|00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|\n"
            + "|00000020| 38 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |8.1.100f00010f00|\n"
            + "|00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|\n"
            + "|00000040| 6f 72 67 66 04                                  |orgf.           |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "\n"
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 5b 58 57 42 5d 31 31 33 30 32 01 30 10 58 55 53 |[XWB]11302.0.XUS|\n"
            + "|00000010| 20 53 49 47 4e 4f 4e 20 53 45 54 55 50 35 30 30 | SIGNON SETUP500|\n"
            + "|00000020| 34 36 27 5b 4d 52 4d 31 4d 36 52 4d 74 31 36 2b |46'[MRM1M6RMt16+|\n"
            + "|00000030| 2b 62 3e 31 52 4d 36 31 74 6d 36 74 6d 31 52 36 |+b>1RM61tm6tm1R6|\n"
            + "|00000040| 4d 36 52 61 74 76 74 62 76 31 4d 52 6d 6d 31 24 |M6Ratvtbv1MRmm1$|\n"
            + "|00000050| 66 04                                           |f.              |\n"
            + "+--------+-------------------------------------------------+----------------+\n";

        NettyLogLineParser logParser = new NettyLogLineParser();
        new TextParser(logParser).parse(new ByteArrayInputStream(content.getBytes()));

        Assert.assertEquals(2, logParser.getEntries().size());
        for (byte[] entry : logParser.getEntries()) {
            DecoderEmbedder<RpcRequest> e = new DecoderEmbedder<RpcRequest>(new RpcRequestDecoder());
            e.offer(ChannelBuffers.copiedBuffer(entry));
            RpcRequest result = e.poll();
            Assert.assertNotNull(result);
        }
    }

    @Test
    public void testParseResponses() throws Exception {
        String content = ""
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 61 63 63 65 70 74 04                      |..accept.       |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 31 04                                     |..1.            |\n"
            + "+--------+-------------------------------------------------+----------------+\n";

        NettyLogLineParser logParser = new NettyLogLineParser();
        new TextParser(logParser).parse(new ByteArrayInputStream(content.getBytes()));

        Assert.assertEquals(2, logParser.getEntries().size());
        byte[] buffer = logParser.getEntries().get(0);
        Assert.assertEquals(9, buffer.length);

        RpcResponse result;
        DecoderEmbedder<RpcResponse> e = new DecoderEmbedder<RpcResponse>(new RpcResponseDecoder());
        e.offer(ChannelBuffers.copiedBuffer(buffer));
        result = e.poll();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getContent().size());
        Assert.assertEquals(1, result.getContent().get(0).size());
        Assert.assertEquals("accept", result.getField(0, 0));
    }

}

