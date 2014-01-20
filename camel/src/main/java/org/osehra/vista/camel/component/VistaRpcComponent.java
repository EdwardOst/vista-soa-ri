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

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.component.netty.ClientPipelineFactory;
import org.apache.camel.component.netty.NettyComponent;
import org.apache.camel.component.netty.ServerPipelineFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;


public class VistaRpcComponent extends NettyComponent {
    private static final String TCP_PROTOCOL = "tcp://";
    private ClientPipelineFactory clientPipelineFactory;
    private ServerPipelineFactory serverPipelineFactory;

    public VistaRpcComponent() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        clientPipelineFactory = new RpcClientPipelineFactory(null);
        serverPipelineFactory = new RpcServerPipelineFactory(null);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(parameters);
        params.put("clientPipelineFactory", clientPipelineFactory);
        params.put("serverPipelineFactory", serverPipelineFactory);
        return super.createEndpoint(uri, TCP_PROTOCOL + remaining, params);
    }

    // TODO: There is a bug in camel-netty that causes the configuration to be overwritten in
    // NettyConfiguration.parseURI, by providing new default values instead of the pre-configured 
    // values. This is a temporary workaround until that issue is fixed.
    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolveAndRemoveReferenceParameter(Map<String, Object> parameters, String key, Class<T> type, T defaultValue) {
        return (T)("clientPipelineFactory".equals(key) ? clientPipelineFactory 
            : "serverPipelineFactory".equals(key) ? serverPipelineFactory
            : super.resolveAndRemoveReferenceParameter(parameters, key, type, defaultValue));
    }
}
