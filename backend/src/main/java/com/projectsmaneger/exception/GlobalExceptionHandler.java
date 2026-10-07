package com.projectsmaneger.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.NOT_FOUND,
        "Resource not found",
        exception.getMessage(),
        request.getRequestURI(),
        null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    Map<String, String> errors =
        exception.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage(),
                    (existing, replacement) -> existing));

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Bad Request",
        "Validation failed",
        request.getRequestURI(),
        errors);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiErrorResponse> handleBadCredentials(
      BadCredentialsException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.UNAUTHORIZED,
        "Unauthorized",
        "Invalid username or password",
        request.getRequestURI(),
        null);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
      HttpMessageNotReadableException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Bad Request",
        "Malformed JSON request",
        request.getRequestURI(),
        null);
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
      HttpStatus status, String error, String message, String path, Map<String, String> fields) {
    ApiErrorResponse response =
        new ApiErrorResponse(Instant.now(), status.value(), error, message, path, fields);

    return ResponseEntity.status(status).body(response);
  }
}
