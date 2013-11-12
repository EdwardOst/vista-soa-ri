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

package org.osehra.vista.examples.federated.identity;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.codec.RpcRequestDecoder;
import org.osehra.vista.soa.rpc.util.commands.VistaCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IdentityProcessor implements Processor {
    private final static Logger LOG = LoggerFactory.getLogger(RpcRequestDecoder.class);
    private String access;
    private String verify;

    public IdentityProcessor(String access, String verify) {
        this.access = access;
        this.verify = verify;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        RpcRequest body = exchange.getIn().getBody(RpcRequest.class);
        if (body != null && body.getName().equals("XUS AV CODE")) {
            LOG.warn("Replacing VistA credentials...");
            exchange.getOut().setBody(VistaCommands.vista().login(access, verify));
        }
    }
    
}
