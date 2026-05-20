package com.example.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException() {
        return "error/500";
    }
}
