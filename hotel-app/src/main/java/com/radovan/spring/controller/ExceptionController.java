package com.radovan.spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.radovan.spring.exceptions.ExistingEmailException;
import com.radovan.spring.exceptions.ExistingRoomNumberException;
import com.radovan.spring.exceptions.InvalidUserException;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ExistingEmailException.class)
	public ResponseEntity<String> handleExistingEmailException(ExistingEmailException ex) {
		return ResponseEntity.internalServerError().body("Email exists already!");
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<String> handleInvalidUserException(InvalidUserException ex) {
		return ResponseEntity.internalServerError().body("Invalid user!");
	}

	@ExceptionHandler(ExistingRoomNumberException.class)
	public ResponseEntity<String> handleExistingRoomNumberException(ExistingRoomNumberException ex) {
		return ResponseEntity.internalServerError().body("Existing room number!");
	}
}
