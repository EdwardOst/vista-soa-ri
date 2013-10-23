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

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.VistaExecutor;
import org.osehra.vista.soa.rpc.util.RecordPlayerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.osehra.vista.soa.rpc.util.commands.VistaCommands.vista;


public class VistaConnectionTest extends VistaServiceTestSupport {
    protected final static Logger LOG = LoggerFactory.getLogger(VistaConnectionTest.class);

    @Override
    protected VistaExecutor getExecutor() {
        String content = ""
                + "+--------+-------------------------------------------------+----------------+\n"
                + "|00000000| 00 00 61 63 63 65 70 74 04                      |..accept.       |\n"
                + "+--------+-------------------------------------------------+----------------+\n";
        return new RecordPlayerExecutor(new ByteArrayInputStream(content.getBytes()));
    }

    @Before
    public void setupClient() {
        createClient();
    }

    @Test
    public void testConnect() throws Exception {
        RpcResponse reply = call(vista().connect("192.168.1.100", "vista.example.org"));
        Assert.assertNotNull(reply);
    }

}

