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

import java.io.InputStream;
import java.util.Scanner;

/*
 * Parses an InputStream, splits it in lines that a parsed by a LineParser
 * 
 */
public class TextParser {
    private final LineParser parser;

    public TextParser(LineParser parser) {
        this.parser = parser;
    }

    public void parse(InputStream in) throws Exception {
        Scanner scanner = new Scanner(in);
        try {
            while (scanner.hasNextLine()) {
                parser.parse(scanner.nextLine());
            }
        } finally {
            scanner.close();
        }
    }

}

