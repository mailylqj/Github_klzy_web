package com.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtil {


    public static String createJWT(String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(const_value.key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setId(uuid).setAudience(String.valueOf(nowMillis)).signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    public static Claims parseJWT(String jwt) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(const_value.key);
        Claims claims = Jwts.parser().setSigningKey(apiKeySecretBytes).parseClaimsJws(jwt).getBody();
        return claims;
    }
}
