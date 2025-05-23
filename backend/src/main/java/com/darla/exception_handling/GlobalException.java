package com.darla.exception_handling;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.darla.dto.Response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalException {
	
	// handle Not Found Exception
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Response> handleNotFoundException(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.builder().message(ex.getMessage()).build());
	}	
	
	
	// handle validation exceptions
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleValidationExceptions(MethodArgumentNotValidException ex) {
        
		Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
       
        Response res = new Response();
        res.setMessage("Validation failed. Please check your input.");
        res.setErrors(errors);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        return res;
//        return ResponseEntity.badRequest().body(errors);
    }
	
	@ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleValidationExceptions(HandlerMethodValidationException ex) {
        
		Map<String, Object> errors = new HashMap<>();
        
		ex.getAllErrors().forEach(err -> errors.put("err", err.getDefaultMessage()));
		
		        // Handle the validation errors here
		Response res = new Response();
		res.setMessage("Validation failed. Please check your input.");
		res.setErrors(errors);
		res.setStatus(HttpStatus.BAD_REQUEST.value());
		        return res;
//        return ResponseEntity.badRequest().body(errors);
    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Response handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString(); // Extract field name
            errors.put(field, violation.getMessage()); // Store error message
        }

        errors.put("message", "Validation failed. Please check your input.");
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        Response res = new Response();
        res.setMessage("Validation failed. Please check your input.");
       res.setErrors(errors);
           res.setStatus(HttpStatus.BAD_REQUEST.value());
        return res;
    }
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Response> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.builder()
				.message(ex.getMessage()+ " You are not authorized to access this resource or perform this action.")
				.status(HttpStatus.FORBIDDEN.value())
				.build());
	}
	
	// Global exception handler
	@ExceptionHandler(Exception.class)
	ResponseEntity<Response> handleGlobalExceptions(Exception msg){
//	    return new ResponseEntity<>(msg.getMessage(), HttpStatus.ACCEPTED);
		Response response = Response.builder().message(msg.getMessage()).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		
	}
}
