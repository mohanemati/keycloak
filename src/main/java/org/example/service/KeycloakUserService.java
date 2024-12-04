package org.example.service;

import org.jboss.jandex.Main;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {

    private static final String REALM_NAME = "ORMRealm";

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    void searchByUsername(String username, boolean exact) {
        logger.info("Searching by username: {} (exact {})", username, exact);
        List<UserRepresentation> users = keycloak.realm(REALM_NAME)
                .users()
                .searchByUsername(username, exact);

        logger.info("Users found by username {}", users.stream()
                .map(user -> user.getUsername())
                .collect(Collectors.toList()));
    }
    public void searchUsers() {
        searchByUsername("user1", true);
        searchByUsername("user", false);
        searchByUsername("1", false);
    }

    void searchByEmail(String email, boolean exact) {
        logger.info("Searching by email: {} (exact {})", email, exact);

        List<UserRepresentation> users = keycloak.realm(REALM_NAME)
                .users()
                .searchByEmail(email, exact);

        logger.info("Users found by email {}", users.stream()
                .map(user -> user.getEmail())
                .collect(Collectors.toList()));
    }
}

