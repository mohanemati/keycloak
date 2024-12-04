//package org.example.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.ByteArrayInputStream;
//import java.security.PublicKey;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateFactory;
//import java.util.Base64;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class JwtService {
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
//    @Value("${keycloak.server-url}")
//    private String keycloakServerUrl;
//
//    @Cacheable("keycloakPublicKey")
//    public PublicKey getKeycloakPublicKey() {
//        try {
//            String jwkUrl = UriComponentsBuilder.fromHttpUrl(keycloakServerUrl)
//                    .pathSegment("realms", realm, "protocol", "openid-connect", "certs")
//                    .toUriString();
//
//            RestTemplate restTemplate = new RestTemplate();
//            Map<String, Object> jwkResponse = restTemplate.getForObject(jwkUrl, Map.class);
//
//            String publicKeyPem = ((List<String>) ((Map<String, Object>) ((List<Object>) jwkResponse.get("keys")).get(0)).get("x5c")).get(0);
//
//            byte[] decodedKey = Base64.getDecoder().decode(publicKeyPem);
//            CertificateFactory factory = CertificateFactory.getInstance("X.509");
//            Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(decodedKey));
//
//            return certificate.getPublicKey();
//        } catch (Exception e) {
//            throw new RuntimeException("Error while fetching public key from Keycloak", e);
//        }
//    }
//
//    public Claims extractAllClaims(String token) {
//        PublicKey publicKey = getKeycloakPublicKey();
//        return Jwts.parserBuilder()
//                .setSigningKey(publicKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public boolean isTokenValid(String token) {
//        try {
//            Claims claims = extractAllClaims(token);
//            return !isTokenExpired(claims);
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    private boolean isTokenExpired(Claims claims) {
//        return claims.getExpiration().before(new Date());
//    }
//
//    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//}

package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    // این متد به‌صورت کش‌شده PublicKey را از Keycloak دریافت می‌کند
    @Cacheable("keycloakPublicKey")
    public PublicKey getKeycloakPublicKey() {
        try {
            // ساخت URL برای دریافت کلید عمومی از Keycloak
            String jwkUrl = UriComponentsBuilder.fromHttpUrl(keycloakServerUrl)
                    .pathSegment("realms", realm, "protocol", "openid-connect", "certs")
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> jwkResponse = restTemplate.getForObject(jwkUrl, Map.class);

            if (jwkResponse == null || !jwkResponse.containsKey("keys")) {
                throw new RuntimeException("Invalid response from Keycloak JWK endpoint");
            }

            // استخراج اولین کلید از پاسخ
            List<Object> keys = (List<Object>) jwkResponse.get("keys");
            if (keys.isEmpty()) {
                throw new RuntimeException("No keys found in Keycloak JWK response");
            }

            // استخراج کلید عمومی از کلید "x5c"
            Map<String, Object> keyDetails = (Map<String, Object>) keys.get(0);
            List<String> x5c = (List<String>) keyDetails.get("x5c");
            if (x5c == null || x5c.isEmpty()) {
                throw new RuntimeException("No x5c certificate chain found in Keycloak JWK response");
            }

            String publicKeyPem = x5c.get(0);

            // تبدیل Base64 PEM به PublicKey
            byte[] decodedKey = Base64.getDecoder().decode(publicKeyPem);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(decodedKey));

            return certificate.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching public key from Keycloak", e);
        }
    }

    // استخراج Claims از توکن
    public Claims extractAllClaims(String token) {
        try {
            PublicKey publicKey = getKeycloakPublicKey();
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }

    // استخراج نام کاربری از توکن
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // بررسی اعتبار توکن
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception ex) {
            return false;
        }
    }

    // بررسی انقضای توکن
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    // استخراج اطلاعات خاص از Claims
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
