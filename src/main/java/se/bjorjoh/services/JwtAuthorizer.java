package se.bjorjoh.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtAuthorizer {

    private static final String PRIVATE_KEY = "qwertyuiopasdfghjklzxcvbnm123456";
    private static final Algorithm algorithm = Algorithm.HMAC256(PRIVATE_KEY);
    private static final JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer("auth.bjorjoh.se")
            .build();


    public static DecodedJWT getAndVerifyJWT(String token) throws JWTVerificationException{

        DecodedJWT result = verifier.verify(token);

        return result;


    }


}
