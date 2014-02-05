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

package org.osehra.vista.camel.rpc.util;


import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import static org.osehra.vista.soa.rpc.codec.RpcCodecUtils.convertFromFileman;
import static org.osehra.vista.soa.rpc.codec.RpcCodecUtils.convertToFileman;


public class VistaConvertionsTest {

    @Test
    public void testConvertDateFromFileman() throws Exception {
        Assert.assertEquals(new GregorianCalendar(2001, 2, 2), convertFromFileman("3010302"));    // March 2nd, 2001
    }

    @Test
    public void testConvertDateToFileman() throws Exception {
        Assert.assertEquals("3010302", convertToFileman(new GregorianCalendar(2001, 2, 2)));    // March 2nd, 2001
    }

}

