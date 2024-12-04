package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.LoginRequest;
import org.example.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import  org.example.security.JwtService;


@RestController
@RequestMapping("/api/user")
public class TestApi {



    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    private final JwtService jwtService;
    @Autowired
    public TestApi(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @PostMapping("/get-token")
    public ResponseEntity<Map<String, Object>> getToken(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        System.out.println("username" + username);
        try {
            String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "grant_type=password&client_id=" + clientId +
                    "&username=" + username +
                    "&password=" + password;

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();

                // استفاده از Jackson برای تجزیه JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // استخراج توکن و expires_in
                String token = jsonNode.get("access_token").asText();
                int expiresIn = jsonNode.get("expires_in").asInt();

                // ساخت JSON پاسخ
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("status", "success");
                responseMap.put("token", token);
                responseMap.put("expires_in", expiresIn);

                return ResponseEntity.ok(responseMap);
            } else {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("status", "error");
                errorMap.put("message", "Failed to retrieve token");
                errorMap.put("code", response.getStatusCode().value());

                return ResponseEntity.status(response.getStatusCode()).body(errorMap);
            }
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", "error");
            errorMap.put("message", "Error occurred");
            errorMap.put("details", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }

    @GetMapping("/test")
    public void test() {
        System.out.printf("i am hereeeeeeeeeee");
    }

//    @GetMapping("/verify-token")
//    public String verifyToken(@RequestHeader("Authorization") String token) {
//
//        try {
//            String jwtToken = token.replace("Bearer ", "");
//            String issuerUri = "http://localhost:8080/realms/ORMRealm";
//            JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
//            Jwt decodedJwt = jwtDecoder.decode(jwtToken);
//
//            return "Token is valid! User: " + decodedJwt.getSubject();
//        } catch (JwtException ex) {
//            return "Invalid token! Error: " + ex.getMessage();
//        }
//    }



//    @GetMapping("/verify-token")
//    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
//
//        System.out.println("validateToken hastim:)");
//        String token = extractTokenFromHeader(authorizationHeader);
//
//        boolean isValid = jwtService.isTokenValid(token);
//        if (isValid) {
//            String username = jwtService.extractUsername(token);
//            return ResponseEntity.ok("Token is valid. Username: " + username);
//        } else {
//            return ResponseEntity.status(401).body("Invalid token");
//        }
//    }

    @GetMapping("/verify-tokenn")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("validateToken hastim:)");

        // استخراج توکن از هدر Authorization
        String token = extractTokenFromHeader(authorizationHeader);

        // چک کردن اعتبار توکن
        if (token == null || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        // استخراج نام کاربری از توکن
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok("Token is valid. Username: " + username);
    }


//    private String extractTokenFromHeader(String authorizationHeader) {
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Invalid Authorization header");
//        }
//        return authorizationHeader.substring(7); // حذف "Bearer "
//    }
private String extractTokenFromHeader(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        return authorizationHeader.substring(7); // حذف "Bearer " از ابتدای رشته
    }
    return null;
}

    private String extractToken(String responseBody) {

        String[] parts = responseBody.split("\"access_token\":\"");
        if (parts.length > 1) {
            return parts[1].split("\"")[0];
        }
        return null;
    }
}
