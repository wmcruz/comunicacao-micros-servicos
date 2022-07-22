package br.com.cursoudemy.productapi.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationJwtException extends RuntimeException {

    public AuthenticationJwtException(String message) {
        super(message);
    }
}
