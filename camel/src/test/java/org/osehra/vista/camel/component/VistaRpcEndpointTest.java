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

package org.osehra.vista.camel.component;

import java.io.ByteArrayInputStream;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.NettyEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.util.RecordPlayerExecutor;

import static org.osehra.vista.soa.rpc.util.commands.VistaCommands.vista;


public class VistaRpcEndpointTest extends CamelTestSupport {
    private static RecordPlayerExecutor executor;
    static {
        String content = ""
            + "+--------+-------------------------------------------------+----------------+\n"
            + "|00000000| 00 00 31 04                                     |..1.            |\n"
            + "+--------+-------------------------------------------------+----------------+\n";
        executor = new RecordPlayerExecutor(new ByteArrayInputStream(content.getBytes()));
    }

    @Test
    public void testVistaEndpointType() throws Exception {
        Endpoint vista = this.getMandatoryEndpoint("vista://localhost:9200");
        assertTrue(vista instanceof NettyEndpoint);

        RpcResponse reply = template.requestBody("vista://localhost:9200", vista().connect("192.168.1.100", "vista.example.org"), RpcResponse.class);
        assertNotNull(reply);
        assertEquals("1", reply.getField(0, 0));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("vista://localhost:9200").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getOut().setBody(executor.getResponses().get(0));
                    }}).to("mock:result");
            }
        };
    }

}
