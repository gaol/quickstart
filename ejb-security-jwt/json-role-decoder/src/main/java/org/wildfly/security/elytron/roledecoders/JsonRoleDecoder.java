/*
 *  Copyright (c) 2023 The original author or authors
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of Apache License v2.0 which
 *  accompanies this distribution.
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.wildfly.security.elytron.roledecoders;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import org.wildfly.security.authz.Attributes;
import org.wildfly.security.authz.AuthorizationIdentity;
import org.wildfly.security.authz.RoleDecoder;
import org.wildfly.security.authz.Roles;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A RoleDecoder to extract Roles from a JSON format data, normally comes from a JWT claim.
 *
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class JsonRoleDecoder implements RoleDecoder {

    private static final String JSON_PATH_KEY = "json-path";
    private static final String DEFAULT_JSON_PATH = "/realm_access/roles";

    /**
     * The Json path to locate the roles. It follows JsonPointer spec
     * The first segment is used to retrieve from Elytron AuthorizationIdentity attributes.
     */
    private String jsonPath = DEFAULT_JSON_PATH;

    /**
     * This method is called by WildFly Elytron subsystem with specified configuration.
     *
     * @param configuration the configuration
     */
    public void initialize(Map<String, String> configuration) {
        jsonPath = configuration.getOrDefault(JSON_PATH_KEY, DEFAULT_JSON_PATH);
        if (!jsonPath.startsWith("/")) {
            throw new IllegalArgumentException("jsonPath must start with '/'");
        }
    }

    @Override
    public Roles decodeRoles(AuthorizationIdentity identity) {
        int idx = jsonPath.indexOf('/', 1);
        String key = idx > 0 ? jsonPath.substring(1, idx) : jsonPath.substring(1);
        Attributes.Entry entry = identity.getAttributes().get(key);
        if (entry != null && entry.size() > 0) {
            // if only one segment, key is the leaf
            try {
                JsonStructure jsonStructure = (JsonStructure)Json.createReader(new StringReader(entry.get(0))).readValue();
                String jsonPointer = jsonPath.substring(key.length() + 1);
                JsonArray jsonArray = (JsonArray)jsonStructure.getValue(jsonPointer);
                Set<String> roles = new HashSet<>();
                jsonArray.forEach(jv -> roles.add(((JsonString)jv).getString()));
                return Roles.fromSet(roles);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Roles.NONE;
    }
}
