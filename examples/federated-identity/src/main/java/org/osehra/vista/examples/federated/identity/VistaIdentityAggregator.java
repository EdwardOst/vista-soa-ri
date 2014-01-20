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

package org.osehra.vista.examples.federated.identity;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VistaIdentityAggregator implements AggregationStrategy {
    private final static Logger LOG = LoggerFactory.getLogger(VistaIdentityAggregator.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String request = newExchange.getProperty("VistaRequest", String.class);
        RpcResponse response = newExchange.getIn().getBody(RpcResponse.class);
        LOG.info("AGGREGATE results for '{}'", request);

        if (request.equals("XUS GET USER INFO")) {
            if (response != null && response.getContent().size() >= 3) {
                RpcResponse.Line l = response.getContent().get(1);
                l.set(0, "HOUSE,GREGORY");
                l = response.getContent().get(2);
                l.set(0, "Gregory House");
            }
        } else if (request.equals("ORWU USERINFO")) {
            if (response != null && response.getContent().size() >= 1) {
                RpcResponse.Line l = response.getContent().get(0);
                if (l.size() >= 2) {
                    l.set(1, "HOUSE,GREGORY");
                }
            }
        }
        return newExchange;
    }
    
}
