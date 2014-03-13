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

import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@WebService
@Path("/emr")
@Consumes("application/json")
@Produces("application/json")
public interface MedicalRecords {
    
    @WebMethod
    @GET
    @Path("/patient/{id}")
    @Consumes("*/*")
    Patient getPatient(@PathParam("id") @WebParam(name = "id") Long id) throws PatientNotFoundFault;

    @WebMethod
    @GET
    @Path("/patients")
    @Consumes("*/*")
    Collection<Patient> getAllPatients();

    @WebMethod
    @GET
    @Path("/allergies/{id}")
    @Consumes("*/*")
    Collection<Allergy> getAllergies(@PathParam("id") @WebParam(name = "id") Long id) throws PatientNotFoundFault;

    @WebMethod
    @POST
    @Path("/allergy")
    Allergy addAllergy(@WebParam(name = "allergy") Allergy allergy);
}

