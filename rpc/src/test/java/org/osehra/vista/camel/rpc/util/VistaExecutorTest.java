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

package org.osehra.vista.camel.rpc.util;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.util.RecordPlayerExecutor;

import static org.osehra.vista.soa.rpc.util.commands.VistaCommands.vista;


public class VistaExecutorTest {

    @Test
    public void testRecordPlayerStringInput() throws Exception {
        String content = ""
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 61 63 63 65 70 74 04                      |..accept.       |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "\n"
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 45 43 32 41 4d 41 5a 2d 39 36 54 51 32 50 |..EC2AMAZ-96TQ2P|\n"
            + "|00000010| 4b 0d 0a 52 4f 55 0d 0a 43 50 4d 0d 0a 2f 2f 2e |K..ROU..CPM..//.|\n"
            + "|00000020| 2f 6e 75 6c 3a 34 36 34 30 0d 0a 35 0d 0a 30 0d |/nul:4640..5..0.|\n"
            + "|00000030| 0a 55 56 41 2e 44 4f 4d 41 49 4e 2e 47 4f 56 0d |.UVA.DOMAIN.GOV.|\n"
            + "|00000040| 0a 30 0d 0a 04                                  |.0...           |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "\n"
            + "         +-------------------------------------------------+\n"
            + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |\n"
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 31 31 39 0d 0a 30 0d 0a 30 0d 0a 0d 0a 30 |..119..0..0....0|\n"
            + "|00000010| 0d 0a 30 0d 0a 0d 0a 47 6f 6f 64 20 61 66 74 65 |..0....Good afte|\n"
            + "|00000020| 72 6e 6f 6f 6e 20 5a 5a 50 52 4f 47 52 41 4d 4d |rnoon ZZPROGRAMM|\n"
            + "|00000030| 45 52 2c 46 49 56 45 0d 0a 20 20 20 20 20 59 6f |ER,FIVE..     Yo|\n"
            + "|00000040| 75 20 6c 61 73 74 20 73 69 67 6e 65 64 20 6f 6e |u last signed on|\n"
            + "|00000050| 20 74 6f 64 61 79 20 61 74 20 31 33 3a 31 36 0d | today at 13:16.|\n"
            + "|00000060| 0a 59 6f 75 20 68 61 76 65 20 31 31 33 20 6e 65 |.You have 113 ne|\n"
            + "|00000070| 77 20 6d 65 73 73 61 67 65 73 2e 20 28 31 31 33 |w messages. (113|\n"
            + "|00000080| 20 69 6e 20 74 68 65 20 27 49 4e 27 20 62 61 73 | in the 'IN' bas|\n"
            + "|00000090| 6b 65 74 29 0d 0a 0d 0a 45 6e 74 65 72 20 27 5e |ket)....Enter '^|\n"
            + "|000000a0| 4e 4d 4c 27 20 74 6f 20 72 65 61 64 20 79 6f 75 |NML' to read you|\n"
            + "|000000b0| 72 20 6e 65 77 20 6d 65 73 73 61 67 65 73 2e 0d |r new messages..|\n"
            + "|000000c0| 0a 59 6f 75 27 76 65 20 67 6f 74 20 50 52 49 4f |.You've got PRIO|\n"
            + "|000000d0| 52 49 54 59 20 6d 61 69 6c 21 0d 0a 04          |RITY mail!...   |\n"
            + "+--------+-------------------------------------------------+----------------+\n";

        playDummyRequest(3, new RecordPlayerExecutor(new ByteArrayInputStream(content.getBytes())));
    }

    @Test
    public void testRecordPlayerFileInput() throws Exception {
        playDummyRequest(3, new RecordPlayerExecutor(new FileInputStream("src/test/resources/RuntimeTest-recordPlayer.txt")));
    }

    protected void playDummyRequest(int count, RecordPlayerExecutor runtime) {
        RpcRequest[] requests = {
            vista().connect("192.168.1.100", "vista.example.org"),
            vista().signonSetup(),
            vista().login("access", "verify")
        };

        RpcResponse response = null;
        Assert.assertEquals(requests.length, runtime.getResponses().size());
        for (RpcRequest request : requests) {
            response = runtime.execute(request);
            Assert.assertNotNull(response);
        }
        Assert.assertEquals(14, response.getContent().size());
        RpcResponse.Line line = response.getContent().get(7);
        Assert.assertEquals("Good afternoon ", line.get(0).substring(0, 15));
    }

}

