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

import org.osehra.vista.example.cxf.jaxrs.resources.Patient;
import org.osehra.vista.example.cxf.jaxrs.resources.PatientNotFoundFault;
import org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecords;

public class Client {
    
    void invoke() throws PatientNotFoundFault {
/*
    	// JAXWSClient invocation
        JAXWSClient jaxwsClient = new JAXWSClient();
        MedicalRecords emr = jaxwsClient.getMedicalRecords();
        
        emr.addPatient(new Patient("Camel User Guide", "123"));
        Patient patient = emr.getPatient("123");
        System.out.println("Get the patient with id 123. " + patient);       
      
        try {
            patient = emr.getPatient("124");
            System.out.println("Get the patient with id 124. " + patient); 
        } catch (Exception exception) {
            System.out.println("Expected exception received: " + exception);
        }
        
        // JAXRSClient invocation
        JAXRSClient jaxrsClient = new JAXRSClient();
        emr =  jaxrsClient.getMedicalRecords();
        
        emr.addPatient(new Patient("Karaf User Guide", "124"));
        patient = emr.getPatient("124");
        System.out.println("Get the patient with id 124. " + patient);
        
        try {
            patient = emr.getPatient("126");
            System.out.println("Get the patient with id 126. " + patient); 
        } catch (Exception exception) {
            System.out.println("Expected exception received: " + exception);
        }
*/
    }
    
    public static void main(String args[]) throws Exception {
        Client client = new Client();
        client.invoke();
    }

}
