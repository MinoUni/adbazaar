package com.adbazaar.exception.handler;

import com.adbazaar.dto.ApiError;
import com.adbazaar.exception.AccessTokenException;
import com.adbazaar.exception.RefreshTokenException;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessTokenException.class)
    public ResponseEntity<Object> handleAccessTokenException(AccessTokenException e) {
        return buildExceptionResponse(e, HttpStatus.UNAUTHORIZED, List.of());
    }

    /**
     * TODO: Think about refresh token exception and his status code
     * @see com.adbazaar.security.JwtService#revokeRefreshToken(String)
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<Object> handleRefreshTokenException(RefreshTokenException e) {
        return buildExceptionResponse(e, HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        return buildExceptionResponse(e, HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return buildExceptionResponse(e, HttpStatus.CONFLICT, List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        return buildExceptionResponse(e, HttpStatus.UNAUTHORIZED, List.of());
    }

    private ResponseEntity<Object> buildExceptionResponse(Exception exception, HttpStatusCode httpStatus, List<String> details) {
        var exceptionResponse = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .message(exception.getMessage())
                .details(details)
                .build();
        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException e,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(collectingAndThen(
                        toList(),
                        details -> ResponseEntity.status(status)
                                .body(ApiError.builder()
                                        .timestamp(LocalDateTime.now())
                                        .status(status.value())
                                        .message("Validation failed")
                                        .details(details)
                                        .build())));
    }
}
