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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.osehra.vista.example.cxf.jaxrs.resources.Patient;
import org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecords;
import org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecordsImpl;

public class CamelRouterBuilder extends RouteBuilder {
    private static final String VISTA_ENDPOINT_URI = "vista://localhost:9200";
    private static final String SOAP_ENDPOINT_URI = "cxf://http://localhost:{{soapEndpointPort}}/soap"
        + "?serviceClass=org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecords";
    private static final String REST_ENDPOINT_URI = "cxfrs://http://localhost:{{restEndpointPort}}/rest"
        + "?resourceClasses=org.osehra.vista.example.cxf.jaxrs.resources.MedicalRecordsImpl";
    
    /**
     * Allow this route to be run as an application
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("soapEndpointPort", "9006");
        System.setProperty("restEndpointPort", "9002");
                
        CamelContext context = new DefaultCamelContext();
        PropertiesComponent pc = new PropertiesComponent();
        context.addComponent("properties", pc);
        context.start();
        context.addRoutes(new CamelRouterBuilder());
        Thread.sleep(1000);
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
        Thread.sleep(1000);
        context.stop();
        System.exit(0);
    }

    public void configure() {
        errorHandler(noErrorHandler());
        // populate the message queue with some messages
        // from(SOAP_ENDPOINT_URI).process(new MappingProcessor(new MedicalRecordsImpl(getContext(), false)));
        from(REST_ENDPOINT_URI).process(new MappingProcessor(new MedicalRecordsImpl(getContext(), true)));
        from("direct:vista").to(VISTA_ENDPOINT_URI);
    }
    
    // Mapping the request to object's invocation
    private static class MappingProcessor implements Processor {
        
        private Class<?> beanClass;
        private Object instance;
        
        public MappingProcessor(Object obj) {
            beanClass = obj.getClass();
            instance = obj;
        }
         
        public void process(Exchange exchange) throws Exception {
            String operationName = exchange.getIn().getHeader(CxfConstants.OPERATION_NAME, String.class);
            Method method = findMethod(operationName, exchange.getIn().getBody(Object[].class));
            try {
                Object response = method.invoke(instance, exchange.getIn().getBody(Object[].class));
                exchange.getOut().setBody(response);
            }  catch (InvocationTargetException e) {
                throw (Exception)e.getCause();
            }
        }
        
        private Method findMethod(String operationName, Object[] parameters) throws SecurityException, NoSuchMethodException {            
            return beanClass.getMethod(operationName, getParameterTypes(parameters));
        }
        
        private Class<?>[] getParameterTypes(Object[] parameters) {
            if (parameters == null) {
                return new Class[0];
            }
            Class<?>[] answer = new Class[parameters.length];
            int i = 0;
            for (Object object : parameters) {
                answer[i] = object.getClass();
                i++;
            }
            return answer;
        }
    }

}
