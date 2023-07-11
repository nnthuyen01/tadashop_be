package com.tadashop.nnt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomResponseEnitityExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(AppException.class)
	public final ResponseEntity<Object> handleException(AppException ex, WebRequest request){
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage());
		
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}