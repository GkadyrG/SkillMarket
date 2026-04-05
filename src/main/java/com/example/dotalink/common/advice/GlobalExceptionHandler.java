package com.example.dotalink.common.advice;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.common.exception.ProfileNotFoundException;
import com.example.dotalink.common.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, ProfileNotFoundException.class, DotaAccountNotFoundException.class, NoHandlerFoundException.class, NoResourceFoundException.class})
    public String handleNotFound(Exception ex, HttpServletRequest request, Model model) {
        log.warn("Not found: path={}, message={}", request.getRequestURI(), ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedBusinessException.class)
    public String handleAccessDenied(AccessDeniedBusinessException ex, HttpServletRequest request, Model model) {
        log.warn("Access denied: path={}, message={}", request.getRequestURI(), ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/403";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled MVC exception: path={}", request.getRequestURI(), ex);
        model.addAttribute("message", "Unexpected server error");
        return "error/500";
    }
}
