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

package org.osehra.vista.soa.rpc;

import java.util.LinkedHashMap;
import java.util.Map;


public final class MapParameter implements Parameter {
    private Map<String, String> params = new LinkedHashMap<String, String>();

    public MapParameter(Map<String, String> params) {
        this.params.putAll(params);
    }
    public Map<String, String> getParams() {
        return params;
    }
    public String getParam(String key) {
        return params.get(key);
    }
    
}

