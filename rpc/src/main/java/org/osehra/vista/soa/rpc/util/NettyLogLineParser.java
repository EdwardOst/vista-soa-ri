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

import java.util.ArrayList;
import java.util.List;


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
public class NettyLogLineParser implements LineParser {
    private boolean parsing = false;
    private final List<byte[]> entries = new ArrayList<byte[]>();
    private NettyLogBuffer buffer;
    private LineParser lineParser;

    public List<byte[]> getEntries() {
        return entries;
    }

    public void parse(String line) throws Exception {
        if (line == null || line.isEmpty()) {
            return; // ignore
        }
        switch (line.charAt(0)) {
        case '+':
            // separator line
            parsing = !parsing;
            if (parsing) {
                buffer = new NettyLogBuffer();
                lineParser = new NettyHexDumpParser(buffer);
            } else {
                // no need to set the buffer and lineParser to null, it'll happen at next pass
                entries.add(buffer.getBuffer());
            }
            break;
        case '|':
            if (!parsing) {
                throw new IllegalStateException("Invalid frame, hex dump separator line expected");
            }
            lineParser.parse(line);
            break;
        case ' ':
        case '\t':
            if (parsing) {
                throw new IllegalStateException("Invalid frame, hex dump line expected");
            }
            // ignore header line
            break;
        default:
            throw new IllegalStateException("Invalid frame, unexpected input");
        }
    }


    private final class NettyHexDumpParser implements LineParser {
        private final int radix;
        private final NettyLogBuffer buffer;

        public NettyHexDumpParser(NettyLogBuffer buffer) {
            this(buffer, NettyLogBuffer.HEX);
        }
        public NettyHexDumpParser(NettyLogBuffer buffer, int radix) {
            this.buffer = buffer;
            this.radix = radix;
        }

        public void parse(String line) throws Exception {
            String[] parts = line.split("\\|");
            if (parts.length < 4) {
                throw new Exception("foo");
            }
            // part[0] is empty, because the line starts with a '|'
            buffer.onAddress(Integer.parseInt(parts[1], radix));
            String[] bytes = parts[2].split(" ");
            for (String b : bytes) {
                if (!b.isEmpty()) {
                    buffer.onHexValue(b);
                }
            }
            // ignore part[3], the char representation of the byte
        }
    }

}

