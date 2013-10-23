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

package org.osehra.vista.soa.rpc.util;

import java.io.ByteArrayOutputStream;


/*
 * Parses log 
 *          +-------------------------------------------------+
 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
 * +--------+-------------------------------------------------+----------------+
 * |00000000| 5b 58 57 42 5d 31 30 33 30 34 0a 54 43 50 43 6f |[XWB]10304.TCPCo|
 * |00000010| 6e 6e 65 63 74 35 30 30 31 33 31 39 32 2e 31 36 |nnect50013192.16|
 * |00000020| 38 2e 31 2e 31 30 30 66 30 30 30 31 30 66 30 30 |8.1.100f00010f00|
 * |00000030| 31 37 76 69 73 74 61 2e 65 78 61 6d 70 6c 65 2e |17vista.example.|
 * |00000040| 6f 72 67 66 04                                  |orgf.           |
 * +--------+-------------------------------------------------+----------------+
 * 
 * 
 * 
 */
public class NettyLogBuffer {
    public static final int HEX = 16;
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public void onAddress(int address) {
        // ignore
    }
    
    public void onHexValue(String hex) {
        stream.write(Integer.valueOf(hex, HEX));
    }
    
    public byte[] getBuffer() {
        return stream.toByteArray();
    }
}

