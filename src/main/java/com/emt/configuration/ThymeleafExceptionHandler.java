package com.emt.configuration;

import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.exception.MatchNotFoundException;
import com.emt.model.exception.PlayerAlreadyExistsException;
import com.emt.model.exception.PlayerNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class ThymeleafExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public String handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, RedirectAttributes redAttr) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String> entry : errors.entrySet()) {
        result.append(entry.getKey()).append(": ").append(entry.getValue()).append("<br>");
    }

    redAttr.addFlashAttribute("errorMessage", result.toString());
    return "redirect:/players";
  }

  @ExceptionHandler({
    PlayerNotFoundException.class,
    PlayerAlreadyExistsException.class,
    IdenticalPlayersException.class,
    MatchNotFoundException.class
  })
  public String handleNotFoundException(RuntimeException ex, RedirectAttributes redAttr) {
    redAttr.addFlashAttribute("errorMessage", ex.getMessage());
    return "redirect:/players";
  }
}
