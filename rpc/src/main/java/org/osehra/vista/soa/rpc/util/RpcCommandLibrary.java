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

package org.osehra.vista.soa.rpc.util;

import org.osehra.vista.soa.rpc.EmptyParameter;
import org.osehra.vista.soa.rpc.GlobalParameter;
import org.osehra.vista.soa.rpc.LiteralParameter;
import org.osehra.vista.soa.rpc.Parameter;
import org.osehra.vista.soa.rpc.ReferenceParameter;
import org.osehra.vista.soa.rpc.RpcConstants;
import org.osehra.vista.soa.rpc.RpcRequest;


public class RpcCommandLibrary {

    public static RpcRequest request() {
        return new RpcRequest()
            .namespace(RpcConstants.RPC_DEFAULT_NS)
            .code(RpcConstants.RPC_DEFAULT_CODE)
            .version(RpcConstants.RPC_VERSION);
    }

    public static Parameter literal(String value) {
        return new LiteralParameter(value);
    }
    public static Parameter ref(String value) {
        return new ReferenceParameter(value);
    }
    public static Parameter global(String key, String value) {
        return new GlobalParameter(key, value);
    }
    public static Parameter empty() {
        return new EmptyParameter();
    }
    public static String one(String param) {
        return new StringBuffer().append("1").append(param).append("1").toString();
    }

}
