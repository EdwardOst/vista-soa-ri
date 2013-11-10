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

package org.osehra.vista.soa.rpc;

import java.util.ArrayList;


public final class RpcResponse {
    private static final String DELIM = "^";
    private static final String CRLF = "\r\n";
    
    public static final class Line extends ArrayList<String> {
        private static final long serialVersionUID = 1L;

        public String toString() {
            StringBuffer sb = new StringBuffer(lineSize(this));
            for (String f : this) {
                sb.append(sb.length() > 0 ? DELIM : "");
                sb.append(f);
            }
            return sb.toString();
        }
    }
    public static final class Entry extends ArrayList<Line> {
        private static final long serialVersionUID = 1L;

        public String toString() {
            StringBuffer sb = new StringBuffer(entrySize(this));
            for (Line l : this) {
                sb.append(sb.length() > 0 ? CRLF : "");
                sb.append(l);
            }
            return sb.toString();
        }
    }

    private Entry content = new Entry();
    
    public RpcResponse() {
    }

    public Entry getContent() {
        return content;
    }

    public String getLine(int index) {
        return content.get(index).toString();
    }

    public String getField(int line, int index) {
        return content.get(line).get(index);
    }

    public void appendLine(Line line) {
        content.add(line);
    }
    private static int entrySize(Entry entry) {
        int len = 0;
        for (Line l: entry) {
            len += lineSize(l) + 2;
        }
        return len;
    }

    private static int lineSize(Line line) {
        int len = line.size();
        for (String f: line) {
            len += f.length();
        }
        return len;
    }
}

