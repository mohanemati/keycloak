package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.configuration.KeycloakConfig;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminClientService {

    @Autowired
    Keycloak keycloak;

    @PostConstruct
    void searchUsers() {
        // ...
    }
}
