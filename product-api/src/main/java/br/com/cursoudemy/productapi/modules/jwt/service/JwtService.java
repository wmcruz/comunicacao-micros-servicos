package br.com.cursoudemy.productapi.modules.jwt.service;

import br.com.cursoudemy.productapi.config.exception.AuthenticationJwtException;
import br.com.cursoudemy.productapi.modules.jwt.dto.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;

    @Value("${app-config.secrets.api-secret}")
    private String apiSecret;

    public void validateAuthorization(String token) throws AuthenticationException {
        var accessToken = this.extractToken(token);

        try {
            var claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(this.apiSecret.getBytes()))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            var user = JwtResponse.getUser(claims);
            if (ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getId()))
                throw new AuthenticationJwtException("The user is not valid.");

        } catch (Exception exception) {
            System.out.println("[JwtService - validateAuthorization()] - " + exception.getMessage());
            throw new AuthenticationJwtException("Error while trying to process the access token.");
        }
    }

    private String extractToken(String token) {
        if (ObjectUtils.isEmpty(token))
            throw new AuthenticationJwtException("The access token was not informed.");

        if (token.contains(this.EMPTY_SPACE))
            return token.split(this.EMPTY_SPACE)[TOKEN_INDEX];

        return token;
    }
}
