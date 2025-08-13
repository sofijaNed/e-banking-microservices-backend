package fon.bank.authservice.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class KeyConfig {

    private final JwtProperties props;

    private static byte[] readPemBody(String pem) {
        String b64 = pem
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }

    private RSAPrivateKey loadPrivateKey(String path) throws Exception {
        String pem = Files.readString(Path.of(path));                // PKCS#8: -----BEGIN PRIVATE KEY-----
        byte[] der = readPemBody(pem);
        var kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private RSAPublicKey loadPublicKey(String path) throws Exception {
        String pem = Files.readString(Path.of(path));                // X.509: -----BEGIN PUBLIC KEY-----
        byte[] der = readPemBody(pem);
        var kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(der));
    }

    // --- Aktivni JWK (privatni + javni + kid) ---
    @Bean
    public RSAKey activeRsaJwk() throws Exception {
        RSAPrivateKey priv = loadPrivateKey(props.getKey().getPrivatePemPath());
        RSAPublicKey pub  = loadPublicKey(props.getKey().getPublicPemPath());
        return new RSAKey.Builder(pub)
                .privateKey(priv)
                .keyID(props.getKey().getId())
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey activeRsaJwk) {
        var jwkSet = new JWKSet(activeRsaJwk);
        var jwkSource = new ImmutableJWKSet<>(jwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey activeRsaJwk) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(activeRsaJwk.toRSAPublicKey()).build();
    }

    // PUBLIC JWKS za izlaganje (koristi se u /.well-known/jwks.json)
    @Bean
    public JWKSet publicJwkSet(RSAKey activeRsaJwk) {
        return new JWKSet(activeRsaJwk.toPublicJWK());
    }
}
