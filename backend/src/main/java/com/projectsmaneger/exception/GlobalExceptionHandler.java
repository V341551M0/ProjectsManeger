package com.projectsmaneger.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Bad Request",
        "Invalid value for parameter: " + exception.getName(),
        request.getRequestURI(),
        null);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.METHOD_NOT_ALLOWED,
        "Method Not Allowed",
        "HTTP method not supported for this endpoint",
        request.getRequestURI(),
        null);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
      NoResourceFoundException exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.NOT_FOUND, "Not Found", "Endpoint not found", request.getRequestURI(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        "An unexpected error occurred",
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
