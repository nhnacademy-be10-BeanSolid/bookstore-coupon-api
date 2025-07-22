package com.nhnacademy.common.exception;

import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public class ValidationFailedException extends BadRequestException {
  public ValidationFailedException(BindingResult bindingResult) {
    super(buildErrorMessage(bindingResult));
  }

  private static String buildErrorMessage(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
            .map(error -> String.format(
                    "field='%s', rejectedValue='%s', message='%s', code='%s'",
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage(),
                    error.getCode()
            ))
            .collect(Collectors.joining("\n"));
  }
}