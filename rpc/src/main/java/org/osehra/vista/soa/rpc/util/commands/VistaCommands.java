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

package org.osehra.vista.soa.rpc.util.commands;

import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.util.RpcCommandLibrary;


public class VistaCommands extends RpcCommandLibrary {

    public static VistaCommands vista() {
        return new VistaCommands();
    }

    public RpcRequest connect(String ipaddress, String hostname) {
        return request()
            .code("10304")
            .version(null)
            .name("TCPConnect")
            .parameter(literal(ipaddress))
            .parameter(literal("0"))
            .parameter(literal(hostname));
    }
    public RpcRequest disconnect() {
        return request()
            .code("10304")
            .version(null)
            .name("#BYE#");    
    }

    public RpcRequest login(String access, String verify) {
        return request()
            .name("XUS AV CODE")
            .parameter(literal(one(new StringBuffer()
                .append(access).append(";").append(verify).toString())));
    }
    public RpcRequest signonSetup() {
        return request()
            .name("XUS SIGNON SETUP");
    }
    public RpcRequest context(String context) {
        return request()
            .name("XWB CREATE CONTEXT")
            .parameter(literal(one(context)));
    }

}

