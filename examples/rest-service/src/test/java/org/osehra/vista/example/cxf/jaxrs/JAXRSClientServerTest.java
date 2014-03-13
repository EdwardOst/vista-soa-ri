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

import static org.osehra.vista.soa.rpc.util.RpcCommandLibrary.literal;
import static org.osehra.vista.soa.rpc.util.RpcCommandLibrary.request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osehra.vista.example.cxf.jaxrs.resources.Allergy;
import org.osehra.vista.example.cxf.jaxrs.resources.Patient;
import org.osehra.vista.example.cxf.jaxrs.resources.PatientNotFoundFault;
import org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecords;
import org.osehra.vista.soa.rpc.MapParameter;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.BusFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JAXRSClientServerTest extends CamelSpringTestSupport {
    
    @BeforeClass
    public static void setupPorts() {
        // System.setProperty("soapEndpointPort", String.valueOf(AvailablePortFinder.getNextAvailable()));
        System.setProperty("restEndpointPort", String.valueOf(AvailablePortFinder.getNextAvailable()));
    }
    
    @Ignore @Test
    public void testJAXWSClient() throws PatientNotFoundFault {
    	/*        
        JAXWSClient jaxwsClient = new JAXWSClient();
        MedicalRecords emr = jaxwsClient.getMedicalRecords();

        emr.addPatient(new Patient("Camel User Guide", "123"));
        Patient patient = emr.getPatient("123");
        assertNotNull("We should find the patient here", patient);       
      
        try {
            patient = emr.getPatient("124");
            fail("We should not have this patient");
        } catch (Exception exception) {
            assertTrue("The exception should be PatientNotFoundFault", exception instanceof PatientNotFoundFault);
        }
*/
    }
    
    @Test
    public void testJAXRSClient() throws PatientNotFoundFault, InterruptedException {
        // JAXRSClient invocation
        JAXRSClient jaxrsClient = new JAXRSClient();
        MedicalRecords emr =  jaxrsClient.getMedicalRecords();
        
        Collection<Patient> patients = emr.getAllPatients();
        Patient p = emr.getPatient(1L);
        // System.out.println(p.toString());

        Collection<Allergy> coll = emr.getAllergies(1L);
        //emr.addAllergy(new Allergy(1));
        /*
        ProducerTemplate producer = this.context().createProducerTemplate();
*/
        Thread.sleep(180000);
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[]{"/META-INF/spring/JAXRSCamelContext.xml"});
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        BusFactory.setDefaultBus(null);
        BusFactory.setThreadDefaultBus(null);
    }

}
