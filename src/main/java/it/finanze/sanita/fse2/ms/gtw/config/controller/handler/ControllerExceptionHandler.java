/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.controller.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ConfigItemsNotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 *	Exceptions Handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConfigItemsNotFoundException.class})
    protected ResponseEntity<ErrorResponseDTO> handleItemsNotFoundException(final ConfigItemsNotFoundException ex, final WebRequest request) {
    	
        log.error("Error, configuration items not found");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
    	return new ResponseEntity<>(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), "Configuration items not found"), headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericException(final BusinessException ex, final WebRequest request) {
    	
        log.error("Internal server error.");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
    	return new ResponseEntity<>(new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ErrorResponseDTO> handleValidationException(final ValidationException ex, final WebRequest request) {
    	
        log.error("Parameters invalid or missing.");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
    	return new ResponseEntity<>(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad request"), headers, HttpStatus.BAD_REQUEST);
    }

}