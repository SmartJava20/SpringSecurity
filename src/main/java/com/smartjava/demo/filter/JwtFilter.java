package com.smartjava.demo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartjava.demo.model.Token;
import com.smartjava.demo.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component

public class JwtFilter {

	static final String issuer = "MyApp";
	private long accessTokenExpirationMs;
	private long refreshTokenExpirationMs;

	private Algorithm accessTokenAlgorithm;
	private Algorithm refreshTokenAlgorithm;
	private JWTVerifier accessTokenVerifier;
	private JWTVerifier refreshTokenVerifier;

	public JwtFilter(@Value("${accessTokenSecret}") String accessTokenSecret,
			@Value("${refreshTokenSecret}") String refreshTokenSecret,
			@Value("${com.smartjava.refreshTokenExpirationMinutes}") int refreshTokenExpirationMinutes,
			@Value("${com.smartjava.accessTokenExpirationMinutes}") int accessTokenExpirationMinutes) {
		accessTokenExpirationMs = (long) accessTokenExpirationMinutes * 60 * 1000;
		refreshTokenExpirationMs = (long) refreshTokenExpirationMinutes * 60 * 1000;
		accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
		refreshTokenAlgorithm = Algorithm.HMAC512(refreshTokenSecret);
		accessTokenVerifier = JWT.require(accessTokenAlgorithm).withIssuer(issuer).build();
		refreshTokenVerifier = JWT.require(refreshTokenAlgorithm).withIssuer(issuer).build();
	}

	public String generateAccessToken(User user) {
		
		return JWT.create().withIssuer(issuer).withSubject(user.getUserName()).withClaim("userid", user.getId())
					.withClaim("userName", user.getUserName()) // Add userName claim
				.withIssuedAt(new Date()).withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
				.sign(accessTokenAlgorithm);
	}



	
	public String generateRefreshToken(User user, Token token) {
		return JWT.create().withIssuer(issuer).withSubject(user.getUserName())
				.withClaim("tokenId", token.getId()).withIssuedAt(new Date())
				.withExpiresAt(new Date((new Date()).getTime() + refreshTokenExpirationMs)).sign(refreshTokenAlgorithm);
	}

	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	
	private DecodedJWT extractClaims(String token) {
		return JWT.decode(token);
		// return
		// Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	private Optional<DecodedJWT> decodeAccessToken(String token) {
		try {
			return Optional.of(accessTokenVerifier.verify(token));
		} catch (JWTVerificationException e) {
			//log.error("invalid access token", e);
		}
		return Optional.empty();
	}

	private Optional<DecodedJWT> decodeRefreshToken(String token) {
		try {
			return Optional.of(refreshTokenVerifier.verify(token));
		} catch (JWTVerificationException e) {
			//log.error("invalid refresh token", e);
		}
		return Optional.empty();
	}

	public boolean validateAccessToken(String token) {
		return decodeAccessToken(token).isPresent();
	}

	public boolean validateRefreshToken(String token) {
		return decodeRefreshToken(token).isPresent();
	}

	public String getUserIdFromAccessToken(String token) {
		return decodeAccessToken(token).get().getSubject();
	}

	public String getUserIdFromRefreshToken(String token) {
		return decodeRefreshToken(token).get().getSubject();
	}

	public String getTokenIdFromRefreshToken(String token) {
		return decodeRefreshToken(token).get().getClaim("tokenId").asString();
	}

}
