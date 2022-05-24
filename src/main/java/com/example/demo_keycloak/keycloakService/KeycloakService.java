package com.example.demo_keycloak.keycloakService;

import com.example.demo_keycloak.vo.EmployeeVO;
import com.example.demo_keycloak.vo.GroupVO;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    private final String role = "login-admin";
    private final String adminName = "hungcmc";
    private final String adminPassword = "hungcmc";
    private final String realmAdmin = "master";
    private final String adminClientId = "admin-cli";

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    public Object listUsers(){
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();
        return keycloak.realm(realm).users().list();
    }
    public EmployeeVO createEmployee(EmployeeVO employeeVo) {
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();
        UserRepresentation employee = new UserRepresentation();
        employee.setEnabled(true);
        employee.setUsername(employeeVo.getEmail());
        employee.setFirstName(employeeVo.getFirstname());
        employee.setLastName(employeeVo.getLastname());
        employee.setEmail(employeeVo.getEmail());
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(employee);
        employeeVo.setStatusCode(response.getStatus());
        employeeVo.setStatus(response.getStatusInfo().toString());

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(employeeVo.getPassword());
            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(passwordCred);
            //for(int i = 0; i < realmResource.roles().list().size(); i++) {
                //log.info(realmResource.roles().list().get(i)+"");
            //}
            RoleRepresentation realmRoleUser = realmResource.roles().get("default-roles-test").
                    toRepresentation();

            userResource.roles().realmLevel().add(Arrays.
                    asList(realmRoleUser));
        }
        return employeeVo;
    }

    public Object login(EmployeeVO employeeVo) {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", "password");
        Configuration configuration =
                new Configuration(authServerUrl, realm, clientId,
                        clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);
        AccessTokenResponse response =
                authzClient.obtainAccessToken(employeeVo.getEmail(),
                        employeeVo.getPassword());
        return ResponseEntity.ok(response);
    }
    public String createGroup(GroupVO groupVo){
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();
        GroupRepresentation group =new GroupRepresentation();
        group.setName(groupVo.getNameGroup());
        group.setPath("admin/lamtest");
        Response response = keycloak.realm(realm).groups().add(group);
        return response.getStatusInfo().toString() ;
    }
    public String creatChildGroup(GroupVO groupVo){
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();
        GroupRepresentation group =new GroupRepresentation();
        group.setName(groupVo.getNameGroup());
        Response response = keycloak.realm(realm).groups().group("238493fa-8ba2-4dd4-aae8-39f571e837ac").subGroup(group);
        return response.getStatusInfo().toString() ;

    }
    public Object allGroup(){
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();

        return keycloak.realm(realm).groups().groups();

    }
    public String detailGroup(){
        Keycloak keycloak = KeycloakBuilder.builder().
                serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).
                realm(realmAdmin).
                clientId(adminClientId)
                .username(adminName).password(adminPassword)
                .resteasyClient(new ResteasyClientBuilder().
                        connectionPoolSize(10).build()).build();
        String a = "238493fa-8ba2-4dd4-aae8-39f571e837ac";
        keycloak.tokenManager().getAccessToken();
        keycloak.realm("demo").remove();
        return "a";

    }


}
