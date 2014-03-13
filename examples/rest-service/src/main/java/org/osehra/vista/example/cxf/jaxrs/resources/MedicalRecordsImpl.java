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
package org.osehra.vista.example.cxf.jaxrs.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.osehra.vista.soa.rpc.MapParameter;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import org.osehra.vista.soa.rpc.codec.RpcCodecUtils;

import static org.osehra.vista.soa.rpc.util.RpcCommandLibrary.literal;
import static org.osehra.vista.soa.rpc.util.RpcCommandLibrary.request;
import static org.osehra.vista.soa.rpc.util.commands.VistaCommands.vista;

public class MedicalRecordsImpl implements MedicalRecords {
	// TODO: MedicalRecordsImpl should be a service, with a lifecycle
	//  this would give it a chance to properly disconnect
	public static final long REFRESH_TIME = 30000;

	private CamelContext context;
	private ProducerTemplate producer;
    private Map<Long, PatientRecord> patients = new HashMap<Long, PatientRecord>();
    private long timestamp;
    private boolean isRest;
    private boolean connected;
    
    public MedicalRecordsImpl(CamelContext context, boolean restFlag) {
    	timestamp = 0;
    	this.context = context;
    	producer = context.createProducerTemplate();
    	producer.setDefaultEndpointUri("direct:vista");
        isRest = restFlag;
    }
    
    public MedicalRecordsImpl() {
    }
    
    public Collection<Patient> getAllPatients() {
    	if (refresh(timestamp)) {
    		connectVista(); // if necessary...

    		timestamp = System.currentTimeMillis();
    		RpcResponse reply = producer.requestBody(request()
    			.version("0")
    			.name("ORWPT LIST ALL")
    			.parameter(literal("0"))
    			.parameter(literal("1")), RpcResponse.class);
    		int count = reply.getContent().size() - 1;
			System.out.println("COUNT=" + count);
    		for (int i = 0; i < count; i++) {
    			long id = Long.parseLong(reply.getField(i,  0));
    			PatientRecord rec = new PatientRecord();
    			rec.timestamp = timestamp;
    			rec.fetched = false;
    			rec.patient = new Patient(id, reply.getField(i, 1));
    			
    			// TODO: not the nicest way to deal with duplicated, but it will do for now
    			patients.put(id, rec);
    		}
    	}
    	Collection<Patient> result = new ArrayList<Patient>(patients.size());
    	for (PatientRecord rec: patients.values()) {
    		result.add(rec.patient);
    	}
    	return result;
    }

    public Patient getPatient(Long id) throws PatientNotFoundFault {
    	if (!patients.containsKey(id)) {
    		// force a refresh
    		timestamp = 0;
    		getAllPatients();
    		
    	}

        if (patients.get(id) == null) {
            PatientNotFoundDetails details = new PatientNotFoundDetails();
            details.setId(id);
            if (!isRest) {
                throw new PatientNotFoundFault("Can't find the Patient with id " + id, details);
            } else {                
                Response r = Response.status(404).header("EMR-HEADER",
                    "No Patient with id " + id + " is available").entity(details).build();
                throw new WebApplicationException(r);
            }
        }

        PatientRecord rec = patients.get(id);
        if (!rec.fetched || refresh(rec.timestamp)) {
        	// refresh patient info
    		connectVista(); // if necessary...
    		rec.timestamp = System.currentTimeMillis();
    		RpcResponse reply = producer.requestBody(request()
    			.version("0")
    			.name("ORWPT ID INFO")
    			.parameter(literal(Long.toString(id))), RpcResponse.class);
    		rec.patient.setSsn(formatSsn(reply.getField(0, 0)));
    		rec.patient.setDob(String.format("%1$tY-%1$tm-%1$td", 
    			RpcCodecUtils.convertFromFileman(reply.getField(0, 1))));
    		String sex = reply.getField(0, 2);
    		rec.patient.setSex(sex.equals("M") ? sex : "F");
    		rec.fetched = true;
        }
        return rec.patient;
    }
    
    public Collection<Allergy> getAllergies(Long id) throws PatientNotFoundFault {
    	if (!patients.containsKey(id)) {
    		// force a refresh
    		timestamp = 0;
    		getAllPatients();
    		
    	}

        if (patients.get(id) == null) {
            PatientNotFoundDetails details = new PatientNotFoundDetails();
            details.setId(id);
            if (!isRest) {
                throw new PatientNotFoundFault("Can't find the Patient with id " + id, details);
            } else {                
                Response r = Response.status(404).header("EMR-HEADER",
                    "No Patient with id " + id + " is available").entity(details).build();
                throw new WebApplicationException(r);
            }
        }

		String pid = Long.toString(id);
        PatientRecord rec = patients.get(id);
        // TODO: optimize instead of fetching allergies every time
    	Collection<Allergy> allergies = new ArrayList<Allergy>();
		producer.requestBody(request().version("0").name("DG SENSITIVE RECORD ACCESS").parameter(literal(pid)));
		producer.requestBody(request().version("0").name("ORWPT SELECT").parameter(literal(pid)));
		RpcResponse reply = producer.requestBody(request().version("0").name("ORQQAL LIST").parameter(literal(pid)), RpcResponse.class);
		
		int count = reply.getContent().size() - 1;
		for (int i = 0; i < count; i++) {
			Allergy a = new Allergy(id);
			a.setIndex(Long.parseLong(reply.getField(i, 0)));
			a.setAgent(reply.getField(i, 1));
			a.setSeverity(reply.getField(i, 2));
			a.setSymptoms(Arrays.asList(reply.getField(i, 3).split(";")));
			allergies.add(a);
			System.out.println(a);
		}

    	rec.allergies = allergies;
    	return rec.allergies;
    }

    public Allergy addAllergy(Allergy allergy) {
    	System.out.println("ADDING ALLERGY");
        Map<String, String> allergyParams = new HashMap<String, String>();
        allergyParams.put("\"GMRAGNT\"", "POLLEN^9;GMRD(120.82,");
        allergyParams.put("\"GMRATYPE\"", "O^Other");
        allergyParams.put("\"GMRANATR\"", "A^Allergy");
        allergyParams.put("\"GMRAORIG\"", "51");
        allergyParams.put("\"GMRAORDT\"", "3140206.222");
        allergyParams.put("\"GMRASYMP\",0", "2");
        allergyParams.put("\"GMRASYMP\",1", "15^CONFUSION^^^");
        allergyParams.put("\"GMRASYMP\",2", "99^HYPOTENSION^^^");
        allergyParams.put("\"GMRACHT\",0", "1");
        allergyParams.put("\"GMRACHT\",1", "3140206.222049");
        allergyParams.put("\"GMRAOBHX\"", "o^OBSERVED");
        allergyParams.put("\"GMRARDT\"", "3140206");
        allergyParams.put("\"GMRASEVR\"", "3");
        allergyParams.put("\"GMRACMTS\",0", "1");
        allergyParams.put("\"GMRACMTS\",1", "Some comment");
        producer.requestBody(request()
    			.version("0")
    			.name("ORWDAL32 SAVE ALLERGY")
    			.parameter(literal("0"))
    			.parameter(literal("1"))
    			.parameter(new MapParameter(allergyParams)));
        producer.requestBody(request()
    			.version("0")
    			.name("ORWU DT")
    			.parameter(literal("NOW")));

        disconnectVista();
        return allergy;
    }

    private synchronized void connectVista() {
    	if (!connected) {
    		producer.requestBody(vista().connect("192.168.1.100", "vista.example.org"));
    		producer.requestBody(vista().signonSetup());
    		producer.requestBody(vista().login("fakedoc1", "1Doc$#@!"));
    		producer.requestBody(request().version("0").name("XWB CREATE CONTEXT").parameter(literal("-2")));
    		producer.requestBody(request().version("0").name("XWB CREATE CONTEXT").parameter(literal("&E.!6N.H!%dC!6ca.-)")));
    		producer.requestBody(request().version("0").name("ORQPT DEFAULT LIST SOURCE"));
    		connected = true;
    	}    	
    }
    
    private synchronized void disconnectVista() {
    	if (connected) {
    		producer.requestBody(vista().disconnect());
    		connected = false;
    	} 
    }
    
    private boolean refresh(long timestamp) {
    	return System.currentTimeMillis() > (timestamp + REFRESH_TIME);
    }

    public static String formatSsn(String raw) {
    	StringBuilder sb = new StringBuilder();
    	if (raw.length() == 9) {
    	    sb.append(raw.substring(0, 3));
    	    sb.append("-");
    	    sb.append(raw.substring(3, 5));
    	    sb.append("-");
    	    sb.append(raw.substring(5));
    	}
    	return sb.toString();
    }
    public static final class PatientRecord {
    	public Patient patient;
    	public boolean fetched;
    	public long timestamp;
    	public Collection<Allergy> allergies;
    }
}
