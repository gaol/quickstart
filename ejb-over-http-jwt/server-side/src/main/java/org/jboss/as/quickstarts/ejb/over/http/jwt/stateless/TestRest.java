/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.ejb.over.http.jwt.stateless;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestRest {

    private static final Logger log = Logger.getLogger(TestRest.class.getName());

    @Inject
    JwtManager jwtManager;

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postJWT(@FormParam("username") String username, @FormParam("password") String password,
                            @FormParam("client_id") String client_id, @FormParam("client_secret") String client_secret) {
        log.info("Authenticating " + username);
        try {
            if ("owner".equals(username) && "owner_pass".equals(password)
                    && "client_id".equals(client_id) && "client_secret".equals(client_secret)) {
                String token = jwtManager.createJwt(username, new String[]{"admin"});
                JsonObjectBuilder tokenBuilder = Json.createObjectBuilder();
                tokenBuilder.add("access_token", token);
                log.info("JWT token created !!");
                return Response.ok(tokenBuilder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                log.warning("Failed to authenticate to get JWT token");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create JWT", e);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}
