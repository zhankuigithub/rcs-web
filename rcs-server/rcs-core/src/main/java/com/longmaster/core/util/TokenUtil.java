package com.longmaster.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    private static final long EXPIRE_DATE = 10 * 100000;
    private static final String TOKEN_SECRET = "ABC";

    public static String token(String userPhone, String verifyCode) {

        String token = "";
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_DATE);
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Map<String, Object> header = new HashMap<>();

            header.put("typ", "JWT");
            header.put("alg", "HS256");
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("userPhone", userPhone)
                    .withClaim("verifyCode", verifyCode).withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
