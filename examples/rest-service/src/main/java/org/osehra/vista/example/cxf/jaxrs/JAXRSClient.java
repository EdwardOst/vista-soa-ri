/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osehra.vista.example.cxf.jaxrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.osehra.vista.example.cxf.jaxrs.resources.PatientNotFoundFault;
import org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecords;

public final class JAXRSClient {
    
    private MedicalRecords emr;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public JAXRSClient() {
    	List providers = new ArrayList();
    	JSONProvider jsonProvider = new JSONProvider();
    	Map<String, String> map = new HashMap<String, String>();
    	jsonProvider.setNamespaceMap (map);
    	providers.add (jsonProvider);
    	providers.add (new TestResponseExceptionMapper());
        emr = JAXRSClientFactory.create(
            "http://localhost:" + System.getProperty("restEndpointPort") + "/rest",
            MedicalRecords.class,
            providers);        
    }
    
    public MedicalRecords getMedicalRecords() {
        return emr;
    }
    
    public static class TestResponseExceptionMapper implements ResponseExceptionMapper<PatientNotFoundFault> {
        
        public TestResponseExceptionMapper() {
        }
        
        public PatientNotFoundFault fromResponse(Response r) {
            Object value = r.getMetadata().getFirst("EMR-HEADER");
            if (value != null) {
                return new PatientNotFoundFault(value.toString());
            }
            throw new WebApplicationException();
        }
        
    }

}
